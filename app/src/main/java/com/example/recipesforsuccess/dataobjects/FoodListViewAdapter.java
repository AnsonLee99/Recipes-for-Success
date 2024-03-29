package com.example.recipesforsuccess.dataobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipesforsuccess.Basket;
import com.example.recipesforsuccess.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FoodListViewAdapter extends ArrayAdapter<FoodListViewItem> implements View.OnClickListener{

    private ArrayList<FoodListViewItem> dataSet;
    private Context mContext;
    private boolean isEditing;
    private Basket.BasketDeleter basketDeleter;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtDate;
        ImageView foodImage;
        //ImageView infoImage;
        ImageView deleteImage;
    }

    public FoodListViewAdapter(ArrayList<FoodListViewItem> data, Context context, boolean isEditing, Basket.BasketDeleter basketDeleter) {
        super(context, R.layout.food_list_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.isEditing = isEditing;
        this.basketDeleter= basketDeleter;
    }

    public void refreshView() {
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        Log.i("VIEW_INFO", v.toString());

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        FoodListViewItem fooditem=(FoodListViewItem) object;

        switch (v.getId())
        {
            // If user presses the delete button
            case R.id.delete_image:
                Snackbar.make(v, fooditem.getName() + " deleted", Snackbar.LENGTH_SHORT)
                        .setAction("No action", null).show();

                // delete item from firebase
                basketDeleter.setItem(fooditem);
                try {
                    Log.d("delete", "deleting in adapter");
                    basketDeleter.call();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                // remove item from current storage
                dataSet.remove(fooditem);
                this.notifyDataSetChanged();

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
            //viewHolder.infoImage = (ImageView) convertView.findViewById(R.id.info_image);
            viewHolder.deleteImage = (ImageView) convertView.findViewById(R.id.delete_image);


            result=convertView;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;
        viewHolder.txtName.setText(fooditem.getName());
        viewHolder.txtDate.setText(fooditem.getDate());
        //viewHolder.infoImage.setOnClickListener(this);
        //viewHolder.infoImage.setTag(position);
        viewHolder.deleteImage.setOnClickListener(this);
        viewHolder.deleteImage.setTag(position);
        //viewHolder.foodImage.setImageResource(fooditem.getImageId());

        viewHolder.foodImage.setImageBitmap(fooditem.getImage());

        return result;
    }


}