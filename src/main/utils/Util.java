package main.utils;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import main.data.Drone;
import main.data.Drones;
import main.data.Location;
import main.data.PackageDeli;
import main.data.Packages;

public class Util {
//-37.8166597,144.9616589-303 Collins Street, Melbourne, VIC 3000 acc to googlemaps
public static Double depo_latitude = -37.8166597;
public static Double depo_longitude = 144.9616589;
public static Double drone_speed = 50.0;

public Drones DronesObjectMapper(String dronesJsonString) {
	Gson gson = new Gson();
	Drones drones = new Drones();
	//drones= gson.fromJson(dronesJsonString, Drones.class);
	List<Drone> list = gson.fromJson(dronesJsonString, new TypeToken<List<Drone>>(){}.getType());
	drones.setDrones(list);
	return drones;
}
public Packages PackagesObjectMapper(String packagesJsonString) {
	Gson gson = new Gson();
	Packages packages=new Packages();
	//packages = gson.fromJson(packagesJsonString, Packages.class);
	List<PackageDeli> list = gson.fromJson(packagesJsonString, new TypeToken<List<PackageDeli>>(){}.getType());
	packages.setPackages(list);
	return packages;	
}
public long CalculatePackageDeliveryTime(Location currLocation,Location start,Location destination,Boolean hasPackage) {
	Double time=0.0;
	Double time_depo_s=0.0;
	Double time_s_depo=0.0;
	Double time_depo_d=0.0;
	Double time_loc_depo=0.0;
	Double time_depo_des=0.0;
	if(hasPackage) {
		if(start.getLatitude() != destination.getLatitude() || start.getLongitude() != destination.getLongitude()) {
			Double distance1=DistanceCalculator.distance(currLocation.getLatitude(),currLocation.getLongitude(),start.getLatitude(), start.getLongitude(),"K");
			time_depo_s=distance1/drone_speed;
			Double distance2=DistanceCalculator.distance(start.getLatitude(), start.getLongitude(),depo_latitude,depo_longitude,"K");
			time_s_depo=distance2/drone_speed;
			Double distance3=DistanceCalculator.distance(depo_latitude,depo_longitude,destination.getLatitude(), destination.getLongitude(),"K");
			time_depo_d=distance3/drone_speed;
			time=time_depo_s+time_s_depo+time_depo_d;
		}	
		
	}
	else {
		start.setLatitude(depo_latitude);
		start.setLongitude(depo_longitude);
		
		if(start.getLatitude() != destination.getLatitude() || start.getLongitude() != destination.getLongitude()) {
			Double distance1=DistanceCalculator.distance(currLocation.getLatitude(),currLocation.getLongitude(),start.getLatitude(), start.getLongitude(),"K");
			time_loc_depo=distance1/drone_speed;
			Double distance2=DistanceCalculator.distance(start.getLatitude(), start.getLongitude(),destination.getLatitude(), destination.getLongitude(),"K");
			time_depo_des=distance2/drone_speed;
			time=time_loc_depo+time_depo_des;
		}
	}
	return (long)(time*60*60);
	
}
}
