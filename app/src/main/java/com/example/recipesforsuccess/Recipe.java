package com.example.recipesforsuccess;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private List<String> ingredients = new ArrayList<String>();
    private String name;
    private String prep_time;
    private List<String> steps = new ArrayList<String>();
    private String recipePic;
    private List<String> equipment = new ArrayList<String>();

    public Recipe() {
    }

    public Recipe(List<String> ingredients, String name, String prep_time, List<String> steps, String recipePic,
                  List<String> equipment)
    {
        this.ingredients = ingredients;
        this.name = name;
        this.prep_time = prep_time;
        this.steps = steps;
        this.recipePic = recipePic;
        this.equipment = equipment;
    }

    public List<String> getIngredients()
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

    public List<String> getSteps()
    {
        return this.steps;
    }

    public String getRecipePic()
    {
        return this.recipePic;
    }

    public List<String> getEquipment() { return this.equipment; }
}
