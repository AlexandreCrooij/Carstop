package com.example.alexa.carstop.activity;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alexa.carstop.R;
import com.example.alexa.carstop.fragment.MainFragment;
import com.example.alexa.carstop.fragment.SettingsFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final String BACK_STACK_ROOT_TAG = "MAIN";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle myToggle;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.dashboard));

        //Set the fragment initially
        MainFragment fragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        myToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.dashboard, R.string.dashboard);
        drawerLayout.addDrawerListener(myToggle);
        myToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (myToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_view);
        //set the name and email in the navigation drawer of the user who is logged in
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.lbl_name);
        TextView email = (TextView) headerView.findViewById(R.id.lbl_email);
        String username = "Mathis Fux";
        String user_email = "mathis.fux@gmx.ch";
        name.setText(username);
        email.setText(user_email);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        // Pop off everything up to and including the current tab
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        String fragmentTag = null;

        if (id == R.id.dashboard) {
            fragmentClass = MainFragment.class;
            fragmentTag = getString(R.string.dashboard);
        } else if (id == R.id.settings) {
            fragmentClass = SettingsFragment.class;
            fragmentTag = getString(R.string.settings);
        } else if (id == R.id.logout) {
            logout();
            return true;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        /* change Fragment
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(BACK_STACK_ROOT_TAG).commit();
        getSupportActionBar().setTitle(fragmentTag);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        */
        return true;
    }

    private void logout() {

    }
}
