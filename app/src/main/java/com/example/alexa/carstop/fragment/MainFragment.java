package com.example.alexa.carstop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alexa.carstop.R;
import com.example.alexa.carstop.activity.MainActivity;
import com.example.alexa.carstop.activity.StartActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    View view;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

}

