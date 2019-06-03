package com.example.recipesforsuccess;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private ArrayList<String> ingredients = new ArrayList<String>();
    private String name;
    private String prep_time;
    private ArrayList<String> steps = new ArrayList<String>();
    private String recipePic;

    public Recipe() {
    }

    public Recipe(ArrayList<String> ingredients, String name, String prep_time, ArrayList<String> steps, String recipePic)
    {
        this.ingredients = ingredients;
        this.name = name;
        this.prep_time = prep_time;
        this.steps = steps;
        this.recipePic = recipePic;
    }

    public ArrayList<String> getIngredients()
    {
        return this.ingredients;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPrepTime()
    {
        return this.prep_time;
    }

    public ArrayList<String> getSteps()
    {
        return this.steps;
    }

    public String getRecipePic()
    {
        return this.recipePic;
    }


}
