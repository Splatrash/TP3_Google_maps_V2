package ca.cegepgarneau.tp3_google_maps_v2.ui.home;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.squareup.picasso.Picasso;

import ca.cegepgarneau.tp3_google_maps_v2.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;

    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowInfo(Marker marker, View view){
        String message = marker.getTitle();
        String pictureURL = marker.getSnippet();
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message_map);
        if(!message.equals("")){
            tvMessage.setText(message);
        }

        ImageView imgPicture = (ImageView) view.findViewById(R.id.img_utilisateur_map);

        //////////////////
        // REQUIERT PARFOIS DE FERMER ET RÉOUVRIR L'INFO WINDOW POUR QUE L'IMAGE APPARAISSE.
        ////////////////
        Picasso.get().load(pictureURL).into(imgPicture);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        renderWindowInfo(marker, mWindow);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        renderWindowInfo(marker, mWindow);
        return mWindow;
    }
}
