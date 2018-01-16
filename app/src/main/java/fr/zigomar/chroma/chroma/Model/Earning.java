package fr.zigomar.chroma.chroma.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Earning {
    private final String description;
    private final double amount;

    public Earning(String description, double amount) {
        this.amount = amount;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    private JSONObject getEarningAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("earning_description", this.description);
            json.put("earning_amount", this.amount);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getEarningAsJSON().toString();
    }
}