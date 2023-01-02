package Utils;

import CarModel.Car;
import java.util.ArrayList;

public class Invoice {
    long time; // timestamp wygenerowania faktury, System.currentTimeMillis()
    String title; // tytuł/numer faktury
    String seller; // sprzedawca
    String buyer; // nabywca
    ArrayList<Car> list; // ArrayList samochodów do wyświetlenia na fakturze

    public Invoice(String title, String seller, String buyer, ArrayList<Car> list) {
        this.time =  System.currentTimeMillis();
        this.title = title;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
    }
}
