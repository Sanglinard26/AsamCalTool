/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.AXIS_PTS_REF;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CURVE_AXIS_REF;
import static constante.SecondaryKeywords.DEPOSIT;
import static constante.SecondaryKeywords.FIX_AXIS_PAR;
import static constante.SecondaryKeywords.FIX_AXIS_PAR_DIST;
import static constante.SecondaryKeywords.FIX_AXIS_PAR_LIST;
import static constante.SecondaryKeywords.FORMAT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class AxisDescr {

	private Attribute attribute;
	@SuppressWarnings("unused")
	private String inputQuantity;
	private String conversion;
	private int maxAxisPoints;
	@SuppressWarnings("unused")
	private float lowerLimit;
	@SuppressWarnings("unused")
	private float upperLimit;

	private CompuMethod compuMethod;
	private RecordLayout recordLayout;
	private AdjustableObject axisPts;
	private AdjustableObject curveAxis;

	private Map<SecondaryKeywords, Object> optionalsParameters;

	public AxisDescr(List<String> parameters) {

		initOptionalsParameters();

		if (parameters.size() == 1 || parameters.size() >= 6) {

			this.attribute = Attribute.getAttribute(parameters.get(0));
			this.inputQuantity = parameters.get(1);
			this.conversion = parameters.get(2);
			this.maxAxisPoints = Integer.parseInt(parameters.get(3));
			this.lowerLimit = Float.parseFloat(parameters.get(4));
			this.upperLimit = Float.parseFloat(parameters.get(5));

			int n = 6;
			
			Set<SecondaryKeywords> keys = optionalsParameters.keySet();
			for (int nPar = n; nPar < parameters.size(); nPar++) {
				if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
					switch (parameters.get(nPar)) {
					case "AXIS_PTS_REF":
						optionalsParameters.put(AXIS_PTS_REF, parameters.get(nPar + 1));
						nPar+=1;
						break;
					case "CURVE_AXIS_REF":
						optionalsParameters.put(CURVE_AXIS_REF, parameters.get(nPar + 1));
						nPar+=1;
						break;
					case "DEPOSIT":
						optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
						nPar+=1;
						break;
					case "FIX_AXIS_PAR":
						n = nPar + 1;
						optionalsParameters.put(FIX_AXIS_PAR, new FixAxisPar(parameters.subList(n, n + 3)));
						nPar+=3;
						break;
					case "FIX_AXIS_PAR_DIST":
						n = nPar + 1;
						optionalsParameters.put(FIX_AXIS_PAR_DIST, new FixAxisParDist(parameters.subList(n, n + 3)));
						nPar+=3;
						break;
					case "FIX_AXIS_PAR_LIST":
						n = nPar + 1;
						do {
						} while (!parameters.get(++nPar).equals("FIX_AXIS_PAR_LIST"));
						optionalsParameters.put(FIX_AXIS_PAR_LIST, new FixAxisParList(parameters.subList(n, nPar - 1)));
						n = nPar + 1;
						break;
					case "FORMAT":
                        optionalsParameters.put(FORMAT, parameters.get(nPar + 1) + "f");
                        nPar+=1;
                        break;
					default:
						break;
					}
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

	private final void initOptionalsParameters() {
		optionalsParameters = new HashMap<SecondaryKeywords, Object>();
		optionalsParameters.put(AXIS_PTS_REF, null);
		optionalsParameters.put(CURVE_AXIS_REF, null);
		optionalsParameters.put(DEPOSIT, null);
		optionalsParameters.put(BYTE_ORDER, null);
		optionalsParameters.put(FIX_AXIS_PAR, null);
		optionalsParameters.put(FIX_AXIS_PAR_DIST, null);
		optionalsParameters.put(FIX_AXIS_PAR_LIST, null);
		optionalsParameters.put(FORMAT, null);
	}

	public final Attribute getAttribute() {
		return attribute;
	}

	public final void setCompuMethod(CompuMethod compuMethod) {
		this.compuMethod = compuMethod;
	}

	public final void setAxisPts(AdjustableObject axisPts) {
		this.axisPts = axisPts;
	}

	public final AdjustableObject getAxisPts() {
		return axisPts;
	}

	public final void setCurveAxis(AdjustableObject adjustableObject) {
		this.curveAxis = adjustableObject;
	}

	public final AdjustableObject getCurveAxis() {
		return curveAxis;
	}

	public final String getConversion() {
		return conversion;
	}

	public final CompuMethod getCompuMethod() {
		return compuMethod;
	}

	public final RecordLayout getRecordLayout() {
		return recordLayout;
	}

	public final int getMaxAxisPoints() {
		return maxAxisPoints;
	}

	public final String getDepositMode() {
		Object oDeposit = optionalsParameters.get(DEPOSIT);
		return oDeposit != null ? oDeposit.toString() : "";
	}

	public Map<SecondaryKeywords, Object> getOptionalsParameters() {
		return optionalsParameters;
	}
	
	public final String getFormat() {
        Object objectDisplayFormat = optionalsParameters.get(FORMAT);
        String displayFormat;

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {
            if (objectDisplayFormat == null) {
                displayFormat = compuMethod.getFormat();
            } else {
                displayFormat = objectDisplayFormat.toString();
            }
            if (displayFormat.charAt(1) == '0') {
                displayFormat = displayFormat.replaceFirst("0", "");
            }
            return displayFormat;
        }
        return "%16.16f";
    }

	public enum Attribute {
		CURVE_AXIS, COM_AXIS, FIX_AXIS, RES_AXIS, STD_AXIS, UNKNOWN;

		public static Attribute getAttribute(String name) {
			switch (name) {
			case "CURVE_AXIS":
				return CURVE_AXIS;
			case "COM_AXIS":
				return COM_AXIS;
			case "FIX_AXIS":
				return FIX_AXIS;
			case "RES_AXIS":
				return RES_AXIS;
			case "STD_AXIS":
				return STD_AXIS;
			default:
				return UNKNOWN;
			}
		}
	}
}
