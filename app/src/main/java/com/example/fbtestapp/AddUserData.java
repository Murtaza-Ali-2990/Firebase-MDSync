package com.example.fbtestapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddUserData extends AppCompatActivity {

    private static String TAG = "AddUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText nameText = findViewById(R.id.name_edit_text);
        final EditText surnameText = findViewById(R.id.surname_edit_text);
        final EditText genderText = findViewById(R.id.sex_edit_text);
        final EditText ageText = findViewById(R.id.age_edit_text);
        Button submitButton = findViewById(R.id.submit_button);
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserData userData = new UserData();
                userData.setName(nameText.getText().toString().trim());
                userData.setSurname(surnameText.getText().toString().trim());
                userData.setSex(genderText.getText().toString().trim());
                userData.setAge(Integer.parseInt(ageText.getText().toString().trim()));
                userData.setToken(getSharedPreferences("token", MODE_PRIVATE).getString("token", ""));
                userData.setUpdates(0);

                DatabaseHandler db = new DatabaseHandler(AddUserData.this);
                long rowId = db.addDataNotif(userData);
                long k = db.addDataUpdate(userData);
                userData.setId(rowId);

                DocumentReference ref = firestore.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.collection("names").document(String.valueOf(rowId)).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: Doc added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to add data", e);
                    }
                });

                getSharedPreferences("id", MODE_PRIVATE).edit().putLong("id", k).apply();

                if(getIntent().getIntExtra("activity", 0) == 1){
                    Intent intent = new Intent(AddUserData.this, NotificationTableActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(AddUserData.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getIntent().getIntExtra("activity", 0) == 1){
            Intent intent = new Intent(AddUserData.this, NotificationTableActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(AddUserData.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
