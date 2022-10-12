package org.matsim.training.session3.network;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;

import javax.xml.transform.TransformerFactory;

public class OsmNetworkReaderRunner {

    private static final String inputFile = "[path to input file]";
    private static final String outputFile = "[path to output file]";
    private static final CoordinateTransformation ct = TransformationFactory.
            getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:27700");

    public static void main(String[] args) {
        Network network = NetworkUtils.createNetwork();

        OsmNetworkReader osmReader = new OsmNetworkReader(network, ct);
        osmReader.parse(inputFile);

        NetworkUtils.runNetworkSimplifier(network);
        NetworkUtils.runNetworkCleaner(network);

        NetworkWriter writer = new NetworkWriter(network);
        writer.write(outputFile);
    }
}

