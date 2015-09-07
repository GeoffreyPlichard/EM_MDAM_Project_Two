package com.hfad.projet2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hfad.projet2.Models.Trainings;
import com.hfad.projet2.R;

import java.util.ArrayList;

/**
 * Created by Geoffrey on 18/08/15.
 */
public class TrainingsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Trainings> trainings;

    public TrainingsListAdapter(Context context, ArrayList<Trainings> trainings) {
        this.context = context;
        this.trainings = trainings;
    }

    @Override
    public int getCount() {
        return trainings.size();
    }

    @Override
    public Object getItem(int trainingPosition) {
        return trainings.get(trainingPosition);
    }

    @Override
    public long getItemId(int trainingPosition) {
        return trainingPosition;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trainings training = (Trainings) getItem(position);
        if(convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.training_item, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.training_name);
        tv.setText(training.getName());

        return convertView;
    }
}
