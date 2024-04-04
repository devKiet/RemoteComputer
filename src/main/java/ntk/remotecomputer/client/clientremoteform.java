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
import java.awt.event.ActionEvent;
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
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author kiet
 */
public class clientremoteform extends javax.swing.JFrame {
    
    private String serverName = "";
    private static int portNo = 8087;
    Dimension screenSize;
    private volatile boolean running = true; 
    private ObjectOutputStream writer = null;
    private Socket eve = null;
    private Socket serverSocket = null;
    private int titleBarHeight = 0;
    
    /**
     * Creates new form clientremoteform
     */
    public clientremoteform(String ip) throws IOException {
        initComponents();
        
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
        
        setSize(screenSize.width, screenSize.height + titleBarHeight);
        panel.setSize(screenSize.width, screenSize.height);
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
        
//        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_WINDOWS, 0, false), "pressedWindows");
//        panel.getActionMap().put("pressedWindows", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//        });
//
//        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_WINDOWS, 0, true), "releasedWindows");
//        panel.getActionMap().put("releasedWindows", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // 
//            }
//        });
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        panel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(panel);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    //Receive Screen Thread 
    class T1 implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {       
                    //New Socket for receiving screen  
                    serverSocket = new Socket(serverName, portNo);  
                    
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
                    panel.removeAll();
                    panel.add(lab);
                    panel.revalidate();
                    panel.repaint();
                 
                    //Sleep for delay
                    sleep(10);  
                } catch (IOException ex) {

                } catch (InterruptedException ex) {

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
                        eve = new Socket(serverName, 8888);
                        System.out.println("Connected to server.");
                        setEvent(eve);
                    }

                    while (!eve.isClosed() && running) {
                        Thread.sleep(1000);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Connection failed. Retrying...");
                    try {
                        Thread.sleep(1000); // Wait for 5 seconds before retrying
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.currentThread().interrupt(); // Restore interrupted status
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
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    writer.writeInt(e.getID());
                    //System.out.println("Mouse Pressed");
                    writer.writeInt(InputEvent.getMaskForButton(e.getButton()));
                    //writer.flush();
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
        
        addMouseMotionListener(new MouseMotionAdapter() {
            //Mouse Moved Event
            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    writer.writeInt(e.getID());
                    System.out.println("Mouse moved");

                    double x = ((double) (e.getX()) / panel.getWidth());
                    double y = ((double) (e.getY() - titleBarHeight) / panel.getHeight());
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

                    double x = ((double) (e.getX()) / panel.getWidth());
                    double y = ((double) (e.getY() - titleBarHeight) / panel.getHeight());
                    writer.writeDouble(x);
                    writer.writeDouble(y);
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        
        // Add key listener to the frame
        addKeyListener(new KeyAdapter() {
            //Key Pressed Event
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    writer.writeInt(e.getID());
                    System.out.println("Key Pressed: " + e.getKeyChar());
                    writer.writeInt(e.getKeyCode());
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if (e.getKeyCode() == KeyEvent.VK_WINDOWS) {
                    e.consume();
                }
            }
                
            //Key Released Event
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    writer.writeInt(e.getID());
                    System.out.println("Key Released");
                    writer.writeInt(e.getKeyCode());
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (e.getKeyCode() == KeyEvent.VK_WINDOWS) {
                    e.consume();
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("Key Released");
            }
        });
        
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                try {
                    writer.writeInt(e.getID());
                    
                    double x = ((double) (e.getX()) / panel.getWidth());
                    double y = ((double) (e.getY() - titleBarHeight) / panel.getHeight());
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
        
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // JFrame gained focus, allow key events to be sent
            }

            @Override
            public void focusLost(FocusEvent e) {
                // JFrame lost focus, disable key events
                panel.requestFocusInWindow(); // Ensure another component gets focus
            }
        });
        
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
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
                    new clientremoteform("192.168.2.22").setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(clientremoteform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
