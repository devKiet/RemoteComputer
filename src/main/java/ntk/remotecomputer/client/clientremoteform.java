/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ntk.remotecomputer.client;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.io.ObjectOutputStream;
import ntk.remotecomputer.Commons;

/**
 *
 * @author kiet
 */
public class clientremoteform extends javax.swing.JFrame {
    
    private String serverName = "";
    Dimension screenSize;
    private volatile boolean running = true; 
    private ObjectOutputStream writer = null;
    private Socket eve = null;
    private Socket serverSocket = null;
    private int titleBarHeight = 0;
    private Dimension lableSize;
    /**
     * Creates new form clientremoteform
     */
    public clientremoteform(String ip) throws IOException {
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimensions = toolkit.getScreenSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Insets screenInsets = toolkit.getScreenInsets(gd.getDefaultConfiguration());
        int taskbarHeight = screenInsets.bottom + screenInsets.top;
        
        JFrame tempFrame = new JFrame();
        tempFrame.setSize(200, 200);
        tempFrame.setVisible(true);
        Insets insets = tempFrame.getInsets();
        titleBarHeight = insets.top;
        tempFrame.dispose();
        
        // Set the frame size excluding taskbar and title bar
        screenSize = new Dimension(screenDimensions.width, screenDimensions.height - taskbarHeight - titleBarHeight);
 
        System.out.println(screenSize);
        //Set frame and Panel size

        panel.setSize(screenSize.width, screenSize.height);
        this.setSize(screenSize.width, screenSize.height + titleBarHeight);
        this.setExtendedState(JFrame.MAXIMIZED_HORIZ);

        //Setting IP address of Server
        serverName = ip;
        
        //Creating three Threads
        T1 t1 = new T1();
        T2 t2 = new T2();

        //Starting Threads
        Thread revScreen = new Thread(t1);
        revScreen.start();
        Thread revEvent = new Thread(t2);
        revEvent.start();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                t1.stop();
                t2.stop();
                dispose();       // Dispose the frame
                System.out.println("Frame closed and threads stopped.");
            }
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

