package com.example.joe.cst2335finalgroupproject;

import android.app.AlertDialog;
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
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class n_NutritionTrackerActivity extends AppCompatActivity {

    protected final static String ACTIVITY_NAME = "NutritionTracker";
    ListView nutritionListView;
    m_GlobalDatabaseHelper globalDatabaseHelper;
    SQLiteDatabase db;
    FoodAdapter listViewAdapter;
    ArrayList<String> foodArrayList = new ArrayList<>();
    Button addFoodButton;
    boolean frameLayoutExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.n_activity_nutrition);

        nutritionListView = findViewById(R.id.nutrition_listview);
        globalDatabaseHelper = new m_GlobalDatabaseHelper(this);
        db = globalDatabaseHelper.getWritableDatabase();

        listViewAdapter = new FoodAdapter(this);
        nutritionListView.setAdapter(listViewAdapter);

        LoadItemsQuery populateList = new LoadItemsQuery();
        populateList.execute();

        Toolbar myToolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(myToolbar);

        addFoodButton = findViewById(R.id.n_addFoodButton);
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItem();
            }
        });


        // STATS -----------------------------------------------------------------------------------

        Button statsButton = findViewById(R.id.n_statsButton);
        statsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder statsBuilder = new AlertDialog.Builder(n_NutritionTrackerActivity.this);
                final View statsView = getLayoutInflater().inflate(R.layout.n_activity_nutrition_stats, null);
                statsBuilder.setView(statsView);
                final AlertDialog dialog = statsBuilder.create();

                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String todaysDate = formatter.format(today);

                int numItemsToday = 0;
                int runningCalories = 0;
                int runningFat = 0;
                int runningCarbs = 0;

                Cursor dateCursor = db.rawQuery("SELECT  * FROM " + m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME + " WHERE " + m_GlobalDatabaseHelper.FOOD_DATE_COL_NAME + " = '" + todaysDate + "';", null);

                int cnt = dateCursor.getCount();
                if (dateCursor != null)
                        dateCursor.moveToFirst();
                if (cnt > 0) {
                    for (dateCursor.moveToFirst(); !dateCursor.isAfterLast(); dateCursor.moveToNext()) {
                        if (dateCursor.getString(dateCursor.getColumnIndex(m_GlobalDatabaseHelper.FOOD_DATE_COL_NAME)).matches(todaysDate)) {
                            numItemsToday += 1;
                            runningCalories += (int)Integer.parseInt(dateCursor.getString(dateCursor.getColumnIndex(m_GlobalDatabaseHelper.CALORIES_COL_NAME)));
                            runningFat += (int)Integer.parseInt(dateCursor.getString(dateCursor.getColumnIndex(m_GlobalDatabaseHelper.FAT_COL_NAME)));
                            runningCarbs += (int)Integer.parseInt(dateCursor.getString(dateCursor.getColumnIndex(m_GlobalDatabaseHelper.CARB_COL_NAME)));
                        }
                    }
                    dateCursor.close();
                    dialog.setMessage("Number of items logged today:" + numItemsToday + "\nCalories logged today: " + runningCalories + "\nAverage calories per food item: " + runningCalories/numItemsToday + "\nFat content logged today: " + runningFat + "\nCarbs logged today: " + runningCarbs);

                } else {
                    dialog.setMessage(getString(R.string.n_NoFood));

                }

                dialog.setTitle(getString(R.string.n_StatsTitle));
                dialog.setButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });



        // LISTVIEW ONCLICK ------------------------------------------------------------------------

        if (findViewById(R.id.wideScreenFrameLayout) != null) {
            frameLayoutExists = true;
            Log.i(ACTIVITY_NAME, "Tablet - FrameLayout is visible.");
        } else {
            frameLayoutExists = false;
            Log.i(ACTIVITY_NAME, "FrameLayout is NOT visible.");
        }

        nutritionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(ACTIVITY_NAME, "ListView was clicked");
                Log.i(ACTIVITY_NAME, "Click on: " + position);

                Cursor cursor = db.rawQuery(m_GlobalDatabaseHelper.N_SELECT_ALL_SQL, null);

                if (cursor != null)
                    cursor.moveToFirst();

                cursor.moveToPosition(position);

                Bundle bundle = new Bundle();

                bundle.putInt("id", cursor.getInt(cursor.getColumnIndex(m_GlobalDatabaseHelper.FOOD_ID)));
                bundle.putString("item", cursor.getString(cursor.getColumnIndex(m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME)));

                if (!cursor.getString(2).isEmpty()) {
                    bundle.putString("calories", cursor.getString(cursor.getColumnIndex(m_GlobalDatabaseHelper.CALORIES_COL_NAME)));
                } else {
                    bundle.putString("calories", "");
                }
                if (!cursor.getString(3).isEmpty()) {
                    bundle.putString("carbs", cursor.getString(cursor.getColumnIndex(m_GlobalDatabaseHelper.CARB_COL_NAME)));
                } else {
                    bundle.putString("carbs", "");
                }
                if (!cursor.getString(4).isEmpty()) {
                    bundle.putString("fat", cursor.getString(cursor.getColumnIndex(m_GlobalDatabaseHelper.FAT_COL_NAME)));
                } else {
                    bundle.putString("fat", "");
                }

                if (!cursor.getString(5).isEmpty()) {
                    bundle.putString("comment", cursor.getString(cursor.getColumnIndex(m_GlobalDatabaseHelper.COMMENTS_COL_NAME)));
                } else {
                    bundle.putString("comment", "");
                }

                bundle.putBoolean("isLandscape", frameLayoutExists);
                bundle.putInt("position", position);

                if (!frameLayoutExists) {
                    Intent detailsIntent = new Intent(n_NutritionTrackerActivity.this, n_NutritionDetail.class);
                    detailsIntent.putExtras(bundle);
                    startActivityForResult(detailsIntent, 10);

                } else {
                    n_NutritionFragment nutritionFragment = new n_NutritionFragment();
                    nutritionFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.wideScreenFrameLayout, nutritionFragment).commit();
                }

            }
        });
    }

    // TOOLBAR -------------------------------------------------------------------------------------

    /**
     * Inflates menu
     * @param m Menu
     * @return Boolean true
     */
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.n_nutrition, m);
        return true;
    }

    /**
     * Toolbar handler
     * @param menuItem item clicked
     * @return Boolean rue
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_exercise:
                startActivity(new Intent(n_NutritionTrackerActivity.this, a_ActivityTrackerActivity.class));
                break;
            case R.id.menu_car:
                startActivity(new Intent(n_NutritionTrackerActivity.this, c_CarTrackerActivity.class));
                break;
            case R.id.menu_home:
                startActivity(new Intent(n_NutritionTrackerActivity.this, m_MainActivity.class));
                break;
            case R.id.menu_help:
                final AlertDialog aboutDialog = new AlertDialog.Builder(n_NutritionTrackerActivity.this).create();
                aboutDialog.setTitle(R.string.n_AboutTitle);
                aboutDialog.setMessage(getResources().getString(R.string.n_AboutMessage));
                aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.n_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                aboutDialog.dismiss();
                            }
                        });
                aboutDialog.show();
                break;
        }
        return true;
    }


    // ADD ITEM TO LISTVIEW ------------------------------------------------------------------------

    /**
     * Verifies the input has a name of food and adds to database if so
     */
    public void addItem() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(n_NutritionTrackerActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.n_activity_nutrition_dialog, null);

        builder.setView(logView);
        final AlertDialog dialog = builder.create();
        final EditText nameField = logView.findViewById(R.id.nutrition_name_field);

        Button submitNewActivity = logView.findViewById(R.id.nutrition_log_new_button);
        Button cancelNewActivity = logView.findViewById(R.id.nutrition_cancel);
        final ProgressBar myProgressBar = logView.findViewById(R.id.progressBar);

        submitNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameField.getText().toString().length() < 1) {
                    showValidationDialog();
                } else {
                    EditText namefield = logView.findViewById(R.id.nutrition_name_field);
                    EditText caloriesfield = logView.findViewById(R.id.nutrition_calories_field);
                    EditText carbsfield = logView.findViewById(R.id.nutrition_carbs_field);
                    EditText fatfield = logView.findViewById(R.id.nutrition_fat_field);
                    EditText commentfield = logView.findViewById(R.id.nutrition_comment_field);

                    String itemName = namefield.getText().toString();
                    String itemCalories = caloriesfield.getText().toString();
                    String itemCarbs = carbsfield.getText().toString();
                    String itemFat = fatfield.getText().toString();
                    String itemComment = commentfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME, itemName);
                    values.put(m_GlobalDatabaseHelper.CALORIES_COL_NAME, itemCalories);
                    values.put(m_GlobalDatabaseHelper.CARB_COL_NAME, itemCarbs);
                    values.put(m_GlobalDatabaseHelper.FAT_COL_NAME, itemFat);
                    values.put(m_GlobalDatabaseHelper.COMMENTS_COL_NAME, itemComment);

                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(today);
                    values.put(m_GlobalDatabaseHelper.FOOD_DATE_COL_NAME, dateString);

                    db.insert(m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME, null, values);

                    Cursor cursor = db.query(m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME, new String[]{m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME}, null, null, null, null, null);
                    int colIndex = cursor.getColumnIndex(m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME);

                    Cursor c = db.rawQuery("SELECT  * FROM " + m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME + ";", null);
                    int cnt = c.getCount();
                    if (cnt > 0) {
                        for (cursor.moveToLast(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            String value = cursor.getString(colIndex);
                            foodArrayList.add(value);
                            listViewAdapter.notifyDataSetChanged();
                        }
                        c.close();
                    }

                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.n_FoodAddedToast,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        cancelNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }



    // EDIT LISTVIEW -------------------------------------------------------------------------------

    /**
     * Edits item currently existing in database
     * @param id Item ID in database
     * @param position Item position in ListView
     */
    public void editItem(int id, int position) {
        final int itemID = id ;
        final int itemPosition = position;
        final AlertDialog.Builder builder = new AlertDialog.Builder(n_NutritionTrackerActivity.this);
        final View logView = getLayoutInflater().inflate(R.layout.n_activity_nutrition_dialog, null);

        builder.setView(logView);
        final AlertDialog dialog = builder.create();
        final EditText nameField = logView.findViewById(R.id.nutrition_name_field);

        Button submitNewActivity = logView.findViewById(R.id.nutrition_log_new_button);
        Button cancelNewActivity = logView.findViewById(R.id.nutrition_cancel);

        submitNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameField.getText().toString().length() < 1) {
                    showValidationDialog();
                } else {

                    EditText namefield = logView.findViewById(R.id.nutrition_name_field);
                    EditText caloriesfield = logView.findViewById(R.id.nutrition_calories_field);
                    EditText carbsfield = logView.findViewById(R.id.nutrition_carbs_field);
                    EditText fatfield = logView.findViewById(R.id.nutrition_fat_field);
                    EditText commentfield = logView.findViewById(R.id.nutrition_comment_field);

                    String itemName = namefield.getText().toString();
                    String itemCalories = caloriesfield.getText().toString();
                    String itemCarbs = carbsfield.getText().toString();
                    String itemFat = fatfield.getText().toString();
                    String itemComment = commentfield.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME, itemName);
                    values.put(m_GlobalDatabaseHelper.CALORIES_COL_NAME, itemCalories);
                    values.put(m_GlobalDatabaseHelper.CARB_COL_NAME, itemCarbs);
                    values.put(m_GlobalDatabaseHelper.FAT_COL_NAME, itemFat);
                    values.put(m_GlobalDatabaseHelper.COMMENTS_COL_NAME, itemComment);

                    String strFilter = "FOOD_ID=" + itemID;
                    db.update(m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME, values, strFilter, null);

                    foodArrayList.set(itemPosition, itemName);
                    listViewAdapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), R.string.n_EditToastMessage,
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        cancelNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    // DELETE ITEM ---------------------------------------------------------------------------------

    /**
     * Deletes item from database and updates the ListView
     * @param id item ID in database
     * @param position item position in ListView
     */
    public void deleteItem(int id, int position) {
        final int itemID = id;
        final int itemPosition = position;
        db.execSQL("DELETE FROM " + m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME + " WHERE " + m_GlobalDatabaseHelper.FOOD_ID + " = " + id + ";");
        foodArrayList.remove(itemPosition);
        listViewAdapter.notifyDataSetChanged();
    }


    // ASYNC TASK ----------------------------------------------------------------------------------

    /**
     * Adapter for ListView
     */
    protected class FoodAdapter extends ArrayAdapter<String> {
        public FoodAdapter(Context c) {
            super(c, 0);
        }

        public int getCount() {
            return foodArrayList.size();
        }

        public String getItem(int position) {
            return foodArrayList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = n_NutritionTrackerActivity.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.n_activity_nutrition_list, null);
            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }
    }


    // ADD ITEM VALIDATION -------------------------------------------------------------------------

    /**
     * Output upon entering an item without a name
     */
    public void showValidationDialog() {
        final AlertDialog validateDialog = new AlertDialog.Builder(n_NutritionTrackerActivity.this).create();
        validateDialog.setTitle(R.string.n_ValidateDialogTitle);
        validateDialog.setMessage(getResources().getString(R.string.n_ValidateDialogMessage));
        validateDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.n_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        validateDialog.dismiss();
                    }
                });
        validateDialog.show();
    }


    // ADD ITEM ------------------------------------------------------------------------------------

    /**
     * AsyncTask loading the items from the database
     */
    protected class LoadItemsQuery extends AsyncTask<String, Integer, String> {
        String recent = "";
        ProgressBar pBar = findViewById(R.id.progressBar);

        /**
         * Runs through database items loading them into the ArrayList
         * @param strings
         * @return null
         */
        @Override
        protected String doInBackground(String... strings) {
            Log.i(ACTIVITY_NAME, "In AddItemQuery");
            pBar.setProgress(25);
            Cursor cursor = db.query(m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME, new String[]{m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME}, null, null, null, null, null);
            int colIndex = cursor.getColumnIndex(m_GlobalDatabaseHelper.FOOD_ITEM_COL_NAME);

            Cursor c = db.rawQuery("SELECT  * FROM " + m_GlobalDatabaseHelper.NUTRITION_TABLE_NAME + ";", null);
            int cnt = c.getCount();
            pBar.setProgress(50);
            if (cnt > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String value = cursor.getString(colIndex);
                    foodArrayList.add(value);
                    listViewAdapter.notifyDataSetChanged();
                    if (cursor.isLast()) {
                        pBar.setProgress(75);
                        recent = cursor.getString(colIndex);
                    }
                }
                c.close();
                pBar.setProgress(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            pBar.setVisibility(View.VISIBLE);
            pBar.setProgress(0);
        }

        /**
         * Displays Snackbar showing the most recently logged item of food
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            if (recent.length() < 1) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Welcome to Nutrition Tracker", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Most recently logged food: " + recent, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    /**
     * Phone has been turned
     * @param newConfig Phone turned
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startActivity(new Intent(this, n_NutritionTrackerActivity.class));
    }

    /**
     * Redirects information from fragment
     * @param requestCode Request code
     * @param resultCode Result code - 10 (Edit item), 20 (Delete item)
     * @param data Carries data from fragment
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("n_NutritionTracker", "In onActivityResult");

        if (requestCode == 10 && resultCode == 10) {
            int returnValue = data.getIntExtra("id", 0);
            int positionValue = data.getIntExtra("position", 0);
            editItem(returnValue, positionValue);
        }

        if (requestCode == 10 && resultCode == 20) {
            int returnValue = data.getIntExtra("id", 0);
            int positionValue = data.getIntExtra("position", 0);
            deleteItem(returnValue, positionValue);
        }
    }

    /**
     * Closes database and activity
     */
    public void onDestroy() {
        db.close();
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }

}
