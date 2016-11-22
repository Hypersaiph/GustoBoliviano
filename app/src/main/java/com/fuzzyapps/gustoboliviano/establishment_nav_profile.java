package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class establishment_nav_profile extends Fragment implements OnMapReadyCallback {
    //UI VARIABLES
    private LinearLayout establishmentLinearLayout, itemLayout, establishmentWebPageLayout;
    private ProgressBar establishmentProgressBar;
    private ImageView branchPositionIcon, branchPhoneIcon, establishmentIconClock, establishmentValidated;
    private RoundedImageView establishmentIcon;
    private FancyButton followButton;
    private TextView establishmentName, establishmentRating, establishmentRatingNumber, followersCount, followingCount,
            establishmentDescription, establishmentDay, establishmentHour, establishmentStatus, establishmentWebPage,
            branchAddress, branchPhone;
    private RatingBar establishmentRatingBar;
    private LayoutInflater layoutInflater;
    private GoogleMap mMap;
    private boolean amIFollowing;
    //FIREBASE VARIABLES
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private Establishment establishment;
    public establishment_nav_profile() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.establishment_nav_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAMOS TODOS LOS OBJETOS
        itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
        establishmentLinearLayout = (LinearLayout) view.findViewById(R.id.establishmentLinearLayout);
        establishmentWebPageLayout = (LinearLayout) view.findViewById(R.id.establishmentWebPageLayout);
        establishmentProgressBar = (ProgressBar) view.findViewById(R.id.establishmentProgressBar);
        branchPositionIcon = (ImageView) view.findViewById(R.id.branchPositionIcon);
        branchPhoneIcon = (ImageView) view.findViewById(R.id.branchPhoneIcon);
        establishmentIconClock = (ImageView) view.findViewById(R.id.establishmentIconClock);
        establishmentValidated = (ImageView) view.findViewById(R.id.establishmentValidated);
        establishmentIcon = (RoundedImageView) view.findViewById(R.id.establishmentIcon);
        followButton = (FancyButton) view.findViewById(R.id.followButton);
        establishmentRatingBar = (RatingBar) view.findViewById(R.id.establishmentRatingBar);

        establishmentName = (TextView) view.findViewById(R.id.establishmentName);
        establishmentRating = (TextView) view.findViewById(R.id.establishmentRating);
        establishmentRatingNumber = (TextView) view.findViewById(R.id.establishmentRatingNumber);
        followersCount = (TextView) view.findViewById(R.id.followersCount);
        followingCount = (TextView) view.findViewById(R.id.followingCount);

        establishmentDescription = (TextView) view.findViewById(R.id.establishmentDescription);
        establishmentDay = (TextView) view.findViewById(R.id.establishmentDay);
        establishmentHour = (TextView) view.findViewById(R.id.establishmentHour);
        establishmentStatus = (TextView) view.findViewById(R.id.establishmentStatus);
        establishmentWebPage = (TextView) view.findViewById(R.id.establishmentWebPage);

        branchAddress = (TextView) view.findViewById(R.id.branchAddress);
        branchPhone = (TextView) view.findViewById(R.id.branchPhone);

        followButton.setVisibility(View.GONE);
        establishmentWebPageLayout.setVisibility(View.GONE);
        establishmentLinearLayout.setVisibility(View.GONE);
        itemLayout.setVisibility(View.GONE);
        establishmentProgressBar.setVisibility(View.VISIBLE);

        Picasso.with(getActivity()).load(R.mipmap.ic_call_black_24dp).into(branchPhoneIcon);
        Picasso.with(getActivity()).load(R.mipmap.ic_place_black_24dp).into(branchPositionIcon);
        Picasso.with(getActivity()).load(R.mipmap.ic_access_time_black_24dp).into(establishmentIconClock);
        layoutInflater = getActivity().getLayoutInflater();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        MapFragment mapFragment = (MapFragment)  this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amIFollowing){
                    displayUnFollowDialog();
                }else{
                    followingEstablishment();
                }
            }
        });
        setQueries();
    }
    private void setQueries() {
        //REAL TIME LISTENER
        ValueEventListener establishmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishmentLinearLayout.setVisibility(View.VISIBLE);
                establishmentProgressBar.setVisibility(View.GONE);
                establishment = dataSnapshot.getValue(Establishment.class);
                try {
                    if (establishment.getImage_url() != null) {
                        Picasso.with(getActivity()).load(establishment.getImage_url()).into(establishmentIcon);
                    }
                }catch (Exception e) {
                }
                try {
                    establishmentName.setText(establishment.getName());
                }catch (Exception e) {
                }
                try {
                    establishmentDescription.setText(establishment.getDescription());
                }catch (Exception e) {
                }
                try {
                    establishmentDay.setText(establishment.getOpenFromDay()+" - "+establishment.getOpenToDay());
                }catch (Exception e) {}
                try {
                    establishmentHour.setText(establishment.getOpenTime() + " - " + establishment.getCloseTime());
                }catch (Exception e) {
                }
                try {
                    if (isBetween(establishment.getOpenTime(), establishment.getCloseTime())) {
                        establishmentStatus.setTextColor(Color.parseColor("#04B431"));
                        establishmentStatus.setText(R.string.open);
                    }else{
                        establishmentStatus.setTextColor(Color.parseColor("#B40404"));
                        establishmentStatus.setText(R.string.closed);
                    }
                }catch (Exception e) {
                    Log.e("TMIE", "" + e.getMessage());
                }
                try {
                    if(establishment.getWebPage() != null && !establishment.getWebPage().equals("")){
                        establishmentWebPage.setText(establishment.getWebPage());
                        establishmentWebPageLayout.setVisibility(View.VISIBLE);
                    }else{
                        establishmentWebPageLayout.setVisibility(View.GONE);
                    }
                }catch (Exception e) {
                    establishmentWebPageLayout.setVisibility(View.GONE);
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
                try {
                    if(establishment.isValidated()){
                        establishmentValidated.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(R.drawable.validated50pc).into(establishmentValidated);
                    }else{
                        establishmentValidated.setVisibility(View.GONE);
                    }
                }catch (Exception e) {
                    establishmentValidated.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("nav_profileFragment", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("establishment").child(Globals.establishmentID).addValueEventListener(establishmentListener);
        //QUERY TO SEE WHO FOLLOWS ME
        Query followingQuery = mDatabase.child("following").child(Globals.establishmentID).orderByValue().equalTo(true);
        followingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    followersCount.setText(dataSnapshot.getChildrenCount()+"");
                }else{
                    followersCount.setText(R.string.zero);
                }
                Log.e("following",""+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY TO SEE WHO DO I FOLLOW AS ESTABLISHMENT
        final Query followersQuery = mDatabase.child("followers").child(Globals.establishmentID).orderByValue().equalTo(true);
        followersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    followingCount.setText(dataSnapshot.getChildrenCount()+"");
                }else{
                    followingCount.setText(R.string.zero);
                }
                Log.e("followers",""+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY TO SEE IF I FOLLOW
        Query doIFollow = mDatabase.child("following").child(Globals.establishmentID).orderByKey().equalTo(Globals.userID);
        doIFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followButton.setVisibility(View.VISIBLE);
                if(dataSnapshot.getValue() != null){
                    amIFollowing = dataSnapshot.child(Globals.userID).getValue(boolean.class);
                    if(amIFollowing){
                        followButton.setGhost(false);
                        followButton.setBackgroundColor(Color.parseColor("#55acee"));
                        followButton.setFocusBackgroundColor(Color.parseColor("#8cc9f8"));
                        followButton.setBorderColor(Color.parseColor("#FFFFFF"));
                        followButton.setTextColor(Color.parseColor("#FFFFFF"));
                        try {
                            followButton.setText(getString(R.string.following));
                        }catch (Exception e){}
                    }else{
                        //NOT FOLLOWING
                        followButton.setGhost(true);
                        followButton.setFocusBackgroundColor(Color.parseColor("#949494"));
                        followButton.setBorderColor(Color.parseColor("#949494"));
                        followButton.setTextColor(Color.parseColor("#949494"));
                        try {
                            followButton.setText(getString(R.string.follow));
                        }catch (Exception e){}
                    }
                }else{
                    //NOT FOLLOWING
                    followButton.setGhost(true);
                    followButton.setFocusBackgroundColor(Color.parseColor("#949494"));
                    followButton.setBorderColor(Color.parseColor("#949494"));
                    followButton.setTextColor(Color.parseColor("#949494"));
                    try {
                        followButton.setText(getString(R.string.follow));
                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY FOR MAIN BRANCH
        Query mainBranchQuery = mDatabase.child("establishment").child(Globals.establishmentID).child("branch").orderByChild("main").equalTo(true);
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
        //QUERY FOR REVIEWS
        Query reviewsQuery = mDatabase.child("reviewEstablishment").child(Globals.establishmentID).orderByChild("timestamp");
        reviewsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("setQueries key",""+dataSnapshot.getKey());
                Log.e("retrieve",""+dataSnapshot.getValue());
                establishmentRatingNumber.setText("("+dataSnapshot.getChildrenCount()+")");
                double rating = 0;
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ReviewForm review = new ReviewForm();
                        review.setRating(child.child("rating").getValue(Double.class));
                        rating += review.getRating();
                    }
                }catch (Exception e){}
                if(rating != 0){
                    establishmentRatingBar.setRating((float) (rating/dataSnapshot.getChildrenCount()));
                    establishmentRating.setText(String.format("%.1f", (float) (rating/dataSnapshot.getChildrenCount())));
                }else{
                    establishmentRating.setText(R.string.zero);
                }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        /*LatLng LaPaz = new LatLng(-16.499, -68.118);
        mMap.addMarker(new MarkerOptions().position(LaPaz));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LaPaz.latitude, LaPaz.longitude), 16.0f));*/
    }
    private void displayUnFollowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= layoutInflater.inflate(R.layout.item_unfollow, null);
        builder.setView(view);
        final CircularImageView image = (CircularImageView) view.findViewById(R.id.image);
        final TextView unFollowTitle = (TextView) view.findViewById(R.id.unFollowTitle);
        Picasso.with(getActivity()).load(establishment.getImage_url()).into(image);
        unFollowTitle.setText(establishment.getName());
        builder.setPositiveButton(R.string.unfollow, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                unFollowEstablishment();
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

    private void unFollowEstablishment() {
        Map<String, Object> userChild = new HashMap<>();
        userChild.put(Globals.userID, false);
        mDatabase.child("following").child(Globals.establishmentID).updateChildren(userChild);
        Map<String, Object> establishmentChild = new HashMap<>();
        establishmentChild.put(Globals.establishmentID, false);
        mDatabase.child("followers").child(Globals.userID).updateChildren(establishmentChild);
    }
    private void followingEstablishment() {
        Map<String, Object> userChild = new HashMap<>();
        userChild.put(Globals.userID, true);
        mDatabase.child("following").child(Globals.establishmentID).updateChildren(userChild);
        Map<String, Object> establishmentChild = new HashMap<>();
        establishmentChild.put(Globals.establishmentID, true);
        mDatabase.child("followers").child(Globals.userID).updateChildren(establishmentChild);
    }
}



/*final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout);
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
                    collapsingToolbarLayout.setTitle(establishmentName.getText().toString());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });*/