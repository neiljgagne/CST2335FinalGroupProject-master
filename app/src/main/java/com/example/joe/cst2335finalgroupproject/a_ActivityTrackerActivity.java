package com.example.joe.cst2335finalgroupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class a_ActivityTrackerActivity extends AppCompatActivity {

    public static final DateFormat ddmmyy = new SimpleDateFormat(" HH:mm dd/MM/yyyy");
    /**
     * Milestone 1 (Must be demonstrated December 7th):
     * 3.	Each Activity must have a ListView to present items. Selecting an item from the ListView must show detailed information about the item selected.
     * 7.	Each activity must have at least 1 button
     * 8.	Each activity must have at least 1 edit text with appropriate text input method.
     * Milestone 2 (Must be demonstrated December 14th):
     * 2.	Each Activity must use a fragment in its graphical interface.
     * 5.	Each activity must use an AsyncTask in the code. This can be to open a Database, retrieve data from a server, save data, or any other reasonable circumstance.
     * 6.	Each activity must have at least 1 progress bar
     * 9.	Each activity must have at least 1 Toast, Snackbar, and custom dialog notification.
     *
     * Milestone 3 (Must be demonstrated December 21st):
     * 1.  The software must have 1 different activity written by each person in your group. The activity must be accessible by selecting a graphical icon from a Toolbar.
     * 4.  The items listed in the ListView must be stored by the application so that appear the next time the application is launched. The user must be able to add and delete items, which would then also be stored.
     * 10.	A help menu item that displays a dialog with the author’s name, Activity version number, and instructions for how to use the interface.
     * 11.	There must be at least 1 other language supported by your Activity. If you are not bilingual, then you must support both British and American English (words like colour, color, neighbour, neighbor, etc).
     * If you know a language other than English, then you can support that language in your application and don’t need to support American English.
     *
     * Activity Specific Requirements:
     * The user should be able to enter physical activities they did.
     * •	The user should be able to choose from 5 different activities: Running, Walking, Biking, Swimming or Skating.
     * •	The user should be able to enter the amount of time in minutes they did the activity.
     * •	The user should also be able to add comment, like “This felt easy”, or “My knee hurts after 10 minutes”. The database should track what time the activity was entered.
     * •	You should use fragments to show a ListView displays the activities, either ordered by time or as a daily summary of activities and time exercising. Users should be able to delete or edit previous entries, like changing the activity type or time spent, or change comments.
     * •	The application should calculate how many minutes of activity the user does per month, and also show how much activity the user did last month.
     */

    final static ArrayList<a_TrackedActivity> activityList = new ArrayList<>();
    static ListView activityListView;
    static ActivityAdapter aa;
    static View parentLayout;
    //handles database references
    static m_GlobalDatabaseHelper dbHelper;
    static SQLiteDatabase db;
    private static Context context;
    //handles on screen object
    ProgressBar pb;
    boolean isPhone;
    boolean firstRun=true;
    //handle async reader
    AsyncReader aRead;
    Button addButton;
    Button statsButton;
    Spinner newTypeSpinner;
    Toolbar a_Toolbar;
    Cursor c;

    protected static void updateActivity(Bundle activityDetailsBundle) {
        String type = activityDetailsBundle.getString("confirmType");
        String comment = activityDetailsBundle.getString("confirmComment");
        int duration = activityDetailsBundle.getInt("confirmDuration");
        long id = activityDetailsBundle.getLong("confirmID");
        int pos = activityDetailsBundle.getInt("confirmPos");
        long longDate = activityDetailsBundle.getLong("confirmDate");
        Date date = new Date(longDate);

        ContentValues cv = new ContentValues();

        cv.put(m_GlobalDatabaseHelper.TYPE_COL_NAME, type);
        cv.put(m_GlobalDatabaseHelper.DURATION_COL_NAME, duration);
        cv.put(m_GlobalDatabaseHelper.NOTE_COL_NAME, comment);
        cv.put(m_GlobalDatabaseHelper.TIME_COL_NAME, longDate);

        db.update(m_GlobalDatabaseHelper.ACTIVITY_TABLE_NAME, cv, m_GlobalDatabaseHelper.WORKOUT_ID + "=" + id, null);

        a_TrackedActivity ta = new a_TrackedActivity(1, type, duration, comment, date);
        activityList.set(pos, ta);
        aa.notifyDataSetChanged();
    }

    protected static void deleteEntry(Long IDtoDelete, int posToRemove) {
        String[] whereargs = {String.valueOf(IDtoDelete)};
        Log.i("DELETING", "Attempt to delete ID " + IDtoDelete);
        int done = db.delete(m_GlobalDatabaseHelper.ACTIVITY_TABLE_NAME, m_GlobalDatabaseHelper.WORKOUT_ID + "=" + IDtoDelete, null);
        Log.i("activity tacker", "Rows affected: " + done);
        activityList.remove(posToRemove);
        aa.notifyDataSetChanged();

        Snackbar.make(parentLayout.findViewById(R.id.a_addButton), R.string.a_main_snackbarText, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.a_activity_tracker_activity);
        FrameLayout fl = findViewById(R.id.a_tabletFrameLayout);
        parentLayout=findViewById(R.id.a_activityParent);
        if(fl==null){isPhone=true;}
        pb = findViewById(R.id.a_progressBar);
        pb.setIndeterminate(true);

        a_Toolbar = findViewById(R.id.a_toolbar);
        setSupportActionBar(a_Toolbar);

        dbHelper=new m_GlobalDatabaseHelper(this);
        db=dbHelper.getWritableDatabase();

        addButton = findViewById(R.id.a_addButton);
         activityListView = findViewById(R.id.a_listView);
        statsButton=findViewById(R.id.a_main_statsButton);

         aRead=new AsyncReader();
        aRead.execute();

         aa=new ActivityAdapter(this);
        activityListView.setAdapter(aa);
        setupAddButtonOnClick();
        setupStatsButtonOnClick();



        activityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                a_TrackedActivity activityToDetail = aa.getItem(position);
                Bundle activityBundle=new Bundle();

                Log.i("ActivityTackerActivity","COLID: "+id);
                int idToSend =(int)id;
                //activityToDetail.setColKey((int)id);
                activityBundle.putInt("Position",position);
                activityBundle.putLong("Database_id",activityToDetail.getColKey());
                activityBundle.putString("Activity",activityToDetail.getType());
                activityBundle.putInt("Duration",activityToDetail.getDuration());
                activityBundle.putString("Comments",activityToDetail.getNotes());
                activityBundle.putLong("Date",activityToDetail.getTimeStamp().getTime());
                if(isPhone) {
                    Intent detailsIntent = new Intent(a_ActivityTrackerActivity.this, a_DetailView.class);
                    detailsIntent.putExtras(activityBundle);
                    startActivityForResult(detailsIntent, 1);
                }
                else{
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    a_DetailFragment dFrag = new a_DetailFragment();
                    dFrag.setArguments(activityBundle);
                    ft.replace(R.id.a_tabletFrameLayout,dFrag);
                    ft.commit();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.a_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.menu_thermostat:
                startActivity(new Intent(a_ActivityTrackerActivity.this, t_ThermostatProgramActivity.class));
                break;

            case R.id.menu_food:
                startActivity(new Intent(a_ActivityTrackerActivity.this, n_NutritionTrackerActivity.class));
                break;

            case R.id.menu_car:
                startActivity(new Intent(a_ActivityTrackerActivity.this, c_CarTrackerActivity.class));
                break;

            case R.id.menu_home:
                startActivity(new Intent(a_ActivityTrackerActivity.this, m_MainActivity.class));
                break;

            case R.id.menu_help:
                launchHelp();
                break;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent data){
        if(requestCode==1){
            if(responseCode==10){
                Log.i("ActivityTrackerActivity","Snackbar goooooo");
                Bundle toDeleteBundle=data.getExtras();
                Long toDeleteID=toDeleteBundle.getLong("deleteID");
                int toDeletePos=toDeleteBundle.getInt("deletePos");
                Log.i("ActivityTracker","You wanted to Delete ID "+toDeleteID+" at pos "+toDeletePos);
                deleteEntry(toDeleteID,toDeletePos);

            }
            else if(responseCode==20){
                Bundle toConfirmBundle=data.getExtras();
                updateActivity(toConfirmBundle);
            }


        }
    }

    protected void launchHelp(){
        AlertDialog.Builder builder= new AlertDialog.Builder(a_ActivityTrackerActivity.this);
        builder.setTitle(R.string.a_help_title).setMessage(R.string.a_help_content).setPositiveButton(R.string.a_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    protected void setupStatsButtonOnClick(){
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double avgDuration=Math.round((calcAvgDuration()*100)/100);
                double totalDuration=calcTotalDuration();
                String statsMessage=getString(R.string.a_alert_statsString,avgDuration,totalDuration);
                AlertDialog.Builder builder=new AlertDialog.Builder(a_ActivityTrackerActivity.this);
                builder.setTitle(R.string.a_alert_title).setMessage(statsMessage)
                .setPositiveButton(R.string.a_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });
    }

    protected void setupAddButtonOnClick(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dia = new Dialog(a_ActivityTrackerActivity.this);
                dia.setContentView(R.layout.a_activity_new_activity);
                dia.setTitle("New entry");


                initSpinner(dia);
                final EditText newDurationEdit = dia.findViewById(R.id.a_newDurationText);
                final EditText newCommentEdit = dia.findViewById(R.id.a_newCommentText);
                Button newCancelButton = dia.findViewById(R.id.a_newCancelButton);
                Button newConfirmButton = dia.findViewById(R.id.a_newConfirmButton);

                newCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dia.dismiss();
                    }
                });
                newConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a_TrackedActivity newAct = new a_TrackedActivity(1, newTypeSpinner.getSelectedItem().toString(), Integer.valueOf(newDurationEdit.getText().toString()), newCommentEdit.getText().toString(), new Date());
                        activityList.add(newAct);
                        ContentValues insertCV=new ContentValues();
                        insertCV.put(m_GlobalDatabaseHelper.TYPE_COL_NAME, newAct.getType());
                        insertCV.put(m_GlobalDatabaseHelper.DURATION_COL_NAME, newAct.getDuration());
                        insertCV.put(m_GlobalDatabaseHelper.NOTE_COL_NAME, newAct.getNotes());
                        insertCV.put(m_GlobalDatabaseHelper.TIME_COL_NAME, newAct.getTimeStamp().getTime());
                        db.insert(m_GlobalDatabaseHelper.ACTIVITY_TABLE_NAME, null, insertCV);


                        aa.notifyDataSetChanged();
                        dia.dismiss();
                        Toast.makeText(a_ActivityTrackerActivity.this, R.string.a_main_toastText, Toast.LENGTH_SHORT).show();
                    }
                });

                dia.show();
                Window window = dia.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    protected void initSpinner(Dialog v){
        newTypeSpinner = v.findViewById(R.id.a_newTypeSpinner);
        List<String> types = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.a_typeArray)));
        final  ArrayAdapter<String> typeAdapter  = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_spinner_item,types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTypeSpinner.setAdapter(typeAdapter);

        newTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String debugString=typeAdapter.getItem(position);
                Log.i("newActivityDialog","You just went: "+debugString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected double calcAvgDuration(){
        int total=0;
        int count=0;
        for(a_TrackedActivity ta:activityList){
            total+=ta.getDuration();
            count++;
        }
        double avg=total/count;
        return avg;
    }

    protected double calcTotalDuration(){
        int total=0;

        for(a_TrackedActivity ta:activityList){
            total+=ta.getDuration();
        }

        return total;
    }

    private class ActivityAdapter extends ArrayAdapter<a_TrackedActivity> {
        public ActivityAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return activityList.size();
        }


        public a_TrackedActivity getItem(int pos) {
            return activityList.get(pos);
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = a_ActivityTrackerActivity.this.getLayoutInflater();
            View result = null;
            result = inflater.inflate(R.layout.a_activity_row, null);

            TextView activityTypeText = result.findViewById(R.id.a_TypeText);
            TextView durationText = result.findViewById(R.id.a_DurationText);
            TextView dateText = result.findViewById(R.id.a_DateText);

            a_TrackedActivity activityToDisplay = getItem(pos);
            activityTypeText.setText(activityToDisplay.getType());
            durationText.setText(" "+Integer.toString(activityToDisplay.getDuration()));
            dateText.setText(ddmmyy.format(activityToDisplay.getTimeStamp()));


            return result;
        }
    }



    private class AsyncReader extends AsyncTask<String,Integer,ArrayList<a_TrackedActivity>>{

        protected ArrayList<a_TrackedActivity> doInBackground(String ...args){
            //an array list of activities to pass back to the GUI thread
            ArrayList<a_TrackedActivity> details = new ArrayList<a_TrackedActivity>();

            c = db.query(false, m_GlobalDatabaseHelper.ACTIVITY_TABLE_NAME, new String[]{m_GlobalDatabaseHelper.WORKOUT_ID, m_GlobalDatabaseHelper.TYPE_COL_NAME, m_GlobalDatabaseHelper.DURATION_COL_NAME, m_GlobalDatabaseHelper.NOTE_COL_NAME, m_GlobalDatabaseHelper.TIME_COL_NAME}, null, null, null, null, null, null);

            double totalRecords = c.getCount();
            double count=0;

            c.moveToFirst();
            while(!c.isAfterLast()){
                try{
                //grab all the data, put into new objects, save to list
                    int toBeCol = c.getInt(c.getColumnIndex(m_GlobalDatabaseHelper.WORKOUT_ID));
                    String toBeType = c.getString(c.getColumnIndex(m_GlobalDatabaseHelper.TYPE_COL_NAME));
                    int toBeDuration = c.getInt(c.getColumnIndex(m_GlobalDatabaseHelper.DURATION_COL_NAME));
                    String toBeComments = c.getString(c.getColumnIndex(m_GlobalDatabaseHelper.NOTE_COL_NAME));
                    Long toBeLongDate = c.getLong(c.getColumnIndex(m_GlobalDatabaseHelper.TIME_COL_NAME));
                Date toBeDate=new Date(toBeLongDate);
                a_TrackedActivity newActivity = new a_TrackedActivity(toBeCol,toBeType,toBeDuration,toBeComments,toBeDate);
                details.add(newActivity);

                Integer pbProgress =(int)Math.round((++count/totalRecords)*100);
                publishProgress(pbProgress);
                Thread.sleep(100);
                c.moveToNext();}
                catch(InterruptedException e){e.printStackTrace();}
            }
            c.close();

            //read from DB
            return details;
        }

        protected void onProgressUpdate(Integer ...values){
            super.onProgressUpdate(values);
            pb.setProgress(values[0]);
        }
        protected void onPostExecute(ArrayList<a_TrackedActivity> result){
            activityList.clear();
            for(a_TrackedActivity ta:result){
                activityList.add(ta);
            }
            aa.notifyDataSetChanged();
            pb.setVisibility(View.INVISIBLE);
        }
    }


}