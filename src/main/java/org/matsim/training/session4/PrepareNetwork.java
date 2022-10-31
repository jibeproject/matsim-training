package org.matsim.training.session4;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;

public class PrepareNetwork {

    private static final String HBEFA_ROAD_TYPE = "hbefa_road_type";

    public static void main(String[] args) {
        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile("input/network/greater-manchester.xml");

        for(Link link: network.getLinks().values()) {
            link.getAttributes().putAttribute(HBEFA_ROAD_TYPE, "URB/Local/50");
        }

        new NetworkWriter(network).write("input/network/greater-manchester_hbefa.xml");
    }

}
