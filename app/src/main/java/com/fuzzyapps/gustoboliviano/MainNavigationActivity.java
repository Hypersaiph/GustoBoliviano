package com.fuzzyapps.gustoboliviano;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainNavigationActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeBottomNavigation(savedInstanceState);
        //Toast.makeText(getApplication(),"",)
    }
    protected void initializeBottomNavigation(final Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            getBottomNavigation().setDefaultSelectedIndex(0);
            changeToolbarColor(0);
            final BadgeProvider provider = getBottomNavigation().getBadgeProvider();
        }
    }
    @Override
    public void onMenuItemSelect(@IdRes final int itemId, final int position) {
        changeToolbarColor(position);
        Toast.makeText(MainNavigationActivity.this,""+position,Toast.LENGTH_SHORT).show();
    }

    private void changeToolbarColor(int position) {
        int primaryColor = 0;
        int darkColor = 0;
        FragmentManager fragmentManager = getFragmentManager();

        switch (position){
            case 0:
                primaryColor = Color.parseColor("#1F1F1F");
                darkColor = Color.parseColor("#000000");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new startFragment())
                        .commit();
                break;
            case 1:
                primaryColor = Color.parseColor("#669900");
                darkColor = Color.parseColor("#527a00");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new searchFragment())
                        .commit();
                break;
            case 2:
                primaryColor = Color.parseColor("#FF5252");
                darkColor = Color.parseColor("#ff1515");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new mapFragment())
                        .commit();
                break;
            case 3:
                primaryColor = Color.parseColor("#FF8800");
                darkColor = Color.parseColor("#e07800");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new notificationFragment())
                        .commit();
                break;
            case 4:
                primaryColor = Color.parseColor("#764ac6");
                darkColor = Color.parseColor("#4d2b89");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new profileFragment())
                        .commit();
                break;
        }
        toolbar.setBackgroundColor(primaryColor);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(primaryColor);
            getWindow().setStatusBarColor(darkColor);
        }
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position) {
        Toast.makeText(MainNavigationActivity.this,"2. "+position,Toast.LENGTH_SHORT).show();
    }
}
