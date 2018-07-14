package fr.zigomar.chroma.chroma.model;


import android.util.Log;

public class Step {

    private String stop;
    private String line;
    private boolean endOfTrip = false;

    public Step(String stop, String line) throws EmptyStationException {
        if (stop.length() == 0) {
            throw new EmptyStationException();
        }

        this.stop = stop;

        if (line.length() > 0) {
            this.line = line;
        } else {
            this.endOfTrip = true;
            Log.i("CHROMA", "Created end of trip step");
        }
    }

    public Step (String stop) throws EmptyStationException {
        if (stop.length() == 0) {
            throw new EmptyStationException();
        }

        Log.i("CHROMA", "Created end of trip step");

        this.stop = stop;
        this.endOfTrip = true;
    }

    public String getStop() {
        return this.stop;
    }

    boolean getNotEndOfTrip() {
        return !this.endOfTrip;
    }

    public String getLine() {
        return this.line;
    }

    public class EmptyStationException extends Exception {
        private EmptyStationException() {
            System.out.println("Invalid step : empty stop string.");
        }
    }

}
