package com.example.ketsune58messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegActivity extends AppCompatActivity {

    Button registerButton;
    Button authButton;
    EditText loginInputTxt;
    EditText passwordInputTxt;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userDataBase = database.getReference("user");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        loginInputTxt = findViewById(R.id.login_input_field);
        passwordInputTxt = findViewById(R.id.password_input_field);

        ArrayList<String> data = new ArrayList<>();

        registerButton = findViewById(R.id.registration_button);
        authButton = findViewById(R.id.auth_button);

        userDataBase.child("userCounter").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Object user = task. getResult().getValue(Object.class);

                String jsonString = String.valueOf(user); //assign your JSON String here
                JSONObject obj = null;
                try {
                    obj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    assert obj != null;
                    String pageName = obj.getString("count");
                    Utility.USER_ON_START = Integer.parseInt(pageName);
                    for (int i = 1; i < Utility.USER_ON_START + 1; i++) {
                        userDataBase.child(String.valueOf(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                Object userDown = task.getResult().getValue(Object.class);

                                String jsonString = String.valueOf(userDown); //assign your JSON String here
                                JSONObject obj1 = null;
                                try {
                                    obj1 = new JSONObject(jsonString);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    assert obj1 != null;
                                    String pageName1 = obj1.getString("login");
                                    String pageName2 = obj1.getString("password");
                                    data.add(pageName1);
                                    data.add(pageName2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        registerButton.setOnClickListener(view -> {
            String middle = String.valueOf(loginInputTxt.getText());
            String middle1 = String.valueOf(passwordInputTxt.getText());
            Intent intent = new Intent(this, MainActivity.class);
            //Intent intent = new Intent(this, MainActivity.class);
            try {
                for (int i = 0; i<data.size();i++)
                {
                    if(data.get(i).equals(middle) && data.get(i+1).equals(middle1)){
                        int ID = i/2 + 1;
                        intent.putExtra("USER_ID", ID);
                        startActivity(intent);
                    }
                }
            }
            catch (Exception e){
            }
        });

        authButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        });
    }
}