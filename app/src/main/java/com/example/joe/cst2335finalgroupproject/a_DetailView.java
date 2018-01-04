package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class a_DetailView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_detail);
        Bundle activityBundle=getIntent().getExtras();
        if(activityBundle!=null){
            Log.i("DetailView","there is content here!");
        }
        FragmentTransaction ft =getFragmentManager().beginTransaction();
        a_DetailFragment detailFrag = new a_DetailFragment();
        detailFrag.setArguments(activityBundle);
        ft.add(R.id.a_detailsFrameLayout,detailFrag);
        ft.commit();
    }
}
