package ModifiedSJF;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class ModifiedSJFDatacenterBroker extends DatacenterBroker {

    ModifiedSJFDatacenterBroker(String name) throws Exception {
        super(name);
    }
    private class CloudletLengthComparator implements Comparator<Cloudlet>
    {
        // Used for sorting in ascending order of
        // cloudlet length
        public int compare(Cloudlet a, Cloudlet b)
        {
            return (int) (a.getCloudletLength() - b.getCloudletLength());
        }
    }

    private void orderCloudletsbyLengthQueues() {
        int numberOfCloudlets = this.getCloudletList().size();
        Collections.sort(this.getCloudletList(), new CloudletLengthComparator());
        Iterator i$ = this.getCloudletList().iterator();
        LinkedList<Cloudlet> queueShortCloudlets = new LinkedList<Cloudlet>();
        LinkedList<Cloudlet> queueMidCloudlets = new LinkedList<Cloudlet>();
        LinkedList<Cloudlet> queueLongCloudlets = new LinkedList<Cloudlet>();
        Cloudlet cloudlet;
        while(i$.hasNext()) {
            cloudlet = (Cloudlet)i$.next();
            double cloudletLength = cloudlet.getCloudletLength();
            if (cloudletLength < 10000) {
                queueShortCloudlets.add(cloudlet);
            } else if (cloudletLength >50000) {
                queueLongCloudlets.add(cloudlet);
            }
            else {
                queueMidCloudlets.add(cloudlet);
            }
        }

        this.getCloudletList().removeAll(this.getCloudletList());

        Iterator shortIter$ = queueShortCloudlets.iterator();
        Iterator midIter$ = queueMidCloudlets.iterator();
        Iterator longIter$ = queueLongCloudlets.iterator();

        int numberOfSendedCloudlets = 0;
        while(numberOfSendedCloudlets < numberOfCloudlets){

            int numberOfSendedShort = 0;
            while(shortIter$.hasNext() && numberOfSendedShort<3) {
                this.getCloudletList().add((Cloudlet) shortIter$.next());
                ++numberOfSendedShort;
                ++numberOfSendedCloudlets;
            }
            int numberOfSendedMid = 0;
            while(midIter$.hasNext() && numberOfSendedMid<2) {
                this.getCloudletList().add((Cloudlet) midIter$.next());
                ++numberOfSendedMid;
                ++numberOfSendedCloudlets;
            }
            int numberOfSendedLong = 0;
            while(longIter$.hasNext() && numberOfSendedLong<1) {
                this.getCloudletList().add((Cloudlet) longIter$.next());
                ++numberOfSendedLong;
                ++numberOfSendedCloudlets;
            }
        }
    }

    @Override
    protected void submitCloudlets() {
        int vmIndex = 0;
        orderCloudletsbyLengthQueues();
        Iterator i$ = this.getCloudletList().iterator();

        while(true) {
            Cloudlet cloudlet;
            while(i$.hasNext()) {
                cloudlet = (Cloudlet)i$.next();
                Vm vm;
                if (cloudlet.getVmId() == -1) {
                    vm = (Vm)this.getVmsCreatedList().get(vmIndex);
                } else {
                    vm = VmList.getById(this.getVmsCreatedList(), cloudlet.getVmId());
                    if (vm == null) {
                        Log.printLine(CloudSim.clock() + ": " + this.getName() + ": Postponing execution of cloudlet " + cloudlet.getCloudletId() + ": bount VM not available");
                        continue;
                    }
                }

                Log.printLine(CloudSim.clock() + ": " + this.getName() + ": Sending cloudlet " + cloudlet.getCloudletId() + " to VM #" + vm.getId());
                cloudlet.setVmId(vm.getId());
                this.sendNow((Integer)this.getVmsToDatacentersMap().get(vm.getId()), 21, cloudlet);
                ++this.cloudletsSubmitted;
                vmIndex = (vmIndex + 1) % this.getVmsCreatedList().size();
                this.getCloudletSubmittedList().add(cloudlet);
            }

            i$ = this.getCloudletSubmittedList().iterator();

            while(i$.hasNext()) {
                cloudlet = (Cloudlet)i$.next();
                this.getCloudletList().remove(cloudlet);
            }

            return;
        }
    }


}