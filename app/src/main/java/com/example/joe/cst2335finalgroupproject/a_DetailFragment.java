package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Owner on 12/14/2017.
 */

public class a_DetailFragment extends Fragment {
    View v;
    Activity parent;
    long colID;
    String activityType;
    int duration;
    long longDate;
    Date date;
    String comment;
    Bundle passedInfo;
    int pos;
    Spinner typeSpinner;
    public void onAttach(Activity activity){
        super.onAttach(activity);
        passedInfo = getArguments();
        if(passedInfo!=null){
            //grab data
            Log.i("a_DetailFragment","Unpack your bundle!");
            activityType=passedInfo.getString("Activity");
            duration=passedInfo.getInt("Duration");
            comment=passedInfo.getString("Comments");
            longDate=passedInfo.getLong("Date");
            date=new Date(longDate);
            colID=passedInfo.getLong("Database_id");
            pos=passedInfo.getInt("Position");
        }
        parent=activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.a_detail_fragment,null);
        Button deleteButton = (Button) v.findViewById(R.id.a_detailDeleteButton);
        Button confirmButton = (Button)v.findViewById(R.id.a_detailConfirmButton);
        Button cancelButton = (Button) v.findViewById(R.id.a_detailCancelButton);

        initializeTypeSpinner();
       // final EditText typeEdit = (EditText)v.findViewById(R.id.a_detailTypeText);
        final EditText durationEdit = (EditText)v.findViewById(R.id.a_detailDurationText);
        final EditText commentEdit = (EditText)v.findViewById(R.id.a_detailCommentText);
        TextView dateText =(TextView)v.findViewById(R.id.a_detailTimeText);

       // typeEdit.setText(activityType);
        durationEdit.setText(String.valueOf(duration));
        commentEdit.setText(comment);
        dateText.setText(a_ActivityTrackerActivity.ddmmyy.format(date));

        switch (parent.getLocalClassName()) {
            case "a_DetailView":
                Log.i("detail_FRAGMENT","IMA PHOWN");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("a_DetailFragment", "Clicked Delete");
                    Intent deleteIntent = new Intent();
                    Bundle deleteBundle = new Bundle();
                    deleteBundle.putLong("deleteID", colID);
                    deleteBundle.putInt("deletePos", pos);
                    deleteIntent.putExtras(deleteBundle);
                    getActivity().setResult(10, deleteIntent);
                    getActivity().finish();
                }
            });

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("a_DetailFragment", "Clicked Confirm");

                    Intent confirmIntent = new Intent();
                    Bundle confirmBundle = new Bundle();

                    confirmBundle.putLong("confirmID",colID);
                    confirmBundle.putInt("confirmPos",pos);
                    confirmBundle.putString("confirmType",typeSpinner.getSelectedItem().toString());
                    confirmBundle.putInt("confirmDuration",Integer.valueOf(durationEdit.getText().toString()));
                    confirmBundle.putString("confirmComment",commentEdit.getText().toString());
                    confirmBundle.putLong("confirmDate",longDate);

                    confirmIntent.putExtras(confirmBundle);
                    getActivity().setResult(20, confirmIntent);
                    getActivity().finish();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("a_DetailFragment", "Clicked Cancel");
                    getActivity().finish();
                }
            });
            break;
            case "a_ActivityTrackerActivity":
                Log.i("detail_FRAGMENT","IMA TARBLET");
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a_ActivityTrackerActivity.deleteEntry(colID,pos);
                        parent.getFragmentManager().beginTransaction().remove(a_DetailFragment.this).commit();
                    }
                });

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle confirmBundle = new Bundle();

                        confirmBundle.putLong("confirmID",colID);
                        confirmBundle.putInt("confirmPos",pos);
                        confirmBundle.putString("confirmType",typeSpinner.getSelectedItem().toString());
                        confirmBundle.putInt("confirmDuration",Integer.valueOf(durationEdit.getText().toString()));
                        confirmBundle.putString("confirmComment",commentEdit.getText().toString());
                        confirmBundle.putLong("confirmDate",longDate);

                        a_ActivityTrackerActivity.updateActivity(confirmBundle);
                        parent.getFragmentManager().beginTransaction().remove(a_DetailFragment.this).commit();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.getFragmentManager().beginTransaction().remove(a_DetailFragment.this).commit();
                    }
                });
                break;
        }
        return v;
    }
    public void initializeTypeSpinner(){
         typeSpinner = v.findViewById(R.id.a_typeSpinner);
        List<String> types = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.a_typeArray)));
        final  ArrayAdapter<String> typeAdapter  = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_spinner_item,types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelection(typeAdapter.getPosition(activityType));

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String debugString=typeAdapter.getItem(position);
                Log.i("DetailFragment","You want to go: "+debugString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
