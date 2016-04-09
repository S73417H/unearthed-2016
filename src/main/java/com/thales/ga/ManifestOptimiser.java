package com.thales.ga;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.util.RandomRegistry;

import com.thales.model.Destination;
import com.thales.model.Item;
import com.thales.model.Manifest;
import com.thales.model.Priority;
import com.thales.model.Store;
import com.thales.model.Urgency;
import com.thales.model.Vessel;
import com.thales.model.Vessel.Dimension;

public class ManifestOptimiser {

	private static final long GENERATION_LIMIT = 10000;

	private final Store store;
	private final Vessel vessel;

	public ManifestOptimiser(Store store, Vessel vessel) {
		this.store = store;
		this.vessel = vessel;
	}

	private Manifest create(Genotype<BitGene> genotype) {
		Manifest manifest = new Manifest(vessel);
		((BitChromosome) genotype.getChromosome()).ones().mapToObj((i) -> store.getItem(i))
				.forEach((item) -> manifest.addItem(item));
		return manifest;

	}

	public Manifest optimise(ManifestOptimsationFunction func) {
		int nitems = (int) (0.7 * (vessel.getDimension().width * vessel.getDimension().height));
		ManifestFitnessFunction ff = new ManifestFitnessFunction(store, nitems);

		Engine<BitGene, Double> engine = Engine.builder(ff, BitChromosome.of(store.size(), 0.01))
				.optimize(Optimize.MAXIMUM).populationSize(500).survivorsSelector(new TournamentSelector<>(5))
				.offspringSelector(new RouletteWheelSelector<>())
				.alterers(new Mutator<>(0.115), new SinglePointCrossover<>(0.30)).build();

		EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

		Phenotype<BitGene, Double> best = engine.stream().peek(statistics)
				.peek((r) -> func.apply(r.getGeneration(), statistics, create(r.getBestPhenotype().getGenotype()))).limit(GENERATION_LIMIT)
				.collect(toBestPhenotype());

		return create(best.getGenotype());
	}

}
