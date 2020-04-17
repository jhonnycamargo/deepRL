package co.merkhet.api.rl.bandit;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class BanditRun {

	/**
	 * Epsilon-Greedy
	 * 
	 * @param m1
	 * @param m2
	 * @param m3
	 * @param eps
	 * @param N
	 * @return
	 */
	public INDArray run_experiment(double m1, double m2, double m3, double eps, int N) {
		Bandit[] bandits = new Bandit[] { new Bandit(0, 0, m1), new Bandit(0, 0, m2), new Bandit(0, 0, m3) };

		INDArray data = Nd4j.empty(Nd4j.create(N).dataType());

		for (int i = 0; i < N; i++) {
			int j;
			double p = Nd4j.getRandom().nextDouble();
			if (p < eps) {
				j = Nd4j.getRandom().nextInt(3);
			} else {
				j = Nd4j.argMax(Nd4j.create(Stream.of(bandits).map(Bandit::getMean).collect(Collectors.toList())), 1)
						.getInt(0);
			}

			double x = bandits[j].pull();
			bandits[j].update(x);

			data.put(i, Nd4j.scalar(x));
		}

		INDArray cumulativeAverage = Nd4j.cumsum(data).div(Nd4j.arange(N).add(1));

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("cumAvg", Nd4j.arange(N).toDoubleVector(), cumulativeAverage.toDoubleVector());
		plot.addLinePlot("m1", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m1).toDoubleVector());
		plot.addLinePlot("m2", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m2).toDoubleVector());
		plot.addLinePlot("m3", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m3).toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("A plot panel: " + eps);
		frame.setContentPane(plot);
		frame.setVisible(true);

		return cumulativeAverage;
	}

	/**
	 * Optimistic Initial Values.
	 * 
	 * @param m1
	 * @param m2
	 * @param m3
	 * @param N
	 * @param upperLimit
	 * @return
	 */
	public INDArray run_experiment(double m1, double m2, double m3, int N, double upperLimit) {
		Bandit[] bandits = new Bandit[] { new Bandit(upperLimit, 1, m1), new Bandit(upperLimit, 1, m2),
				new Bandit(upperLimit, 1, m3) };

		INDArray data = Nd4j.empty(Nd4j.create(N).dataType());

		for (int i = 0; i < N; i++) {
			int j = Nd4j.argMax(Nd4j.create(Stream.of(bandits).map(Bandit::getMean).collect(Collectors.toList())), 1)
					.getInt(0);

			double x = bandits[j].pull();
			bandits[j].update(x);

			data.put(i, Nd4j.scalar(x));
		}

		INDArray cumulativeAverage = Nd4j.cumsum(data).div(Nd4j.arange(N).add(1));

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("cumAvg", Nd4j.arange(N).toDoubleVector(), cumulativeAverage.toDoubleVector());
		plot.addLinePlot("m1", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m1).toDoubleVector());
		plot.addLinePlot("m2", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m2).toDoubleVector());
		plot.addLinePlot("m3", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m3).toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Cumulative Average");
		frame.setContentPane(plot);
		frame.setVisible(true);

		return cumulativeAverage;
	}

	/**
	 * Upper confident bound.
	 * 
	 * @param m1
	 * @param m2
	 * @param m3
	 * @param N
	 * @return
	 */
	public INDArray run_experiment(double m1, double m2, double m3, int N) {
		Bandit[] bandits = new Bandit[] { new Bandit(0, 0, m1), new Bandit(0, 0, m2), new Bandit(0, 0, m3) };

		INDArray data = Nd4j.empty(Nd4j.create(N).dataType());

		for (int i = 0; i < N; i++) {
			final int ith = i;
			int j = Nd4j.argMax(Nd4j.create(Stream.of(bandits)
					.map(bandit -> ucb(bandit.getMean(), ith + 1, bandit.getN())).collect(Collectors.toList())), 1)
					.getInt(0);

			double x = bandits[j].pull();
			bandits[j].update(x);

			data.put(i, Nd4j.scalar(x));
		}

		INDArray cumulativeAverage = Nd4j.cumsum(data).div(Nd4j.arange(N).add(1));

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("cumAvg", Nd4j.arange(N).toDoubleVector(), cumulativeAverage.toDoubleVector());
		plot.addLinePlot("m1", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m1).toDoubleVector());
		plot.addLinePlot("m2", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m2).toDoubleVector());
		plot.addLinePlot("m3", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m3).toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Cumulative Average");
		frame.setContentPane(plot);
		frame.setVisible(true);

		return cumulativeAverage;
	}

	private double ucb(double mean, int n, int nj) {
		if (nj == 0) {
			return Double.POSITIVE_INFINITY;
		}

		return mean + Math.sqrt(2 * Math.log(n) / nj);
	}

	/**
	 * Decaying epsilon.
	 * 
	 * @param m1
	 * @param m2
	 * @param m3
	 * @param N
	 * @return
	 */
	public INDArray run_experiment_decaying_epsilon(double m1, double m2, double m3, int N) {
		Bandit[] bandits = new Bandit[] { new Bandit(0, 0, m1), new Bandit(0, 0, m2), new Bandit(0, 0, m3) };

		INDArray data = Nd4j.empty(Nd4j.create(N).dataType());

		for (int i = 0; i < N; i++) {
			int j;
			double p = Nd4j.getRandom().nextDouble();
			if (p < 1.0 / (i + 1)) {
				j = Nd4j.getRandom().nextInt(3);
			} else {
				j = Nd4j.argMax(Nd4j.create(Stream.of(bandits).map(Bandit::getMean).collect(Collectors.toList())), 1)
						.getInt(0);
			}

			double x = bandits[j].pull();
			bandits[j].update(x);

			data.put(i, Nd4j.scalar(x));
		}

		INDArray cumulativeAverage = Nd4j.cumsum(data).div(Nd4j.arange(N).add(1));

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("cumAvg", Nd4j.arange(N).toDoubleVector(), cumulativeAverage.toDoubleVector());
		plot.addLinePlot("m1", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m1).toDoubleVector());
		plot.addLinePlot("m2", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m2).toDoubleVector());
		plot.addLinePlot("m3", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m3).toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Decaying Epsilon");
		frame.setContentPane(plot);
		frame.setVisible(true);

		return cumulativeAverage;
	}

	/**
	 * Thompson sampling Bayes.
	 * 
	 * @param m1
	 * @param m2
	 * @param m3
	 * @param N
	 * @param upperLimit
	 * @return
	 */
	public INDArray run_experimentBayes(double m1, double m2, double m3, int N) {
		BayesianBandit[] bandits = new BayesianBandit[] { new BayesianBandit(m1, 0, 1, 0, 1),
				new BayesianBandit(m2, 0, 1, 0, 1), new BayesianBandit(m3, 0, 1, 0, 1) };

		INDArray data = Nd4j.empty(Nd4j.create(N).dataType());

		for (int i = 0; i < N; i++) {
			int j = Nd4j
					.argMax(Nd4j.create(Stream.of(bandits).map(BayesianBandit::sample).collect(Collectors.toList())), 1)
					.getInt(0);

			double x = bandits[j].pull();
			bandits[j].update(x);

			data.put(i, Nd4j.scalar(x));
		}

		INDArray cumulativeAverage = Nd4j.cumsum(data).div(Nd4j.arange(N).add(1));

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("cumAvg", Nd4j.arange(N).toDoubleVector(), cumulativeAverage.toDoubleVector());
		plot.addLinePlot("m1", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m1).toDoubleVector());
		plot.addLinePlot("m2", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m2).toDoubleVector());
		plot.addLinePlot("m3", Nd4j.arange(N).toDoubleVector(), Nd4j.ones(N).mul(m3).toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Thompson sampling");
		frame.setContentPane(plot);
		frame.setVisible(true);

		return cumulativeAverage;
	}

	public static void main(String[] args) {
//		runEpsiloGreedy();
//		runOptimisticInitialValues();
//		runUcb();
		runComparing();
	}

	private static void runEpsiloGreedy() {
		BanditRun run = new BanditRun();
		INDArray c1 = run.run_experiment(1.0, 2.0, 3.0, 0.1, 100000);
		INDArray c05 = run.run_experiment(1.0, 2.0, 3.0, 0.05, 100000);
		INDArray c01 = run.run_experiment(1.0, 2.0, 3.0, 0.01, 100000);

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("cumAvg1", Nd4j.arange(100000).toDoubleVector(), c1.toDoubleVector());
		plot.addLinePlot("cumAvg05", Nd4j.arange(100000).toDoubleVector(), c05.toDoubleVector());
		plot.addLinePlot("cumAvg01", Nd4j.arange(100000).toDoubleVector(), c01.toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Cumulative Average");
		frame.setContentPane(plot);
		frame.setVisible(true);

		Plot2DPanel plotLinear = new Plot2DPanel();
		plotLinear.addLinePlot("cumAvg1", Nd4j.arange(100000).toDoubleVector(), c1.toDoubleVector());
		plotLinear.addLinePlot("cumAvg05", Nd4j.arange(100000).toDoubleVector(), c05.toDoubleVector());
		plotLinear.addLinePlot("cumAvg01", Nd4j.arange(100000).toDoubleVector(), c01.toDoubleVector());
		plot.addLegend("NORTH");
		plotLinear.setAxisScale(0, "LIN");

		JFrame frameLinear = new JFrame("Cumulative Average Linear");
		frameLinear.setContentPane(plotLinear);
		frameLinear.setVisible(true);
	}

	private static void runOptimisticInitialValues() {
		BanditRun run = new BanditRun();
		INDArray c1 = run.run_experiment(1.0, 2.0, 3.0, 0.1, 100000);
		INDArray oiv = run.run_experiment(1.0, 2.0, 3.0, 100000, 10.0);

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("Eps = 0.1", Nd4j.arange(100000).toDoubleVector(), c1.toDoubleVector());
		plot.addLinePlot("Optimistic", Nd4j.arange(100000).toDoubleVector(), oiv.toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Cumulative Average");
		frame.setContentPane(plot);
		frame.setVisible(true);

		Plot2DPanel plotLinear = new Plot2DPanel();
		plotLinear.addLinePlot("Eps = 0.1", Nd4j.arange(100000).toDoubleVector(), c1.toDoubleVector());
		plotLinear.addLinePlot("Optimistic", Nd4j.arange(100000).toDoubleVector(), oiv.toDoubleVector());
		plot.addLegend("NORTH");
		plotLinear.setAxisScale(0, "LIN");

		JFrame frameLinear = new JFrame("Cumulative Average Linear");
		frameLinear.setContentPane(plotLinear);
		frameLinear.setVisible(true);
	}

	private static void runUcb() {
		BanditRun run = new BanditRun();
		INDArray eps = run.run_experiment(1.0, 2.0, 3.0, 0.1, 100000);
		INDArray ucb = run.run_experiment(1.0, 2.0, 3.0, 100000);

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("Eps = 0.1", Nd4j.arange(100000).toDoubleVector(), eps.toDoubleVector());
		plot.addLinePlot("Upper Confident Bound", Nd4j.arange(100000).toDoubleVector(), ucb.toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Cumulative Average");
		frame.setContentPane(plot);
		frame.setVisible(true);

		Plot2DPanel plotLinear = new Plot2DPanel();
		plotLinear.addLinePlot("Eps = 0.1", Nd4j.arange(100000).toDoubleVector(), eps.toDoubleVector());
		plotLinear.addLinePlot("Optimistic", Nd4j.arange(100000).toDoubleVector(), ucb.toDoubleVector());
		plotLinear.addLegend("NORTH");
		plotLinear.setAxisScale(0, "LIN");

		JFrame frameLinear = new JFrame("Cumulative Average Linear");
		frameLinear.setContentPane(plotLinear);
		frameLinear.setVisible(true);
	}

	private static void runComparing() {
		double m1 = 1.0;
		double m2 = 2.0;
		double m3 = 3.0;

		BanditRun run = new BanditRun();

		INDArray eps = run.run_experiment_decaying_epsilon(m1, m2, m3, 100000);
		INDArray oiv = run.run_experiment(m1, m2, m3, 100000, 10.0);
		INDArray ucb = run.run_experiment(m1, m2, m3, 100000);
		INDArray bayes = run.run_experimentBayes(m1, m2, m3, 100000);

		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("Decaying Epsilon", Nd4j.arange(100000).toDoubleVector(), eps.toDoubleVector());
		plot.addLinePlot("Optimistic", Nd4j.arange(100000).toDoubleVector(), oiv.toDoubleVector());
		plot.addLinePlot("Upper Confident Bound", Nd4j.arange(100000).toDoubleVector(), ucb.toDoubleVector());
		plot.addLinePlot("Bayesian", Nd4j.arange(100000).toDoubleVector(), bayes.toDoubleVector());
		plot.addLegend("NORTH");
		plot.setAxisScale(0, "LOG");

		JFrame frame = new JFrame("Comparing Cumulative Average");
		frame.setContentPane(plot);
		frame.setVisible(true);

		Plot2DPanel plotLinear = new Plot2DPanel();
		plotLinear.addLinePlot("Decaying Epsilon", Nd4j.arange(100000).toDoubleVector(), eps.toDoubleVector());
		plotLinear.addLinePlot("Optimistic", Nd4j.arange(100000).toDoubleVector(), oiv.toDoubleVector());
		plotLinear.addLinePlot("Upper Confident Bound", Nd4j.arange(100000).toDoubleVector(), ucb.toDoubleVector());
		plotLinear.addLinePlot("Bayesian", Nd4j.arange(100000).toDoubleVector(), bayes.toDoubleVector());
		plotLinear.addLegend("NORTH");
		plotLinear.setAxisScale(0, "LIN");

		JFrame frameLinear = new JFrame("Comparing Cumulative Average Linear");
		frameLinear.setContentPane(plotLinear);
		frameLinear.setVisible(true);
	}
}
