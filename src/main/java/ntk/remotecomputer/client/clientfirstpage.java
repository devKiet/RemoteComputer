
package ntk.remotecomputer.client;

import ntk.remotecomputer.server.Server;
import ntk.remotecomputer.server.serverfile;
import ntk.remotecomputer.server.servermsg;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class clientfirstpage extends javax.swing.JFrame {

    //Initializing JFrame
    public clientfirstpage() {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        ipAddress = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(428, 56, 72, 40);

        ipAddress.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        getContentPane().add(ipAddress);
        ipAddress.setBounds(44, 56, 366, 51);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setText("Enter the IP address to connect with");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(44, 125, 338, 22);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        jLabel2.setText("Your IP address");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(50, 240, 227, 34);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel3.setText("Your desk can be accessed by");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(50, 300, 280, 22);

        jTextField2.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        getContentPane().add(jTextField2);
        jTextField2.setBounds(50, 340, 366, 42);

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("File Transfer");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(520, 50, 220, 51);

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Show info process");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton3MouseReleased(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(750, 50, 220, 51);
        getContentPane().add(jLabel6);
        jLabel6.setBounds(0, 0, 1930, 1050);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        //Checking Null values of IP
        if (ipAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the IP address to get connected");
        } else {
            
            //Chat and Share Screen Window Initialization
            Client c = new Client(ipAddress.getText());
            clientmsg c1 = new clientmsg(ipAddress.getText());
            c1.setBounds(0, 0, 800, 700);
            c1.setResizable(false);
            c1.setVisible(true);
            
            
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        //Checking Null values of IP
        if (ipAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the IP address to get connected");
        } else {
            
            //File Transfer Initialization
            clientfileform c = new clientfileform(ipAddress.getText());
            c.setBounds(550, 150, 800, 700);
            c.setResizable(false);
            c.setVisible(true);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
        // TODO add your handling code here:
        if (ipAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the IP address to get connected");
        } else {
            
            //File Transfer Initialization
            SystemInfoForm systemInfoForm = new SystemInfoForm(ipAddress.getText());
            systemInfoForm.setBounds(550, 150, 600, 400);
            systemInfoForm.setResizable(false);
            systemInfoForm.setVisible(true);
        }
    }//GEN-LAST:event_jButton3MouseReleased

    public static void main(String args[]) throws UnknownHostException {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    
                    //Setting Host IP address in the frame
                    new clientfirstpage().setVisible(true);
                    
                    //Getting IP address of the host
                    InetAddress IP = InetAddress.getLocalHost();
                    System.out.println("My IP Address is:");
                    System.out.println(IP.getHostAddress());
                    String x = IP.getHostAddress();
                    jTextField2.setText(x);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(clientfirstpage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField ipAddress;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    public static javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
