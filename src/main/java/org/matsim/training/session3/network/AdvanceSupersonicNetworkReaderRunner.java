package org.matsim.training.session3.network;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.LinkProperties;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.Arrays;
import java.util.HashSet;

public class AdvanceSupersonicNetworkReaderRunner {

    private static final String inputFile = "scenarios/week3/oberbayern-latest.osm.pbf";
    private static final String outputFile = "scenarios/week3/oberbayern.xml.gz";
    private static final CoordinateTransformation ct = TransformationFactory.
            getCoordinateTransformation(TransformationFactory.WGS84,
                    "EPSG:27700");

    public static void main(String[] args) {

        Network network = new SupersonicOsmNetworkReader.Builder()
                .setCoordinateTransformation(ct)
                .setIncludeLinkAtCoordWithHierarchy((coord, hierachyLevel) -> hierachyLevel == LinkProperties.LEVEL_MOTORWAY)
                .setPreserveNodeWithId(id -> id == 2)
                .addOverridingLinkProperties("residential", new LinkProperties(9, 1, 30.0 / 3.6, 1500, false))
                .setAfterLinkCreated((link, osmTags, isReverse) -> link.setAllowedModes(new HashSet<>(Arrays.asList(TransportMode.car, TransportMode.bike))))
                .build()
                .read(inputFile);

        NetworkUtils.runNetworkSimplifier(network);
        NetworkUtils.runNetworkCleaner(network);

        new NetworkWriter(network).write(outputFile);
    }
}

