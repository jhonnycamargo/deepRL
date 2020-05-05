package co.merkhet.api.rl.ttt;

import static co.merkhet.api.rl.ttt.Environment.BOARD_LENGTH;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import lombok.Setter;

public class TicTacToeAgent {

	@Setter
	private double eps;
	@Setter
	private double alpha;
	private List<Integer> stateHistory;
	@Setter
	private INDArray value;
	@Setter
	private int symbol;

	public TicTacToeAgent() {
		this.stateHistory = new ArrayList<>();
		this.eps = 0.1;
		this.alpha = 0.5;
	}

	public void resetHistory() {
		this.stateHistory = new ArrayList<>();
	}

	public void takeAction(Environment environment) {
		double r = Nd4j.getRandom().nextDouble();
		Pair<Integer, Integer> nextMove;
		Integer bestState = null;
		if (r < this.eps) {
			List<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();
			for (int i = 0; i < BOARD_LENGTH; i++) {
				for (int j = 0; j < BOARD_LENGTH; j++) {
					if (environment.isEmpty(i, j)) {
						possibleMoves.add(Pair.with(i, j));
					}
				}
			}
			int idx = Nd4j.getRandom().nextInt(possibleMoves.size());
			nextMove = possibleMoves.get(idx);
		} else {
			nextMove = null;
			double bestValue = -1;
			for (int i = 0; i < BOARD_LENGTH; i++) {
				for (int j = 0; j < BOARD_LENGTH; j++) {
					if (environment.isEmpty(i, j)) {
						environment.setBoard(i, j, this.symbol);
						int state = environment.getState();
						environment.setBoard(i, j, 0);
						if (this.value.getDouble(state) > bestValue) {
							bestValue = this.value.getDouble(state);
							bestState = state;
							nextMove = Pair.with(i, j);
						}
					}
				}
			}
		}

		if (nextMove != null) {
			environment.setBoard(nextMove.getValue0(), nextMove.getValue1(), this.symbol);
		}
	}

	public Pair<Integer, Integer> playAction(Environment environment) {
		Pair<Integer, Integer> nextMove;
		nextMove = null;
		double bestValue = -1;
		for (int i = 0; i < BOARD_LENGTH; i++) {
			for (int j = 0; j < BOARD_LENGTH; j++) {
				if (environment.isEmpty(i, j)) {
					environment.setBoard(i, j, this.symbol);
					int state = environment.getState();
					environment.setBoard(i, j, 0);
					if (this.value.getDouble(state) > bestValue) {
						bestValue = this.value.getDouble(state);
						nextMove = Pair.with(i, j);
					}
				}
			}
		}

		return nextMove;
	}

	public void updateStateHistory(int state) {
		this.stateHistory.add(state);
	}

	/**
	 * V(St) <- V(St) + a[V(St+1) - V(St)]
	 * 
	 * @param environment
	 */
	public void update(Environment environment) {
		int reward = environment.reward(this.symbol);
		double target = reward;
		for (int i = this.stateHistory.size(); i-- > 0;) {
			double value = this.value.getDouble(this.stateHistory.get(i))
					+ this.alpha * (target - this.value.getDouble(this.stateHistory.get(i)));
			this.value.putScalar(this.stateHistory.get(i), value);
			target = value;
		}
		resetHistory();
	}
}
