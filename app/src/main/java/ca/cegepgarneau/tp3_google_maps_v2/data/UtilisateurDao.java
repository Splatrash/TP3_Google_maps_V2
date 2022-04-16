package ca.cegepgarneau.tp3_google_maps_v2.data;

import androidx.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;

@Dao
public interface UtilisateurDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Utilisateur utilisateur);

    @Update
    void update(Utilisateur utilisateur);

    @Query("DELETE FROM utilisateur_table")
    void deleteAllUtilisateurs();

    @Query("SELECT * FROM utilisateur_table ")
    LiveData<List<Utilisateur>> getAllUtilisateurs();

}
