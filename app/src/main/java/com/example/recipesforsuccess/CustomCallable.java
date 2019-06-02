package com.example.recipesforsuccess;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class CustomCallable implements Callable<JSONArray> {
    String curr;
    JSONArray res;

    public CustomCallable(String curr) {
        this.curr = curr;
    }

    @Override
    public JSONArray call() {
        try {

            String url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/food/ingredients/autocomplete?number=10&intolerances=egg&query="+ curr;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
            con.setRequestProperty("X-RapidAPI-Key", "d062d8f60cmsh31071aadde7f9ddp13edb5jsn07411d458f51");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()) );
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            res = new JSONArray(response.toString());

        } catch (Exception e) {
            Log.d("test", "EXCEPTION: " + e);
        }
        return res;
    }
}
