package co.merkhet.api.rl.gridworld;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.javatuples.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class IterativePolicyEvaluation {

	private final static double SMALL_ENOUGH = 1E-3;

	static void printValues(INDArray values, Grid g) {
		for (int i = 0; i < g.getRows(); i++) {
			System.out.println("------------------------------------------------------------------------------------");
			for (int j = 0; j < g.getCols(); j++) {
				double v = Optional.ofNullable(values.getDouble(i, j)).orElse(0.0);
				System.out.print(v + " ");
			}
			System.out.println();
		}
	}

	static void printPolicy(INDArray policy, Grid g) {
		for (int i = 0; i < g.getRows(); i++) {
			System.out.println("------------------------------------------------------------------------------------");
			for (int j = 0; j < g.getCols(); j++) {
				int p = Optional.ofNullable(policy.getInt(i, j)).orElse(0);
				System.out.print(GridAction.getByActionValue(p) + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		Grid grid = Grid.standardGrid();
		Set<Pair<Integer, Integer>> states = grid.allStates();
		INDArray upperV = Nd4j.zeros(grid.getRows(), grid.getCols());
		double gamma = 1.0;
		while (true) {
			double biggestChange = 0.0;
			for (Pair<Integer, Integer> s : states) {
				double oldV = upperV.getDouble(s.getValue0(), s.getValue1());

				if (grid.getActions().containsKey(s)) {
					INDArray newV = Nd4j.scalar(0.0);
					double pOfA = 1.0 / grid.getActions().get(s).size();
					for (GridAction a : grid.getActions().get(s)) {
						grid.setState(s);
						float r = grid.move(a);
						newV = newV.add(pOfA * (r + gamma
								* upperV.getDouble(grid.currentState().getValue0(), grid.currentState().getValue1())));
					}
					upperV.put(s.getValue0(), s.getValue1(), newV);
					biggestChange = Math.max(biggestChange,
							Math.abs(oldV - upperV.getDouble(s.getValue0(), s.getValue1())));
				}
			}
			if (biggestChange < SMALL_ENOUGH) {
				break;
			}
		}

		System.out.println("Values for uniformly random actions: ");
		printValues(upperV, grid);
		System.out.println();
		System.out.println();

		INDArray policy = Nd4j.zeros(grid.getRows(), grid.getCols());
		policy.putScalar(2, 0, GridAction.U.action);
		policy.putScalar(1, 0, GridAction.U.action);
		policy.putScalar(0, 0, GridAction.R.action);
		policy.putScalar(0, 1, GridAction.R.action);
		policy.putScalar(0, 2, GridAction.R.action);
		policy.putScalar(1, 2, GridAction.R.action);
		policy.putScalar(2, 1, GridAction.R.action);
		policy.putScalar(2, 2, GridAction.R.action);
		policy.putScalar(2, 3, GridAction.U.action);

		printPolicy(policy, grid);

		upperV = Nd4j.zeros(grid.getRows(), grid.getCols());
		gamma = 0.9;
		while (true) {
			double biggestChange = 0.0;
			for (Pair<Integer, Integer> s : states) {
				double oldV = upperV.getDouble(s.getValue0(), s.getValue1());

				if (policy.getInt(s.getValue0(), s.getValue1()) != 0) {
					int a = policy.getInt(s.getValue0(), s.getValue1());
					grid.setState(s);
					double r = grid.move(GridAction.getByActionValue(a));
					upperV.put(s.getValue0(), s.getValue1(), r + gamma
							* upperV.getDouble(grid.currentState().getValue0(), grid.currentState().getValue1()));

					biggestChange = Math.max(biggestChange,
							Math.abs(oldV - upperV.getDouble(s.getValue0(), s.getValue1())));
				}
			}
			if (biggestChange < SMALL_ENOUGH) {
				break;
			}
		}

		System.out.println("Values for fixed policy: ");
		printValues(upperV, grid);
	}
}
