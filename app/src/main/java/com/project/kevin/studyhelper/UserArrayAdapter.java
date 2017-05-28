package com.project.kevin.studyhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kevin on 5/22/2017.
 */

public class UserArrayAdapter extends ArrayAdapter<ListInfo> {

    private Context context;
    private int layoutResource;
    private List<ListInfo> userList;

    public UserArrayAdapter(Context context, int resource, List<ListInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.userList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResource, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(userList.get(position).getUsername());

        textView = (TextView) view.findViewById(R.id.cardSet);
        if(userList.get(position).getCardSetName() != ""){
            textView.setText(userList.get(position).getCardSetName());
        }

        CheckBox cb = (CheckBox) view.findViewById(R.id.ready);
        cb.setChecked(userList.get(position).getReady());

        return view;
    }

}
