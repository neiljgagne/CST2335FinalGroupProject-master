package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
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
import android.support.annotation.NonNull;
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
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class c_CarTrackerActivity extends AppCompatActivity {

    /**
     * Priorities for each Activity (suggested complete in this order in case Prof
     * pulls us for review with no warning.)
     *
     * Milestone 1 (Must be demonstrated December 7th):
     *          3.	Each Activity must have a ListView to present items. Selecting an item from the ListView must show detailed information about the item selected.
     *          7.	Each activity must have at least 1 button
     *          8.	Each activity must have at least 1 edit text with appropriate text input method.
     *
     * Milestone 2 (Must be demonstrated December 14th):
     *          2.	Each Activity must use a fragment in its graphical interface.
     *          5.	Each activity must use an AsyncTask in the code. This can be to open a Database, retrieve data from a server, save data, or any other reasonable circumstance.
     *          6.	Each activity must have at least 1 progress bar
     *          9.	Each activity must have at least 1 Toast, Snackbar, and custom dialog notification.
     *
     * Milestone 3 (Must be demonstrated December 21st):
     *          1.  The software must have 1 different activity written by each person in your group. The activity must be accessible by selecting a graphical icon from a Toolbar.
     *          4.  The items listed in the ListView must be stored by the application so that appear the next time the application is launched. The user must be able to add and delete items, which would then also be stored.
     *          10.	A help menu item that displays a dialog with the author’s name, Activity version number, and instructions for how to use the interface.
     *          11.	There must be at least 1 other language supported by your Activity. If you are not bilingual, then you must support both British and American English (words like colour, color, neighbour, neighbor, etc).
     *          If you know a language other than English, then you can support that language in your application and don’t need to support American English.
     *
     * Activity Specific Requirements:
     •	Create an application that records buying gasoline for a car. The user can select the number of litres, price, and kilometers of the gasoline they purchased. Display the entries in a ListView. The database should store the time that the information was recorded.
     •	The user should be able to select items in the list view and edit them, or click a button to add a new purchase to the list.
     •	The application should provide information including the average price of gas for the last month, how much gasoline the user purchased last month, and how much gasoline the user purchases per month.
     *
     */

    public static final DateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);

    public static final int ADD_DETAILS_REQUEST = 1;
    public static final int EDIT_DETAILS_REQUEST = 2;

    private static final String AVERAGE = "average";
    private static final String TOTAL = "total";

    private c_EnterFuelDetailsFragment loadedFragment = null;
    private boolean frameLayoutExists;
    private Toolbar c_Toolbar;
    private LinearLayout btnHome;
    private LinearLayout btnAbout;
    private View parentLayout;
    private ListView lvPurchaseHistory;
    private LinearLayout btnAddPurchase;
    private LinearLayout btnViewFuelStats;

    private GridLayout glLoading;
    private ProgressBar pbLoadFuelDetails;
    private TextView tvLoadingPercentage;

    private ArrayList<c_FuelDetails> cFuelDetailsList;
    private FuelDetailsAdapter adapter;
    private m_GlobalDatabaseHelper carDbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_car);

        carDbHelper = new m_GlobalDatabaseHelper(c_CarTrackerActivity.this);
        db = carDbHelper.getWritableDatabase();

        findControls();
        setUpListeners();

        cFuelDetailsList = new ArrayList<>();
        adapter = new FuelDetailsAdapter(this);
        lvPurchaseHistory.setAdapter(adapter);

        new DataBaseQuery().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.c_menu, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        carDbHelper.close();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (loadedFragment != null){
            getFragmentManager().beginTransaction().remove(loadedFragment).commit();
        }
        super.onConfigurationChanged(newConfig);
        startActivity(new Intent(this, c_CarTrackerActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == Activity.RESULT_OK){

            if (requestCode == EDIT_DETAILS_REQUEST){
                Bundle extras = data.getExtras();
                Bundle fuelDetails = extras.getBundle("fuelDetails");
                updateFuelDetail(fuelDetails);
            }

            if (requestCode == ADD_DETAILS_REQUEST){
                Bundle extras = data.getExtras();
                Bundle fuelDetails = extras.getBundle("fuelDetails");
                addFuelDetail(fuelDetails);
            }
        }
    }

    protected void updateFuelDetail(Bundle fuelDetails){

        if (fuelDetails != null){
            double price = fuelDetails.getDouble("price");
            double litres = fuelDetails.getDouble("litres");
            double kilometers = fuelDetails.getDouble("kilometers");
            long longDate = fuelDetails.getLong("date");
            long id = fuelDetails.getLong("id");
            int position = fuelDetails.getInt("position");

            ContentValues contentValues = new ContentValues();
            contentValues.put(m_GlobalDatabaseHelper.KEY_PRICE, price);
            contentValues.put(m_GlobalDatabaseHelper.KEY_LITRES, litres);
            contentValues.put(m_GlobalDatabaseHelper.KEY_KILOMETERS, kilometers);
            contentValues.put(m_GlobalDatabaseHelper.KEY_DATE, longDate);

            db.update(m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE,
                    contentValues,
                    m_GlobalDatabaseHelper.KEY_ID + "=" + id,
                    null);

            c_FuelDetails fd = new c_FuelDetails(price, litres, kilometers, new Date(longDate));
            cFuelDetailsList.set(position, fd);
            adapter.notifyDataSetChanged();
            Toast.makeText(c_CarTrackerActivity.this,
                    getResources().getString(R.string.c_changesSaved),
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void addFuelDetail(Bundle fuelDetails) {
        if (fuelDetails != null) {
            double price = fuelDetails.getDouble("price");
            double litres = fuelDetails.getDouble("litres");
            double kilometers = fuelDetails.getDouble("kilometers");
            long longDate = fuelDetails.getLong("date");

            ContentValues contentValues = new ContentValues();
            contentValues.put(m_GlobalDatabaseHelper.KEY_PRICE, price);
            contentValues.put(m_GlobalDatabaseHelper.KEY_LITRES, litres);
            contentValues.put(m_GlobalDatabaseHelper.KEY_KILOMETERS, kilometers);
            contentValues.put(m_GlobalDatabaseHelper.KEY_DATE, longDate);

            db.insert(m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE,
                    "",
                    contentValues);

            c_FuelDetails fd = new c_FuelDetails(price, litres, kilometers, new Date(longDate));
            cFuelDetailsList.add(fd);
            adapter.notifyDataSetChanged();
            Toast.makeText(c_CarTrackerActivity.this,
                    getResources().getString(R.string.c_detailsAdded),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFuelDetail(long id, int position){

        // remove fragment (edit / add details) if it exsits
        if (loadedFragment != null){
            getFragmentManager().beginTransaction().remove(loadedFragment).commit();
        }

        cFuelDetailsList.remove(position);
        db.delete(m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE,
                m_GlobalDatabaseHelper.KEY_ID + "=" + id,
                null);
        adapter.notifyDataSetChanged();

        Snackbar.make(parentLayout,
                getResources().getString(R.string.c_DeleteSuccessful),
                Snackbar.LENGTH_LONG).show();
    }

    // https://stackoverflow.com/questions/28171256/android-asynctask-that-fills-an-adapter-for-a-listview
    private class DataBaseQuery extends AsyncTask<String, Integer, ArrayList<c_FuelDetails>>{

        @Override
        protected ArrayList<c_FuelDetails> doInBackground(String[] args){

            //testFillDB();
            cursor = db.rawQuery(m_GlobalDatabaseHelper.C_SELECT_ALL_SQL, null);

            // build an array list in the background and pass it back to the GUI thread
            //  after the resource intense processing is complete
            ArrayList<c_FuelDetails> detailsList = new ArrayList<>();

            // used double to perform division and display overall progress
            double totalRecords = cursor.getCount();
            double counter = 0;

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                try {
                    double price = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_PRICE));
                    double litres = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_LITRES));
                    double kilometers = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_KILOMETERS));
                    long longDateRepresentation = cursor.getLong(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_DATE));
                    Date date = new Date(longDateRepresentation);

                    c_FuelDetails details = new c_FuelDetails(price, litres, kilometers, date);
                    detailsList.add(details);

                    Integer progress = (int )Math.round((++counter / totalRecords) * 100);
                    publishProgress(progress);

                    Thread.sleep(100);

                    cursor.moveToNext();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return detailsList;
        }

        @Override
        protected void onProgressUpdate(Integer[] value){
            int progress = value[0];

            glLoading.setVisibility(View.VISIBLE);
            tvLoadingPercentage.setText(String.valueOf(progress).concat("%"));
            pbLoadFuelDetails.setProgress(progress);
        }

        @Override
        protected void onPostExecute(ArrayList<c_FuelDetails> details){
            cFuelDetailsList.clear();
            cFuelDetailsList.addAll(details);
            adapter.notifyDataSetChanged();
            glLoading.setVisibility(View.GONE);
            lvPurchaseHistory.setVisibility(View.VISIBLE);
        }
    }

    private class FuelDetailsAdapter extends ArrayAdapter<c_FuelDetails> {

        private FuelDetailsAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getCount() {
            return cFuelDetailsList.size();
        }

        @Override
        public c_FuelDetails getItem(int position) {
            return cFuelDetailsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (cursor == null)
                throw new NullPointerException("ERROR: cursor is null");

            cursor = db.rawQuery(m_GlobalDatabaseHelper.C_SELECT_ALL_SQL, null);
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_ID));
        }

        // https://stackoverflow.com/questions/17525886/listview-with-add-and-delete-buttons-in-each-row-in-android
        @Override
        @NonNull
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if (view == null){
                LayoutInflater inflater = c_CarTrackerActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.c_fuel_details_summary, parent, false);
            }

            TableRow fuelDetailRow = view.findViewById(R.id.fuelDetailRow);
            if ((position % 2) == 0){
                fuelDetailRow.setBackgroundColor(getResources().getColor(R.color.c_rowWhite));
            } else {
                fuelDetailRow.setBackgroundColor(getResources().getColor(R.color.c_rowBlue));
            }

            RelativeLayout btnDeleteFuelDetails = view.findViewById(R.id.btnDeleteFuelDetails);
            btnDeleteFuelDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater inflater = getLayoutInflater();
                    LinearLayout rootView
                            = (LinearLayout) inflater.inflate(R.layout.c_custom_alert_dialog, null);

                    ((TextView)rootView.findViewById(R.id.tvCarAlertMsg))
                            .setText(getResources().getString(R.string.c_AlertDeleteDetailsMsg));

                    AlertDialog.Builder builder = new AlertDialog.Builder(c_CarTrackerActivity.this);
                    builder.setView(rootView);
                    builder.setPositiveButton(getResources().getString(R.string.c_Yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                long id = adapter.getItemId(position);
                                deleteFuelDetail(id, position);
                            }
                        }
                    );

                    builder.setNegativeButton(getResources().getString(R.string.c_No),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {}
                            }
                    );

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            c_FuelDetails details = getItem(position);

            if (details != null){
                TextView tvPrice = view.findViewById(R.id.tvPrice);
                tvPrice.setText(String.valueOf(details.getPrice()));

                TextView tvLitres = view.findViewById(R.id.tvLitres);
                tvLitres.setText(String.valueOf(details.getLitres()));

                TextView tvKilometers = view.findViewById(R.id.tvKilometers);
                tvKilometers.setText(String.valueOf(details.getKilometers()));

                TextView tvDate = view.findViewById(R.id.tvDate);
                tvDate.setText(DD_MM_YYYY.format(details.getDate()));
            }
            return view;
        }
    }

    private void findControls(){
        frameLayoutExists = (findViewById(R.id.flEnterFuelDetailsHolder) != null);
        c_Toolbar = findViewById(R.id.c_Toolbar);
        setSupportActionBar(c_Toolbar);
        parentLayout = findViewById(R.id.fuelDetailsParent);
        lvPurchaseHistory = findViewById(R.id.lvPurchaseHistory);
        btnAddPurchase = findViewById(R.id.btnAddPurchase);
        btnViewFuelStats = findViewById(R.id.btnViewFuelStats);
        pbLoadFuelDetails = findViewById(R.id.pbLoadFuelDetails);
        glLoading = findViewById(R.id.glLoading);
        tvLoadingPercentage = findViewById(R.id.tvLoadingPercentage);
    }

    private void setUpListeners(){
        btnAddPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // landscape orientation
                if (frameLayoutExists){
                    Bundle fragmentDetails = new Bundle();
                    fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnAddPurchase));
                    fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_AddDetailsTitle));

                    c_EnterFuelDetailsFragment addFragment = new c_EnterFuelDetailsFragment();
                    addFragment.setArguments(fragmentDetails);

                    // cache the fragment so it can be removed
                    loadedFragment = addFragment;

                    getFragmentManager().beginTransaction()
                            .replace(R.id.flEnterFuelDetailsHolder, addFragment).commit();
                }

                // portrait orientation
                else {
                    Intent intent = new Intent(c_CarTrackerActivity.this,
                            c_AddFuelDetailsActivity.class);
                    startActivityForResult(intent, ADD_DETAILS_REQUEST);
                }
            }
        });


        btnViewFuelStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c_CarTrackerActivity.this,
                        c_FuelStatisticsActivity.class);

                Bundle data = new Bundle();
                data.putParcelableArrayList("gasPurchasesPerMonth", getPrevGasPurchasesByMonth());
                data.putDouble("prevMonthGasPriceAvg", getPrevMonthGasStat(AVERAGE));
                data.putDouble("prevMonthGasPriceTot", getPrevMonthGasStat(TOTAL));

                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        lvPurchaseHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                c_FuelDetails details = cFuelDetailsList.get(position);
                Bundle fuelDetails = new Bundle();
                fuelDetails.putDouble("price", details.getPrice());
                fuelDetails.putDouble("litres", details.getLitres());
                fuelDetails.putDouble("kilometers", details.getKilometers());
                fuelDetails.putLong("date", details.getDate().getTime());
                fuelDetails.putLong("id", id);
                fuelDetails.putInt("position", position);

                // the device is in landscape mode
                if (frameLayoutExists){
                    Bundle fragmentDetails = new Bundle();
                    fragmentDetails.putString("btnText", getResources().getString(R.string.c_BtnSaveDetails));
                    fragmentDetails.putString("fragmentTitle", getResources().getString(R.string.c_EditDetailsTitle));
                    fragmentDetails.putBundle("fuelDetails", fuelDetails);

                    c_EnterFuelDetailsFragment editFragment = new c_EnterFuelDetailsFragment();
                    editFragment.setArguments(fragmentDetails);

                    // cache the fragment so it can be removed
                    loadedFragment = editFragment;

                    getFragmentManager().beginTransaction()
                            .replace(R.id.flEnterFuelDetailsHolder, editFragment).commit();
                }

                // the device is in portrait mode
                else {
                    Intent intent = new Intent(c_CarTrackerActivity.this, c_EditFuelDetailsActivity.class);
                    intent.putExtra("fuelDetails", fuelDetails);
                    startActivityForResult(intent, EDIT_DETAILS_REQUEST);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch(menuItem.getItemId()){

            case R.id.menu_exercise:
                startActivity(new Intent(c_CarTrackerActivity.this, a_ActivityTrackerActivity.class));
                break;

            case R.id.menu_food:
                startActivity(new Intent(c_CarTrackerActivity.this, n_NutritionTrackerActivity.class));
                break;

            case R.id.menu_thermostat:
                startActivity(new Intent(c_CarTrackerActivity.this, t_ThermostatProgramActivity.class));
                break;

            case R.id.menu_home:
                startActivity(new Intent(c_CarTrackerActivity.this, m_MainActivity.class));
                break;

            case R.id.menu_help:
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout rootView
                        = (LinearLayout) inflater.inflate(R.layout.c_custom_alert_dialog, null);

                TextView tvAlertMsg = rootView.findViewById(R.id.tvCarAlertMsg);
                tvAlertMsg.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                tvAlertMsg.setText(getResources().getText(R.string.c_helpMenu));

                AlertDialog.Builder builder = new AlertDialog.Builder(c_CarTrackerActivity.this);
                builder.setView(rootView);
                builder.setPositiveButton(getResources().getString(R.string.c_done),
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

    // STATISTICS FUNCTIONS
    private ArrayList<c_FuelStats> getPrevGasPurchasesByMonth(){
        ArrayList<c_FuelStats> purchases = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        cursor = db.rawQuery(m_GlobalDatabaseHelper.C_SELECT_ALL_SQL, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            long lDate = cursor.getLong(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_DATE));
            calendar.setTime(new Date(lDate));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            String monthYear = getResources().getStringArray(R.array.c_months)[month] + " " + String.valueOf(year);

            double purchasePrice = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_PRICE))
                    * cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_LITRES));

            c_FuelStats stats;

            // no entries or current monthYear is different from the last
            if (purchases.isEmpty() || !purchases.get(purchases.size()-1).getMonthYear().equals(monthYear)){
                stats = new c_FuelStats(monthYear, purchasePrice);
                purchases.add(stats);
            } else {
                stats = purchases.get(purchases.size()-1);
                stats.setTotalPurchases(stats.getTotalPurchases() + purchasePrice);
            }

            cursor.moveToNext();
        }
        return purchases;
    }

    private double getPrevMonthGasStat(String stat){
        String table = m_GlobalDatabaseHelper.FUEL_DETAILS_TABLE;
        String where = m_GlobalDatabaseHelper.KEY_DATE + " >= ? AND " + m_GlobalDatabaseHelper.KEY_DATE + " <= ?";
        String[] whereArgs = {
                String.valueOf(getFirstTimestampOfPrevMonth()),
                String.valueOf(getLastTimestampOfPrevMonth())
        };

        cursor = db.query(table, null, where, whereArgs, null, null, null);
        cursor.moveToFirst();

        double gasPriceSum = 0;
        double totalPrice = 0;

        while(!cursor.isAfterLast()){
            double gasPrice = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_PRICE));
            double litres = cursor.getDouble(cursor.getColumnIndex(m_GlobalDatabaseHelper.KEY_LITRES));

            gasPriceSum += gasPrice;
            totalPrice += (gasPrice * litres);
            cursor.moveToNext();
        }

        if (stat.equals(AVERAGE) && cursor.getCount() != 0){
            return (gasPriceSum / cursor.getCount());
        }
        else if (stat.equals(TOTAL)){
            return totalPrice;
        }
        return -1; // no results
    }

    private long getFirstTimestampOfPrevMonth(){
        Calendar calendar = getPrevMonthAndYear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date firstDateOfPrevMonth = calendar.getTime();
        return firstDateOfPrevMonth.getTime();
    }

    // java2s getthelastdayofamonth
    private long getLastTimestampOfPrevMonth(){
        Calendar calendar = getPrevMonthAndYear();
        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date lastDayOfPrevMonth = calendar.getTime();
        return lastDayOfPrevMonth.getTime();
    }

    private Calendar getPrevMonthAndYear(){
        Calendar calendar = Calendar.getInstance();
        int currMonth = calendar.get(Calendar.MONTH);
        int prevMonth = currMonth == 0 ? 11 : currMonth-1;
        int currYear = calendar.get(Calendar.YEAR);
        int prevYear = currMonth == 0 ? currYear-1 : currYear;
        calendar.set(Calendar.MONTH, prevMonth);
        calendar.set(Calendar.YEAR, prevYear);
        return calendar;
    }
}