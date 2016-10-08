package com.fuzzyapps.gustoboliviano;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Geovani on 07/10/2016
 */

public final class Singleton extends AppCompatActivity{
    private static Singleton instance = null;
    private Singleton() {}
    public static synchronized Singleton getInstance() {
        if (instance == null){
            instance = new Singleton();
        }
        return instance;
    }
}