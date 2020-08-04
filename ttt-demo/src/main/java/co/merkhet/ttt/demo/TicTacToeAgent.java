package co.merkhet.ttt.demo;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringJoiner;
import org.deeplearning4j.rl4j.observation.Observation;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicTacToeAgent {

  private static Scanner in;
  private static int[][] board;
  private static int turn;
  private static String winner;
  private DQNPolicy<Observation> policy;
  private TTTObservationSpaceImpl observationSpace;

  public TicTacToeAgent() throws IOException {
    this.policy = DQNPolicy.load("E:\\Dev\\tmp\\ttt.policy");
    this.observationSpace = new TTTObservationSpaceImpl();
  }

  public int move(int[][] board) {
    return this.policy.nextAction(this.observationSpace.getObservation(board));
  }

  public static void main(String[] args) throws IOException {
    TicTacToeAgent ticTacToeAgent = new TicTacToeAgent();
    in = new Scanner(System.in);
    board = new int[3][3];
    turn = 1;

    log.info("Welcome to Tic Tac Toe.");
    printBoard();
    log.info("X's turn [1-9]: ");

    while (winner == null) {
      int numInput;
      try {
        if (turn == -1) {
          numInput = in.nextInt();
        } else {
          numInput = ticTacToeAgent.move(board);
          numInput += 1;
        }

        if (!(numInput > 0 && numInput <= 9)) {
          log.info("Invalid move. Try again [1-9]: ");
          continue;
        }
      } catch (InputMismatchException e) {
        log.info("Invalid move. Try again [1-9]: ");
        continue;
      }
      if (move(numInput)) {
        printBoard();
        turn = turn * -1;
        winner = gameOver();
      } else {
        continue;
      }
    }
    if (winner.equalsIgnoreCase("draw")) {
      log.info("It's a draw!");
    } else {
      log.info("Congratulations " + winner + " player you're the winner!.");
    }
  }

  private static String gameOver() {
    if (xWon()) {
      return "X";
    }
    if (oWon()) {
      return "O";
    }
    if (fullBoard()) {
      return "draw";
    }
    return null;
  }


  public static boolean xWon() {
    // horizontal
    if (board[0][0] + board[0][1] + board[0][2] == 3) {
      return true;
    }
    if (board[1][0] + board[1][1] + board[1][2] == 3) {
      return true;
    }
    if (board[2][0] + board[2][1] + board[2][2] == 3) {
      return true;
    }
    // vertical
    if (board[0][0] + board[1][0] + board[2][0] == 3) {
      return true;
    }
    if (board[0][1] + board[1][1] + board[2][1] == 3) {
      return true;
    }
    if (board[2][0] + board[2][1] + board[2][2] == 3) {
      return true;
    }
    // diagonals
    if (board[0][0] + board[1][1] + board[2][2] == 3) {
      return true;
    }
    if (board[0][2] + board[1][1] + board[2][0] == 3) {
      return true;
    }

    return false;
  }

  public static boolean oWon() {
    // horizontal
    if (board[0][0] + board[0][1] + board[0][2] == -3) {
      return true;
    }
    if (board[1][0] + board[1][1] + board[1][2] == -3) {
      return true;
    }
    if (board[2][0] + board[2][1] + board[2][2] == -3) {
      return true;
    }
    // vertical
    if (board[0][0] + board[1][0] + board[2][0] == -3) {
      return true;
    }
    if (board[0][1] + board[1][1] + board[2][1] == -3) {
      return true;
    }
    if (board[2][0] + board[2][1] + board[2][2] == -3) {
      return true;
    }
    // diagonals
    if (board[0][0] + board[1][1] + board[2][2] == -3) {
      return true;
    }
    if (board[0][2] + board[1][1] + board[2][0] == -3) {
      return true;
    }
    return false;
  }

  public static boolean fullBoard() {
    boolean full = true;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == 0) {
          return false;
        }
      }
    }
    return full;
  }

  private static boolean move(int numInput) {
    TTTMove move = TTTMove.getByMove(numInput);
    if (board[move.row][move.col] == 0) {
      board[move.row][move.col] = turn;
      return true;
    }
    log.info("Invalid move. Try again [1-9]: ");
    return false;
  }

  private static void printBoard() {
    log.info("\n------------ BOARD---------------\n");
    for (int i = 0; i < board.length; i++) {
      StringJoiner joiner = new StringJoiner("|");
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == 1) {
          joiner.add(" X ");
        } else if (board[i][j] == -1) {
          joiner.add(" O ");
        } else {
          joiner.add(" - ");
        }
      }
      log.info(joiner.toString());
    }
  }
}
