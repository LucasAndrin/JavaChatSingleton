import java.io.IOException;
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

        String msg;

        Socket client = null;
        PrintStream output;

        try {
            client = new Socket("localhost", 7000);
            output = new PrintStream(client.getOutputStream());

            output.println(nickname);

            do {
                msg = input.nextLine();
                output.println(msg);
            } while (!"exit".equals(msg));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert client != null;
            client.close();
            input.close();
        }
    }

}
