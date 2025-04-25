package com.meowstudio.calendar_organizer.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.meowstudio.calendar_organizer.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    private final Fragment calendarFragment = new CalendarFragment();
    private final Fragment upcomingEventsFragment = new UpcomingEventsFragment();
    private final Fragment userSettingsFragment = new UserSettingsFragment();
    private final Fragment aboutFragment = new AboutFragment();

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getAppTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.MainActivity_BottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        fragmentManager.beginTransaction().add(R.id.MainActivity_FrameLayout_Container, aboutFragment).hide(aboutFragment).commit();
        fragmentManager.beginTransaction().add(R.id.MainActivity_FrameLayout_Container, userSettingsFragment).hide(userSettingsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.MainActivity_FrameLayout_Container, upcomingEventsFragment).hide(upcomingEventsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.MainActivity_FrameLayout_Container, calendarFragment).commit();

        if (getFlag("isChanged")) {
            bottomNavigationView.setSelectedItemId(R.id.BottomNavigation_Item_Settings);
            bottomNavigationView.performClick();
            saveFlag("isChanged", false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.BottomNavigation_Item_Calendar:
                ((CalendarFragment) calendarFragment).setUpCalendar();
                fragmentManager.beginTransaction()
                        .hide(aboutFragment)
                        .hide(userSettingsFragment)
                        .hide(upcomingEventsFragment)
                        .show(calendarFragment)
                        .commit();
                break;
            case R.id.BottomNavigation_Item_UpcomingEvents:
                ((UpcomingEventsFragment) upcomingEventsFragment).setUpRecyclerView();
                fragmentManager.beginTransaction()
                        .hide(calendarFragment)
                        .hide(aboutFragment)
                        .hide(userSettingsFragment)
                        .show(upcomingEventsFragment)
                        .commit();
                break;
            case R.id.BottomNavigation_Item_Settings:
                fragmentManager.beginTransaction()
                        .hide(calendarFragment)
                        .hide(aboutFragment)
                        .hide(upcomingEventsFragment)
                        .show(userSettingsFragment)
                        .commit();
                break;
            case R.id.BottomNavigation_Item_About:
                fragmentManager.beginTransaction()
                        .hide(calendarFragment)
                        .hide(upcomingEventsFragment)
                        .hide(userSettingsFragment)
                        .show(aboutFragment)
                        .commit();
                break;
        }
        return true;
    }

    private int getAppTheme() {
        switch (getString("theme")) {
            case "Dark":
                return R.style.DarkTheme;
            case "Orange":
                return R.style.OrangeTheme;
        }
        return R.style.OrangeTheme;
    }

    private void saveFlag(String key, boolean flag) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, flag);
        editor.apply();
    }

    private boolean getFlag(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(key, false);
    }

    private String getString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(key, "Orange");
    }
}
