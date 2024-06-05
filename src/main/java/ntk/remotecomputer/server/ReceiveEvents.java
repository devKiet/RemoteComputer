
package ntk.remotecomputer.server;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
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
import java.util.HashMap;
import java.util.Map;

public class ReceiveEvents {
    private static final Map<Integer, Integer> keyCodeMap = new HashMap<>();
    
    static {
        // Map chữ cái
        keyCodeMap.put(NativeKeyEvent.VC_A, KeyEvent.VK_A);
        keyCodeMap.put(NativeKeyEvent.VC_B, KeyEvent.VK_B);
        keyCodeMap.put(NativeKeyEvent.VC_C, KeyEvent.VK_C);
        keyCodeMap.put(NativeKeyEvent.VC_D, KeyEvent.VK_D);
        keyCodeMap.put(NativeKeyEvent.VC_E, KeyEvent.VK_E);
        keyCodeMap.put(NativeKeyEvent.VC_F, KeyEvent.VK_F);
        keyCodeMap.put(NativeKeyEvent.VC_G, KeyEvent.VK_G);
        keyCodeMap.put(NativeKeyEvent.VC_H, KeyEvent.VK_H);
        keyCodeMap.put(NativeKeyEvent.VC_I, KeyEvent.VK_I);
        keyCodeMap.put(NativeKeyEvent.VC_J, KeyEvent.VK_J);
        keyCodeMap.put(NativeKeyEvent.VC_K, KeyEvent.VK_K);
        keyCodeMap.put(NativeKeyEvent.VC_L, KeyEvent.VK_L);
        keyCodeMap.put(NativeKeyEvent.VC_M, KeyEvent.VK_M);
        keyCodeMap.put(NativeKeyEvent.VC_N, KeyEvent.VK_N);
        keyCodeMap.put(NativeKeyEvent.VC_O, KeyEvent.VK_O);
        keyCodeMap.put(NativeKeyEvent.VC_P, KeyEvent.VK_P);
        keyCodeMap.put(NativeKeyEvent.VC_Q, KeyEvent.VK_Q);
        keyCodeMap.put(NativeKeyEvent.VC_R, KeyEvent.VK_R);
        keyCodeMap.put(NativeKeyEvent.VC_S, KeyEvent.VK_S);
        keyCodeMap.put(NativeKeyEvent.VC_T, KeyEvent.VK_T);
        keyCodeMap.put(NativeKeyEvent.VC_U, KeyEvent.VK_U);
        keyCodeMap.put(NativeKeyEvent.VC_V, KeyEvent.VK_V);
        keyCodeMap.put(NativeKeyEvent.VC_W, KeyEvent.VK_W);
        keyCodeMap.put(NativeKeyEvent.VC_X, KeyEvent.VK_X);
        keyCodeMap.put(NativeKeyEvent.VC_Y, KeyEvent.VK_Y);
        keyCodeMap.put(NativeKeyEvent.VC_Z, KeyEvent.VK_Z);

        // Map chữ số
        keyCodeMap.put(NativeKeyEvent.VC_1, KeyEvent.VK_1);
        keyCodeMap.put(NativeKeyEvent.VC_2, KeyEvent.VK_2);
        keyCodeMap.put(NativeKeyEvent.VC_3, KeyEvent.VK_3);
        keyCodeMap.put(NativeKeyEvent.VC_4, KeyEvent.VK_4);
        keyCodeMap.put(NativeKeyEvent.VC_5, KeyEvent.VK_5);
        keyCodeMap.put(NativeKeyEvent.VC_6, KeyEvent.VK_6);
        keyCodeMap.put(NativeKeyEvent.VC_7, KeyEvent.VK_7);
        keyCodeMap.put(NativeKeyEvent.VC_8, KeyEvent.VK_8);
        keyCodeMap.put(NativeKeyEvent.VC_9, KeyEvent.VK_9);
        keyCodeMap.put(NativeKeyEvent.VC_0, KeyEvent.VK_0);

        // Map phím chức năng
        keyCodeMap.put(NativeKeyEvent.VC_F1, KeyEvent.VK_F1);
        keyCodeMap.put(NativeKeyEvent.VC_F2, KeyEvent.VK_F2);
        keyCodeMap.put(NativeKeyEvent.VC_F3, KeyEvent.VK_F3);
        keyCodeMap.put(NativeKeyEvent.VC_F4, KeyEvent.VK_F4);
        keyCodeMap.put(NativeKeyEvent.VC_F5, KeyEvent.VK_F5);
        keyCodeMap.put(NativeKeyEvent.VC_F6, KeyEvent.VK_F6);
        keyCodeMap.put(NativeKeyEvent.VC_F7, KeyEvent.VK_F7);
        keyCodeMap.put(NativeKeyEvent.VC_F8, KeyEvent.VK_F8);
        keyCodeMap.put(NativeKeyEvent.VC_F9, KeyEvent.VK_F9);
        keyCodeMap.put(NativeKeyEvent.VC_F10, KeyEvent.VK_F10);
        keyCodeMap.put(NativeKeyEvent.VC_F11, KeyEvent.VK_F11);
        keyCodeMap.put(NativeKeyEvent.VC_F12, KeyEvent.VK_F12);

        // Map phím điều hướng
        keyCodeMap.put(NativeKeyEvent.VC_UP, KeyEvent.VK_UP);
        keyCodeMap.put(NativeKeyEvent.VC_DOWN, KeyEvent.VK_DOWN);
        keyCodeMap.put(NativeKeyEvent.VC_LEFT, KeyEvent.VK_LEFT);
        keyCodeMap.put(NativeKeyEvent.VC_RIGHT, KeyEvent.VK_RIGHT);

        // Map phím điều khiển
        keyCodeMap.put(NativeKeyEvent.VC_ENTER, KeyEvent.VK_ENTER);
        keyCodeMap.put(NativeKeyEvent.VC_ESCAPE, KeyEvent.VK_ESCAPE);
        keyCodeMap.put(NativeKeyEvent.VC_BACKSPACE, KeyEvent.VK_BACK_SPACE);
        keyCodeMap.put(NativeKeyEvent.VC_TAB, KeyEvent.VK_TAB);
        keyCodeMap.put(NativeKeyEvent.VC_SPACE, KeyEvent.VK_SPACE);
        keyCodeMap.put(NativeKeyEvent.VC_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK);
        keyCodeMap.put(NativeKeyEvent.VC_SHIFT, KeyEvent.VK_SHIFT);
        keyCodeMap.put(NativeKeyEvent.VC_CONTROL, KeyEvent.VK_CONTROL);
        keyCodeMap.put(NativeKeyEvent.VC_ALT, KeyEvent.VK_ALT);
        keyCodeMap.put(NativeKeyEvent.VC_META, KeyEvent.VK_WINDOWS);

        // Map các phím ký tự đặc biệt
        keyCodeMap.put(NativeKeyEvent.VC_MINUS, KeyEvent.VK_MINUS);
        keyCodeMap.put(NativeKeyEvent.VC_EQUALS, KeyEvent.VK_EQUALS);
        keyCodeMap.put(NativeKeyEvent.VC_OPEN_BRACKET, KeyEvent.VK_OPEN_BRACKET);
        keyCodeMap.put(NativeKeyEvent.VC_CLOSE_BRACKET, KeyEvent.VK_CLOSE_BRACKET);
        keyCodeMap.put(NativeKeyEvent.VC_BACK_SLASH, KeyEvent.VK_BACK_SLASH);
        keyCodeMap.put(NativeKeyEvent.VC_SEMICOLON, KeyEvent.VK_SEMICOLON);
        keyCodeMap.put(NativeKeyEvent.VC_QUOTE, KeyEvent.VK_QUOTE);
        keyCodeMap.put(NativeKeyEvent.VC_COMMA, KeyEvent.VK_COMMA);
        keyCodeMap.put(NativeKeyEvent.VC_PERIOD, KeyEvent.VK_PERIOD);
        keyCodeMap.put(NativeKeyEvent.VC_SLASH, KeyEvent.VK_SLASH);

        // Map các phím khác
        keyCodeMap.put(NativeKeyEvent.VC_DELETE, KeyEvent.VK_DELETE);
        keyCodeMap.put(NativeKeyEvent.VC_INSERT, KeyEvent.VK_INSERT);
        keyCodeMap.put(NativeKeyEvent.VC_HOME, KeyEvent.VK_HOME);
        keyCodeMap.put(NativeKeyEvent.VC_END, KeyEvent.VK_END);
        keyCodeMap.put(NativeKeyEvent.VC_PAGE_UP, KeyEvent.VK_PAGE_UP);
        keyCodeMap.put(NativeKeyEvent.VC_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        keyCodeMap.put(NativeKeyEvent.VC_PRINTSCREEN, KeyEvent.VK_PRINTSCREEN);
        keyCodeMap.put(NativeKeyEvent.VC_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
        keyCodeMap.put(NativeKeyEvent.VC_PAUSE, KeyEvent.VK_PAUSE);
        keyCodeMap.put(NativeKeyEvent.VC_NUM_LOCK, KeyEvent.VK_NUM_LOCK);
    }
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
                    case NativeKeyEvent.NATIVE_KEY_PRESSED:
                        int keyCodePress = getKeyEventCode(reader.readInt());
                        if (keyCodePress == KeyEvent.VK_UNDEFINED) {
                            continue;
                        }
                        r.keyPress(keyCodePress);
                        System.out.println("Key Pressed");
                        break;
                    case NativeKeyEvent.NATIVE_KEY_RELEASED:
                        int keyCodeRelease = getKeyEventCode(reader.readInt());
                        if (keyCodeRelease == KeyEvent.VK_UNDEFINED) {
                            continue;
                        }
                        r.keyRelease(keyCodeRelease);
                        System.out.println("Key Released");
                        break;
                    case MouseWheelEvent.MOUSE_WHEEL:
                        int wheelRotation = reader.readInt();
                        r.mouseWheel(wheelRotation);
                        break;
                    default:
                        break;
                }
            } catch (IOException ex) {
                System.out.println("Exception in receive events:" + ex);
                isRuning = false;
            }
        }
    }
    
    public static int getKeyEventCode(int nativeKeyCode) {
        return keyCodeMap.getOrDefault(nativeKeyCode, KeyEvent.VK_UNDEFINED);
    }
}
