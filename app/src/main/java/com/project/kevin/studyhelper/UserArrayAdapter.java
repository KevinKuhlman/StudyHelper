package com.project.kevin.studyhelper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin on 5/22/2017.
 */

public class UserArrayAdapter extends ArrayAdapter<User> {

    private Context context;
    private int layoutResource;
    private List<User> userList;
    private HashMap<Integer, CardSet> cardSetHashMap;

    public UserArrayAdapter(Context context, int resource, List<User> objects, HashMap<Integer, CardSet> cardSetHashMap) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.userList = objects;
        this.cardSetHashMap = cardSetHashMap;
        Log.d("Tag", "Tag");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResource, parent, false);

        Log.d("Tag", position+" test");

        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(userList.get(position).getUsername());

        if(cardSetHashMap.containsKey(position)){
            TextView cardSetTextView = (TextView) view.findViewById(R.id.cardSet);
            cardSetTextView.setText(cardSetHashMap.get(position).getName());
        }


        return view;
    }

}
