package Car;

import java.util.Objects;

public final class CarUpdate {
    private String model;
    private final Integer year;

    public CarUpdate(String model, Integer year) {
        this.model = model;
        this.year = year;
    }

    public String model() {
        return model;
    }

    public Integer year() {
        return year;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CarUpdate) obj;
        return Objects.equals(this.model, that.model) &&
                Objects.equals(this.year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, year);
    }

    @Override
    public String toString() {
        return "CarUpdate[" +
                "model=" + model + ", " +
                "year=" + year + ']';
    }

}
