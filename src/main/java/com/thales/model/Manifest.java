package com.thales.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manifest {

	private final List<Item> items = Collections.synchronizedList(new ArrayList<Item>());

	private final Vessel vessel;

	public Manifest(Vessel vessel) {
		this.vessel = checkNotNull(vessel);
	}

	public Manifest()
	{
		vessel = null;
	}


	public Vessel getVessel() {
		return vessel;
	}

	public void addItem(Item item) {
		items.add(checkNotNull(item));
	}

	public Item getPosition(int x, int y) {
		int index = checkPositionIndex(vessel.getDimension().width * y + x, vessel.getDimension().size);
		return items.get(index);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Item item : items) {
			builder.append(item.getName());
			builder.append(":");
			builder.append(item.getPriority());
			builder.append(":");
			builder.append(item.getUrgency());
			builder.append('\n');
		}
		return builder.toString();
	}

	public List<Item> getItems() {
		return items;
	}
}
