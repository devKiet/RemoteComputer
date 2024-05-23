
package ntk.remotecomputer.server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReceiveEvents {

    //Constructor with socket and robot as parameters
    ReceiveEvents(Socket s, Robot r) throws IOException, AWTException {
    
        //Input Stream for receiving events from clients
        ObjectInputStream scanner = new ObjectInputStream(s.getInputStream());
        
        //Size of the Server screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimensions = toolkit.getScreenSize();
        
        //Creating new capture for height and width values
        Robot re = new Robot();
        BufferedImage st = re.createScreenCapture(new Rectangle(dimensions));
        
        //Set height and width values
        int height = st.getHeight();
        int width = st.getWidth();
        
        //Received Events decoder
        while (true) {
            try {
                
                //Getting Event id from input stream
                int eventId = scanner.readInt();
                
                //Check Event id and take actions accordingly
                switch (eventId) {
                    case MouseEvent.MOUSE_PRESSED:
                        int btnMask1 = scanner.readInt();
                        r.mousePress(btnMask1);
                        System.out.println("Mouse pressed");
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        int btnMask2 = scanner.readInt();
                        r.mouseRelease(btnMask2);
                        System.out.println("Mouse Released");
                        break;
                    case MouseEvent.MOUSE_MOVED:
                    case MouseEvent.MOUSE_DRAGGED:
                        r.mouseMove((int) (scanner.readDouble()* width), (int) (scanner.readDouble() * height));
                        System.out.println("Mouse moved");
                        break;
                    case KeyEvent.KEY_PRESSED:
                        r.keyPress(scanner.readInt());
                        System.out.println("Key Pressed");
                        break;
                    case KeyEvent.KEY_RELEASED:
                        r.keyRelease(scanner.readInt());
                        System.out.println("Key Released");
                        break;
                    case MouseWheelEvent.MOUSE_WHEEL:
                        r.mouseWheel(scanner.readInt());
                        break;
                         
                    default:
                        break;
                }
            } catch (Exception ex) {

                System.out.println("Exception in receive events:" + ex);
            }
        }

    }
}
