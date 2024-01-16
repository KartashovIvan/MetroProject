import metro.model.Metro;
import metro.model.Station;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) {
        Metro metro = createMetro();
        System.out.println(metro);

//        System.out.println(metro.findChangeStation(metro.takeLine("Синяя"),metro.takeLine("Красная")));
    }

    public static Metro createMetro(){
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
        Station stationLine2 = metro.getStation("Тяжмаш");
        stationLine1.setChangStations(new ArrayList<>(List.of(stationLine2)));

        return metro;
    }
}
