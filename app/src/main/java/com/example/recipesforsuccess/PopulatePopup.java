package com.example.recipesforsuccess;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.api.Http;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

class PopulatePopup extends AsyncTask<String, Void, JSONObject> {
    private Exception exception;
    private String ingredientName;

    public PopulatePopup(String ingredient) {
        super();

        this.ingredientName = ingredient;
    }

    @Override
    protected JSONObject doInBackground(String... urls) {
        JSONObject result = new JSONObject();

        try {
            StringBuilder url = new StringBuilder(urls[0]);
            url.append("?api_key=LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ&format=json&sort=r&max=1&da=Standard Referece&q=" + ingredientName);



/*

            String rawURL = convertToREST(params, urls[0]);
            URL url = new URL(rawURL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
//            con.setRequestProperty("format", "json");
//            con.setRequestProperty("q", ingredientName);
//            con.setRequestProperty("sort", "r");
            //con.("api_key", "LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ");
//            con.setRequestProperty("max", "1");
//            con.setRequestProperty("ds", "Standard Reference");

            Log.d("test", "URL = " + url.toString());

            HttpURLConnection.setFollowRedirects(true);
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());

            // Print the response code
            // and response message from server.
            Log.d("test", "Response Code:"
                    + con.getResponseCode());
            Log.d("test", "Response Message:"
                    + con.getResponseMessage());


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("api_key", "LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("test", "CONTENTS: " + line);
            }
*/
        } catch (Exception e) {
            Log.d("test", "ERROR WITH QUERYING FOR NDBNO ID: " + e );
        }




        return null;
    }

}
