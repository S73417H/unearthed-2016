package com.thales.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

public final class Vessel {
	//
	// private static final CargoValidator DEFAULT_VALIDATOR = new
	// DefaultCargoValidator();

	public static final Vessel VESSEL16 = new Vessel("Vessel 16", new Dimension(14, 50), Destination.values());
	public static final Vessel VESSEL7 = new Vessel("Vessel 7", new Dimension(10, 16), Destination.AU05, Destination.AU17,
			Destination.AU18, Destination.AU21);
	public static final Vessel VESSEL2 = new Vessel("Vessel 2", new Dimension(11, 19), Destination.AU04, Destination.AU21,
			Destination.AU21);

	public static final Vessel[] VESSELS = { VESSEL16, VESSEL7, VESSEL2 };

	private final String id;

	private final Dimension dimension;

	private final List<Destination> destinations;

	private Vessel(String id, Dimension dimension, Destination... destinations) {
		this.id = checkNotNull(id);
		this.dimension = checkNotNull(dimension);
		this.destinations = Arrays.asList(destinations);
	}

	public String getId() {
		return id;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public static class Dimension {

		public final int width;
		public final int height;
		public final int size;

		public Dimension(int width, int height) {
			this.width = width;
			this.height = height;
			size = width * height;
		}

	}

	public boolean checkDestination(Item item) {
		return destinations.contains(item.getDestination());
	}

	public boolean check(Item item, int x, int y) {

		// Use the dimensions of the vessel to determine the pathway down the
		// centre of the deck.
		// This is a 2m wide pathway (should be at least 1.5m according to
		// documentation).
		int invalidX_centre = getDimension().width / 2;
		if (x == invalidX_centre || x == (invalidX_centre - 1))
			return false;

		// Use the dimensions of the vessel to determine a pathway across the
		// centre of the deck.
		// This will be a 1m wide pathway to reach the safehavens on the sides
		// of the deck.
		int invalidY_centre = getDimension().height / 2;
		if (y == invalidY_centre)
			return false;

		// Invalid y values for safehaven on right hand side of deck
		int invalidX_right = getDimension().width - 1;
		if (x == invalidX_right)
			return false;

		// Invalid y values for safehaven on left hand side of deck
		int invalidX_left = 0;
		if (x == invalidX_left)
			return false;

		// If an item is destined for AU17 then we should not place it in the
		// lower right hand quadrant
		// of the deck space (starboard aft)
		if (item.getDestination() == Destination.AU17 && x > (getDimension().width / 2)
				&& y > (getDimension().height / 2))
			return false;

		return true;
	}

}
