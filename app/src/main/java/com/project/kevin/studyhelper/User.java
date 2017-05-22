package com.project.kevin.studyhelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kevin on 4/30/2017.
 */

@SuppressWarnings("serial")

public class User implements Serializable {

    private String username;
    private String password;

    ArrayList<CardSet> cardSets;

    public User(){

        cardSets = new ArrayList<CardSet>();
    }

    public User(String username, String password){

        this.username = username;
        this.password = password;
        cardSets = new ArrayList<CardSet>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<CardSet> getCardSets() {
        return cardSets;
    }

    public void setCardSets(ArrayList<CardSet> cardSets) {
        this.cardSets = cardSets;
    }

}
