package ca.cegepgarneau.tp3_google_maps_v2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.content.ContextCompat;
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
import ca.cegepgarneau.tp3_google_maps_v2.ui.FormulaireModifierUtilisateur;
import ca.cegepgarneau.tp3_google_maps_v2.ui.home.HomeFragment;

public class DrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDrawerBinding binding;
    private static final String TAG = "MAIN";
    private boolean formIsUp;
    private UtilisateurRoomDatabase utilisateursListDb;

    //Je me
    public static GoogleMap mMap;

    public static LatLng goToMarker;

    public static LatLng markerAjoute;

    private String prenomUtilisateurKey = "prenom";
    private String nomUtilisateurKey = "nom";

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        formIsUp = false;

        prefs = this.getSharedPreferences("ca.cegepgarneau.tp3_google_maps_v2", Context.MODE_PRIVATE);

        if(!prefs.contains(prenomUtilisateurKey)){
            prefs.edit().putString(prenomUtilisateurKey, String.valueOf(R.string.default_user_firstname)).apply();
        }
        if(!prefs.contains(nomUtilisateurKey)){
            prefs.edit().putString(nomUtilisateurKey, String.valueOf(R.string.default_user_lastname)).apply();
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.bt_config_toolbar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_form_modifier_mtilisateur, new FormulaireModifierUtilisateur(), "formUtilisateur")
                        .addToBackStack(null).commit();
                return true;
            case R.id.bt_suppression_toolbar:
                SupprimerToutesUtilisateurs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


    //Trouver ouvrir automatiquement la carte lors d'un click sur un élément de la liste.
    public void goToMarker(LatLng markerPosition){
        getSupportFragmentManager().beginTransaction().add(R.id.homeFragment, new HomeFragment()).commit();
    }

    public void AjouterUnMarker(String message){
        prefs = this.getSharedPreferences("ca.cegepgarneau.tp3_google_maps_v2", Context.MODE_PRIVATE);
        utilisateursListDb = UtilisateurRoomDatabase.getDatabase(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String prenom = prefs.getString(prenomUtilisateurKey, new String());
                String nom = prefs.getString(nomUtilisateurKey, new String());
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setFirstname(prenom);
                utilisateur.setLastname(nom);
                utilisateur.setMessage(message);
                utilisateur.setLatitude(markerAjoute.latitude);
                utilisateur.setLongitude(markerAjoute.longitude);
                utilisateur.setPicture("https://robohash.org/"+nom+prenom);
                utilisateursListDb.utilisateurDao().insert(utilisateur);
            }
        });
    }

    public void ModifierUtilisateur(String prenom, String nom){
        prefs = this.getSharedPreferences("ca.cegepgarneau.tp3_google_maps_v2", Context.MODE_PRIVATE);

        if(prenom != ""){
            prefs.edit().putString(prenomUtilisateurKey, prenom).apply();
        }
        if(nom != ""){
            prefs.edit().putString(nomUtilisateurKey, nom).apply();
        }
        Toast.makeText(this, "Données utilisateur modifiées", Toast.LENGTH_LONG).show();

    }

    public void closeForm(){
        formIsUp = false;
        FormulaireModifierUtilisateur formulaireModifierUtilisateurFragmentRemove = (FormulaireModifierUtilisateur) getSupportFragmentManager()
                .findFragmentByTag("formUtilisateur");
        getSupportFragmentManager()
                .beginTransaction()
                .remove(formulaireModifierUtilisateurFragmentRemove)
                .commit();
    }

    public void SupprimerToutesUtilisateurs(){
        utilisateursListDb = UtilisateurRoomDatabase.getDatabase(this);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                utilisateursListDb.utilisateurDao().deleteAllUtilisateurs();
            }
        });
    }


}