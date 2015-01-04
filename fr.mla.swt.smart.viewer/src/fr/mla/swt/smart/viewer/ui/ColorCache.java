package fr.mla.swt.smart.viewer.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorCache {
	private final Map<String, Color> map = new HashMap<String, Color>();
	private Display display;

	public ColorCache(Display display) {
		this.display = display;
	}

	public Color getColor(int r, int g, int b) {
		String key = String.format("#%02X%02X%02X", r, g, b);
		Color color = map.get(key);
		if (color == null) {
			color = new Color(display, r, g, b);
			map.put(key, color);
		}
		return color;
	}

	public Color getColor(float h, float s, float b) {
		java.awt.Color hsbColor = java.awt.Color.getHSBColor(h, s, b);
		return getColor(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue());
	}

	public void dispose() {
		for (Color color : map.values()) {
			color.dispose();
		}
		map.clear();
	}
}
