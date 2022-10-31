/* *********************************************************************** *
 * project: org.matsim.*
 * RunEmissionToolOffline.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.training.session4;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Injector;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;

import static org.matsim.core.controler.Injector.createInjector;


public final class RunEmissionsOffline{

    private static final String configFile = "input/config_emission.xml";
    private final static String outputDirectory = "./output/";

    private static final String eventsFile =  "output/output_events.xml.gz";
    // (remove dependency of one test/execution path from other. kai/ihab, nov'18)

    private static final String emissionEventOutputFileName = "emission_events.xml.gz";
    private Config config;

    // =======================================================================================================

    public static void main (String[] args){
        RunEmissionsOffline emissionToolOfflineExampleV2 = new RunEmissionsOffline();
        emissionToolOfflineExampleV2.run();
    }

    public Config prepareConfig() {
        config = ConfigUtils.loadConfig(configFile, new EmissionsConfigGroup());
        return config;
    }

    public Config prepareConfig(String configFile) {
        config = ConfigUtils.loadConfig(configFile, new EmissionsConfigGroup());
        return config;
    }

    public void run() {
        if ( config==null ) {
            this.prepareConfig(configFile) ;
        }
        Scenario scenario = ScenarioUtils.loadScenario(config);

        EventsManager eventsManager = new EventsManagerImpl();

        AbstractModule module = new AbstractModule(){
            @Override
            public void install(){
                bind( Scenario.class ).toInstance( scenario );
                bind( EventsManager.class ).toInstance( eventsManager );
                bind( EmissionModule.class ) ;
            }
        };


        com.google.inject.Injector injector = createInjector(scenario.getConfig(), module);

        EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter = new EventWriterXML(scenario.getConfig().controler().getOutputDirectory() + "/" + emissionEventOutputFileName);
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);

        MatsimEventsReader matsimEventsReader = new MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(eventsFile);

        emissionEventWriter.closeFile();
    }
}