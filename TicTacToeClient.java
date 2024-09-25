import java.net.*;
import java.util.Scanner;

public class TicTacToeClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter row (0-6): ");
                int row = scanner.nextInt();
                System.out.print("Enter column (0-6): ");
                int col = scanner.nextInt();
                System.out.print("Enter player (X or O): ");
                char player = scanner.next().charAt(0);

                // Construct data: "row,col,player"
                String data = row + "," + col + "," + player;
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
                        InetAddress.getByName(SERVER_IP), PORT);
                socket.send(packet);

                byte[] buffer = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(responsePacket);
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println(response);

                if (response.equals("Winner: X") || response.equals("Winner: O") || response.equals("Draw")) {
                    break;
                }
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
