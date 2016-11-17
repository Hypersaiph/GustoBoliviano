package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

public class fragment_restaurant extends Fragment implements OnMapReadyCallback {
    //UI VARIABLES
    private LinearLayout restaurantLinearLayout, itemLayout, restaurantWebPageLayout;
    private ProgressBar restaurantProgressBar;
    private ImageView restaurantBanner, branchPositionIcon, branchPhoneIcon, restaurantIconClock, restaurantValidated;
    private RoundedImageView restaurantIcon;
    private FancyButton followButton;
    private TextView restaurantName, restaurantRating, restaurantRatingNumber, followersCount, followingCount,
                    restaurantDescription, restaurantDay, restaurantHour, restaurantStatus, restaurantWebPage,
                    branchAddress, branchPhone;
    private RatingBar restaurantRatingBar;
    private TabLayout restaurantOptions;
    private LayoutInflater layoutInflater;
    private GoogleMap mMap;
    //FIREBASE VARIABLES
    private DatabaseReference reviewEstablishmentRef, reviewUserRef;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;

    public fragment_restaurant(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
        restaurantWebPageLayout = (LinearLayout) view.findViewById(R.id.restaurantWebPageLayout);
        restaurantLinearLayout = (LinearLayout) view.findViewById(R.id.restaurantLinearLayout);
        restaurantProgressBar = (ProgressBar) view.findViewById(R.id.restaurantProgressBar);
        restaurantBanner = (ImageView) view.findViewById(R.id.restaurantBanner);
        branchPositionIcon = (ImageView) view.findViewById(R.id.branchPositionIcon);
        branchPhoneIcon = (ImageView) view.findViewById(R.id.branchPhoneIcon);
        restaurantIconClock = (ImageView) view.findViewById(R.id.restaurantIconClock);
        restaurantValidated = (ImageView) view.findViewById(R.id.restaurantValidated);
        restaurantIcon = (RoundedImageView) view.findViewById(R.id.restaurantIcon);
        followButton = (FancyButton) view.findViewById(R.id.followButton);
        restaurantRatingBar = (RatingBar) view.findViewById(R.id.restaurantRatingBar);

        restaurantName = (TextView) view.findViewById(R.id.restaurantName);
        restaurantRating = (TextView) view.findViewById(R.id.restaurantRating);
        restaurantRatingNumber = (TextView) view.findViewById(R.id.restaurantRatingNumber);
        followersCount = (TextView) view.findViewById(R.id.followersCount);
        followingCount = (TextView) view.findViewById(R.id.followingCount);

        restaurantDescription = (TextView) view.findViewById(R.id.restaurantDescription);
        restaurantDay = (TextView) view.findViewById(R.id.restaurantDay);
        restaurantHour = (TextView) view.findViewById(R.id.restaurantHour);
        restaurantStatus = (TextView) view.findViewById(R.id.restaurantStatus);
        restaurantWebPage = (TextView) view.findViewById(R.id.restaurantWebPage);

        branchAddress = (TextView) view.findViewById(R.id.branchAddress);
        branchPhone = (TextView) view.findViewById(R.id.branchPhone);

        restaurantOptions = (TabLayout) view.findViewById(R.id.restaurantOptions);

        restaurantWebPageLayout.setVisibility(View.GONE);
        restaurantLinearLayout.setVisibility(View.GONE);
        itemLayout.setVisibility(View.GONE);
        restaurantProgressBar.setVisibility(View.VISIBLE);

        Picasso.with(getActivity()).load(R.mipmap.ic_call_black_24dp).into(branchPhoneIcon);
        Picasso.with(getActivity()).load(R.mipmap.ic_place_black_24dp).into(branchPositionIcon);
        Picasso.with(getActivity()).load(R.mipmap.ic_access_time_black_24dp).into(restaurantIconClock);

        layoutInflater = getActivity().getLayoutInflater();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        reviewEstablishmentRef = database.getReference("reviewEstablishment");
        reviewUserRef = database.getReference("reviewUser");
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(restaurantName.getText().toString());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        //GOOGLE MAP
        MapFragment mapFragment = (MapFragment)  this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog();
            }
        });
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_rate_review_black_24dp), true);
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_restaurant_menu_black_24dp));
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_store_black_24dp));
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_loyalty_black_24dp));
        restaurantOptions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setRestaurantTab(tab.getPosition());
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
                .replace(R.id.restaurantFrame, new restaurant_nav_review())
                .commit();
        //REAL TIME LISTENER
        ValueEventListener establishmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurantLinearLayout.setVisibility(View.VISIBLE);
                restaurantProgressBar.setVisibility(View.GONE);
                Establishment establishment = dataSnapshot.getValue(Establishment.class);
                try {
                    if (establishment.getImage_url() != null) {
                        Picasso.with(getActivity()).load(establishment.getImage_url()).into(restaurantIcon);
                    }
                }catch (Exception e) {
                }
                try {
                    if (establishment.getBanner_url() != null) {
                        Picasso.with(getActivity()).load(establishment.getBanner_url()).into(restaurantBanner);
                    }
                }catch (Exception e) {
                }
                try {
                    restaurantName.setText(establishment.getName());
                }catch (Exception e) {
                }
                try {
                    restaurantDescription.setText(establishment.getDescription());
                }catch (Exception e) {
                }
                try {
                    restaurantDay.setText(establishment.getOpenFromDay()+" - "+establishment.getOpenToDay());
                }catch (Exception e) {}
                try {
                    restaurantHour.setText(establishment.getOpenTime() + " - " + establishment.getCloseTime());
                }catch (Exception e) {
                }
                try {
                    if (isBetween(establishment.getOpenTime(), establishment.getCloseTime())) {
                        restaurantStatus.setTextColor(Color.parseColor("#04B431"));
                        restaurantStatus.setText(R.string.open);
                    }else{
                        restaurantStatus.setTextColor(Color.parseColor("#B40404"));
                        restaurantStatus.setText(R.string.closed);
                    }
                }catch (Exception e) {
                    Log.e("TMIE", "" + e.getMessage());
                }
                try {
                    if(establishment.getWebPage() != null && !establishment.getWebPage().equals("")){
                        restaurantWebPage.setText(establishment.getWebPage());
                        restaurantWebPageLayout.setVisibility(View.VISIBLE);
                    }else{
                        restaurantWebPageLayout.setVisibility(View.GONE);
                    }
                }catch (Exception e) {
                    restaurantWebPageLayout.setVisibility(View.GONE);
                }
                try {
                    if(establishment.getPhone() != null){
                        branchPhone.setText(establishment.getPhone());
                    }
                }catch (Exception e) {}
                try {
                    if(establishment.getAddress() != null){
                        branchAddress.setText(establishment.getAddress());
                    }
                }catch (Exception e) {}
                /*try {
                    restaurantRatingBar.setRating((float) establishment.getRating());
                }catch (Exception e) {}*/
                /*try {
                    restaurantRating.setText(establishment.getRating()+"");
                }catch (Exception e) {}*/
                try {
                    //QUERY TO KNOW HOW MANY HAVE RATED
                }catch (Exception e) {}
                try {
                    followersCount.setText(establishment.getFollowers());
                }catch (Exception e) {
                    followersCount.setText(R.string.zero);
                }
                try {
                    followingCount.setText(establishment.getFollowing());
                }catch (Exception e) {
                    followingCount.setText(R.string.zero);
                }
                try {
                    if(establishment.isValidated()){
                        restaurantValidated.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(R.drawable.validated50pc).into(restaurantValidated);
                    }else{
                        restaurantValidated.setVisibility(View.GONE);
                    }
                }catch (Exception e) {
                    restaurantValidated.setVisibility(View.GONE);
                }
                /*;
                restaurantRatingNumber;
                followersCount;
                followingCount;*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("nav_profileFragment", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("establishment").child(Globals.restaurantID).addValueEventListener(establishmentListener);
        //QUERY MAIN BRANCH
        Query mainBranchQuery = mDatabase.child("establishment").child(Globals.restaurantID).child("branch").orderByChild("main").equalTo(true);
        mainBranchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Branch branch = child.getValue(Branch.class);
                    itemLayout.setVisibility(View.VISIBLE);
                    try {
                        setBranchPosition(branch.getLatitude(), branch.getLongitude());
                    }catch (Exception e){
                        Log.e("GoogleMaps",""+e.getMessage());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                itemLayout.setVisibility(View.GONE);
            }
        });
        setQueries();
    }
    private void setQueries() {
        Query reviewsQuery = mDatabase.child("reviewEstablishment").child(Globals.restaurantID).orderByChild("timestamp");
        reviewsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("setQueries key",""+dataSnapshot.getKey());
                Log.e("retrieve",""+dataSnapshot.getValue());
                restaurantRatingNumber.setText("("+dataSnapshot.getChildrenCount()+")");
                double rating = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ReviewForm review = new ReviewForm();
                    review.setId(child.getKey());
                    review.setRestaurantID(child.child("restaurantID").getValue(String.class));
                    review.setUserID(child.child("userID").getValue(String.class));
                    review.setTitle(child.child("title").getValue(String.class));
                    review.setDescription(child.child("description").getValue(String.class));
                    review.setRating(child.child("rating").getValue(Double.class));
                    review.setPostedOn(child.child("timestamp").getValue(long.class));
                    rating += review.getRating();
                }
                restaurantRatingBar.setRating((float) (rating/dataSnapshot.getChildrenCount()));
                restaurantRating.setText(String.format("%.1f", (float) (rating/dataSnapshot.getChildrenCount())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setBranchPosition(String latitude, String longitude) {
        try {
            LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            mMap.addMarker(new MarkerOptions().position(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.latitude, position.longitude), 16.0f));
            itemLayout.setVisibility(View.VISIBLE);
        }catch (Exception e){
            itemLayout.setVisibility(View.GONE);
        }
    }

    public boolean isBetween(String from, String to) {
        String[] splt1 = from.split(":");
        String[] splt2 = to.split(":");
        int fromTime = Integer.parseInt(splt1[0])*100 + Integer.parseInt(splt1[1]);
        int toTime = Integer.parseInt(splt2[0])*100 + Integer.parseInt(splt2[1]);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int actual = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);
        boolean isBetween = toTime > fromTime && actual >= fromTime && actual <= toTime || toTime < fromTime && (actual >= fromTime || actual <= toTime);
        return isBetween;
    }
    private void setRestaurantTab(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position){
            case 0:
                //RESEÑAS
                fragmentManager.beginTransaction()
                        .replace(R.id.restaurantFrame, new restaurant_nav_review())
                        .commit();
                break;
            case 1:
                //PRODUCTOS
                fragmentManager.beginTransaction()
                        .replace(R.id.restaurantFrame, new restaurant_nav_product())
                        .commit();
                break;
            case 2:
                //SUCURSALES
                fragmentManager.beginTransaction()
                        .replace(R.id.restaurantFrame, new restaurant_nav_branch())
                        .commit();
                break;
            case 3:
                //PROMOCIONES
                fragmentManager.beginTransaction()
                        .replace(R.id.restaurantFrame, new restaurant_nav_promotion())
                        .commit();
                break;
            case 4:
                //RESERVAS
                //A FUTURO SE PROGRAMARA
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        /*LatLng LaPaz = new LatLng(-16.499, -68.118);
        mMap.addMarker(new MarkerOptions().position(LaPaz));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LaPaz.latitude, LaPaz.longitude), 16.0f));*/
    }
    private void displayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= layoutInflater.inflate(R.layout.item_review_input, null);
        builder.setView(view);
        final RatingBar reviewRatingBar = (RatingBar) view.findViewById(R.id.reviewRatingBar);
        final EditText reviewTitle = (EditText) view.findViewById(R.id.reviewTitle);
        final EditText reviewDescription = (EditText) view.findViewById(R.id.reviewDescription);
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
    private void writeUserReview(float rating, String title, String description) {
        double roundRating = (double) Math.round(rating * 100) / 100;
        ReviewForm reviewForm = new ReviewForm(Globals.userID,Globals.restaurantID, title, description, roundRating, ServerValue.TIMESTAMP );
        reviewEstablishmentRef.child(Globals.restaurantID).child(Globals.userID).setValue(reviewForm);
        reviewUserRef.child(Globals.userID).child(Globals.restaurantID).setValue(reviewForm, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(getActivity(), R.string.Success, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
