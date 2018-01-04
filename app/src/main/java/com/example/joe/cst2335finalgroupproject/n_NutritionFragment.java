package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class n_NutritionFragment extends Fragment {

    private Activity myActivity;

    /**
     * Reads and loads food item information into fragment and sets button handlers
     * @param inflater Layout inflater
     * @param container Container
     * @param savedInstanceState State of activity
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final int id = getArguments().getInt("id");
        final String name = getArguments().getString("item");
        final String calories = getArguments().getString("calories");
        final String carbs = getArguments().getString("carbs");
        final String fat = getArguments().getString("fat");
        final String comment = getArguments().getString("comment");
        final Boolean frameLayoutExists = getArguments().getBoolean("isLandscape");
        final int position = getArguments().getInt("position");

        View view = inflater.inflate(R.layout.n_activity_nutrition_details, container, false);

        TextView detailsName = view.findViewById(R.id.nutrition_detail_name_value);
        TextView detailsCalories = view.findViewById(R.id.nutrition_detail_calories_value);
        TextView detailsCarbs = view.findViewById(R.id.nutrition_detail_carbs_value);
        TextView detailsFat = view.findViewById(R.id.nutrition_detail_fat_value);
        TextView detailsComment = view.findViewById(R.id.nutrition_detail_comment_value);

        Button btnEdit = view.findViewById(R.id.nutrition_detail_edit_button);
        Button btnClose = view.findViewById(R.id.nutrition_detail_cancel);
        Button btnDelete = view.findViewById(R.id.nutrition_detail_delete);

        detailsName.setText(name);
        detailsCalories.setText(calories);
        detailsCarbs.setText(carbs);
        detailsFat.setText(fat);
        detailsComment.setText(comment);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent(n_NutritionFragment.this.getActivity(), n_NutritionTrackerActivity.class);
                startActivityForResult(detailsIntent, 0);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LANDSCAPE
                if (frameLayoutExists == true) {
                    myActivity.getFragmentManager().beginTransaction().remove(n_NutritionFragment.this).commit();
                    ((n_NutritionTrackerActivity) myActivity).editItem(id, position);
                } else  {
                    // PORTRAIT
                    Bundle idBundle = new Bundle();
                    idBundle.putInt("id", id);
                    idBundle.putInt("position", position);
                    Intent resultIntent = new Intent().putExtras(idBundle);
                    myActivity.setResult(10, resultIntent);
                    myActivity.finish();   //finish closes this empty activity on phones.
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LANDSCAPE
                if (frameLayoutExists == true) {
                    myActivity.getFragmentManager().beginTransaction().remove(n_NutritionFragment.this).commit();
                    ((n_NutritionTrackerActivity) myActivity).deleteItem(id, position);
                } else  {
                    // POTRAIT
                    Bundle idBundle = new Bundle();
                    idBundle.putInt("id", id);
                    idBundle.putInt("position", position);
                    Intent resultIntent = new Intent().putExtras(idBundle);
                    myActivity.setResult(20, resultIntent);
                    myActivity.finish();   //finish closes this empty activity on phones.
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.myActivity = activity;
    }


}
