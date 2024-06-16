package ntk.remotecomputer.server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.net.*;
import java.sql.SQLException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import java.util.concurrent.atomic.AtomicBoolean;
import ntk.remotecomputer.Commons;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;


public class Server extends Thread {

    //Initializing Server and client sockets
    private static ServerSocket serverSocket = null;
    private static Socket server = null;
    private static ServerSocket eveSocket = null;
    private static Socket eve = null;
    private AtomicBoolean running = new AtomicBoolean(true);
    private static ServerSocket trackingServerSocket = null;
    private static ServerSocket chatSocket = null;
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final HardwareAbstractionLayer hal = systemInfo.getHardware();
    private static final CentralProcessor processor = hal.getProcessor();
    private static long[] prevTicks = processor.getSystemCpuLoadTicks();

    public void setRunning(boolean running) {
        this.running.set(running);
    }
    
    //Robot and Threads Initialization
    static Robot robot = null;
    static Thread Server_Thread_1 = null;
    static Thread Server_Thread_2 = null;
    static Thread Server_Thread_3 = null;
    static Thread Server_Thread_4 = null;
    
    //Constructor to assign threads
    public Server() throws IOException, SQLException, ClassNotFoundException, Exception {

        //Creating Sockets on different ports
        serverSocket = new ServerSocket(Commons.SCREEN_SOCKET_PORT);
        eveSocket = new ServerSocket(Commons.EVENT_SOCKET_PORT);
        trackingServerSocket = new ServerSocket(Commons.TRACKING_SOCKET_PORT);
        
        //Thread for Sending Screenshots
        Server_Thread_1 = new Thread(new sendScreenThread());

        //Thread to Receive events from clients
        Server_Thread_2 = new Thread(new receviceEventThread());

        //Thread for chat with Clients
        Server_Thread_3 = new Thread(new chatThread());
        
        ///Thread for send i4 
        Server_Thread_4 = new Thread(new trackingThread());
        
        Server_Thread_1.start();
        Server_Thread_2.start();
        Server_Thread_3.start();
        Server_Thread_4.start();
    }
    
    public void stopServer() {
        running.set(false);
        try {
            if (eveSocket != null) {
                eveSocket.close();
                eve.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (trackingServerSocket != null) {
                trackingServerSocket.close();
            }
            if (chatSocket != null) {
                chatSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Thread for Sending Screenshots
    class sendScreenThread implements Runnable {
        @Override
        public void run() {
            try {
                //New Robot for the current screen
                robot = new Robot();
            } catch (AWTException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (running.get()) {
                try {
                    //Accepting Client Requests and creating new socket for each client
                    server = serverSocket.accept();

                    //Send Screen captures to clients
                    sendScreen(robot);
                } catch (SocketTimeoutException st) {
                    System.out.println("Socket timed out!");
                    break;
                } catch (IOException e) {
                    if (running.get()) {
                        e.printStackTrace();
                    } else {
                        System.out.println("Server stopped.");
                    }
                    break;
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        
        public void stopRunning() {
            running.set(false);
        }

        public void startRunning() {
            if (!running.get()) {
                running.set(true);
                new Thread(this).start(); // Khởi động lại thread
            }
        }
    }

    //Thread to Receive events from clients
    class receviceEventThread implements Runnable {
        @Override
        public void run() {
            try {
                //New Robot for the current screen
                robot = new Robot();
            } catch (AWTException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (running.get()) {
                try {
                    //Accepting Client Requests and creating new socket for each client
                    eve = eveSocket.accept();
                    System.out.println("Thread to Receive events from clients");
                    //Recieve Events from Clients on current screen
                    new ReceiveEvents(eve, robot);
                } catch (IOException e) {
                    if (running.get()) {
                        e.printStackTrace();
                    } else {
                        System.out.println("Server stopped.");
                    }
                    break;
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
            System.out.println("Th 2 stop");
        }
    }

    //Thread for chat with Clients
    class chatThread implements Runnable {
        @Override
        public void run() {
            while (running.get()) {

                //Calling Server message method for chat
                servermsg s = new servermsg();
                try {
                    chatSocket = new ServerSocket(Commons.CHAT_SOCKET_PORT);
                    s.sendmsg(chatSocket);
                    s.repaint();
                    s.pack();
                } catch (IOException e) {
                    if (running.get()) {
                        e.printStackTrace();
                    } else {
                        System.out.println("Server stopped.");
                    }
                }
            }
        }
    }

    ///Thread for send i4 
    class trackingThread implements Runnable {
        @Override
        public void run() {
            while (running.get()) {
                try (Socket clientSocket = trackingServerSocket.accept();
                    ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
                    while (running.get()) {
                        ResourceInfo resourceInfo = gatherResourceInfo();
                        oos.writeObject(resourceInfo);
                        oos.flush();
                        Thread.sleep(1000); // Gửi thông tin mỗi giây
                    }
                } catch (IOException ex) {
                    if (running.get()) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    } else {
                        System.out.println("Server stopped.");
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        // Method to send screenshots to clients
        private ResourceInfo gatherResourceInfo() {
            // Lấy thông tin CPU
            double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            prevTicks = processor.getSystemCpuLoadTicks();

            // Lấy thông tin bộ nhớ
            GlobalMemory memory = hal.getMemory();
            long totalMemory = memory.getTotal();
            long availableMemory = memory.getAvailable();
            long usedMemory = totalMemory - availableMemory;

            // Lấy thông tin uptime
            long uptime = systemInfo.getOperatingSystem().getSystemUptime();

            // Lấy thông tin tốc độ đọc/ghi ổ đĩa
            long totalReadBytes = 0;
            long totalWriteBytes = 0;
            List<HWDiskStore> diskStores = systemInfo.getHardware().getDiskStores();
            for (HWDiskStore disk : diskStores) {
                disk.updateAttributes();
                totalReadBytes += disk.getReadBytes();
                totalWriteBytes += disk.getWriteBytes();
            }
            double diskReadRate = totalReadBytes / 1024.0; // Convert to KB
            double diskWriteRate = totalWriteBytes / 1024.0; // Convert to KB

            // Lấy thông tin tốc độ mạng
            List<NetworkIF> networkIFs = hal.getNetworkIFs();
            long totalBytesSent = 0;
            long totalBytesRecv = 0;
            for (NetworkIF net : networkIFs) {
                net.updateAttributes();
                totalBytesSent += net.getBytesSent();
                totalBytesRecv += net.getBytesRecv();
            }
            double networkSendRate = totalBytesSent * 8 / 1024.0; // Convert to Kbps
            double networkReceiveRate = totalBytesRecv * 8 / 1024.0; // Convert to Kbps

            return new ResourceInfo(cpuLoad, usedMemory, totalMemory, uptime, diskReadRate, diskWriteRate, networkSendRate, networkReceiveRate);
        }
    }
    
    // Method to send screenshots to clients
    private static void sendScreen(final Robot robot) throws IOException {
        //Get current screen dimensions
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        // Take screenshot using robot class
        BufferedImage screenshot = robot.createScreenCapture(new Rectangle(screenSize));

        // Send image to client through output stream of socket
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Use JPEG with lower quality to reduce size
        ImageIO.write(screenshot, "jpg", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        // Write image size to output stream
        try (OutputStream outputStream = server.getOutputStream();
             DataOutputStream dos = new DataOutputStream(outputStream)) {

            dos.writeInt(imageBytes.length);
            dos.flush();

            // Write image data to output stream
            outputStream.write(imageBytes);
            outputStream.flush();
        }
    }
}
