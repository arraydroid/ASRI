package asri_tools.devalkhatech.com.asri.activity;

/**
 * Created by Array-PC on 15-Feb-16.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.helpers.ConnectionDetector;

public class Splashscreen extends ActionBarActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 6000;
    private CoordinatorLayout coordinatorLayout;
    // DB Class to perform DB related operations
    DBController controller = new DBController(this);
    // Progress Dialog Object
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues,userValues;

    // Connection detector class
    ConnectionDetector cd;

    // flag for Internet connection status
    Boolean isInternetPresent = false;
    Boolean isFastConnection = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorSplashLayout);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        //isFastConnection = cd.isConnectedFast();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    syncUsers();
                    syncCustomer();
                    syncItem();
                    Intent i = new Intent(Splashscreen.this, LoginActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            //cd.showSettingsAlert();
            showAlertDialog(Splashscreen.this, "No Internet Connection",
                    "Please try again or enabling in your setting!", false);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();

        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(final Context context, String title, String message, Boolean status) {
        //AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);


        // On pressing Settings button
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                context.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                System.exit(0);
            }
        });

        alertDialog.show();
    }

    public void syncCustomer() {

        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show();
        // Make Http call to getcustomers.php

        //client.post("http://192.168.0.106/aang/mysqlsqlitesync/getcustomers.php", params, new AsyncHttpResponseHandler() {
        client.post("http://192.168.1.33/aang/mysqlsqlitesync/getcustomers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                //prgDialog.hide();
                // Update SQLite DB with response sent by getcustomers.php
                updateStatCustomers(response);

            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Requested resource not found", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Something went wrong at server end", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();*/
                }
            }
        });
    }

    private void syncUsers() {
        AsyncHttpClient sales_user = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show();
        // Make Http call to getuser.php

        //sales_user.post("http://192.168.0.106/aang/mysqlsqlitesync/getusers.php", params, new AsyncHttpResponseHandler() {
        sales_user.post("http://192.168.1.33/aang/mysqlsqlitesync/getuser.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                //prgDialog.hide();
                // Update SQLite DB with response sent by getuser.php
                updateStatUsers(response);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Requested resource not found", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Something went wrong at server end", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();*/
                }
            }

        });
    }

    private void syncItem() {
        AsyncHttpClient items = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        // Show ProgressBar
        //prgDialog.show();
        // Make Http call to getuser.php

        //items.post("http://192.168.0.106/aang/mysqlsqlitesync/getusers.php", params, new AsyncHttpResponseHandler() {
        items.post("http://192.168.1.33/aang/mysqlsqlitesync/getitem.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                // Hide ProgressBar
                // prgDialog.hide();
                // Update SQLite DB with response sent by getuser.php
                updateStatItems(response);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Requested resource not found", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Something went wrong at server end", Snackbar.LENGTH_LONG);
                    snackbar.show();*/
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                    /*Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();*/
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
                //reloadActivity();
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
                    System.out.println(obj.get("name"));
                    System.out.println(obj.get("email"));
                    System.out.println(obj.get("phone"));


                    // DB QueryValues Object to insert into SQLite
                    userValues = new HashMap<String, String>();
                    userValues.put("code", obj.get("code").toString());
                    userValues.put("username", obj.get("username").toString());
                    userValues.put("password", obj.get("password").toString());
                    userValues.put("name", obj.get("name").toString());
                    userValues.put("email", obj.get("email").toString());
                    userValues.put("phone", obj.get("phone").toString());

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

                    controller.insertItem(userValues);
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
                /*Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "MySQL DB has been informed about Sync activity", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();*/
                Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                /*Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Error Occured", Snackbar.LENGTH_LONG);
                snackbar.show();*/
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
                Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
//                Snackbar snackbar = Snackbar
//                        .make(coordinatorLayout, "Error Occured", Snackbar.LENGTH_LONG);
//                snackbar.show();
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
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
                /*Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "MySQL DB has been informed about Sync activity", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();*/
                Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                /*Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Error Occured", Snackbar.LENGTH_LONG);
                snackbar.show();*/
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }

}