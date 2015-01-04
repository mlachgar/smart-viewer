package fr.mla.swt.smart.viewer.test;

public class ColorModel {
	private int power;
	private int size;
	private float[] minValue = new float[] { 0f, 0.3f, 0.3f };
	private float[] maxValue = new float[] { 1f, 0.8f, 0.8f };
	private GroupColor[] colors;
	private boolean dirty = true;

	public ColorModel() {
		setPower(4);
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

	public GroupColor[] getColors() {
		if (dirty) {
			dirty = false;
			colors = new GroupColor[size];
			for (int i = 0; i < colors.length; i++) {
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
				colors[i] = new GroupColor(h, s, b);
			}
		}
		return colors;
	}

}
