package com.thales.ga;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SwapMutator;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.limit;
import org.jenetics.util.ISeq;

import com.thales.model.Item;
import com.thales.model.Manifest;
import com.thales.model.Store;
import com.thales.model.Vessel;

public class ManifestOptimiser {

	private final Store store;
	private final List<Vessel> vessels;

	public ManifestOptimiser(Store store, List<Vessel> vessels) {
		this.store = store;
		this.vessels = vessels;
	}

	private static class ManifestComparitor implements Comparator<EnumGene<Item>> {

		@Override
		public int compare(EnumGene<Item> o1, EnumGene<Item> o2) {
			Item item1 = o1.getAllele();
			Item item2 = o2.getAllele();
			int delta = item1.getDestination().compareTo(item2.getDestination());
			if (delta == 0) {
				return item1.getPriority().compareTo(item2.getPriority());
			}
			return delta;
		}

	}

	private List<Manifest> create(Genotype<EnumGene<Item>> genotype) {
		List<Manifest> manifests = vessels.stream().map((v) -> new Manifest(v)).collect(Collectors.toList());
		ISeq<EnumGene<Item>> seq = ((PermutationChromosome<Item>) genotype.getChromosome()).toSeq();

		for (int i = 0; i < manifests.size(); i++) {
			Manifest m = manifests.get(i);
			List<EnumGene<Item>> genes = seq.subSeq(i, i + m.getVessel().getDimension().size).stream()
					.sorted(new ManifestComparitor()).collect(Collectors.toList());
			for (EnumGene<Item> gene : genes) {
				m.addItem(gene.getAllele());
			}
		}

		return manifests;
	}

	public List<Manifest> optimise(ManifestOptimsationFunction func) {
		ISeq<Item> seq = ISeq.of(store.allItems());

		PermutationChromosome<Item> c = PermutationChromosome.of(seq,
				vessels.stream().mapToInt((v) -> v.getDimension().size).sum());
		final Engine<EnumGene<Item>, Double> engine = Engine.builder(new ManifestFitnessFunction(vessels), c)
				.optimize(Optimize.MINIMUM).populationSize(500).maximalPhenotypeAge(70)
				.alterers(new SwapMutator<>(0.3), new PartiallyMatchedCrossover<>(0.5)).build();
		EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

		Phenotype<EnumGene<Item>, Double> best = engine.stream().limit(limit.bySteadyFitness(2000)).peek(statistics)
				.peek((r) -> func.apply(r.getGeneration(), statistics, create(r.getBestPhenotype().getGenotype())))
				.collect(toBestPhenotype());

		return create(best.getGenotype());
	}

}
