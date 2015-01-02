package fr.mla.swt.smart.viewer.test;

public class FileGroupFactory {

	private int index = 0;
	private final int maxColors = 100;
	private final int kinds = 8;

	public FileGroup newGroup() {
		index++;
		return new FileGroup(String.valueOf(index), newColor());
	}

	private GroupColor newColor() {
		float h = index * 1f / maxColors + (index * 0.5f);
		java.awt.Color c = java.awt.Color.getHSBColor(h, 1f, 1f);
		return new GroupColor(c.getRed(), c.getGreen(), c.getBlue());
	}

}
