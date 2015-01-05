package fr.mla.swt.smart.viewer.color;

public class ColorDescriptor {
	public final int red;
	public final int green;
	public final int blue;
	public final float hue;
	public final float saturation;
	public final float value;

	public ColorDescriptor(float h, float s, float v) {
		this.hue = h;
		this.saturation = s;
		this.value = v;
		java.awt.Color c = java.awt.Color.getHSBColor(h, s, v);
		this.red = c.getRed();
		this.green = c.getGreen();
		this.blue = c.getBlue();
	}

	@Override
	public String toString() {
		return String.format("rgb(%d,%d,%d) - hsv(%.2f:%.2f:%.2f)", red, green, blue, hue, saturation, value);
	}

}
