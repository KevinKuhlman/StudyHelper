package com.project.kevin.studyhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Kevin on 5/27/2017.
 */

public class Quiz extends AppCompatActivity{

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String lobbyName;
    private Intent myIntent;
    private List<User> userList;
    private List<CardSet> cardSetList;
    private List<QuizInfo> userInfoList;
    private QuizArrayAdapter quizArrayAdapter;
    private ListView listView;
    private TextView countdownTextView;
    private TextView lobbyNameTextView;
    private TextView card;
    private CardSet quizCardSet;
    private int count;
    private String quizName;
    private User user;
    private EditText answer;
    private int check;
    private Button leaveButton;

    @Override
    //Quiz activity - has users input their answers to the card questions
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("lobbies");
        myIntent = getIntent();
        lobbyName = (String) myIntent.getSerializableExtra("Lobby");
        user = (User) myIntent.getSerializableExtra("User");
        myRef = myRef.child(lobbyName);

        userList = new ArrayList<User>();
        cardSetList = new ArrayList<CardSet>();
        userInfoList = new ArrayList<QuizInfo>();
        listView = (ListView) findViewById(R.id.quizList);
        countdownTextView = (TextView) findViewById(R.id.countdown);
        lobbyNameTextView = (TextView) findViewById(R.id.quizName);
        Button leaveButton = (Button)findViewById(R.id.leaveQuiz);
        answer = (EditText) findViewById(R.id.answer);
        card = (TextView) findViewById(R.id.card);
        check = 2;

        quizName = lobbyName.substring(0,lobbyName.length()-8);
        quizName = quizName+"'s Quiz";
        lobbyNameTextView.setText(quizName);
        quizCardSet = new CardSet();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check is 2 only on initialization of the Quiz class
                //retrieved stored user information from the database
                if(check==2) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User tempUser = ds.getValue(User.class);
                        String chosenCardSet = ds.child("chosenCardSet").getValue().toString();
                        DataSnapshot card_setsSnapshot = ds.child("card_sets");
                        for (DataSnapshot card_set : card_setsSnapshot.getChildren()) {
                            CardSet cardsSet = new CardSet();
                            for (DataSnapshot card : card_set.getChildren()) {
                                if (card.getKey().equals("name")) {
                                    cardsSet.setName(card.getValue().toString());
                                } else {
                                    Card newCard = card.getValue(Card.class);
                                    cardsSet.getCards().add(newCard);
                                }
                            }
                            if (chosenCardSet.equals(cardsSet.getName())) {
                                cardSetList.add(cardsSet);
                            }
                            tempUser.getCardSets().add(cardsSet);
                        }
                        userList.add(tempUser);
                    }
                    check = 1;
                    generateQuiz();
                }else if (check==1){
                    //updates the Quiz to display user answers at appropriate times
                    userInfoList = new ArrayList<QuizInfo>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.hasChild("quiz")){
                            userInfoList.add(new QuizInfo(ds.child("username").getValue().toString(), ds.child("quiz").child("quizAnswer").getValue().toString()));
                        }else{
                            userInfoList.add(new QuizInfo(ds.child("username").getValue().toString(), ""));
                        }

                    }
                    updateListView();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Button to leave the lobby, same functionality as in Lobby
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Quiz.this);
                alertDialog.setTitle("Leave Lobby");
                alertDialog.setMessage("Are you sure you want to leave the lobby?");
                alertDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        check = 0;

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

    }

    //Generate Quiz based on retrieved user information
    protected void generateQuiz(){

        //create Quiz Info list for the listview
        for(int i = 0; i<userList.size(); i++){
            userInfoList.add(new QuizInfo(userList.get(i).getUsername(),""));
        }
        updateListView();

        //get the chosen card sets from each user
        for(int i=0; i<cardSetList.size(); i++){
            for(int j=0; j<cardSetList.get(i).getCards().size(); j++){
                quizCardSet.getCards().add(cardSetList.get(i).getCards().get(j));
            }
        }

        //10 second countdown before Quiz begins
        CountDownTimer cdt = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTextView.setText(millisUntilFinished/1000 + " Seconds till Quiz Begins");
            }

            @Override
            public void onFinish() {
                count = quizCardSet.getCards().size();
                runQuiz();
            }
        }.start();

    }

    //updates the list view for the Quiz
    protected void updateListView(){
        quizArrayAdapter = new QuizArrayAdapter(this, R.layout.quiz_item, userInfoList);
        listView.setAdapter(quizArrayAdapter);

    }

    //Chooses a card and displays it
    protected void runQuiz(){

        //choose random card and set it to be shown in the Quiz
        Random random = new Random();
        int next = random.nextInt(count);

        final Card currentCard = quizCardSet.getCards().get(next);
        card.setText(currentCard.getFront());
        quizCardSet.getCards().remove(next);

        //Stores a quizAnswer value for current users for displaying their answers
        myRef = database.getReference("lobbies");
        myRef = myRef.child(lobbyName);
        myRef = myRef.child(user.getUsername());
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("quizAnswer", "");
        myRef = myRef.child("quiz");
        myRef.setValue(newData);

        myRef = database.getReference("lobbies");
        myRef = myRef.child(lobbyName);

        //15 second countdown till displaying answer (back-side of card)
        CountDownTimer cdt = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTextView.setText(millisUntilFinished/1000 + " Seconds till Answer is Shown");
            }

            @Override
            public void onFinish() {
                //Update database to have back of card showing and user answers stored
                card.setText(currentCard.getBack());
                myRef = database.getReference("lobbies");
                myRef = myRef.child(lobbyName);
                myRef = myRef.child(user.getUsername());
                Map<String, String> newData = new HashMap<String, String>();
                newData.put("quizAnswer", answer.getText().toString());
                myRef = myRef.child("quiz");
                myRef.setValue(newData);

                myRef = database.getReference("lobbies");
                myRef = myRef.child(lobbyName);

                //10 second countdown till moving on to the next card
                CountDownTimer countdowntimer = new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countdownTextView.setText(millisUntilFinished/1000 + " Seconds till Next Question");
                    }

                    @Override
                    //repeat until out of cards
                    public void onFinish() {
                        count--;
                        if(count>0){
                            runQuiz();
                        }else{
                            exitQuiz();
                        }
                    }
                }.start();

            }
        }.start();
    }

    //same functionality as leaveQuiz button, exits the quiz
    protected void exitQuiz(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Quiz.this);
        alertDialog.setTitle("Quiz Complete");
        alertDialog.setMessage("Quiz is now complete. Click Leave to return to the Group Study page");
        alertDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                check = 0;

                myRef = database.getReference("lobbies");
                myRef = myRef.child(lobbyName);
                myRef.child(user.getUsername()).removeValue();

                Intent myIntent = new Intent(getApplicationContext(), GroupStudy.class);
                myIntent.putExtra("User", user);
                startActivityForResult(myIntent, 0);
            }
        });
        alertDialog.show();

    }


    @Override
    //same functionality as leaveQuiz button, exits the quiz
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Quiz.this);
        alertDialog.setTitle("Leave Lobby");
        alertDialog.setMessage("Are you sure you want to leave the lobby?");
        alertDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                check = 0;

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



}
