package com.fuzzyapps.gustoboliviano;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class visit_profileFragment extends Fragment {
    //UI VARIABLES
    private TextView profileReviewsNumber, profileFollowersNumber, profileFollowingNumber, profileStatus, profileUserName;
    private FancyButton profileFollowButton;
    private CircularImageView profileImage;
    private TabLayout profileOptions;
    private LayoutInflater layoutInflater;
    private boolean amIFollowing;
    //FIREBASE VARIABLES
    //private Button upload;private EditText urlUpload; TextView progressText;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private User user;

    public visit_profileFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.visit_profile_fragment, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        profileReviewsNumber = (TextView) view.findViewById(R.id.profileReviewsNumber);
        profileFollowersNumber = (TextView) view.findViewById(R.id.profileFollowersNumber);
        profileFollowingNumber = (TextView) view.findViewById(R.id.profileFollowingNumber);
        profileStatus = (TextView) view.findViewById(R.id.profileStatus);
        profileUserName = (TextView) view.findViewById(R.id.profileUserName);

        profileOptions = (TabLayout) view.findViewById(R.id.profileOptions);
        profileImage = (CircularImageView) view.findViewById(R.id.profileImage);
        profileFollowButton = (FancyButton) view.findViewById(R.id.profileFollowButton);

        layoutInflater = getActivity().getLayoutInflater();
        /*upload = (Button) view.findViewById(R.id.upload);
        urlUpload = (EditText) view.findViewById(R.id.urlUpload);
        progressText = (TextView) view.findViewById(R.id.progressText);*/
        //DATABASE REFERENCE
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        profileFollowButton.setVisibility(View.GONE);
        //RESEÑAS
        profileOptions.addTab(profileOptions.newTab().setIcon(R.mipmap.ic_rate_review_black_24dp), true);
        //FAVORITOS
        profileOptions.addTab(profileOptions.newTab().setIcon(R.mipmap.ic_bookmark_black_24dp));
        profileOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setProfileTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //SETTING FIRST FRAGMENT
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.profileFrame, new visit_nav_review())
                .commit();
        profileFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amIFollowing){
                    displayUnFollowDialog();
                }else{
                    followUser();
                }
            }
        });
        setQueries();
    }

    private void setQueries() {
        //FIREBASE LISTENER
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                try{
                    profileStatus.setText(user.getStatus());
                }catch (Exception e){}
                try{
                    profileUserName.setText(user.getName());
                }catch (Exception e){}
                try{
                    Log.e("nav_profileFragment1",user.getImage_url()+"");
                    if(user.getImage_url() != null){
                        Log.e("not empty",user.getImage_url()+"");
                        Picasso.with(getActivity()).load(user.getImage_url()).into(profileImage);
                    }else{
                        Log.e("empty",user.getImage_url()+"");
                        Picasso.with(getActivity()).load(R.drawable.defaultprofile).into(profileImage);
                    }
                }catch (Exception e){
                    Log.e("nav_profileFragment2",e.getMessage());
                    Picasso.with(getActivity()).load(R.drawable.defaultprofile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("nav_profileFragment", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("users").child(Globals.visitedID).addValueEventListener(userListener);
        Query reviewsQuery = mDatabase.child("reviewUser").child(Globals.visitedID).orderByChild("timestamp");
        reviewsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("setQueries key",""+dataSnapshot.getKey());
                Log.e("retrieve",""+dataSnapshot.getValue());
                profileReviewsNumber.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY TO SEE WHO FOLLOWS ME
        Query followingQuery = mDatabase.child("following").child(Globals.visitedID).orderByValue().equalTo(true);
        followingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    profileFollowersNumber.setText(dataSnapshot.getChildrenCount()+"");
                }else{
                    profileFollowersNumber.setText(R.string.zero);
                }
                Log.e("following",""+dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY TO SEE WHO DO I FOLLOW AS USER
        final Query followersQuery = mDatabase.child("followers").child(Globals.visitedID).orderByValue().equalTo(true);
        followersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    profileFollowingNumber.setText(dataSnapshot.getChildrenCount()+"");
                }else{
                    profileFollowingNumber.setText(R.string.zero);
                }
                Log.e("followers",""+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY TO SEE IF I FOLLOW
        Query doIFollow = mDatabase.child("following").child(Globals.visitedID).orderByKey().equalTo(Globals.userID);
        doIFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileFollowButton.setVisibility(View.VISIBLE);
                if(dataSnapshot.getValue() != null){
                    amIFollowing = dataSnapshot.child(Globals.userID).getValue(boolean.class);
                    if(amIFollowing){
                        profileFollowButton.setGhost(false);
                        profileFollowButton.setBackgroundColor(Color.parseColor("#55acee"));
                        profileFollowButton.setFocusBackgroundColor(Color.parseColor("#8cc9f8"));
                        profileFollowButton.setBorderColor(Color.parseColor("#FFFFFF"));
                        profileFollowButton.setTextColor(Color.parseColor("#FFFFFF"));
                        try {
                            profileFollowButton.setText(getString(R.string.following));
                        }catch (Exception e){}
                    }else{
                        //NOT FOLLOWING
                        profileFollowButton.setGhost(true);
                        profileFollowButton.setFocusBackgroundColor(Color.parseColor("#949494"));
                        profileFollowButton.setBorderColor(Color.parseColor("#949494"));
                        profileFollowButton.setTextColor(Color.parseColor("#949494"));
                        try {
                            profileFollowButton.setText(getString(R.string.follow));
                        }catch (Exception e){}
                    }
                }else{
                    //NOT FOLLOWING
                    profileFollowButton.setGhost(true);
                    profileFollowButton.setFocusBackgroundColor(Color.parseColor("#949494"));
                    profileFollowButton.setBorderColor(Color.parseColor("#949494"));
                    profileFollowButton.setTextColor(Color.parseColor("#949494"));
                    try {
                        profileFollowButton.setText(getString(R.string.follow));
                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setProfileTab(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                //RESEÑAS
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFrame, new visit_nav_review())
                        .commit();
                break;
            case 1:
                //FAVORITOS
                fragmentManager.beginTransaction()
                        .replace(R.id.profileFrame, new visit_nav_favorites())
                        .commit();
                break;
            case 2:

                break;
            default:
                break;
        }
    }
    private void displayUnFollowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= layoutInflater.inflate(R.layout.item_unfollow, null);
        builder.setView(view);
        final CircularImageView image = (CircularImageView) view.findViewById(R.id.image);
        final TextView unFollowTitle = (TextView) view.findViewById(R.id.unFollowTitle);
        Picasso.with(getActivity()).load(user.getImage_url()).into(image);
        unFollowTitle.setText(user.getName());
        builder.setPositiveButton(R.string.unfollow, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                unFollowUser();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void unFollowUser() {
        Map<String, Object> userChild = new HashMap<>();
        userChild.put(Globals.userID, false);
        mDatabase.child("following").child(Globals.visitedID).updateChildren(userChild);
        Map<String, Object> establishmentChild = new HashMap<>();
        establishmentChild.put(Globals.visitedID, false);
        mDatabase.child("followers").child(Globals.userID).updateChildren(establishmentChild);
    }
    private void followUser() {
        Map<String, Object> userChild = new HashMap<>();
        userChild.put(Globals.userID, true);
        mDatabase.child("following").child(Globals.visitedID).updateChildren(userChild);
        Map<String, Object> establishmentChild = new HashMap<>();
        establishmentChild.put(Globals.visitedID, true);
        mDatabase.child("followers").child(Globals.userID).updateChildren(establishmentChild);
    }
}
