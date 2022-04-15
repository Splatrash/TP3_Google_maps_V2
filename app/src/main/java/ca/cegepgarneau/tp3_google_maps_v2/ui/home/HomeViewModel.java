package ca.cegepgarneau.tp3_google_maps_v2.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ca.cegepgarneau.tp3_google_maps_v2.data.UtilisateurRoomDatabase;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;

public class HomeViewModel extends AndroidViewModel {

    private LiveData<List<Utilisateur>> allUtilisateurs;
    private UtilisateurRoomDatabase mDb;

    public HomeViewModel(Application application) {
        super(application);
        mDb = UtilisateurRoomDatabase.getDatabase(application);
        allUtilisateurs = mDb.utilisateurDao().getAllUtilisateurs();
    }

    public LiveData<List<Utilisateur>> getAllUtilisateurs() { return allUtilisateurs; }
}