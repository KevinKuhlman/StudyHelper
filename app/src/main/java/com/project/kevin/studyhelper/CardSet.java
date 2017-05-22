package com.project.kevin.studyhelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kevin on 5/7/2017.
 */

public class CardSet implements Serializable {


    ArrayList<Card> cards;
    String name;

    public CardSet(){

        cards = new ArrayList<Card>();

    }

    public CardSet(String name){

        this.name = name;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }






}
