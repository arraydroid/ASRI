package asri_tools.devalkhatech.com.asri.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.application.AppConfig;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.interfaces.SalesOrderAPI;
import asri_tools.devalkhatech.com.asri.interfaces.SoItemAPI;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BarcodeDetailActivity extends AppCompatActivity {

    private Context context;

    private CoordinatorLayout coordinatorLayout;
    DBController controller = new DBController(this);
    TextView tCust, tAddress,tLat, tLong, tDate;
    Button sync_bc;
    HashMap<String, String> queryValues;

    ProgressDialog prgDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayoutDetail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //final ProgressDialog csprogress = new ProgressDialog(DetailActivity.this, R.style.AppTheme_Dark_Dialog);

        tCust = (TextView) findViewById(R.id.txtCustomerBarcode);
        tAddress = (TextView) findViewById(R.id.txtAlamat);
        tLat = (TextView) findViewById(R.id.txtLatitude);
        tLong = (TextView) findViewById(R.id.txtLongitude);
        tDate = (TextView) findViewById(R.id.txtDateAparture);



        sync_bc = (Button) findViewById(R.id.btn_sync_bc);

        //sync_detail =  (Button) findViewById(R.id.btn_sync_item);
        // ===============================================================================//
        // Ambil parameter intent dari class SoListFragment myList.setOnItemClickListener
        // ===============================================================================//
        Intent intent = getIntent();
        final String _cus = intent.getStringExtra("cust");
        final String _adr = intent.getStringExtra("addr");
        final String _lan = intent.getStringExtra("long");
        final String _lat = intent.getStringExtra("lang");
        final String _date = intent.getStringExtra("date");

        // ===============================================================================//
        tCust = (TextView) findViewById(R.id.txtCustomerBarcode);
        tAddress = (TextView) findViewById(R.id.txtAlamat);
        tLat = (TextView) findViewById(R.id.txtLatitude);
        tLong = (TextView) findViewById(R.id.txtLongitude);
        tDate = (TextView) findViewById(R.id.txtDateAparture);

        //id.setText(_res);
        tCust.setText(_cus);
        tAddress.setText(_adr);
        tLat.setText(_lan);
        tLong.setText(_lat);
        tDate.setText(_date);


        //String id_list = orderInvoice.getText().toString();

        // ===============================================================================//
        // Menampilkan data list detail order item
        /*context = this; //use the instance variable
        DBController db = new DBController(context);

        ArrayList<HashMap<String, String>> soList = db.getListSoBySoCode(id_list);
        // If users exists in SQLite DB

        if (soList.size() != 0) {
            ListAdapter adapter = new SimpleAdapter(this, soList, R.layout.list_so_sync, new String[]{
                    "so_code", "item", "name", "qty"}, new int[]{R.id.detail_soId, R.id.detail_itemCode, R.id.detail_itemName, R.id.detail_qtyId});
            ListView myList = (ListView) findViewById(R.id.lv_itemdetails);
            myList.setDivider(null);
            myList.setAdapter(adapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(), position, Toast.LENGTH_LONG).show();
                    String id_so_parameter =  ((TextView) view.findViewById(R.id.detail_soId)).getText().toString();
                    String item_parameter  =  ((TextView) view.findViewById(R.id.detail_itemCode)).getText().toString();
                    String qty_parameter   =  ((TextView) view.findViewById(R.id.detail_qtyId)).getText().toString();

                    insertItemServer(id_so_parameter,item_parameter,qty_parameter);
                }
            });

        }

        */// ===============================================================================//
        // On Sync Button Press
        // ===============================================================================//
        //sync_so.setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /*private void insertItemServer(String PARAM_SO_ID, String PARAM_ITEM, String PARAM_QTY) {
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter

        //sync_detail.setVisibility(View.VISIBLE);
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(AppConfig.ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        SoItemAPI web_api = adapter.create(SoItemAPI.class);
        web_api.insertItem(
                PARAM_SO_ID,
                PARAM_ITEM,
                PARAM_QTY,

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


    private void insertSalesOrder() {
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter

        //sync_detail.setVisibility(View.VISIBLE);
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(AppConfig.ROOT_URL) //Setting the Root URL
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
*/
   /* @Override
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
        //sync_so.setVisibility(View.GONE);
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sales_entry, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
}