        panel = new javax.swing.JPanel();
        lab = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 102));
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);

        panel.setBackground(new java.awt.Color(102, 102, 102));
        panel.setLayout(new java.awt.GridBagLayout());
        panel.add(lab, new java.awt.GridBagConstraints());

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    //Receive Screen Thread 
    class T1 implements Runnable {
        @Override
        public void run() {
            
            while (running) {
                try {       
                    //New Socket for receiving screen  
                    serverSocket = new Socket(serverName, Commons.SCREEN_SOCKET_PORT);  
                    
                    InputStream inputStream = serverSocket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);
                    
                    int imageSize = dataInputStream.readInt();

                    // Read image data
                    byte[] imageBytes = new byte[imageSize];
                    dataInputStream.readFully(imageBytes);

                    // Convert bytes to BufferedImage
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    
                    lableSize = getScaledDimension(new Dimension(img.getWidth(), img.getHeight()), screenSize);

                    Image scaledImage = img.getScaledInstance(lableSize.width, lableSize.height, Image.SCALE_SMOOTH);                  
                   
                    //Height and width of image         
                    //JLable lab = new JLabel(new ImageIcon(scaledImage));
                    lab.setIcon(new ImageIcon(scaledImage));

                    panel.revalidate();
                    panel.repaint();

                    //Sleep for delay
                    Thread.sleep(Commons.SLEEP_TIME);  
                } catch (IOException | InterruptedException ex) {

                }
            }

        }
         
        public void stop() {
            running = false;
            try {
                if (eve != null && !eve.isClosed()) {                   
                    eve.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Send Events Thread
    class T2 implements Runnable {
        @Override
        public void run() {
            
            while (running) {
                try {
                    if (eve == null || eve.isClosed()) {
                        System.out.println("Attempting to connect to server...");
                        eve = new Socket(serverName, Commons.EVENT_SOCKET_PORT);
                        System.out.println("Connected to server.");
                        setEvent(eve);
                    }

                    while (eve.isConnected() && !eve.isClosed() && running) {
                        try {
                            if (eve.isInputShutdown() || eve.isOutputShutdown()) {
                                System.out.println("Connection lost. Retrying...");
                                break;
                            }
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                            Thread.currentThread().interrupt(); // Restore interrupted status
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Connection failed. Retrying...");
                    try {
                        Thread.sleep(5000); // Wait for 5 seconds before retrying
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                }
            }
        }

        public void stop() {
            running = false;
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    // End thread
                    serverSocket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void setEvent(Socket eve) throws IOException {
        
        try {
            writer = new ObjectOutputStream(eve.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    writer.writeInt(e.getID());
                    //System.out.println("Mouse Pressed");
                    writer.writeInt(InputEvent.getMaskForButton(e.getButton()));
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            //Mouse Release Event
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    writer.writeInt(e.getID());
                    //System.out.println("Mouse released");
                    writer.writeInt(InputEvent.getMaskForButton(e.getButton()));
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        
        lab.addMouseMotionListener(new MouseMotionAdapter() {
            //Mouse Moved Event
            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    writer.writeInt(e.getID());
                    System.out.println("Mouse moved");

                    double x = ((double) (e.getX()) / lableSize.getWidth());
                    double y = ((double) (e.getY()) / lableSize.getHeight());
                    writer.writeDouble(x);
                    writer.writeDouble(y);
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                try {
                    writer.writeInt(e.getID());
                    System.out.println("Mouse moved");

                    double x = ((double) (e.getX()) / lableSize.getWidth());
                    double y = ((double) (e.getY()) / lableSize.getHeight());
                    writer.writeDouble(x);
                    writer.writeDouble(y);
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        
        // Add key listener to the frame
        lab.addKeyListener(new KeyAdapter() {
            //Key Pressed Event
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_WINDOWS) {
                        e.consume(); // Ignore Windows key
                        return;
                    }
                    writer.writeInt(e.getID());
                    System.out.println("Key Pressed: " + e.getKeyChar());
                    writer.writeInt(e.getKeyCode());
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                
            //Key Released Event
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_WINDOWS) {
                        e.consume(); // Ignore Windows key
                        return;
                    }
                    writer.writeInt(e.getID());
                    System.out.println("Key Released");
                    writer.writeInt(e.getKeyCode());
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("Key Released");
            }
        });
        
        lab.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                try {
                    writer.writeInt(e.getID());
                    System.out.println("Scroll mouse");
                    
                    double x = ((double) (e.getX()) / lableSize.getWidth());
                    double y = ((double) (e.getY()) / lableSize.getHeight());
                    writer.writeDouble(x);
                    writer.writeDouble(y);
                    
                    writer.writeInt(e.getWheelRotation());
                    writer.writeDouble(e.getPreciseWheelRotation());
                    writer.writeInt(e.getScrollAmount());
                    writer.writeInt(e.getScrollType());
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        lab.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // JFrame gained focus, allow key events to be sent
            }

            @Override
            public void focusLost(FocusEvent e) {
                // JFrame lost focus, disable key events
                lab.requestFocusInWindow(); // Ensure another component gets focus
            }
        });
        
        lab.setFocusable(true);
        lab.setFocusTraversalKeysEnabled(false);
    }
    
    private Dimension getScaledDimension(Dimension imgSize, Dimension frameSize) {
        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundWidth = frameSize.width;
        int boundHeight = frameSize.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // Calculate the aspect ratio
        double aspectRatio = (double) originalWidth / (double) originalHeight;

        // Scale to fit within frame size, maintaining aspect ratio
        if (originalWidth > boundWidth || originalHeight > boundHeight) {
            // If image is larger than frame, scale it down
            if ((double) boundWidth / (double) boundHeight > aspectRatio) {
                newHeight = boundHeight;
                newWidth = (int) (newHeight * aspectRatio);
            } else {
                newWidth = boundWidth;
                newHeight = (int) (newWidth / aspectRatio);
            }
        } else {
            // If image is smaller than frame, scale it up
            if ((double) boundWidth / (double) boundHeight > aspectRatio) {
                newHeight = boundHeight;
                newWidth = (int) (newHeight * aspectRatio);
            } else {
                newWidth = boundWidth;
                newHeight = (int) (newWidth / aspectRatio);
            }
        }

        return new Dimension(newWidth, newHeight);
    }

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
            java.util.logging.Logger.getLogger(clientremoteform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(clientremoteform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(clientremoteform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(clientremoteform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new clientremoteform(Commons.SERVER_NAME).setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lab;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
