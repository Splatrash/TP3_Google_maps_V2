package ca.cegepgarneau.tp3_google_maps_v2.datahttp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject query = response.getJSONObject("query");
                    JSONArray recentChanges = query.getJSONArray("recentchanges");

                    for (int i = 0; i < recentChanges.length(); i++) {
                        Utilisateur utilisateur = new Utilisateur();
                        try {
                            JSONObject jsonObject = recentChanges.getJSONObject(i);

                            utilisateur.setPicture(jsonObject.getString("picture"));
                            utilisateur.setFirstname(jsonObject.getString("firstname"));
                            utilisateur.setLastname(jsonObject.getString("lastname"));
                            utilisateur.setMessage(jsonObject.getString("message"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        utilisateurs.add(utilisateur);

                    }
                    if (callback != null) callback.processFinished(utilisateurs);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });

        // Add the request to the RequestQueue.
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }
}
