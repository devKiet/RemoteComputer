package ntk.remotecomputer.server;

import java.io.Serializable;

/**
 *
 * @author kiet
 */
public class ResourceInfo implements Serializable {
    private final double cpuLoad;
    private final long usedMemory;
    private final long totalMemory;
    private final long uptime;
    private final double diskReadRate;
    private final double diskWriteRate;
    private final double networkSendRate;
    private final double networkReceiveRate;

    public ResourceInfo(double cpuLoad, long usedMemory, long totalMemory, long uptime, double diskReadRate, double diskWriteRate, double networkSendRate, double networkReceiveRate) {
        this.cpuLoad = cpuLoad;
        this.usedMemory = usedMemory;
        this.totalMemory = totalMemory;
        this.uptime = uptime;
        this.diskReadRate = diskReadRate;
        this.diskWriteRate = diskWriteRate;
        this.networkSendRate = networkSendRate;
        this.networkReceiveRate = networkReceiveRate;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getUptime() {
        return uptime;
    }
    
    public double getDiskReadRate() {
        return diskReadRate;
    }
    
    public double getDiskWriteRate() {
        return diskWriteRate;
    }
    
    public double getNetworkSendRate() {
        return networkSendRate;
    }
    
    public double getNetworkReceiveRate() {
        return networkReceiveRate;
    }
}
