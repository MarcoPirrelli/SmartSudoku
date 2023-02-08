package com.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.smartsudoku.business.Manager;
import com.smartsudoku.database.DBHelper;

public class MainActivity extends AppCompatActivity {
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) (getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment));
        NavController navController = NavHostFragment.findNavController(navHostFragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Manager.autoUpdateNotes = sharedPref.getBoolean("autoUpdateNotes", true);
        Manager.checkForMistakes = sharedPref.getBoolean("checkForMistakes", true);
        Manager.INSTANCE.setTime(sharedPref.getLong("time", 0));
        Manager.INSTANCE.setLevel(sharedPref.getInt("level", 0));

        DBHelper db = DBHelper.getInstance(this);
        Manager.currentSudoku.setCellGrid(db.getGrid());
        Manager.INSTANCE.findSolvedGrid();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("autoUpdateNotes", Manager.autoUpdateNotes);
        editor.putBoolean("checkForMistakes", Manager.checkForMistakes);
        editor.putLong("time", Manager.INSTANCE.getTime());
        editor.putInt("level", Manager.INSTANCE.getLevel());
        editor.apply();

        DBHelper db = DBHelper.getInstance(this);
        db.addGrid(Manager.currentSudoku.getCellGrid());
    }
}