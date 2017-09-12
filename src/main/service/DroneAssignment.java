package main.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import main.data.Drone;
import main.data.Drones;
import main.data.Location;
import main.data.PackageDeli;
import main.data.Packages;
import main.data.Pair;
import main.utils.Httpclient;
import main.utils.Util;

public class DroneAssignment {
	public static final long currentUnixTime = Instant.now().getEpochSecond();
	private static final Gson gson = new Gson();
	private static final Gson gsonprettyprinter = new GsonBuilder().setPrettyPrinting().create();

	public static void main(String args[]) {
		// Get input Drones
		Util utils = new Util();
		String dronesUrl = "https://codetest.kube.getswift.co/drones";
		String dronesJsonString = null;
		try {
			dronesJsonString = Httpclient.callClient(dronesUrl);
		} catch (IOException e) {
			System.out.println("IO Exception occured while calling HTTP Client for Drones :" + e.getMessage());
		}
		String packagesURl = "https://codetest.kube.getswift.co/packages";
		String packageJsonString = null;
		try {
			packageJsonString = Httpclient.callClient(packagesURl);
		} catch (IOException e) {
			System.out.println("IO Exception occured while calling HTTP Client for Packages :" + e.getMessage());
		}
		if (packageJsonString != null && dronesJsonString != null) {
			System.out.println("*******************************************************************************************************************************************************************************************************************");
			System.out.println("**************************************************************************************************               INPUT    		    ***********************************************************************");
			System.out.println("*******************************************************************************************************************************************************************************************************************");
			System.out.println("Input dronesJsonString : " + dronesJsonString);
			System.out.println("Input packageJsonString : " + packageJsonString);
			System.out.println("*******************************************************************************************************************************************************************************************************************");
			// Call the object mappers
			Drones drones = utils.DronesObjectMapper(dronesJsonString);
			Packages packages = utils.PackagesObjectMapper(packageJsonString);
			System.out.println("Number of drones :" + drones.getDrones().size());
			System.out.println("Number of packages :" + packages.getPackages().size());
			JsonObject jsonOutput = new JsonObject();
			if (drones.getDrones().size() == 0) {
				System.out.println("No Drones to assign packages");
			} else if (packages.getPackages().size() == 0) {
				System.out.println("No Packages to assign to drones");
			} else {
				jsonOutput = assignDronesPackages(drones, packages);
				System.out.println("******************************************************************************************************************************************************************************************************************");
				System.out.println("************************************************************************************************           FINAL ASSIGNMENT    		 *************************************************************************");
				System.out.println("******************************************************************************************************************************************************************************************************************");
				
				System.out.println(gsonprettyprinter.toJson(jsonOutput));
			}

		}
	}

	private static JsonObject assignDronesPackages(Drones drones, Packages packages) {
		// Package Assignment to Delivery Drones
		Util utils = new Util();
		Drones dronesClone = new Drones();
		Packages packagesClone = new Packages();
		JsonObject jsonOutput = new JsonObject();
		int p_index = 0;
		int d_index = 0;
		PackageDeli packagetoDeliver = new PackageDeli();
		Drone availableDrone = new Drone();
		HashMap<Integer, Integer> droneAssignmentFinal = new HashMap<Integer, Integer>();
		HashMap<Long, Pair> droneAssignment = new HashMap<Long, Pair>();
		Long minTimeToDeliver = Long.MAX_VALUE;
		ArrayList<Integer> unAssignedPackages = new ArrayList<Integer>();
		Pair fastestDrone = new Pair(0, 0);
		dronesClone.setDrones(drones.getDrones());
		packagesClone.setPackages(packages.getPackages());
		while (p_index < packagesClone.getPackages().size()) {
			boolean assigned = false;
			packagetoDeliver = packagesClone.getPackages().get(p_index);
			minTimeToDeliver = Long.MAX_VALUE;
			for (d_index = 0; d_index < dronesClone.getDrones().size(); d_index++) {
				availableDrone = dronesClone.getDrones().get(d_index);
				Long time_required_todeliver;
				if (availableDrone.getPackages().isEmpty()) {
					time_required_todeliver = utils.CalculatePackageDeliveryTime(availableDrone.getLocation(), new Location(),
							packagetoDeliver.getDestination(), false);
				} else {
					time_required_todeliver = utils.CalculatePackageDeliveryTime(availableDrone.getLocation(),
							availableDrone.getPackages().get(0).getDestination(), packagetoDeliver.getDestination(),
							true);
				}
				Long total_time_todeliver = currentUnixTime + time_required_todeliver;
				if (packagetoDeliver.getDeadline() >= (total_time_todeliver)) {
					// Find all possible assignments of a package with available drones and minimum
					// required to deliver
					Pair entry = new Pair(availableDrone.getDroneId(), d_index);
					droneAssignment.put(total_time_todeliver, entry);
					if (total_time_todeliver < minTimeToDeliver) {
						minTimeToDeliver = total_time_todeliver;
					}
					assigned = true;
				}
			}
			if (assigned == false) {
				// Add unassigned packages
				unAssignedPackages.add(packagetoDeliver.getPackageId());
			} else {
				// Assign Fastest Drone for the package and remove from further consideration
				fastestDrone = droneAssignment.get(minTimeToDeliver);
				droneAssignmentFinal.put(fastestDrone.getKey(), packagetoDeliver.getPackageId());
				dronesClone.getDrones().remove(fastestDrone.getValue().intValue());
				droneAssignment.clear();
			}
			droneAssignment.clear();
			p_index++;
		}

		jsonOutput = convertToJSON(droneAssignmentFinal, unAssignedPackages);
		return jsonOutput;
	}

	private static JsonObject convertToJSON(HashMap<Integer, Integer> droneAssignmentFinal,
			ArrayList<Integer> unAssignedPackages) {
		JsonArray assignmentsArray = new JsonArray();
		if (!droneAssignmentFinal.isEmpty()) {
			for (Integer name : droneAssignmentFinal.keySet()) {
				JsonObject jsonObject = new JsonObject();
				String key = name.toString();
				String value = droneAssignmentFinal.get(name).toString();
				jsonObject.addProperty("droneId", key);
				jsonObject.addProperty("packageId", value);
				assignmentsArray.add(jsonObject);
			}
		}

		JsonObject jsonOutput = new JsonObject();
		jsonOutput.add("assignments", assignmentsArray);
		jsonOutput.add("unassignedPackageIds", gson.toJsonTree(unAssignedPackages));
		return jsonOutput;

	}
}
