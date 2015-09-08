package com.hfad.projet2.Fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.projet2.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //ImageView homeScreen = (ImageView) view.findViewById(R.id.home_screen);
        //homeScreen.setImageResource(R.drawable.elephorm);
        //Drawable xxx = getResources().getDrawable(R.drawable.elephorm);
        //homeScreen.setImageDrawable(xxx);
        return view;
    }


}
