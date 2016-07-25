package asri_tools.devalkhatech.com.asri.fragment;

import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.DBController;


public class SoItemFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onRefresh() {
        fetchDataSo();
    }

    public SoItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_so_item, container, false);
        context = getActivity(); //use the instance variable
        final ListView myList = (ListView) v.findViewById(R.id.listItem);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        DBController db = new DBController(context);
        ArrayList<HashMap<String, String>> userList = db.getAllSoDetailList();
        // If users exists in SQLite DB
        if (userList.size() != 0) {
            // Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.list_so_detail, new String[]{
                    "id", "code", "item", "qty"}, new int[]{R.id.detail_soId, R.id.detail_soCode, R.id.detail_itemName, R.id.detail_qtyId});
            myList.setAdapter(adapter);

        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        DBController db = new DBController(context);
                                        ArrayList<HashMap<String, String>> userList = db.getAllSoDetailList();
                                        if (userList.size() != 0) {
                                            // Set the User Array list in ListView
                                            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.list_so_detail, new String[]{
                                                    "id", "code", "item", "qty"}, new int[]{R.id.detail_soId, R.id.detail_soCode, R.id.detail_itemName, R.id.detail_qtyId});
                                            //ListView myList = (ListView) getChildFragmentManager(R.id.listItem);
                                            //ListView listview = getActivity()..getListView();
                                            myList.setAdapter(adapter);
                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                        fetchDataSo();
                                    }
                                }
        );
        swipeRefreshLayout.setRefreshing(false);

        return v;
    }

    private void fetchDataSo() {
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setRefreshing(false);
    }


}
