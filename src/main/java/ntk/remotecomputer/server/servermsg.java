
package ntk.remotecomputer.server;

import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class servermsg extends javax.swing.JFrame {
    static ServerSocket ssckt;
    static Socket sckt;
    static DataInputStream dtinpt;
    static DataOutputStream dtotpt;

    public void sendmsg() {
        System.out.println("Send message method called Server side");

        String msg = "";
        try {
            ssckt = new ServerSocket(1201);
            sckt = ssckt.accept();

		/* Get message from input stream */
            dtinpt = new DataInputStream(sckt.getInputStream());

		/* Send message from output stream */
            dtotpt = new DataOutputStream(sckt.getOutputStream());

		/* conversation between client and server until bye */
            while (!msg.equals("bye")) {
                msg = dtinpt.readUTF();
                System.out.println("message received: " + msg);
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = now.format(formatter);
                jTextArea2.setText(jTextArea2.getText().trim() + "\n[" + formattedDateTime + "] Client: " + msg);
            }
        } catch (IOException ex) {
        }
    }

    public servermsg() {
        initComponents();
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Sever side");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(331, 29, 127, 30);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setText("Enter the message that you want to send");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(140, 380, 511, 30);

        jButton1.setBackground(new java.awt.Color(0, 102, 0));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(300, 590, 180, 36);

        jTextField1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        getContentPane().add(jTextField1);
        jTextField1.setBounds(67, 432, 655, 95);

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(67, 85, 655, 260);

        jLabel3.setBackground(new java.awt.Color(102, 255, 255));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/background.png"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 0, 800, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try {
            String msgout = "";
            msgout = jTextField1.getText().trim();
            dtotpt.writeUTF(msgout);
            System.out.println("Message sent: " + msgout);
            jTextField1.setText(null);
            
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            jTextArea2.setText(jTextArea2.getText().trim() + "\n[" + formattedDateTime + "] Server: " + msgout);            
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    static void display() {
        servermsg c = new servermsg();

        c.setPreferredSize(new Dimension(800,700));
        c.setResizable(false);

        c.setLocationRelativeTo(null);
        c.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

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
            java.util.logging.Logger.getLogger(servermsg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(servermsg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(servermsg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(servermsg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                display();
                new servermsg().setVisible(true);
            }
        });
     
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
