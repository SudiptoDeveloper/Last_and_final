package com.abd.lastandfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button buttonPwdReset;
    private EditText editTextpwdRestEmail;
    private ProgressBar progressBarforgetPwd;
    private FirebaseAuth authForget;

    private static final String TAG = "ForgetPasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().setTitle("ForgetPasswordActivity");

        editTextpwdRestEmail = findViewById(R.id.editText_password_reset_email);
        buttonPwdReset = findViewById(R.id.button_password_reset);
        progressBarforgetPwd = findViewById(R.id.progressBar);

        buttonPwdReset.setOnClickListener(view -> {
            String email = editTextpwdRestEmail.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgetPasswordActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                editTextpwdRestEmail.setError("Full name is required");
                editTextpwdRestEmail.requestFocus();

            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgetPasswordActivity.this, "Please enter your Email ", Toast.LENGTH_LONG).show();
                editTextpwdRestEmail.setError("Email is required");
                editTextpwdRestEmail.requestFocus();
            } else {
                progressBarforgetPwd.setVisibility(View.VISIBLE);
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        authForget = FirebaseAuth.getInstance();
        authForget.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Please check your inbox for password rest link ", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextpwdRestEmail.setError("User does not exists or is a longer valid. Please register again.");
                    } catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgetPasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
                progressBarforgetPwd.setVisibility(View.GONE);
            }
        });
    }
}