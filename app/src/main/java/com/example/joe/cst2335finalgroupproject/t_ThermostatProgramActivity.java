package com.example.joe.cst2335finalgroupproject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Milestone 1 (Must be demonstrated December 7th):
 * X 3.	Each Activity must have a ListView to present items. Selecting an item from the ListView must show detailed information about the item selected.
 * X 7.	Each activity must have at least 1 button
 * X 8.	Each activity must have at least 1 edit text with appropriate text input method.
 *
 * Milestone 2 (Must be demonstrated December 14th):
 * X 2.	Each Activity must use a fragment in its graphical interface.
 * X 5.	Each activity must use an AsyncTask in the code. This can be to open a Database, retrieve data from a server, save data, or any other reasonable circumstance.
 * X 6.	Each activity must have at least 1 progress bar
 * X 9.	Each activity must have at least 1 Toast, Snackbar, and custom dialog notification.
 *
 * Milestone 3 (Must be demonstrated December 21st):
 * X 1.  The software must have 1 different activity written by each person in your group. The activity must be accessible by selecting a graphical icon from a Toolbar.
 * X 4.  The items listed in the ListView must be stored by the application so that appear the next time the application is launched. The user must be able to add and delete items, which would then also be stored.
 * X 10.	A help menu item that displays a dialog with the author’s name, Activity version number, and instructions for how to use the interface.
 * X 11.	There must be at least 1 other language supported by your Activity. If you are not bilingual, then you must support both British and American English (words like colour, color, neighbour, neighbor, etc).
 * If you know a language other than English, then you can support that language in your application and don’t need to support American English.
 *
 * Activity Specific Requirements:
 * •	Include an instruction window that the user can access from a menu on the navigation bar.
 * •	Users should be able to set a schedule for a thermostat. The user should be able to set rules for the temperature based on which day and time to set the temperature.  For example:
 * o	Monday 6:00 Temp -> 20
 * o	Monday 9:00 Temp -> 16
 * o	Wednesday 16:00 Temp -> 20
 * o	Wednesday 22:00 Temp -> 18
 * •	This should be displayed in a ListView. Clicking on an item from the list should show the details of the temperature rule, and let the user edit the rule and save it.
 * The user should also be allowed to edit the rule, and “save as new rule” instead of changing the selected rule. For example, clicking on “Monday 6:00 Temp -> 20” will show the day, time, and temperature.
 * If the user changes the day to Tuesday and clicks “save”, the rule will now apply to Tuesday instead of Monday. If the user changes the day to Tuesday and clicks “Save as new rule”,
 * a new rule “Tuesday 6:00 Temp -> 20” will be added, and the original “Monday 6:00 Temp -> 20” will still be in the list.
 */

public class t_ThermostatProgramActivity extends AppCompatActivity {

    public static final int DISCARD_RESULT = 10;

    public static final int SAVE_AS_NEW_RESULT = 20;
    public static final int SAVE_RESULT = 21;

    public static final int ADD_METHOD_CODE = 30;
    public static final int EDIT_METHOD_CODE = 31;

    //local ArrayList to handle the rules_list being read from the database and displayed on screen
    final ArrayList<String> rules_list = new ArrayList<>();
    final ArrayList<RelativeLayout> button_list = new ArrayList<>();

    //database references
    m_GlobalDatabaseHelper tdh;
    SQLiteDatabase db;
    Cursor c;

    //create handles to asyncTask object (database read)
    asyncRead aRead;

    //create handles to the objects on the screen
    ListView lv;
    Button addButton;
    Button editButton;
    TextView progressBarTextView;
    ProgressBar progressBar;
    RuleAdapter rulesAdapter;

    boolean isTabletOrLandscape;

