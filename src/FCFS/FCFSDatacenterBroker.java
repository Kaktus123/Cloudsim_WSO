package FCFS;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.ArrayList;

/**
 * A Broker that schedules Tasks to the VMs
 * as per FCFS Scheduling Policy
 *
 * @author Linda J
 */
public class FCFSDatacenterBroker extends DatacenterBroker {

    public FCFSDatacenterBroker(String name) throws Exception {
        super(name);
    }

}