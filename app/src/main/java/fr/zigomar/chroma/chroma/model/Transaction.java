package fr.zigomar.chroma.chroma.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Transaction {
    private final String description;
    private final String category;
    private final double amount;

    public Transaction(String description, String category, double amount) throws InvalidDescriptionException, InvalidCategoryException {

        if (Objects.equals(description, "")) {
            throw new InvalidDescriptionException(description);
        }

        if (Objects.equals(category, "")) {
            throw new InvalidCategoryException(category);
        }

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

    JSONObject getTransactionAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("description", this.description);
            json.put("category", this.category);
            json.put("amount", this.amount);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getTransactionAsJSON().toString();
    }

    public class InvalidDescriptionException extends Exception {
        private InvalidDescriptionException(String d) {
            System.out.println("Invalid transaction: invalid description (" + d + ")");
        }
    }

    public class InvalidCategoryException extends Exception {
        private InvalidCategoryException(String c) {
            System.out.println("Invalid transaction: invalid category (" + c + ")");
        }
    }

}