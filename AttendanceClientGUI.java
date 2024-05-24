import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class AttendanceClientGUI extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextArea outputArea;
    private JButton checkInButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public AttendanceClientGUI() {
        setTitle("Attendance System (Client)");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        nameField = new JTextField();
        panel.add(nameField);


        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane);

        checkInButton = new JButton("Check In");
        checkInButton.addActionListener(this);
        checkInButton.setPreferredSize(new Dimension(400, 30));
        panel.add(checkInButton);

        add(panel);

        setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 3000 );
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkInButton) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                out.println(name);
                try {
                    String response = in.readLine();
                    outputArea.append(response + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AttendanceClientGUI::new);
    }
}
