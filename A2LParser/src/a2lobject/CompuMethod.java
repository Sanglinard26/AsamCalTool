/*
 * Creation : 3 janv. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.COEFFS;
import static constante.SecondaryKeywords.COEFFS_LINEAR;
import static constante.SecondaryKeywords.COMPU_TAB_REF;
import static constante.SecondaryKeywords.FORMULA;
import static constante.SecondaryKeywords.REF_UNIT;
import static constante.SecondaryKeywords.STATUS_STRING_REF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class CompuMethod implements Comparable<CompuMethod> {

	private String name;
	@SuppressWarnings("unused")
	private String longIdentifier;
	private ConversionType conversionType;
	private String format;
	@SuppressWarnings("unused")
	private String unit;

	private Map<SecondaryKeywords, Object> optionalsParameters;

	public CompuMethod(List<String> parameters) {

		initOptionalsParameters();

		parameters.remove("/begin"); // Remove /begin
		parameters.remove("COMPU_METHOD"); // Remove COMPU_METHOD

		if (parameters.size() == 1 || parameters.size() >= 5) {
			for (int n = 0; n < parameters.size(); n++) {
				switch (n) {
				case 0:
					this.name = parameters.get(n);
					break;
				case 1:
					this.longIdentifier = parameters.get(n);
					break;
				case 2:
					this.conversionType = ConversionType.getConversionType(parameters.get(n));
					break;
				case 3:
					this.format = parameters.get(n);
					break;
				case 4:
					this.unit = parameters.get(n);
					break;

				default: // Cas de parametres optionels

				Set<SecondaryKeywords> keys = optionalsParameters.keySet();
				for (int nPar = n; nPar < parameters.size(); nPar++) {
					if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
						switch (parameters.get(nPar)) {
						case "COEFFS": // 6 coeffs
							optionalsParameters.put(COEFFS, new Coeffs(parameters.subList(nPar + 1, nPar + 7)));
							break;
						case "COEFFS_LINEAR": // 2 coeffs
							optionalsParameters.put(COEFFS_LINEAR, new CoeffsLinear(parameters.subList(nPar + 1, nPar + 3)));
							break;
						case "COMPU_TAB_REF":
							optionalsParameters.put(COMPU_TAB_REF, parameters.get(nPar + 1));
							break;
						case "FORMULA":
							break;

						default:
							break;
						}
					}
				}
				n = parameters.size();
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
		optionalsParameters.put(COEFFS, null);
		optionalsParameters.put(COEFFS_LINEAR, null);
		optionalsParameters.put(COMPU_TAB_REF, null); // ToDo
		optionalsParameters.put(FORMULA, null); // ToDo
		optionalsParameters.put(REF_UNIT, null); // ToDo
		optionalsParameters.put(STATUS_STRING_REF, null); // ToDo
	}

	@Override
	public String toString() {
		return this.name;
	}

	public final void assignCompuVTab(HashMap<String, CompuVTab> compuVTabs) {

		String compuTabRef = (String) this.optionalsParameters.get(COMPU_TAB_REF);
		this.optionalsParameters.put(COMPU_TAB_REF, compuVTabs.get(compuTabRef));
	}

	public final double compute(double hex) {

		float[] _coeffs;

		switch (this.conversionType) {
		case IDENTICAL:
			return hex;
		case FORM:
			return hex;
		case LINEAR:
			CoeffsLinear coeffsLinear = (CoeffsLinear) this.optionalsParameters.get(COEFFS_LINEAR);
			_coeffs = coeffsLinear.getCoeffs();
			return (_coeffs[0] * hex) - _coeffs[1];
		case RAT_FUNC:
			Coeffs coeffs = (Coeffs) this.optionalsParameters.get(COEFFS);
			_coeffs = coeffs.getCoeffs();
			if (_coeffs[0] + _coeffs[2] + _coeffs[3] + _coeffs[4] == 0) {
				return (hex * _coeffs[5]) / _coeffs[1];
			}
			if (_coeffs[0] + _coeffs[3] + _coeffs[4] == 0) {
				return (hex * _coeffs[5] - _coeffs[2]) / _coeffs[1];
			}
			if (_coeffs[0] + _coeffs[1] + _coeffs[3] + _coeffs[5] == 0) {
				return _coeffs[2] / (hex * _coeffs[4]);
			}
			return Double.NaN;
		default:
			return hex;
		}
	}

	public final String computeString(double hex) {

		Object compuTabRef = this.optionalsParameters.get(COMPU_TAB_REF);

		switch (this.conversionType) {
		case TAB_VERB:
			CompuVTab compuVTab = (CompuVTab) compuTabRef;
			Float key = new Float(hex);
			return compuVTab.getValuePairs().get(key);
		default:
			return "";
		}
	}

	public final ConversionType getConversionType() {
		return conversionType;
	}

	public final Map<SecondaryKeywords, Object> getOptionalsParameters() {
		return optionalsParameters;
	}

	public final static CompuMethod createEmptyCompuMethod(String name) {
		List<String> parameters = new ArrayList<String>();
		parameters.add(name);
		return new CompuMethod(parameters);
	}

	@Override
	public boolean equals(Object obj) {
		return this.name.equals(obj.toString());
	}

	public final String getFormat() {
		return format;
	}

	@Override
	public int compareTo(CompuMethod o) {
		return this.name.compareTo(o.name);
	}

	public final class Coeffs {

		// INT = f(PHYS), f(x) = (axx + bx + c) / (dxx + ex + f)

		private float[] coeffs;

		public Coeffs(List<String> params) {
			this.coeffs = new float[6];
			for (int i = 0; i < coeffs.length; i++) {
				this.coeffs[i] = Float.parseFloat(params.get(i));
			}
		}

		public final float[] getCoeffs() {
			return coeffs;
		}
	}

	private final class CoeffsLinear {

		// PHYS = f(INT), f(x) = ax + b

		private float[] coeffs;

		public CoeffsLinear(List<String> params) {
			this.coeffs = new float[2];
			for (int i = 0; i < coeffs.length; i++) {
				this.coeffs[i] = Float.parseFloat(params.get(i));
			}
		}

		public final float[] getCoeffs() {
			return coeffs;
		}
	}

}
