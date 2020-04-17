package co.merkhet.api.rl.bandit;

import org.nd4j.linalg.factory.Nd4j;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Bandit {

	@Getter
	private double mean;

	@Getter
	private int N;

	private double m;

	public double pull() {
		return Nd4j.getRandom().nextDouble() + this.m;
	}

	public void update(double x) {
		this.N += 1;
		this.mean = (1 - 1.0 / this.N) * this.mean + 1.0 / this.N * x;
	}

}
