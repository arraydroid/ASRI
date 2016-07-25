package asri_tools.devalkhatech.com.asri.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.activity.DetailActivity;
import asri_tools.devalkhatech.com.asri.controllers.DBController;

public class SoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    public SoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_so_list, container, false);
        context = getActivity(); //use the instance variable
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchDataSo();
            }
        },1000);

        DBController db = new DBController(context);

        ArrayList<HashMap<String, String>> soList = db.getAllSo();
        // If users exists in SQLite DB
        if (soList.size() != 0) {
            // Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter(getActivity(), soList, R.layout.list_so, new String[]{
                    "id", "code", "customer_code", "customer_name", "salesman", "remark", "created_date"}, new int[]{R.id.soId, R.id.soCode, R.id.customerId, R.id.customerName, R.id.salesName, R.id.remarkName, R.id.dateName});
            ListView myList = (ListView) v.findViewById(R.id.list_so);
            myList.setDivider(null);
            myList.setAdapter(adapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String so_id = ((TextView) view.findViewById(R.id.soId)).getText().toString();
                    String so_code = ((TextView) view.findViewById(R.id.soCode)).getText().toString();
                    String so_cust_id = ((TextView) view.findViewById(R.id.customerId)).getText().toString();
                    String so_cust_name = ((TextView) view.findViewById(R.id.customerName)).getText().toString();
                    String so_salesname = ((TextView) view.findViewById(R.id.salesName)).getText().toString();
                    String so_remark = ((TextView) view.findViewById(R.id.remarkName)).getText().toString();
                    String so_date = ((TextView) view.findViewById(R.id.dateName)).getText().toString();

                    Intent in = new Intent(getActivity(), DetailActivity.class);
                    in.putExtra("id_reference", so_id);
                    in.putExtra("code_reference", so_code);
                    in.putExtra("cust_id", so_cust_id);
                    in.putExtra("cust_name", so_cust_name);
                    in.putExtra("salesman", so_salesname);
                    in.putExtra("remark", so_remark);
                    in.putExtra("date", so_date);
                    startActivity(in);
                }
            });
        }
        return v;
    }

    private void fetchDataSo() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        DBController db = new DBController(context);

        ArrayList<HashMap<String, String>> soList = db.getAllSo();
        // if users exist in SQLite DB
        // If users exists in SQLite DB
        if (soList.size() != 0) {
            // Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter(getActivity(), soList, R.layout.list_so, new String[]{
                    "id", "code", "customer_code", "customer_name", "salesman", "remark", "created_date"}, new int[]{R.id.soId, R.id.soCode, R.id.customerId, R.id.customerName, R.id.salesName, R.id.remarkName, R.id.dateName});
            ListView myList = (ListView) getView().findViewById(R.id.list_so);
            myList.setDivider(null);
            myList.setAdapter(adapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String so_id = ((TextView) view.findViewById(R.id.soId)).getText().toString();
                    String so_code = ((TextView) view.findViewById(R.id.soCode)).getText().toString();
                    String so_cust_id = ((TextView) view.findViewById(R.id.customerId)).getText().toString();
                    String so_cust_name = ((TextView) view.findViewById(R.id.customerName)).getText().toString();
                    String so_salesname = ((TextView) view.findViewById(R.id.salesName)).getText().toString();
                    String so_remark = ((TextView) view.findViewById(R.id.remarkName)).getText().toString();
                    String so_date = ((TextView) view.findViewById(R.id.dateName)).getText().toString();

                    Intent in = new Intent(getActivity(), DetailActivity.class);
                    in.putExtra("id_reference", so_id);
                    in.putExtra("code_reference", so_code);
                    in.putExtra("cust_id", so_cust_id);
                    in.putExtra("cust_name", so_cust_name);
                    in.putExtra("salesman", so_salesname);
                    in.putExtra("remark", so_remark);
                    in.putExtra("date", so_date);
                    startActivity(in);
                }
            });
        }
            // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        fetchDataSo();
    }

}