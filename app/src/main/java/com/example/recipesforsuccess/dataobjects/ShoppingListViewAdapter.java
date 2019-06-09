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

import com.example.recipesforsuccess.ShoppingList;
import com.example.recipesforsuccess.R;

import java.util.ArrayList;

public class ShoppingListViewAdapter extends ArrayAdapter<ShoppingListViewItem> implements View.OnClickListener{

    private ArrayList<ShoppingListViewItem> dataSet;
    Context mContext;
    boolean isEditing;
    private ShoppingList.ShoppingDeleter shoppingDeleter;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView deleteImage;
    }

    public ShoppingListViewAdapter(ArrayList<ShoppingListViewItem> data, Context context, boolean isEditing, ShoppingList.ShoppingDeleter shoppingDeleter) {
        super(context, R.layout.food_list_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.isEditing = isEditing;
        this.shoppingDeleter = shoppingDeleter;
    }

    @Override
    public void onClick(View v) {

        Log.i("VIEW_INFO", v.toString());

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ShoppingListViewItem fooditem=(ShoppingListViewItem) object;

        switch (v.getId())
        {
            case R.id.info_image:
                Snackbar.make(v, "Food Name: " +fooditem.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;

            // If user presses the delete button
            case R.id.delete_image:
                Snackbar.make(v, fooditem.getName() + " deleted", Snackbar.LENGTH_SHORT)
                        .setAction("No action", null).show();

                // delete item from firebase
                shoppingDeleter.setItem(fooditem);
                try {
                    Log.d("delete", "deleting in adapter");
                    shoppingDeleter.call();
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
        ShoppingListViewItem fooditem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            // choose to show editing or non-editing list items
            int layoutType = R.layout.shopping_list_item;
            convertView = inflater.inflate(layoutType, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.food_name);
            viewHolder.deleteImage = (ImageView) convertView.findViewById(R.id.delete_image);

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
        viewHolder.deleteImage.setOnClickListener(this);
        viewHolder.deleteImage.setTag(position);
        // Return the completed view to render on screen
        //return convertView;
        return result;
    }
}
