package SJF;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Log;

import org.cloudbus.cloudsim.core.CloudSim;

import org.cloudbus.cloudsim.lists.VmList;

import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;


public class SJFDatacenterBroker extends DatacenterBroker {

    SJFDatacenterBroker(String name) throws Exception {
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


    @Override
    protected void submitCloudlets() {
        int vmIndex = 0;
        Collections.sort(this.getCloudletList(), new CloudletLengthComparator());
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