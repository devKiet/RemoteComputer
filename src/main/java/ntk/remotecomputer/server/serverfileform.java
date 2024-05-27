/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ntk.remotecomputer.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author kiet
 */
public class serverfileform extends javax.swing.JFrame {
    private File dstFile = null;
    private static ObjectOutputStream outputStream = null;
    private static ObjectInputStream inputStream = null;
    private FileEvent fileEvent = null;
    private static String fname = null;
    private FileOutputStream fileOutputStream = null;
    private static String destinationPath1 = "";
    private Thread fileThread = null;
    
    /**
     * Creates new form serverfileform
     */
    public serverfileform() {
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/icons8-remote-desktop-64.png"));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null); 

        destinationPath1 = System.getProperty("user.dir");   
        jTextField1.setText(destinationPath1);
        T1 t1 = new T1();
        fileThread = new Thread(t1);
        fileThread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(700, 300));
        getContentPane().setLayout(null);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setText("Select the floder for the client to upload the file");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(140, 70, 460, 22);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/icons8-opened-folder-50.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel3MousePressed(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(600, 120, 50, 50);

        jTextField1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        getContentPane().add(jTextField1);
        jTextField1.setBounds(70, 120, 520, 50);

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField2);
        jTextField2.setBounds(70, 120, 586, 50);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/background.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 1920, 1079);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    class T1 implements Runnable {
        
        @Override
        public void run() {
            ServerSocket sersock = null;

            try {
                sersock = new ServerSocket(1234); 
            }   catch (IOException ex) {
                Logger.getLogger(serverfileform.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (true) {
                try {   
                    System.out.println("Server ready ");
                    Socket sock = sersock.accept();
                   
                    /* Define input stream and read line from client */
                    InputStream istream = sock.getInputStream();
                    BufferedReader fileRead = new BufferedReader(new InputStreamReader(istream));
                    fname = fileRead.readLine();

                    if (fname.contains("download")) {

                        System.out.println(removeWord(fname, "download"));
                        fname = removeWord(fname, "download");
                        outputStream = new ObjectOutputStream(sock.getOutputStream());

                        sendFile();
                        istream.close();
                        fileRead.close();
                    } else {
                        inputStream = new ObjectInputStream(sock.getInputStream());
                        downloadFile();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(serverfileform.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static String removeWord(String string, String word) {
        if (string.contains(word)) {
            String tempWord = word + " ";
            string = string.replaceAll(tempWord, "");
            tempWord = " " + word;
            string = string.replaceAll(tempWord, "");
        }
        return string;
    }

    public void sendFile() throws IOException {
        fileEvent = new FileEvent();
        String fileName = fname.substring(fname.lastIndexOf("/") + 1, fname.length());
        String path = fname.substring(0, fname.lastIndexOf("/") + 1);
        fileEvent.setDestinationDirectory(destinationPath1);
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
            } catch (IOException e) {
                fileEvent.setStatus("Error");
            }
        } else {
            JOptionPane.showMessageDialog(null, "path specified is not pointing to a file");
            fileEvent.setStatus("Error");
        }

        try {
            outputStream.writeObject(fileEvent);
            JOptionPane.showMessageDialog(null, "Done...Going to exit");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public void downloadFile() {
        try {
            fileEvent = (FileEvent) inputStream.readObject();
            if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                JOptionPane.showMessageDialog(null, "Error occurred ..So exiting");
                return;
            }
            
            String outputFile = destinationPath1 + "\\" + fileEvent.getFilename();
            if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                new File(fileEvent.getDestinationDirectory()).mkdirs();
            }

            dstFile = new File(outputFile);
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            JOptionPane.showMessageDialog(null, "Output file : " + outputFile + " is successfully saved ");
            Thread.sleep(1000);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
        }

    }
    
    private void jLabel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MousePressed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            jTextField1.setText(selectedDirectory.getAbsolutePath());
            destinationPath1 = jTextField1.getText();
        }
    }//GEN-LAST:event_jLabel3MousePressed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(serverfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(serverfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(serverfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(serverfileform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new serverfileform().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
