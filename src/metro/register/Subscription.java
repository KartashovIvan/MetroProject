package metro.register;

import java.time.LocalDate;

public class Subscription {
    private LocalDate startDate;
    private LocalDate expirationDate;
    private final String id;

    public Subscription(LocalDate startDate, String id) {
        this.startDate = startDate;
        this.expirationDate = startDate.plusMonths(1);
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.expirationDate = startDate.plusMonths(1);
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Абонимент: " + id + ". Дата начала действия: " + startDate + ". Дата окончания действия: "+ expirationDate;
    }
}
