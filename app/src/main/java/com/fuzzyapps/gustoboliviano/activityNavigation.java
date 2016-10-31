package com.fuzzyapps.gustoboliviano;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;

import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class activityNavigation extends activityBase implements BottomNavigation.OnMenuItemSelectionListener{
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // Fixing Later Map loading Delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorLayout01);

        if (translucentStatus) {
            Log.d("asd", "hasTranslucentStatusBar");
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) coordinatorLayout.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }
        initializeBottomNavigation(savedInstanceState);
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
        //Toast.makeText(activityNavigation.this,""+position,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position) {
        //Toast.makeText(activityNavigation.this,"2. "+position,Toast.LENGTH_SHORT).show();
    }
    private void changeToolbarColor(int position) {
        String colorPrimary = "#525252";
        String colorPrimaryDark = "#333333";
        int primaryColor = 0;
        int darkColor = 0;
        FragmentManager fragmentManager = getFragmentManager();

        switch (position){
            case 0:
                primaryColor = Color.parseColor(colorPrimary);
                darkColor = Color.parseColor(colorPrimaryDark);
                //getSupportActionBar().show();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_startFragment())
                        .commit();
                break;
            case 1:
                primaryColor = Color.parseColor(colorPrimary);
                darkColor = Color.parseColor(colorPrimaryDark);
                //getSupportActionBar().show();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_searchFragment())
                        .commit();
                break;
            case 2:
                primaryColor = Color.parseColor(colorPrimary);
                darkColor = Color.parseColor(colorPrimaryDark);
                //getSupportActionBar().hide();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_mapFragment())
                        .commit();
                break;
            case 3:
                primaryColor = Color.parseColor(colorPrimary);
                darkColor = Color.parseColor(colorPrimaryDark);
                //getSupportActionBar().hide();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new fragment_product())
                        .commit();
                //.replace(R.id.frame_layout, new nav_notificationFragment())
                break;
            case 4:
                primaryColor = Color.parseColor(colorPrimary);
                darkColor = Color.parseColor(colorPrimaryDark);
                //getSupportActionBar().show();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, new nav_profileFragment())
                        .commit();
                break;
        }
        //toolbar.setBackgroundColor(primaryColor);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(primaryColor);
            getWindow().setStatusBarColor(darkColor);
        }
    }
}
