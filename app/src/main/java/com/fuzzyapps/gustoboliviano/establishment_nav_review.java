package com.fuzzyapps.gustoboliviano;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class establishment_nav_review extends Fragment {

    private RecyclerView recyclerViewReviews;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //FIREBASE VARIABLES
    private FirebaseDatabase database;
    private DatabaseReference reviewRef, mDatabase;

    //UI VARIABLES
    private LayoutInflater layoutInflater;
    ArrayList<ReviewForm> reviewArrayList = new ArrayList<>();
    public establishment_nav_review() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.establishment_nav_review_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI INICIALIZAMOS TODOS LOS OBJETOS
        layoutInflater = getActivity().getLayoutInflater();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        reviewRef = database.getReference("reviewEstablishment");
        recyclerViewReviews = (RecyclerView) view.findViewById(R.id.recyclerViewReviews);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewReviews.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReviews.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new reviewAdapter(reviewArrayList, getActivity(), database);
        recyclerViewReviews.setAdapter(mAdapter);
        listAllReviewsFor(Globals.establishmentID);
    }
    private void displayAlertDialogOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.chooseOptionReport));
        View view= layoutInflater.inflate(R.layout.item_review_options, null);
        builder.setView(view);
        builder.show();
    }
    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
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
            //holder.circularImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.asd ));
            //holder.reviewDate.setText(/*reviewArrayList.get(position).getTimestamp().toString()*/"3h");
            holder.reviewLikeButton.setVisibility(View.GONE);
            holder.reviewLikeNumber.setVisibility(View.GONE);
            holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
            //holder.reviewUserName.setText(reviewArrayList.get(position).getUserID());
            holder.reviewTitle.setText(reviewArrayList.get(position).getTitle());
            holder.reviewDescription.setText(reviewArrayList.get(position).getDescription());
            holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
            holder.reviewRatingBarText.setText(holder.reviewRatingBar.getRating()+"");
            holder.reviewLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    registerLike(true, position);
                    //Toast.makeText(context, "you like it!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    registerLike(false, position);
                    //Toast.makeText(context, "you dont like it! duh e.e", Toast.LENGTH_SHORT).show();
                }
            });
            holder.reviewOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayAlertDialogOptions();
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
            queryForTextView(holder, reviewArrayList.get(position).getId(), position);
            doILike(holder, position);
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return reviewArrayList.size();
        }
        private void queryForTextView(final ViewHolder holder, String reviewID, int position) {
            //QUERY TO SEE HOW MANY PEOPLE LIKES THIS REVIEW
            Query likeQuery = mDatabase.child("reviewEstablishment").child(reviewArrayList.get(position).getEstablishmentID()).child(reviewID).child("likes").orderByValue().equalTo(true);
            likeQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.reviewLikeNumber.setVisibility(View.VISIBLE);
                    if(dataSnapshot.getValue() != null){
                        holder.reviewLikeNumber.setText(dataSnapshot.getChildrenCount()+"");
                    }else{
                        holder.reviewLikeNumber.setText(R.string.zero);
                    }
                    Log.e("following",""+dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void registerLike(boolean like, int position){
            Log.e("register Like",""+like);
            Map<String, Object> userChild = new HashMap<>();
            userChild.put(Globals.userID, like);
            mDatabase.child("reviewEstablishment").child(reviewArrayList.get(position).getEstablishmentID()).child(reviewArrayList.get(position).getId()).child("likes").updateChildren(userChild);
            mDatabase.child("reviewUser").child(reviewArrayList.get(position).getUserID()).child(reviewArrayList.get(position).getEstablishmentID()).child("likes").updateChildren(userChild);
        }
        private void doILike(final ViewHolder holder, int position) {
            //QUERY TO SEE IF I LIKE THIS COMMENTE
            Query likeQuery = mDatabase.child("reviewEstablishment").child(reviewArrayList.get(position).getEstablishmentID()).child(reviewArrayList.get(position).getId()).child("likes").orderByKey().equalTo(Globals.userID);
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
    private void listAllReviewsFor(String restauranID) {
        /*reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //READ FINALIZED
                Log.e("rest", "We're done loading the initial: "+dataSnapshot.getChildrenCount()+" items");
                updateRecyclerView();
            }
            public void onCancelled(DatabaseError firebaseError) { }
        });*/
        Query mainBranchQuery = reviewRef.child(restauranID).orderByChild("timestamp");
        mainBranchQuery.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                ReviewForm review = new ReviewForm();
                review.setEstablishmentID(dataSnapshot.child("establishmentID").getValue(String.class));
                review.setUserID(dataSnapshot.child("userID").getValue(String.class));
                review.setTitle(dataSnapshot.child("title").getValue(String.class));
                review.setDescription(dataSnapshot.child("description").getValue(String.class));
                review.setRating(dataSnapshot.child("rating").getValue(Double.class));
                review.setPostedOn(dataSnapshot.child("timestamp").getValue(long.class));
                review.setId(dataSnapshot.getKey());
                reviewArrayList.add(review);
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
                review.setId(dataSnapshot.getKey());
                int search = Search(review.getUserID());
                reviewArrayList.set(search, review);
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
}
