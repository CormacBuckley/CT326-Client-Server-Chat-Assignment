import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

//15534413 Cormac Buckley

public class Server extends JFrame {

	private JButton login;
	private Container container;
	private JTextArea log, input;
	private final static int port = 1234;
	private static Server thisServer;
	private static boolean swap;

	String clientSentence;
	String response;
	Socket socket;

	protected ArrayList<PrintWriter> clients = new ArrayList<PrintWriter>();

	private static final Dimension WindowSize = new Dimension(500, 800);

	public static void main(String[] args) throws IOException {
		Server serv = new Server();
		ServerSocket server = new ServerSocket(port);
		ServerSocket imageServer = new ServerSocket(4567); // using a new server process to handle image requests
		swap = false;
		try {
			while (true) {
				new clientThread(server.accept()).start();

				// Multiple users can connect to the chat server
				// However, an image must be uploaded by an already connected user to allow the
				// next to post
				// I attempted to fix this issue with different forms of the below if statement,
				// but unfortunately
				// I could not find a solution that worked without throwing errors.

				// if(swap == true) {

				InputStream inputStream = imageServer.accept().getInputStream();

				byte[] sizeAr = new byte[12000];
				inputStream.read(sizeAr);
				int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

				byte[] imageAr = new byte[size];
				inputStream.read(imageAr);

				BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

				System.out.println(
						"Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());

				ImageIO.write(image, "jpg", new File("C:\\Users\\I342042\\Desktop\\image.jpg"));
				continue;
				// }else {

			}

		} catch (IOException /* | InterruptedException */ e) {
			System.out.println("Server Died");
		}

	}

	public Server() throws IOException {
		super("Server Window");

		System.out.println("Server Running");

		thisServer = this;

		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int x = screensize.width / 2 - WindowSize.width / 2;
		int y = screensize.height / 2 - WindowSize.height / 2;
		setBounds(x, y, WindowSize.width, WindowSize.height);
		setVisible(true);

		GridBagLayout layout = new GridBagLayout();
		// setLayout(layout);

		container = getContentPane();
		container.setLayout(layout);
		container.setBackground(Color.DARK_GRAY);

		GridBagConstraints c = new GridBagConstraints();

		// The CenterPanel which is the chat room
		log = new JTextArea("Server Running\n");
		Border border1 = BorderFactory.createLineBorder(Color.black);
		log.setBorder(BorderFactory.createCompoundBorder(border1, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 5, 30, 5); // Box Spacing from wall
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; // Distribution of x and y start points
		c.weighty = 1;
		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.add(new JScrollPane(log));

		log.setEditable(false);
		add(centerPanel, c);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}

	public static Server getServ() {
		return thisServer;
	}

	public ArrayList<PrintWriter> getClients() {
		return clients;
	}

	public void print(String output) {
		log.append(output + "\n");
		// System.out.println("Trying to append " + output + " to server window");
	}

	public void removeClient(PrintWriter outToServer) {
		clients.remove(outToServer);
	}

	public void newClient(PrintWriter outToServer) {
		clients.add(outToServer);
	}

	/*
	 * public void toggleImages() { if(swap == false) { swap = true; }else { swap =
	 * false; } }
	 */

}