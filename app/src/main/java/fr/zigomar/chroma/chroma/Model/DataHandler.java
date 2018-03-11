package fr.zigomar.chroma.chroma.Model;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DataHandler {

    //private Date currentDate;
    private final String filename;

    private JSONObject data;

    private static final int INITIAL_MOOD = 5;

    public DataHandler(Context ctx, Date currentDate) {
        this.filename = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(currentDate) + ".json";
        this.data = new JSONObject();
        initData(ctx);
        Log.i("CHROMA", "Opened or created file " + this.filename);
    }

    public DataHandler(Context ctx, String filename) {
        this.filename = filename;
        this.data = new JSONObject();
        initData(ctx);
        Log.i("CHROMA", "Opened or created file " + this.filename);
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
            int byte_read = is.read(buffer);
            if (byte_read != size) { Log.i("CHROMA", "Did not read the complete file, or something else went wrong"); }
            is.close();
            this.data = new JSONObject(new String(buffer, "UTF-8"));
            Log.i("CHROMA", "Read currentDate file and obtained following data :" + this.data.toString());

        } catch (FileNotFoundException e) {
            // the file does exist yet so we load the data with the default data for each section
            Log.i("CHROMA", "File " + this.filename + " was not found. Creating data with default values.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            Log.e("CHROMA","Data in file does not seem to be in JSON format");
            e.printStackTrace();
        }

    }

    public void writeDataToFile(Context ctx) {
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

    public void saveMoodData(int v1, int v2, int v3, String txt) {
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

    public HashMap<String, String> getMoodData() {
        // returns the mood data currently present in the file
        // a HashMap is returned, instead of a JSONObject because I don't want any
        // JSON logic in the activities (it belongs to the DataHandler only)
        Log.i("CHROMA", "getMoodData was invoked");
        HashMap<String, String> d = new HashMap<>();

        for (int i = 1; i < 4; i++) {
            try {
                d.put("eval" + i, String.valueOf(this.data.getInt("mood_eval" + i)));
            } catch (JSONException e) {
                e.printStackTrace();
                d.put("eval" + i, String.valueOf(INITIAL_MOOD));
            }
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

    public void saveMoneyData(List<Spending> l) {
        Log.i("CHROMA", "SaveMoneyData was invoked.");
        JSONArray jArray = new JSONArray();
        for (Spending spending : l) {
            jArray.put(spending.getSpendingAsJSON());
        }
        try {
            this.data.put("spendings", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Spending> getSpendingsList() {
        ArrayList<Spending> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("spendings").toString());


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jso = jsonArray.getJSONObject(i);
            s.add(new Spending(jso.getString("description"), jso.getString("category"), jso.getDouble("amount")));
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

    /*
    ########################################################
    Drink section :
        - saveAlcoholData
        - getDrinksList
    ########################################################
    */

    public void saveAlcoholData(List<Drink> l) {
        Log.i("CHROMA", "SaveAlcoholData was invoked.");
        JSONArray jArray = new JSONArray();
        for (Drink drink : l) {
            jArray.put(drink.getDrinkAsJSON());
        }
        try {
            this.data.put("drinks", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Drink> getDrinksList() {
        ArrayList<Drink> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("drinks").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                s.add(new Drink(jso.getString("description"), jso.getDouble("volume"), jso.getDouble("content")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

        /*
    ########################################################
    Transport section :
        - saveTransportData
        - getTripsList
    ########################################################
    */

    public void saveTransportData(List<Trip> l) {
        Log.i("CHROMA", "SaveTransportData was invoked.");
        JSONArray jArray = new JSONArray();
        for (Trip trip : l) {
            jArray.put(trip.getTripAsJSON());
        }
        try {
            this.data.put("trips", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Trip> getTripsList() {
        ArrayList<Trip> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("trips").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                s.add(new Trip(jso.getString("trip"), jso.getDouble("cost")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

    /*
    ########################################################
    CarTrip section :
        - saveCarTripData
        - getCarTripsList
    ########################################################
    */

    public void saveCarTripData(ArrayList<CarTrip> l) {
        Log.i("CHROMA", "SaveCarTripData was invoked.");
        JSONArray jArray = new JSONArray();
        for (CarTrip carTrip : l) {
            jArray.put(carTrip.getCarTripAsJSON());
        }
        try {
            this.data.put("carTrips", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CarTrip> getCarTripsList() {
        ArrayList<CarTrip> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("carTrips").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                if (jso.getBoolean("completed")) {
                    // create completed carTrip
                    s.add(new CarTrip(jso.getString("origin"), jso.getString("destination"),
                            new Date(jso.getLong("startDate")), new Date(jso.getLong("endDate")),
                            jso.getDouble("startKM"), jso.getDouble("endKM")));
                } else {
                    // create an uncompleted carTrip
                    s.add(new CarTrip(jso.getString("origin"),
                            new Date(jso.getLong("startDate")),
                            jso.getDouble("startKM")));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

    /*
    ########################################################
    Money In section :
        - saveMoneyInData
        - getIncomesList
    ########################################################
    */

    public void saveMoneyInData(List<Income> l) {
        Log.i("CHROMA", "SaveMoneyInData was invoked.");
        JSONArray jArray = new JSONArray();
        for (Income income : l) {
            jArray.put(income.getIncomeAsJSON());
        }
        try {
            this.data.put("incomes", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Income> getIncomesList() {
        ArrayList<Income> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("incomes").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                s.add(new Income(jso.getString("description"), jso.getDouble("amount")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

    /*
    ########################################################
    Money In section :
        - saveMovieData
        - getMoviesList
    ########################################################
    */

    public void saveMovieData(List<Movie> l) {
        Log.i("CHROMA", "SaveMovieData was invoked.");
        JSONArray jArray = new JSONArray();
        for (Movie movie : l) {
            jArray.put(movie.getMovieAsJSON());
        }
        try {
            this.data.put("movies", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Movie> getMoviesList() {
        ArrayList<Movie> s = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(this.data.get("movies").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                s.add(new Movie(jso.getString("title"), jso.getString("director"), jso.getString("description"), Float.parseFloat(jso.getString("rating"))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

    /*
    ########################################################
    Book section :
        - saveOpenBooksData (save data to file openBooks.json)
        - getOpenBooksData (read data from file openBooks.json)

        - saveReviewedBooksData
        - getReviewedBooksList
    ########################################################
    */

    public void saveOpenBookData(List<Book> l) {
        Log.i("CHROMA", "SaveNewBook was invoked.");
        JSONArray jArray = new JSONArray();
        for (Book book : l) {
            jArray.put(book.getBookAsJSON());
        }
        try {
            this.data.put("books", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Book> getOpenBooksData() {
        ArrayList<Book> s = new ArrayList<>();
        JSONArray jsonArray;
        Log.i("CHROMA", this.data.toString());
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);
        try {
            jsonArray = new JSONArray(this.data.get("books").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                s.add(new Book(jso.getString("title"), jso.getString("author"), df.parse(jso.getString("date_opened"))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            Log.i("CHROMA", "Could not parse date in OpenBook.json file !");
            e.printStackTrace();
        }

        return s;
    }

    public void saveReviewedBooksData(List<Book> l) {
        Log.i("CHROMA", "saveReviewedBooksData was invoked.");
        JSONArray jArray = new JSONArray();
        for (Book book : l) {
            jArray.put(book.getBookAsJSON());
        }
        try {
            this.data.put("books", jArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Book> getReviewedBooksList() {
        ArrayList<Book> s = new ArrayList<>();
        JSONArray jsonArray;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);

        try {
            jsonArray = new JSONArray(this.data.get("books").toString());


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jso = jsonArray.getJSONObject(i);
                if (jso.has("date_finished")) {
                    // we have a finished book
                    s.add(new Book(jso.getString("title"),
                            jso.getString("author"),
                            df.parse(jso.getString("date_opened")),
                            jso.getString("review"),
                            df.parse(jso.getString("date_finished")),
                            (float) jso.getDouble("rating")));
                } else if (jso.has("review")) {
                    // we have an unfinished book with a review already written for this day
                    s.add(new Book(jso.getString("title"),
                            jso.getString("author"),
                            df.parse(jso.getString("date_opened")),
                            jso.getString("review")));
                } else {
                    // we have a unreviewed book
                    s.add(new Book(jso.getString("title"),
                            jso.getString("author"),
                            df.parse(jso.getString("date_opened"))));
                }
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return s;
    }
}