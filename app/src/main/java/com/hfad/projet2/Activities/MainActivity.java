package com.hfad.projet2.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.Configuration;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hfad.projet2.Adapters.ExpandListAdapter;
import com.hfad.projet2.Fragments.HomeFragment;
import com.hfad.projet2.Fragments.TrainingListFragment;
import com.hfad.projet2.Helpers.AppController;
import com.hfad.projet2.Models.Child;
import com.hfad.projet2.Models.Group;
import com.hfad.projet2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends Activity {


    private String categoriesUrl = "http://eas.elephorm.com/api/v1/categories";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    private ProgressDialog pDialog;

    ArrayList<Group> list = new ArrayList<Group>();
    ArrayList<Child> ch_list;

    private ExpandListAdapter ExpAdapter;
    private ExpandableListView ExpandList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EXPANDABLE LIST
        ExpandList = (ExpandableListView) findViewById(R.id.left_drawer);

        // BOITE DE DIALOGUE
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Chargement...");
        pDialog.setCancelable(false);

        // NAVIGATION DRAWER
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        makeJsonArrayRequest();

        // Ajouter une instance de OnItemClickListener à la ListView
        ExpandList.setOnChildClickListener(new DrawerItemClickListener());
        // Si la MainActivity vient d'être créée, on affiche le fragment HOME sinon on réafecte le bon titre de la barre
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
            //setActionBarTitle(currentPosition);
        } else {
            //selectItem(0);
        }


        /**
         * Action bar drawer toggle
         */

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Set à recréer the menu items pour pouvoir changer sa visibilité
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Set à recréer the menu items pour pouvoir changer sa visibilité
                invalidateOptionsMenu();
            }
        };

        // Créer toogle drawer
        drawerLayout.setDrawerListener(drawerToggle);

        // Enable the UP icon
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        /**
         * Règle le problème du back button, récupère le fragment précédent
         */

        /*getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    // Appelé quand le Back Stack change (quand on revient en arrière avec le Back Button)
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getFragmentManager();
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment");

                        // Check à quelle instance appartient le fragment actuellement attaché à l'activité
                        if (fragment instanceof HomeFragment) {
                            currentPosition = 0;
                        }


                        // Set le titre de l'action bar et met en surbrillance l'onglet
                        //setActionBarTitle(currentPosition);
                        drawerList.setItemChecked(currentPosition, true);
                    }
                }
        );*/

        Fragment fragment;
        fragment = new HomeFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Ici on ajoute le tag "visible fragment" en dernier paramètre (voir fragment manager plus loin)
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        // Remplace le fragment avec une transition fade
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();


    }



    private class DrawerItemClickListener implements ExpandableListView.OnChildClickListener {
        /*@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Au clique sur un item
            System.out.println("TEST");
            //setActionBarTitle(position);
            //String test = categoriesList.get(position).getSubcat().toString();
            //System.out.println(categoriesList.get(position).getSubcat().toString());
            selectItem(position);
        }*/

        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPosition, int childPosition, long l) {
            String childId = list.get(parentPosition).getItems().get(childPosition).getId();
            selectItem(childId);
            return false;
        }
    };



    private void selectItem(String childId) {
        //currentPosition = position; // Sauvegarde la position pour la resortir si le vue est destroy (rotation écran)
        Bundle args = new Bundle();
        args.putString("childId", childId);

        Fragment fragment;
        fragment = new TrainingListFragment();
        fragment.setArguments(args);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Ici on ajoute le tag "visible fragment" en dernier paramètre (voir fragment manager plus loin)
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        // Remplace le fragment avec une transition fade
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        // Setter le titre de l'action bar
        //setActionBarTitle(position);

        // Dire au drawer layout de fermer la list layout qu'il contient
        drawerLayout.closeDrawer(ExpandList);
    }



    private void setActionBarTitle(int position) {
        //getActionBar().setTitle(ExpandList.getItemAtPosition(position));
    }



    /**
     * Synchroniser l'état de ActionBarDrawerToggle avec l'état du drawer
     * @param savedInstanceState
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate le menu, ajoute des items au menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_history:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Appelé à chaque fois que invalidateOptionMenu() est appelé
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Si le drawer est ouvert, cacher le menu relatif au content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(ExpandList);
        return super.onPrepareOptionsMenu(menu);
    }




    /**
     * Sauvegarder la "position" au cas où l'activité est destroy
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }


    private void makeJsonArrayRequest() {
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(categoriesUrl ,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject objResponse = (JSONObject) response.get(i);

                                // Récupérer catégories
                                Group gru = new Group();
                                gru.setName(objResponse.getString("title"));

                                // Récupérer sous-catégories
                                JSONArray childArray = objResponse.getJSONArray("subcategories");
                                ch_list = new ArrayList<Child>();
                                for (int j = 0; j < childArray.length(); j++) {
                                    JSONObject childObject = childArray.getJSONObject(j);
                                    Child ch = new Child();
                                    ch.setName(childObject.getString("title"));
                                    ch.setId(childObject.getString("_id"));
                                    ch_list.add(ch);
                                }

                                gru.setItems(ch_list);
                                list.add(gru);
                            }

                            ExpAdapter = new ExpandListAdapter(MainActivity.this, list);
                            ExpandList.setAdapter(ExpAdapter);
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(req, "jreq");
    }


}
