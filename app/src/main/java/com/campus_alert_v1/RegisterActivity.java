package com.campus_alert_v1;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Handler;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserProfileChangeRequest;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPassword;

    private Button mRegisterBtn;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mProgress = new ProgressDialog(this);


        mNameField = (EditText) findViewById(R.id.nameField);
        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mRegisterBtn =(Button) findViewById(R.id.registerBtn);
        mConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();


            }
        });


    }

    private void startRegister() {

        final String name = mNameField.getText().toString().trim();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email) && !(email.matches("sunway.edu.my"))){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if ((TextUtils.isEmpty(password)) && (password.length() < 6)) {
            Toast.makeText(this, "Please enter minimum 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please reenter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!(password.equals(confirmPassword))) {
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    progressDialog = new ProgressDialog(RegisterActivity.this,progressDialog.THEME_HOLO_DARK);
                    progressDialog.setMessage("Registerating...");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,"Registration Success!",Toast.LENGTH_SHORT).show();
                        }
                    }, 1500);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog = new ProgressDialog(RegisterActivity.this,progressDialog.THEME_HOLO_DARK);
                            progressDialog.setMessage("Logging in...");
                            progressDialog.show();
                        }
                    }, 2500);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseUser currentUser=mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
                }else{
                    Toast.makeText(RegisterActivity.this,"Registration Fail!",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


}

