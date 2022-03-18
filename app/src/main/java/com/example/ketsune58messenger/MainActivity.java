package com.example.ketsune58messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messageDataBase = database.getReference("messages");
    DatabaseReference userDataBase = database.getReference("user");

    private static final int MAX_MESSAGE_LENGTH = 100;

    Utility utility = new Utility();

    ArrayList<String> messagesStorage = new ArrayList<>();
    RecyclerView mRecyclerviewOutput;
    EditText mEditTextInput;
    Button mButtonInput;
    FirebaseTranslator englishJapanTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerviewOutput = findViewById(R.id.message_output_recyclerview);
        mEditTextInput = findViewById(R.id.message_input_field);
        mButtonInput = findViewById(R.id.message_input_button);

        DataAdapter dataAdapter = new DataAdapter(this, messagesStorage);

        mRecyclerviewOutput.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerviewOutput.setAdapter(dataAdapter);

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.JA)
                        .build();

        englishJapanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        mButtonInput.setOnClickListener(view -> {
            String msg = mEditTextInput.getText().toString();
            if (msg.equals("") || msg.length() > MAX_MESSAGE_LENGTH) {
                return;
            }

            Intent intent = new Intent(this, RegActivity.class);

            //messageDataBase.push().setValue(msg);

            //messageDataBase.child("1").child("text").setValue(msg);
            //messageDataBase.child("messageCount").setValue(msg);
            mRecyclerviewOutput.smoothScrollToPosition(messagesStorage.size());
            //downloadModal(mEditTextInput.getText().toString());

            //mEditTextInput.setText("");

            Message message = new Message(99, 99, msg);

            ReadMessageCounter();

            messageDataBase.child("messageCount").child("counter").setValue(String.valueOf(utility.MESSAGE_COUNTER));

            //messageDataBase.child(String.valueOf(utility.MESSAGE_COUNTER)).setValue(message);
        });

        messageDataBase.child("1").addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //start each time for every item in database branch
                //String msg = snapshot.getValue(String.class);
                //downloadModal(msg);
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void downloadModal(String input) {
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

        englishJapanTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(aVoid -> {
            translateLanguage(input);
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Fail to download modal", Toast.LENGTH_SHORT).show());
    }

    private void translateLanguage(String input) {
        englishJapanTranslator.translate(input).addOnSuccessListener(s -> messagesStorage.add(s)).addOnFailureListener(e -> {
            mEditTextInput.setText(e.toString());
        });
    }

    private void ReadMessageCounter() {
        int total = 0;
        int tt = 0;
        messageDataBase.child("messageCount").addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //start each time for every item in database branch
                String msg = snapshot.getValue(String.class);
                assert msg != null;
                Utility.MESSAGE_COUNTER = Integer.parseInt(msg);
                mEditTextInput.setText(Utility.MESSAGE_COUNTER);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //return Integer.valueOf(tt[0]);
    }
}