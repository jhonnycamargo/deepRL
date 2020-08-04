package co.merkhet.ttt.demo;

import org.deeplearning4j.rl4j.observation.Observation;
import org.deeplearning4j.rl4j.space.ObservationSpace;

public abstract class TTTObservationSpace implements ObservationSpace<Observation> {

	public abstract Observation getObservation(int[][] tttState);
}
