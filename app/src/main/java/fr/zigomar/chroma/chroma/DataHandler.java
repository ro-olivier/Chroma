package fr.zigomar.chroma.chroma;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


class DataHandler {

    //private Date currentDate;
    private String filename;

    private JSONObject data;

    private static final int INITIAL_MOOD = 5;

    DataHandler(Context ctx, Date currentDate) {
        //this.currentDate = currentDate;
        this.filename = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(currentDate);
        this.data = new JSONObject();
        initData(ctx);
    }

    /*
        ########################################################
        Global section :
            - initData (readFromFile)
            - writeToFile method
        ########################################################
     */
    private void initData(Context ctx) {
        Log.i("CHROMA", "Data init started.");

        FileInputStream is;

        try {
            // read the file if it exists, and create JSON object with was is in it
            is  = ctx.getApplicationContext().openFileInput(this.filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            this.data = new JSONObject(new String(buffer, "UTF-8"));
            Log.i("CHROMA", "Read currentDate file and obtained following data :" + this.data.toString());

        } catch (FileNotFoundException e) {
            // the file does exist yet so we load the data with the default data for each section
            Log.i("CHROMA", "File " + this.filename + " was not found. Creating data with default values.");
            e.printStackTrace();
            saveMoodData(INITIAL_MOOD,INITIAL_MOOD,INITIAL_MOOD, "");
            // no saveMoneyData(new Array<Spending>) because if now "spendings" data can be found in the file
            // we have no good reason to create an empty one before we actually write anything
            //...

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            Log.e("CHROMA","Data in file does not seem to be in JSON format");
            e.printStackTrace();
        }

    }


    void writeDataToFile(Context ctx) {
        // this method does the actual writing-to-file work
        // no big deal, the OutputStream is used and it should go well
        // and remain private to the app
        Log.i("CHROMA", "DataHandler is going to write the following data to file : " + this.data.toString());

        String string = this.data.toString();

        FileOutputStream outputStream;

        try {
            Log.i("CHROMA", "Writing new values to file " + this.filename + " : " + string);
            outputStream = ctx.getApplicationContext().openFileOutput(this.filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    ########################################################
    Mood section :
        - saveMoodData
        - getMoodData
    ########################################################
    */
    void saveMoodData(int v1, int v2, int v3, String txt) {
        // method used by the MoodActivity to update the mood data with was was
        // written in the view. Pretty simple !
        Log.i("CHROMA", "SaveMoodData was invoked.");
        try {
            this.data.put("mood_eval1", v1);
            this.data.put("mood_eval2", v2);
            this.data.put("mood_eval3", v3);
            this.data.put("mood_text", txt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, String> getMoodData() {
        // returns the mood data currently present in the file
        // a HashMap is returned, instead of a JSONObject because I don't want any
        // JSON logic in the activities (it belongs to the DataHandler only)
        Log.i("CHROMA", "getMoodData was invoked");
        HashMap<String, String> d = new HashMap<>();

        // TODO : there is probably a more concise way to do this...
        try {
            d.put("eval1", String.valueOf(this.data.getInt("mood_eval1")));
        } catch (JSONException e) {
            e.printStackTrace();
            d.put("eval1", "");
        }

        try {
            d.put("eval2", String.valueOf(this.data.getInt("mood_eval2")));
        } catch (JSONException e) {
            e.printStackTrace();
            d.put("eval2", "");
        }

        try {
            d.put("eval3", String.valueOf(this.data.getInt("mood_eval3")));
        } catch (JSONException e) {
            e.printStackTrace();
            d.put("eval3", "");
        }

        try {
            d.put("txt", this.data.getString("mood_text"));
        } catch (JSONException e) {
            e.printStackTrace();
            d.put("txt", "");
        }

        Log.i("CHROMA", "Returning : " + d.toString());
        return d;
    }


    /*
    ########################################################
    Money section :
        - saveMoneyData
        - getSpendingsList
    ########################################################
    */

    void saveMoneyData(List<Spending> l) {
        Log.i("CHROMA", "SaveMoneyData was invoked.");
        try {
            this.data.put("spendings", l);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Spending> getSpendingsList() {
        ArrayList<Spending> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("spendings").toString());


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jso = jsonArray.getJSONObject(i);
            s.add(new Spending(jso.getString("spending_description"), jso.getString("spending_category"), jso.getDouble("spending_amount")));
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }
}