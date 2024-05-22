package ntk.remotecomputer.client;


import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

class SendEvents implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    ObjectOutputStream writer;
    int width, height;
    private JPanel frame = null;

    //Constructor with panel and socket as parameters
    SendEvents(JPanel frame, Socket s) {
       
        //Frame and height, width Initialization
        this.frame = frame;
        this.height = frame.getWidth();
        this.width = frame.getHeight();
        
        //Adding Listeners in the frame
        
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);  
        frame.addMouseWheelListener(this);
        frame.addKeyListener(this);
        frame.setFocusable(true);
        
        System.out.println("Send Events called" + height + " " + width);
        
        //Getting Output Streams of the Socket for sending Events
        try {
            writer = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Mouse Press Event
    @Override
    public void mousePressed(MouseEvent e) {
        try {
            writer.writeInt(e.getID());
            //System.out.println("Mouse Pressed");
            writer.writeInt(InputEvent.getMaskForButton(e.getButton()));
            //writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    }
    
    //Mouse Moved Event
    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            writer.writeInt(e.getID());
            //System.out.println("Mouse moved");
     
            double x = ((double) e.getX() / frame.getWidth());
            double y = ((double) e.getY() / frame.getHeight());
            writer.writeDouble(x);
            writer.writeDouble(y);
            // writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        try {
            writer.writeInt(e.getID());
            //System.out.println("Mouse moved");
     
            double x = ((double) e.getX() / frame.getWidth());
            double y = ((double) e.getY() / frame.getHeight());
            writer.writeDouble(x);
            writer.writeDouble(y);
            // writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        try {
            writer.writeInt(e.getID());
            writer.writeInt(e.getWheelRotation());
        } catch (IOException ex) {
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Key Pressed Event
    @Override
    public void keyPressed(KeyEvent e) {
        try {
            writer.writeInt(e.getID());
            System.out.println("Key Pressed: " + e.getKeyChar());
            writer.writeInt(e.getKeyCode());
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SendEvents.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("Key Released");
    }
}
