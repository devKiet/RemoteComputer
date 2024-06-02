
package ntk.remotecomputer.client;

import ntk.remotecomputer.server.FileEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import ntk.remotecomputer.Commons;

public class uploadfileform extends javax.swing.JFrame {
    static String ip = "";
   
    public uploadfileform(String ip) {
        this.ip = ip;
        setSize(700, 400);
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });
        getContentPane().add(jFileChooser1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private static ObjectOutputStream outputStream = null;
    private FileEvent fileEvent = null;
    private static String fname = null;
    private String destinationPath = "C:/User";

	/* send file from client to server */
    public void sendFile() {
        fileEvent = new FileEvent();
        String fileName = fname.substring(fname.lastIndexOf("/") + 1, fname.length());
        String path = fname.substring(0, fname.lastIndexOf("/") + 1);
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(fname);
        File file = new File(fname);
        if (file.isFile()) {
            try {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead = 0;
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
                    read = read + numRead;
                }
                fileEvent.setFileSize(len);
                fileEvent.setFileData(fileBytes);
                fileEvent.setStatus("Success");
            } catch (Exception e) {
                e.printStackTrace();
                fileEvent.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
            fileEvent.setStatus("Error");
        }

        try {
            outputStream.writeObject(fileEvent);
            JOptionPane.showMessageDialog(this, "File uploaded successfully!");
            System.out.println("Done...Going to exit");
            Thread.sleep(1000);
            outputStream.close();
            
            this.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e);
        } catch (IOException ex) {
            Logger.getLogger(uploadfileform.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex);
        }

    }

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        try {        
            if (JFileChooser.APPROVE_SELECTION.equals(evt.getActionCommand())) {
                String serverName = ip; //loop back ip   
                Socket sock = new Socket(serverName, Commons.FILE_SOCKET_PORT);
                Scanner input = new Scanner(System.in);
                System.out.print("Enter the file name : ");
                String keyRead = jFileChooser1.getSelectedFile().getAbsolutePath();
                System.out.println("Old path: " + keyRead);
                keyRead = keyRead.replaceAll("\\\\", "/");

                System.out.println("Path: " + keyRead);

                fname = keyRead;
                OutputStream ostream = null;
                ostream = sock.getOutputStream();
                PrintWriter pwrite = new PrintWriter(ostream, true);
                pwrite.println("");
                outputStream = new ObjectOutputStream(sock.getOutputStream());
                this.sendFile();
                sock.close();
            } else if (JFileChooser.CANCEL_SELECTION.equals(evt.getActionCommand())) {
                this.dispose();
            }       
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage());
            });
        }
    }//GEN-LAST:event_jFileChooser1ActionPerformed

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
            java.util.logging.Logger.getLogger(uploadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(uploadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(uploadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(uploadfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new uploadfileform(ip).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    // End of variables declaration//GEN-END:variables
}
