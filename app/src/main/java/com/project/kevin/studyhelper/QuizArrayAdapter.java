package com.project.kevin.studyhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kevin on 5/28/2017.
 */
public class QuizArrayAdapter extends ArrayAdapter<QuizInfo> {

    private Context context;
    private int layoutResource;
    private List<QuizInfo> userList;

    public QuizArrayAdapter(Context context, int resource, List<QuizInfo> objects) {
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

        TextView textView2 = (TextView) view.findViewById(R.id.answer);
        textView2.setText(userList.get(position).getAnswer());

        return view;
    }

}
