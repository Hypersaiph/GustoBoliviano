package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class fragment_establishment extends Fragment {
    //REVIEW FORM VARIBALES
    private EditText reviewTitle, reviewDescription;
    private CircularImageView profileImage;
    private RatingBar reviewRatingBar;
    private LinearLayout review_inputLinearLayout;
    //UI VARIABLES
    private String TAG = "fragment_establishment";
    private ImageView establishmentBanner;
    private TabLayout establishmentOptions;
    private LayoutInflater layoutInflater;
    //FIREBASE VARIABLES
    private DatabaseReference reviewEstablishmentRef, reviewUserRef;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;

    public fragment_establishment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_establishment, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        establishmentBanner = (ImageView) view.findViewById(R.id.establishmentBanner);
        establishmentOptions = (TabLayout) view.findViewById(R.id.establishmentOptions);

        layoutInflater = getActivity().getLayoutInflater();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        reviewEstablishmentRef = database.getReference("reviewEstablishment");
        reviewUserRef = database.getReference("reviewUser");

        establishmentOptions.addTab(establishmentOptions.newTab().setIcon(R.mipmap.ic_business_black_24dp), true);
        establishmentOptions.addTab(establishmentOptions.newTab().setIcon(R.mipmap.ic_rate_review_black_24dp));
        establishmentOptions.addTab(establishmentOptions.newTab().setIcon(R.mipmap.ic_restaurant_black_24dp));
        establishmentOptions.addTab(establishmentOptions.newTab().setIcon(R.mipmap.ic_store_black_24dp));
        establishmentOptions.addTab(establishmentOptions.newTab().setIcon(R.mipmap.ic_loyalty_black_24dp));
        establishmentOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setEstablishmentTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        //SETTING FIRST FRAGMENT
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.establishmentFrame, new establishment_nav_profile())
                .commit();
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog();
            }
        });
        setQueries();
    }
    private void setQueries() {
        //REAL TIME LISTENER
        ValueEventListener establishmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Establishment establishment = dataSnapshot.getValue(Establishment.class);
                try {
                    if (establishment.getBanner_url() != null) {
                        Picasso.with(getActivity()).load(establishment.getBanner_url()).into(establishmentBanner);
                    }
                }catch (Exception e) {}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("establishment").child(Globals.establishmentID).addValueEventListener(establishmentListener);
    }
    private void setEstablishmentTab(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                //PROFILE
                fragmentManager.beginTransaction()
                        .replace(R.id.establishmentFrame, new establishment_nav_profile())
                        .commit();
                break;
            case 1:
                //REVIEWS
                fragmentManager.beginTransaction()
                        .replace(R.id.establishmentFrame, new establishment_nav_review())
                        .commit();
                break;
            case 2:
                //PRODUCTS
                fragmentManager.beginTransaction()
                        .replace(R.id.establishmentFrame, new establishment_nav_product())
                        .commit();
                break;
            case 3:
                //BRANCHES
                fragmentManager.beginTransaction()
                        .replace(R.id.establishmentFrame, new establishment_nav_branch())
                        .commit();
                break;
            case 4:
                //PROMOTIONS
                fragmentManager.beginTransaction()
                        .replace(R.id.establishmentFrame, new establishment_nav_promotion())
                        .commit();
                break;
            case 5:
                //RESERVAS
                //A FUTURO SE PROGRAMARA
                break;
        }
    }
    private void displayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= layoutInflater.inflate(R.layout.item_review_input, null);
        builder.setView(view);
        review_inputLinearLayout = (LinearLayout) view.findViewById(R.id.review_inputLinearLayout);
        review_inputLinearLayout.setVisibility(View.GONE);
        profileImage = (CircularImageView) view.findViewById(R.id.profileImage);
        reviewRatingBar = (RatingBar) view.findViewById(R.id.reviewRatingBar);
        reviewTitle = (EditText) view.findViewById(R.id.reviewTitle);
        reviewDescription = (EditText) view.findViewById(R.id.reviewDescription);
        final TextView reviewStatus = (TextView) view.findViewById(R.id.reviewStatus);
        reviewRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating >= 0 && rating<1){
                    reviewStatus.setText(R.string.rate0);
                }else{
                    if (rating >= 1 && rating<2){
                        reviewStatus.setText(R.string.rate1);
                    }else{
                        if (rating >= 2 && rating<3){
                            reviewStatus.setText(R.string.rate2);
                        }else{
                            if (rating >= 3 && rating<4){
                                reviewStatus.setText(R.string.rate3);
                            }else{
                                if (rating >= 4 && rating<5){
                                    reviewStatus.setText(R.string.rate4);
                                }else{
                                    if (rating >= 5 ){
                                        reviewStatus.setText(R.string.rate5);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        setUpDataFromUser();
        mDatabase.child("users").child(Globals.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImage_url() != null){
                    Picasso.with(getActivity()).load(user.getImage_url()).into(profileImage);
                }else{
                    Picasso.with(getActivity()).load(R.drawable.defaultprofile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                writeUserReview(reviewRatingBar.getRating(), reviewTitle.getText().toString(), reviewDescription.getText().toString());
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

    private void setUpDataFromUser() {
        mDatabase.child("reviewEstablishment").child(Globals.establishmentID).child(Globals.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                review_inputLinearLayout.setVisibility(View.VISIBLE);
                ReviewForm review = new ReviewForm();
                try{
                    review.setTitle(dataSnapshot.child("title").getValue(String.class));
                    reviewTitle.setText(review.getTitle());
                }catch (Exception e){}
                try{
                    review.setDescription(dataSnapshot.child("description").getValue(String.class));
                    reviewDescription.setText(review.getDescription());
                }catch (Exception e){}
                try {
                    review.setRating(dataSnapshot.child("rating").getValue(Double.class));
                    reviewRatingBar.setRating((float) review.getRating());
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writeUserReview(float rating, String title, String description) {
        double roundRating = (double) Math.round(rating * 100) / 100;
        ReviewForm reviewForm = new ReviewForm(Globals.userID,Globals.establishmentID, title, description, roundRating, ServerValue.TIMESTAMP, Globals.establishmentID);
        reviewEstablishmentRef.child(Globals.establishmentID).child(Globals.userID).setValue(reviewForm);
        reviewUserRef.child(Globals.userID).child(Globals.establishmentID).setValue(reviewForm, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                try {
                    Toast.makeText(getActivity(), R.string.Success, Toast.LENGTH_SHORT).show();
                }catch (Exception e){}
            }
        });
    }
}
