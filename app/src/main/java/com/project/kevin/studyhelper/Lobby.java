package com.project.kevin.studyhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 4/24/2017.
 */

//Lobby instance, allow multiple users to interact
public class Lobby extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private UserArrayAdapter userArrayAdapter;
    Intent myIntent;
    List<ListInfo> usersData;

    Button loadCardSet;
    Button leaveLobby;
    Button readyButton;

    ValueEventListener listener;

    User user;
    ListView listView;
    String lobbyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        usersData = new ArrayList<ListInfo>();
        database = FirebaseDatabase.getInstance();
        myIntent = getIntent();

        user = (User) myIntent.getSerializableExtra("User");
        lobbyName = (String) myIntent.getSerializableExtra("Lobby");
        myRef = database.getReference("lobbies");
        myRef = myRef.child(lobbyName);

        TextView textView = (TextView) findViewById(R.id.lobbyName);
        textView.setText(lobbyName);

        //listener to re-generate lobby every time a change is made to it
        //initialized explicitly to allow it to be deleted later
        listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(user !=null){
                    generateLobby(dataSnapshot);
                    checkListView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mapUser();


        loadCardSet = (Button) findViewById(R.id.loadCardSet);
        leaveLobby = (Button) findViewById(R.id.leaveLobby);
        readyButton = (Button) findViewById(R.id.readyButton);

        //Loads a stored card set's information when clicked
        loadCardSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = new String[user.getCardSets().size()];

                for (int i = 0; i < user.getCardSets().size(); i++) {
                    items[i] = user.getCardSets().get(i).getName();
                }

                //alert dialog of all created card sets for the user
                AlertDialog.Builder builder = new AlertDialog.Builder(Lobby.this);
                builder.setTitle("Choose Card Set to Load");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        CardSet tempCardSet = user.getCardSets().get(item);
                        for(int i = 0; i<usersData.size(); i++){
                            //override the stored card set value in usersData
                            if(usersData.get(i).getUsername() == user.getUsername()){

                                usersData.get(i).setCardSetName(tempCardSet.getName());
                                //writes the chosen card set into the database
                                myRef = database.getReference("lobbies");
                                myRef = myRef.child(lobbyName);
                                myRef = myRef.child(user.getUsername()).child("chosenCardSet");
                                myRef.setValue(tempCardSet.getName());

                            }
                        }

                        updateListView();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        //exits lobby
        leaveLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Lobby.this);
                alertDialog.setTitle("Leave Lobby");
                alertDialog.setMessage("Are you sure you want to leave the lobby?");
                alertDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //delete user from the current lobby
                        myRef = database.getReference("lobbies");
                        myRef = myRef.child(lobbyName);
                        myRef.child(user.getUsername()).removeValue();

                        //return to GroupStudy - No back issues as Lobby has no history tracking
                        Intent myIntent = new Intent(getApplicationContext(), GroupStudy.class);
                        myIntent.putExtra("User", user);
                        startActivityForResult(myIntent, 0);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        //Sets user status to ready, stores in database
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i<usersData.size(); i++){
                    if(usersData.get(i).getUsername() == user.getUsername()){
                        Boolean ready = usersData.get(i).getReady();
                        usersData.get(i).setReady(!ready);
                        myRef = database.getReference("lobbies");
                        myRef = myRef.child(lobbyName);
                        myRef.child(user.getUsername()).child("ready").setValue(!ready);
                    }
                }
            }
        });
    }

    //creates lobby based on database information
    protected void generateLobby(DataSnapshot dataSnapshot){

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            //checks for chosenCardSet, would sometimes break after leaving/rejoining lobby
            //if it did not check first
            if(ds.hasChild("chosenCardSet")){
                //get database values to put into ListInfo
                String username = ds.child("username").getValue().toString();
                String cardSet = ds.child("chosenCardSet").getValue().toString();
                String ready = "false";
                if(ds.hasChild("ready")){
                    ready = ds.child("ready").getValue().toString();
                }

                //Checks if the list of ListInfos already has this user
                boolean check = true;
                for(int i = 0; i<usersData.size(); i++){
                    if(usersData.get(i).getUsername().equals(username)){
                        check = false;
                    }
                }
                //adds a new listinfo to the list if it doesnt already exist
                if(check){
                    if(ready.equals("true")){
                        usersData.add(new ListInfo(username, cardSet, true));
                    }
                    else{
                        usersData.add(new ListInfo(username, cardSet, false));
                    }
                }
            }

        }

        listView = (ListView) findViewById(R.id.lobbyList);
        updateListView();

    }

    //maps user to the database - all values
    protected void mapUser(){

        //adds basic user info
        Map<String, String> newUser = new HashMap<String, String>();
        newUser.put("username", user.getUsername());
        newUser.put("password", user.getPassword());
        newUser.put("chosenCardSet", "");

        myRef = myRef.child(user.getUsername());
        myRef.setValue(newUser);

        //adds all card sets
        for(int i = 0; i<user.getCardSets().size(); i++){
            CardSet tempCardSet = user.getCardSets().get(i);
            myRef = myRef.child("card_sets").child(tempCardSet.getName());
            for(int j = 0; j<tempCardSet.getCards().size(); j++){
                myRef.child("card" + (j+1)).setValue(tempCardSet.getCards().get(j));
            }

            myRef.child("name").setValue(tempCardSet.getName());

            myRef = database.getReference("lobbies");
            myRef = myRef.child(lobbyName);
            myRef = myRef.child(user.getUsername());
        }

        myRef = myRef.child(lobbyName);

    }

    //updates the list view with new ListInfo items
    protected void updateListView(){
        userArrayAdapter = new UserArrayAdapter(this, R.layout.user_item, usersData);
        listView.setAdapter(userArrayAdapter);

    }

    @Override
    //overrides on back pressed to ensure they want to leave and to delete database info if so
    //same functionality as the leave lobby button
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Lobby.this);
        alertDialog.setTitle("Leave Lobby");
        alertDialog.setMessage("Are you sure you want to leave the lobby?");
        alertDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                myRef = database.getReference("lobbies");
                myRef = myRef.child(lobbyName);
                myRef.child(user.getUsername()).removeValue();

                Intent myIntent = new Intent(getApplicationContext(), GroupStudy.class);
                myIntent.putExtra("User", user);
                startActivityForResult(myIntent, 0);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    //checks if all users in the list are ready
    public void checkListView(){

        boolean check = true;
        boolean check2 = false;
        for(int i=0; i<usersData.size(); i++){

            if(!usersData.get(i).getReady()){
                check = false;
            }
            if(usersData.get(i).getUsername().equals(user.getUsername())){
                check2 = true;
            }
        }
        if(check && check2){
            beginCountdown();
        }
    }

    //if all users are ready, countdown to Quiz
    public void beginCountdown(){

        readyButton.setEnabled(false);
        leaveLobby.setEnabled(false);
        loadCardSet.setEnabled(false);

        boolean check = false;
        //check to make sure at least one card set has been selected
        for(int i = 0; i<usersData.size(); i++){
            if(!usersData.get(i).getCardSetName().equals("")){
                check = true;
            }
        }

        if(check){

            final TextView status = (TextView) findViewById(R.id.status);

            //countdown till transfer
            CountDownTimer cdt = new CountDownTimer(6000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    status.setText(millisUntilFinished/1000 + " Seconds till moving to Quiz");
                }

                @Override
                public void onFinish() {
                    myRef.removeEventListener(listener);
                    Intent myIntent = new Intent(getApplicationContext(), Quiz.class);
                    myIntent.putExtra("Lobby", lobbyName);
                    myIntent.putExtra("User", user);
                    startActivityForResult(myIntent, 0);
                }
            }.start();
        }else{

            //return to previous status if no card sets have been selected
            readyButton.setEnabled(true);
            leaveLobby.setEnabled(true);
            loadCardSet.setEnabled(true);

            Toast toast = Toast.makeText(getApplicationContext(), "No card sets have been selected", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
            toast.show();

            for(int i=0; i<usersData.size(); i++){

                if(usersData.get(i).getUsername().equals(user.getUsername())){
                    Boolean ready = usersData.get(i).getReady();
                    usersData.get(i).setReady(!ready);
                    myRef = database.getReference("lobbies");
                    myRef = myRef.child(lobbyName);
                    myRef.child(user.getUsername()).child("ready").setValue(!ready);
                }
            }


        }

    }

}
