package metro.model;

import exception.*;
import metro.register.Subscription;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class Metro {
    private final String city;
    private final HashMap<String, Line> lines = new HashMap<>();
    private final HashMap<String, Subscription> storageSubscription = new HashMap<>();
    private int subscriptionId = 0;

    public Metro(String city) {
        this.city = city;
    }

    public void createNewLine(String color) throws LineException {
        if (lines.containsKey(color)) {
            throw new LineException(color + " линия метро уже есть!");
        }
        lines.put(color, new Line(this, color));
    }

    public void createFirstStationLine(String colorLine, String nameStation) throws StationException, LineException {
        fillingFirstStationLine(colorLine, nameStation);
    }

    public void createFirstStationLine(String colorLine, String nameStation, ArrayList<Station> changStations) throws StationException, LineException {
        addChangeStations(fillingFirstStationLine(colorLine, nameStation), changStations);

    }

    private Station fillingFirstStationLine(String colorLine, String nameStation) throws StationException, LineException {
        checkExistLine(colorLine);
        Station station = createStation(colorLine, nameStation);
        checkExistStation(station);

        Line line = lines.get(colorLine);
        if (checkExistFirstStation(line)) {
            throw new StationException("На линии " + line.getColor() + " уже есть первая станция");
        }
        line.getStations().add(station);
        return station;
    }

    public void createLastStation(String colorLine, String nameStation, Duration durationTime) throws StationException, LineException {
        fillingLastStation(colorLine, nameStation, durationTime);
    }

    public void createLastStation(String colorLine, String nameStation, Duration durationTime, ArrayList<Station> changeStations) throws StationException, LineException {
        addChangeStations(fillingLastStation(colorLine, nameStation, durationTime), changeStations);
    }

    private Station fillingLastStation(String colorLine, String nameStation, Duration durationTime) throws StationException, LineException {
        checkExistLine(colorLine);
        Line line = lines.get(colorLine);
        if (!checkExistFirstStation(line)) {
            throw new StationException("У линии " + line.getColor() + " нет первой станции");
        }

        Station station = createStation(colorLine, nameStation);
        checkExistStation(station);
        ArrayList<Station> stations = line.getStations();
        Station prevStation = stations.get(stations.size() - 1);
        station.setPrevStation(prevStation);
        addNexStation(station, prevStation);
        addDurationTime(durationTime, station, prevStation);
        stations.add(station);
        return station;
    }

    private void addChangeStations(Station station, ArrayList<Station> changeStations) {
        station.setChangeStations(changeStations);
        for (int i = 0; i < changeStations.size(); i++) {
            ArrayList<Station> changStationsCopy = new ArrayList<>(changeStations);
            Station station1 = changeStations.get(i);
            changStationsCopy.set(i, station);
            station1.setChangeStations(changStationsCopy);
        }
    }

    private Station createStation(String colorLine, String nameStation) {
        return new Station(this, lines.get(colorLine), nameStation);
    }

    private void checkExistLine(String colorLine) throws NoLineException {
        if (!lines.containsKey(colorLine)) {
            throw new NoLineException(colorLine + " линии метро не существует!");
        }
    }

    private void checkExistStation(Station station) throws ExistStationException {
        for (Line line : lines.values()) {
            for (Station existStation : line.getStations()) {
                if (existStation.equals(station)) {
                    throw new ExistStationException("Станция " + station.getName() + " существует в ветке " + line.getColor());
                }
            }
        }
    }

    private boolean checkExistFirstStation(Line line) {
        return line.getStations().size() != 0;
    }

    private void addNexStation(Station station, Station prevStation) throws ExistStationException {
        if (prevStation.getNextStation() != null) {
            throw new ExistStationException("У станции " + prevStation.getName() + " уже есть следующая станция " + prevStation.getNextStation().getName());
        }
        prevStation.setNextStation(station);
    }

    private void addDurationTime(Duration durationTime, Station station, Station prevStation) {
        if (durationTime.isZero()) {
            throw new RuntimeException("Время поездки от станции " + station.getName() + " до станции " + prevStation.getName() + " не может быть 0");
        }
        prevStation.setTimeToNextStation(durationTime);
    }

    public Station getStation(String name) throws ExistStationException {
        for (Line line : lines.values()) {
            for (Station station : line.getStations()) {
                if (station.getName().equals(name)) {
                    return station;
                }
            }
        }
        throw new ExistStationException("Нет станции " + name);
    }

    public Station findChangeStation(Line from, Line to) throws NoLineException {
        for (Station station : lines.get(from.getColor()).getStations()) {
            if (findLine(station.getChangeStations(), to)) {
                return station;
            }
        }
        throw new NoLineException("У линии " + from.getColor() + " нет перехода на линию " + to.getColor());
    }

    private boolean findLine(ArrayList<Station> changStations, Line to) {
        if (changStations != null) {
            for (Station s : changStations) {
                if (s.getLine().equals(to)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Station findChangeStation(ArrayList<Station> changStations, Line to) throws ExistStationException {
        for (Station s : changStations) {
            if (s.getLine().equals(to)) {
                return s;
            }
        }
        throw new ExistStationException("Нет станции для пересадки на линию " + to.getColor());
    }

    public int countStation(String from, String to) throws StationException, NoLineException {
        Station fromStation = getStation(from);
        Station toStation = getStation(to);
        if (fromStation.equals(toStation)) {
            throw new RuntimeException("Указана одна станция");
        }
        Line fromLine = fromStation.getLine();
        Line toLine = toStation.getLine();

        if (fromLine.equals(toLine)) {
            return countStationOneLine(fromStation, toStation);
        } else {
            int sum;
            Station changeStation = findChangeStation(fromLine, toLine);
            sum = countStationOneLine(fromStation, changeStation);
            return sum + countStationOneLine(findChangeStation(changeStation.getChangeStations(), toLine), toStation);
        }
    }

    private int countStationOneLine(Station from, Station to) throws StationException {
        int countNext = countNextStation(from, to);
        if (countNext != -1) {
            return countNext;
        }
        int countPrev = countPrevStation(from, to);
        if (countPrev != -1) {
            return countPrev;
        }
        throw new StationException("Нет пути из станции " + from.getName() + " - " + to.getName());
    }

    private int countNextStation(Station from, Station to) {
        int countNextStation = 0;
        if (from.equals(to)) {
            return countNextStation;
        }
        Station nextStation = from.getNextStation();
        while (true) {
            if (nextStation != null) {
                countNextStation++;
                if (nextStation.equals(to)) {
                    return countNextStation;
                } else {
                    nextStation = nextStation.getNextStation();
                }
            } else {
                break;
            }
        }
        return -1;
    }

    private int countPrevStation(Station from, Station to) {
        int countPrevStation = 0;
        if (from.equals(to)) {
            return countPrevStation;
        }
        Station prevStation = from.getPrevStation();
        while (true) {
            if (prevStation != null) {
                countPrevStation++;
                if (prevStation.equals(to)) {
                    return countPrevStation;
                } else {
                    prevStation = prevStation.getPrevStation();
                }
            } else {
                break;
            }
        }
        return -1;
    }

    public void addSubscription(Subscription subscription) {
        storageSubscription.put(subscription.getId(), subscription);
    }

    public String generateIdSubscription() {
        String id = "a" + String.format("%04d", subscriptionId);
        subscriptionId++;
        return id;
    }

    public Subscription getSubscription(String idSubscription) throws SubscriptionException {
        if (storageSubscription.containsKey(idSubscription)) {
            return storageSubscription.get(idSubscription);
        }
        throw new SubscriptionException("Нет абонимента с Id - " + idSubscription);
    }

    public boolean validSubscription(String idSubscription, LocalDate dateInspection) throws SubscriptionException {
        LocalDate subscriptionStart = getSubscription(idSubscription).getExpirationDate();
        return subscriptionStart.compareTo(dateInspection) >= 0;
    }

    public void profitAllCashBox() {
        HashMap<LocalDate, Integer> report = new HashMap<>();
        for (Line line : lines.values()) {
            for (Station station : line.getStations()) {
                HashMap<LocalDate, Integer> cash = station.getCashBox().getReport();
                if (cash.isEmpty()) {
                    continue;
                }
                addCash(cash, report);
            }
        }
        printReport(report);
    }

    private void addCash(HashMap<LocalDate, Integer> cash, HashMap<LocalDate, Integer> report) {
        for (Map.Entry<LocalDate, Integer> entry : cash.entrySet()) {
            LocalDate key = entry.getKey();
            if (!report.containsKey(key)) {
                report.put(entry.getKey(), entry.getValue());
            } else {
                report.put(entry.getKey(), report.get(entry.getKey()) + entry.getValue());
            }
        }
    }

    private void printReport(HashMap<LocalDate, Integer> report) {
        for (Map.Entry<LocalDate, Integer> entry : report.entrySet()) {
            System.out.println("Дата - " + entry.getKey() + " : доход " + entry.getValue());
        }
    }

    @Override
    public String toString() {
        return "Metro{" + "city='" + city + '\'' + ", lines=" + lines.values() + '}';
    }
}
