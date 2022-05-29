package com.example.ketsune58messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DialogActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messageDataBase = database.getReference("messages");
    DatabaseReference userDataBase = database.getReference("user");
    DatabaseReference dialogDataBase = database.getReference("dialogs");

    ArrayList<String> dialogList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);



    }
}