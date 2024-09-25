import java.net.*;

public class TicTacToeServer {
    private static final int PORT = 12345;
    private static DatagramSocket socket;
    private static byte[] buffer = new byte[1024];
    private static TicTacToe game;

    public static void main(String[] args) {
        try {
            socket = new DatagramSocket(PORT);
            System.out.println("Server listening...");

            game = new TicTacToe();

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.equals("exit")) {
                    break;
                }

                // Parse data: "row,col,player"
                String[] parts = received.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                char player = parts[2].charAt(0);

                if (game.makeMove(row, col, player)) {
                    // Check for winner after each move
                    String response = game.checkWinner();
                    if (response != null) {
                        InetAddress clientAddress = packet.getAddress();
                        int clientPort = packet.getPort();
                        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.length(), clientAddress, clientPort);
                        socket.send(responsePacket);
                        break; // End game if winner is determined
                    } else {
                        // Send board to client after each move
                        InetAddress clientAddress = packet.getAddress();
                        int clientPort = packet.getPort();
                        DatagramPacket responsePacket = new DatagramPacket(game.getBoard().getBytes(), game.getBoard().length(), clientAddress, clientPort);
                        socket.send(responsePacket);
                    }
                } else {
                    System.out.println("Invalid move! That spot is already taken.");
                }
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class TicTacToe {
    private char[][] board;
    private char currentPlayer;

    public TicTacToe() {
        board = new char[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                board[i][j] = ' ';
            }
        }
        currentPlayer = 'X';
    }

    public boolean makeMove(int row, int col, char player) {
        if (board[row][col] == ' ') {
            board[row][col] = player;
            currentPlayer = (player == 'X') ? 'O' : 'X';
            return true;
        } else {
            return false;
        }
    }

    public String checkWinner() {
        // Check horizontally
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] != ' ' && board[i][j] == board[i][j+1] && board[i][j] == board[i][j+2] && board[i][j] == board[i][j+3]) {
                    return "Winner: " + board[i][j];
                }
            }
        }

        // Check vertically
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < 4; i++) {
                if (board[i][j] != ' ' && board[i][j] == board[i+1][j] && board[i][j] == board[i+2][j] && board[i][j] == board[i+3][j]) {
                    return "Winner: " + board[i][j];
                }
            }
        }

        // Check diagonally (top-left to bottom-right)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] != ' ' && board[i][j] == board[i+1][j+1] && board[i][j] == board[i+2][j+2] && board[i][j] == board[i+3][j+3]) {
                    return "Winner: " + board[i][j];
                }
            }
        }

        // Check diagonally (top-right to bottom-left)
        for (int i = 0; i < 4; i++) {
            for (int j = 3; j < 7; j++) {
                if (board[i][j] != ' ' && board[i][j] == board[i+1][j-1] && board[i][j] == board[i+2][j-2] && board[i][j] == board[i+3][j-3]) {
                    return "Winner: " + board[i][j];
                }
            }
        }

        // Check for draw
        boolean draw = true;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (board[i][j] == ' ') {
                    draw = false;
                    break;
                }
            }
        }
        if (draw) {
            return "Draw";
        }

        // If no winner yet, return null indicating the game is still ongoing
        return null;
    }

    public String getBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                sb.append(board[i][j]);
                if (j < 6) {
                    sb.append("|");
                }
            }
            sb.append("\n");
            if (i < 6) {
                sb.append("-------------\n");
            }
        }
        return sb.toString();
    }
}
