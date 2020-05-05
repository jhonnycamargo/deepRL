package co.merkhet.api.rl.gridworld;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.javatuples.Pair;

import lombok.Getter;

/**
 * The environment.
 * 
 * @author jhonn
 *
 */
public class Grid {

	@Getter
	private int rows;
	@Getter
	private int cols;
	private int i;
	private int j;
	private Map<Pair<Integer, Integer>, Float> rewards;
	@Getter
	private Map<Pair<Integer, Integer>, List<GridAction>> actions;

	public Grid(int rows, int cols, Pair<Integer, Integer> start) {
		this.rows = rows;
		this.cols = cols;
		this.i = start.getValue0();
		this.j = start.getValue1();
	}

	public void set(Map<Pair<Integer, Integer>, Float> rewards, Map<Pair<Integer, Integer>, List<GridAction>> actions) {
		this.rewards = rewards;
		this.actions = actions;
	}

	public void setState(Pair<Integer, Integer> s) {
		this.i = s.getValue0();
		this.j = s.getValue1();
	}

	public Pair<Integer, Integer> currentState() {
		return Pair.with(this.i, this.j);
	}

	public boolean isTerminal(Pair<Integer, Integer> s) {
		return !this.actions.containsKey(s);
	}

	public float move(GridAction action) {
		if (this.actions.get(Pair.with(this.i, this.j)).contains(action)) {
			switch (action) {
			case U:
				this.i--;
				break;
			case D:
				this.i++;
				break;
			case R:
				this.j++;
				break;
			case L:
				this.j--;
				break;
			}
		}
		return Optional.ofNullable(this.rewards.get(Pair.with(this.i, this.j))).orElse(0.0f);
	}

	public void undoMove(GridAction action) {
		switch (action) {
		case U:
			this.i++;
			break;
		case D:
			this.i--;
			break;
		case R:
			this.j--;
			break;
		case L:
			this.j++;
			break;
		}
	}

	public boolean gameOver() {
		return !this.actions.containsKey(Pair.with(this.i, this.j));
	}

	public Set<Pair<Integer, Integer>> allStates() {
		Set<Pair<Integer, Integer>> allStates = new HashSet<Pair<Integer, Integer>>();
		allStates.addAll(this.actions.keySet());
		allStates.addAll(this.rewards.keySet());

		return allStates;
	}

	public static Grid standardGrid() {
		Grid g = new Grid(3, 4, Pair.with(2, 0));
		Map<Pair<Integer, Integer>, Float> rewards = new HashMap<>();
		rewards.put(Pair.with(0, 3), 1.0f);
		rewards.put(Pair.with(1, 3), -1.0f);

		Map<Pair<Integer, Integer>, List<GridAction>> actions = new HashMap<>();
		actions.put(Pair.with(0, 0), Arrays.asList(GridAction.D, GridAction.R));
		actions.put(Pair.with(0, 1), Arrays.asList(GridAction.L, GridAction.R));
		actions.put(Pair.with(0, 2), Arrays.asList(GridAction.L, GridAction.D, GridAction.R));
		actions.put(Pair.with(1, 0), Arrays.asList(GridAction.U, GridAction.D));
		actions.put(Pair.with(1, 2), Arrays.asList(GridAction.U, GridAction.D, GridAction.R));
		actions.put(Pair.with(2, 0), Arrays.asList(GridAction.U, GridAction.R));
		actions.put(Pair.with(2, 1), Arrays.asList(GridAction.L, GridAction.R));
		actions.put(Pair.with(2, 2), Arrays.asList(GridAction.L, GridAction.R, GridAction.U));
		actions.put(Pair.with(2, 3), Arrays.asList(GridAction.L, GridAction.U));

		g.set(rewards, actions);

		return g;
	}

	public static Grid negativeGrid(float stepCost) {
		Grid g = standardGrid();
		g.rewards.put(Pair.with(0, 0), stepCost);
		g.rewards.put(Pair.with(0, 1), stepCost);
		g.rewards.put(Pair.with(0, 2), stepCost);
		g.rewards.put(Pair.with(1, 0), stepCost);
		g.rewards.put(Pair.with(1, 2), stepCost);
		g.rewards.put(Pair.with(2, 0), stepCost);
		g.rewards.put(Pair.with(2, 1), stepCost);
		g.rewards.put(Pair.with(2, 2), stepCost);
		g.rewards.put(Pair.with(2, 3), stepCost);

		return g;
	}

}
