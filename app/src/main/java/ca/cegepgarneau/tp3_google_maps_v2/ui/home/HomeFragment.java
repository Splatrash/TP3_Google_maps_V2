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
import ca.cegepgarneau.tp3_google_maps_v2.databinding.FragmentHomeBinding;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;
import ca.cegepgarneau.tp3_google_maps_v2.ui.FormulaireAjoutMarker;
import ca.cegepgarneau.tp3_google_maps_v2.ui.UtilisateurAdapter;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String TAG = "HOME";

    private FragmentHomeBinding binding;

    public static TextView tvDistance;

    private HomeViewModel homeViewModel;
    private ArrayList<Utilisateur> utilisateursListDb;
    private UtilisateurAdapter utilisateurAdapter;

    private Location userLocation;

    private boolean formIsUp;

    private List<Utilisateur> utilisateurList;

    // pour suivre position de l'utilisateur
    private FusedLocationProviderClient fusedLocationClient;
    // D??claration pour le callback de la mise ?? jour de la position
    private LocationCallback locationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvDistance = (TextView) view.findViewById(R.id.tv_distance);

        SupportMapFragment mapFragment =(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Callback lors d'une mise ?? jour de la position de l'utilisateur
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

        // d??tection du click sur une fen??tre d'information d'un marqueur
        DrawerActivity.mMap.setOnInfoWindowClickListener(this);
        // Permet de modifier l'apparence de la fen??tre d'information d'un marqueur

        // Permet de d??tecter le click sur le bouton de position
        DrawerActivity.mMap.setOnMyLocationButtonClickListener(this);
        // Permet de d??tecter le click sur la position de l'utilisateur
        DrawerActivity.mMap.setOnMyLocationClickListener(this);

        // D??tecter click sur la carte
        DrawerActivity.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                DrawerActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }
        });


        // D??tecter un click long sur la carte
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

        // Configuration pour mise ?? jour automatique de la position
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
                            // Centre la carte sur la position de l'utilisateur au d??marrage
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            DrawerActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                            if(DrawerActivity.goToMarker != null){
                                DrawerActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DrawerActivity.goToMarker, 13));
                                DrawerActivity.goToMarker = null;
                            }
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
                    .title(utilisateur.getMessage())
                    .snippet(utilisateur.getPicture()))
                    .setTag(utilisateur);
        }
    }

    private void enableMyLocation() {
        // v??rification si la permission de localisation est d??j?? donn??e
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


    // d??tection du click sur une fen??tre d'information d'un marqueur
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

    // Permet de d??tecter le click sur le bouton de position
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    // Permet de d??tecter le click sur la position de l'utilisateur
    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }




}