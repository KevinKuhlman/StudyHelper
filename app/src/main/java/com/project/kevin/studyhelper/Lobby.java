package com.project.kevin.studyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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
    User user;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        userList = new ArrayList<User>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myIntent = getIntent();
        user = (User) myIntent.getSerializableExtra("User");
        listView = (ListView) findViewById(R.id.lobbyList);

        userArrayAdapter = new UserArrayAdapter(this, R.layout.user_item, userList);
        listView.setAdapter(userArrayAdapter);

        initializeUserList();

    }

    protected void initializeUserList(){

        userList.add(new User("name1", "password1"));
        userList.add(new User("name2", "password2"));
        userList.add(new User("name3", "password3"));
        userList.add(new User("name4", "password4"));

    }
}
