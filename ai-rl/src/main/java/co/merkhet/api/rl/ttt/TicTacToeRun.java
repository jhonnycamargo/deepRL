package co.merkhet.api.rl.ttt;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TicTacToeRun {
	public static void main(String[] args) {
		TicTacToeAgent p1 = new TicTacToeAgent();
		p1.setEps(0.1);
		p1.setAlpha(0.5);
		TicTacToeAgent p2 = new TicTacToeAgent();
		p2.setEps(0.1);
		p2.setAlpha(0.5);

		Environment environment = new Environment();
		TicTacToeRun ticTacToeRun = new TicTacToeRun();
		List<Triplet<Integer, Integer, Boolean>> stateWinnerTriples = ticTacToeRun.getStateHashAndWinner(environment, 0,
				0);
		INDArray vx = ticTacToeRun.initialVx(environment, stateWinnerTriples);
		p1.setValue(vx);
		INDArray vo = ticTacToeRun.initialVo(environment, stateWinnerTriples);
		p2.setValue(vo);

		p1.setSymbol(environment.getX());
		p2.setSymbol(environment.getO());

		int t = 10000;
		for (int i = 0; i < t; i++) {
			if (i % 200 == 0) {
				System.out.println(i);
			}
			ticTacToeRun.playGame(p1, p2, new Environment());
		}

		TicTacToe.externalRun(p1);
	}

	private List<Triplet<Integer, Integer, Boolean>> getStateHashAndWinner(Environment environment, int i, int j) {
		List<Triplet<Integer, Integer, Boolean>> results = new ArrayList<>();
		int[] players = new int[] { 0, environment.getX(), environment.getO() };
		for (int k = 0; k < players.length; k++) {
			int player = players[k];
			environment.setBoard(i, j, player);
			if (j == 2) {
				if (i == 2) {
					int state = environment.getState();
					boolean ended = environment.gameOver(true);
					Integer winner = environment.getWinner();
					results.add(Triplet.with(state, winner, ended));
				} else {
					results.addAll(getStateHashAndWinner(environment, i + 1, 0));
				}
			} else {
				results.addAll(getStateHashAndWinner(environment, i, j + 1));
			}
		}

		return results;
	}

	private INDArray initialVx(Environment environment, List<Triplet<Integer, Integer, Boolean>> stateWinnerTriples) {
		INDArray values = Nd4j.zeros(environment.getNumStates());
		for (Triplet<Integer, Integer, Boolean> stateWinner : stateWinnerTriples) {
			Double v = null;
			if (stateWinner.getValue2()) {
				if (stateWinner.getValue1() != null && stateWinner.getValue1() == environment.getX()) {
					v = 1.0;
				} else {
					v = 0.0;
				}
			} else {
				v = 0.5;
			}
			values.putScalar(stateWinner.getValue0(), v);
		}

		return values;
	}

	private INDArray initialVo(Environment environment, List<Triplet<Integer, Integer, Boolean>> stateWinnerTriples) {
		INDArray values = Nd4j.zeros(environment.getNumStates());
		for (Triplet<Integer, Integer, Boolean> stateWinner : stateWinnerTriples) {
			Double v = null;
			if (stateWinner.getValue2()) {
				if (stateWinner.getValue1() != null && stateWinner.getValue1() == environment.getO()) {
					v = 1.0;
				} else {
					v = 0.0;
				}
			} else {
				v = 0.5;
			}
			values.putScalar(stateWinner.getValue0(), v);
		}

		return values;
	}

	private void playGame(TicTacToeAgent p1, TicTacToeAgent p2, Environment environment) {
		TicTacToeAgent currentPlayer = null;
		while (!environment.gameOver(false)) {
			if (currentPlayer == p1) {
				currentPlayer = p2;
			} else {
				currentPlayer = p1;
			}

			currentPlayer.takeAction(environment);

			int state = environment.getState();
			p1.updateStateHistory(state);
			p2.updateStateHistory(state);
		}

		p1.update(environment);
		p2.update(environment);
	}
}
