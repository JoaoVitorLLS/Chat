import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server extends Thread {

    private static ArrayList<BufferedWriter> clients;
    private static ServerSocket server;
    private String name;
    private Socket socket;
    private InputStream inputStream;
    private InputStreamReader inr;
    private BufferedReader bfr;

    public Server(Socket socket) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            inr = new InputStreamReader(inputStream);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            JLabel messageLabel = new JLabel("Server port");
            JTextField portText = new JTextField("12345");
            Object[] texts = { messageLabel, portText };
            JOptionPane.showMessageDialog(null, texts);
            server = new ServerSocket(Integer.parseInt(portText.getText()));
            clients = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null, "Server active in the following port: " + portText.getText());

            while (true) {
                System.out.println("Awaiting connection...");
                Socket socket = server.accept();
                System.out.println("Client connected!");
                Thread thread = new Server(socket);
                thread.start();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void run() {
        try {
            String message;
            OutputStream outputStream = this.socket.getOutputStream();
            Writer ouw = new OutputStreamWriter(outputStream);
            BufferedWriter bfw = new BufferedWriter(ouw);
            clients.add(bfw);
            name = message = bfr.readLine();

            while (!"Exit".equalsIgnoreCase(message) && message != null) {
                message = bfr.readLine();
                sendToAll(bfw, message);
                System.out.println(message);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendToAll(BufferedWriter bwSaida, String message) throws IOException {
        BufferedWriter bwS;
        for (BufferedWriter bw : clients) {
            bwS = (BufferedWriter) bw;
            if (!(bwSaida == bwS)) {
                bw.write(name + " -> " + message + "\r\n");
                bw.flush();
            }
        }
    }

}
