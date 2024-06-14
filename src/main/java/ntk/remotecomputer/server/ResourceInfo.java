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
    private long diskUsage;
    private long networkSpeed;

    public ResourceInfo(double cpuLoad, long usedMemory, long totalMemory, long uptime, long diskUsage, long networkSpeed) {
        this.cpuLoad = cpuLoad;
        this.usedMemory = usedMemory;
        this.totalMemory = totalMemory;
        this.uptime = uptime;
        this.diskUsage = diskUsage;
        this.networkSpeed = networkSpeed;
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
    
    public long getDiskUsage() {
        return diskUsage;
    }
    
    public long getNetworkSpeed() {
        return networkSpeed;
    }
}
