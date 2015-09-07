package com.hfad.projet2.Fragments;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hfad.projet2.Adapters.TrainingsListAdapter;
import com.hfad.projet2.Helpers.AppController;
import com.hfad.projet2.Models.Trainings;
import com.hfad.projet2.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingListFragment extends Fragment {

    private String trainingsUrl;
    private ProgressDialog pDialog;

    ArrayList<Trainings> list = new ArrayList<Trainings>();

    private TrainingsListAdapter TrainAdapter;
    private ListView TrainList;


    public TrainingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_training_list, container, false);
        TrainList = (ListView) view.findViewById(R.id.training_list);

        Bundle bundle = getArguments();
        String childId = bundle.getString("childId");
        trainingsUrl = String.format("http://eas.elephorm.com/api/v1/subcategories/%1$s/trainings", childId);


        // BOITE DE DIALOGUE
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Chargement...");
        pDialog.setCancelable(false);

        makeJsonArrayRequest();

        TrainList.setOnItemClickListener(new TrainingClickListener());

        return view;
    }


    private class TrainingClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {

            selectItem(list.get(position).getCode());
        }
    }

    private void selectItem(String trainingCode) {
        Bundle args = new Bundle();
        args.putString("trainingCode", trainingCode);

        Fragment fragment;
        fragment = new TrainingDetailFragment();
        fragment.setArguments(args);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Ici on ajoute le tag "visible fragment" en dernier paramètre (voir fragment manager plus loin)
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        // Remplace le fragment avec une transition fade
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void makeJsonArrayRequest() {
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(trainingsUrl ,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject objResponse = (JSONObject) response.get(i);

                                // Récupérer trainings
                                Trainings training = new Trainings();
                                training.setName(objResponse.getString("title"));
                                training.setCode(objResponse.getString("ean13"));

                                list.add(training);
                            }

                            TrainAdapter = new TrainingsListAdapter(getActivity(), list);
                            TrainList.setAdapter(TrainAdapter);
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            /*Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();*/
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();*/
                pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(req, "jreq");
    }


}
