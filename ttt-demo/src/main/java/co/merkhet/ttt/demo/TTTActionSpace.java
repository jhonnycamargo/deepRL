package co.merkhet.ttt.demo;

import org.deeplearning4j.rl4j.space.DiscreteSpace;

public abstract class TTTActionSpace extends DiscreteSpace {
  public static final String NORTH_WEST = "NW";
  public static final String NORTH = "N";
  public static final String NORTH_EST = "NE";
  public static final String WEST = "W";
  public static final String CENTER = "C";
  public static final String EST = "E";
  public static final String SOUTH_WEST = "SW";
  public static final String SOUTH = "S";
  public static final String SOUTH_EST = "SE";

  protected String[] actions;

  public TTTActionSpace(int size) {
    super(size);
  }

  @Override
  public String encode(Integer a) {
    return actions[a];
  }

  @Override
  public Integer noOp() {
    return -1;
  }

  public void setRandomSeed(long seed) {
    rnd.setSeed(seed);
  }
}
