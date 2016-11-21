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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class establishment_nav_product extends Fragment {

    ArrayList<Product> productArrayList = new ArrayList<>();
    //UI VARIABLES
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //FIREBASE VARIABLES
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    public establishment_nav_product() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.establishment_nav_product_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAMOS TODOS LOS OBJETOS
        recyclerView = (RecyclerView) view.findViewById(R.id.restaurantProducts);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new productAdapter(productArrayList, getActivity());
        recyclerView.setAdapter(mAdapter);
        database = FirebaseDatabase.getInstance();
        getAllProductsFromRestaurant(Globals.establishmentID);
    }
    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
    }
    public class productAdapter extends RecyclerView.Adapter<productAdapter.ViewHolder> {
        private ArrayList<Product> productArrayList = new ArrayList<>();
        private final Picasso picasso;
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
        public productAdapter(ArrayList<Product> productArrayList, Context context) {
            this.productArrayList = productArrayList;
            this.picasso = Picasso.with(context);
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public productAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                picasso.load(R.mipmap.ic_attach_money_black_24dp).into(holder.productPriceIcon);
                picasso.load(productArrayList.get(position).getImage_url()).into(holder.productBanner);
            }catch (Exception e){
            }
            try {
                holder.productDescription.setText(productArrayList.get(position).getDescription());
            }catch (Exception e){
            }
            try {
                holder.productName.setText(productArrayList.get(position).getName());
            }catch (Exception e){
            }
            try {
                Log.e("PRICE",productArrayList.get(position).getPrice());
                holder.productPrice.setText("");
                for (int i=0; i< Integer.parseInt(productArrayList.get(position).getPrice()); i++){
                    holder.productPrice.append("$");
                }
            }catch (Exception e){
            }
            holder.productMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globals.productID = productArrayList.get(position).getId();
                    setProductFragment();
                }
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return productArrayList.size();
        }
    }
    private void getAllProductsFromRestaurant(String restaurantID) {
        mDatabase = database.getReference();
        Query productQuery = mDatabase.child("establishment").child(restaurantID).child("product").orderByChild("available").equalTo(true);
        productQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recyclerView.setVisibility(View.VISIBLE);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    product.setId(child.getKey());
                    if(!doesNotExist(product.getId())){
                        productArrayList.add(product);
                        updateRecyclerView();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean doesNotExist(String idBranch) {
        boolean exists = false;
        for (int i=0;i<productArrayList.size();i++){
            if(productArrayList.get(i).getId().equals(idBranch)){
                exists = true;
            }
        }
        return exists;
    }
    public void setProductFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new fragment_product())
                .commit();
    }
}
