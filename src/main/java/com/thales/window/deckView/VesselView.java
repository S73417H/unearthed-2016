package com.thales.window.deckView;

import com.thales.model.Vessel;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * @author Will Crisp
 */
public class VesselView extends Xform{

  public VesselView(Vessel v)//Vessel vesselToShow)
  {
    final PhongMaterial material = new PhongMaterial(Color.LIGHTGREY);

    final Box vessel = new Box(v.getDimension().width, v.getDimension().height,1);

    vessel.setMaterial(material);

    this.getChildren().addAll(vessel);
  }
}
