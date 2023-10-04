import java.util.List;

public class SolarSystemEager {
	private static final SolarSystemEager INSTANCE = new SolarSystemEager();
	
	private SolarSystemEager() {
	String [] planets = {"Mercury", "Venus", "Earth", "Mars", "Jupiter",
		        "Saturn", "Uranus","Neptune"};
	String sun = "The Sun";
	
	
	}
	public static SolarSystemEager getInstance() {
		return INSTANCE;
	}
	
	public String getSun(){
		return SolarSystemEager.INSTANCE.getSun();
	}
	public List getPlanets(){
		return SolarSystemEager.INSTANCE.getPlanets();
	}
	
	public String getStar() {
		return null;
	}
}
