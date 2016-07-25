package asri_tools.devalkhatech.com.asri.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.CaptureActivityAnyOrientation;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.helpers.SessionManager;
import asri_tools.devalkhatech.com.asri.services.SampleBC;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CoordinatorLayout coordinatorLayout;
    // DB Class to perform DB related operations
    DBController controller = new DBController(this);
    // Progress Dialog Object
    ProgressDialog prgDialog;
    TextView getUserText;
    HashMap<String, String> queryValues, userValues;
    // Button Logout
    Button btnLogout;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Typeface myFont = Typeface.createFromAsset(getAssets(), "indy.ttf");
        TextView companyName = (TextView) findViewById(R.id.company);
        companyName.setTypeface(myFont);

        getUserText = (TextView) findViewById(R.id.txtLabelName);

        btnLogout = (Button) findViewById(R.id.btn_logout);

        // Session manager
        session = new SessionManager(getApplicationContext());

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        getUserText.setText(name);

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                session.logoutUser();
            }
        });

        // Initialize Progress Dialog properties
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("synchronizes.. Please wait...");
        prgDialog.setCancelable(false);
        // BroadCase Receiver Intent Object
        Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000, pendingIntent);

        /* End Sync */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            // Transfer data from remote MySQL DB to SQLite on Android and perform Sync
            syncUsers();
            syncCustomer();
            syncItem();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void syncCustomer() {

        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        prgDialog.show();
        // Make Http call to getcustomers.php

        //client.post("http://192.168.0.106/aang/mysqlsqlitesync/getcustomers.php", params, new AsyncHttpResponseHandler() {
        client.post("http://192.168.1.33/aang/mysqlsqlitesync/getcustomers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                prgDialog.hide();
                // Update SQLite DB with response sent by getcustomers.php
                updateStatCustomers(response);

            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    /*Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();*/
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });
    }

    private void syncUsers() {
        AsyncHttpClient sales_user = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        prgDialog.show();
        // Make Http call to getuser.php

        //sales_user.post("http://192.168.0.106/aang/mysqlsqlitesync/getuser.php", params, new AsyncHttpResponseHandler() {
        sales_user.post("http://192.168.1.33/aang/mysqlsqlitesync/getuser.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                prgDialog.hide();
                // Update SQLite DB with response sent by getuser.php
                updateStatUsers(response);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                    //        Toast.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                }
            }

        });
    }

    private void syncItem() {
        AsyncHttpClient items = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        prgDialog.show();
        // Make Http call to getuser.php

        //items.post("http://192.168.0.106/aang/mysqlsqlitesync/getitem.php", params, new AsyncHttpResponseHandler() {
        items.post("http://192.168.1.33/aang/mysqlsqlitesync/getitem.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                prgDialog.hide();
                // Update SQLite DB with response sent by getuser.php
                updateStatItems(response);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
//                            Toast.LENGTH_LONG).show();
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                }
            }

        });
    }

    // Method to Sync MySQL to SQLite DB
    public void updateStatCustomers(String respons) {
        ArrayList<HashMap<String, String>> customersynclist;
        customersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(respons);
            System.out.println(arr.length());

            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    Log.i("AS:", obj.toString());

                    System.out.println(obj.get("code"));
                    System.out.println(obj.get("name"));
                    System.out.println(obj.get("latitude"));
                    System.out.println(obj.get("langitude"));

                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    queryValues.put("code", obj.get("code").toString());
                    queryValues.put("name", obj.get("name").toString());
                    queryValues.put("latitude", obj.get("latitude").toString());
                    queryValues.put("langitude", obj.get("langitude").toString());

                    controller.insertCustomer(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("code").toString());
                    map.put("status", "1");
                    customersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateCustomerSts(gson.toJson(customersynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateStatUsers(String respons) {
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(respons);
            System.out.println(arr.length());

            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    Log.i("AS:", obj.toString());

                    System.out.println(obj.get("code"));
                    System.out.println(obj.get("username"));
                    System.out.println(obj.get("password"));

                    // DB QueryValues Object to insert into SQLite
                    userValues = new HashMap<String, String>();
                    userValues.put("code", obj.get("code").toString());
                    userValues.put("username", obj.get("username").toString());
                    userValues.put("password", obj.get("password").toString());

                    controller.insertUser(userValues);
                    HashMap<String, String> mapUpdate = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    mapUpdate.put("Id", obj.get("code").toString());
                    mapUpdate.put("status", "1");
                    usersynclist.add(mapUpdate);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateUserSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                //reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // Method to inform remote MySQL DB about completion of Sync activity


    public void updateStatItems(String respons) {
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(respons);
            System.out.println(arr.length());

            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    Log.i("AS:", obj.toString());

                    System.out.println(obj.get("code"));
                    System.out.println(obj.get("name"));

                    // DB QueryValues Object to insert into SQLite
                    userValues = new HashMap<String, String>();
                    userValues.put("code", obj.get("code").toString());
                    userValues.put("name", obj.get("name").toString());

                    controller.insertUser(userValues);
                    HashMap<String, String> mapUpdate = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    mapUpdate.put("Id", obj.get("code").toString());
                    mapUpdate.put("status", "1");
                    usersynclist.add(mapUpdate);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateItemSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                //reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateCustomerSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("sync_status", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        //client.post("http://192.168.0.106/aang/mysqlsqlitesync/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
        client.post("http://192.168.1.33/aang/mysqlsqlitesync/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "MySQL DB has been informed about Sync activity", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateUserSts(String json) {
        System.out.println(json);
        AsyncHttpClient user = new AsyncHttpClient();
        RequestParams rParams = new RequestParams();
        rParams.put("sync_status", json);

        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        //user.post("http://192.168.0.106/aang/mysqlsqlitesync/updateusersync.php", rParams, new AsyncHttpResponseHandler() {
        user.post("http://192.168.1.33/aang/mysqlsqlitesync/updateusersync.php", rParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "MySQL DB has been informed about Sync activity", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateItemSts(String json) {
        System.out.println(json);
        AsyncHttpClient user = new AsyncHttpClient();
        RequestParams rParams = new RequestParams();
        rParams.put("sync_status", json);

        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        //user.post("http://192.168.0.106/aang/mysqlsqlitesync/updateitemsync.php", rParams, new AsyncHttpResponseHandler() {
        user.post("http://192.168.1.33/aang/mysqlsqlitesync/updateitemsync.php", rParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "MySQL DB has been informed about Sync activity", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);


                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_sales_order) {
            Intent sales_order = new Intent(MainActivity.this, SalesOrderActivity.class);
            startActivity(sales_order);
        /*}else if(id==R.id.nav_list_sales_order){
            Intent i = new Intent(this, SalesOrderActivity.class);
            //i.putExtra("tab_index","1");
            startActivity(i);*/
        } else if (id == R.id.check_in_user) {

            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        } else if (id == R.id.about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        final ProgressDialog csprogress = new ProgressDialog(this);

        if (scanningResult != null) {

            String scanContent = scanningResult.getContents();
            Intent scan_result = new Intent(this, Barcode.class);
            scan_result.putExtra("result_scan", scanContent);
            startActivity(scan_result);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
//        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });

        // Showing Alert Message
        alertDialog.show();
    }
}


