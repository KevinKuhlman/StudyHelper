package com.project.kevin.studyhelper;

/**
 * Created by Kevin on 5/26/2017.
 */

public class ListInfo {

    String username;
    String cardSetName;
    Boolean ready;

    ListInfo(){

    }



    ListInfo(String username, String cardSetName, Boolean ready){

        this.username = username;
        this.cardSetName = cardSetName;
        this.ready = ready;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCardSetName() {
        return cardSetName;
    }

    public void setCardSetName(String cardSetName) {
        this.cardSetName = cardSetName;
    }

    public Boolean getReady() {
        return ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

}
