package metro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

public class CashBox {
    private static int subscriptionId = 0;
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
}
