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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import javax.swing.JFrame;

public class Server extends Thread {

    //Initializing Server and client sockets
    private static ServerSocket serverSocket = null;
    private static Socket server = null;
    private static ServerSocket eveSocket = null;
    private static Socket eve = null;
    private static serverstartscreen serverScreen = null;

    //Robot and Threads Initialization
    static Robot robot = null;
    static Thread Server_Thread_1 = null;
    static Thread Server_Thread_2 = null;
    static Thread Server_Thread_3 = null;
    static Thread Server_Thread_4 = null;

    //Constructor to assign threads
    public Server() throws IOException, SQLException, ClassNotFoundException, Exception {

        //Creating Sockets on different ports
        serverSocket = new ServerSocket(8087);
        eveSocket = new ServerSocket(8888);
        serverSocket.setSoTimeout(1000000);
        
        //Thread for Sending Screenshots
        Server_Thread_1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //New Robot for the current screen
                    robot = new Robot();
                } catch (AWTException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }

                while (true) {
                    try {
                        //Accepting Client Requests and creating new socket for each client
                        server = serverSocket.accept();

                        //Send Screen captures to clients
                        sendScreen(robot);
                    } catch (SocketTimeoutException st) {
                        System.out.println("Socket timed out!");
                        break;
                    } catch (IOException e) {
                        break;
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        });

