package metro;

import java.util.LinkedList;

public class Line {
    private final Metro metro;
    private final String color;
    private final LinkedList<Station> stations = new LinkedList<>();
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

    public LinkedList<Station> getStations() {
        return stations;
    }

    @Override
    public int hashCode() {
        return color.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return color.equalsIgnoreCase(line.color);
    }

    @Override
    public String toString() {
        return "Line{"
                + "color='" + color + '\''
                + ", stations=" + stations
                + '}';
    }
}
