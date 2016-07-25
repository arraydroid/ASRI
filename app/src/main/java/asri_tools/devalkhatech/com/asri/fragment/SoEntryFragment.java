package asri_tools.devalkhatech.com.asri.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.activity.MainActivity;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.helpers.SessionManager;
import asri_tools.devalkhatech.com.asri.models.ItemsObject;
import asri_tools.devalkhatech.com.asri.models.SpinnerObject;

public class SoEntryFragment extends Fragment implements
        OnItemSelectedListener {

    Spinner c_customer;
    EditText t_invoice, t_customer, t_remark, t_session;
    EditText id, nameItem, qty, dsc;
    Button save, save_detail, btn_save_db, btn_edit_list;
    Spinner c_item;
    SwitchCompat swdisc;
    ListView lv_item;
    CardView cv_item;
    HashMap<String, String> queryValues;
    private Context context;
    String is_disc = "";

    private SessionManager session;

    public SoEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_so_entry, container, false);

        final ProgressDialog csprogress = new ProgressDialog(getActivity());

        // Session manager
        session = new SessionManager(getActivity());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);

        t_invoice = (EditText) rootView.findViewById(R.id.txtSoNumber);
        t_customer = (EditText) rootView.findViewById(R.id.txtCustomer);
        t_remark = (EditText) rootView.findViewById(R.id.txtRemark);
        t_session = (EditText) rootView.findViewById(R.id.txtSales);
        dsc = (EditText) rootView.findViewById(R.id.t_dis);

        swdisc = (SwitchCompat) rootView.findViewById(R.id.switch_compat);
        swdisc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!swdisc.isChecked()) {
                    is_disc = "N";
                    //Toast.makeText(getActivity(), "N is switch", Toast.LENGTH_SHORT).show();
                    dsc.setText(is_disc);
                } else {
                    is_disc = "Y";
                    //Toast.makeText(getActivity(), "Y is switch", Toast.LENGTH_SHORT).show();
                    dsc.setText(is_disc);
                }
            }
        });
        lv_item = (ListView) rootView.findViewById(R.id.listView2);
        cv_item = (CardView) rootView.findViewById(R.id.cv_item);

        c_customer = (Spinner) rootView.findViewById(R.id.spnCustomer);
        btn_save_db = (Button) rootView.findViewById(R.id.btn_save_db);

        c_customer.setOnItemSelectedListener(this);
        save = (Button) rootView.findViewById(R.id.btn_save);
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
                        cv_item.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);
                    }
                }, 1000);//just mention the time when you want to launch your action

            }
        });

        btn_save_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        t_session.setText(name);
        loadSpinnerData();
        id = (EditText) rootView.findViewById(R.id.ref);
        c_item = (Spinner) rootView.findViewById(R.id.spinerItem);
        nameItem = (EditText) rootView.findViewById(R.id.t_item_name);
        qty = (EditText) rootView.findViewById(R.id.t_qty);
        save_detail = (Button) rootView.findViewById(R.id.btn_add_list);
        btn_edit_list = (Button) rootView.findViewById(R.id.btn_edit_list);

        c_item.setOnItemSelectedListener(this);
        loadSpinnerItem();

        save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Loading...");
                csprogress.setCancelable(false);
                csprogress.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        csprogress.dismiss();
                        insertDataDetail();
                        lv_item.setVisibility(View.VISIBLE);
                        btn_save_db.setVisibility(View.VISIBLE);
                        String id_item = t_invoice.getText().toString();
                        context = getActivity(); //use the instance variable
                        DBController db = new DBController(context);

                        ArrayList<HashMap<String, String>> userList = db.getAllSoDetail(id_item);
                        // If users exists in SQLite DB
                        if (userList.size() != 0) {
                            // Set the User Array list in ListView
                            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.list_so_detail, new String[]{
                                    "id", "so_code", "item", "qty"}, new int[]{R.id.detail_soId, R.id.detail_soCode, R.id.detail_itemName, R.id.detail_qtyId});
                            final ListView myList = (ListView) rootView.findViewById(R.id.listView2);
                            myList.setDivider(null);
                            myList.setAdapter(adapter);

                            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    showAlertDialog(getActivity(), "Edit/Delete",
                                            "Do you want to edit or delete?", false);
                                    String so_id = ((TextView) view.findViewById(R.id.detail_soId)).getText().toString();
                                    String so_code = ((TextView) view.findViewById(R.id.detail_soCode)).getText().toString();
                                    String so_item_id = ((TextView) view.findViewById(R.id.detail_itemName)).getText().toString();
                                    String so_qty = ((TextView) view.findViewById(R.id.detail_qtyId)).getText().toString();

                                    nameItem.setText(so_item_id);
                                    qty.setText(so_qty);

                                }
                            });


                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(qty.getWindowToken(), 0);
                            qty.setText("");
                        }
                    }
                }, 300);//just mention the time when you want to launch your action
            }
        });
        return rootView;
    }

    private void insertData() {
        try {
            // DB QueryValues Object to insert into SQLite
            // database handler
            DBController db = new DBController(getActivity());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTimeString = df.format(new Date());

            queryValues = new HashMap<String, String>();
            queryValues.put("code", t_invoice.getText().toString());
            queryValues.put("customer_code", t_customer.getText().toString());
            queryValues.put("salesman", t_session.getText().toString());
            queryValues.put("remark", t_remark.getText().toString());
            queryValues.put("is_discount", dsc.getText().toString());
            queryValues.put("created_date", currentDateTimeString);

            db.insertSo(queryValues);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void showAlertDialog(final Context context, String title, String message, Boolean status) {
        //AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // On pressing Settings button
        alertDialog.setPositiveButton("Edit Data", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
//                context.startActivity(intent);
                btn_edit_list.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Delete Data", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                finish();
//                System.exit(0);
            }
        });
        alertDialog.show();
    }

    private void loadSpinnerData() {
        // database handler
        DBController db = new DBController(getActivity());
        // Spinner Drop down elements
        List<SpinnerObject> lables = db.getAllLabels();
        // Creating adapter for spinner
        ArrayAdapter<SpinnerObject> dataAdapter = new ArrayAdapter<SpinnerObject>(getActivity(),
                android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        c_customer.setAdapter(dataAdapter);
        /*int databaseId = Integer.parseInt ( ( (SpinnerObject) c_customer.getSelectedItem () ).getId () );
        t_customer.setText(databaseId);*/
    }

    private void insertDataDetail() {
        try {
            // DB QueryValues Object to insert into SQLite
            //Add this here:
            context = getActivity(); //use the instance variable
            DBController db = new DBController(context);


            queryValues = new HashMap<String, String>();
            queryValues.put("so_code", t_invoice.getText().toString());
            queryValues.put("item", nameItem.getText().toString());
            queryValues.put("qty", qty.getText().toString());
            db.insertSoDetail(queryValues);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadSpinnerItem() {
        // database handler
        DBController db = new DBController(getActivity());
        // Spinner Drop down elements
        List<ItemsObject> lables = db.getAllItem();
        // Creating adapter for spinner
        ArrayAdapter<ItemsObject> dataAdapter = new ArrayAdapter<ItemsObject>(getActivity(),
                android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        c_item.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        //String label = parent.getItemAtPosition(position).toString();
        String customerId = ((SpinnerObject) c_customer.getSelectedItem()).getId();
        t_customer.setText(customerId);

        String itemId = ((ItemsObject) c_item.getSelectedItem()).getId();
        nameItem.setText(itemId);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Add this here:
        context = getActivity(); //use the instance variable
        DBController db = new DBController(context);

        TelephonyManager TelephonyMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String m_deviceId = TelephonyMgr.getDeviceId();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyy");
        String currentDateTimeString = df.format(new Date());
        int LAST_ID = db.getHighestID();
        String ID_INVOICE = "SO" + m_deviceId + "/" + currentDateTimeString + "/" + String.valueOf(LAST_ID);
        t_invoice.setText(ID_INVOICE);
    }
}
