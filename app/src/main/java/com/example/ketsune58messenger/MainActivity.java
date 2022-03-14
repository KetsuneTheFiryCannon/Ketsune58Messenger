package com.example.ketsune58messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static int MAX_MESSAGE_LENGTH = 100;

    RecyclerView mRecyclerviewOutput;
    EditText mEditTextInput;
    Button mButtonInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> messagesStorage = new ArrayList<>();

        mRecyclerviewOutput = findViewById(R.id.message_output_recyclerview);
        mEditTextInput = findViewById(R.id.message_input_field);
        mButtonInput = findViewById(R.id.message_input_button);

        DataAdapter dataAdapter = new DataAdapter(this, messagesStorage); //sending array list to adapter


        mRecyclerviewOutput.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerviewOutput.setAdapter(dataAdapter);


        mButtonInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mEditTextInput.getText().toString();
                if(msg.equals("") || msg.length()>MAX_MESSAGE_LENGTH){
                    return;
                }

                myRef.push().setValue(msg);
                mEditTextInput.setText("");
                mRecyclerviewOutput.smoothScrollToPosition(messagesStorage.size());
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //start each time for every item in database branch
                String msg = snapshot.getValue(String.class);
                messagesStorage.add(msg); //filling array list with firebase elements
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
}