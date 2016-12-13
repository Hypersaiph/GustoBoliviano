package com.fuzzyapps.gustoboliviano;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;


public class profile_nav_settings extends Fragment {

    private Spinner spinner;
    private Button exit;

    public profile_nav_settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_nav_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        exit = (Button) view.findViewById(R.id.exit);
        String[] genderArray = getResources().getStringArray(R.array.languaje_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, genderArray);
        spinner.setAdapter(adapter);
        if(spinner.getItemAtPosition(0).equals(Locale.getDefault().getDisplayLanguage())){
            spinner.setSelection(0);
        }else{
            spinner.setSelection(1);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        changeLocation("en");
                        break;
                    case 1:
                        changeLocation("es");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                try{
                    Toast.makeText(getActivity(), R.string.goodbye, Toast.LENGTH_SHORT).show();
                }catch (Exception e){}
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 500);
            }
        });
    }

    private void changeLocation(String languaje) {
        Locale locale = new Locale(languaje);
        Locale.setDefault(locale);
        Configuration config = getActivity().getResources().getConfiguration();
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config,
                getActivity().getResources().getDisplayMetrics());
    }
}
