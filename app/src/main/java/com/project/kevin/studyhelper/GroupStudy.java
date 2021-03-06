package com.project.kevin.studyhelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kevin on 4/24/2017.
 */

//Allows user to choose between joining a lobby or creating their own lobby
public class GroupStudy extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    Intent myIntent;
    User user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_study);

        //initializes values
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myIntent = getIntent();
        user = (User) myIntent.getSerializableExtra("User");

        //create a lobby button and action listener
        Button createLobbyButton = (Button) findViewById(R.id.createALobby);
        createLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                myRef = database.getReference();

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    //if there are lobbies already; add to the lobbies in the database
                    //if not, add "lobbies" to the database and add a new lobby to it
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("lobbies")){
                            myRef = database.getReference("lobbies");
                        }else{
                            dataSnapshot.child("lobbies");
                            myRef = database.getReference("lobbies");
                        }
                        myRef = myRef.child(user.getUsername() + "'s Lobby");
                        Intent myIntent = new Intent(view.getContext(), Lobby.class);
                        myIntent.putExtra("User", user);
                        myIntent.putExtra("Lobby", user.getUsername() + "'s Lobby");
                        startActivityForResult(myIntent, 0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        //join a lobby button and action listener
        Button joinLobbyButton = (Button) findViewById(R.id.joinAnExistingLobby);
        joinLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myRef = database.getReference();
                //checker prevents Toast from showing in rare scenarios
                final boolean[] checker = {true};

                //get stored lobbies and show them in an alert dialog
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!checker[0]){

                        }else if(dataSnapshot.hasChild("lobbies")){
                            //Get lobby reference and add all of its children to the a list
                            myRef = database.getReference("lobbies");
                            dataSnapshot = dataSnapshot.child("lobbies");
                            ArrayList<String> lobbyList = new ArrayList<String>();
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                lobbyList.add(ds.getKey().toString());

                            }

                            String[] lobbyStringList = new String[lobbyList.size()];
                            lobbyStringList = lobbyList.toArray(lobbyStringList);

                            //create an alert dialog with all of the available lobbies
                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupStudy.this);
                            builder.setTitle("Choose Lobby to Join");
                            final String[] finalLobbyStringList = lobbyStringList;
                            builder.setItems(lobbyStringList, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {

                                    //Add basic user info to the chosen lobby
                                    //detailed user info added in the Lobby page
                                    myRef = myRef.child(finalLobbyStringList[item]);
                                    myRef = myRef.child(user.getUsername());
                                    HashMap<String, String> newLobby = new HashMap<String, String>();
                                    newLobby.put("username", user.getUsername());
                                    newLobby.put("cardSet", "");
                                    myRef.setValue(newLobby);

                                    checker[0] = false;

                                    //switch to Lobby view
                                    Intent myIntent = new Intent(GroupStudy.this.getApplicationContext(), Lobby.class);
                                    myIntent.putExtra("User", user);
                                    myIntent.putExtra("Lobby", finalLobbyStringList[item]);
                                    startActivityForResult(myIntent, 0);

                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();

                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "There are no lobbies to join", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                            if(checker[0])
                                toast.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

}
