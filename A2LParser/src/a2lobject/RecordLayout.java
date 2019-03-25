/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.ALIGNMENT_BYTE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT16_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT32_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT64_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_INT64;
import static constante.SecondaryKeywords.ALIGNMENT_LONG;
import static constante.SecondaryKeywords.ALIGNMENT_WORD;
import static constante.SecondaryKeywords.AXIS_PTS_X;
import static constante.SecondaryKeywords.AXIS_PTS_Y;
import static constante.SecondaryKeywords.NO_AXIS_PTS_X;
import static constante.SecondaryKeywords.NO_AXIS_PTS_Y;
import static constante.SecondaryKeywords.SRC_ADDR_X;
import static constante.SecondaryKeywords.FNC_VALUES;
import static constante.SecondaryKeywords.STATIC_RECORD_LAYOUT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import constante.AdressType;
import constante.DataType;
import constante.IndexMode;
import constante.IndexOrder;
import constante.SecondaryKeywords;

public final class RecordLayout {

	private String name;

	private Map<SecondaryKeywords, Object> optionalsParameters;

	public RecordLayout(List<String> parameters) {

		initOptionalsParameters();

		parameters.remove("/begin"); // Remove /begin
		parameters.remove("RECORD_LAYOUT"); // Remove RECORD_LAYOUT

		if (parameters.size() >= 1) {
			for (int n = 0; n < parameters.size(); n++) {
				switch (n) {
				case 0:
					this.name = parameters.get(n);
					break;
				default: // Cas de parametres optionels
					switch (parameters.get(n)) {
					case "FNC_VALUES":
						List<String> subList = parameters.subList(n + 1, n + 5);
						optionalsParameters.put(FNC_VALUES, new FncValues(subList));
						n += 4;
						break;
					case "AXIS_PTS_X":
						List<String> subList2 = parameters.subList(n + 1, n + 5);
						optionalsParameters.put(AXIS_PTS_X, new AxisPtsX(subList2));
						n += 4;
						break;
					case "AXIS_PTS_Y":
						List<String> subList4 = parameters.subList(n + 1, n + 5);
						optionalsParameters.put(AXIS_PTS_Y, new AxisPtsY(subList4));
						n += 4;
						break;
					case "NO_AXIS_PTS_X":
						List<String> subList3 = parameters.subList(n + 1, n + 3);
						optionalsParameters.put(NO_AXIS_PTS_X, new NoAxisPtsX(subList3));
						n += 2;
						break;
					case "NO_AXIS_PTS_Y":
						List<String> subList5 = parameters.subList(n + 1, n + 3);
						optionalsParameters.put(NO_AXIS_PTS_Y, new NoAxisPtsY(subList5));
						n += 2;
						break;
					case "SRC_ADDR_X":
						List<String> subList6 = parameters.subList(n + 1, n + 3);
						optionalsParameters.put(SRC_ADDR_X, new SrcAddrX(subList6));
						n += 2;
						break;
					default:
						break;
					}
					break;
				}
			}

			// On vide la MAP de parametre non utilise
			Iterator<Map.Entry<SecondaryKeywords, Object>> iter = optionalsParameters.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<SecondaryKeywords, Object> entry = iter.next();
				if (entry.getValue() == null) {
					iter.remove();
				}
			}

		} else {
			throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
		}

	}

	private final void initOptionalsParameters()
	{
		optionalsParameters = new HashMap<SecondaryKeywords, Object>();
		optionalsParameters.put(ALIGNMENT_BYTE, null);
		optionalsParameters.put(ALIGNMENT_FLOAT16_IEEE, null);
		optionalsParameters.put(ALIGNMENT_FLOAT32_IEEE, null);
		optionalsParameters.put(ALIGNMENT_FLOAT64_IEEE, null);
		optionalsParameters.put(ALIGNMENT_INT64, null);
		optionalsParameters.put(ALIGNMENT_LONG, null);
		optionalsParameters.put(ALIGNMENT_WORD, null);
		optionalsParameters.put(AXIS_PTS_X, null);
		optionalsParameters.put(AXIS_PTS_Y, null);
		optionalsParameters.put(FNC_VALUES, null);
		optionalsParameters.put(NO_AXIS_PTS_X, null);
		optionalsParameters.put(NO_AXIS_PTS_Y, null);
		optionalsParameters.put(SRC_ADDR_X, null);
		optionalsParameters.put(STATIC_RECORD_LAYOUT, null);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public FncValues getFncValues() {
		return (FncValues) optionalsParameters.get(FNC_VALUES);
	}

	public NoAxisPtsX getNoAxisPtsX() {
		Object object = optionalsParameters.get(NO_AXIS_PTS_X);
		return object != null ? (NoAxisPtsX) object : null;
	}

	public AxisPtsX getAxisPtsX() {
		Object object = optionalsParameters.get(AXIS_PTS_X);
		return object != null ? (AxisPtsX) object : null;
	}

	public NoAxisPtsY getNoAxisPtsY() {
		Object object = optionalsParameters.get(NO_AXIS_PTS_Y);
		return object != null ? (NoAxisPtsY) object : null;
	}

	public AxisPtsY getAxisPtsY() {
		Object object = optionalsParameters.get(AXIS_PTS_Y);
		return object != null ? (AxisPtsY) object : null;
	}
	
	public SrcAddrX getSrcAddrX() {
		Object object = optionalsParameters.get(SRC_ADDR_X);
		return object != null ? (SrcAddrX) object : null;
	}

	public final class FncValues {

		private int position;
		private DataType dataType;
		private IndexMode indexMode;
		private AdressType adressType;

		public FncValues(List<String> parameters) {
			this.position = Integer.parseInt(parameters.get(0));
			this.dataType = DataType.getDataType(parameters.get(1));
			this.indexMode = IndexMode.getIndexMode(parameters.get(2));
			this.adressType = AdressType.getAdressType(parameters.get(3));
		}

		public int getPosition() {
			return position;
		}

		public DataType getDataType() {
			return dataType;
		}

		public IndexMode getIndexMode() {
			return indexMode;
		}

		public AdressType getAdressType() {
			return adressType;
		}
	}

	public final class AxisPtsX {

		private int position;
		private DataType dataType;
		private IndexOrder indexOrder;
		private AdressType adressType;

		public AxisPtsX(List<String> parameters) {
			this.position = Integer.parseInt(parameters.get(0));
			this.dataType = DataType.getDataType(parameters.get(1));
			this.indexOrder = IndexOrder.getIndexOrder(parameters.get(2));
			this.adressType = AdressType.getAdressType(parameters.get(3));
		}

		public int getPosition() {
			return position;
		}

		public DataType getDataType() {
			return dataType;
		}

		public IndexOrder getIndexOrder() {
			return indexOrder;
		}

		public AdressType getAdressType() {
			return adressType;
		}
	}

	public final class NoAxisPtsX {
		private int position;
		private DataType dataType;

		public NoAxisPtsX(List<String> parameters) {
			this.position = Integer.parseInt(parameters.get(0));
			this.dataType = DataType.getDataType(parameters.get(1));
		}

		public int getPosition() {
			return position;
		}

		public DataType getDataType() {
			return dataType;
		}

	}

	public final class AxisPtsY {

		private int position;
		private DataType dataType;
		private IndexOrder indexOrder;
		private AdressType adressType;

		public AxisPtsY(List<String> parameters) {
			this.position = Integer.parseInt(parameters.get(0));
			this.dataType = DataType.getDataType(parameters.get(1));
			this.indexOrder = IndexOrder.getIndexOrder(parameters.get(2));
			this.adressType = AdressType.getAdressType(parameters.get(3));
		}

		public int getPosition() {
			return position;
		}

		public DataType getDataType() {
			return dataType;
		}

		public IndexOrder getIndexOrder() {
			return indexOrder;
		}

		public AdressType getAdressType() {
			return adressType;
		}
	}

	public final class NoAxisPtsY {
		private int position;
		private DataType dataType;

		public NoAxisPtsY(List<String> parameters) {
			this.position = Integer.parseInt(parameters.get(0));
			this.dataType = DataType.getDataType(parameters.get(1));
		}

		public int getPosition() {
			return position;
		}

		public DataType getDataType() {
			return dataType;
		}

	}
	
	public final class SrcAddrX {
		private int position;
		private DataType dataType;

		public SrcAddrX(List<String> parameters) {
			this.position = Integer.parseInt(parameters.get(0));
			this.dataType = DataType.getDataType(parameters.get(1));
		}

		public int getPosition() {
			return position;
		}

		public DataType getDataType() {
			return dataType;
		}

	}

}
