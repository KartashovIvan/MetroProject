package metro.model;

import java.util.ArrayList;

public class Line {
    private final Metro metro;
    private final String color;
    private final ArrayList<Station> stations = new ArrayList<>();

    public Line(Metro metro, String color) {
        this.metro = metro;
        this.color = color;
    }

    public Metro getMetro() {
        return metro;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }

    @Override
    public String toString() {
        return "Line{" +
                "color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
