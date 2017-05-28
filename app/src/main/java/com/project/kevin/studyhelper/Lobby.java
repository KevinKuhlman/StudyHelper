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

        loadCardSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = new String[user.getCardSets().size()];

                for (int i = 0; i < user.getCardSets().size(); i++) {
                    items[i] = user.getCardSets().get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Lobby.this);
                builder.setTitle("Choose Card Set to Load");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        CardSet tempCardSet = user.getCardSets().get(item);
                        for(int i = 0; i<usersData.size(); i++){
                            if(usersData.get(i).getUsername() == user.getUsername()){

                                usersData.get(i).setCardSetName(tempCardSet.getName());
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

        leaveLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });

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

    protected void generateLobby(DataSnapshot dataSnapshot){

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.hasChild("chosenCardSet")){
                String username = ds.child("username").getValue().toString();
                String cardSet = ds.child("chosenCardSet").getValue().toString();
                String ready = "false";
                if(ds.hasChild("ready")){
                    ready = ds.child("ready").getValue().toString();
                }

                boolean check = true;
                for(int i = 0; i<usersData.size(); i++){
                    if(usersData.get(i).getUsername().equals(username)){
                        check = false;
                    }
                }
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

    protected void mapUser(){

        Map<String, String> newUser = new HashMap<String, String>();
        newUser.put("username", user.getUsername());
        newUser.put("password", user.getPassword());
        newUser.put("chosenCardSet", "");

        myRef = myRef.child(user.getUsername());
        myRef.setValue(newUser);

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

    protected void updateListView(){
        userArrayAdapter = new UserArrayAdapter(this, R.layout.user_item, usersData);
        listView.setAdapter(userArrayAdapter);

    }

    @Override
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

    public void beginCountdown(){

        readyButton.setEnabled(false);
        leaveLobby.setEnabled(false);
        loadCardSet.setEnabled(false);

        boolean check = false;
        for(int i = 0; i<usersData.size(); i++){
            if(!usersData.get(i).getCardSetName().equals("")){
                check = true;
            }
        }

        if(check){

            final TextView status = (TextView) findViewById(R.id.status);

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
