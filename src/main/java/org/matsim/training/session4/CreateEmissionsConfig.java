package org.matsim.training.session4;

import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.MatsimServices;

public final class CreateEmissionsConfig {

    private static final String networkFile = "network/greater-manchester_hbefa.xml";

    private static final String averageFleetWarmEmissionFactorsFile = "sample_41_EFA_HOT_vehcat_2020average.txt";
    private static final String averageFleetColdEmissionFactorsFile = "sample_41_EFA_ColdStart_vehcat_2020average.txt ";


    private static final String configFilePath ="input/config_emission.xml";

    public static void main(String[] args) {

        Config config = new Config();
        config.addCoreModules();
        MatsimServices controler = new Controler(config);

        //vehicles
        config.vehicles().setVehiclesFile("vehicles.xml");


        // network
        NetworkConfigGroup ncg = controler.getConfig().network();
        ncg.setInputFile(networkFile);

        // define emission tool input files
        EmissionsConfigGroup ecg = new EmissionsConfigGroup() ;
        controler.getConfig().addModule(ecg);

        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setHbefaVehicleDescriptionSource( EmissionsConfigGroup.HbefaVehicleDescriptionSource.asEngineInformationAttributes );
        ecg.setAverageWarmEmissionFactorsFile(averageFleetWarmEmissionFactorsFile);
        ecg.setAverageColdEmissionFactorsFile(averageFleetColdEmissionFactorsFile);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setWritingEmissionsEvents(true);

        // write config
        ConfigWriter cw = new ConfigWriter(config);
        cw.write(configFilePath);           }
}