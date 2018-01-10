package fr.zigomar.chroma.chroma.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CarTrip {

    private String startLocation;
    private String endLocation;
    private String description;

    private Date startDate;
    private Date endDate;
    private long duration;

    private double startKM;
    private double endKM;
    private double distance;
    
    private boolean completed = false;

    public CarTrip(String beginLocation, Date beginDate, double beginKM) {
        this.startLocation = beginLocation;
        this.startDate = beginDate;
        this.startKM = beginKM;
    }

    public CarTrip(String beginLocation, String endLocation,
                   Date beginDate, Date endDate,
                   double beginKM, double endKM) {
        this.startLocation = beginLocation;
        this.startDate = beginDate;
        this.startKM = beginKM;

        this.endLocation = endLocation;
        this.endDate = endDate;
        this.endKM = endKM;

        completeTrip();
    }

    public String getDescription() {
        return description;
    }

    public long getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }
    
    public void endTrip(String endLocation, Date endDate, double endKM) {
        this.endLocation = endLocation;
        this.endDate = endDate;
        this.endKM = endKM;

        completeTrip();
    }

    private void completeTrip() {
        this.description = this.startLocation + " - " + this.endLocation;
        this.duration = this.endDate.getTime() - this.startDate.getTime();
        this.distance = this.endKM - this.startKM;

        this.completed = true;
    }

    public String toString() {
        return this.getCarTripAsJSON().toString();
    }

    private JSONObject getCarTripAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("origin", this.startLocation);
            json.put("destination", this.endLocation);
            json.put("startDate", this.startDate.getTime());
            json.put("endDate", this.endDate.getTime());
            json.put("startKM", this.startKM);
            json.put("endKM", this.endKM);
            json.put("completed", this.completed);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public boolean getCompleted() {
        return this.completed;
    }
}
