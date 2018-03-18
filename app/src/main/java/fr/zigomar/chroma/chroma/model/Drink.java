package fr.zigomar.chroma.chroma.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Drink {

    private final String description;
    private final double volume;
    private final double degree;
    private final double ua;

    public String getDescription() {
        return description;
    }

    public double getUA() {
        return ua;
    }

    public Drink(String description, double volume, double degree) {
        this.description = description;
        this.volume = volume;
        this.degree = degree;

        this.ua = 0.8*0.001*volume*degree;
    }

    JSONObject getDrinkAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("description", this.description);
            json.put("volume", this.volume);
            json.put("alcohol", this.degree);
            json.put("ua", this.ua);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getDrinkAsJSON().toString();
    }
}