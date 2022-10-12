package org.matsim.training.session3.network;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.IdentityTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class SupersonicNetworkReaderRunner {

    private static final String inputFile = "input/network/greater-manchester-latest.osm.pbf";
    private static final String outputFile = "input/network/greater-manchester.xml";
    private static final CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(
            TransformationFactory.WGS84,"EPSG:27700");

    public static void main(String[] args) {
        Network network = new SupersonicOsmNetworkReader.Builder()
                .setCoordinateTransformation(ct)
                .build().read(inputFile);

        NetworkUtils.runNetworkSimplifier(network);
        NetworkUtils.runNetworkCleaner(network);

        new NetworkWriter(network).write(outputFile);
    }
}
