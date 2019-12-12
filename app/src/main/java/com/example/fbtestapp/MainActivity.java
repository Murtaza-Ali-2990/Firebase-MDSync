package com.example.fbtestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.fbtestapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private String TAG = "FB";
    private RecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ListenerRegistration registration;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        firebaseFirestore = FirebaseFirestore.getInstance();
        databaseHandler = new DatabaseHandler(this);

        Button logout = findViewById(R.id.logout);
        Button addData = findViewById(R.id.add_data_button);
        Button refresh = findViewById(R.id.refresh);
        Button notif = findViewById(R.id.notif_table);

        adapter = new RecyclerAdapter(new DatabaseHandler(this).getListData(0));
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("token", MODE_PRIVATE).edit().putString("token", "").apply();
                firebaseAuth.signOut();
            }
        });

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddUserData.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.updateData(databaseHandler.getListData(0));
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotificationTableActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);

        registration = firebaseFirestore.collection("user").document(firebaseAuth.getCurrentUser().getUid()).collection("names")
                .whereGreaterThan("id", getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.i(TAG, "onEvent: FAILED");
                            return;
                        }
                        Log.i(TAG, "onEvent: current id " + getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0));

                        assert queryDocumentSnapshots != null;
                        for(DocumentSnapshot doc: queryDocumentSnapshots){
                            Log.i(TAG, "onEvent: Document in snapshots " + doc);
                            if(doc.getLong("id") > getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0)){
                                databaseHandler.addDataUpdate(UserData.makeUserData(doc));
                                Log.i(TAG, "onEvent: Document added " + doc);
                                adapter.updateData(databaseHandler.getListData(0));
                                getSharedPreferences("id", MODE_PRIVATE).edit().putLong("id", doc.getLong("id")).apply();
                            }
                        }
                        Log.i(TAG, "onEvent: updated id " + getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0));
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
        if(registration != null)
            registration.remove();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
    }
}
