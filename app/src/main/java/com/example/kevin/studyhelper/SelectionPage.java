package com.example.kevin.studyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Kevin on 4/24/2017.
 */

public class SelectionPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_page);

        Button studySoloButton = (Button) findViewById(R.id.studySoloButton);
        studySoloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), StudySolo.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button groupStudyButton = (Button) findViewById(R.id.groupStudyButton);
        groupStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), GroupStudy.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }
}
