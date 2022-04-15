package ca.cegepgarneau.tp3_google_maps_v2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import ca.cegepgarneau.tp3_google_maps_v2.data.AppExecutors;
import ca.cegepgarneau.tp3_google_maps_v2.data.UtilisateurRoomDatabase;
import ca.cegepgarneau.tp3_google_maps_v2.databinding.ActivityDrawerBinding;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;
import ca.cegepgarneau.tp3_google_maps_v2.ui.FormulaireAjoutMarker;
import ca.cegepgarneau.tp3_google_maps_v2.ui.home.HomeFragment;

public class DrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDrawerBinding binding;
    private static final String TAG = "MAIN";
    private boolean formIsUp;
    private UtilisateurRoomDatabase utilisateursListDb;

    public static LatLng markerAjoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        formIsUp = false;

        setSupportActionBar(binding.appBarDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_gallery, R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        try {
            Float.parseFloat(HomeFragment.tvDistance.getText().toString());
            savedInstanceState.putFloat("distance", Float.parseFloat(HomeFragment.tvDistance.getText().toString()));
            super.onSaveInstanceState(savedInstanceState);
        }
        catch (NumberFormatException e){
            Log.d(TAG, "onSaveInstanceState: no distance set" + e);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Float distance;
        if(savedInstanceState != null){
            if (savedInstanceState.getFloat("distance") != 0.0f){
                distance = savedInstanceState.getFloat("distance");
                HomeFragment.tvDistance.setText(distance.toString());
                HomeFragment.tvDistance.setVisibility(View.VISIBLE);
            }
        }
    }

    public void AjouterUnMarker(String message){
        utilisateursListDb = UtilisateurRoomDatabase.getDatabase(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                int nbrIconPossible = 5;
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setFirstname("UserFirstName");
                utilisateur.setLastname("UserLastName");
                utilisateur.setMessage(message);
                utilisateur.setLatitude(markerAjoute.latitude);
                utilisateur.setLongitude(markerAjoute.longitude);
                utilisateur.setPicture("https://robohash.org/"+"UserFirstName"+"UserLastName");
                utilisateursListDb.utilisateurDao().insert(utilisateur);
            }
        });
    }

    public void closeForm(){
        formIsUp = false;
        FormulaireAjoutMarker formulaireAjoutMarkerRemove = (FormulaireAjoutMarker) getSupportFragmentManager()
                .findFragmentByTag("TAG");
        getSupportFragmentManager()
                .beginTransaction()
                .remove(formulaireAjoutMarkerRemove)
                .commit();
    }
}