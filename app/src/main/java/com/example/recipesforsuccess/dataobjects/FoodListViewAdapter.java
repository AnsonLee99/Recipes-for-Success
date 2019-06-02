package com.example.recipesforsuccess.dataobjects;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipesforsuccess.R;

import java.util.ArrayList;

public class FoodListViewAdapter extends ArrayAdapter<FoodListViewItem> implements View.OnClickListener{

    private ArrayList<FoodListViewItem> dataSet;
    Context mContext;
    boolean isEditing;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtDate;
        ImageView foodImage;
        ImageView infoImage;
    }

    public FoodListViewAdapter(ArrayList<FoodListViewItem> data, Context context, boolean isEditing) {
        super(context, R.layout.food_list_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.isEditing = isEditing;
    }

    @Override
    public void onClick(View v) {

        Log.i("VIEW_INFO", v.toString());

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        FoodListViewItem fooditem=(FoodListViewItem) object;

        switch (v.getId())
        {
            case R.id.info_image:
                Snackbar.make(v, "Food Name: " +fooditem.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FoodListViewItem fooditem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // choose to show editing or non-editing list items
            int layoutType = isEditing ? R.layout.food_list_item_editing : R.layout.food_list_item;
            convertView = inflater.inflate(layoutType, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.food_name);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.food_date);
            viewHolder.foodImage = (ImageView) convertView.findViewById(R.id.food_image);
            viewHolder.infoImage = (ImageView) convertView.findViewById(R.id.info_image);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(fooditem.getName());
        viewHolder.txtDate.setText(fooditem.getDate());
        viewHolder.infoImage.setOnClickListener(this);
        viewHolder.infoImage.setTag(position);
        viewHolder.foodImage.setImageResource(fooditem.getImageId());;
        // Return the completed view to render on screen
        //return convertView;
        return result;
    }
}