package fr.zigomar.chroma.chroma.model;


import android.util.Log;

public class Step {

    private String stop;
    private String line;
    private boolean endOfTrip;

    public Step(String stop, String line, boolean EOT) throws EmptyStationException, EmptyLineException {
        if (stop.length() == 0) {
            throw new EmptyStationException();
        }

        this.stop = stop;
        this.endOfTrip = EOT;

        if (EOT) {
            if (line.length() == 0) {
                Log.i("CHROMA", "Created end of trip step");
            } else {
                Log.i("CHROMA", "Created end of trip step with none empty line on step (ignored)");
            }
        } else {
            if (line.length() == 0) {
                throw new EmptyLineException();
            } else {
                this.line = line;
            }
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

    public class EmptyLineException extends Exception {
        private EmptyLineException() {
            System.out.println("Invalid step : empty line string and step is not end of trip.");
        }
    }
}
