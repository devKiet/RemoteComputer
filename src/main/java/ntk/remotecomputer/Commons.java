/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ntk.remotecomputer;

/**
 *
 * @author kiet
 */
public final class Commons {
    private Commons() {}
        
    // Server constants
    public static final String SERVER_NAME = "localhost";
    public static final int SCREEN_SOCKET_PORT = 8080;
    public static final int EVENT_SOCKET_PORT = 8081;
    public static final int CHAT_SOCKET_PORT = 8082;
    public static final int FILE_SOCKET_PORT = 8083;
    public static final int TRACKING_SOCKET_PORT = 8084;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

    public static final int LOGIN_SOCKET_PORT = 8888;

    // Image constants
    public static final int IMAGE_WIDTH = 800;
    public static final int IMAGE_HEIGHT = 600;

    // Other constants
    public static final int SLEEP_TIME = 5;
    public static final String ICON_IMG_PATH = "/ntk/remotecomputer/res/icons8-remote-desktop-96.png";
    
}
