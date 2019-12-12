package com.example.fbtestapp.ui.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fbtestapp.MainActivity;
import com.example.fbtestapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String token_id;
    String TAG = "Login";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()) {
                    Log.i(TAG, "onComplete: Not done ", task.getException());
                }

                String token = task.getResult().getToken();
                Log.i(TAG, "onComplete: Token : "+ token);
                token_id = token;
            }
        });

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uname = usernameEditText.getText().toString().trim();
                final String pass = passwordEditText.getText().toString().trim();

                if(!uname.isEmpty() && !pass.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(uname, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LOGIN", "LoginUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.i("LOGIN", "onComplete: " + user.getUid());
                                sendToFirebase();
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            } else {

                                mAuth.createUserWithEmailAndPassword(uname, pass)
                                        .addOnCompleteListener(new  OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d("LOGIN", "createUserWithEmail:success");
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Log.i("LOGIN", "onComplete: " + user.getUid());
                                                    sendToFirebase();
                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                    //updateUI(user);
                                                } else {

                                                    Log.i("LOGIN", "onComplete: Couldn't Log in " +task.getResult());
                                                }

                                                // ...
                                            }
                                        });
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "EMPTY", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendToFirebase() {
        db.collection("user").document(mAuth.getCurrentUser().getUid()).collection("Token")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() == 0) {
                        ArrayList<String> tokens = new ArrayList<>();
                        tokens.add(token_id);
                        HashMap<String, Object> t = new HashMap<>();
                        t.put("Token", tokens);
                        db.collection("user").document(mAuth.getCurrentUser().getUid()).collection("Token")
                                .document("token").set(t).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "onSuccess: Token sent");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: Token Failed", e);
                            }
                        });
                    } else {
                        DocumentSnapshot token = task.getResult().getDocuments().get(0);
                        ArrayList<String> tokens = (ArrayList<String>)token.get("Token");
                        int flag = 0;
                        for (String t : tokens) {
                            if (t.equals(token_id))
                                flag = 1;
                        }
                        if (flag == 0) {
                            tokens.add(token_id);
                            HashMap<String, Object> t = new HashMap<>();
                            t.put("Token", tokens);
                            db.collection("user").document(mAuth.getCurrentUser().getUid()).collection("Token")
                                    .document("token").set(t).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "onSuccess: Token sent");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "onFailure: Token Failed", e);
                                }
                            });
                        }
                    }
                } else {
                    Log.i(TAG, "onComplete: Task Failed");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Failed Token", e);
            }
        });

        getSharedPreferences("token", MODE_PRIVATE).edit().putString("token", token_id).apply();
        getSharedPreferences("id", MODE_PRIVATE).edit().putLong("id", 0).apply();
    }
}
