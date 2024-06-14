/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ntk.remotecomputer.server;

import java.io.Serializable;

/**
 *
 * @author kiet
 */
public class ResourceInfo implements Serializable {
    private double cpuLoad;
    private long usedMemory;
    private long totalMemory;
    private long uptime;
    private double diskReadRate;
    private double diskWriteRate;
    private double networkSendRate;
    private double networkReceiveRate;

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
