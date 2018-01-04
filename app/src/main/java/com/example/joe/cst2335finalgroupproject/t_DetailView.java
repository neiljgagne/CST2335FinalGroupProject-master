package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class t_DetailView extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.t_fragment_detail_view);

        Bundle passedInfo = getIntent().getExtras();

        int mode = passedInfo.getInt("mode");

        if (mode == 1) {    //edit
            //setContentView(R.layout.t_edit_rule);

            t_editRuleFragment fragment = new t_editRuleFragment();
            fragment.setArguments(passedInfo);
            getFragmentManager().beginTransaction()
                    .replace(R.id.t_frameLayout, fragment)
                    .commit();

        } else if (mode == 2) { //add
            // setContentView(R.layout.t_add_rule);

            t_addRuleFragment fragment = new t_addRuleFragment();
            fragment.setArguments(passedInfo);
            getFragmentManager().beginTransaction()
                    .replace(R.id.t_frameLayout, fragment)
                    .commit();

        } else {
            Log.i("Error", "You really should not be here.");
        }
    }
}
