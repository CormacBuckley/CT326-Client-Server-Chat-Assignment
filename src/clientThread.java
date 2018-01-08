import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

//15534413 Cormac Buckley
public class clientThread extends Thread {

	private Socket socket;

	private BufferedReader inFromClient;
	private PrintWriter outToServer;

	public clientThread(Socket sock) {
		super("ClientThread");
		this.socket = sock;
	}

	@Override
	public void run() {
		Server serv = Server.getServ();

		try {
			outToServer = new PrintWriter(socket.getOutputStream());
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			serv.newClient(outToServer);
			System.out.println("Client Thread Up");
			serv.print(inFromClient.readLine());
			String resp;
			while (true) {
				try {
					resp = inFromClient.readLine();

					for (int i = 0; i < serv.getClients().size(); i++) {
						serv.getClients().get(i).println(resp.toString() + "\n");
						serv.getClients().get(i).flush();
					}

					serv.print(resp.toString());
				} catch (Exception ex) {
					System.out.println("Client disconnected");
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (outToServer != null) {
				serv.removeClient(outToServer);
			}
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("System Crash");
			}
		}
	}
}
