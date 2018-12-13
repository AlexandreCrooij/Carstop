package com.driveby.alexa.carstop.activity;

import android.content.Intent;
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

import com.driveby.alexa.carstop.R;
import com.driveby.alexa.carstop.fragment.MainFragment;
import com.driveby.alexa.carstop.fragment.SettingsFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final String BACK_STACK_ROOT_TAG = "MAIN";

    public static final String PREFS_NAME = "SharedPrefs";
    public static final String PREFS_NAME_USER = "SharedPrefsAthlete";
    public static final String PREFS_USER = "LoggedIn";
    public static final String PREFS_USER_FIRSTNAME = "LoggedInFirstname";
    public static final String PREFS_USER_LASTNAME = "LoggedInLastname";
    public static final String PREFS_USER_UID = "LoggedInUID";
    public static final String PREFS_ADM = "UserPermission";
    public static final String PREFS_LNG = "Language";

    private DrawerLayout drawerLayout;
    private String user_email;
    private String user_firstname;
    private String user_lastname;
    private ActionBarDrawerToggle myToggle;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get name, email and isAdmin
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME_USER, 0);
        user_email = sharedPreferences.getString(MainActivity.PREFS_USER, null);
        user_firstname = sharedPreferences.getString(MainActivity.PREFS_USER_FIRSTNAME, null);
        user_lastname = sharedPreferences.getString(MainActivity.PREFS_USER_LASTNAME, null);

        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.dashboard));

        //Set the fragment initially
        MainFragment fragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        myToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
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
        String username = user_firstname + " " + user_lastname;
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

        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(BACK_STACK_ROOT_TAG).commit();
        getSupportActionBar().setTitle(fragmentTag);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        //remove the stored preference for the next login
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFS_NAME_USER, 0).edit();
        editor.remove(PREFS_USER);
        editor.remove(PREFS_USER_FIRSTNAME);
        editor.remove(PREFS_USER_LASTNAME);
        editor.remove(PREFS_ADM);
        editor.remove(PREFS_USER_UID);
        editor.apply();

        //start login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClickBtnStart(View v)
    {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
    }
/*
    public void onClickBtnSeePickupPoints(View v)
    {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }
    */

}
