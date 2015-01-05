package fr.mla.swt.smart.viewer.color;


public class ColorModel {
	private int power;
	private int size;
	private float[] minValue = new float[] { 0f, 0.5f, 0.5f };
	private float[] maxValue = new float[] { 1f, 1f, 1f };
	private ColorDescriptor[] colors;
	private boolean dirty = true;

	public ColorModel() {
		setPower(5);
	}

	public void setPower(int power) {
		this.power = power;
		this.size = (int) Math.pow(2, power);
		dirty = true;
	}

	public int getPower() {
		return power;
	}

	public int getSize() {
		return size;
	}

	public void setMinValue(float h, float s, float b) {
		minValue[0] = h;
		minValue[1] = s;
		minValue[2] = b;
		dirty = true;
	}

	public void setMaxValue(float h, float s, float b) {
		maxValue[0] = h;
		maxValue[1] = s;
		maxValue[2] = b;
		dirty = true;
	}

	public float getMinH() {
		return minValue[0];
	}

	public float getMinS() {
		return minValue[1];
	}

	public float getMinB() {
		return minValue[2];
	}

	public float getMaxH() {
		return maxValue[0];
	}

	public float getMaxS() {
		return maxValue[1];
	}

	public float getMaxB() {
		return maxValue[2];
	}

	public float getRangeH() {
		return Math.abs(maxValue[0] - minValue[0]);
	}

	public float getRangeS() {
		return Math.abs(maxValue[1] - minValue[1]);
	}

	public float getRangeB() {
		return Math.abs(maxValue[2] - minValue[2]);
	}

	public ColorDescriptor[] getAllColors() {
		if (dirty) {
			dirty = false;
			colors = new ColorDescriptor[size];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = getColor(i);
			}
		}
		return colors;
	}

	public ColorDescriptor getColor(int i) {
		float h = getMinH();
		float s = getMinS();
		float b = getMinB();
		for (int p = 0; p < power; p++) {
			int c = (int) Math.pow(2, p);
			int c2 = (int) Math.pow(2, (power - p - 1));
			h += ((i / c) % 2) * getRangeH() / 2 / c;
			s += ((i / c2) % 2) * getRangeS() / 2 / c2;
			b += ((i / c2) % 2) * getRangeB() / 2 / c2;
		}
		return new ColorDescriptor(h, s, b);
	}
}
