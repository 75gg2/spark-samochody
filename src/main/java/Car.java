import static java.lang.Integer.parseInt;

public class Car {
    private final int index;
    private String model;
    private int doors;
    private boolean damaged;
    private String country;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getDoors() {
        return doors;
    }

    public void setDoors(int doors) {
        this.doors = doors;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Car(int index, String model, int doors, Boolean damaged, String country) {
        this.index = index;
        this.model = model;
        this.doors = doors;
        this.damaged = damaged;
        this.country = country;
    }

    public int getIndex() {
        return index;
    }
}
