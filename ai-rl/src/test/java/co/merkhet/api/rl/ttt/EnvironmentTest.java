package co.merkhet.api.rl.ttt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EnvironmentTest {

	@Test
	void whenBoardPositionIsEmptyThenTrue() {
		Environment environment = new Environment();
		assertTrue(environment.isEmpty(0, 0));
	}

	@Test
	void whenInitialStateThenRewardZero() {
		Environment environment = new Environment();
		assertEquals(0, environment.reward(-1));
		assertEquals(0, environment.reward(1));
	}

	@Test
	void whenInitialStateThenStateEqualsZero() {
		Environment environment = new Environment();
		assertEquals(0, environment.getState());
	}

	@Test
	void whenInitialStateThenGameOverIsfalse() {
		Environment environment = new Environment();
		assertFalse(environment.gameOver(false));
	}
}
