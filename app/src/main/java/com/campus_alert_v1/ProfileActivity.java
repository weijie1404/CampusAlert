package com.campus_alert_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class ProfileActivity extends AppCompatActivity {
    private TextView profileName;
    private TextView profileEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser currentUser;
    private Button signOutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_account:
                        Intent intent0 = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.ic_home:
                        Intent intent1 = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent1);
                        ;
                        break;

                    case R.id.ic_report:
                        Intent intent2 = new Intent(ProfileActivity.this, PostActivity.class);
                        startActivity(intent2);

                        break;

                }


                return false;
            }
        });

        Button signOutButton = (Button) findViewById(R.id.buttonSignOut2);
        final TextView profileName = (TextView) findViewById(R.id.profile_name);
        final TextView profileEmail = (TextView) findViewById(R.id.profile_email);

        currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();

        mDatabaseUsers = mRef.child("Users").child(currentUser.getUid());

        profileName.setText(currentUser.getDisplayName());
        profileEmail.setText(currentUser.getEmail());


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

    }
}