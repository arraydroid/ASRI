package asri_tools.devalkhatech.com.asri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
//import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.AppController;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.interfaces.SalesOrderAPI;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String insertDataURL = "http://192.168.1.33/aang/mysqlsqlitesync/insertitemso.php";
    private Context context;

    private CoordinatorLayout coordinatorLayout;
    DBController controller = new DBController(this);
    TextView orderCustomer, orderInvoice, orderRemark, orderSales, orderDate;
    EditText EorderCustomer, EorderInvoice, EorderRemark, EorderSales, EorderDate;
    //EditText id;
    Button sync_so;
    HashMap<String, String> queryValues;

    ProgressDialog prgDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;
    //This is our root url
    public static final String ROOT_URL = "http://192.168.1.33/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayoutDetail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //final ProgressDialog csprogress = new ProgressDialog(DetailActivity.this, R.style.AppTheme_Dark_Dialog);

        orderCustomer = (TextView) findViewById(R.id.txtCustomerNameItem);
        orderInvoice = (TextView) findViewById(R.id.txtsoId);
        orderRemark = (TextView) findViewById(R.id.txtRemarkItem);
        orderDate = (TextView) findViewById(R.id.txtDateItem);
        orderSales = (TextView) findViewById(R.id.txtSalesmanItem);
        EorderCustomer = (EditText) findViewById(R.id.ed_customer_name);
        EorderInvoice = (EditText) findViewById(R.id.ed_id_so);
        EorderRemark = (EditText) findViewById(R.id.ed_remark);
        EorderDate = (EditText) findViewById(R.id.ed_date);
        EorderSales = (EditText) findViewById(R.id.ed_salesman);

        sync_so = (Button) findViewById(R.id.btn_sync_so);

        // ===============================================================================//
        // Ambil parameter intent dari class SoListFragment myList.setOnItemClickListener
        // ===============================================================================//
        Intent intent = getIntent();
        final String _res = intent.getStringExtra("code_reference");
        final String _cst = intent.getStringExtra("cust_name");
        final String _csi = intent.getStringExtra("cust_id");
        final String _rmk = intent.getStringExtra("remark");
        final String _ses = intent.getStringExtra("salesman");
        final String _date = intent.getStringExtra("date");
        // ===============================================================================//
        //id.setText(_res);
        orderCustomer.setText(_cst);
        orderInvoice.setText(_res);
        orderRemark.setText(_rmk);
        orderDate.setText(_date);
        orderSales.setText(_ses);

        EorderCustomer.setText(_csi);
        EorderInvoice.setText(_res);
        EorderRemark.setText(_rmk);
        EorderDate.setText(_date);
        EorderSales.setText(_ses);

        String id_list = orderInvoice.getText().toString();

        // ===============================================================================//
        // Menampilkan data list detail order item
        context = this; //use the instance variable
        DBController db = new DBController(context);

        ArrayList<HashMap<String, String>> soList = db.getListSoBySoCode(id_list);
        // If users exists in SQLite DB

        if (soList.size() != 0) {
            ListAdapter adapter = new SimpleAdapter(this, soList, R.layout.list_so_sync, new String[]{
                    "so_id", "item", "name", "qty"}, new int[]{R.id.detail_soId, R.id.detail_itemCode, R.id.detail_itemName, R.id.detail_qtyId});
            ListView myList = (ListView) findViewById(R.id.listView2);
            myList.setDivider(null);
            myList.setAdapter(adapter);

        }
        // ===============================================================================//
        // On Sync Button Press
        // ===============================================================================//
        sync_so.setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

   /* private void insertItemtoDB() {
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        String id_list = orderInvoice.getText().toString();
        // ===============================================================================//
        // Menampilkan data list detail order item
        context = this; //use the instance variable
        DBController db = new DBController(context);

        ArrayList<HashMap<String, String>> soList = db.getListSoBySoCode(id_list);
        String url = new Gson().toJson(soList);
        //client.post("http://192.168.0.106/aang/mysqlsqlitesync/getcustomers.php", params, new AsyncHttpResponseHandler() {
        System.out.println(url);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //insert sales entry detail with data from respons getcustomers.php
                updateStat(response);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                // prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    *//*Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();*//*
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Unexpected Error occcured!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });
    }*/

    private void insertSalesOrder() {
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        SalesOrderAPI api = adapter.create(SalesOrderAPI.class);

        //Defining the method insertuser of our interface
        api.insertSOrder(

                //Passing the values by getting it from editTexts
                EorderInvoice.getText().toString(),
                EorderCustomer.getText().toString(),
                EorderRemark.getText().toString(),
                EorderDate.getText().toString(),
                EorderSales.getText().toString(),

                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        BufferedReader reader = null;

                        //An string to store output from the server
                        String output = "";

                        try {
                            //Initializing buffered reader
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            //Reading the output in the string
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.print(output);
                        //Displaying the output as a toast
//                        Toast.makeText(DetailActivity.this, output, Toast.LENGTH_LONG).show();
                        //csprogress.dismiss();
                        //Displaying the output as a Snackbar
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, output, Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(Color.GREEN);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.GREEN);
                        snackbar.show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        //Toast.makeText(DetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, error.toString(), Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(Color.GREEN);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        snackbar.show();
                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Detail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://asri_tools.devalkhatech.com.asri/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Detail Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://asri_tools.devalkhatech.com.asri/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onClick(View v) {
        insertSalesOrder();
       // insertSalesOrderItem();

//        insertItemtoDB();
    }

    //for inserting data into server databse
    /*private void insertSalesOrderItem() {
        StringRequest request = new StringRequest(Request.Method.POST, insertDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id_so",);
                return parameters;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }
*//*
    // Method to Sync MySQL to SQLite DB
    public void updateStat(String respons) {
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

                    System.out.println(obj.get("so_id"));
                    System.out.println(obj.get("item"));
                    System.out.println(obj.get("name"));
                    System.out.println(obj.get("qty"));

                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    queryValues.put("so_id", obj.get("so_id").toString());
                    queryValues.put("item", obj.get("item").toString());
                    queryValues.put("name", obj.get("name").toString());
                    queryValues.put("qty", obj.get("qty").toString());

                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("so_id", obj.get("code").toString());
                    map.put("item", obj.get("item").toString());
                    map.put("qty", obj.get("qty").toString());

                    customersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateSoItem(gson.toJson(customersynclist));
                // Reload the Main Activity
                //reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateSoItem(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("so_id", json);
        params.put("item", json);
        params.put("qty", json);

        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        //client.post("http://192.168.0.106/aang/mysqlsqlitesync/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
        client.post("http://192.168.1.33/aang/mysqlsqlitesync/insertitemso.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Data Item was inserted", Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.GREEN);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }*/

}
