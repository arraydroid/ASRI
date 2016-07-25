package asri_tools.devalkhatech.com.asri.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.helpers.SessionManager;
import asri_tools.devalkhatech.com.asri.models.SpinnerObject;

public class SalesEntry extends AppCompatActivity implements
        OnItemSelectedListener {

    Spinner c_customer;
    Button save;
    EditText t_invoice, t_customer, t_remark, t_session;

    HashMap<String, String> queryValues;

    DBController controller = new DBController(this);

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ProgressDialog csprogress = new ProgressDialog(SalesEntry.this);

        // Session manager
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);

        t_invoice = (EditText) findViewById(R.id.txtInvoice);
        t_customer = (EditText) findViewById(R.id.txtCustomer);
        t_remark = (EditText) findViewById(R.id.txtRemark);
        t_session = (EditText) findViewById(R.id.txtSales);

        c_customer = (Spinner) findViewById(R.id.spnCustomer);

        c_customer.setOnItemSelectedListener(this);
        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Loading...Inserting data to Local DB");
                csprogress.setCancelable(false);
                csprogress.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        csprogress.dismiss();
                        insertData();

                        /*ArrayList<HashMap<String, String>> userList = controller.getAllSo();
                        // If users exists in SQLite DB
                        if (userList.size() != 0) {
                            // Set the User Array list in ListView
                            ListAdapter adapter = new SimpleAdapter(SalesEntry.this, userList, R.layout.list_customer, new String[]{
                                    "code", "customer", "salesman", "remark"}, new int[]{R.id.soId, R.id.customerName, R.id.salesmanId, R.id.remarkId});
                            ListView myList = (ListView) findViewById(R.id.listView2);
                            myList.setAdapter(adapter);
                        }*/
                        //whatever you want just you have to launch overhere.
                        String idSalesOrder = t_invoice.getText().toString();
                        String customerOrder = t_customer.getText().toString();
                        String remarkOrder = t_remark.getText().toString();
                        String sessionOrder = t_session.getText().toString();

                        Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                        i.putExtra("result_id", idSalesOrder);
                        i.putExtra("result_customer", customerOrder);
                        i.putExtra("result_remark", remarkOrder);
                        i.putExtra("result_session", sessionOrder);

                        startActivity(i);
                    }
                }, 1000);//just mention the time when you want to launch your action

            }
        });

        /*load = (Button) findViewById(R.id.btn_load_item);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idSalesOrder = t_invoice.getText().toString();
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("result_id", idSalesOrder);
                startActivity(i);
            }
        });*/
        //Get Device ID Identifier to salesorder code
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String m_deviceId = TelephonyMgr.getDeviceId();

        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String currentDateTimeString = df.format(new Date());
        int LAST_ID = controller.getHighestID();
        String ID_INVOICE = "INV" + m_deviceId + "/" + currentDateTimeString + "/" + String.valueOf(LAST_ID);
        t_invoice.setText(ID_INVOICE);
        t_session.setText(name);

        //loadNewIdInvoice();
        loadSpinnerData();

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */

    }

    private void insertData() {
        try {
            // DB QueryValues Object to insert into SQLite
            queryValues = new HashMap<String, String>();
            queryValues.put("code", t_invoice.getText().toString());
            queryValues.put("customer_code", t_customer.getText().toString());
            queryValues.put("salesman", t_session.getText().toString());
            queryValues.put("remark", t_remark.getText().toString());
            controller.insertSo(queryValues);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showdialogInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void loadSpinnerData() {
        // database handler
        DBController db = new DBController(getApplicationContext());
        // Spinner Drop down elements
        List<SpinnerObject> lables = db.getAllLabels();
        // Creating adapter for spinner
        ArrayAdapter<SpinnerObject> dataAdapter = new ArrayAdapter<SpinnerObject>(this,
                android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        c_customer.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();
        t_customer.setText(label);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}