package metro.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;

public class Metro {
    private final String city;
    private final HashSet<Line> lines = new HashSet<>();

    public Metro(String city) {
        this.city = city;
    }

    public void createNewLine(String color) {
        Line line = new Line(this, color);
        if (!lines.add(line)) {
            throw new RuntimeException(color + " линия метро уже есть!");
        }
    }

    public void createFirstStationLine(String colorLine, String nameStation) {
        Station firstStation = createStation(colorLine, nameStation);
        checkExistStation(firstStation);

        Line line = takeLine(colorLine);
        if (line.getStations().size() != 0) {
            throw new RuntimeException("На линии " + line.getColor() + " уже есть первая станция");
        }
        line.getStations().add(firstStation);
    }

    public void createLastStation(String colorLine, String nameStation, Duration durationTime) {
        createLastStation(colorLine, nameStation, durationTime, null);
    }

    public void createLastStation(String colorLine, String nameStation, Duration durationTime, ArrayList<Station> changStations) {
        Line line = takeLine(colorLine);
        ArrayList<Station> stations = checkLineFirstStation(line);

        Station station = createStation(colorLine, nameStation);
        checkExistStation(station);

        Station prevStation = stations.get(stations.size() - 1);
        station.setPrevStation(prevStation);
        addNexStation(station, prevStation);
        addDurationTime(durationTime, station, prevStation);
        station.setChangStations(changStations);
        stations.add(station);
    }

    private Line takeLine(String colorLine) {
        for (Line line : lines) {
            if (line.getColor().equals(colorLine)) {
                return line;
            }
        }
        throw new RuntimeException(colorLine + " линии метро не существует!");
    }

    private ArrayList<Station> checkLineFirstStation(Line line) {
        ArrayList<Station> stations = line.getStations();
        if (stations.size() == 0) {
            throw new RuntimeException("У линии " + line.getColor() + " нет первой станции");
        }
        return stations;
    }

    private Station createStation(String colorLine, String nameStation) {
        if (checkExistLine(colorLine)) {
            return new Station(this, takeLine(colorLine), nameStation);
        }
        throw new RuntimeException(colorLine + " линии метро не существует!");
    }

    private boolean checkExistLine(String colorLine) {
        return lines.contains(new Line(this, colorLine));
    }

    private void checkExistStation(Station station) {
        for (Line line : lines) {
            for (Station existStation : line.getStations()) {
                if (existStation.equals(station)) {
                    throw new RuntimeException("Станция " + station.getName() + " существует в ветке " + line.getColor());
                }
            }
        }
    }

    private void addNexStation(Station station, Station prevStation) {
        if (prevStation.getNextStation() != null) {
            throw new RuntimeException("У станции " + prevStation.getName() + " уже есть следующая станция " + prevStation.getNextStation().getName());
        }
        prevStation.setNextStation(station);
    }

    private void addDurationTime(Duration durationTime, Station station, Station prevStation) {
        if (durationTime.isZero()) {
            throw new RuntimeException("Время поездки от станции " + station.getName() + " до станции " + prevStation.getName() + " не может быть 0");
        }
        prevStation.setTimeToNextStation(durationTime);
    }

    public Station getStation(String stationName) {
        for (Line line : lines) {
            for (Station st : line.getStations()) {
                if (st.getName().equalsIgnoreCase(stationName)) {
                    return st;
                }
            }
        }
        throw new RuntimeException("Нет станции " + stationName);
    }

    @Override
    public String toString() {
        return "Metro{" + "city='" + city + '\'' + ", lines=" + lines + '}';
    }
}
