package fr.zigomar.chroma.chroma.Model;


import org.json.JSONException;
import org.json.JSONObject;

public class Drink {

    private String description;
    private double volume;
    private double degree;
    private double ua;

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

    private JSONObject getDrinkAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("drink_description", this.description);
            json.put("drink_volume", this.volume);
            json.put("drink_degree", this.degree);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getDrinkAsJSON().toString();
    }
}
