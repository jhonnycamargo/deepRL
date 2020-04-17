package co.merkhet.api.rl.bandit;

import org.nd4j.linalg.factory.Nd4j;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BayesianBandit {

	private double trueMean;
	private double predictedMean;
	private int lambda;
	private int sumX;
	private int tau;

	public double pull() {
		return Nd4j.getRandom().nextDouble() + this.trueMean;
	}

	public double sample() {
		return Nd4j.getRandom().nextDouble() / Math.sqrt(this.lambda) + this.predictedMean;
	}

	public void update(double x) {
		this.lambda += this.tau;
		this.sumX += x;
		this.predictedMean = this.tau * this.sumX / this.lambda;
	}

}
