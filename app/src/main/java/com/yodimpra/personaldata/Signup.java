package com.yodimpra.personaldata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yodimpra.personaldata.databinding.ActivitySignupBinding;
import com.yodimpra.personaldata.model.UserPersonal;

public class Signup extends AppCompatActivity {
    ActivitySignupBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference reference;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(Signup.this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(Signup.this);
        reference = FirebaseDatabase.getInstance().getReference("userpersonal");

        preferences = getSharedPreferences("userpersonal", MODE_PRIVATE); //// userdiary ganti
        editor = preferences.edit();

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.username.getText().toString();
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                storeDatatoFirebase(username, email, password);
            }
        });
    }

    private void storeDatatoFirebase(String username, String email,String password){
        dialog.setMessage("Please wait...");
        dialog.show();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String unique = user.getUid();
                    UserPersonal diary = new UserPersonal(username,email);

                    reference.child(unique).setValue(diary).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()){
                                editor.putString("unique", unique);
                                editor.commit();
                                Toast.makeText(Signup.this, "Register success!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(Signup.this, "Register failed!", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onComplete: "+task.getException());
                }
            }
        });
    }
}
