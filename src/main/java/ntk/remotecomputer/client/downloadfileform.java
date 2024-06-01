
package ntk.remotecomputer.client;

import ntk.remotecomputer.server.FileEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import ntk.remotecomputer.Commons;

public class downloadfileform extends javax.swing.JFrame {
    static String ip = "";
 
    public downloadfileform(String ip) {
        this.ip = ip;
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Download File");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(266, 41, 176, 30);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setText("Enter the file path to be downloaded");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(192, 100, 342, 22);

        jTextField1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        getContentPane().add(jTextField1);
        jTextField1.setBounds(53, 142, 586, 40);

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Download");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(267, 206, 171, 28);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/background.png"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 0, 700, 300);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private static ObjectInputStream inputStream = null;
    private FileEvent fileEvent = null;
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;
   
	/* Function for downloading file */
    public void downloadFile() {
        try {
            fileEvent = (FileEvent) inputStream.readObject();
            if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                System.out.println("Error occurred ..So exiting");
                System.exit(0);
            }

            // Tạo JFileChooser để người dùng chọn thư mục đích
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = fileChooser.getSelectedFile();
                String outputFile = selectedDirectory.getAbsolutePath() + File.separator + fileEvent.getFilename();

                dstFile = new File(outputFile);

                fileOutputStream = new FileOutputStream(dstFile);
                fileOutputStream.write(fileEvent.getFileData());
                fileOutputStream.flush();
                fileOutputStream.close();

                System.out.println("Output file : " + outputFile + " is successfully saved ");
                JOptionPane.showMessageDialog(null, "File downloaded successfully!");
                Thread.sleep(3000);
                this.dispose();
            } else {
                System.out.println("No directory selected. Exiting...");
                JOptionPane.showMessageDialog(null, "No directory selected. Exiting...");
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e);
        }
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
           
            String serverName = ip; //loop back ip      
            Socket sock = new Socket(serverName, Commons.FILE_SOCKET_PORT);
            this.setBounds(550, 150, 700, 300);
            this.setResizable(false);

            String keyRead = jTextField1.getText();
            String sourceFilePath2 = keyRead;
            System.out.println("Path is: " + sourceFilePath2);
          
	    OutputStream ostream = null;
            ostream = sock.getOutputStream();
            PrintWriter pwrite = new PrintWriter(ostream, true);
            pwrite.println(sourceFilePath2 + " download");
            inputStream = new ObjectInputStream(sock.getInputStream());
           
            this.downloadFile();
            sock.close();
            ostream.close();
            pwrite.close();
        } catch (IOException ex) {
            Logger.getLogger(downloadfileform.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(downloadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(downloadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(downloadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(downloadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new downloadfileform(ip).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
