package metro.register;

import java.time.LocalDate;
import java.util.HashMap;

public class CashBox {
    private final HashMap<LocalDate, Integer> report = new HashMap<>();

    public HashMap<LocalDate, Integer> getReport() {
        return report;
    }

    public void addCash(LocalDate dateSale, Integer cash) {
        if (report.containsKey(dateSale)) {
            Integer money = report.get(dateSale) + cash;
            report.put(dateSale, money);
        } else {
            report.put(dateSale, cash);
        }
    }
}
