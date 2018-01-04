package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class c_AddFuelDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_add_fuel_details);

        Bundle fragmentDetails = new Bundle();
        fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnAdd));
        fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_AddDetailsTitle));

        c_EnterFuelDetailsFragment loadedFragment = new c_EnterFuelDetailsFragment();
        loadedFragment.setArguments(fragmentDetails);

        getFragmentManager().beginTransaction()
                .add(R.id.flAddDetails, loadedFragment).commit();
    }

    public void addFuelDetail(Bundle fuelDetails){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("fuelDetails", fuelDetails);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
