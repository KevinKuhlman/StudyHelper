package com.project.kevin.studyhelper;

/**
 * Created by Kevin on 5/28/2017.
 */

//User info for displaying in the Quiz's ListView
public class QuizInfo {

    String username;
    String answer;

    public QuizInfo(){

    }

    public QuizInfo(String username, String answer){
        this.username = username;
        this.answer = answer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
