package metro.model;

import java.time.Duration;
import java.util.*;

public class Metro {
    private final String city;
    private final HashMap<String, Line> lines = new HashMap<>();

    public Metro(String city) {
        this.city = city;
    }

    public void createNewLine(String color) {
        if (lines.containsKey(color)) {
            throw new RuntimeException(color + " линия метро уже есть!");
        }
        lines.put(color, new Line(this, color));
    }

    public void createFirstStationLine(String colorLine, String nameStation) {
        createFirstStationLine(colorLine, nameStation, null);
    }

    public void createFirstStationLine(String colorLine, String nameStation, ArrayList<Station> changStations) {
        Station station = createStation(colorLine, nameStation);
        checkExistStation(station);

        Line line = lines.get(colorLine);
        if (checkExistFirstStation(line)) {
            throw new RuntimeException("На линии " + line.getColor() + " уже есть первая станция");
        }
        addChangStations(station, changStations);
        line.getStations().add(station);
    }

    public void createLastStation(String colorLine, String nameStation, Duration durationTime) {
        createLastStation(colorLine, nameStation, durationTime, null);
    }

    public void createLastStation(String colorLine, String nameStation, Duration durationTime, ArrayList<Station> changStations) {
        Line line = lines.get(colorLine);
        if (!checkExistFirstStation(line)) {
            throw new RuntimeException("У линии " + line.getColor() + " нет первой станции");
        }

        Station station = createStation(colorLine, nameStation);
        checkExistStation(station);
        ArrayList<Station> stations = line.getStations();
        Station prevStation = stations.get(stations.size() - 1);
        station.setPrevStation(prevStation);
        addNexStation(station, prevStation);
        addDurationTime(durationTime, station, prevStation);
        station.setChangStations(changStations);
        stations.add(station);
    }

    private Station createStation(String colorLine, String nameStation) {
        if (checkExistLine(colorLine)) {
            return new Station(this, lines.get(colorLine), nameStation);
        }
        throw new RuntimeException(colorLine + " линии метро не существует!");
    }

    private boolean checkExistLine(String colorLine) {
        return lines.containsKey(colorLine);
    }

    private void checkExistStation(Station station) {
        for (Line line : lines.values()) {
            for (Station existStation : line.getStations()) {
                if (existStation.equals(station)) {
                    throw new RuntimeException("Станция " + station.getName() + " существует в ветке " + line.getColor());
                }
            }
        }
    }

    private boolean checkExistFirstStation(Line line) {
        return line.getStations().size() != 0;
    }

    private void addChangStations(Station station, ArrayList<Station> changStations) {
        station.setChangStations(changStations);
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

    public Station getStation(String name) {
        for (Line line : lines.values()) {
            for (Station station : line.getStations()) {
                if (station.getName().equals(name)) {
                    return station;
                }
            }
        }
        throw new RuntimeException("Нет станции " + name);
    }

    @Override
    public String toString() {
        return "Metro{" + "city='" + city + '\'' + ", lines=" + lines.values() + '}';
    }
}
