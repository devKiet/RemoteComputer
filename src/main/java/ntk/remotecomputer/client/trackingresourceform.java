/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ntk.remotecomputer.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author kiet
 */
public class trackingresourceform extends javax.swing.JFrame {
    private TimeSeries cpuSeries;
    private TimeSeries memorySeries;
    private TimeSeries diskReadSeries;
    private TimeSeries diskWriteSeries;
    private TimeSeries networkSendSeries;
    private TimeSeries networkReceiveSeries;
    private String ip = null;
    private static final int MAX_POINTS = 30;
    private Socket socket = null;
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
        cpuSeries.setMaximumItemCount(MAX_POINTS);
        cpuDataset.addSeries(cpuSeries);

        // Create dataset for Memory
        TimeSeriesCollection memoryDataset = new TimeSeriesCollection();
        memorySeries = new TimeSeries("Used Memory");
        memorySeries.setMaximumItemCount(MAX_POINTS);
        memoryDataset.addSeries(memorySeries);

        // Create dataset for Disk Read/Write
        TimeSeriesCollection diskDataset = new TimeSeriesCollection();
        diskReadSeries = new TimeSeries("Disk Read Rate");
        diskReadSeries.setMaximumItemCount(MAX_POINTS);
        diskWriteSeries = new TimeSeries("Disk Write Rate");
        diskWriteSeries.setMaximumItemCount(MAX_POINTS);
        diskDataset.addSeries(diskReadSeries);
        diskDataset.addSeries(diskWriteSeries);

        // Create dataset for Network Send/Receive
        TimeSeriesCollection networkDataset = new TimeSeriesCollection();
        networkSendSeries = new TimeSeries("Network Send Rate");
        networkSendSeries.setMaximumItemCount(MAX_POINTS);
        networkReceiveSeries = new TimeSeries("Network Receive Rate");
        networkReceiveSeries.setMaximumItemCount(MAX_POINTS);
        networkDataset.addSeries(networkSendSeries);
        networkDataset.addSeries(networkReceiveSeries);

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

        // Create Disk Read/Write chart
        JFreeChart diskChart = ChartFactory.createTimeSeriesChart(
                "Disk Read/Write Rate",
                "Time",
                "Rate (KB/s)",
                diskDataset,
                true,
                true,
                false
        );

        // Create Network Send/Receive chart
        JFreeChart networkChart = ChartFactory.createTimeSeriesChart(
                "Network Send/Receive Rate",
                "Time",
                "Rate (Kbps)",
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

        // Create ChartPanel for CPU, Memory, Disk, Network
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart);
        ChartPanel memoryChartPanel = new ChartPanel(memoryChart);
        ChartPanel diskChartPanel = new ChartPanel(diskChart);
        ChartPanel networkChartPanel = new ChartPanel(networkChart);

        // Create JTabbedPane and add charts
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("CPU Usage", cpuChartPanel);
        tabbedPane.addTab("Memory Usage", memoryChartPanel);
        tabbedPane.addTab("Disk Read/Write Rate", diskChartPanel);
        tabbedPane.addTab("Network Send/Receive Rate", networkChartPanel);


        // Add tabbedPane to JFrame
        setContentPane(tabbedPane);
        
        TrackingThread th = new TrackingThread();
        new Thread(th).start();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to Close this Application?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                         socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(clientfirstpage.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        dispose();
                    }
                    dispose();
                }
            }
        });
        
        setVisible(true);
    }
    //Thread for Chat
    class TrackingThread implements Runnable {

        @Override
        public void run() {
            try {
                socket = new Socket(ip, Commons.TRACKING_SOCKET_PORT);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
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
            FixedMillisecond time = new FixedMillisecond(currentTime);
            cpuSeries.addOrUpdate(time, resourceInfo.getCpuLoad());
            memorySeries.addOrUpdate(time, resourceInfo.getUsedMemory() / (1024 * 1024)); // Convert to MB
            diskReadSeries.addOrUpdate(time, resourceInfo.getDiskReadRate()); // Already in KB/s
            diskWriteSeries.addOrUpdate(time, resourceInfo.getDiskWriteRate()); // Already in KB/s
            networkSendSeries.addOrUpdate(time, resourceInfo.getNetworkSendRate()); // Already in Kbps
            networkReceiveSeries.addOrUpdate(time, resourceInfo.getNetworkReceiveRate()); // Already in Kbps
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
