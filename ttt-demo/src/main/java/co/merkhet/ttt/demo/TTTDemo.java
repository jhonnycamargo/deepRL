package co.merkhet.ttt.demo;

import java.io.IOException;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.observation.Observation;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.learning.config.Adam;

public class TTTDemo {

  private static final int MAX_STEP = 5000;

  private static final int EPOCH_STEP = 40;

  public static QLearningConfiguration TTT_QL = QLearningConfiguration.builder().doubleDQN(true)
      .epsilonNbStep(10000).minEpsilon(0.3f).errorClamp(10.0).gamma(0.99).rewardFactor(0.05)
      .targetDqnUpdateFreq(100).batchSize(1).expRepMaxSize(150000).maxStep(EPOCH_STEP * MAX_STEP)
      .maxEpochStep(EPOCH_STEP).seed(123L).build();

  public static DQNDenseNetworkConfiguration TTT_NET = DQNDenseNetworkConfiguration.builder()
      .l2(0.00).updater(new Adam(0.001)).numHiddenNodes(20).numLayers(3).build();

  public static void main(String[] args) throws IOException {
    trainTTTDemo();
  }

  private static void trainTTTDemo() throws IOException {
    TTTEnv mdp = createTTTEnvironment();

    QLearningDiscreteDense<Observation> dql =
        new QLearningDiscreteDense<Observation>(mdp, TTT_NET, TTT_QL);

    dql.train();

    DQNPolicy<Observation> policy = dql.getPolicy();

    policy.save("E:\\Dev\\tmp\\ttt.policy");

    mdp.close();
  }

  private static TTTEnv createTTTEnvironment() {
    TTTActionSpaceImpl actionSpace = new TTTActionSpaceImpl(TTTActionSpace.NORTH_WEST,
        TTTActionSpace.NORTH, TTTActionSpace.NORTH_EST, TTTActionSpace.WEST, TTTActionSpace.CENTER,
        TTTActionSpace.EST, TTTActionSpace.SOUTH_WEST, TTTActionSpace.SOUTH,
        TTTActionSpace.SOUTH_EST);
    actionSpace.setRandomSeed(123);
    TTTObservationSpaceImpl observationSpace = new TTTObservationSpaceImpl();

    return new TTTEnv(observationSpace, actionSpace);
  }

}
