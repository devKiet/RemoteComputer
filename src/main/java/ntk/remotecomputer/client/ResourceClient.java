package ntk.remotecomputer.client;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.net.Socket;
import ntk.remotecomputer.Commons;
import org.jfree.chart.plot.PlotOrientation;
import ntk.remotecomputer.server.ResourceInfo;

public class ResourceClient extends JFrame {
    private TimeSeries cpuSeries;
    private TimeSeries memorySeries;
    private String ip = null;

    public ResourceClient(String title, String ip) {
        super(title);
        this.ip = ip;
        // Create dataset
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        cpuSeries = new TimeSeries("CPU Load");
        memorySeries = new TimeSeries("Used Memory");
        dataset.addSeries(cpuSeries);
        dataset.addSeries(memorySeries);

        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "System Resource Usage",
                "Time",
                "Value",
                dataset,
                true,
                true,
                false
        );

        // Customize plot
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        // Create ChartPanel and add it to JFrame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ResourceClient example = new ResourceClient("System Resource Usage", "localhosts");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);

            example.startClient();
        });
    }

    public void startClient() {
        try (Socket socket = new Socket(this.ip, Commons.TRACKING_SOCKET_PORT);
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                ResourceInfo resourceInfo = (ResourceInfo) ois.readObject();
                updateChart(resourceInfo);
            }
        } catch (Exception e) {
        }
    }

    private void updateChart(ResourceInfo resourceInfo) {
        Second current = new Second();
        cpuSeries.addOrUpdate(current, resourceInfo.getCpuLoad());
        memorySeries.addOrUpdate(current, resourceInfo.getUsedMemory() / (1024 * 1024)); // Convert to MB
    }
}
