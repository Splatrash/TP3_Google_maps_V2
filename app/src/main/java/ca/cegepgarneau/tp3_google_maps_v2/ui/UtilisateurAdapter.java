package ca.cegepgarneau.tp3_google_maps_v2.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ca.cegepgarneau.tp3_google_maps_v2.DrawerActivity;
import ca.cegepgarneau.tp3_google_maps_v2.R;
import ca.cegepgarneau.tp3_google_maps_v2.data.AppExecutors;
import ca.cegepgarneau.tp3_google_maps_v2.data.UtilisateurRoomDatabase;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;
import ca.cegepgarneau.tp3_google_maps_v2.ui.gallery.GalleryFragment;

public class UtilisateurAdapter extends RecyclerView.Adapter<UtilisateurAdapter.ViewHolder> {

    private final ArrayList<Utilisateur> utilisateurs;

    private UtilisateurRoomDatabase mDb;

    public UtilisateurAdapter(ArrayList<Utilisateur> utilisateurs) { this.utilisateurs = utilisateurs; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utilisateur_one_line, parent, false);
        mDb = UtilisateurRoomDatabase.getDatabase(parent.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utilisateur utilisateur = utilisateurs.get(position);
        // TODO: 2022-04-14 Changer l'icon de l'image 
        //////////////////////////////////////////////////////////
        Picasso.get().load(utilisateur.getPicture()).into(holder.imgPicture);
        //////////////////////////////////////////////////////////
        holder.tvFirstName.setText(utilisateur.getFirstname());
        holder.tvLastName.setText(utilisateur.getLastname());
        holder.tvMessage.setText(utilisateur.getMessage());

        //Écouter un clic simple sur une tâche
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        LatLng latlng = new LatLng(utilisateur.getLatitude(),utilisateur.getLongitude());
                        DrawerActivity.goToMarker = latlng;
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return utilisateurs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFirstName, tvLastName, tvMessage;
        private ImageView imgPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPicture = itemView.findViewById(R.id.img_utilisateur);
            tvFirstName = itemView.findViewById(R.id.tv_firstname);
            tvLastName = itemView.findViewById(R.id.tv_lastname);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }
    }

}
