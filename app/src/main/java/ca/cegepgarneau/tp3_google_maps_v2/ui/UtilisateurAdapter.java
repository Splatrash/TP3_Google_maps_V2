package ca.cegepgarneau.tp3_google_maps_v2.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cegepgarneau.tp3_google_maps_v2.R;
import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;

public class UtilisateurAdapter extends RecyclerView.Adapter<UtilisateurAdapter.ViewHolder> {

    private final ArrayList<Utilisateur> utilisateurs;

    public UtilisateurAdapter(ArrayList<Utilisateur> utilisateurs) { this.utilisateurs = utilisateurs; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.utilisateur_one_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utilisateur utilisateur = utilisateurs.get(position);
        // TODO: 2022-04-14 Changer l'icon de l'image 
        //////////////////////////////////////////////////////////
        //holder.imgPicture.(utilisateur.getPicture().toString());
        //////////////////////////////////////////////////////////
        holder.tvFirstName.setText(utilisateur.getFirstname());
        holder.tvLastName.setText(utilisateur.getLastname());
        holder.tvMessage.setText(utilisateur.getMessage());
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
