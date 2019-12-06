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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbtestapp.ui.login.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "FB";
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAL;
    private ListenerRegistration registration;
    private FirebaseFirestore db;
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
            return;
        }

        db = FirebaseFirestore.getInstance();
        databaseHandler = new DatabaseHandler(this);

        final EditText editText = findViewById(R.id.etext);
        final TextView t = findViewById(R.id.textview);
        Button button = findViewById(R.id.button);
        Button logout = findViewById(R.id.logout);
        Button addData = findViewById(R.id.add_data_button);
        Button refresh = findViewById(R.id.refresh);
        Button notif = findViewById(R.id.notif_table);

        adapter = new RecyclerAdapter(new DatabaseHandler(this).getListData(0));
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

  /*      db.collection("user").document(firebaseAuth.getCurrentUser().getUid()).collection("names").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    Log.i(TAG, "onEvent: FAILED");
                    return;
                }
                Log.i(TAG, "onEvent: "+ queryDocumentSnapshots);

                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Log.i(TAG, "onEvent: " + doc);
                }
            }
        });
*/

        Log.i(TAG, "onCreate: " + new Timestamp(new Date()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Map<String, Object> name = new HashMap<>();
                name.put("name", editText.getText().toString());
                name.put("contact", 90999990);

                final Map<String, Object> k = new HashMap<>();
                k.put("gender", "Male");
                k.put("name", editText.getText().toString());

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Not done ", task.getException());
                        }

                        String token = task.getResult().getToken();
                        Log.i(TAG, "onComplete: Token : "+ token);
                        k.put("token", token);
                        k.put("timestamp", new Timestamp(new Date()));
                    }
                });

            /*    db.collection("user").document(firebaseAuth.getCurrentUser().getUid()).update(name)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "onSuccess: Added");
                                db.collection("user/" + firebaseAuth.getCurrentUser().getUid()+ "/names").add(k)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.i(TAG, "onSuccess: " + documentReference.getPath());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: ", e);
                            }
                        });
*/
                db.collection("user").document(firebaseAuth.getCurrentUser().getUid()).set(name)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "onSuccess: Added ");
                                db.collection("user/" + firebaseAuth.getCurrentUser().getUid() + "/names").add(k)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.i(TAG, "onSuccess: " + documentReference.getPath());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "onFailure: ",e);
                            }
                        });
            }
        });

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("user").get().
                        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    ArrayList<String> nList = new ArrayList<>();
                                    for(QueryDocumentSnapshot doc : task.getResult()) {
                                        Log.i(TAG, "onComplete: " + doc.get("name"));
                                        t.setText(doc.get("name").toString());
                                        break;
                                    }
                                }
                                else
                                    Log.i(TAG, "onComplete: TASK FAILED SUCCESSFULLY");
                            }
                        });
            }
        });

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
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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

        registration = db.collection("user").document(firebaseAuth.getCurrentUser().getUid()).collection("names")
                .whereGreaterThan("id", getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.i(TAG, "onEvent: FAILED");
                            return;
                        }
                        Log.i(TAG, "onEvent: ME?" + queryDocumentSnapshots);
                        Log.i(TAG, "onEvent: previous" + getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0));

                        for(DocumentSnapshot doc: queryDocumentSnapshots){
                            Log.i(TAG, "onEvent: YES EM " + doc);
                            if(doc.getLong("id") > getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0)){
                                databaseHandler.addDataUpdate(UserData.makeUserData(doc));
                                Log.i(TAG, "onEvent: YES EM ye hua " + doc);
                                adapter.updateData(databaseHandler.getListData(0));
                                getSharedPreferences("id", MODE_PRIVATE).edit().putLong("id", doc.getLong("id")).apply();
                            }
                        }

                        Log.i(TAG, "onEvent: " + getSharedPreferences("id", MODE_PRIVATE).getLong("id", 0));

                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: CALLED");
        if(registration != null)
            registration.remove();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
    }
}
