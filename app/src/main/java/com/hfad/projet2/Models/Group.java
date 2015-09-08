package com.hfad.projet2.Models;

import java.util.ArrayList;

/**
 * Created by Geoffrey on 16/08/15.
 */
public class Group {



    private String _id;
    private String Name;
    private ArrayList<Child> Items;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<Child> getItems() {
            return Items;
        }

    public void setItems(ArrayList<Child> Items) {
            this.Items = Items;
        }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

}
