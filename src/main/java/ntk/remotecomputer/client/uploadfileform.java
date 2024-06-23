
package ntk.remotecomputer.client;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import ntk.remotecomputer.server.FileEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import ntk.remotecomputer.Commons;

public class uploadfileform extends javax.swing.JFrame {
    static String ip = "";
    private static ObjectOutputStream outputStream = null;
    private FileEvent fileEvent = null;
    private static String fname = null;
    private String destinationPath = "C:/User";

    public uploadfileform(String ip) {
        this.ip = ip;
        setSize(700, 400);
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        jFileChooser1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        progressBar.setVisible(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                    null, "Are You Sure to Close this Application?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 500));

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });
        getContentPane().add(jFileChooser1, java.awt.BorderLayout.CENTER);
        getContentPane().add(progressBar, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
        /* send file from client to server */
    public void sendFile(Socket sock) {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                fileEvent = new FileEvent();
                File file = new File(fname);
                if (file.isDirectory()) {
                    fileEvent.setDirectory(true);
                    fileEvent.setFileList(getFileList(file));
                } else {
                    fileEvent.setDirectory(false);
                    fileEvent.setFileData(readFile(file));
                }
                fileEvent.setFilename(file.getName());
                fileEvent.setSourceDirectory(fname);
                fileEvent.setDestinationDirectory(destinationPath);

                try {
                    outputStream.writeObject(fileEvent);
                    progressBar.setVisible(false);
                    JOptionPane.showMessageDialog(uploadfileform.this, "File/Folder uploaded successfully!");
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.setVisible(false);
                    JOptionPane.showMessageDialog(uploadfileform.this, "Error: " + e);
                } finally {
                    try {
                        sock.close();
                        uploadfileform.this.dispose();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }

            @Override
            protected void done() {
                progressBar.setValue(100);
            }
        };
        worker.execute();
    }
    
    private byte[] readFile(File file) {
        try {
            DataInputStream diStream = new DataInputStream(new FileInputStream(file));
            long len = (int) file.length();
            byte[] fileBytes = new byte[(int) len];
            int read = 0;
            int numRead = 0;
            while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
                read = read + numRead;
                int progress = (int) ((read / (float) fileBytes.length) * 100);
                // Publish the progress
                progressBar.setValue(progress);
            }
            diStream.close();
            fileEvent.setFileSize(len);
            fileEvent.setFileData(fileBytes);
            fileEvent.setStatus("Success");
            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
            fileEvent.setStatus("Error");
            return null;
        }
    }

    private List<FileEvent> getFileList(File directory) {
        List<FileEvent> fileList = new ArrayList<>();
        for (File file : directory.listFiles()) {
            FileEvent event = new FileEvent();
            event.setFilename(file.getName());
            event.setSourceDirectory(file.getAbsolutePath());
            if (file.isDirectory()) {
                event.setDirectory(true);
                event.setFileList(getFileList(file));
            } else {
                event.setDirectory(false);
                event.setFileData(readFile(file));
            }
            fileList.add(event);
        }
        return fileList;
    }

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        try {        
            if (JFileChooser.APPROVE_SELECTION.equals(evt.getActionCommand())) {
                progressBar.setVisible(true);
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
                this.sendFile(sock);
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
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
