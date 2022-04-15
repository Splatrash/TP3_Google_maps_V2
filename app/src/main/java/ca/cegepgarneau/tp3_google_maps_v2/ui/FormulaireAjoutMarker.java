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
import ca.cegepgarneau.tp3_google_maps_v2.ui.home.HomeFragment;

public class FormulaireAjoutMarker extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_ajouter_marker, container, false);

        Button btnAjouter = view.findViewById(R.id.btn_ajouter);
        EditText etMessage = view.findViewById(R.id.et_message);

        HomeFragment homeFragment = ((HomeFragment)FormulaireAjoutMarker.this.getParentFragment());

        btnAjouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString().trim();

                if (!"".equals(message))
                {
                    ((DrawerActivity)getActivity()).AjouterUnMarker(message);
                }
                homeFragment.closeForm();
            }
        });
        Button btnAnnulerPopup = view.findViewById(R.id.btn_annuler);
        btnAnnulerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                homeFragment.closeForm();

            }
        });
        return view;
    }
}
