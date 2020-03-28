public class Sample
{
    private String art;
    private String instance;
    private double pH;

    public Sample(String art, String instance)
    {
        this.art = art;
        this.instance = instance;
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

    public String toString()
    {
        return art + " - " + instance;
    }
}
