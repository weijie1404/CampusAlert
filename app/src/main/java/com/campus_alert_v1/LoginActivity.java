package com.campus_alert_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MiMessage";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean connectOn=false;
    private EditText mEmailField;
    private EditText mPasswordField;
    private FirebaseUser mCurrentUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Runnable checkInternet = new Runnable() {
            @Override
            public void run() {
                onCheckInternetConnection(LoginActivity.this);
            }
        };
        new Thread(checkInternet).start();

        mEmailField = (EditText)findViewById(R.id.email_box);
        mPasswordField = (EditText)findViewById(R.id.password_box);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.buttonRegister).setOnClickListener(this);
        findViewById(R.id.forgot_pass).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser= mAuth.getCurrentUser();
        if(mCurrentUser!=null){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        Log.i(TAG, "onCreate");
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                else
                    Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onCreate");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        if(mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    private void createAccount(String email, String password)
    {
        Log.d(TAG, "createAccount:" + email);
        if(!validateForm())
            return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Failed",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void signIn(String email, String password)
    {
        Log.d(TAG, "signIn:" +email);
        if(!validateForm()) {
            Toast.makeText(LoginActivity.this, "Invalid Entry",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        if(!(connectOn)){
            Toast.makeText(LoginActivity.this, "Please check your network",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if(!task.isSuccessful())
                        {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Failed",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                        else
                        {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private void forgotPassword(String email){
        if(TextUtils.isEmpty(email))
        {
            mEmailField.setError("Required");
            return;

        }
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Email sent",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.sign_in_button) {

            if(!connectOn)
            {
                Runnable checkInternet = new Runnable() {
                    @Override
                    public void run() {
                        onCheckInternetConnection(LoginActivity.this);
                    }
                };
                new Thread(checkInternet).start();
            }

            progressDialog = new ProgressDialog(LoginActivity.this,progressDialog.THEME_HOLO_DARK);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());

        }

        else if(i == R.id.buttonRegister){
            Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);

        }
        else if (i == R.id.forgot_pass){
            forgotPassword(mEmailField.getText().toString());
        }
    }

    private boolean validateForm()
    {
        boolean valid = true;
        String email = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            mEmailField.setError("Required");
            valid = false;
        }
        else
            mEmailField.setError(null);

        String password = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(password))
        {
            mPasswordField.setError("Required");
            valid = false;
        }
        else
            mPasswordField.setError(null);

        if (valid==false){
            progressDialog.dismiss();
        }
        return valid;


    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
    }

    public void onCheckInternetConnection(Context context)
    {
        connectOn = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if((networkInfo != null) && networkInfo.isConnected())
        {
            connectOn = true;
        }
        if(connectOn)
        {
            try{
                HttpURLConnection urlConnection = (HttpURLConnection)(new URL("http://clients3.google.com/generate_204").openConnection());
                urlConnection.setRequestProperty("User-Agent", "Android");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(1500);
                urlConnection.connect();
                if(urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0)
                {
                    connectOn = true;
                }
            }catch (IOException e)
            {
                connectOn = false;
            }
        }
    }

}

