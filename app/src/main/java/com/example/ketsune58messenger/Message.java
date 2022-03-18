package com.example.ketsune58messenger;

public class Message {
    public int from;
    public int to;
    public String text;

    public Message(int from, int to, String text){
        this.from = from;
        this.to = to;
        this.text = text;
    }
}
