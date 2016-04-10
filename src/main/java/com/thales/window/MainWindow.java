package com.thales.window;

import com.thales.ga.ManifestOptimiser;
import com.thales.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jenetics.util.RandomRegistry;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class MainWindow extends Application {

	final Pane mainPane = new StackPane();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Loady Boat");

		Scene scene = new Scene(mainPane);

		InputStream icon = this.getClass().getResourceAsStream("/icon.png");

		Image image = new Image(icon);
		primaryStage.getIcons().add(image);
		// Generate a random collection of store items.

		List<Vessel> vessels = Arrays.asList(Vessel.VESSELS);

		Store store = new Store();
		
		for (int i = 0; i < 10000; i++) {
			int p = RandomRegistry.getRandom().nextInt(Priority.values().length);
			int u = RandomRegistry.getRandom().nextInt(Urgency.values().length);
			int d = RandomRegistry.getRandom().nextInt(Destination.values().length);
			store.addItem(new Item("Item " + i, Priority.values()[p], Urgency.values()[u], Destination.values()[d]));
		}

		ManifestOptimiser optimiser = new ManifestOptimiser(store, vessels);
		VesselOptimizationView view = new VesselOptimizationView(optimiser, vessels);

		mainPane.getChildren().add(view);

//		view.setStyle("-fx-background-color: #E0EEEE;");
		view.setStyle("-fx-background-color: linear-gradient(from 15% 15% to 100% 100%, ##FFFFFF, #E0EEEE);");
		primaryStage.setScene(scene);
//		primaryStage.setFullScreen(true);
		primaryStage.show();


		primaryStage.setOnCloseRequest((a) -> {
			System.exit(0);

		});

		Executors.newSingleThreadExecutor().execute(() -> view.go());
	}
}
