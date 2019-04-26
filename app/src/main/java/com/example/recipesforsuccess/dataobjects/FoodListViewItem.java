package com.example.recipesforsuccess.dataobjects;

public class FoodListViewItem {

    String name;
    String dateAdded;
    int imageid;

    public FoodListViewItem(String name, String dateAdded, int imageid) {
        this.name=name;
        this.dateAdded=dateAdded;
        this.imageid=imageid;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return dateAdded;
    }

    public int getImageId() { return imageid; }
}
