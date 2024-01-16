package metro.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.StringJoiner;

public class Station {
    private final Metro metro;
    private final Line line;
    private final String name;
    private Station prevStation;
    private Station nextStation;
    private Duration timeToNextStation;
    private ArrayList<Station> changStations;

    public Station(Metro metro, Line line, String name) {
        this.metro = metro;
        this.line = line;
        this.name = name;
    }

    public Metro getMetro() {
        return metro;
    }

    public Line getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    public Station getPrevStation() {
        return prevStation;
    }

    public void setPrevStation(Station prevStation) {
        this.prevStation = prevStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public Duration getTimeToNextStation() {
        return timeToNextStation;
    }

    public void setTimeToNextStation(Duration timeToNextStation) {
        this.timeToNextStation = timeToNextStation;
    }

    public ArrayList<Station> getChangStations() {
        return changStations;
    }

    public void setChangStations(ArrayList<Station> changStations) {
        if(this.changStations != null){
            this.changStations.addAll(changStations);
        }
        this.changStations = changStations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", changeLines=" + (changStations != null ? concatLine(changStations) : null) +
                '\'' + '}';
    }

    private String concatLine(ArrayList<Station> stations) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Station station : stations) {
            joiner.add(station.getLine().getColor());
        }
        return joiner.toString();
    }
}
