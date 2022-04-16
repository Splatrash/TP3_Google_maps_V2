package ca.cegepgarneau.tp3_google_maps_v2.datahttp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ca.cegepgarneau.tp3_google_maps_v2.model.Utilisateur;

public class VolleyUtils {

    private static final String TAG = "tag";
    private ArrayList<Utilisateur> utilisateurs = new ArrayList<>();

    public interface ListUtilisateursAsyncResponse {
        void processFinished(ArrayList<Utilisateur> utilisateurArrayList);
    }

    public void getUtilisateurs(Context context, ListUtilisateursAsyncResponse callback) {
        String url = "https://onoup.site/data.json";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                    for (int i = 0; i < response.length(); i++) {
                        Utilisateur utilisateur = new Utilisateur();
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            utilisateur.setPicture(jsonObject.getString("picture"));
                            utilisateur.setFirstname(jsonObject.getString("firstname"));
                            utilisateur.setLastname(jsonObject.getString("lastname"));
                            utilisateur.setMessage(jsonObject.getString("message"));
                            utilisateur.setLatitude(jsonObject.getDouble("latitude"));
                            utilisateur.setLongitude(jsonObject.getDouble("longitude"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        utilisateurs.add(utilisateur);


                    if (callback != null) callback.processFinished(utilisateurs);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });

        // Add the request to the RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }
}
