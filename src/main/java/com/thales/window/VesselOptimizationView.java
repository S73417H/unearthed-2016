package com.thales.window;

import com.thales.ga.ManifestOptimiser;
import com.thales.model.Manifest;
import com.thales.model.Priority;
import com.thales.model.Vessel;
import com.thales.utils.FXExecutor;
import com.thales.window.Manifest.FitnessView;
import com.thales.window.Manifest.GenerationView;
import com.thales.window.Manifest.ManifestView;
import com.thales.window.deckView.DeckView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jenetics.stat.DoubleMomentStatistics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 8/04/2016.
 */
public class VesselOptimizationView extends HBox {

	// TODO
//	final DeckView deckView = new DeckView();

	final TabPane boaties = new TabPane();



	final GenerationView generationView = new GenerationView();

	final FitnessView fitnessView = new FitnessView();

	final ManifestView manifestView = new ManifestView();

	private final ManifestOptimiser optimiser;

	Hashtable<Vessel, DeckView> boatiesTable = new Hashtable<>();


	//TODO
	Vessel v = Vessel.VESSEL2;

	public VesselOptimizationView(ManifestOptimiser optimiser, List<Vessel> vessels) {
		this.optimiser = optimiser;
		VBox leftBox = new VBox();

//		menuBar = new ColorMenubar(deckView);

		leftBox.getChildren().addAll(generationView, fitnessView, manifestView);

		// TODO
//		Vessel v = Vessel.VESSEL2;
		// TODO get from manifest

//		DeckView deckView = new DeckView();
//		boatiesTable.put(v, deckView);
//
//		Tab tab = new Tab();
//		tab.setText(v.getId());
//
//		tab.setContent(new ColorMenubar(deckView));
//		tab.setClosable(false);
//
//		InputStream icon = this.getClass().getResourceAsStream("/icon.png");
//
//		Image image = new Image(icon);
//		tab.setGraphic(new ImageView(image));
//
//		boaties.getTabs().addAll(tab);

		vessels.stream().forEach((v)->{createTab(v);});



		this.getChildren().addAll(leftBox, boaties);

	}

	private void createTab(Vessel v)
	{
		DeckView deckView = new DeckView(v);
		boatiesTable.put(v, deckView);

		Tab tab = new Tab();
		tab.setText(v.getId());

		tab.setContent(new ColorMenubar(deckView));
		tab.setClosable(false);

		InputStream icon = this.getClass().getResourceAsStream("/icon.png");

		Image image = new Image(icon);
		tab.setGraphic(new ImageView(image));

		boaties.getTabs().addAll(tab);
	}

	public void go() {
		optimiser.optimise((g, s, m) -> {
			FXExecutor.INSTANCE.execute(() -> {
				if (g % 5 == 0) {
					DoubleMomentStatistics fitness = (DoubleMomentStatistics) s.getFitness();
					fitnessView.addDataToQueue(fitness.getMin(), fitness.getMean());
//					generationView.setPriority(
//							m.getItems().stream().filter((item) -> item.getPriority().equals(Priority.HIGH)).count());
//					manifestView.update(m);
//					generationView.setBoxCount(m.getItems().size());
//					deckView.updateDeck(m);

					Manifest temp = new Manifest();
					for(Manifest ms : m) {

						boatiesTable.get(ms.getVessel()).updateDeck(ms);
						temp.getItems().addAll(ms.getItems());
					}





					manifestView.update(temp);
					generationView.setBoxCount(m.stream().mapToLong((x) -> x.getItems().size()).sum());
//					deckView.updateDeck(m.get(0));
				}

				generationView.setGeneration(g);
			});
		});
	}

}
