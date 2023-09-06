import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        Scanner input = new Scanner(System.in);

        String nickname;
        do {
            System.out.print("Enter your nickname: ");
            nickname = input.nextLine();
        } while (nickname == null || nickname.isEmpty());

        Socket client = null;
        PrintStream output = null;

        try {
            client = new Socket("localhost", 7000);
            output = new PrintStream(client.getOutputStream());

            // Enviar o nickname para o servidor
            output.println(nickname);

            // Iniciar a thread para receber mensagens do servidor
            Thread receiveThread = new Thread(new ClientReceiveRunnable(client));
            receiveThread.start();

            String msg;
            do {
                msg = input.nextLine();
                output.println(msg);
            } while (!"exit".equals(msg));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                output.close();
            }
            if (client != null) {
                client.close();
            }
            input.close();
        }
    }

    private static class ClientReceiveRunnable implements Runnable {
        private Socket clientSocket;

        public ClientReceiveRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
