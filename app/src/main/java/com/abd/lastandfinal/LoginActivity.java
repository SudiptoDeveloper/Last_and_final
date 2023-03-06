package com.abd.lastandfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;

    private TextView textViewRegisterHere, textViewForgettenPwd;

    private ProgressBar progressBarLog;
    FirebaseAuth auth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("LogIn");

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBarLog = findViewById(R.id.progressBarLogin);

        textViewRegisterHere = findViewById(R.id.textView_register_link);
        textViewRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });


        textViewForgettenPwd = findViewById(R.id.textView_forgetPwd);
        textViewForgettenPwd.setOnClickListener(view -> {
            Toast.makeText(LoginActivity.this, "You can rest your password now!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class ));
        });

        auth = FirebaseAuth.getInstance();

        //Login user
        Button button = findViewById(R.id.button_login);
        button.setOnClickListener(view -> {
            String loginEmail = editTextLoginEmail.getText().toString();
            String loginPwd = editTextLoginPwd.getText().toString();

            if (TextUtils.isEmpty(loginEmail)) {
                Toast.makeText(LoginActivity.this, "Please enter your Email", Toast.LENGTH_LONG).show();
                editTextLoginEmail.setError("Email is required");
                editTextLoginEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Please re-enter your Email", Toast.LENGTH_LONG).show();
                editTextLoginEmail.setError("Valid email is required");
                editTextLoginEmail.requestFocus();
            } else if (TextUtils.isEmpty(loginPwd)) {
                Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                editTextLoginPwd.setError("Password is required");
                editTextLoginPwd.requestFocus();
            } else {
                progressBarLog.setVisibility(View.VISIBLE);
                loginUser(loginEmail,loginPwd);
            }


        });

    }

    private void loginUser(String email, String pwd) {
        auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //Get instance of the current user
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            //Checked if email is verified before user can access their profile
                            if (firebaseUser.isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "You are login now", Toast.LENGTH_LONG).show();
                                // open user profile
                                //start the UserProfileActivity
                                startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                                finish();       //close the Activity
                            } else {
                                firebaseUser.sendEmailVerification();
                                auth.signOut();     //signout user
                                showAlertDiolog();
                            }
                        }/* if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                }*/ else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editTextLoginEmail.setError("User does not exists or is a longer valid. Please register again.");
                                editTextLoginEmail.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextLoginEmail.setError("Valid credentials. Kindly, Check and re-enter.");
                                editTextLoginEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            progressBarLog.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void showAlertDiolog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verified your email now. You can not login without email verification");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //create the AlertDiolog
        AlertDialog alertDialog = builder.create();
        // show the AlertDiolog
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser()!= null){
            Toast.makeText(LoginActivity.this,"Are you Logged In!",Toast.LENGTH_LONG).show();

            //start the UserProfileActivity
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();       //close the Activity
        } else {
            Toast.makeText(LoginActivity.this,"You can Login now!",Toast.LENGTH_LONG).show();
        }
    }
}