package metro.model;

import exception.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.StringJoiner;
import metro.register.CashBox;
import metro.register.Subscription;

public class Station {
    private final int singlePayment = 20;
    private final int subscriptionCost = 3000;
    private final Metro metro;
    private final Line line;
    private final String name;
    private Station prevStation;
    private Station nextStation;
    private Duration timeToNextStation;
    private ArrayList<Station> changeStations;

    private final CashBox cashBox = new CashBox();

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

    public ArrayList<Station> getChangeStations() {
        return changeStations;
    }

    public void setChangeStations(ArrayList<Station> changeStations) {
        if (this.changeStations != null) {
            this.changeStations.addAll(changeStations);
        }
        this.changeStations = changeStations;
    }

    public CashBox getCashBox() {
        return cashBox;
    }

    public void sellTicket(LocalDate dateSale, String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            throw new RuntimeException("Станция начала пути " + from + " равна конечной станции " + to);
        }
        try {
            metro.getStation(from);
            metro.getStation(to);
        } catch (ExistStationException e) {
            throw new RuntimeException(e);
        }
        cashBox.addCash(dateSale, calculateProfit(singlePayment, from, to));
    }

    private int calculateProfit(int singleFee, String from, String to) {
        try {
            return singleFee + (metro.countStation(from, to) * 5);
        } catch (NoLineException | StationException e) {
            throw new RuntimeException(e);
        }
    }

    public void sellSubscription(String nameSalesStation, LocalDate dateSale) {
        try {
            metro.getStation(nameSalesStation);
        } catch (ExistStationException e) {
            throw new RuntimeException(e);
        }
        cashBox.addCash(dateSale, subscriptionCost);
        metro.addSubscription(new Subscription(dateSale, metro.generateIdSubscription()));
    }

    public void renewalSubscription(String idSubscription, LocalDate dateSale) {
        try {
            metro.getSubscription(idSubscription).setStartDate(dateSale);
        } catch (SubscriptionException e) {
            e.printStackTrace();
        }
        cashBox.addCash(dateSale, subscriptionCost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return name.equals(station.name);
    }

    @Override
    public String toString() {
        return "Station{"
                + "name='" + name + '\''
                + ", changeLines=" + (changeStations != null ? concatLine(changeStations) : null)
                + '\'' + '}';
    }

    private String concatLine(ArrayList<Station> stations) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Station station : stations) {
            joiner.add(station.getLine().getColor());
        }
        return joiner.toString();
    }
}
