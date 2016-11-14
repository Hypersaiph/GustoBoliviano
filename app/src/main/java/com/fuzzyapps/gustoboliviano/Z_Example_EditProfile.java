package com.fuzzyapps.gustoboliviano;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Z_Example_EditProfile extends AppCompatActivity {

    private Button updateButton;
    private EditText userName;
    private String username;
    private String userId;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuario");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("userName");
        userId = bundle.getString("userID");
        //updateButton = (Button) findViewById(R.id.updateButton);
        //userName = (EditText) findViewById(R.id.userName);
        userName.setText(username);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeNewUser(userId,userName.getText().toString(),"");
            }
        });
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
    private void writeNewUser(String userId, String name, String email) {
        /*User user = new User(name, email);
        myRef.child(userId).setValue(user);*/
    }
}
