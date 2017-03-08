package gr.eap.dxt.main;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.eap.dxt.R;



public class AboutFragment extends Fragment {

    public AboutFragment() {}

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
