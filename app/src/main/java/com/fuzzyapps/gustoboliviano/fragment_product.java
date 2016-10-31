package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class fragment_product extends Fragment{
    private String TAG = "fragment_product";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutInflater layoutInflater;

    private FirebaseDatabase database;
    private DatabaseReference reviewRef;
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
        reviewRef = database.getReference("reviewProduct");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new reviewAdapter(reviewArrayList, getActivity());
        recyclerView.setAdapter(mAdapter);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.productRateButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlertDialog();
            }
        });
        listAllReviewsFor(Globals.productID);
    }
    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
    }
    private void displayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= layoutInflater.inflate(R.layout.item_review_input, null);
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
    private void displayAlertDialogOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= layoutInflater.inflate(R.layout.item_review_options, null);
        builder.setView(view);
        builder.show();
    }
    private void writeUserReview(float rating, String title, String description) {
        double roundRating = (double) Math.round(rating * 100) / 100;
        ReviewForm reviewForm = new ReviewForm(Globals.userID,Globals.productID, title, description, roundRating, ServerValue.TIMESTAMP);
        reviewRef.child(Globals.userID).setValue(reviewForm, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(getActivity(),"Registrado.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {
        private ArrayList<ReviewForm> reviewArrayList = new ArrayList<>();
        private final Picasso picasso;
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
            TextView reviewLikeNumber;
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
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public reviewAdapter(ArrayList<ReviewForm> reviewArrayList, Context context) {
            this.reviewArrayList = reviewArrayList;
            this.picasso = Picasso.with(context);
            this.context = context;
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
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.circularImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.asd ));
            holder.reviewRatingBar.setRating((float)reviewArrayList.get(position).getRating());
            holder.reviewUserName.setText(reviewArrayList.get(position).getUserID());
            holder.reviewTitle.setText(reviewArrayList.get(position).getTitle());
            holder.reviewDescription.setText(reviewArrayList.get(position).getDescription());
            holder.reviewDate.setText(/*reviewArrayList.get(position).getTimestamp().toString()*/"3h");
            holder.reviewLikeNumber.setText("0");
            holder.reviewLikeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Toast.makeText(getActivity(), "you like it!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Toast.makeText(getActivity(), "you dont like it! duh e.e", Toast.LENGTH_SHORT).show();
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
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return reviewArrayList.size();
        }
    }
    // FIREBASE FUNCTIONS
    private void listAllReviewsFor(String productID) {
        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //READ FINALIZED
                Log.e(TAG, "We're done loading the initial: "+dataSnapshot.getChildrenCount()+" items");
                updateRecyclerView();
            }
            public void onCancelled(DatabaseError firebaseError) { }
        });
        reviewRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                //System.out.println("Add "+dataSnapshot.getKey()+" to UI after "+previousKey);
                Log.e(TAG, "Add "+dataSnapshot.getKey()+" to UI after "+previousKey);
                ReviewForm review = new ReviewForm();//dataSnapshot.getValue(ReviewForm.class);
                review.setRestaurantID(dataSnapshot.child("restaurantID").getValue(String.class));
                review.setUserID(dataSnapshot.child("userID").getValue(String.class));
                review.setTitle(dataSnapshot.child("title").getValue(String.class));
                review.setDescription(dataSnapshot.child("description").getValue(String.class));
                review.setRating(dataSnapshot.child("rating").getValue(Double.class));
                //restaurant.setId(dataSnapshot.getKey());
                //String name = dataSnapshot.child("name").getValue(String.class);
                //Toast.makeText(getActivity(), ""+name,Toast.LENGTH_SHORT).show();
                reviewArrayList.add(review);
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
