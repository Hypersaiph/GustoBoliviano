package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class fragment_restaurant extends Fragment implements OnMapReadyCallback {
    private TabLayout restaurantOptions;
    private LayoutInflater layoutInflater;
    private GoogleMap mMap;
    private Toolbar toolbar;
    public String restaurantName = "Mc Donalds";
    private DatabaseReference reviewRef;

    public fragment_restaurant(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        restaurantOptions = (TabLayout) view.findViewById(R.id.restaurantOptions);
        layoutInflater = getActivity().getLayoutInflater();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reviewRef = database.getReference("review");
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
                    collapsingToolbarLayout.setTitle(restaurantName);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        MapFragment mapFragment = (MapFragment)  this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog();
            }
        });
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_add_black_18dp), true);
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_add_black_18dp));
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_add_black_18dp));
        restaurantOptions.addTab(restaurantOptions.newTab().setIcon(R.mipmap.ic_add_black_18dp));
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
        LatLng LaPaz = new LatLng(-16.499, -68.118);
        mMap.addMarker(new MarkerOptions().position(LaPaz));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LaPaz.latitude, LaPaz.longitude), 16.0f));
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
        ReviewForm reviewForm = new ReviewForm(Globals.userID,Globals.restaurantID, title, description, roundRating, ServerValue.TIMESTAMP);
        reviewRef.child(Globals.userID).setValue(reviewForm, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(getActivity(), R.string.Success, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
