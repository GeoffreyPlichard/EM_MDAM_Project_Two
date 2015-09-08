package com.hfad.projet2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hfad.projet2.Adapters.ExpandListAdapter;
import com.hfad.projet2.Models.Group;
import com.hfad.projet2.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryActivity extends Activity {

    private ExpandListAdapter ExpAdapter;
    private ExpandableListView ExpandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Expandable history list
        ExpandList = (ExpandableListView) findViewById(R.id.history_list);

        Intent intent = getIntent();
        final String History = intent.getStringExtra("history");

        TextView tv = (TextView) findViewById(R.id.test_history);
        tv.setText(History);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        String json = sharedPrefs.getString("historyString", null);
        Type type = new TypeToken<ArrayList<Group>>() {}.getType();
        ArrayList<Group> list = gson.fromJson(json, type);
        System.out.println("HISTORY HISTORY HISTORY HISTORY HISTORY HISTORY HISTORY");
        System.out.println(json);
        ExpAdapter = new ExpandListAdapter(this, list);
        ExpandList.setAdapter(ExpAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
}
