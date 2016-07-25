package asri_tools.devalkhatech.com.asri.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import asri_tools.devalkhatech.com.asri.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesEntryFragment extends Fragment {

    public SalesEntryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales_entry, container, false);

        return rootView;

    }
}
