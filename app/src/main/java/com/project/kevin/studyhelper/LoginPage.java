package com.project.kevin.studyhelper;

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

public class LoginPage extends AppCompatActivity{

    //Declaration of objects
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private EditText username;
    private EditText password;
    private HashMap<String, User> usersMap;

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
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    //check to see if the input data matches any stored values
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //context needed for future toast operations
                        Context context = getApplicationContext();
                        User user = new User();

                        //store all current user data into the hashmap
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            User tempUser = ds.getValue(User.class);
                            if(tempUser.getUsername().equals(possibleUsername) && tempUser.getPassword().equals(possiblePassword)){
                                DataSnapshot card_setsSnapshot = ds.child("card_sets");
                                for(DataSnapshot card_set : card_setsSnapshot.getChildren()){
                                    CardSet cardsSet = new CardSet();
                                    for(DataSnapshot card : card_set.getChildren()){
                                        if(card.getKey().equals("name")){
                                            cardsSet.setName(card.getValue().toString());
                                        }else{
                                            Card newCard = card.getValue(Card.class);
                                            cardsSet.getCards().add(newCard);
                                        }
                                    }
                                    tempUser.getCardSets().add(cardsSet);
                                }
                                user = tempUser;
                            }
                        }

                        if(user!=null) {
                            Intent myIntent = new Intent(view.getContext(), SelectionPage.class);
                            myIntent.putExtra("User", user);
                            startActivityForResult(myIntent, 0);
                        }else {
                            Toast toast = Toast.makeText(context, "Username or Password is incorrect", Toast.LENGTH_SHORT);
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
