package fr.mla.swt.smart.viewer.test;

import fr.mla.swt.smart.viewer.color.ColorModel;
import fr.mla.swt.smart.viewer.group.DataGroup;

public class FileGroupFactory {

	private int index = 0;
	ColorModel model = new ColorModel();

	public DataGroup newGroup() {
		return new DataGroup(String.valueOf(index), model.getColor(index++));
	}

}
