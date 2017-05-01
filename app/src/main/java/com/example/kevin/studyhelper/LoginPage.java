package com.example.kevin.studyhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginPage extends AppCompatActivity {

    //Declaration of objects
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private EditText username;
    private EditText password;
    private HashMap<String, String> usersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //create page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //load in firebase database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        //connect the xml code to the java code
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        //create a login button and add an on click listener
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            //when clicked, attempt to log in
            public void onClick(final View view) {

                //store the current username and password values entered by the user
                final String possibleUsername =  username.getText().toString();
                final String possiblePassword = password.getText().toString();

                //add a value event listener for the database to access data
                myRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    //check to see if the input data matches any stored values
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //create a hashmap to store all current user info
                        usersMap = new HashMap<String, String>();

                        //context needed for future toast operations
                        Context context = getApplicationContext();

                        //store all current user data into the hashmap
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            usersMap.put(user.getUsername(), user.getPassword());
                        }

                        //check if the input username matches any stored values
                        if(usersMap.containsKey(possibleUsername)){
                            //check to see if input password matches the password for the stored username
                            if(usersMap.get(possibleUsername).equals(possiblePassword)){
                                //proceed to the user selection page
                                Intent myIntent = new Intent(view.getContext(), SelectionPage.class);
                                startActivityForResult(myIntent, 0);
                            }else {
                                //present message stating the password is incorrect
                                Toast toast = Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                                toast.show();
                            }
                        }else{
                            //present message stating the username is incorrect
                            Toast toast = Toast.makeText(context, "Username Does Not Exist", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);
                            toast.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        //create button for the create account action
        Button createAccountButton = (Button) findViewById(R.id.createAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener(){

            @Override
            //send user to the create a new account page
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), NewLoginPage.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

}
