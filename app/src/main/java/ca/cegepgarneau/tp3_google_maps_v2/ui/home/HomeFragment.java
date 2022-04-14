package ca.cegepgarneau.tp3_google_maps_v2.ui.home;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ca.cegepgarneau.tp3_google_maps_v2.R;
import ca.cegepgarneau.tp3_google_maps_v2.databinding.FragmentHomeBinding;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final String TAG = "TAG";
    private GoogleMap mMap;
    private FragmentHomeBinding binding;

    private Location userLocation;

    private Marker markerCamera;

    private List<Utilisateur> utilisateurList;

    // pour suivre position de l'utilisateur
    private FusedLocationProviderClient fusedLocationClient;
    // Déclaration pour le callback de la mise à jour de la position
    private LocationCallback locationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFragment =(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        return view;

        /*HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Service pour suivre la position de l'utilisateur
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}