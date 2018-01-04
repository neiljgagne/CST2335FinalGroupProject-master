package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by neilg_000 on 2017-12-30.
 */

public class n_NutritionDetail extends Activity {
    protected final static String ACTIVITY_NAME = "n_NutritionDetail";
    protected Bundle bundle = new Bundle();

    /**
     * New activity displaying empty frame layout which is inflated, (phone in portrait mode)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_fragment_detail_view);

        Bundle passedInfo = getIntent().getExtras();

        n_NutritionFragment nutritionFragment = new n_NutritionFragment();
        nutritionFragment.setArguments(passedInfo);
        getFragmentManager().beginTransaction()
                .replace(R.id.t_frameLayout, nutritionFragment).commit();
    }
}
