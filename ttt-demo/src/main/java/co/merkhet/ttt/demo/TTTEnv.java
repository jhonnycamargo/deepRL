package co.merkhet.ttt.demo;

import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.observation.Observation;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TTTEnv implements MDP<Observation, Integer, DiscreteSpace> {

  @Getter
  TTTObservationSpace observationSpace;

  @Getter
  TTTActionSpace actionSpace;

  TTTBoard tttBoard;

  public TTTEnv(TTTObservationSpace observationSpace, TTTActionSpace actionSpace) {
    this.observationSpace = observationSpace;
    this.actionSpace = actionSpace;
    this.tttBoard = new TTTBoard();
  }

  public Observation reset() {
    close();
    return observationSpace.getObservation(this.tttBoard.getBoard());
  }

  public void close() {
    this.tttBoard.setBoard(new int[3][3]);
  }

  public StepReply<Observation> step(Integer action) {
    int[][] lastBoardState = this.tttBoard.getBoard();
    boolean played = this.tttBoard.play(this.actionSpace.encode(action));
    double reward = 0.0;
    if (this.tttBoard.won()) {
      reward = 50;
    } else if (this.tttBoard.fullBoard()) {
      reward = 15;
    } else if (this.tttBoard.lost() || !played) {
      reward = -10;
    }

    return new StepReply<Observation>(this.observationSpace.getObservation(lastBoardState), reward,
        isDone(), null);
  }

  public boolean isDone() {
    if (this.tttBoard.fullBoard() || this.tttBoard.won() || this.tttBoard.lost()) {
      log.debug(ArrayUtils.toString(this.tttBoard.getBoard()));
    }
    return this.tttBoard.fullBoard() || this.tttBoard.won() || this.tttBoard.lost();
  }

  public MDP<Observation, Integer, DiscreteSpace> newInstance() {
    return new TTTEnv(this.observationSpace, this.actionSpace);
  }
}
