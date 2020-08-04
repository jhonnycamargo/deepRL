package co.merkhet.ttt.demo;

public enum TTTMove {

  NW(1, 0, 0), N(2, 0, 1), NE(3, 0, 2), W(4, 1, 0), C(5, 1, 1), E(6, 1, 2), SW(7, 2, 0), S(8, 2,
      1), SE(9, 2, 2);

  int move;
  int row;
  int col;

  private TTTMove(int move, int row, int col) {
    this.move = move;
    this.row = row;
    this.col = col;
  }

  public static TTTMove getByMove(int move) {
    switch (move) {
      case 1:
        return NW;
      case 2:
        return N;
      case 3:
        return NE;
      case 4:
        return W;
      case 5:
        return C;
      case 6:
        return E;
      case 7:
        return SW;
      case 8:
        return S;
      case 9:
        return SE;
    }
    return null;
  }

}
