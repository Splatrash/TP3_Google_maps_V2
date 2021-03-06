package ca.cegepgarneau.tp3_google_maps_v2.ui.gallery;

import android.content.Context;

import android.os.Bundle;

import java.io.File;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.cegepgarneau.tp3_google_maps_v2.R;
import ca.cegepgarneau.tp3_google_maps_v2.data.AppExecutors;
import ca.cegepgarneau.tp3_google_maps_v2.data.UtilisateurRoomDatabase;
import ca.cegepgarneau.tp3_google_maps_v2.databinding.FragmentGalleryBinding;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;
import ca.cegepgarneau.tp3_google_maps_v2.ui.UtilisateurAdapter;
import ca.cegepgarneau.tp3_google_maps_v2.datahttp.VolleyUtils;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private RecyclerView rvUtilisateurs;
    private ArrayList<Utilisateur> utilisateurs;
    private UtilisateurAdapter utilisateurAdapter;
    private UtilisateurRoomDatabase mDb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mDb = UtilisateurRoomDatabase.getDatabase(getActivity());

        galleryViewModel.getAllUtilisateurs().observe(getViewLifecycleOwner(), new Observer<List<Utilisateur>>() {
            @Override
            public void onChanged(List<Utilisateur> utilisateursList) {
                utilisateurs.clear();
                utilisateurs.addAll(utilisateursList);
                utilisateurAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUtilisateurs = view.findViewById(R.id.rv_utilisateurs);

        utilisateurs = new ArrayList<>();
        utilisateurAdapter = new UtilisateurAdapter(utilisateurs);

        rvUtilisateurs.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvUtilisateurs.setAdapter(utilisateurAdapter);

        refresh();
    }

    private static boolean doesDatabaseExist(Context context, String db_name) {
        File dbFile = context.getDatabasePath(db_name);
        return dbFile.exists();
    }

    public void refresh() {
        if (!doesDatabaseExist(getContext(), "utilisateur_database")){
            new VolleyUtils().getUtilisateurs(getContext(), new VolleyUtils.ListUtilisateursAsyncResponse() {
                @Override
                public void processFinished(ArrayList<Utilisateur> utilisateurArrayList) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //Pour ??viter un: java.util.ConcurrentModificationException
                            List<Utilisateur> listUtilisateurTempo = new ArrayList<Utilisateur>(utilisateurArrayList);

                            mDb.utilisateurDao().deleteAllUtilisateurs();
                            // Ins??re les utilisateurs dans la bd
                            for (Utilisateur utilisateur : listUtilisateurTempo)
                                mDb.utilisateurDao().insert(utilisateur);

                        }
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}