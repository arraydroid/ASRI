package asri_tools.devalkhatech.com.asri.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.controllers.DBController;

public class BarcodeListFragment extends Fragment {

    private Context context;

    public BarcodeListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v =inflater.inflate(R.layout.fragment_barcode_list, container, false);
        // Inflate the layout for this fragment
        context = getActivity(); //use the instance variable
        DBController controller = new DBController(context);
        ArrayList<HashMap<String, String>> userList = controller.getAllAbsen();
        // If users exists in SQLite DB
        if (userList.size() != 0) {
            // Set the User Array list in ListView
            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.list_absen, new String[]{
                    "customer_code", "latitude", "langitude", "created_date"}, new int[]{R.id.txtCustomerNameItem, R.id.txtLatitudeAbsen, R.id.txtLongitude, R.id.txtDate});
            ListView myList = (ListView) v.findViewById(R.id.listAbsen);
            myList.setAdapter(adapter);
        }
        return v;
    }

}
