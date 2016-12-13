package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class fragment_product extends Fragment{
    private String TAG = "fragment_product";
    //REVIEW FORM VARIBALES
    private EditText reviewTitle, reviewDescription;
    private CircularImageView profileImage;
    private RatingBar reviewRatingBar;
    private LinearLayout review_inputLinearLayout;
    //UI VARIABLES
    private TextView productTittle, productDescription, productRating, productRatingStats, productPrice;
    private RatingBar productRatingBar;
    private ImageView productFavoritesImage, productBanner;//ic_bookmark_border_black_24dp
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutInflater layoutInflater;
    private boolean isItMyFavorite;
    //FIREBASE VARIABLES
    private FirebaseDatabase database;
    private DatabaseReference reviewEstablishmentRef, reviewUserRef, mDatabase;
    ArrayList<ReviewForm> reviewArrayList = new ArrayList<>();
    public fragment_product(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAR LOS OBJETOS
        layoutInflater = getActivity().getLayoutInflater();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        reviewEstablishmentRef = database.getReference("reviewEstablishment");
        reviewUserRef = database.getReference("reviewUser");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        productTittle = (TextView) view.findViewById(R.id.productTittle);
        productDescription = (TextView) view.findViewById(R.id.productDescription);
        productRating = (TextView) view.findViewById(R.id.productRating);
        productRatingStats = (TextView) view.findViewById(R.id.productRatingStats);
        productPrice = (TextView) view.findViewById(R.id.productPrice);
        productRatingBar = (RatingBar) view.findViewById(R.id.productRatingBar);
        productFavoritesImage = (ImageView) view.findViewById(R.id.productFavoritesImage);
        productBanner = (ImageView) view.findViewById(R.id.productBanner);
        productFavoritesImage.setEnabled(false);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new reviewAdapter(reviewArrayList, getActivity(), database);
        recyclerView.setAdapter(mAdapter);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.productRateButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog();
            }
        });
        listAllReviewsFor(Globals.productID);
        addRealTimeValueEventListener();
        setQueries();
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
                    collapsingToolbarLayout.setTitle(productTittle.getText().toString());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        productFavoritesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isItMyFavorite){
                    addToFavorites(false);
                }else{
                    addToFavorites(true);
                }
            }
        });
    }
    private void setQueries() {
        Query reviewsQuery = mDatabase.child("reviewEstablishment").child(Globals.productID).orderByChild("timestamp");
        reviewsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("setQueries key",""+dataSnapshot.getKey());
                Log.e("retrieve",""+dataSnapshot.getValue());
                double rating = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ReviewForm review = new ReviewForm();
                    review.setId(child.getKey());
                    review.setEstablishmentID(child.child("establishmentID").getValue(String.class));
                    review.setUserID(child.child("userID").getValue(String.class));
                    review.setTitle(child.child("title").getValue(String.class));
                    review.setDescription(child.child("description").getValue(String.class));
                    review.setRating(child.child("rating").getValue(Double.class));
                    review.setPostedOn(child.child("timestamp").getValue(long.class));
                    rating += review.getRating();
                }
                if(rating != 0){
                    productRatingBar.setRating((float) (rating/dataSnapshot.getChildrenCount()));
                    productRating.setText(String.format("%.1f", (float) (rating/dataSnapshot.getChildrenCount())));
                }else{
                    productRating.setText(R.string.zero);
                }
                productRatingStats.setText("("+dataSnapshot.getChildrenCount()+")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //QUERY TO SEE IF ITS IN MY FAVORITES
        mDatabase.child("favorites").child(Globals.userID).child(Globals.productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productFavoritesImage.setEnabled(true);
                try {
                    Log.e("datasnapshot", dataSnapshot.getValue().toString());
                    Favorites favorites = new Favorites();
                    favorites.setActive(dataSnapshot.child("active").getValue(boolean.class));
                    if(favorites.isActive()){
                        isItMyFavorite = true;
                        Picasso.with(getActivity()).load(R.mipmap.ic_bookmark_black_24dp).into(productFavoritesImage);
                    }else{
                        isItMyFavorite = false;
                        Picasso.with(getActivity()).load(R.mipmap.ic_bookmark_border_black_24dp).into(productFavoritesImage);
                    }
                }catch (Exception e){
                    isItMyFavorite = false;
                    Picasso.with(getActivity()).load(R.mipmap.ic_bookmark_border_black_24dp).into(productFavoritesImage);
                    Log.e("error", e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void addRealTimeValueEventListener() {
        //REAL TIME LISTENER
        ValueEventListener productListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("snapshot", dataSnapshot.getValue().toString());
                Product product = dataSnapshot.getValue(Product.class);
                try{
                    productTittle.setText(product.getName());
                }catch (Exception e){}
                try{
                    productDescription.setText(product.getDescription());
                }catch (Exception e){}
                /*try{
                    productPrice.setText("");
                    for (int i=0; i< Integer.parseInt(product.getPrice()); i++){
                        productPrice.append("$");
                    }
                }catch (Exception e){}*/
                try{
                    if(product.isPriceVisible()){
                        productPrice.setText(product.getPrice());
                    }else{
                        productPrice.setText(getResources().getString(R.string.notspecified));
                    }
                }catch (Exception e){

                }
                try{
                    Picasso.with(getActivity()).load(product.getImage_url()).into(productBanner);
                    Log.e("image",product.getImage_url());
                }catch (Exception e){
                    Log.e("error picaso",e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child("establishment").child(Globals.establishmentID).child("product").child(Globals.productID).addValueEventListener(productListener);
    }

    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
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
    private void setUpDataFromUser() {
        mDatabase.child("reviewEstablishment").child(Globals.productID).child(Globals.userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
    private void displayAlertDialogOptions(final String link, final String userID, final String reportedUserID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.chooseOptionReport));
        View view= layoutInflater.inflate(R.layout.item_review_options, null);
        Button offensiveContent = (Button) view.findViewById(R.id.offensiveContent);
        Button spam = (Button) view.findViewById(R.id.spam);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        offensiveContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report report = new Report(link, userID, reportedUserID, "Offensive Content", "Product Review", ServerValue.TIMESTAMP);
                mDatabase.child("report").push().setValue(report, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        try {
                            Toast.makeText(getActivity(), R.string.Success, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
                alert.cancel();
            }
        });
        spam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report report = new Report(link, userID, reportedUserID, "Spam or Fraud", "Product Review", ServerValue.TIMESTAMP);
                mDatabase.child("report").push().setValue(report, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        try {
                            Toast.makeText(getActivity(), R.string.Success, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
                alert.cancel();
            }
        });
        alert.show();
    }
    private void writeUserReview(float rating, String title, String description) {
        double roundRating = (double) Math.round(rating * 100) / 100;
        ReviewForm reviewForm = new ReviewForm(Globals.userID,Globals.establishmentID, title, description, roundRating, ServerValue.TIMESTAMP, Globals.productID, true);
        reviewEstablishmentRef.child(Globals.productID).child(Globals.userID).setValue(reviewForm);
        reviewUserRef.child(Globals.userID).child(Globals.productID).setValue(reviewForm, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(getActivity(),"Registrado.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {
        private ArrayList<ReviewForm> reviewArrayList = new ArrayList<>();
        private final Picasso picasso;
        private FirebaseDatabase database;
        private Context context;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CircularImageView circularImageView;
            LikeButton reviewLikeButton;
            ImageView reviewOptionsButton;
            RatingBar reviewRatingBar;
            TextView reviewUserName;
            TextView reviewTitle;
            TextView reviewDescription;
            TextView reviewDate;
            TextView reviewLikeNumber, reviewRatingBarText;
            ViewHolder(View view) {
                super(view);
                circularImageView = (CircularImageView) view.findViewById(R.id.reviewUserImage);
                reviewLikeButton = (LikeButton) view.findViewById(R.id.reviewLikeButton);
                reviewOptionsButton = (ImageView) view.findViewById(R.id.reviewOptionsButton);
                reviewRatingBar = (RatingBar) view.findViewById(R.id.reviewRatingBar);
                reviewUserName = (TextView) view.findViewById(R.id.reviewUserName);
                reviewTitle = (TextView) view.findViewById(R.id.reviewTitle);
                reviewDescription = (TextView) view.findViewById(R.id.reviewDescription);
                reviewDate = (TextView) view.findViewById(R.id.reviewDate);
                reviewLikeNumber = (TextView) view.findViewById(R.id.reviewLikeNumber);
                reviewRatingBarText = (TextView) view.findViewById(R.id.reviewRatingBarText);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public reviewAdapter(ArrayList<ReviewForm> reviewArrayList, Context context, FirebaseDatabase database) {
            this.reviewArrayList = reviewArrayList;
            this.picasso = Picasso.with(context);
            this.context = context;
            this.database = database;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public reviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.reviewLikeButton.setEnabled(false);
            holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
            holder.reviewTitle.setText(reviewArrayList.get(position).getTitle());
            holder.reviewDescription.setText(reviewArrayList.get(position).getDescription());
            holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
            holder.reviewRatingBarText.setText(holder.reviewRatingBar.getRating()+"");
            holder.reviewLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    registerLike(true, reviewArrayList.get(position).getProductID(), reviewArrayList.get(position).getUserID());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    registerLike(false, reviewArrayList.get(position).getProductID(), reviewArrayList.get(position).getUserID());
                }
            });
            holder.reviewOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayAlertDialogOptions("reviewEstablishment/"+reviewArrayList.get(position).getProductID()+"/"+reviewArrayList.get(position).getId()+"/", Globals.userID, reviewArrayList.get(position).getUserID());
                }
            });
            holder.circularImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Globals.userID.equals(reviewArrayList.get(position).getUserID())) {
                        Globals.visitedID = reviewArrayList.get(position).getUserID();
                        setProductFragment();
                    }
                }
            });
            holder.reviewUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Globals.userID.equals(reviewArrayList.get(position).getUserID())) {
                        Globals.visitedID = reviewArrayList.get(position).getUserID();
                        setProductFragment();
                    }
                }
            });
            //Picasso Image holder
            picasso.cancelRequest(holder.reviewOptionsButton);
            picasso.load(R.mipmap.ic_more_vert_black_24dp)
                    .noPlaceholder()
                    .resizeDimen(R.dimen.icon36dp, R.dimen.icon36dp)
                    .centerCrop()
                    .into(holder.reviewOptionsButton);
            getAllDataFromUser(reviewArrayList.get(position).getUserID(), holder);
            queryForTextView(holder, reviewArrayList.get(position).getId(), reviewArrayList.get(position).getProductID());
            doILike(holder, reviewArrayList.get(position).getProductID(), reviewArrayList.get(position).getUserID());
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return reviewArrayList.size();
        }
        private void queryForTextView(final ViewHolder holder, String reviewID, String productID) {
            //QUERY TO SEE HOW MANY PEOPLE LIKES THIS REVIEW
            Query likeQuery = mDatabase.child("reviewEstablishment").child(productID).child(reviewID).child("likes").orderByValue().equalTo(true);
            likeQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        holder.reviewLikeNumber.setText(dataSnapshot.getChildrenCount()+"");
                    }else{
                        holder.reviewLikeNumber.setText(R.string.zero);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void registerLike(boolean like, String productID, String userID){
            Log.e("register Like",""+like);
            Map<String, Object> userChild = new HashMap<>();
            userChild.put(Globals.userID, like);
            mDatabase.child("reviewEstablishment").child(productID).child(userID).child("likes").updateChildren(userChild);
            mDatabase.child("reviewUser").child(userID).child(productID).child("likes").updateChildren(userChild);
        }
        private void doILike(final ViewHolder holder, String productID, String userID) {
            //QUERY TO SEE IF I LIKE THIS COMMENTE
            Query likeQuery = mDatabase.child("reviewEstablishment").child(productID).child(userID).child("likes").orderByKey().equalTo(Globals.userID);
            likeQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.reviewLikeButton.setEnabled(true);
                    boolean iLikeThisReview;
                    if(dataSnapshot.getValue() != null){
                        iLikeThisReview = dataSnapshot.child(Globals.userID).getValue(boolean.class);
                        if(iLikeThisReview){
                            holder.reviewLikeButton.setLiked(true);
                        }else{
                            holder.reviewLikeButton.setLiked(false);
                        }
                    }else{
                        holder.reviewLikeButton.setLiked(false);
                    }
                    Log.e("doILike",""+dataSnapshot.getValue());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void getAllDataFromUser(String userID, final ViewHolder holder) {
            DatabaseReference mDatabase = database.getReference();
            mDatabase.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    try{
                        holder.reviewUserName.setText(user.getName());
                    }catch (Exception e){}
                    try{
                        picasso.load(user.getImage_url()).into(holder.circularImageView);
                    }catch (Exception e){
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    // FIREBASE FUNCTIONS
    private void listAllReviewsFor(String productID) {
        Query productQuery = reviewEstablishmentRef.child(productID).orderByChild("timestamp");
        productQuery.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                ReviewForm review = new ReviewForm();
                review.setEstablishmentID(dataSnapshot.child("establishmentID").getValue(String.class));
                review.setUserID(dataSnapshot.child("userID").getValue(String.class));
                review.setTitle(dataSnapshot.child("title").getValue(String.class));
                review.setDescription(dataSnapshot.child("description").getValue(String.class));
                review.setRating(dataSnapshot.child("rating").getValue(Double.class));
                review.setPostedOn(dataSnapshot.child("timestamp").getValue(long.class));
                review.setProductID(dataSnapshot.child("productID").getValue(String.class));
                review.setId(dataSnapshot.getKey());
                try {
                    review.setVisible(dataSnapshot.child("visible").getValue(boolean.class));
                    review.setId(dataSnapshot.getKey());
                    if (review.isVisible()) {
                        reviewArrayList.add(review);
                    }
                }catch (Exception e){}
                updateRecyclerView();
                Log.e("retrieve",""+dataSnapshot.getKey());
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ReviewForm review = new ReviewForm();
                review.setEstablishmentID(dataSnapshot.child("establishmentID").getValue(String.class));
                review.setUserID(dataSnapshot.child("userID").getValue(String.class));
                review.setTitle(dataSnapshot.child("title").getValue(String.class));
                review.setDescription(dataSnapshot.child("description").getValue(String.class));
                review.setRating(dataSnapshot.child("rating").getValue(Double.class));
                review.setPostedOn(dataSnapshot.child("timestamp").getValue(long.class));
                review.setProductID(dataSnapshot.child("productID").getValue(String.class));
                review.setVisible(dataSnapshot.child("visible").getValue(boolean.class));
                int search = Search(review.getUserID());
                review.setId(dataSnapshot.getKey());
                if(review.isVisible()){
                    try{
                        reviewArrayList.set(search, review);
                    }catch (Exception e){
                        reviewArrayList.add(review);
                    }
                }else{
                    try {
                        reviewArrayList.remove(search);
                    }catch (Exception e){
                        reviewArrayList.clear();
                    }
                }
                updateRecyclerView();
                Log.e("updated",""+dataSnapshot.getKey());
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            public void onCancelled(DatabaseError firebaseError) { }
        });
    }
    public int Search(String userID) {
        int position = 0;
        for(int i=0 ; i< reviewArrayList.size(); i++){
            if(reviewArrayList.get(i).getUserID().equals(userID)){
                position = i;
                break;
            }
        }
        Log.e("Search",""+position);
        return position;
    }
    public void setProductFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, new visit_profileFragment())
                .commit();
    }
    private void addToFavorites(boolean favorite) {
        Favorites favorites = new Favorites(Globals.productID, Globals.establishmentID, ServerValue.TIMESTAMP, favorite);
        mDatabase.child("favorites").child(Globals.userID).child(Globals.productID).setValue(favorites);
    }
}
