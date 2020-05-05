package co.merkhet.api.rl.gridworld;

import lombok.Getter;

public enum GridAction {

	U(1), D(2), R(3), L(4);

	GridAction(int action) {
		this.action = action;
	}

	@Getter
	int action;

	public static GridAction getByActionValue(int action) {
		switch (action) {
		case 1:
			return U;
		case 2:
			return D;
		case 3:
			return R;
		case 4:
			return L;
		}

		return null;
	}
}
