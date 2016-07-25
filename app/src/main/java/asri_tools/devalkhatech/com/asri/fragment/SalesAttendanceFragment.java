package asri_tools.devalkhatech.com.asri.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import asri_tools.devalkhatech.com.asri.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SalesAttendanceFragment extends Fragment {

    public SalesAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales_attendance, container, false);
    }
}
