package ca.cegepgarneau.tp3_google_maps_v2.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;

@Database(entities = {Utilisateur.class}, version = 1, exportSchema = true)
public abstract class UtilisateurRoomDatabase extends RoomDatabase {

    public static UtilisateurRoomDatabase INSTANCE;

    public abstract UtilisateurDao utilisateurDao();

    public static synchronized UtilisateurRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Crée la BDD
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    UtilisateurRoomDatabase.class, "utilisateur_database")
                    .fallbackToDestructiveMigration()
//                    .addCallback(roomDatabaseCallBack)
                    .build();
        }
        return INSTANCE;
    }
}
