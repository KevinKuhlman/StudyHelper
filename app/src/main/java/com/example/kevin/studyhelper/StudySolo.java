package com.example.kevin.studyhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

/**
 * Created by Kevin on 4/24/2017.
 */

public class StudySolo extends AppCompatActivity {


    int cardIndex;
    Intent myIntent;
    User user;
    EditText textBox;
    Boolean side;
    CardSet cardSet;
    int numberOfCards;
    Card currentCard;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_solo);

        cardIndex = 0;
        myIntent = getIntent();
        side = true;
        user = (User) myIntent.getSerializableExtra("User");
        textBox = (EditText) findViewById(R.id.card);

        Button createNewSet = (Button) findViewById(R.id.createNewSet);
        Button saveSet = (Button) findViewById(R.id.saveSet);
        Button loadSet = (Button) findViewById(R.id.loadSet);
        final ToggleButton cardSide = (ToggleButton) findViewById(R.id.side);
        ImageButton nextCard = (ImageButton) findViewById(R.id.nextCard);
        ImageButton previousCard = (ImageButton) findViewById(R.id.previousCard);


        createNewSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loadSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = new String[user.getCardSets().size()];
                for(int i= 0; i<user.getCardSets().size(); i++){
                    items[i] = user.getCardSets().get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(StudySolo.this);
                builder.setTitle("Choose Which Card Set");
                builder.setItems(items, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        updateCardSet(item);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        cardSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
                cardSide.setChecked(!side);
                side = !side;
                updateCard();
            }
        });

        nextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cardSet!=null){
                    if(cardSet.getCards()!=null){
                        if(cardIndex - (cardSet.getCards().size()-1) == 0){
                            cardIndex = 0;
                        }else{
                            cardIndex++;
                        }

                        saveCard();
                        side = true;
                        updateCard();
                    }

                }
            }
        });

        previousCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardSet!=null) {
                    if(cardSet.getCards()!=null){
                        if (cardIndex == 0) {
                            cardIndex = (cardSet.getCards().size() - 1);
                        } else {
                            cardIndex--;
                        }

                        saveCard();
                        side = true;
                        updateCard();
                    }

                }
            }
        });
    }

    protected void updateCardSet(int item){

        cardSet = user.getCardSets().get(item);
        currentCard = cardSet.getCards().get(cardIndex);
        updateCard();
    }

    protected void updateCard(){

        currentCard = cardSet.getCards().get(cardIndex);
        if(side)
            textBox.setText(currentCard.getFront());
        else
            textBox.setText(currentCard.getBack());
    }

    protected void saveCard(){
        if(side)
            currentCard.setFront(textBox.getText().toString());
        else
            currentCard.setBack(textBox.getText().toString());
    }

}
