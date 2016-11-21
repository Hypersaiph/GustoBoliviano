package com.fuzzyapps.gustoboliviano;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.fuzzyapps.gustoboliviano.R.id.profileFollowersNumber;
import static com.fuzzyapps.gustoboliviano.R.id.profileReviewsNumber;

/**
 * Created by Geovani on 17/11/2016
 */

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {
    private ArrayList<ReviewForm> reviewArrayList = new ArrayList<>();
    private final Picasso picasso;
    private FirebaseDatabase database;
    private Context context;
    private String userID;
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
    public reviewAdapter(ArrayList<ReviewForm> reviewArrayList, Context context, FirebaseDatabase database, String userID) {
        this.reviewArrayList = reviewArrayList;
        this.picasso = Picasso.with(context);
        this.context = context;
        this.database = database;
        this.userID = userID;
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
        holder.reviewLikeButton.setVisibility(View.GONE);
        holder.reviewLikeNumber.setVisibility(View.GONE);
        holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
        holder.reviewTitle.setText(reviewArrayList.get(position).getTitle());
        holder.reviewDescription.setText(reviewArrayList.get(position).getDescription());
        holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
        holder.reviewRatingBarText.setText(holder.reviewRatingBar.getRating()+"");
        holder.reviewLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(reviewArrayList.get(position).getProductID().equals(reviewArrayList.get(position).getEstablishmentID())) {
                    registerLike(true, reviewArrayList.get(position).getEstablishmentID(), reviewArrayList.get(position).getUserID());
                }else{
                    registerLike(true, reviewArrayList.get(position).getProductID(), reviewArrayList.get(position).getUserID());
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if(reviewArrayList.get(position).getProductID().equals(reviewArrayList.get(position).getEstablishmentID())) {
                    registerLike(false, reviewArrayList.get(position).getEstablishmentID(), reviewArrayList.get(position).getUserID());
                }else{
                    registerLike(false, reviewArrayList.get(position).getProductID(), reviewArrayList.get(position).getUserID());
                }
            }
        });
        if(reviewArrayList.get(position).getProductID().equals(reviewArrayList.get(position).getEstablishmentID())){
            //IT IS AN ESTABLISHMENT
            getDataFromEstablishment(reviewArrayList.get(position).getEstablishmentID(), holder);
            queryForTextView(holder, reviewArrayList.get(position).getUserID(), reviewArrayList.get(position).getEstablishmentID());
            doILike(holder, reviewArrayList.get(position).getEstablishmentID(), reviewArrayList.get(position).getUserID());
        }else{
            //IT IS A PRODUCT
            getDataFromProduct(reviewArrayList.get(position).getEstablishmentID(), reviewArrayList.get(position).getProductID(), holder);
            queryForTextView(holder, reviewArrayList.get(position).getUserID(), reviewArrayList.get(position).getProductID());
            doILike(holder, reviewArrayList.get(position).getProductID(), reviewArrayList.get(position).getUserID());
        }
        getAllDataFromUser(reviewArrayList.get(position).userID, holder);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }
    private void queryForTextView(final ViewHolder holder, String reviewID, String productID) {
        //QUERY TO SEE HOW MANY PEOPLE LIKES THIS REVIEW
        Log.e("productID",productID);
        Log.e("reviewID",reviewID);
        DatabaseReference mDatabase = database.getReference();
        Query likeQuery = mDatabase.child("reviewEstablishment").child(productID).child(reviewID).child("likes").orderByValue().equalTo(true);
        likeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.reviewLikeNumber.setVisibility(View.VISIBLE);
                if(dataSnapshot.getValue() != null){
                    holder.reviewLikeNumber.setText(dataSnapshot.getChildrenCount()+"");
                }else{
                    holder.reviewLikeNumber.setText(R.string.zero);
                }
                Log.e("textView",""+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void registerLike(boolean like, String productID, String userID){
        DatabaseReference mDatabase = database.getReference();
        Map<String, Object> userChild = new HashMap<>();
        userChild.put(Globals.userID, like);
        mDatabase.child("reviewEstablishment").child(productID).child(userID).child("likes").updateChildren(userChild);
        mDatabase.child("reviewUser").child(userID).child(productID).child("likes").updateChildren(userChild);
    }
    private void doILike(final ViewHolder holder, String productID, String userID) {
        //QUERY TO SEE IF I LIKE THIS COMMENTE
        DatabaseReference mDatabase = database.getReference();
        Query likeQuery = mDatabase.child("reviewEstablishment").child(productID).child(userID).child("likes").orderByKey().equalTo(Globals.userID);
        likeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.reviewLikeButton.setVisibility(View.VISIBLE);
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
                    picasso.load(user.getImage_url()).into(holder.circularImageView);
                }catch (Exception e){
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getDataFromEstablishment(String establishmentID, final ViewHolder holder) {
        DatabaseReference mDatabase = database.getReference();
        mDatabase.child("establishment").child(establishmentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Establishment establishment = dataSnapshot.getValue(Establishment.class);
                try{
                    holder.reviewUserName.setText(establishment.getName());
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getDataFromProduct(String establishmentID, String productID, final ViewHolder holder) {
        DatabaseReference mDatabase = database.getReference();
        mDatabase.child("establishment").child(establishmentID).child("product").child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                try{
                    holder.reviewUserName.setText(product.getName());
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}