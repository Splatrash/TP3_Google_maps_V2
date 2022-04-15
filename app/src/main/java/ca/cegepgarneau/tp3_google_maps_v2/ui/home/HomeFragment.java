package ca.cegepgarneau.tp3_google_maps_v2.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import ca.cegepgarneau.tp3_google_maps_v2.DrawerActivity;
import ca.cegepgarneau.tp3_google_maps_v2.R;
import ca.cegepgarneau.tp3_google_maps_v2.data.AppExecutors;
import ca.cegepgarneau.tp3_google_maps_v2.databinding.FragmentHomeBinding;
import ca.cegepgarneau.tp3_google_maps_v2.datahttp.VolleyUtils;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;
import ca.cegepgarneau.tp3_google_maps_v2.ui.FormulaireAjoutMarker;
import ca.cegepgarneau.tp3_google_maps_v2.ui.UtilisateurAdapter;
import ca.cegepgarneau.tp3_google_maps_v2.ui.gallery.GalleryViewModel;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String TAG = "HOME";

    private FragmentHomeBinding binding;

    public static TextView tvDistance;

    private HomeViewModel homeViewModel;
    private ArrayList<Utilisateur> utilisateursListDb;
    private UtilisateurAdapter utilisateurAdapter;

    private Location userLocation;

    private Marker markerCamera;

    private boolean formIsUp;

    private List<Utilisateur> utilisateurList;

    // pour suivre position de l'utilisateur
    private FusedLocationProviderClient fusedLocationClient;
    // Déclaration pour le callback de la mise à jour de la position
    private LocationCallback locationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvDistance = (TextView) view.findViewById(R.id.tv_distance);

        SupportMapFragment mapFragment =(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Callback lors d'une mise à jour de la position de l'utilisateur
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "onLocationResult: " + location.getLatitude());
                    userLocation = location;
                }
            }
        };

        homeViewModel.getAllUtilisateurs().observe(getViewLifecycleOwner(), new Observer<List<Utilisateur>>() {
            @Override
            public void onChanged(List<Utilisateur> utilisateursList) {
                utilisateursListDb.clear();
                utilisateursListDb.addAll(utilisateursList);
                utilisateurAdapter.notifyDataSetChanged();
                setMarkersOnMap();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Mise en place de l'adapter pour le recyclerView
        utilisateursListDb = new ArrayList<>();
        // Envoi une liste vide d'article dans l'adapter
        utilisateurAdapter = new UtilisateurAdapter(utilisateursListDb);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        DrawerActivity.mMap = googleMap;

        DrawerActivity.mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));

        // détection du click sur une fenêtre d'information d'un marqueur
        DrawerActivity.mMap.setOnInfoWindowClickListener(this);
        // Permet de modifier l'apparence de la fenêtre d'information d'un marqueur

        // Permet de détecter le click sur le bouton de position
        DrawerActivity.mMap.setOnMyLocationButtonClickListener(this);
        // Permet de détecter le click sur la position de l'utilisateur
        DrawerActivity.mMap.setOnMyLocationClickListener(this);

        // Détecter click sur la carte
        DrawerActivity.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: " + latLng.toString());
                DrawerActivity.mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Je suis ici !")
                        .draggable(true)
                );
                DrawerActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                // Supprime marker ajouté par le bouton Add
                if (markerCamera != null) {
                    markerCamera.remove();
                }
            }
        });


        // Détecter un click long sur la carte
        /////////////////////////////////////////////
        //VA SERVIR POUR AJOUTER UN MARKER SUR LA MAP
        /////////////////////////////////////////////
        DrawerActivity.mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                formIsUp = !formIsUp;

                DrawerActivity.markerAjoute = latLng;

                getChildFragmentManager().beginTransaction().replace(R.id.fl_fragment_form, new FormulaireAjoutMarker(), "formAjouterMarker")
                        .addToBackStack(null).commit();

            }
        });

        enableMyLocation();

        // Configuration pour mise à jour automatique de la position
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Vérifie les permissions avant d'utiliser le service fusedLocationClient.getLastLocation()
        // qui permet de connaître la dernière position
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);

            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG, "onSuccess: " + location);
                            // Centre la carte sur la position de l'utilisateur au démarrage
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            DrawerActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        }
                    }
                });

        setMarkersOnMap();

    }

    public void closeForm(){
        formIsUp = false;
        FormulaireAjoutMarker formulaireAjoutMarkerRemove = (FormulaireAjoutMarker) getChildFragmentManager()
                .findFragmentByTag("formAjouterMarker");
        getChildFragmentManager()
                .beginTransaction()
                .remove(formulaireAjoutMarkerRemove)
                .commit();
    }

    public void setMarkersOnMap(){
        for (Utilisateur utilisateur : utilisateursListDb){
            LatLng position = new LatLng(utilisateur.getLatitude(), utilisateur.getLongitude());
            DrawerActivity.mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(utilisateur.getMessage()))
                    .setTag(utilisateur);
        }
    }

    private void enableMyLocation() {
        // vérification si la permission de localisation est déjà donnée
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (DrawerActivity.mMap != null) {
                // Permet d'afficher le bouton pour centrer la carte sur la position de l'utilisateur
                DrawerActivity.mMap.setMyLocationEnabled(true);
            }
        } else {
            // La permission est manquante, demande donc la permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }


    // détection du click sur une fenêtre d'information d'un marqueur
    /*
    *  Un clic sur la fenêtre d'information doit afficher la distance entre ce marker
    *  et la position de l’utilisateur dans un TextView.
     */
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        tvDistance.setVisibility(View.VISIBLE);
        LatLng positionMarker = marker.getPosition();
        LatLng positionUser = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        float[] results = new float[1];
        Location.distanceBetween(positionMarker.latitude,positionMarker.longitude, positionUser.latitude,positionUser.longitude, results);
        Float distance = results[0]/1000;
        tvDistance.setText(distance.toString());
    }


    // Permet de détecter le click sur le bouton de position
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // Permet de détecter le click sur la position de l'utilisateur
    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }




}