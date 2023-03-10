package SchedulerBase;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Log;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SchedulerBase {

    protected static List<Cloudlet> cloudletList;
    protected static List<Vm> vmList;
    protected static Datacenter[] datacenter;

    protected static List<Vm> createVMs(int userId) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();
        List<Integer> vmMips = Arrays.asList(1750, 1750, 1750, 1750, 1750, 1750);

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 256; //vm memory (MB)
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vmMips.size()];

        for (int i = 0; i < vmMips.size(); i++) {
            vm[i] = new Vm(i, userId, vmMips.get(i), pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    protected static List<Cloudlet> createCloudlets(int userId, int idShift) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
        List<Integer> cdlMIs = Arrays.asList(500, 2500,8000,	90000,	4000,	2600,	6500,	40000,	5000,	30000,	15000,	800,	7000,	61000,	6500);
        //cloudlet parameters
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cdlMIs.size()];

        for (int i = 0; i < cdlMIs.size(); i++) {
            cloudlet[i] = new Cloudlet(idShift + i, cdlMIs.get(i), pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }
        return list;
    }

    /**
     * Prints the Cloudlet objects
     *
     * @param list list of Cloudlets
     */
    protected static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        double totalWaitingTime = 0;
        double totalExecutionTime = 0;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" +
                indent + "Data center ID" +
                indent + "VM ID" +
                indent + "Length" +
                indent + indent + "Time" +
                indent + indent+"Start Time" +
                indent + indent+"Finish Time" +
                indent + indent+"Waiting Time" +
                indent + indent+"Cost of running");

        DecimalFormat dft = new DecimalFormat("###.##");
        dft.setMinimumIntegerDigits(2);

        double totalCost = 0;
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            totalWaitingTime += cloudlet.getWaitingTime();
            totalExecutionTime += cloudlet.getActualCPUTime();
            Log.format("%11s", dft.format(cloudlet.getCloudletId()));


            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.format("%10s", "SUCCESS");
                double cost = cloudlet.getCostPerSec() * cloudlet.getActualCPUTime();
                totalCost += cost;

                Log.format("%18s%9s%10s%12s%18s%19s%20s%23s\n", dft.format(cloudlet.getResourceId()).toString(),
                        dft.format(cloudlet.getVmId()).toString(),
                        dft.format(cloudlet.getCloudletLength()).toString(),
                        dft.format(cloudlet.getActualCPUTime()).toString(),
                        dft.format(cloudlet.getExecStartTime()).toString(),
                        dft.format(cloudlet.getFinishTime()).toString(),
                        dft.format(cloudlet.getWaitingTime()).toString(),
                        dft.format(cost).toString());
            }
        }
        Log.printLine("Total cost of cloudlet running " + totalCost);

        Log.printLine("Total waiting time " + totalWaitingTime);
        Log.printLine("Total execution time " + totalExecutionTime);
    }
}
