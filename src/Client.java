import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private JTextArea text;
    private JTextField messageText;
    private JButton sendButton;
    private JButton exitButton;
    private JLabel lblHistorico;
    private JLabel messageLabel;
    private JPanel pnlContent;
    private Socket socket;
    private OutputStream ou;
    private Writer writer;
    private BufferedWriter bfw;
    private JTextField txtIP;
    private JTextField portText;
    private JTextField nameText;

    public Client() throws IOException {
        JLabel lblMessage = new JLabel("Verify!");
        txtIP = new JTextField("127.0.0.1");
        portText = new JTextField("12345");
        nameText = new JTextField("Client");
        Object[] texts = { lblMessage, txtIP, portText, nameText };
        JOptionPane.showMessageDialog(null, texts);
        pnlContent = new JPanel();
        text = new JTextArea(10, 20);
        text.setEditable(false);
        text.setBackground(new Color(240, 240, 240));
        messageText = new JTextField(20);
        lblHistorico = new JLabel("History");
        messageLabel = new JLabel("Message");
        sendButton = new JButton("Send");
        sendButton.setToolTipText("Send message");
        exitButton = new JButton("Exit");
        exitButton.setToolTipText("Exit chat");
        sendButton.addActionListener(this);
        exitButton.addActionListener(this);
        sendButton.addKeyListener(this);
        messageText.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(text);
        text.setLineWrap(true);
        pnlContent.add(lblHistorico);
        pnlContent.add(scroll);
        pnlContent.add(messageLabel);
        pnlContent.add(messageText);
        pnlContent.add(exitButton);
        pnlContent.add(sendButton);
        pnlContent.setBackground(Color.LIGHT_GRAY);
        text.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        messageText.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        setTitle(nameText.getText());
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] arguments) throws IOException {
        Client client = new Client();
        client.connect();
        client.listen();
    }

    public void connect() throws IOException {
        socket = new Socket(txtIP.getText(), Integer.parseInt(portText.getText()));
        ou = socket.getOutputStream();
        writer = new OutputStreamWriter(ou);
        bfw = new BufferedWriter(writer);
        bfw.write(nameText.getText() + "\r\n");
        bfw.flush();
    }

    public void sendMessage(String msg) throws IOException {
        if (msg.equals("Exit")) {
            bfw.write("Disconnected \r\n");
            text.append("Disconnected \r\n");
        } else {
            bfw.write(msg + "\r\n");
            text.append(nameText.getText() + ": " + messageText.getText() + "\r\n");
        }
        bfw.flush();
        messageText.setText("");
    }

    public void listen() throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while (!"Exit".equalsIgnoreCase(msg))
            if (bfr.ready()) {
                msg = bfr.readLine();
                if (msg.equals("Exit"))
                    text.append("Connection failed! \r\n");
                else
                    text.append(msg + "\r\n");
            }
    }

    public void sair() throws IOException {
        sendMessage("Exit");
        bfw.close();
        writer.close();
        ou.close();
        socket.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals(sendButton.getActionCommand()))
                sendMessage(messageText.getText());
            else if (e.getActionCommand().equals(exitButton.getActionCommand()))
                sair();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                sendMessage(messageText.getText());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

}
