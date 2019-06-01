//package com.example.recipesforsuccess;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONObject;
//
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//class PopulatePopup extends AsyncTask<Void, Void, Void> {
//    private Exception exception;
//
//    public PopulatePopup(String ingredient) {
//        super();
//
//
//    }
//
//    protected void doInBackground(String... urls) {
//
//    }
//
//
//    // https://stackoverflow.com/questions/6343166/how-do-i-fix-android-os-networkonmainthreadexception
//
//    private void fillInNutrients(String ingredient) throws Exception {
//        JSONObject ezm = new JSONObject();
//
//        // Query for the ndbno ID of the ingredient
//        URL url = new URL("https://api/nal.usda.gov/ndb/nutrients/");
//
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("format", "json");
//        con.setRequestProperty("q", ingredient);
//        con.setRequestProperty("sort", "r");
//        con.setRequestProperty("api_key", "LHgDB2008wpwdJEzvK2wlR7gLNv7oPzYXCVAyJVZ");
//        con.setRequestProperty("max", "1");
//        con.setRequestProperty("ds", "Standard Reference");
//
//        HttpURLConnection.setFollowRedirects(true);
//        con.setInstanceFollowRedirects(false);
//        con.setDoOutput(true);
//
//
//        Log.d("test", "REACHED    111111111");
//        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
//        Log.d("test", "REACHED");
//
//        // Print the response code
//        // and response message from server.
//        Log.d("test", "Response Code:"
//                + con.getResponseCode());
//        Log.d("test", "Response Message:"
//                + con.getResponseMessage());
//
//        //            URL url = new URL("https://api/nal.usda.gov/ndb/nutrients/");
////            HttpURLConnection con = (HttpURLConnection) url.openConnection();
////
////            con.setRequestMethod("GET");
////            con.setRequestProperty("")
//    }
//}
