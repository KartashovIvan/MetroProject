package metro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import metro.exception.ExistStationException;
import metro.exception.NoLineException;
import metro.exception.StationException;
import metro.exception.SubscriptionException;

public class CashBox {
    private static int subscriptionId = 0;
    private final static int SINGLE_PAYMENT = 20;
    private final static int SUBSCRIPTION_COST = 3000;

    private final HashMap<LocalDate, BigDecimal> report = new HashMap<>();

    public HashMap<LocalDate, BigDecimal> getReport() {
        return report;
    }

    public void addCash(LocalDate dateSale, Integer cash) {
        if (report.containsKey(dateSale)) {
            report.put(dateSale, report.get(dateSale).add(new BigDecimal(cash)));
        } else {
            report.put(dateSale, new BigDecimal(cash));
        }
    }

    public String generateIdSubscription() {
        String id = "a" + String.format("%04d", subscriptionId);
        subscriptionId++;
        return id;
    }

    public void accountSellTicket(LocalDate dateSale, String from, String to, Metro metro) {
        if (from.equalsIgnoreCase(to)) {
            throw new RuntimeException("Станция начала пути " + from + " равна конечной станции " + to);
        }
        try {
            metro.getStation(from);
            metro.getStation(to);
        } catch (ExistStationException e) {
            throw new RuntimeException(e);
        }
        addCash(dateSale, calculateProfit(from, to, metro));
    }

    private int calculateProfit(String from, String to, Metro metro) {
        try {
            return SINGLE_PAYMENT + (metro.countStation(from, to) * 5);
        } catch (NoLineException | StationException e) {
            throw new RuntimeException(e);
        }
    }

    public void accountSellSubscription(String nameSalesStation, LocalDate dateSale, Metro metro) {
        try {
            metro.getStation(nameSalesStation);
        } catch (ExistStationException e) {
            throw new RuntimeException(e);
        }
        addCash(dateSale, SUBSCRIPTION_COST);
        metro.addSubscription(new Subscription(dateSale, generateIdSubscription()));
    }

    public void accountRenewalSubscription(String idSubscription, LocalDate dateSale, Metro metro) {
        try {
            metro.getSubscription(idSubscription).setStartDate(dateSale);
        } catch (SubscriptionException e) {
            e.printStackTrace();
        }
        addCash(dateSale, SUBSCRIPTION_COST);
    }
}
