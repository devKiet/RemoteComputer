package ntk.remotecomputer.server;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class serverfile {
    private File dstFile = null;
    private static ObjectOutputStream outputStream = null;
    private static ObjectInputStream inputStream = null;
    private FileEvent fileEvent = null;
    private static String fname = null;
    private FileOutputStream fileOutputStream = null;
    private String destinationPath1 = "C:/User";
    private Thread fileThread = null;
    
    public serverfile() {
        T1 t1 = new T1();
        fileThread = new Thread(t1);
        fileThread.start();
    }
    
    class T1 implements Runnable {
        @Override
        public void run() {
            while (true) {
                ServerSocket sersock;
                try {
                    sersock = new ServerSocket(1234);
                    System.out.println("Server ready ");
                    JOptionPane.showMessageDialog(null, "Connection is successful, Wating Client upload or download !!!");
                    Socket sock = sersock.accept();
                   
                    serverfile o1 = new serverfile();

                    /* Define input stream and read line from client */
                    InputStream istream = sock.getInputStream();
                    BufferedReader fileRead = new BufferedReader(new InputStreamReader(istream));
                    fname = fileRead.readLine();

                    if (fname.contains("download")) {

                        System.out.println(removeWord(fname, "download"));
                        fname = removeWord(fname, "download");
                        outputStream = new ObjectOutputStream(sock.getOutputStream());

                        o1.sendFile();
                        istream.close();
                        fileRead.close();
                    } else {
                        inputStream = new ObjectInputStream(sock.getInputStream());
                        o1.downloadFile();
                    }
                    
                    sock.close();
                    sersock.close();
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(serverfile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void main(String args[]) throws Exception {
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
            } catch (Exception e) {
                e.printStackTrace();
                fileEvent.setStatus("Error");
            }
        } else {
            JOptionPane.showMessageDialog(null, "path specified is not pointing to a file");
            fileEvent.setStatus("Error");
        }

        try {
            outputStream.writeObject(fileEvent);
            JOptionPane.showMessageDialog(null, "Done...Going to exit");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void downloadFile() {
        try {
            fileEvent = (FileEvent) inputStream.readObject();
            if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                JOptionPane.showMessageDialog(null, "Error occurred ..So exiting");
                System.exit(0);
            }
            
            String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
            if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                new File(fileEvent.getDestinationDirectory()).mkdirs();
            }

            dstFile = new File(outputFile);
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            JOptionPane.showMessageDialog(null, "Output file : " + outputFile + " is successfully saved ");
            Thread.sleep(3000);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