    ViewHolder vh = new ViewHolder();
    //https://stackoverflow.com/questions/43429998/how-to-sort-strings-containing-day-of-week-names-in-ascending-order
    Comparator<String> dayOfWeekComparator = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("EEE");
                Date d1 = format.parse(s1);
                Date d2 = format.parse(s2);
                if (d1.equals(d2)) {
                    return 0;
                    //s1.substring(s1.indexOf(" ") + 1).compareTo(s2.substring(s2.indexOf(" ") + 1))
                } else {
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(d1);
                    cal2.setTime(d2);
                    return cal1.get(Calendar.DAY_OF_WEEK) - cal2.get(Calendar.DAY_OF_WEEK);
                }
            } catch (ParseException pe) {
                throw new RuntimeException(pe);
            }
        }
    };
    private Fragment loadedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_activity_program_thermostat);

        lv = findViewById(R.id.rulesView);

        tdh = new m_GlobalDatabaseHelper(this);
        db = tdh.getWritableDatabase();

        aRead = new asyncRead();

        progressBarTextView = findViewById(R.id.t_currentTaskTextView);
        progressBar = findViewById(R.id.progressBar);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);

        Toolbar t_Toolbar = findViewById(R.id.t_toolbar);
        setSupportActionBar(t_Toolbar);

        aRead.execute();

        rulesAdapter = new RuleAdapter(this);
        lv.setAdapter(rulesAdapter);

        isTabletOrLandscape = findViewById(R.id.t_frameLayout) != null;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle info = new Bundle();
                info.putInt("mode", 1);
                info.putString("rule", rules_list.get(position));
                info.putInt("listPosition", position);
                info.putLong("databaseID", id);

                if (isTabletOrLandscape) {
                    t_editRuleFragment editFragment = new t_editRuleFragment();
                    editFragment.setArguments(info);
                    loadedFragment = editFragment;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.t_frameLayout, editFragment)
                            .commit();
                } else {
                    Intent detailsIntent = new Intent(t_ThermostatProgramActivity.this, t_DetailView.class);
                    detailsIntent.putExtras(info);
                    startActivityForResult(detailsIntent, EDIT_METHOD_CODE);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle info = new Bundle();
                info.putInt("mode", 2);
                info.putString("rule", "");
                info.putInt("listPosition", 0);

                if (isTabletOrLandscape) {
                    t_addRuleFragment addFragment = new t_addRuleFragment();
                    addFragment.setArguments(info);
                    loadedFragment = addFragment;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.t_frameLayout, addFragment)
                            .commit();
                } else {
                    Intent detailsIntent = new Intent(t_ThermostatProgramActivity.this, t_DetailView.class);
                    detailsIntent.putExtras(info);
                    startActivityForResult(detailsIntent, ADD_METHOD_CODE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.t_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.menu_exercise:
                startActivity(new Intent(t_ThermostatProgramActivity.this, a_ActivityTrackerActivity.class));
                break;

            case R.id.menu_food:
                startActivity(new Intent(t_ThermostatProgramActivity.this, n_NutritionTrackerActivity.class));
                break;

            case R.id.menu_car:
                startActivity(new Intent(t_ThermostatProgramActivity.this, c_CarTrackerActivity.class));
                break;

            case R.id.menu_home:
                startActivity(new Intent(t_ThermostatProgramActivity.this, m_MainActivity.class));
                break;

            case R.id.menu_help:
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout rootView
                        = (LinearLayout) inflater.inflate(R.layout.t_custom_alert_dialog, null);

                TextView tvAlertMsg = rootView.findViewById(R.id.tvCarAlertMsg);
                tvAlertMsg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                tvAlertMsg.setText(getResources().getText(R.string.t_helpMenuText));

                AlertDialog.Builder builder = new AlertDialog.Builder(t_ThermostatProgramActivity.this);
                builder.setView(rootView);
                builder.setPositiveButton(getResources().getString(R.string.t_helpDoneButtonText),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }
                );

                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
        db.close();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startActivity(new Intent(this, t_ThermostatProgramActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //this is where t_DetailView Activites land when they are finished.

        if (resultCode != t_ThermostatProgramActivity.DISCARD_RESULT && data != null) {

            Bundle resultBundle = data.getExtras();

            if (requestCode == ADD_METHOD_CODE) {
                if (resultCode == SAVE_RESULT) {
                    add_SaveRule(resultBundle);
                }
            } else if (requestCode == EDIT_METHOD_CODE) {
                if (resultCode == SAVE_AS_NEW_RESULT) {
                    edit_SaveAsNew(resultBundle);
                } else if (resultCode == SAVE_RESULT) {
                    edit_SaveChanges(resultBundle);
                }
            }
        } else {
            discardMethod();
        }
    }

    void discardMethod() {
        Snackbar.make(findViewById(R.id.t_toolbar),
                "Rule Discarded",
                Snackbar.LENGTH_SHORT).show();
    }

    void add_SaveRule(Bundle resultBundle) {

        String rule = resultBundle.getString("ruleToAdd");

        rules_list.add(rule);
        ContentValues newRule = new ContentValues();
        newRule.put(m_GlobalDatabaseHelper.RULE_COL_NAME, rule);

        db.insert(m_GlobalDatabaseHelper.THERMOSTAT_TABLE_NAME, null, newRule);

        updateList();
    }

    void edit_SaveChanges(Bundle resultBundle) {

        deleteRule(resultBundle.getLong("deleteID"), resultBundle.getInt("deletePosition"));

        ContentValues newRule = new ContentValues();
        newRule.put(m_GlobalDatabaseHelper.RULE_COL_NAME, resultBundle.getString("ruleToAdd"));
        db.insert(m_GlobalDatabaseHelper.THERMOSTAT_TABLE_NAME, null, newRule);

        updateList();
    }

    void edit_SaveAsNew(Bundle resultBundle) {

        String rule = resultBundle.getString("ruleToAdd");
        rules_list.add(rule);
        ContentValues newRule = new ContentValues();
        newRule.put(m_GlobalDatabaseHelper.RULE_COL_NAME, rule);

        db.insert(m_GlobalDatabaseHelper.THERMOSTAT_TABLE_NAME, null, newRule);

        updateList();
    }

    private void deleteRule(long id, int position) {

        //remove fragment (edit / add details) if it exsits
        if (loadedFragment != null) {
            getFragmentManager().beginTransaction().remove(loadedFragment).commit();
        }

        rules_list.remove(position);
        db.delete(m_GlobalDatabaseHelper.THERMOSTAT_TABLE_NAME,
                m_GlobalDatabaseHelper.RULE_ID + " = ? ",
                new String[]{String.valueOf(id)});
        rulesAdapter.notifyDataSetChanged();

        Snackbar.make(findViewById(R.id.t_toolbar),
                "Rule Deleted",
                Snackbar.LENGTH_SHORT).show();
    }

    private void updateList() {
        Collections.sort(rules_list, dayOfWeekComparator);
        rulesAdapter.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView ruleView;
        RelativeLayout selected;
    }

    //https://stackoverflow.com/questions/20945528/android-hide-and-show-checkboxes-in-custome-listview-on-button-click
    class RuleAdapter extends ArrayAdapter<String> {

        RuleAdapter(Context ctx) {
            super(ctx, 0);
        }

        @Override
        public long getItemId(int position) {
            c = db.rawQuery(m_GlobalDatabaseHelper.THERMOSTAT_SELECT_ALL_SQL, null);
            c.moveToPosition(position);
            return c.getLong(c.getColumnIndex(m_GlobalDatabaseHelper.RULE_ID));
        }

        public int getCount() {
            return rules_list.size();
        }

        public String getItem(int position) {
            return rules_list.get(position);
        }

        @Override
        public View getView(final int position, View recycled, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View layout;

            layout = inflater.inflate(R.layout.t_rule_row, null);

            vh.ruleView = layout.findViewById(R.id.ruleTextView);
            vh.ruleView.setText(getItem(position));

            vh.selected = layout.findViewById(R.id.t_deleteRuleRowButton);

            vh.selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = rulesAdapter.getItemId(position);
                    deleteRule(id, position);
                }
            });

            layout.setTag(vh);

            return layout;
        }
    }

    private class asyncRead extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            progressBarTextView.setText("Reading Database:");
            progressBar.setVisibility(View.VISIBLE);

            c = db.query(false, m_GlobalDatabaseHelper.THERMOSTAT_TABLE_NAME, new String[]{m_GlobalDatabaseHelper.RULE_COL_NAME}, null, null, null, null, null, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String rule_text = c.getString(c.getColumnIndex(m_GlobalDatabaseHelper.RULE_COL_NAME));
                //Log.i("db contained", rule);
                rules_list.add(rule_text);
                c.moveToNext();
            }


            for (int i = 0; i < 100; i++) {
                publishProgress(i);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return "Database Read Complete!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBarTextView.setText("");
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(t_ThermostatProgramActivity.this, result, Toast.LENGTH_LONG)
                    .show();

        }
    }
}