        //Thread to Receive events from clients
        Server_Thread_2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //New Robot for the current screen
                    robot = new Robot();
                } catch (AWTException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                while (true) {
                    try {
                        //Accepting Client Requests and creating new socket for each client
                        eve = eveSocket.accept();
                        System.out.println("Thread to Receive events from clients");
                        //Recieve Events from Clients on current screen
                        new ReceiveEvents(eve, robot);
                    } catch (SocketTimeoutException st) {
                        System.out.println("Socket timed out!");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        });

        //Thread for chat with Clients
        Server_Thread_3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    //Calling Server message method for chat
                    servermsg s = new servermsg();
                    s.sendmsg();
                    s.repaint();
                    s.pack();
                }
            }
        });
        
        ///Thread for send i4 
        Server_Thread_4 = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket processSeverSocket;
                ServerSocket hardwareSeverSocket;
                ServerSocket performenceSeverSocket;
                
                try {
                    processSeverSocket = new ServerSocket(8001);
                    hardwareSeverSocket = new ServerSocket(8002);
                    performenceSeverSocket = new ServerSocket(8003);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                while (true) {
                    try {
                        // Tạo các luồng riêng biệt cho từng hàm xử lý
                        Thread hardwareThread = new Thread(() -> {
                            try {    
                                //New Socket for send events
                                Socket hardSocket = hardwareSeverSocket.accept();     
                                JSONObject systemResourceInfo = new JSONObject();
                                systemResourceInfo.put("HardwareInfo", getHardwareInfo());
                                sendSystemResourceInfo(hardSocket, systemResourceInfo);
                            } catch (IOException | JSONException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                        Thread realtimeProcessThread = new Thread(() -> {
                            try {
                                Socket processSocket = processSeverSocket.accept();    
                                JSONObject systemResourceInfo = new JSONObject();
                                systemResourceInfo.put("RealtimeProcess", getRealtimeProcessInfo());
                                sendSystemResourceInfo(processSocket, systemResourceInfo);
                            } catch (IOException | JSONException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                        Thread performanceThread = new Thread(() -> {
                            try {
                                Socket perSocket = performenceSeverSocket.accept();    
                                JSONObject systemResourceInfo = new JSONObject();
                                systemResourceInfo.put("Performence", getPerformanceInfo());
                                sendSystemResourceInfo(perSocket, systemResourceInfo);
                            } catch (IOException | JSONException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                        // Khởi động các luồng
                        hardwareThread.start();
                        realtimeProcessThread.start();
                        performanceThread.start();
                        
                        hardwareThread.join();
                        realtimeProcessThread.join();
                        performanceThread.join();

                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        //Making start screen visible
        serverScreen = new serverstartscreen();
        serverScreen.setVisible(true);
        
        Server_Thread_1.start();
        Server_Thread_2.start();
        Server_Thread_3.start();
        Server_Thread_4.start();
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

    private static void sendSystemResourceInfo(Socket socket, JSONObject systemResourceInfo) throws IOException {
        DataOutputStream dtotpt = new DataOutputStream(socket.getOutputStream());
        dtotpt.writeUTF(systemResourceInfo.toString());
    }

    private static JSONObject getHardwareInfo() throws JSONException, IOException {
        Process process = Runtime.getRuntime().exec("systeminfo");

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        reader.close();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("systemInfo", output.toString());

        return jsonObject;
    }
    
    private static JSONArray getRealtimeProcessInfo() throws JSONException, IOException {
        JSONArray jsonArray = new JSONArray();

        // Thực thi lệnh tasklist.exe và lấy đầu ra từ nó
        Process process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Bỏ qua 2 dòng đầu tiên (chứa tiêu đề)
        reader.readLine();
        reader.readLine();
        reader.readLine();
        
        // Đọc từng dòng và trích xuất thông tin cần thiết
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            String name = parts[0];
            String pid = parts[1];
            String sessionName = parts[2];
            String sessionNumber = parts[3];
            String memoryUsage = parts[4];
            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Name", name);
            jsonObject.put("PID", pid);
            jsonObject.put("Session", sessionName + " " + sessionNumber);
            jsonObject.put("MemoryUsage", memoryUsage);

            jsonArray.put(jsonObject);
        }

        // Đóng reader
        reader.close();

        return jsonArray;
    }
     
    private static JSONObject getPerformanceInfo() throws JSONException, IOException {
        JSONObject performanceInfo = new JSONObject();
        
        LocalDateTime currentTime = LocalDateTime.now();
        
        // Lấy giây từ thời gian hiện tại
        int second = currentTime.getSecond();
        String currentSecond = String.valueOf(second);
        
        performanceInfo.put("CurrentSecond", currentSecond);
        // Lấy thông tin CPU
        Process cpuProcess = Runtime.getRuntime().exec("wmic cpu get loadpercentage");
        BufferedReader cpuReader = new BufferedReader(new InputStreamReader(cpuProcess.getInputStream()));
        String cpuLine;
        while ((cpuLine = cpuReader.readLine()) != null) {
            if (!cpuLine.isBlank() && !cpuLine.contains("LoadPercentage")) {
                performanceInfo.put("CPU", cpuLine.trim());
                break;
            }
        }
        cpuReader.close();
        
        // Lấy thông tin RAM
        Process ramProcess = Runtime.getRuntime().exec("wmic memorychip get capacity");
        BufferedReader ramReader = new BufferedReader(new InputStreamReader(ramProcess.getInputStream()));
        long totalRam = 0;
        while ((cpuLine = ramReader.readLine()) != null) {
            if (!cpuLine.isBlank() && !cpuLine.contains("Capacity")) {
                totalRam += Long.parseLong(cpuLine.trim());
            }
        }
        
        DecimalFormat df = new DecimalFormat("####.####");
        double ramInGB = totalRam / (1024.0 * 1024.0);
        performanceInfo.put("RAMTotal", df.format(ramInGB));
        
        ramProcess = Runtime.getRuntime().exec("wmic OS get FreePhysicalMemory /Value");
        ramReader = new BufferedReader(new InputStreamReader(ramProcess.getInputStream()));

        String line;
        long freePhysicalMemory = 0;
        while ((line = ramReader.readLine()) != null) {
            if (line.startsWith("FreePhysicalMemory")) {
                freePhysicalMemory = Long.parseLong(line.split("=")[1].trim());
            }
        }

        ramReader.close();

        double freePhysicalMemoryGB = freePhysicalMemory / (1024.0);
        performanceInfo.put("RAMFree", df.format(freePhysicalMemoryGB));
        
        // Lấy thông tin ổ cứng
        Process DiskProcess = Runtime.getRuntime().exec("wmic logicaldisk get DeviceID,Size,FreeSpace /format:value");
        BufferedReader diskReader = new BufferedReader(new InputStreamReader(DiskProcess.getInputStream()));

        String diskLine;
        String currentDrive;
        long totalSize;
        long freeSpace;
        JSONArray diskArrayJson = new JSONArray();
        JSONObject currentDisk = new JSONObject();
        while ((diskLine = diskReader.readLine()) != null) {
            if (diskLine.startsWith("DeviceID")) {
                if (diskLine.split("=").length == 2) {
                    currentDrive = diskLine.split("=")[1].trim();
                } else {
                    currentDrive = "";
                } 
                currentDisk.put("Name", currentDrive);
            } else if (diskLine.startsWith("FreeSpace")) {
                if (diskLine.split("=").length == 2) {
                    freeSpace = Long.parseLong(diskLine.split("=")[1].trim());
                } else {
                    freeSpace = 0;
                } 
                
                currentDisk.put("Free", freeSpace);
            }  else if (diskLine.startsWith("Size")) {
                if (diskLine.split("=").length == 2) {
                    totalSize = Long.parseLong(diskLine.split("=")[1].trim());
                } else {
                    totalSize = 0;
                } 
                currentDisk.put("Size", totalSize);
                diskArrayJson.put(currentDisk);
                currentDisk = new JSONObject();
            } 
        }
        
        performanceInfo.put("Disk", diskArrayJson);
        diskReader.close();
        
        // Thêm các thông tin khác như GPU, Ethernet, Wifi, các tiến trình đang chạy...
        JSONArray wifiAdapters = new JSONArray();
        // Lấy thông tin card mạng Ethernet
        Process CardProcess = Runtime.getRuntime().exec("wmic nic get Name, Speed");
        BufferedReader CardReader = new BufferedReader(new InputStreamReader(CardProcess.getInputStream()));
        while ((line = CardReader.readLine()) != null) {
            line = line.trim();
            if (line.contains("Wi-Fi") || line.contains("Wireless") || line.contains("Ethernet")) {
                String[] parts = line.split("\\s{2,}"); // Split by multiple spaces
                if (parts.length >= 2) {
                    JSONObject wifiAdapter = new JSONObject();
                    wifiAdapter.put("Name", parts[0].trim());
                    wifiAdapter.put("Speed", parts[1].trim());
                    wifiAdapters.put(wifiAdapter);
                }
            }
        }
        CardReader.close();   
        performanceInfo.put("Internet", wifiAdapters);
        
        return performanceInfo;
    }
}
