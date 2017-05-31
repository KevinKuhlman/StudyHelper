package com.project.kevin.studyhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kevin on 4/24/2017.
 */

//Solo Study view, allows creation of cards/updates/saving
public class StudySolo extends AppCompatActivity {


    int cardIndex;
    Intent myIntent;
    User user;
    EditText textBox;
    TextView seekText;
    Boolean side;
    CardSet cardSet;
    int numberOfCards;
    Card currentCard;
    SeekBar seekBar;
    Button addCard;
    Button removeCard;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_solo);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        cardIndex = 0;
        myIntent = getIntent();
        side = true;
        user = (User) myIntent.getSerializableExtra("User");
        textBox = (EditText) findViewById(R.id.card);

        Button createNewSet = (Button) findViewById(R.id.createNewSet);
        Button saveSet = (Button) findViewById(R.id.saveSet);
        Button loadSet = (Button) findViewById(R.id.loadSet);
        addCard = (Button) findViewById(R.id.addButton);
        removeCard = (Button) findViewById(R.id.removeButton);

        final ToggleButton cardSide = (ToggleButton) findViewById(R.id.side);
        ImageButton nextCard = (ImageButton) findViewById(R.id.nextCard);
        ImageButton previousCard = (ImageButton) findViewById(R.id.previousCard);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekText = (TextView) findViewById(R.id.seekText);
        seekBar.setEnabled(false);
        addCard.setEnabled(false);
        removeCard.setEnabled(false);

        //Asks user for name of a new set and creates a new card set for the user
        createNewSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(StudySolo.this);
                builder.setTitle("Enter Name for New Card Set");
                final EditText input = new EditText(StudySolo.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newCardSetName = input.getText().toString();
                        boolean check = true;
                        //check if name is already taken
                        for (int i = 0; i < user.getCardSets().size(); i++) {
                            if (user.getCardSets().get(i).getName().equals(newCardSetName)) {
                                check = false;
                            }
                        }

                        //creates new card set if name isnt taken
                        if (check) {
                            cardSet = new CardSet();
                            cardSet.name = newCardSetName;
                            cardIndex = 0;
                            side = true;
                            currentCard = new Card();
                            cardSet.getCards().add(currentCard);
                            seekText.setText(newCardSetName + ": Card 1/1");
                            textBox.setText("");
                            textBox.setHint("Enter text");
                            addCard.setEnabled(true);
                            removeCard.setEnabled(true);

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Card Set Name Already Taken", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //stores current set into the database
        saveSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cardSet != null){
                    saveCard();

                    user.getCardSets().add(cardSet);
                    myRef = myRef.child(user.getUsername()).child("card_sets").child(cardSet.getName());
                    for(int i = 0; i<cardSet.getCards().size(); i++){
                        myRef.child("card" + (i+1)).setValue(cardSet.getCards().get(i));
                    }
                    myRef.child("name").setValue(cardSet.getName());

                    myRef = database.getReference("users");
                }

            }
        });

        //creates list of stored sets and displays them for user to load
        loadSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = new String[user.getCardSets().size()];

                for (int i = 0; i < user.getCardSets().size(); i++) {
                    items[i] = user.getCardSets().get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(StudySolo.this);
                builder.setTitle("Choose Which Card Set");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        updateCardSet(item);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                cardSide.setText(cardSide.getTextOn());

            }
        });

        //flips the card's side - displays back or front text
        cardSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardSet!=null){
                    saveCard();
                    cardSide.setChecked(!side);
                    side = !side;
                    updateCard();
                }
            }
        });

        //changes currentCard to the next card in the list
        //if its the last card, returns to the beginning
        nextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cardSet != null) {
                    if (cardSet.getCards() != null) {
                        if (cardIndex - (cardSet.getCards().size() - 1) == 0) {
                            cardIndex = 0;
                        } else {
                            cardIndex++;
                        }

                        saveCard();
                        side = true;
                        seekText.setText(cardSet.getName() + ": Card " + (cardIndex + 1) + "/" + cardSet.getCards().size());
                        seekBar.setProgress(cardIndex);
                        cardSide.setChecked(true);
                        updateCard();
                    }

                }
            }
        });

        //changes currentCard to the previous card in the list
        //if its the first card, sends it to the end of the list
        previousCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardSet != null) {
                    if (cardSet.getCards() != null) {
                        if (cardIndex == 0) {
                            cardIndex = (cardSet.getCards().size() - 1);
                        } else {
                            cardIndex--;
                        }

                        saveCard();
                        side = true;
                        seekText.setText(cardSet.getName() + ": Card " + (cardIndex + 1) + "/" + cardSet.getCards().size());
                        seekBar.setProgress(cardIndex);
                        cardSide.setChecked(true);
                        updateCard();
                    }

                }
            }
        });

        //allows the user to skip around through their cards
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekText.setText(cardSet.getName() + ": Card " + (progress + 1) + "/" + cardSet.getCards().size());
                cardIndex = seekBar.getProgress();
                updateCard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cardIndex = seekBar.getProgress();
                updateCard();
            }
        });

        //adds a new card to the end of the current set
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
                cardSet.getCards().add(new Card());
                cardIndex = cardSet.getCards().size() - 1;
                seekBar.setMax(seekBar.getMax() + 1);
                seekBar.setProgress(seekBar.getMax());
                seekText.setText(cardSet.getName() + ": Card " + cardSet.getCards().size() + "/" + cardSet.getCards().size());
                cardSide.setChecked(true);
                updateCard();
            }
        });

        //removes the currently selected card after prompting the user
        removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudySolo.this);
                alertDialog.setTitle("Remove Card");
                alertDialog.setMessage("Are you sure you want to delete the current card? If there is only one card remaining it will delete the set");
                alertDialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(cardSet.getCards().size() == 1){
                            deleteCardSet();
                        }else{
                            cardSet.getCards().remove(cardIndex);
                            if (cardIndex == 0) {
                                cardIndex = (cardSet.getCards().size() - 1);
                            } else {
                                cardIndex--;
                            }
                            side = true;
                            seekBar.setMax(seekBar.getMax()-1);
                            seekText.setText(cardSet.getName() + ": Card " + (cardIndex + 1) + "/" + cardSet.getCards().size());
                            seekBar.setProgress(cardIndex);
                            updateCard();
                            dialog.dismiss();
                        }

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

    //changes the current cardSet to a different card set based on user choice
    protected void updateCardSet(int item){

        cardSet = user.getCardSets().get(item);
        currentCard = cardSet.getCards().get(cardIndex);
        seekText.setText(cardSet.getName() + ": Card " + (cardIndex+1) + "/" + cardSet.getCards().size());
        seekBar.setEnabled(true);
        addCard.setEnabled(true);
        removeCard.setEnabled(true);
        seekBar.setMax(cardSet.getCards().size()-1);
        updateCard();
    }

    //updates the card's value
    protected void updateCard(){

        currentCard = cardSet.getCards().get(cardIndex);
        if(side)
            textBox.setText(currentCard.getFront());
        else
            textBox.setText(currentCard.getBack());
    }

    //saves the current card
    protected void saveCard(){
        if(side)
            currentCard.setFront(textBox.getText().toString());
        else
            currentCard.setBack(textBox.getText().toString());
    }

    //deletes the card set if no cards remain
    protected void deleteCardSet(){

        cardSet = null;
        currentCard = null;
        cardIndex = 0;
        side = true;
        textBox.setText("");
        textBox.setHint("Enter text");
        seekBar.setProgress(0);
        seekBar.setMax(0);
        seekText.setText("Card 0/0");

        addCard.setEnabled(false);
        removeCard.setEnabled(false);

    }

    @Override
    //override on back pressed to send SelectionPage user info
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudySolo.this);
        alertDialog.setTitle("Leave Solo Study");
        alertDialog.setMessage("Are you sure you want to leave the Solo Study?");
        alertDialog.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Intent myIntent = new Intent(getApplicationContext(), SelectionPage.class);
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
