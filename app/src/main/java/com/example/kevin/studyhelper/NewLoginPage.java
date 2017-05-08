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
import java.util.Map;

/**
 * Created by Kevin on 4/30/2017.
 */

public class NewLoginPage extends AppCompatActivity{

    //Declaration of objects
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private HashMap<String, String> usersMap;
    private boolean checker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_login_page);

        //connects front end fields to the backend storage
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        //connects to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        //checker used to disable toasts following the sucessful creation of a new account
        checker = false;

        //create button for submitting new account information, create an on click listener
        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {

                //store current values from the fields
                final String possibleUsername = username.getText().toString();
                final String possiblePassword = password.getText().toString();
                final String possibleConfirmPassword = confirmPassword.getText().toString();

                //create a listener for updating account information
                myRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //create a user map to store current user information
                        usersMap = new HashMap<String, String>();
                        //context necessary for the future toasts
                        Context context = getApplicationContext();

                        //store all current user data
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            usersMap.put(user.getUsername(), user.getPassword());
                        }

                        //if the user already exists, display appropriate message
                        if(usersMap.containsKey(possibleUsername)){

                            Toast toast = Toast.makeText(context, "Username Already Taken", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);

                            //skip displaying message if this is due to sucessful addition of new account
                            if(checker == false)
                                toast.show();

                            //helps deal with errors due to moving back to previous pages
                            checker = false;

                        }else if (!possiblePassword.equals(possibleConfirmPassword)){

                            //if the passwords don't match, display appropriate message

                            Toast toast = Toast.makeText(context, "Passwords Do Not Match", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0,0);

                            //skip displaying message if this is due to sucessful addtion of new account
                            if(checker == false)
                                toast.show();

                            //helps deal with errors due to moving back to previous pages
                            checker = false;

                        }else{

                            //otherwise add a new account
                            Map<String, String> newUser = new HashMap<String, String>();
                            newUser.put("username", possibleUsername);
                            newUser.put("password", possiblePassword);

                            //disable toasts
                            checker=true;

                            //add new account data to the database
                            myRef = myRef.child(possibleUsername);
                            myRef.setValue(newUser);

                            //go to the next page
                            User user = new User();
                            user.setUsername(possibleUsername);
                            user.setPassword(possiblePassword);
                            Intent myIntent = new Intent(view.getContext(), SelectionPage.class);
                            myIntent.putExtra("User", user);
                            startActivityForResult(myIntent, 0);

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
