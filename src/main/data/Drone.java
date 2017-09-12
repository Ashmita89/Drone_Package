package main.data;

import java.util.List;

public class Drone {
    private Integer droneId;
    private Location location;
    private List<PackageDeli> packages;
	public Integer getDroneId() {
		return droneId;
	}
	public void setDroneId(Integer droneId) {
		this.droneId = droneId;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public List<PackageDeli> getPackages() {
		return packages;
	}
	public void setPackages(List<PackageDeli> packages) {
		this.packages = packages;
	}
}
