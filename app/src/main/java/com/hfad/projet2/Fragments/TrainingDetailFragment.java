package com.hfad.projet2.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.ExpandableListView;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hfad.projet2.Activities.CourseActivity;
import com.hfad.projet2.Activities.HistoryActivity;
import com.hfad.projet2.Adapters.ExpandListAdapter;
import com.hfad.projet2.Helpers.AppController;
import com.hfad.projet2.Models.Child;
import com.hfad.projet2.Models.Course;
import com.hfad.projet2.Models.Group;
import com.hfad.projet2.Models.Training;
import com.hfad.projet2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingDetailFragment extends Fragment {

    private String trainingUrl;
    private String videoUrl;
    private TextView training_title;
    private VideoView training_video;
    private ProgressDialog pDialog;
    private Training training = new Training();

    ArrayList<Group> list = new ArrayList<Group>();
    ArrayList<Child> ch_list;
    ArrayList<Course> courses = new ArrayList<Course>();

    private ExpandListAdapter ExpAdapter;
    private ExpandableListView ExpandList;


    public TrainingDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = getActivity();



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_detail, container, false);

        // Expandable Summary
        ExpandList = (ExpandableListView) view.findViewById(R.id.training_summary);

        Bundle bundle = getArguments();
        String trainingCode = bundle.getString("trainingCode");

        trainingUrl = String.format("http://eas.elephorm.com/api/v1/trainings/%1$s", trainingCode);

        training_title = (TextView) view.findViewById(R.id.training_title);

        training_video = (VideoView) view.findViewById(R.id.training_video);
        training_video.setMediaController(new MediaController(context));


        // BOITE DE DIALOGUE
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Chargement...");
        pDialog.setCancelable(false);

        makeJsonObjectRequest();

        ExpandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPosition, int childPosition, long l) {
                String courseId = list.get(parentPosition).getItems().get(childPosition).getId();
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("trainingUrl", trainingUrl);
                startActivity(intent);
                return false;
            }
        });

        training_video.start();

        training_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                System.out.println("C'est fini :(");
                saveHistory();
            }
        });

        return view;
    }

    private void saveHistory() {
        System.out.println(training.getName());
    }

    private void makeJsonObjectRequest() {
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(trainingUrl ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // TRAINING TITLE
                            training.setName(response.getString("title"));
                            training_title.setText(response.getString("title"));

                            // TRAINING VIDEO
                            JSONObject teaserInfo = response.getJSONObject("teaser_info");
                            videoUrl = String.format("http://videos.elephorm.com/%1$s/video", teaserInfo.getString("video_url"));
                            training_video.setVideoURI(Uri.parse(videoUrl));


                            // SOMMAIRE
                            JSONArray items = response.getJSONArray("items");

                            // Récupération de l'objet dans une ArrayList pour pouvoir la réutiliser par la suite
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject childItems = items.getJSONObject(i);
                                Course course = new Course();
                                course.setName(childItems.getString("title"));
                                course.set_id(childItems.getString("_id"));
                                courses.add(course);
                            }

                            // i = 1 pour éviter d'afficher le premier objet qui correspond à la liste des chapitres
                            for (int i = 1; i < items.length(); i++) {
                                JSONObject childItems = items.getJSONObject(i);
                                JSONArray children = childItems.getJSONArray("children");
                                if(childItems.getString("type").equals("chapter")){
                                    Group gru = new Group();
                                    gru.setName(childItems.getString("title"));
                                    list.add(gru);
                                    ch_list = new ArrayList<Child>();
                                    for (int j = 0; j < children.length(); j++) {
                                        Child ch = new Child();
                                        for(int k = 0; k < courses.size(); k++) {
                                            if(children.get(j).equals(courses.get(k).get_id())) {
                                                ch.setName(courses.get(k).getName());
                                                ch.setId(courses.get(k).get_id());
                                            }
                                        }
                                        ch_list.add(ch);
                                    }
                                    gru.setItems(ch_list);
                                }

                            }

                            ExpAdapter = new ExpandListAdapter(getActivity(), list);
                            ExpandList.setAdapter(ExpAdapter);
                            pDialog.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
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
