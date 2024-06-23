package ntk.remotecomputer.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import ntk.remotecomputer.Commons;

public class clientmsg extends javax.swing.JFrame {	
    static Socket sock;
    static DataInputStream dtinpt;
    static DataOutputStream dtotpt;
    static String ip = "";

    class ChatThread implements Runnable {
        @Override
        public void run() {
            sendmess();
            repaint();
            pack();
        }
    }
    
    public clientmsg(String ip) {
        clientmsg.ip = ip;
        ChatThread chat = new ChatThread();
        new Thread(chat).start();
        initComponents();
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(Commons.ICON_IMG_PATH));
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        
        jTextArea2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        // Shift + Enter for new line
                        jTextArea2.append("\n");
                    } else {
                        // Enter to send message
                        e.consume(); 
                        handleSendBtn();
                    }
                }
            }
        });
    }

    public void sendmess() {
        try {
            System.out.println("Send message method called Client side");
          
            String serverName = ip;             
            sock = new Socket(serverName, Commons.CHAT_SOCKET_PORT);
            dtinpt = new DataInputStream(sock.getInputStream());
            dtotpt = new DataOutputStream(sock.getOutputStream());

            while (true) {
                String msg = dtinpt.readUTF();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
                String formattedDateTime = now.format(formatter);
                jTextArea1.setText(jTextArea1.getText().trim() + "\n[" + formattedDateTime + "] Server: " + msg);
            }
        } catch (IOException ex) {
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 700));
        setSize(new java.awt.Dimension(800, 700));
        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Client side");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(320, 30, 133, 30);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(10);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(67, 79, 655, 460);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setText("Enter the message that you want to send");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(150, 560, 511, 30);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/icons8-send-48.png"))); // NOI18N
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        getContentPane().add(jLabel4);
        jLabel4.setBounds(672, 600, 48, 48);

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(1);
        jTextArea2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane3.setViewportView(jTextArea2);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(70, 600, 600, 48);

        jTextField2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(jTextField2);
        jTextField2.setBounds(70, 600, 655, 48);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ntk/remotecomputer/res/background.png"))); // NOI18N
        jLabel3.setSize(new java.awt.Dimension(1920, 1079));
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 0, 1920, 1079);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
        // TODO add your handling code here:
        handleSendBtn();
    }//GEN-LAST:event_jLabel4MousePressed
    
    private void handleSendBtn() {
        try {
            String msgout = jTextArea2.getText().trim();
            dtotpt.writeUTF(msgout);
            System.out.println("Message sent:" + msgout);
            jTextArea2.setText(null);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            jTextArea1.setText(jTextArea1.getText().trim() + "\n[" + formattedDateTime + "] You: " + msgout);
        } catch (IOException ex) {
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new clientmsg(ip).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
