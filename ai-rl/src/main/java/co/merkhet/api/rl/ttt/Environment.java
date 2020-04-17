package co.merkhet.api.rl.ttt;

import java.util.stream.Stream;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.CustomOp;
import org.nd4j.linalg.api.ops.DynamicCustomOp;
import org.nd4j.linalg.api.ops.impl.transforms.custom.Reverse;
import org.nd4j.linalg.api.ops.impl.transforms.custom.Trace;
import org.nd4j.linalg.factory.Nd4j;

import lombok.Getter;

public class Environment {

	public static final int BOARD_LENGTH = 3;
	private INDArray board;
	@Getter
	private int x;
	@Getter
	private int o;
	@Getter
	private Integer winner;
	private boolean ended;
	@Getter
	private int numStates;

	public Environment() {
		board = Nd4j.zeros(BOARD_LENGTH, BOARD_LENGTH);
		x = -1;
		o = 1;
		numStates = (int) Math.pow(3, BOARD_LENGTH * BOARD_LENGTH);
	}

	public boolean isEmpty(int i, int j) {
		return this.board.getDouble(i, j) == 0;
	}

	public int reward(int symbol) {
		if (!gameOver(false)) {
			return 0;
		}

		if (this.winner != null && this.winner == symbol) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getState() {
		int k = 0;
		int h = 0;
		for (int i = 0; i < BOARD_LENGTH; i++) {
			for (int j = 0; j < BOARD_LENGTH; j++) {
				Integer v = null;
				if (this.board.getDouble(i, j) == 0) {
					v = 0;
				} else if (this.board.getDouble(i, j) == this.x) {
					v = 1;
				} else if (this.board.getDouble(i, j) == this.o) {
					v = 2;
				}

				h += Math.pow(3, k) * v;
				k += 1;
			}

		}

		return h;
	}

	public boolean gameOver(boolean forceRecalculate) {
		if (!forceRecalculate && this.ended) {
			return this.ended;
		}

		for (int i = 0; i < BOARD_LENGTH; i++) {
			final int row = i;
			if (Stream.of(-1, 1).anyMatch(player -> {
				if (this.board.getRow(row).sumNumber().intValue() == player * BOARD_LENGTH) {
					this.winner = player;
					this.ended = true;

					return true;
				}
				return false;
			})) {
				return true;
			}
		}

		for (int i = 0; i < BOARD_LENGTH; i++) {
			final int row = i;
			if (Stream.of(-1, 1).anyMatch(player -> {
				if (this.board.getColumn(row).sumNumber().intValue() == player * BOARD_LENGTH) {
					this.winner = player;
					this.ended = true;

					return true;
				}
				return false;
			})) {
				return true;
			}
		}

		if (Stream.of(-1, 1).anyMatch(player -> {
			CustomOp op = DynamicCustomOp.builder(new Trace().opName()).addInputs(this.board).build();
			INDArray[] output = Nd4j.exec(op);
			if (output[0].getInt(0) == player * BOARD_LENGTH) {
				this.winner = player;
				this.ended = true;

				return true;
			}
			return false;
		})) {
			return true;
		}

		if (Stream.of(-1, 1).anyMatch(player -> {
			CustomOp opReverse = DynamicCustomOp.builder(new Reverse().opName()).addInputs(this.board).build();
			INDArray[] outputReverse = Nd4j.exec(opReverse);

			CustomOp opTrace = DynamicCustomOp.builder(new Trace().opName()).addInputs(outputReverse).build();
			INDArray[] outputTrace = Nd4j.exec(opTrace);
			if (outputTrace[0].getInt(0) == player * BOARD_LENGTH) {
				this.winner = player;
				this.ended = true;

				return true;
			}
			return false;
		})) {
			return true;
		}

		if (this.board.all()) {
			this.winner = null;
			this.ended = true;

			return true;
		}

		this.winner = null;
		return false;
	}

	public boolean isDraw() {
		return this.ended && this.winner == null;
	}

	public void setBoard(int i, int j, int value) {
		this.board.putScalar(new int[] { i, j }, value);
	}
}
