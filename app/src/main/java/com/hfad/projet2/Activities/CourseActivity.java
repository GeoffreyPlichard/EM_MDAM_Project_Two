package com.hfad.projet2.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hfad.projet2.Adapters.ExpandListAdapter;
import com.hfad.projet2.Helpers.AppController;
import com.hfad.projet2.Models.Child;
import com.hfad.projet2.Models.Course;
import com.hfad.projet2.Models.Group;
import com.hfad.projet2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CourseActivity extends Activity {

    private ProgressDialog pDialog;
    private String courseId;
    private String trainingUrl;
    private VideoView course_video;
    private String course_title;
    ArrayList<Group> list = new ArrayList<Group>();
    ArrayList<Child> ch_list = new ArrayList<Child>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        trainingUrl = intent.getStringExtra("trainingUrl");

        TextView tv = (TextView) findViewById(R.id.course_id);
        tv.setText(courseId);

        course_video = (VideoView) findViewById(R.id.course_video);
        course_video.setMediaController(new MediaController(this));

        // BOITE DE DIALOGUE
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Chargement...");
        pDialog.setCancelable(false);

        makeJsonObjectRequest();




        course_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(CourseActivity.this);

                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();

                String json = gson.toJson(list);

                editor.putString("historyString", json);
                editor.commit();


                Intent intent = new Intent(CourseActivity.this, HistoryActivity.class);
                intent.putExtra("history", "test");
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void makeJsonObjectRequest() {
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(trainingUrl ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // TITRE
                            Group gru = new Group();
                            gru.setName(response.getString("title"));
                            gru.setId(response.getString("ean13"));
                            list.add(gru);

                            // SOMMAIRE
                            JSONArray items = response.getJSONArray("items");

                            // Récupération de l'objet dans une ArrayList pour pouvoir la réutiliser par la suite
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject childItems = items.getJSONObject(i);
                                if(childItems.getString("_id").equals(courseId)) {
                                    JSONObject videoObj = (JSONObject) childItems.getJSONArray("field_video").get(0);
                                    course_video.setVideoPath(videoObj.getString("filepath"));
                                    Child ch = new Child();
                                    ch.setName(childItems.getString("title"));
                                    ch.setId(childItems.getString("_id"));
                                    ch_list.add(ch);


                                }
                            }
                            gru.setItems(ch_list);
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
