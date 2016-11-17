package com.fuzzyapps.gustoboliviano;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class restaurant_nav_branch extends Fragment {

    ArrayList<Branch> branchArrayList = new ArrayList<>();
    //UI VARIABLES
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private ImageView imageNoData;
    //FIREBASE VARIABLES
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    public restaurant_nav_branch() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.restaurant_nav_branch_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAMOS TODOS LOS OBJETOS
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.restaurantBranches);
        imageNoData = (ImageView) view.findViewById(R.id.imageNoData);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        imageNoData.setVisibility(View.GONE);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new branchAdapter(branchArrayList, getActivity());
        recyclerView.setAdapter(mAdapter);


        database = FirebaseDatabase.getInstance();
        getAllBranchesFromRestaurant(Globals.restaurantID);
    }
    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
    }

    public class branchAdapter extends RecyclerView.Adapter<branchAdapter.ViewHolder> implements OnMapReadyCallback {
        private GoogleMap map;
        private ArrayList<Branch> branchArrayList = new ArrayList<>();
        private final Picasso picasso;
        private Context context;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            ImageView branchPositionIcon, branchPhoneIcon;
            TextView branchAddress, branchPhone;
            LinearLayout itemLayout;
            MapView mapView;
            ViewHolder(View view) {
                super(view);
                branchPositionIcon = (ImageView) view.findViewById(R.id.branchPositionIcon);
                branchPhoneIcon = (ImageView) view.findViewById(R.id.branchPhoneIcon);
                branchAddress = (TextView) view.findViewById(R.id.branchAddress);
                branchPhone = (TextView) view.findViewById(R.id.branchPhone);
                itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
                mapView = (MapView) view.findViewById(R.id.mapView);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public branchAdapter(ArrayList<Branch> branchArrayList, Context context) {
            this.branchArrayList = branchArrayList;
            this.picasso = Picasso.with(context);
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public branchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch2, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try {
                picasso.load(R.mipmap.ic_call_black_24dp).into(holder.branchPhoneIcon);
                picasso.load(R.mipmap.ic_place_black_24dp).into(holder.branchPositionIcon);
                holder.branchAddress.setText(branchArrayList.get(position).getAddress());
                holder.branchPhone.setText(branchArrayList.get(position).getPhone());

            }catch (Exception e){
            }
            try{
               holder.mapView.getMapAsync(this);
            }catch (Exception e){}

        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return branchArrayList.size();
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
        }
    }
    private void getAllBranchesFromRestaurant(String restaurantID) {
        mDatabase = database.getReference();
        Query branchQuery = mDatabase.child("establishment").child(restaurantID).child("branch").orderByChild("main").equalTo(false);
        branchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Branch branch = child.getValue(Branch.class);
                    branchArrayList.add(branch);
                    updateRecyclerView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
