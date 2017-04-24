package com.example.kevin.studyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kevin on 4/24/2017.
 */

public class GroupStudy extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_study);

        Button createLobbyButton = (Button) findViewById(R.id.createALobby);
        createLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Lobby.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button joinLobbyButton = (Button) findViewById(R.id.joinAnExistingLobby);
        joinLobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Lobby.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }
}
