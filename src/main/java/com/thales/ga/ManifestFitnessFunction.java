package com.thales.ga;

import java.util.List;
import java.util.function.Function;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.Genotype;
import org.jenetics.util.ISeq;

import com.thales.model.Item;
import com.thales.model.Urgency;
import com.thales.model.Vessel;

public class ManifestFitnessFunction implements Function<Genotype<EnumGene<Item>>, Double> {

	private final List<Vessel> vessels;

	public ManifestFitnessFunction(List<Vessel> vessels) {
		this.vessels = vessels;
	}

	@Override
	public Double apply(Genotype<EnumGene<Item>> gt) {
		ISeq<Chromosome<EnumGene<Item>>> seq = gt.toSeq();
		double tcost = 0;
		for (int i = 0; i < vessels.size(); i++) {
			Vessel vessel = vessels.get(i);
			List<EnumGene<Item>> genes = gt.getChromosome().toSeq().subSeq(i, i + vessel.getDimension().size).asList();
			double vcost = 0;
			for (EnumGene<Item> c : genes) {
				Item item = c.getAllele();
				
				double fact = item.getUrgency().getValue();
				vcost += fact * (vessel.checkDestination(item) ? 0.5 : 1);
				
			}
			tcost += vessel.getDimension().size * 100 / vcost;
		}

		return tcost * 100 / vessels.size();
	}

}
