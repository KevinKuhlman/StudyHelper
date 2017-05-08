package com.example.kevin.studyhelper;

import java.io.Serializable;

/**
 * Created by Kevin on 5/1/2017.
 */

public class Card implements Serializable {

    private String front;
    private String back;

    public Card(){

    }

    public Card(String front, String back){
        this.front = front;
        this.back = back;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }



}
