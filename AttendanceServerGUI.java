import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AttendanceServerGUI extends JFrame {
    private JTextArea statusTextArea;

    public AttendanceServerGUI() {
        setTitle("Server Status");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        statusTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        add(scrollPane);

        setVisible(true);

        // Start the server
        startServer();
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(3000);
                publish("Server started. Listening on port 3000...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    // Create reader and writer for communication with client
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Read name sent by client
                    String name = in.readLine();

                    // Process name (e.g., save to database)
                    // For now, just print the received name
                    System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                    System.out.println("Received: " + name);

                    // Get current time
                    LocalDateTime currentTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedTime = currentTime.format(formatter);

                    // Send confirmation back to client with current time
                    out.println("Attendance recorded for: " + name + " at " + formattedTime);

                    // Close client socket
                    clientSocket.close();

                    // Notify GUI that name and time have been received
                    publish(name + "\t" + formattedTime);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    private void publish(String message) {
        SwingUtilities.invokeLater(() -> statusTextArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AttendanceServerGUI::new);
    }
}
