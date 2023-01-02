package CarModel;

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
    public String invoice = "";

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public void generateUUID() {
        this.uuid = Generators.randomBasedGenerator().generate();
    }

    private UUID uuid;

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<Airbag> getAirbags() {
        return airbags;
    }

    public String getColor() {
        return color;
    }

    private String model;
    private int year;

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public UUID getUuid() {
        return uuid;
    }

    private ArrayList<Airbag> airbags;
    private String color;

}

