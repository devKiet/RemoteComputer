/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ntk.remotecomputer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ntk.remotecomputer.client.clientfirstpage;
import ntk.remotecomputer.server.Server;

/**
 *
 * @author kiet
 */
public class RemoteComputer extends JFrame{

    public RemoteComputer() {
        setTitle("Main Frame");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimensions = toolkit.getScreenSize();
        setSize(screenDimensions);        
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create buttons
        JButton button1 = new JButton("Open Client Frame");
        JButton button2 = new JButton("Open Server Frame");

        // Set layout
        setLayout(new GridLayout(2, 1));

        // Add buttons to frame
        add(button1);
        add(button2);

        // Add action listeners to buttons
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close current frame
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        clientfirstpage client = new clientfirstpage();
                        client.setVisible(true);
                    }
                });
            }
        });
        

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close current frame
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Server server = new Server();
                            server.start();
                        } catch (SQLException ex) {
                            Logger.getLogger(RemoteComputer.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(RemoteComputer.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(RemoteComputer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RemoteComputer().setVisible(true);
            }
        });
    }
}
