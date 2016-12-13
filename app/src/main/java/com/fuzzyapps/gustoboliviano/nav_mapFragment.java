package com.fuzzyapps.gustoboliviano;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class nav_mapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Permission variables
    private int MY_LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    //UI Variables
    private GoogleMap mMap;
    private View view;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private Toolbar toolbar;
    private ArrayList<String> idSearched = new ArrayList<>();
    private int results = 0;
    private Establishment establishment;
    private LayoutInflater layoutInflater;
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (!query.equals("")) {
                //Toast.makeText(getActivity(), "s:" + query, Toast.LENGTH_LONG).show();
                searchFor(query);
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!newText.equals("")) {
                //Toast.makeText(getActivity(), ":"+newText, Toast.LENGTH_LONG).show();
            }
            return false;
        }
    };
    //Database Variables
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    public nav_mapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.nav_map_fragment, container, false);
        } catch (InflateException e) {
            //Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return view;
        /*try {
            view = inflater.inflate(R.layout.nav_map_fragment, container, false);
        } catch (InflateException e) {
            //Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return view;*/
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        layoutInflater = getActivity().getLayoutInflater();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        mMap.setInfoWindowAdapter(new infoAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String splt[]= marker.getSnippet().split("%%");
                Globals.establishmentID = splt[0];
                setRestaurantFragment();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(listener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission was denied. Display an error message.

            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void searchFor(String query) {
        try {
            idSearched.clear();
            results = 0;
            mMap.clear();
            String[] splitedWords = query.split("\\s+");
            for (int i = 0; i < splitedWords.length; i++) {
                if (i == (splitedWords.length - 1)) {
                    addQueryToWordMod(splitedWords[i]);
                } else {
                    addQueryToWord(splitedWords[i]);
                }
            }
        }catch (Exception e){
            Log.e("searchFor", e.getMessage());
        }
    }
    private void addQueryToWordMod(String word){
        try {
            Query wordQuery = mDatabase.child("tags").child(word).orderByValue().equalTo(true);
            wordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        results++;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            addQueryForProduct(child.getKey());
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            wordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("results", ""+results);
                    if(results == 0){
                        Toast.makeText(getActivity(), R.string.noresult, Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }catch (Exception e){
            Log.e("addQueryToWord", e.getMessage());
        }
    }
    private void addQueryToWord(final String word) {
        try {
            Query wordQuery = mDatabase.child("tags").child(word).orderByValue().equalTo(true);
            wordQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("init", word);
                    if(dataSnapshot.getValue() != null){
                        results++;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            addQueryForProduct(child.getKey());
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }catch (Exception e){
            Log.e("addQueryToWord", e.getMessage());
        }
    }
    private void addQueryForProduct(String productID) {
        try {
            mDatabase.child("product").child(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        String establishmentID = dataSnapshot.child("establishmentID").getValue(String.class);
                        if (!isAlreadyInArray(establishmentID)) {
                            idSearched.add(establishmentID);
                            addQueryForBranch(establishmentID);
                        }
                        //Toast.makeText(getActivity(), establishmentID, Toast.LENGTH_LONG).show();
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Log.e("addQueryForProduct", e.getMessage());
        }
    }
    private void addQueryForBranch(final String establishmentID) {
        final double[] rating = new double[1];
        final String[] ratingNumber = {""};
        mDatabase.child("establishment").child(establishmentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                establishment = dataSnapshot.getValue(Establishment.class);
                Log.e("gotEstablishment", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("reviewEstablishment").child(establishmentID).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rating[0] = 0;
                ratingNumber[0] = "("+dataSnapshot.getChildrenCount()+")";
                Log.e("gotrating", dataSnapshot.getValue().toString());
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ReviewForm review = new ReviewForm();
                        review.setRating(child.child("rating").getValue(Double.class));
                        rating[0] += review.getRating();
                    }
                    rating[0] = (float) (rating[0]/dataSnapshot.getChildrenCount());
                    mDatabase.child("establishment").child(establishmentID).child("branch").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Log.e("child", child.getValue().toString());
                                    Branch branch = child.getValue(Branch.class);
                                    LatLng position = new LatLng(Double.parseDouble(branch.getLatitude()), Double.parseDouble(branch.getLongitude()));
                                    Marker marker;
                                    if(branch.isMain()){
                                        //Main Branch
                                        mMap.addMarker(new MarkerOptions()
                                                .position(position)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                                .snippet(establishmentID+"%%"+establishment.getDescription()+"%%"+"("+getResources().getString(R.string.branchP)+")"+"%%"+rating[0]+"%%"+ratingNumber[0]+"%%"+establishment.getImage_url())
                                                .title(establishment.getName()));
                                    }else{
                                        //Simple Branch
                                        mMap.addMarker(new MarkerOptions()
                                                .position(position)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                                .snippet(establishmentID+"%%"+establishment.getDescription()+"%%"+"("+getResources().getString(R.string.branch)+")"+"%%"+rating[0]+"%%"+ratingNumber[0]+"%%"+establishment.getImage_url())
                                                .title(establishment.getName()));
                                    }
                                }
                            }catch (Exception e){
                                Log.e("addQueryForBranch", e.getMessage());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){}
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean isAlreadyInArray(String establishmentID){
        boolean isInTheArray = false;
        for(int i=0 ;i<idSearched.size(); i++){
            if(idSearched.get(i).equals(establishmentID)){
                isInTheArray = true;
                break;
            }
        }
        Log.e("isAlreadyInArray", establishmentID+":"+isInTheArray);
        return isInTheArray;
    }
    public class infoAdapter implements GoogleMap.InfoWindowAdapter{

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = layoutInflater.inflate(R.layout.item_info_window_establishment, null);
            ImageView iconEstablishment = (ImageView) v.findViewById(R.id.iconEstablishment);
            TextView establishmentName = (TextView) v.findViewById(R.id.establishmentName);
            TextView establishmentEspecification = (TextView) v.findViewById(R.id.establishmentEspecification);
            TextView establishmentDescription = (TextView) v.findViewById(R.id.establishmentDescription);
            TextView establishmentRating = (TextView) v.findViewById(R.id.establishmentRating);
            TextView establishmentRatingNumber = (TextView) v.findViewById(R.id.establishmentRatingNumber);
            final TextView establishmentID = (TextView) v.findViewById(R.id.establishmentID);
            //Button establishmentButton = (Button) v.findViewById(R.id.establishmentButton);
            RatingBar establishmentRatingBar = (RatingBar) v.findViewById(R.id.establishmentRatingBar);
            String splt[]= marker.getSnippet().split("%%");
            establishmentName.setText(marker.getTitle());
            establishmentEspecification.setText(splt[2]);
            establishmentDescription.setText(splt[1]);
            establishmentID.setText(splt[0]);
            establishmentRating.setText(String.format("%.1f", Float.parseFloat(splt[3])));
            establishmentRatingNumber.setText(splt[4]);
            establishmentRatingBar.setRating(Float.parseFloat(splt[3]));
            Picasso.with(getActivity()).load(splt[5]).into(iconEstablishment);
            return v;
        }
    }
    public void setRestaurantFragment(){
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new fragment_establishment())
                .addToBackStack("")
                .commit();
    }
}
