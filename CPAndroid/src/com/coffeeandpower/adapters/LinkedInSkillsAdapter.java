package com.coffeeandpower.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coffeeandpower.app.R;
import com.coffeeandpower.cont.UserLinkedinSkills;

public class LinkedInSkillsAdapter extends BaseAdapter {

    private ArrayList<UserLinkedinSkills> skills;
    private LayoutInflater inflater;

    public LinkedInSkillsAdapter(Activity context, ArrayList<UserLinkedinSkills> skills) {

        this.inflater = context.getLayoutInflater();

        if (skills != null) {
            this.skills = skills;
        } else {
            this.skills = new ArrayList<UserLinkedinSkills>();
        }
    }

    @Override
    public int getCount() {
        return skills.size();
    }

    @Override
    public Object getItem(int position) {
        return skills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {

        public TextView textNickName;
        public ToggleButton toggleButtonVisible;

        public ViewHolder(View convertView) {

            this.textNickName = (TextView) convertView
                    .findViewById(R.id.textview_persone_nickname);
            this.toggleButtonVisible = (ToggleButton) convertView
                    .findViewById(R.id.toggleButtonVisible);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_linkedin_skills,
                    null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserLinkedinSkills currentSkill = skills.get(position);
        holder.textNickName.setText(currentSkill.getName());
        holder.toggleButtonVisible.setTag(skills.get(position));
        holder.toggleButtonVisible.setChecked(currentSkill.isVisible());
        return convertView;
    }


}
