package CarModel;


import java.util.Objects;

public final class Airbag {
    private final String airbagType;
    private final boolean active;

    public Airbag(String airbagType, boolean active) {
        this.airbagType = airbagType;
        this.active = active;
    }

    public String airbagType() {
        return airbagType;
    }

    public boolean active() {
        return active;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Airbag) obj;
        return Objects.equals(this.airbagType, that.airbagType) &&
                this.active == that.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(airbagType, active);
    }

    @Override
    public String toString() {
        return "Airbag[" +
                "airbagType=" + airbagType + ", " +
                "active=" + active + ']';
    }
}