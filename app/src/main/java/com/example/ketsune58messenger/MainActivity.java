package com.example.ketsune58messenger;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messageDataBase = database.getReference("messages");
    DatabaseReference userDataBase = database.getReference("user");

    private static final int MAX_MESSAGE_LENGTH = 100;

    ArrayList<Integer> dialogsStorage = new ArrayList<>();
    ArrayList<String> messagesStorage = new ArrayList<>();
    ArrayList<String> messageOwner = new ArrayList<>();
    RecyclerView mRecyclerviewOutput;
    EditText mEditTextInput;
    Button mButtonInput;
    FirebaseTranslator englishJapanTranslator;

    //public int USER_ID = (int)getIntent().getSerializableExtra("USER_ID");

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int USER_ID = (int)getIntent().getSerializableExtra("USER_ID");
        mRecyclerviewOutput = findViewById(R.id.message_output_recyclerview);
        mEditTextInput = findViewById(R.id.message_input_field);
        mButtonInput = findViewById(R.id.message_input_button);

        DataAdapter dataAdapter = new DataAdapter(this, messagesStorage, messageOwner);

        mRecyclerviewOutput.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerviewOutput.setAdapter(dataAdapter);

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.JA)
                        .build();

        englishJapanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        messageDataBase.child("messageCount").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                Object user = snapshot.getValue(Object.class);
                Utility.MESSAGE_ON_START = Integer.parseInt((String) user);

                for (int i = 1; i < Utility.MESSAGE_ON_START + 1; i++) {
                    ValueEventListener postListener = new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Message message = dataSnapshot.getValue(Message.class);
                            assert message != null;

                            findOwner(message.from);

                            downloadModal(String.valueOf(message.text));
                            dataAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    messageDataBase.child(String.valueOf(i)).addValueEventListener(postListener);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageDataBase.child("messageCount").child("counter").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Object user = task.getResult().getValue(Object.class);
                Utility.MESSAGE_ON_START = Integer.parseInt((String) user);
                for (int i = 1; i < Utility.MESSAGE_ON_START + 1; i++) {

                    createDialogBase(i);

                    ValueEventListener postListener = new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Message message = dataSnapshot.getValue(Message.class);
                            assert message != null;

                            findOwner(message.from);
                            dialogsStorage.add(message.from);
                            dialogsStorage.add(message.to);

                            downloadModal(String.valueOf(message.text));

                            dataAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    messageDataBase.child(String.valueOf(i)).addValueEventListener(postListener);
                }
            }
        });

        mButtonInput.setOnClickListener(view -> {
            String msg = mEditTextInput.getText().toString();
            if (msg.equals("") || msg.length() > MAX_MESSAGE_LENGTH) {
                return;
            }

            mRecyclerviewOutput.smoothScrollToPosition(messagesStorage.size());

            mEditTextInput.setText("");

            Message message = new Message(USER_ID, 2, msg);

            WriteNewMessage(message, dataAdapter);

            dataAdapter.notifyDataSetChanged();
        });
    }

    private void downloadModal(String input) {
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();

        englishJapanTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(aVoid -> {
            translateLanguage(input);
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Fail to download modal", Toast.LENGTH_SHORT).show());
    }

    private void findOwner(int ownerID) {
        userDataBase.child(String.valueOf(ownerID)).child("login").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Object user = task.getResult().getValue(Object.class);
                Utility.CURRENT_OWNER = String.valueOf(user);

                messageOwner.add(String.valueOf(user));
            }
        });
    }

    private void createDialogBase(int messageID){
        messageDataBase.child(String.valueOf(messageID)).child("from").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Object message = task.getResult().getValue(Object.class);

                String middle = String.valueOf(message);
                dialogsStorage.add(Integer.valueOf(middle));
            }
        });

        messageDataBase.child(String.valueOf(messageID)).child("to").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Object message = task.getResult().getValue(Object.class);

                String middle = String.valueOf(message);
                dialogsStorage.add(Integer.valueOf(middle));
            }
        });
    }

    private void translateLanguage(String input) {
        englishJapanTranslator.translate(input).addOnSuccessListener(s -> messagesStorage.add(s)).addOnFailureListener(e -> {
            mEditTextInput.setText(e.toString());
        });
    }

    private void WriteNewMessage(Message message, DataAdapter dataAdapter) {
        messageDataBase.child("messageCount").addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //start each time for every item in database branch
                String msg = snapshot.getValue(String.class);
                assert msg != null;
                Utility.MESSAGE_COUNTER = Integer.parseInt(msg);
                Utility.MESSAGE_COUNTER++;

                messageDataBase.child("messageCount").child("counter").setValue(String.valueOf(Utility.MESSAGE_COUNTER));

                messageDataBase.child(String.valueOf(Utility.MESSAGE_COUNTER)).setValue(message);

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