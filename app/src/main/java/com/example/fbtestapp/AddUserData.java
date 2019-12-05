package com.example.fbtestapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

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
                userData.setTimestamp(new Timestamp(new Date()));
                userData.setTstamp(new Timestamp(new Date()).getSeconds());
                userData.setToken(getSharedPreferences("token", MODE_PRIVATE).getString("token", ""));

                DatabaseHandler db = new DatabaseHandler(AddUserData.this);
                db.addDataNotif(userData);
                long rowId = db.addDataUpdate(userData);
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

                getSharedPreferences("timestamp", MODE_PRIVATE).edit().putString("timestamp", new Timestamp(new Date()).toString()).apply();
                getSharedPreferences("timestamp", MODE_PRIVATE).edit().putLong("tstamp", new Timestamp(new Date()).getSeconds()).apply();
                Intent intent = new Intent(AddUserData.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddUserData.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
