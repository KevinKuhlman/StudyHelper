package com.project.kevin.studyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Kevin on 4/24/2017.
 */

//Pick between studying by yourself or studying as a group
public class SelectionPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_page);

        Intent myIntent = getIntent();
        final User user = (User) myIntent.getSerializableExtra("User");

        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome " + user.getUsername());

        //switch to StudySolo display
        Button studySoloButton = (Button) findViewById(R.id.studySoloButton);
        studySoloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), StudySolo.class);
                myIntent.putExtra("User", user);
                startActivityForResult(myIntent, 0);
            }
        });

        //switch to GroupStudy display
        Button groupStudyButton = (Button) findViewById(R.id.groupStudyButton);
        groupStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(view.getContext(), GroupStudy.class);
                myIntent.putExtra("User", user);
                startActivityForResult(myIntent, 0);

            }
        });
    }
}
