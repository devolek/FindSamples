public class Sample {
    private String type;
    private String art;
    private String instance;
    private double pH;

    public Sample(String art, String instance, String type) {
        this.art = art;
        this.instance = instance;
        this.type = type;
    }

    public String getArt() {
        return art;
    }

    public String getInstance() {
        return instance;
    }

    public double getPH() {
        return pH;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return type + " - " + art + " - " + instance;
    }
}
