package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class visit_nav_review extends Fragment {
    //UI VARIABLES
    private RecyclerView recyclerViewReviews;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ReviewForm> visitedReviewArrayList = new ArrayList<>();
    //FIREBASE VARIABLES
    private FirebaseDatabase database;
    private DatabaseReference reviewUserRef;
    public visit_nav_review() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.visit_nav_review_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        reviewUserRef = database.getReference("reviewUser");
        recyclerViewReviews = (RecyclerView) view.findViewById(R.id.recyclerViewReviews);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewReviews.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReviews.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new reviewAdapter(visitedReviewArrayList, getActivity(), database, Globals.visitedID);
        recyclerViewReviews.setAdapter(mAdapter);
        listMyReviews(Globals.visitedID);
    }
    public void updateRecyclerView(){
        mAdapter.notifyDataSetChanged();
    }

    private void listMyReviews(String userID) {
        Log.e("userID",userID);
        Query mainBranchQuery = reviewUserRef.child(userID).orderByChild("timestamp");
        mainBranchQuery.addChildEventListener(new ChildEventListener() {
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
                visitedReviewArrayList.add(review);
                updateRecyclerView();
                /*Log.e("key",""+dataSnapshot.getKey());
                Log.e("retrieve",""+dataSnapshot.getValue());*/
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
                review.setId(dataSnapshot.getKey());
                int search = Search(review.getId());
                visitedReviewArrayList.set(search, review);
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
    public int Search(String reviewID) {
        int position = 0;
        for(int i=0 ; i< visitedReviewArrayList.size(); i++){
            if(visitedReviewArrayList.get(i).getId().equals(reviewID)){
                position = i;
                break;
            }
        }
        Log.e("Search",""+position);
        return position;
    }
}
