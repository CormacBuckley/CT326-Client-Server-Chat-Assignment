import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

//15534413 Cormac Buckley

public class Client extends JFrame implements ActionListener {

	private JButton login, post, color, settings, image, upload;
	private Container container;
	private JTextField input, user;
	private JTextArea log;
	private String username;

	PrintWriter outToServer;
	BufferedReader inFromServer;
	String sentence;
	String chatLine;
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

	String fileLocation;

	JFileChooser fc = new JFileChooser();
	private final int port = 1234;

	private static final Dimension WindowSize = new Dimension(550, 800);

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
		Client client = new Client();
		client.run();
	}

	public Client() throws InterruptedException, UnknownHostException, IOException {

		super("Client Window");

		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int x = screensize.width / 2 - WindowSize.width / 2;
		int y = screensize.height / 2 - WindowSize.height / 2;
		setBounds(x, y, WindowSize.width, WindowSize.height);
		setVisible(true);

		GridBagLayout layout = new GridBagLayout();
		// setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		container = getContentPane();
		container.setLayout(layout);
		container.setBackground(Color.DARK_GRAY);

		// The CenterPanel which is the chat room
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 150, 5); // Box Spacing from wall
		c.weightx = 1; // Distribution of x and y start points
		c.weighty = 1;
		log = new JTextArea("Welcome to the Chat\n");
		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.add(new JScrollPane(log));
		log.setEditable(false);
		add(centerPanel, c);

		// c.gridy = 2;
		// c.weighty = 0;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 22, 5);

		JPanel options = new JPanel(new GridLayout());
		settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setEnabled(false);

		image = new JButton("Image");
		image.addActionListener(this);
		image.setEnabled(false);

		upload = new JButton("Upload");
		upload.addActionListener(this);
		upload.setEnabled(false);

		options.add(settings);
		options.add(image);
		options.add(upload);
		add(options, c);

		// c.gridy = 3;
		// c.weighty = 0;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 60, 5);

		JPanel southPanel = new JPanel();

		BufferedImage myPicture = ImageIO.read(new File("place.png"));
		JLabel pic = new JLabel(new ImageIcon(myPicture));
		pic.setSize(20, 20);
		login = new JButton("Login");
		login.addActionListener(this);
		user = new JTextField("", 10);

		input = new JTextField("", 10);
		post = new JButton("Post");
		post.addActionListener(this);
		post.setEnabled(false);

		color = new JButton("Change Colour");
		color.addActionListener(this);

		southPanel.add(pic);
		southPanel.add(new JLabel("Login: "));
		southPanel.add(user);
		southPanel.add(login);
		southPanel.add(new JLabel("Post: "));
		southPanel.add(input);
		southPanel.add(post);

		add(southPanel, c);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == login) {
			username = user.getText();
			user.setEditable(false);
			login.setEnabled(false);
			post.setEnabled(true);
			settings.setEnabled(true);
			image.setEnabled(true);
			upload.setEnabled(true);
			container.remove(user);
			container.remove(login);

		}
		if (o == post) {
			chatLine = input.getText();
			input.setText("");
			outToServer.println(username + ": " + chatLine);
		}

		if (o == settings) {
			try {
				final JColorChooser colour = new JColorChooser();
				class OkListener implements ActionListener {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						Color c = colour.getColor();
						container.setBackground(c);
					}

				}
				JDialog co = JColorChooser.createDialog(null, "Pick a colour", false, colour, new OkListener(), null);
				colour.setColor(new Color(154, 51, 52));
				co.setVisible(true);
			} catch (Exception ex) {

			}
		}

		if (o == image) {
			try {
				String dir = System.getProperty("user.home");
				fc.setCurrentDirectory(new File(dir));
				fc.setDialogTitle("Choose File");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				if (fc.showOpenDialog(image) == JFileChooser.APPROVE_OPTION) {

				}

				fileLocation = fc.getSelectedFile().getAbsolutePath();
			} catch (Exception ex) {
				System.out.println("Error Selecting image file");
			}
		}

		// Commented out code is my attempt to restrict image upload code to only fire
		// when a user wants to upload.
		// Unfortunately I could not make this work as it kept throwing null pointer
		// errors.

		if (o == upload) {
			try {
				// Server server = Server.getServ();
				// server.toggleImages();
				Socket s = new Socket("localhost", 4567);
				OutputStream os = s.getOutputStream();
				InputStream is = new FileInputStream(new File(fileLocation));
				BufferedImage image = ImageIO.read(is);

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", byteArrayOutputStream);

				byte[] size = ByteBuffer.allocate(12000).putInt(byteArrayOutputStream.size()).array(); // 12 KB buffer

				os.write(size);
				os.write(byteArrayOutputStream.toByteArray());
				os.flush();
				// server.toggleImages();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Failed to send image file");
			}
		}
	}

	public void run() throws UnknownHostException, IOException {
		try {
			System.out.println("Client Running");
			Socket socket = new Socket("localhost", port);

			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToServer = new PrintWriter(socket.getOutputStream(), true);

			outToServer.println(socket.getRemoteSocketAddress() + " Connected to the chat \n");
			String chat = "";

			while (true) {
				chat = inFromServer.readLine();
				log.append(chat + "\n");
			}
		} catch (Exception ex) {

			System.out.println("Server Offline. Client Shutting Down");
			System.exit(ERROR);

		}
	}

}