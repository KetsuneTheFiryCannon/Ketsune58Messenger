package com.example.ketsune58messenger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    DatabaseReference myRef = database.getReference("message");

    private static final int MAX_MESSAGE_LENGTH = 100;

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

        DataAdapter dataAdapter = new DataAdapter(this, messagesStorage); //sending array list to adapter

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
            if(msg.equals("") || msg.length()>MAX_MESSAGE_LENGTH){
                return;
            }

            myRef.push().setValue(msg);

            mRecyclerviewOutput.smoothScrollToPosition(messagesStorage.size());

            //downloadModal(mEditTextInput.getText().toString());

            mEditTextInput.setText("");
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //start each time for every item in database branch
                String msg = snapshot.getValue(String.class);
                downloadModal(msg);
                //messagesStorage.add(msg); //filling array list with firebase elements
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
        // below line is use to download the modal which
        // we will require to translate in german language
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

        // below line is use to download our modal.
        englishJapanTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(aVoid -> {

            // this method is called when modal is downloaded successfully.
            //Toast.makeText(MainActivity.this, "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();

            // calling method to translate our entered text.
            translateLanguage(input);

        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Fail to download modal", Toast.LENGTH_SHORT).show());
    }

    private void translateLanguage(String input) {
        englishJapanTranslator.translate(input).addOnSuccessListener(s -> {
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            //mEditTextInput.setText(s);
            messagesStorage.add(s);
        }).addOnFailureListener(e -> {
            mEditTextInput.setText(e.toString());
            //Toast.makeText(MainActivity.this, "Fail to translate", Toast.LENGTH_SHORT).show();
        });
    }
}