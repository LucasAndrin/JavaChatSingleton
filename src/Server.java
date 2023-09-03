import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static Server server;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    private Server() {
        try {
            serverSocket = new ServerSocket(7000);
            clients = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public void startListening() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;

        private PrintWriter output;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
            try {
                output = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String clientAddress = clientSocket.getInetAddress().getHostAddress();

                String clientNickname = reader.readLine();
                System.out.println(clientNickname + " (" + clientAddress + ") was connected! ");

                String message;
                while ((message = reader.readLine()) != null && !message.equals("exit")) {
                    System.out.println(clientNickname + ": " + message);

                    broadcastMessage(clientNickname + ": " + message, this);
                }

                System.out.println(clientNickname + " was desconnected! ");
                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            output.println(message);
        }
    }

    public static void main(String[] args) {
        Server server = Server.getInstance();
        server.startListening();
    }

}
