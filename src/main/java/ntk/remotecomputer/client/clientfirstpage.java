
package ntk.remotecomputer.client;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class clientfirstpage extends javax.swing.JFrame {

    //Initializing JFrame
    public clientfirstpage(String serverIp) {
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        String ip = getWifiIPAddress();
        jTextField2.setText(ip);
        ipAddress.setText(serverIp);
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
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(850, 550));
        setPreferredSize(new java.awt.Dimension(850, 550));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/icons8-remote-desktop-48.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 100, 48, 48));

        ipAddress.setEditable(false);
        ipAddress.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        getContentPane().add(ipAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, 360, 40));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setText("This is the server IP address");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 338, -1));

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        jLabel2.setText("Your IP address");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, -1, -1));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel3.setText("Your desk can be accessed by");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, -1, -1));

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 360, 40));

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("File Transfer");
        jButton2.setMaximumSize(new java.awt.Dimension(180, 30));
        jButton2.setMinimumSize(new java.awt.Dimension(180, 30));
        jButton2.setPreferredSize(new java.awt.Dimension(180, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 260, 200, 40));

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Mesage");
        jButton4.setMaximumSize(new java.awt.Dimension(180, 30));
        jButton4.setMinimumSize(new java.awt.Dimension(180, 30));
        jButton4.setPreferredSize(new java.awt.Dimension(180, 30));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 180, 200, 40));

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Show info process");
        jButton3.setPreferredSize(new java.awt.Dimension(180, 30));
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
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 340, 200, 40));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Welcome to Remote Computer");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 620, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/background.png"))); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1930, 1050));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        //Checking Null values of IP
        if (ipAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the IP address to get connected");
        } else {
            //Chat and Share Screen Window Initialization
            Client c = new Client(ipAddress.getText());       
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

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        clientmsg c1 = new clientmsg(ipAddress.getText());
        c1.setBounds(0, 0, 800, 700);
        c1.setResizable(false);
        c1.setVisible(true);   
    }//GEN-LAST:event_jButton4ActionPerformed
    
    public static String getWifiIPAddress() {
        try {
            // Liệt kê tất cả các giao diện mạng
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                
                // Kiểm tra nếu giao diện đang hoạt động và không phải là loopback
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    // Kiểm tra tên giao diện để xác định WiFi adapter
                    if (networkInterface.getName().contains("wlan") || networkInterface.getDisplayName().toLowerCase().contains("wi-fi")) {
                        // Lấy danh sách các địa chỉ IP của giao diện
                        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                        
                        while (inetAddresses.hasMoreElements()) {
                            InetAddress inetAddress = inetAddresses.nextElement();
                            // Lấy địa chỉ IPv4
                            if (inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Not connected";
    }
    
    public static void main(String args[]) throws UnknownHostException {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new clientfirstpage("localhost").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField ipAddress;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    public static javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
