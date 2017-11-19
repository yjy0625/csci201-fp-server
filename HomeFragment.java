package cs201.project.afinal.thetraveler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment {

    public static final String BUNDLE_KEY = "choice";

    private HomeActivity homeActivity;
    private RequestQueue queue;

    //if loginChoice = 2 -> guest, display score 0
    private int userLoginChoice;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(HomeActivity homeActivity, int loginChoice) {
        Log.e("HomeFragment", "New Instance");
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_KEY, loginChoice);
        fragment.homeActivity = homeActivity;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userLoginChoice = getArguments().getInt(BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Log.e("HOME FRAGMENT", "SUCCESS");


        final View fragmentLayout = inflater.inflate(R.layout.fragment_home, container, false);
        Mapbox.getInstance(super.getContext(), "pk.eyJ1IjoibGFyaXNzYWNoaXUiLCJhIjoiY2o5eGRkbGRuMHZmcTJxcG8wcWtwbnBubyJ9.W6Zsc7_FJa8irGHzbNAaXw");


       final MapView mapView = (MapView) fragmentLayout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        String requestUrl = "http://10.0.2.2:8080/csci201-fp-server/rest/map/inArea/latBetween/34/and/35/lonBetween/-119/and/-118";
        //populate map here
        queue = Volley.newRequestQueue(getActivity());
        Log.e("HOMEFRAGMENT", "Entered after making queue");
        //data comes back as an array
        JsonArrayRequest request = new JsonArrayRequest(requestUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {

                            Log.e("TIMELINE FRAGMENT", response.length() + "");
                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(MapboxMap mapboxMap) {


                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject place = (JSONObject) response.get(i);
                                            double lng = place.getDouble("lon");
                                            double lat = place.getDouble("lat");
                                            String title = place.getString("name");
                                            mapboxMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, lng))
                                                    .title(title)

                                            );


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e("TIMELINE FRAGMENT", "ERROR");
                                        }
                                    }
                                }
                            });
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(request);

        //check if this user is a guest and set scores
        if(userLoginChoice == 2) {

        }

        // Inflate the layout for this fragment
//        int points = SignupActivity.user.getScore();

       /* TextView pointsTextView = (TextView) fragmentLayout.findViewById(R.id.home_points);
        pointsTextView.setText(Integer.toString(homeActivity.homeUser.getScore()));

        String level = Integer.toString(homeActivity.homeUser.getScore() / 10 + 1);
        TextView levelTextView = (TextView) fragmentLayout.findViewById(R.id.home_level);
        levelTextView.setText(level);*/

        requestUrl = "http://10.0.2.2:8080/csci201-fp-server/rest/user/rank/id/" + homeActivity.homeUser.getId();

        Log.e("HELLO", requestUrl);
        Request request2 = new StringRequest(Request.Method.GET, requestUrl
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Home Fragment", response);

                TextView rankTextView = (TextView) fragmentLayout.findViewById(R.id.home_ranking);
                rankTextView.setText(response);

            }

        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);




        return fragmentLayout;
    }

    public void addhomeActivity(HomeActivity homeActivity){
        this.homeActivity = homeActivity;
    }

}
