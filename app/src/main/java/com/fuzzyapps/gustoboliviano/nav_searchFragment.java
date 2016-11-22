package com.fuzzyapps.gustoboliviano;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class nav_searchFragment extends Fragment{
    private String TAG = "nav_searchFragment";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //FIREBASE VARIABLES
    private FirebaseDatabase database;
    // UI VARIABLES
    private SearchView searchView;
    private MenuItem searchMenuItem;
    //private ListView restaurantList;
    ArrayList<Establishment> RestaurantArrayList = new ArrayList<>();
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")){
                Toast.makeText(getActivity(), "s:"+query, Toast.LENGTH_LONG).show();
            }
            return false;
        }
        @Override
        public boolean onQueryTextChange(String newText) {
            if(!newText.equals("")){
                Toast.makeText(getActivity(), ":"+newText, Toast.LENGTH_LONG).show();
            }
            return false;
        }
    };
    public nav_searchFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nav_search_fragment, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        database = FirebaseDatabase.getInstance();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new adapterSearchItem(RestaurantArrayList, getActivity());
        recyclerView.setAdapter(mAdapter);

        /*restaurantList = (ListView) view.findViewById(R.id.restaurantList);
        restaurantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), ""+position, Toast.LENGTH_LONG).show();
            }
        });*/
        listAllRestaurants();
    }
    public void updateListView(){
        mAdapter.notifyDataSetChanged();
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
    public void setRestaurantFragment(){
        //((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new fragment_establishment())
                .addToBackStack("")
                .commit();
    }
    public class adapterSearchItem extends RecyclerView.Adapter<adapterSearchItem.ViewHolder> {
        private ArrayList<Establishment> RestaurantArrayList = new ArrayList<>();
        private final Picasso picasso;
        private Context context;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView restaurantName;
            TextView restaurantAddress;
            TextView restairantId;
            LinearLayout itemLayout;
            ImageView restaurantIcon;
            ViewHolder(View view) {
                super(view);
                restaurantName = (TextView) view.findViewById(R.id.restaurantName);
                restaurantAddress = (TextView) view.findViewById(R.id.restaurantAddress);
                restairantId = (TextView) view.findViewById(R.id.restairantId);
                itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
                restaurantIcon = (ImageView) view.findViewById(R.id.restaurantIcon);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public adapterSearchItem(ArrayList<Establishment> RestaurantArrayList, Context context) {
            this.RestaurantArrayList = RestaurantArrayList;
            this.picasso = Picasso.with(context);
            this.context = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public adapterSearchItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_establishment, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.restaurantName.setText(RestaurantArrayList.get(position).getName());
            holder.restaurantAddress.setText(RestaurantArrayList.get(position).getAddress());
            holder.restairantId.setText(RestaurantArrayList.get(position).getId());
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, ""+holder.restairantId.getText().toString(), Toast.LENGTH_SHORT).show();
                    Globals.establishmentID = holder.restairantId.getText().toString();
                    setRestaurantFragment();
                }
            });
            //Picasso Image holder
            picasso.cancelRequest(holder.restaurantIcon);
            picasso.load(RestaurantArrayList.get(position).getImage_url())
                    .noPlaceholder()
                    .resizeDimen(R.dimen.searchIconSize, R.dimen.searchIconSize)
                    .centerCrop()
                    .into(holder.restaurantIcon);
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return RestaurantArrayList.size();
        }
    }
    // FIREBASE FUNCTIONS
    private void listAllRestaurants() {
        RestaurantArrayList.clear();
        DatabaseReference restaurantsRef = database.getReference("establishment");
        restaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //READ FINALIZED
                Log.e("FINISH", "We're done loading the initial: "+dataSnapshot.getChildrenCount()+" items");
                updateListView();
            }
            public void onCancelled(DatabaseError firebaseError) { }
        });
        restaurantsRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                //System.out.println("Add "+dataSnapshot.getKey()+" to UI after "+previousKey);
                Log.e("START", "Add "+dataSnapshot.getKey()+" to UI after "+previousKey);
                Establishment restaurant = dataSnapshot.getValue(Establishment.class);
                restaurant.setId(dataSnapshot.getKey());
                //String name = dataSnapshot.child("name").getValue(String.class);
                //Toast.makeText(getActivity(), ""+name,Toast.LENGTH_SHORT).show();
                RestaurantArrayList.add(restaurant);
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }
}
