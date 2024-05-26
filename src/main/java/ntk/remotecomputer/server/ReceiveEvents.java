
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
        ObjectInputStream reader = new ObjectInputStream(s.getInputStream());
        
        //Size of the Server screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimensions = toolkit.getScreenSize();
        
        //Creating new capture for height and width values
        Robot re = new Robot();
        BufferedImage st = re.createScreenCapture(new Rectangle(dimensions));
        
        //Set height and width values
        int height = st.getHeight();
        int width = st.getWidth();
        boolean isRuning = true;
        //Received Events decoder
        while (isRuning) {
            try {
                //Getting Event id from input stream
                var event = reader.readInt();
                
                int eventId = (int) event;
                
                //Check Event id and take actions accordingly
                switch (eventId) {
                    case MouseEvent.MOUSE_PRESSED:
                        int btnMask1 = reader.readInt();
                        r.mousePress(btnMask1);
                        System.out.println("Mouse pressed");
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        int btnMask2 = reader.readInt();
                        r.mouseRelease(btnMask2);
                        System.out.println("Mouse Released");
                        break;
                    case MouseEvent.MOUSE_MOVED:
                    case MouseEvent.MOUSE_DRAGGED:
                        r.mouseMove((int) (reader.readDouble()* width), (int) (reader.readDouble() * height));
                        System.out.println("Mouse moved");
                        break;
                    case KeyEvent.KEY_PRESSED:
                        r.keyPress(reader.readInt());
                        System.out.println("Key Pressed");
                        break;
                    case KeyEvent.KEY_RELEASED:
                        r.keyRelease(reader.readInt());
                        System.out.println("Key Released");
                        break;
                    case MouseWheelEvent.MOUSE_WHEEL:
                        int wheelRotation = reader.readInt();
                        double preciseWheelRotation = reader.readDouble();
                        int scrollAmount = reader.readInt();
                        int scrollType = reader.readInt();
                        int scrollMultiplier = scrollType == MouseWheelEvent.WHEEL_UNIT_SCROLL ? 1 : scrollAmount;
                        int wheelAmount = (int) (preciseWheelRotation * scrollMultiplier * wheelRotation);
                        r.mouseWheel(wheelAmount);
                        break;
                    case 10000:
                    default:
                        isRuning = false;
                        break;
                }
            } catch (IOException ex) {

                System.out.println("Exception in receive events:" + ex);
                // isRuning = false;
            }
        }

    }
}
