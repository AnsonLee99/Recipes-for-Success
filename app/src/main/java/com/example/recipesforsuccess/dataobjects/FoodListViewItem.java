package com.example.recipesforsuccess.dataobjects;

public class FoodListViewItem {

    String name;
    String dateAdded;
    String picture;

    public FoodListViewItem(String name, String dateAdded, String picture) {
        this.name=name;
        this.dateAdded=dateAdded;
        this.picture=picture;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return dateAdded;
    }

}
