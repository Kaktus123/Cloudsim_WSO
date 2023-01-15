package FCFS;


import org.cloudbus.cloudsim.DatacenterBroker;


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