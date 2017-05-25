package com.project.kevin.studyhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin on 4/24/2017.
 */

public class Lobby extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private UserArrayAdapter userArrayAdapter;
    Intent myIntent;
    List<User> userList;
    HashMap<Integer, CardSet> cardSetHashMap;
    User user;
    ListView listView;
    String lobbyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        userList = new ArrayList<User>();
        cardSetHashMap = new HashMap<Integer, CardSet>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myIntent = getIntent();


        user = (User) myIntent.getSerializableExtra("User");
        lobbyName = (String) myIntent.getSerializableExtra("Lobby");
        myRef = myRef.child(lobbyName);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                generateLobby(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button loadCardSet = (Button) findViewById(R.id.loadCardSet);

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
                        int position = userList.indexOf(user);
                        cardSetHashMap.put(position, user.getCardSets().get(item));
                        updateListView();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    protected void generateLobby(DataSnapshot dataSnapshot){

        userList = new ArrayList<User>();
        cardSetHashMap = new HashMap<Integer, CardSet>();

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            String username = ds.child("username").getValue().toString();
            String cardSet = ds.child("cardSet").getValue().toString();
        }
        listView = (ListView) findViewById(R.id.lobbyList);
        updateListView();
        userList.add(user);
    }

    protected void updateListView(){
        userArrayAdapter = new UserArrayAdapter(this, R.layout.user_item, userList, cardSetHashMap);
        listView.setAdapter(userArrayAdapter);
    }

}
