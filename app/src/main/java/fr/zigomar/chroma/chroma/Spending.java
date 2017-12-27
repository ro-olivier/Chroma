package fr.zigomar.chroma.chroma;


public class Spending {
    private String description;
    private String category;
    private double amount;

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

}
