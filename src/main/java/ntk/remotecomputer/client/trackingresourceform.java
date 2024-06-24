package ntk.remotecomputer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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

public class trackingresourceform extends javax.swing.JFrame {
    private final TimeSeries cpuSeries;
    private final TimeSeries memorySeries;
    private final TimeSeries diskReadSeries;
    private final TimeSeries diskWriteSeries;
    private final TimeSeries networkSendSeries;
    private final TimeSeries networkReceiveSeries;
    private String ip = null;
    private static final int MAX_POINTS = 30;
    private Socket socket = null;
    private final JLabel cpuLabel;
    private final JLabel memoryLabel;
    private final JLabel diskReadLabel;
    private final JLabel diskWriteLabel;
    private final JLabel networkSendLabel;
    private final JLabel networkReceiveLabel;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public trackingresourceform(String ip) {
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setSize(850, 550);

        this.ip = ip;

        // Create datasets
        TimeSeriesCollection cpuDataset = new TimeSeriesCollection();
        cpuSeries = new TimeSeries("CPU Load");
        cpuSeries.setMaximumItemCount(MAX_POINTS);
        cpuDataset.addSeries(cpuSeries);

        TimeSeriesCollection memoryDataset = new TimeSeriesCollection();
        memorySeries = new TimeSeries("Used Memory");
        memorySeries.setMaximumItemCount(MAX_POINTS);
        memoryDataset.addSeries(memorySeries);

        TimeSeriesCollection diskDataset = new TimeSeriesCollection();
        diskReadSeries = new TimeSeries("Disk Read Rate");
        diskReadSeries.setMaximumItemCount(MAX_POINTS);
        diskWriteSeries = new TimeSeries("Disk Write Rate");
        diskWriteSeries.setMaximumItemCount(MAX_POINTS);
        diskDataset.addSeries(diskReadSeries);
        diskDataset.addSeries(diskWriteSeries);

        TimeSeriesCollection networkDataset = new TimeSeriesCollection();
        networkSendSeries = new TimeSeries("Network Send Rate");
        networkSendSeries.setMaximumItemCount(MAX_POINTS);
        networkReceiveSeries = new TimeSeries("Network Receive Rate");
        networkReceiveSeries.setMaximumItemCount(MAX_POINTS);
        networkDataset.addSeries(networkSendSeries);
        networkDataset.addSeries(networkReceiveSeries);

        // Create charts
        JFreeChart cpuChart = createChart(cpuDataset, "CPU Usage", "Time", "CPU Load (%)");
        JFreeChart memoryChart = createChart(memoryDataset, "Memory Usage", "Time", "Used Memory (MB)");
        JFreeChart diskChart = createChart(diskDataset, "Disk Read/Write Rate", "Time", "Rate (KB/s)");
        JFreeChart networkChart = createChart(networkDataset, "Network Send/Receive Rate", "Time", "Rate (Kbps)");

        // Create chart panels
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart);
        ChartPanel memoryChartPanel = new ChartPanel(memoryChart);
        ChartPanel diskChartPanel = new ChartPanel(diskChart);
        ChartPanel networkChartPanel = new ChartPanel(networkChart);

        // Customize tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("CPU Usage", cpuChartPanel);
        tabbedPane.addTab("Memory Usage", memoryChartPanel);
        tabbedPane.addTab("Disk Read/Write Rate", diskChartPanel);
        tabbedPane.addTab("Network Send/Receive Rate", networkChartPanel);

        // Create info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(6, 2));
        cpuLabel = new JLabel("CPU Load: ");
        memoryLabel = new JLabel("Used Memory: ");
        diskReadLabel = new JLabel("Disk Read Rate: ");
        diskWriteLabel = new JLabel("Disk Write Rate: ");
        networkSendLabel = new JLabel("Network Send Rate: ");
        networkReceiveLabel = new JLabel("Network Receive Rate: ");
        infoPanel.add(cpuLabel);
        infoPanel.add(memoryLabel);
        infoPanel.add(diskReadLabel);
        infoPanel.add(diskWriteLabel);
        infoPanel.add(networkSendLabel);
        infoPanel.add(networkReceiveLabel);
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 0));
        
        // Set layout and add components
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
        
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
    
    private JFreeChart createChart(TimeSeriesCollection dataset, String title, String timeAxisLabel, String valueAxisLabel) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                timeAxisLabel,
                valueAxisLabel,
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        return chart;
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
           } catch (IOException | ClassNotFoundException e) {
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

            cpuLabel.setText("CPU Load: " + decimalFormat.format(resourceInfo.getCpuLoad()) + " %");
            memoryLabel.setText("Used Memory: " + decimalFormat.format(resourceInfo.getUsedMemory() / (1024 * 1024)) + " MB");
            diskReadLabel.setText("Disk Read Rate: " + decimalFormat.format(resourceInfo.getDiskReadRate()) + " KB/s");
            diskWriteLabel.setText("Disk Write Rate: " + decimalFormat.format(resourceInfo.getDiskWriteRate()) + " KB/s");
            networkSendLabel.setText("Network Send Rate: " + decimalFormat.format(resourceInfo.getNetworkSendRate()) + " Kbps");
            networkReceiveLabel.setText("Network Receive Rate: " + decimalFormat.format(resourceInfo.getNetworkReceiveRate()) + " Kbps");
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

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
        java.awt.EventQueue.invokeLater(() -> {
            new trackingresourceform("localhost").setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
