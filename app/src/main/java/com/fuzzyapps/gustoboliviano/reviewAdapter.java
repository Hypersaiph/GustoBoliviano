package com.fuzzyapps.gustoboliviano;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //holder.circularImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.asd ));
        //holder.reviewDate.setText(/*reviewArrayList.get(position).getTimestamp().toString()*/"3h");
        holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
        //holder.reviewUserName.setText(reviewArrayList.get(position).getUserID());
        holder.reviewTitle.setText(reviewArrayList.get(position).getTitle());
        holder.reviewDescription.setText(reviewArrayList.get(position).getDescription());
        holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
        holder.reviewRatingBarText.setText(holder.reviewRatingBar.getRating()+"");
        holder.reviewLikeNumber.setText("0");
        holder.reviewLikeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(context, "you like it!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(context, "you dont like it! duh e.e", Toast.LENGTH_SHORT).show();
            }
        });
        if(reviewArrayList.get(position).getId().equals(reviewArrayList.get(position).getRestaurantID())){
            //IT IS AN ESTABLISHMENT
            getDataFromEstablishment(reviewArrayList.get(position).getRestaurantID(), holder);
        }else{
            //IT IS A PRODUCT
            getDataFromProduct(reviewArrayList.get(position).getRestaurantID(), reviewArrayList.get(position).getId(), holder);
        }
        getAllDataFromUser(reviewArrayList.get(position).userID, holder);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviewArrayList.size();
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