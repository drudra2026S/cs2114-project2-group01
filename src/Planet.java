import java.time.Duration;
public class Planet
{
    private final String name;
    private final double dilateFactor;
    
    public Planet(String n, double d) {
        name = n;
        dilateFactor = d;
    }
    
    public String getName() {
        return name;
    }
    
    public double getDilate() {
        return dilateFactor;
    }
    
    private static Duration scale(Duration d, double factor) {
        return Duration.ofSeconds(Math.round(d.getSeconds() * factor));
    }
    
    public Duration localToEarth(Duration local) { 
        return scale(local, dilateFactor);
    }
    
    public Duration earthToLocal(Duration earth) {
        return scale(earth, 1.0 / dilateFactor);
    }

}
