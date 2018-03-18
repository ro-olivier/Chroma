package fr.zigomar.chroma.chroma.model;


public class Step {

    private String stop;
    private String line;
    private boolean endOfTrip = false;

    public Step(String stop, String line) throws EmptyStationException, EmptyLineException {
        if (stop.length() == 0) {
            throw new EmptyStationException();
        }

        if (line.length() == 0) {
            throw new EmptyLineException();
        }

        this.stop = stop;
        this.line = line;
    }

    public Step (String stop) throws EmptyStationException {
        if (stop.length() == 0) {
            throw new EmptyStationException();
        }

        this.stop = stop;
        this.endOfTrip = true;
    }

    public String getStop() {
        return this.stop;
    }

    boolean getEndOfTrip() {
        return this.endOfTrip;
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
            System.out.println("Invalid step : empty line string.");
        }
    }
}
