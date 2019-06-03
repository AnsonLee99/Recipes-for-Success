package com.example.recipesforsuccess.dataobjects;

public class FoodListViewItem {

    String name;
    String dateAdded;
    String imgURL;

    public FoodListViewItem(String name, String dateAdded, String imgURL) {
        this.name=name;
        this.dateAdded=dateAdded;
        this.imgURL = imgURL;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return dateAdded;
    }

    public String getImageId() { return imgURL; }
}