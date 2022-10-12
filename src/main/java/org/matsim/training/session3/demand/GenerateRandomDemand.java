package org.matsim.training.session3.demand;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenerateRandomDemand {
	private static final Logger logger = Logger.getLogger(GenerateRandomDemand.class);

	// Specify all input files
	private static final String MSOAs = "input/MSOA/Middle_Layer_Super_Output_Areas_December_2011_Boundaries_BFC.shp"; // Polygon shapefile for demand generation
	private static final String CAR_MATRIX = "input/car_matrix.csv"; // OD Matrix for cars
	private static final String PLANS_FILE_OUTPUT = "input/plans.xml"; // The output file of demand generation

	// Define objects and parameters
	private final Scenario scenario;
	private Map<String, Geometry> shapeMap;
	private static final double SCALE_FACTOR = 0.05;

	// Entering point of the class "Generate Random Demand"
	public static void main(String[] args) {
		GenerateRandomDemand grd = new GenerateRandomDemand();
		grd.run();
	}

	// A constructor for this class, which is to set up the scenario container.
	GenerateRandomDemand() {
		this.scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
	}

	// Generate randomly sampling demand
	private void run() {
		this.shapeMap = readShapeFile(MSOAs, "msoa11cd");
		String[] destinationZones;
		String originZone;
		String line;
		String name;

		// Read in OD Matrix and Car Shares for each OD pair
		logger.info("Reading Car OD Matrix");
		try (BufferedReader ODMatrixFile = new BufferedReader(new FileReader(CAR_MATRIX))) {

			// Skip first 9 lines
			for ( int i = 0 ; i < 9 ; i++) {
				ODMatrixFile.readLine();
			}

			// Read destination zone names
			line = ODMatrixFile.readLine();
			destinationZones = Arrays.stream(line.split(",")).map(s -> s.substring(1).split(" : ")[0]).toArray(String[]::new);

			// Skip another line
			ODMatrixFile.readLine();

			// Read Matrix Flows
			while(!(line = ODMatrixFile.readLine()).isEmpty()) {
					String[] lineArray = line.split(",");
					originZone = lineArray[0].substring(1).split(" : ")[0];

					for (int i = 1 ; i < lineArray.length ; i++) {
						int count = Integer.parseInt(lineArray[i]);
						if(count > 0) {
							name = originZone + "-" + destinationZones[i];
							createOD(count,originZone,destinationZones[i],name);
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
		}

		// Write the population file to specified folder
		PopulationWriter pw = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
		pw.write(PLANS_FILE_OUTPUT);
	}

	// Read in shapefile
	public Map<String, Geometry> readShapeFile(String filename, String attrString) {
		Map<String, Geometry> shapeMap = new HashMap<>();
		for (SimpleFeature ft : ShapeFileReader.getAllFeatures(filename)) {
			GeometryFactory geometryFactory = new GeometryFactory();
			WKTReader wktReader = new WKTReader(geometryFactory);
			Geometry geometry;
			try {
				geometry = wktReader.read((ft.getAttribute("the_geom")).toString());
				shapeMap.put(ft.getAttribute(attrString).toString(), geometry);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return shapeMap;
	}

	// Create random coordinates within a given polygon
	private Coord drawRandomPointFromGeometry(Geometry g) {
		Random rnd = MatsimRandom.getLocalInstance();
		Point p;
		double x, y;
		do {
			x = g.getEnvelopeInternal().getMinX()
					+ rnd.nextDouble() * (g.getEnvelopeInternal().getMaxX() - g.getEnvelopeInternal().getMinX());
			y = g.getEnvelopeInternal().getMinY()
					+ rnd.nextDouble() * (g.getEnvelopeInternal().getMaxY() - g.getEnvelopeInternal().getMinY());
			p = MGC.xy2Point(x, y);
		} while (!g.contains(p));
		return new Coord(p.getX(), p.getY());
	}

	// Create od relations for each MSOA pair
	private void createOD(int pop, String origin, String destination, String toFromPrefix) {

		// Specify the number of commuters and the modal split of this relation
		double commuters = pop * SCALE_FACTOR;
		// Specify the ID of these two MSOAs
		Geometry home = this.shapeMap.get(origin);
		Geometry work = this.shapeMap.get(destination);
		// Randomly creating the home and work location of each commuter
		for (int i = 0; i <= commuters; i++) {
			// Specify the home location randomly
			Coord homeCoord = drawRandomPointFromGeometry(home);
			// Specify the working location randomly
			Coord workCoord = drawRandomPointFromGeometry(work);
			// Create plan for each commuter
			createOnePerson(i, homeCoord, workCoord, "car", toFromPrefix);
		}
	}

	// Create plan for each commuter
	private void createOnePerson(int i, Coord coord, Coord coordWork, String mode, String toFromPrefix) {

		double variance = Math.random() * 60;

		Id<Person> personId = Id.createPersonId(toFromPrefix + i);
		Person person = scenario.getPopulation().getFactory().createPerson(personId);

		Plan plan = scenario.getPopulation().getFactory().createPlan();

		Activity home = scenario.getPopulation().getFactory().createActivityFromCoord("home", coord);
		home.setEndTime(7 * 60 * 60 + variance * 120);
		plan.addActivity(home);

		Leg hinweg = scenario.getPopulation().getFactory().createLeg(mode);
		plan.addLeg(hinweg);

		Activity work = scenario.getPopulation().getFactory().createActivityFromCoord("work", coordWork);
		work.setEndTime(16 * 60 * 60 + variance * 120);
		plan.addActivity(work);

		Leg rueckweg = scenario.getPopulation().getFactory().createLeg(mode);
		plan.addLeg(rueckweg);

		Activity home2 = scenario.getPopulation().getFactory().createActivityFromCoord("home", coord);
		plan.addActivity(home2);

		person.addPlan(plan);
		scenario.getPopulation().addPerson(person);
	}

}