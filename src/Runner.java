import exception.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import metro.model.Metro;
import metro.model.Station;

public class Runner {
    public static void main(String[] args) {
        try {
            Metro metro = createMetro();
            System.out.println(metro);
            ticketSelling(metro);
        } catch (LineException | StationException | SubscriptionException e) {
            e.printStackTrace();
        }
    }

    public static Metro createMetro() throws LineException, StationException {
        Metro metro = new Metro("Пермь");
        metro.createNewLine("Красная");
        metro.createNewLine("Синяя");

        metro.createFirstStationLine("Красная", "Спортивная");
        metro.createLastStation("Красная", "Медведковская", Duration.ofMinutes(2).plusSeconds(21));
        metro.createLastStation("Красная", "Молодежная", Duration.ofMinutes(1).plusSeconds(58));
        metro.createLastStation("Красная", "Пермь 1", Duration.ofMinutes(3));
        metro.createLastStation("Красная", "Пермь 2", Duration.ofMinutes(2).plusSeconds(10));
        metro.createLastStation("Красная", "Дворец Культуры", Duration.ofMinutes(4).plusSeconds(26));
        Station stationLine1 = metro.getStation("Пермь 1");

        metro.createFirstStationLine("Синяя", "Пацанская");
        metro.createLastStation("Синяя", "Улица Кирова", Duration.ofMinutes(1).plusSeconds(30));
        metro.createLastStation("Синяя", "Тяжмаш", Duration.ofMinutes(1).plusSeconds(47), new ArrayList<>(List.of(stationLine1)));
        metro.createLastStation("Синяя", "Нижнекамская", Duration.ofMinutes(3).plusSeconds(19));
        metro.createLastStation("Синяя", "Соборная", Duration.ofMinutes(1).plusSeconds(48));

        return metro;
    }

    public static void ticketSelling(Metro metro) throws ExistStationException, SubscriptionException {
        metro.getStation("Спортивная")
                .sellTicket(LocalDate.of(2024, 1, 2), "Спортивная", "Соборная");
        metro.getStation("Спортивная")
                .sellTicket(LocalDate.of(2024, 1, 2), "Спортивная", "Соборная");
        metro.getStation("Спортивная")
                .sellTicket(LocalDate.of(2024, 1, 3), "Спортивная", "Соборная");
        metro.getStation("Медведковская")
                .sellTicket(LocalDate.of(2024, 1, 2), "Медведковская", "Нижнекамская");
        metro.getStation("Спортивная")
                .sellSubscription("Спортивная", LocalDate.of(2024, 1, 2));
        metro.getStation("Медведковская")
                .sellSubscription("Медведковская", LocalDate.of(2024, 1, 3));
        metro.getStation("Медведковская")
                .sellSubscription("Медведковская", LocalDate.of(2024, 1, 4));
        metro.getStation("Улица Кирова")
                .sellSubscription("Улица Кирова", LocalDate.of(2023, 1, 1));

        System.out.println("Действие билета id=a0001 "
                + metro.validSubscription("a0001", LocalDate.of(2024, 2, 2)));
        System.out.println("Действие билета id=a0002 "
                + metro.validSubscription("a0002", LocalDate.of(2024, 2, 5)));

        System.out.println(metro.getSubscription("a0000"));
        metro.getStation("Молодежная")
                .renewalSubscription("a0000", LocalDate.of(2024, 2, 3));
        System.out.println(metro.getSubscription("a0000"));

        metro.profitAllCashBox();
    }
}
