package ntk.remotecomputer.client;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.*;

class Client extends Thread {
    //Height and Width sync class
    class Control {
        public AtomicInteger height = new AtomicInteger(0);               
        public AtomicInteger width = new AtomicInteger(0);
        public volatile JFrame frame;
        public volatile JPanel panel;
    }

    final Control c = new Control();
    clientfirstpage c1 = new clientfirstpage();               
    private String serverName = c1.ipAddress.getText(); //loop back ip
    private static int portNo = 8087;
    Dimension screenSize;

    //Receive Screen Thread 
    class T1 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {       
                    //New Socket for receiving screen  
                    Socket serverSocket = new Socket(serverName, portNo);  
                    
                    InputStream inputStream = serverSocket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    
                    int imageSize = dataInputStream.readInt();

                    // Read image data
                    byte[] imageBytes = new byte[imageSize];
                    dataInputStream.readFully(imageBytes);

                    // Convert bytes to BufferedImage
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

                    Image scaledImage = img.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);                  
                   
                    //Height and width of image         
                    JLabel lab = new JLabel(new ImageIcon(scaledImage));

                    //Add label to panel
                    c.panel.removeAll();
                    c.panel.add(lab);
                    c.panel.revalidate();
                    c.panel.repaint();
                 
                    //Sleep for delay
                    sleep(10);                                
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

    //Send Events Thread
    class T2 implements Runnable {

        @Override
        public void run() {
            try {
                // New Socket for send events
                Socket eve = new Socket(serverName, 8888);

                // Instance of SendEvents class
                SendEvents sendEvent = new SendEvents(c.panel, eve); 
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (true) {
                try {
                    // Sleep to reduce CPU usage
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
                
    //Client Constructor with IP of Server as parameter 
    Client(String ip) {
       
        //Frame and Panel Initialization
        c.frame = new JFrame();
        c.panel = new JPanel();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        Dimension screenDimensions = toolkit.getScreenSize();
        String os = System.getProperty("os.name").toLowerCase();
        int taskbarHeight = 0;
        if (os.contains("mac")) {
            // Get screen insets for macOS
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            Insets screenInsets = toolkit.getScreenInsets(gd.getDefaultConfiguration());
            taskbarHeight = screenInsets.top + 100;
        } else if (os.contains("win")) {
            // For Windows, use toolkit to get the screen insets
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            Insets screenInsets = toolkit.getScreenInsets(gd.getDefaultConfiguration());
            taskbarHeight = screenInsets.bottom + 25;
        }

        // Create a temporary JFrame to get the height of the title bar
        JFrame tempFrame = new JFrame();
        tempFrame.setUndecorated(true);
        tempFrame.setSize(200, 200);
        tempFrame.setVisible(true);
        Insets insets = tempFrame.getInsets();
        tempFrame.dispose();
        int titleBarHeight = insets.top;

        // Set the frame size excluding taskbar and title bar
        screenSize = new Dimension(screenDimensions.width, screenDimensions.height - taskbarHeight - titleBarHeight);
        
        System.out.println(screenSize);
        //Set frame and Panel size
        c.frame.setSize(screenSize.width, screenSize.height);
        c.panel.setSize(screenSize.width, screenSize.height);
        c.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        
        c.frame.add(c.panel);
        c.frame.pack();
        c.frame.setVisible(true);
        
        //Setting IP address of Server
        serverName = ip;
        
        //Creating three Threads
        T1 t1 = new T1();
        T2 t2 = new T2();

        //Starting Threads
        new Thread(t1).start();
        new Thread(t2).start();
    }
}
