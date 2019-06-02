package com.example.recipesforsuccess;

import java.util.List;

public class User {

    private List<String> Basket;
    private String firstName;
    private String lastName;
    private List<String> PersonalRecipes;
    private String profilePic;

    public User() {}

    public User(List<String> Basket, String firstName, String lastName, List<String> PersonalRecipes, String profilePic)
    {
        this.Basket = Basket;
        this.firstName = firstName;
        this.lastName = lastName;
        this.PersonalRecipes = PersonalRecipes;
        this.profilePic = profilePic;
    }

    public User(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public List<String> getBasket()
    {
        return this.Basket;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public List<String> getPersonalRecipes() {
        return PersonalRecipes;
    }

    public String getProfilePic()
    {
        return profilePic;
    }

}
