package co.merkhet.ttt.demo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class TTTState {

	public static final int[] players = new int[] { 1, -1 };

	@Getter
	private int[][] state = new int[3][3];

	public void playFirstPlayer(int x, int y) {
		if (state[x][y] == 0) {
			state[x][y] = players[0];
		}
	}

	public void playSecondPlayer(int x, int y) {
		if (state[x][y] == 0) {
			state[x][y] = players[1];
		}
	}
}
