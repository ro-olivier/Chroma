package fr.zigomar.chroma.chroma.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Spending {
    private final String description;
    private final String category;
    private final double amount;

    public Spending(String description, String category, double amount) {
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    private JSONObject getSpendingAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("spending_description", this.description);
            json.put("spending_category", this.category);
            json.put("spending_amount", this.amount);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getSpendingAsJSON().toString();
    }
}