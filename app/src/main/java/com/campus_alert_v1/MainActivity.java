package com.campus_alert_v1;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.support.design.widget.BottomNavigationView;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.squareup.picasso.Picasso;


public class  MainActivity extends AppCompatActivity {

    private RecyclerView mReportList;

    private DatabaseReference mDatabase;

    private DatabaseReference mDatabaseUsers;

    private DatabaseReference mDatabaseLike;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mProcessLike = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Report");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mReportList = (RecyclerView) findViewById(R.id.report_list);
        mReportList.setHasFixedSize(true);
        mReportList.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_account:
                        Intent intent0 = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent0);
                        break;

                    case R.id.ic_home:
                        Intent intent1 = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent1);
                        ;
                        break;

                    case R.id.ic_report:
                        Intent intent2 = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent2);

                        break;

                }


                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserExist();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Report,ReportViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(

                Report.class,
                R.layout.report_row,
                ReportViewHolder.class,
                mDatabase


        ) {
            @Override
            protected void populateViewHolder(ReportViewHolder viewHolder, Report model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setLocation(model.getLocation());
                viewHolder.setmLikeCount(post_key);

                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;


                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike){

                                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        mProcessLike = false;

                                    } else {

                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");

                                        mProcessLike = false;

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }
                });
            }
        };

        mReportList.setAdapter(firebaseRecyclerAdapter);

    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() == null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(registerIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder{

        View mView;

        TextView mLikesCount;
        ImageButton mLikebtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public ReportViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mLikebtn = (ImageButton) mView.findViewById(R.id.like_btn);
            mLikesCount = (TextView) mView.findViewById(R.id.like_count);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);
        }

        public void setmLikeCount(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mLikesCount.setText(dataSnapshot.child(post_key).getChildrenCount()+"");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setLikeBtn(final String post_key){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        mLikebtn.setImageResource(R.mipmap.ic_favorite_black_24dp);

                    } else {

                        mLikebtn.setImageResource(R.mipmap.ic_favorite_border_black_24dp);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc){

            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }


        public void setLocation(String location){

            TextView post_location = (TextView) mView.findViewById(R.id.post_location);
            post_location.setText(location);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();;
                    }
                }).setNegativeButton("No", null).show();
    }
}
