package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class fragment_restaurant extends Fragment implements OnMapReadyCallback {
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
        View view= layoutInflater.inflate(R.layout.item_review, null);
        builder.setView(view);
        final RatingBar reviewRatingBar = (RatingBar) view.findViewById(R.id.reviewRatingBar);
        final EditText reviewTitle = (EditText) view.findViewById(R.id.reviewTitle);
        final EditText reviewDescription = (EditText) view.findViewById(R.id.reviewDescription);
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                writeUserReview(reviewRatingBar.getRating(), reviewTitle.getText().toString(), reviewDescription.getText().toString());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void writeUserReview(float rating, String title, String description) {
        double roundRating = (double) Math.round(rating * 100) / 100;
        Review review = new Review(Globals.userID,Globals.restaurantID, title, description, roundRating, ServerValue.TIMESTAMP);
        reviewRef.child(Globals.userID).setValue(review, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(getActivity(),"Registrado.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
