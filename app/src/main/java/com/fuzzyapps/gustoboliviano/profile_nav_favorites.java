package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class profile_nav_favorites extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    //UI VARIABLES
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Favorites> favoritesArrayList = new ArrayList<>();
    public profile_nav_favorites() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_nav_favorites_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        //AQUI INICIALIZAMOS TODOS LOS OBJETOS
        recyclerView = (RecyclerView) view.findViewById(R.id.favoritesRecyclerView);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new favoritesAdapter(favoritesArrayList, getActivity(), database);
        recyclerView.setAdapter(mAdapter);
        setQueries();
    }
    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
    }
    private void setQueries() {
        //QUERY TO SEE IF ITS IN MY FAVORITES
        mDatabase.child("favorites").child(Globals.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Favorites favorites = new Favorites();
                        favorites.setProductID(child.child("productID").getValue(String.class));
                        favorites.setEstablishmentID(child.child("establishmentID").getValue(String.class));
                        favorites.setActive(child.child("active").getValue(boolean.class));
                        if(favorites.isActive()){
                            favoritesArrayList.add(favorites);
                            updateRecyclerView();
                        }
                    }
                }catch (Exception e){
                    Log.e("error", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setProductFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new fragment_product())
                .addToBackStack("")
                .commit();
    }
    public class favoritesAdapter extends RecyclerView.Adapter<favoritesAdapter.ViewHolder> {
        private ArrayList<Favorites> favoritesArrayList = new ArrayList<>();
        private final Picasso picasso;
        private FirebaseDatabase database;
        private Context context;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            ImageView productBanner, productPriceIcon;
            TextView productDescription, productPrice, productName;
            LinearLayout itemLayout;
            Button productMore;
            ViewHolder(View view) {
                super(view);
                productBanner = (ImageView) view.findViewById(R.id.productBanner);
                productPriceIcon = (ImageView) view.findViewById(R.id.productPriceIcon);
                productDescription = (TextView) view.findViewById(R.id.productDescription);
                productName = (TextView) view.findViewById(R.id.productName);
                productPrice = (TextView) view.findViewById(R.id.productPrice);
                itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
                productMore = (Button) view.findViewById(R.id.productMore);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public favoritesAdapter(ArrayList<Favorites> favoritesArrayList, Context context, FirebaseDatabase database) {
            this.favoritesArrayList = favoritesArrayList;
            this.picasso = Picasso.with(context);
            this.context = context;
            this.database = database;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public favoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            getData(favoritesArrayList.get(position).getEstablishmentID(), favoritesArrayList.get(position).getProductID(), holder);
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return favoritesArrayList.size();
        }
        private void getData(final String establishmentID, final String productID, final ViewHolder holder) {
            DatabaseReference mDatabase = database.getReference();
            mDatabase.child("establishment").child(establishmentID).child("product").child(productID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Product product = dataSnapshot.getValue(Product.class);
                    try {
                        picasso.load(R.mipmap.ic_attach_money_black_24dp).into(holder.productPriceIcon);
                        picasso.load(product.getImage_url()).into(holder.productBanner);
                    }catch (Exception e){
                    }
                    try {
                        holder.productDescription.setText(product.getDescription());
                    }catch (Exception e){
                    }
                    try {
                        holder.productName.setText(product.getName());
                    }catch (Exception e){
                    }
                    /*try {
                        holder.productPrice.setText("");
                        for (int i=0; i< Integer.parseInt(product.getPrice()); i++){
                            holder.productPrice.append("$");
                        }
                    }catch (Exception e){
                    }*/
                    try{
                        if(product.isPriceVisible()){
                            holder.productPrice.setText(product.getPrice());
                        }else{
                            holder.productPrice.setText(getResources().getString(R.string.notspecified));
                        }
                    }catch (Exception e){

                    }
                    holder.productMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Globals.productID = productID;
                            Globals.establishmentID = establishmentID;
                            setProductFragment();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
