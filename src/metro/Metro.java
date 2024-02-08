package metro;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import metro.exception.*;

public class Metro {
    private final String city;
    private final HashSet<Line> lines = new HashSet<>();
    private final HashSet<Subscription> storageSubscription = new HashSet<>();

    public Metro(String city) {
        this.city = city;
    }

    public Line createNewLine(String color) throws LineException {
        Line line = new Line(this, color);
        if (!lines.add(line)) {
            throw new LineException(color + " линия метро уже есть!");
        }
        return line;
    }

    public Station createFirstStationLine(String colorLine, String nameStation)
            throws StationException, LineException {
        return fillingFirstStationLine(colorLine, nameStation);
    }

    public Station createFirstStationLine(String colorLine, String nameStation, List<Station> changStations)
            throws StationException, LineException {
        Station station = fillingFirstStationLine(colorLine, nameStation);
        addChangeStations(station, changStations);
        return station;
    }

    private Station fillingFirstStationLine(String colorLine, String nameStation)
            throws StationException, LineException {
        Line line = getLine(colorLine);
        Station station = createStation(line, nameStation);
        checkExistStation(station);
        if (checkExistFirstStation(line)) {
            throw new StationException("На линии " + line.getColor() + " уже есть первая станция");
        }
        line.getStations().add(station);
        return station;
    }

    public Station createLastStation(String colorLine, String nameStation, Duration durationTime)
            throws StationException, LineException {
        return fillingLastStation(colorLine, nameStation, durationTime);
    }

    public Station createLastStation(String colorLine, String nameStation, Duration durationTime, ArrayList<Station> changeStations)
            throws StationException, LineException {
        Station station = fillingLastStation(colorLine, nameStation, durationTime);
        addChangeStations(station, changeStations);
        return station;
    }

    private Station fillingLastStation(String colorLine, String nameStation, Duration durationTime)
            throws StationException, LineException {
        Line line = getLine(colorLine);
        if (!checkExistFirstStation(line)) {
            throw new StationException("У линии " + line.getColor() + " нет первой станции");
        }

        Station station = createStation(line, nameStation);
        checkExistStation(station);
        ArrayList<Station> stations = line.getStations();
        Station prevStation = stations.get(stations.size() - 1);
        station.setPrevStation(prevStation);
        addNexStation(station, prevStation);
        addDurationTime(durationTime, station, prevStation);
        stations.add(station);
        return station;
    }

    private void addChangeStations(Station station, List<Station> changeStations) {
        station.setChangeStations(changeStations);
        for (int i = 0; i < changeStations.size(); i++) {
            List<Station> changStationsCopy = new ArrayList<>(changeStations);
            Station station1 = changeStations.get(i);
            changStationsCopy.set(i, station);
            station1.setChangeStations(changStationsCopy);
        }
    }

    private Station createStation(Line line, String nameStation) {
        return new Station(this, line, nameStation);
    }

    private Line getLine(String colorLine) throws NoLineException {
        return lines.stream()
                .filter(line -> line.equals(new Line(this, colorLine)))
                .findFirst()
                .orElseThrow(() -> new NoLineException(colorLine + " линии метро не существует!"));
    }

    private void checkExistStation(Station station) throws ExistStationException {
        if (lines.stream()
                .flatMap(line->line.getStations().stream())
                .anyMatch(existStation -> existStation.equals(station))) {
            throw new ExistStationException("Станция "
                    + station.getName()
                    + " существует в ветке "
                    + station.getLine().getColor());
        }
    }

    private boolean checkExistFirstStation(Line line) {
        return line.getStations().size() != 0;
    }

    private void addNexStation(Station station, Station prevStation) throws ExistStationException {
        if (prevStation.getNextStation() != null) {
            throw new ExistStationException("У станции "
                    + prevStation.getName()
                    + " уже есть следующая станция "
                    + prevStation.getNextStation().getName());
        }
        prevStation.setNextStation(station);
    }

    private void addDurationTime(Duration durationTime, Station station, Station prevStation) {
        if (durationTime.isZero()) {
            throw new RuntimeException("Время поездки от станции "
                    + station.getName()
                    + " до станции "
                    + prevStation.getName()
                    + " не может быть 0");
        }
        prevStation.setTimeToNextStation(durationTime);
    }

    public Station getStation(String name) throws ExistStationException {
        return lines.stream()
                .flatMap(line->line.getStations().stream())
                .filter(station -> station.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ExistStationException("Нет станции " + name));
    }

    public Station findChangeLine(Line from, Line to) throws NoLineException {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .filter(station -> station.getChangeStations() != null && findLine(station.getChangeStations(), to))
                .findFirst().orElseThrow(() ->
                        new NoLineException("У линии "
                                + from.getColor()
                                + " нет перехода на линию "
                                + to.getColor()));
    }

    private boolean findLine(List<Station> changStations, Line to) {
        return changStations.stream()
                .allMatch(station -> station.getLine().equals(to));
    }

    private Station findChangeStation(List<Station> changStations, Line to) throws ExistStationException {
        return changStations.stream()
                .filter(station -> station.getLine().equals(to))
                .findFirst()
                .orElseThrow(() -> new ExistStationException("Нет станции для пересадки на линию " + to.getColor()));
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
            Station changeStation = findChangeLine(fromLine, toLine);
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
        storageSubscription.add(subscription);
    }

    public Subscription getSubscription(String idSubscription) throws SubscriptionException {
        return storageSubscription.stream()
                .filter(subscription -> subscription.getId().equals(idSubscription))
                .findFirst()
                .orElseThrow(() -> new SubscriptionException("Нет абонимента с Id - " + idSubscription));
    }

    public boolean validSubscription(String idSubscription, LocalDate dateInspection) throws SubscriptionException {
        LocalDate subscriptionStart = getSubscription(idSubscription).getExpirationDate();
        return subscriptionStart.compareTo(dateInspection) >= 0;
    }

    public void printReport() {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .filter(station -> !station.getCashBox().getReport().isEmpty())
                .map(station -> station.getCashBox().getReport().entrySet())
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        BigDecimal::add))
                .forEach((date, profit) -> System.out.println("Дата - " + date + " : доход " + profit));
    }

    @Override
    public String toString() {
        return "Metro{" + "city='" + city + '\'' + ", lines=" + lines + '}';
    }
}
