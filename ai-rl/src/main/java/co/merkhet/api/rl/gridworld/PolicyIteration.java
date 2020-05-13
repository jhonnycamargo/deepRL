package co.merkhet.api.rl.gridworld;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.javatuples.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class PolicyIteration {

	private final static double SMALL_ENOUGH = 1E-3;

	private final static double GAMMA = 0.9;

	public static void main(String[] args) {
		Grid grid = Grid.negativeGrid(-0.1f);
		System.out.println("Rewards: ");
		printValues(grid.getRewards(), grid);

		INDArray policy = Nd4j.zeros(grid.getRows(), grid.getCols());
		for (Pair<Integer, Integer> s : grid.getActions().keySet()) {
			policy.putScalar(s.getValue0(), s.getValue1(), Nd4j.randomFactory.getNewRandomInstance().nextInt(1, 5));
		}

		System.out.println("Initial policy:");
		IterativePolicyEvaluation.printPolicy(policy, grid);

		INDArray upperV = Nd4j.zeros(grid.getRows(), grid.getCols());
		Set<Pair<Integer, Integer>> states = grid.allStates();
		for (Pair<Integer, Integer> s : states) {
			if (grid.getActions().containsKey(s)) {
				upperV.putScalar(s.getValue0(), s.getValue1(), Nd4j.getRandom().nextDouble());
			}
		}

		while (true) {
			while (true) {
				double biggestChange = 0.0;
				for (Pair<Integer, Integer> s : states) {
					double oldV = upperV.getDouble(s.getValue0(), s.getValue1());
					if (policy.getInt(s.getValue0(), s.getValue1()) != 0) {
						int a = policy.getInt(s.getValue0(), s.getValue1());
						grid.setState(s);
						float r = grid.move(GridAction.getByActionValue(a));
						double newV = r + GAMMA
								* upperV.getDouble(grid.currentState().getValue0(), grid.currentState().getValue1());
						upperV.put(s.getValue0(), s.getValue1(), newV);
						biggestChange = Math.max(biggestChange,
								Math.abs(oldV - upperV.getDouble(s.getValue0(), s.getValue1())));
					}
				}
				if (biggestChange < SMALL_ENOUGH) {
					break;
				}
			}

			boolean isPolicyConverged = true;
			for (Pair<Integer, Integer> s : states) {
				if (policy.getInt(s.getValue0(), s.getValue1()) != 0) {
					GridAction oldAction = GridAction.getByActionValue(policy.getInt(s.getValue0(), s.getValue1()));
					GridAction newAction = null;
					double bestValue = Double.NEGATIVE_INFINITY;
					for (GridAction action : GridAction.values()) {
						grid.setState(s);
						float r = grid.move(action);
						double v = r + GAMMA
								* upperV.getDouble(grid.currentState().getValue0(), grid.currentState().getValue1());
						if (v > bestValue) {
							bestValue = v;
							newAction = action;
						}
					}
					policy.putScalar(s.getValue0(), s.getValue1(), newAction.getAction());
					if (newAction != oldAction) {
						isPolicyConverged = false;
					}
				}
			}

			if (isPolicyConverged) {
				break;
			}
		}

		System.out.println("Values: ");
		IterativePolicyEvaluation.printValues(upperV, grid);
		System.out.println("Policy: ");
		IterativePolicyEvaluation.printPolicy(policy, grid);
	}

	private static void printValues(Map<Pair<Integer, Integer>, Float> rewards, Grid g) {
		for (int i = 0; i < g.getRows(); i++) {
			System.out.println("------------------------------------------------------------------------------------");
			for (int j = 0; j < g.getCols(); j++) {
				double v = Optional.ofNullable(rewards.get(Pair.with(i, j))).orElse(0.0f);
				System.out.print(v + " ");
			}
			System.out.println();
		}
	}
}
