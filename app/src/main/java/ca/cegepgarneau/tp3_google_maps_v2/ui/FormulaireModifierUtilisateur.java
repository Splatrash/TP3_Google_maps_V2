package ca.cegepgarneau.tp3_google_maps_v2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.cegepgarneau.tp3_google_maps_v2.DrawerActivity;
import ca.cegepgarneau.tp3_google_maps_v2.R;
public class FormulaireModifierUtilisateur extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_modifier_info_user, container, false);

        Button btnModifierUtilisateur = view.findViewById(R.id.btn_modifier_utilisateur);

        EditText etPrenom = view.findViewById(R.id.et_prenom_utilisateur);
        EditText etNom = view.findViewById(R.id.et_nom_utilisateur);

        //Action lorsque le bouton ajouter ou modifier du formulaire est cliqué.
        btnModifierUtilisateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prenom = etPrenom.getText().toString().trim();
                String nom = etNom.getText().toString().trim();

                if (!"".equals(prenom) || !"".equals(nom))
                {
                    ((DrawerActivity)getActivity()).ModifierUtilisateur(prenom, nom);
                    ((DrawerActivity)getActivity()).closeForm();
                }
            }
        });
        //Action lorsque le bouton annuler du formulaire est cliqué.
        Button btnAnnulerPopup = view.findViewById(R.id.btn_annuler_modif_utilisateur);
        btnAnnulerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerActivity)getActivity()).closeForm();
            }
        });
        return view;
    }
}
