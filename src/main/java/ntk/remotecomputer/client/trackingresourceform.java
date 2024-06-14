/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ntk.remotecomputer.client;

import java.awt.Dimension;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import ntk.remotecomputer.Commons;
import ntk.remotecomputer.server.ResourceInfo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author kiet
 */
public class trackingresourceform extends javax.swing.JFrame {
    private TimeSeries cpuSeries;
    private TimeSeries memorySeries;
    private TimeSeries diskSeries;
    private TimeSeries networkSeries;
    private String ip = null;
    private long startTime = System.currentTimeMillis();
    /**
     * Creates new form trackingresourceform
     */
    public trackingresourceform(String ip) {
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setSize(850, 550);
        
        this.ip = ip;
        // Create dataset for CPU
        TimeSeriesCollection cpuDataset = new TimeSeriesCollection();
        cpuSeries = new TimeSeries("CPU Load");
        cpuSeries.setMaximumItemCount(30);
        cpuDataset.addSeries(cpuSeries);

        // Create dataset for Memory
        TimeSeriesCollection memoryDataset = new TimeSeriesCollection();
        memorySeries = new TimeSeries("Used Memory");
        memorySeries.setMaximumItemCount(30);
        memoryDataset.addSeries(memorySeries);
        
        // Create dataset for Disk
        TimeSeriesCollection diskDataset = new TimeSeriesCollection();
        diskSeries = new TimeSeries("Disk Usage");
        diskSeries.setMaximumItemCount(30);
        diskDataset.addSeries(diskSeries);

        // Create dataset for Network
        TimeSeriesCollection networkDataset = new TimeSeriesCollection();
        networkSeries = new TimeSeries("Network Speed");
        networkSeries.setMaximumItemCount(30);
        networkDataset.addSeries(networkSeries);

        // Create CPU chart
        JFreeChart cpuChart = ChartFactory.createTimeSeriesChart(
                "CPU Usage",
                "Time",
                "CPU Load (%)",
                cpuDataset,
                true,
                true,
                false
        );

        // Create Memory chart
        JFreeChart memoryChart = ChartFactory.createTimeSeriesChart(
                "Memory Usage",
                "Time",
                "Used Memory (MB)",
                memoryDataset,
                true,
                true,
                false
        );
        
        // Create Disk chart
        JFreeChart diskChart = ChartFactory.createTimeSeriesChart(
                "Disk Usage",
                "Time",
                "Disk Usage (MB)",
                diskDataset,
                true,
                true,
                false
        );

        // Create Network chart
        JFreeChart networkChart = ChartFactory.createTimeSeriesChart(
                "Network Speed",
                "Time",
                "Network Speed (Bytes)",
                networkDataset,
                true,
                true,
                false
        );

        // Customize CPU plot
        XYPlot cpuPlot = (XYPlot) cpuChart.getPlot();
        XYLineAndShapeRenderer cpuRenderer = new XYLineAndShapeRenderer();
        cpuPlot.setRenderer(cpuRenderer);
        cpuPlot.setOrientation(PlotOrientation.VERTICAL);

        // Customize Memory plot
        XYPlot memoryPlot = (XYPlot) memoryChart.getPlot();
        XYLineAndShapeRenderer memoryRenderer = new XYLineAndShapeRenderer();
        memoryPlot.setRenderer(memoryRenderer);
        memoryPlot.setOrientation(PlotOrientation.VERTICAL);

        // Customize Disk plot
        XYPlot diskPlot = (XYPlot) diskChart.getPlot();
        XYLineAndShapeRenderer diskRenderer = new XYLineAndShapeRenderer();
        diskPlot.setRenderer(diskRenderer);
        diskPlot.setOrientation(PlotOrientation.VERTICAL);

        // Customize Network plot
        XYPlot networkPlot = (XYPlot) networkChart.getPlot();
        XYLineAndShapeRenderer networkRenderer = new XYLineAndShapeRenderer();
        networkPlot.setRenderer(networkRenderer);
        networkPlot.setOrientation(PlotOrientation.VERTICAL);

        // Create ChartPanel for CPU, Memory, Disk, and Network
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart);
        ChartPanel memoryChartPanel = new ChartPanel(memoryChart);
        ChartPanel diskChartPanel = new ChartPanel(diskChart);
        ChartPanel networkChartPanel = new ChartPanel(networkChart);

        // Create JTabbedPane and add charts
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("CPU Usage", cpuChartPanel);
        tabbedPane.addTab("Memory Usage", memoryChartPanel);
        tabbedPane.addTab("Disk Usage", diskChartPanel);
        tabbedPane.addTab("Network Speed", networkChartPanel);

        // Add tabbedPane to JFrame
        setContentPane(tabbedPane);
        
        TrackingThread th = new TrackingThread();
        new Thread(th).start();
        
        setVisible(true);
    }
    //Thread for Chat
    class TrackingThread implements Runnable {

        @Override
        public void run() {
            try (Socket socket = new Socket(ip, Commons.TRACKING_SOCKET_PORT);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

               while (true) {
                   ResourceInfo resourceInfo = (ResourceInfo) ois.readObject();
                   updateChart(resourceInfo);
               }
           } catch (Exception e) {
           }
        }
    }

    private void updateChart(ResourceInfo resourceInfo) {
        SwingUtilities.invokeLater(() -> {
            long currentTime = System.currentTimeMillis();
            FixedMillisecond time = new FixedMillisecond(currentTime - startTime);
            cpuSeries.addOrUpdate(time, resourceInfo.getCpuLoad());
            memorySeries.addOrUpdate(time, resourceInfo.getUsedMemory() / (1024 * 1024)); // Convert to MB
            diskSeries.addOrUpdate(time, resourceInfo.getDiskUsage() / (1024 * 1024)); // Convert to MB
            networkSeries.addOrUpdate(time, resourceInfo.getNetworkSpeed());
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(trackingresourceform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(trackingresourceform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(trackingresourceform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(trackingresourceform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // new trackingresourceform().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
