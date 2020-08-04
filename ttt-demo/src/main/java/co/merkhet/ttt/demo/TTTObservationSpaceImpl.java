package co.merkhet.ttt.demo;

import org.deeplearning4j.rl4j.observation.Observation;
import org.nd4j.common.util.ArrayUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TTTObservationSpaceImpl extends TTTObservationSpace {

  int[][] board;

  public String getName() {
    return "TTTObservations";
  }

  public int[] getShape() {
    return new int[] {1, 9};
  }

  public INDArray getLow() {
    return Nd4j.create(getShape());
  }

  public INDArray getHigh() {
    return Nd4j.create(getShape());
  }

  @Override
  public Observation getObservation(int[][] tttState) {
    int[] flattened = ArrayUtil.flatten(tttState);
    double[] doubles = ArrayUtil.toDoubles(flattened);
    INDArray data = Nd4j.create(doubles, getShape());

    return new Observation(data);
  }

}
