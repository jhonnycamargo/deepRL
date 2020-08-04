package co.merkhet.ttt.demo;

import java.util.Random;
import lombok.Getter;
import lombok.Setter;

public class TTTBoard {

  @Getter
  @Setter
  private int[][] board = new int[3][3];

  private Random r = new Random();

  public boolean play(String move) {
    // this could be improved using enumerations.
    if (invalidMove(move)) {
      return false;
    }
    if (move.equals(TTTActionSpace.NORTH_WEST)) {
      this.board[0][0] = 1;
    } else if (move.equals(TTTActionSpace.NORTH)) {
      this.board[0][1] = 1;
    } else if (move.equals(TTTActionSpace.NORTH_EST)) {
      this.board[0][2] = 1;
    } else if (move.equals(TTTActionSpace.WEST)) {
      this.board[1][0] = 1;
    } else if (move.equals(TTTActionSpace.CENTER)) {
      this.board[1][1] = 1;
    } else if (move.equals(TTTActionSpace.EST)) {
      this.board[1][2] = 1;
    } else if (move.equals(TTTActionSpace.SOUTH_WEST)) {
      this.board[2][0] = 1;
    } else if (move.equals(TTTActionSpace.SOUTH)) {
      this.board[2][1] = 1;
    } else if (move.equals(TTTActionSpace.SOUTH_EST)) {
      this.board[2][2] = 1;
    }
    opponentMove();
    return true;
  }

  private static final boolean randomOpponent = true;

  private void opponentMove() {
    if (randomOpponent) {
      randomOpponentMove();
    } else {
      opponent();
    }
  }

  private void opponent() {
    if (!fullBoard() && !(won() || lost())) {
      if (this.board[1][1] == 0) {
        this.board[1][1] = -1;
      } else if (this.board[0][0] == 0) {
        this.board[0][0] = -1;
      } else if (this.board[0][2] == 0) {
        this.board[0][2] = -1;
      } else if (this.board[2][0] == 0) {
        this.board[2][0] = -1;
      } else if (this.board[2][2] == 0) {
        this.board[2][2] = -1;
      } else if (this.board[0][1] == 0) {
        this.board[0][1] = -1;
      } else if (this.board[1][0] == 0) {
        this.board[1][0] = -1;
      } else if (this.board[1][2] == 0) {
        this.board[1][2] = -1;
      } else if (this.board[2][1] == 0) {
        this.board[2][1] = -1;
      }
    }
  }

  private void randomOpponentMove() {
    if (!fullBoard() && !(won() || lost())) {
      boolean moved = false;
      while (!moved) {
        int row = r.ints(0, 3).findFirst().getAsInt();
        int col = r.ints(0, 3).findFirst().getAsInt();
        if (this.board[row][col] == 0) {
          this.board[row][col] = -1;
          moved = true;
        }
      }
    }
  }

  public boolean won() {
    // horizontal
    if (this.board[0][0] + this.board[0][1] + this.board[0][2] == 3) {
      return true;
    }
    if (this.board[1][0] + this.board[1][1] + this.board[1][2] == 3) {
      return true;
    }
    if (this.board[2][0] + this.board[2][1] + this.board[2][2] == 3) {
      return true;
    }
    // vertical
    if (this.board[0][0] + this.board[1][0] + this.board[2][0] == 3) {
      return true;
    }
    if (this.board[0][1] + this.board[1][1] + this.board[2][1] == 3) {
      return true;
    }
    if (this.board[2][0] + this.board[2][1] + this.board[2][2] == 3) {
      return true;
    }
    // diagonals
    if (this.board[0][0] + this.board[1][1] + this.board[2][2] == 3) {
      return true;
    }
    if (this.board[0][2] + this.board[1][1] + this.board[2][0] == 3) {
      return true;
    }

    return false;
  }

  public boolean lost() {
    // horizontal
    if (this.board[0][0] + this.board[0][1] + this.board[0][2] == -3) {
      return true;
    }
    if (this.board[1][0] + this.board[1][1] + this.board[1][2] == -3) {
      return true;
    }
    if (this.board[2][0] + this.board[2][1] + this.board[2][2] == -3) {
      return true;
    }
    // vertical
    if (this.board[0][0] + this.board[1][0] + this.board[2][0] == -3) {
      return true;
    }
    if (this.board[0][1] + this.board[1][1] + this.board[2][1] == -3) {
      return true;
    }
    if (this.board[2][0] + this.board[2][1] + this.board[2][2] == -3) {
      return true;
    }
    // diagonals
    if (this.board[0][0] + this.board[1][1] + this.board[2][2] == -3) {
      return true;
    }
    if (this.board[0][2] + this.board[1][1] + this.board[2][0] == -3) {
      return true;
    }
    return false;
  }

  private boolean invalidMove(String move) {
    if (move.equals(TTTActionSpace.NORTH_WEST)) {
      return this.board[0][0] != 0;
    }
    if (move.equals(TTTActionSpace.NORTH)) {
      return this.board[0][1] != 0;
    }
    if (move.equals(TTTActionSpace.NORTH_EST)) {
      return this.board[0][2] != 0;
    }
    if (move.equals(TTTActionSpace.WEST)) {
      return this.board[1][0] != 0;
    }
    if (move.equals(TTTActionSpace.CENTER)) {
      return this.board[1][1] != 0;
    }
    if (move.equals(TTTActionSpace.EST)) {
      return this.board[1][2] != 0;
    }
    if (move.equals(TTTActionSpace.SOUTH_WEST)) {
      return this.board[2][0] != 0;
    }
    if (move.equals(TTTActionSpace.SOUTH)) {
      return this.board[2][1] != 0;
    }
    if (move.equals(TTTActionSpace.SOUTH_EST)) {
      return this.board[2][2] != 0;
    }

    return false;
  }

  public boolean fullBoard() {
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

}
