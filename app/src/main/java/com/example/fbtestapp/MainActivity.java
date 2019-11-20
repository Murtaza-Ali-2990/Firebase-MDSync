package com.example.fbtestapp;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "FB";
    private ArrayList<String> nameList = null;
    private RecyclerView recyclerView;
    private FirebaseFunctions firebaseFunctions;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);



        firebaseAuth = FirebaseAuth.getInstance();
        mAL = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
        firebaseAuth.addAuthStateListener(mAL);

        firebaseFunctions = FirebaseFunctions.getInstance();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final EditText editText = findViewById(R.id.etext);
        final TextView t = findViewById(R.id.textview);
        Button button = findViewById(R.id.button);
        Button logout = findViewById(R.id.logout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Map<String, Object> name = new HashMap<>();
                name.put("name", editText.getText().toString());
                name.put("contact", 90999990);

                final Map<String, Object> k = new HashMap<>();
                k.put("Gender", "Male");
                k.put("Name", editText.getText().toString());

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Not done ", task.getException());
                        }

                        String token = task.getResult().getToken();
                        Log.i(TAG, "onComplete: Token : "+ token);
                        k.put("Token", token);
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
                                    nameList = nList;
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
                firebaseAuth.signOut();
            }
        });
    }

    private Task<String> addMessage(Map<String, Object> obj) {
        return firebaseFunctions.getHttpsCallable("addMessage").call(obj)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (String) task.getResult().getData();
                    }
                });
    }
}
