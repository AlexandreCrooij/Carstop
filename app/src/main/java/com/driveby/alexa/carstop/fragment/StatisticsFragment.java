package com.driveby.alexa.carstop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.driveby.alexa.carstop.R;
import com.driveby.alexa.carstop.activity.AlertsActivity;

public class StatisticsFragment extends Fragment {

    private static final String TAG = "StatisticsFragment";

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        Button btn_alert = view.findViewById(R.id.btn_statistics_alert);
        btn_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start the main activity
                Intent intent = new Intent(getContext(), AlertsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}

