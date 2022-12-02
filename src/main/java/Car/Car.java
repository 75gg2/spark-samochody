package Car;

import com.fasterxml.uuid.Generators;

import java.util.ArrayList;
import java.util.UUID;

import static java.lang.Integer.parseInt;

public class Car {
    public Car(String model, Integer year, ArrayList<Airbag> airbags, String color) {
        this.uuid = Generators.randomBasedGenerator().generate();
        this.model = model;
        this.year = year;
        this.airbags = airbags;
        this.color = color;
    }

    private final UUID uuid;
    private String model;
    private Integer year;
    private ArrayList<Airbag> airbags;
    private String color;

}
