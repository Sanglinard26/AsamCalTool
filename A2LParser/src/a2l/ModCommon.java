/*
 * Creation : 20 févr. 2019
 */
package a2l;

import static constante.SecondaryKeywords.ALIGNMENT_BYTE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT16_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT32_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_FLOAT64_IEEE;
import static constante.SecondaryKeywords.ALIGNMENT_INT64;
import static constante.SecondaryKeywords.ALIGNMENT_LONG;
import static constante.SecondaryKeywords.ALIGNMENT_WORD;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.DATA_SIZE;
import static constante.SecondaryKeywords.DEPOSIT;

import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.DataType;
import constante.SecondaryKeywords;

public final class ModCommon implements A2lObject {

	private String comment;

	private Map<SecondaryKeywords, Object> optionalsParameters;

	public ModCommon(List<String> parameters, int beginLine, int endLine) {

		initOptionalsParameters();

		build(parameters, beginLine, endLine);
	}

	private final void initOptionalsParameters() {
		optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
		optionalsParameters.put(ALIGNMENT_BYTE, 1);
		optionalsParameters.put(ALIGNMENT_FLOAT16_IEEE, 2);
		optionalsParameters.put(ALIGNMENT_FLOAT32_IEEE, 4);
		optionalsParameters.put(ALIGNMENT_FLOAT64_IEEE, 8);
		optionalsParameters.put(ALIGNMENT_INT64, 8);
		optionalsParameters.put(ALIGNMENT_LONG, 4);
		optionalsParameters.put(ALIGNMENT_WORD, 2);
		optionalsParameters.put(BYTE_ORDER, "MSB_LAST"); // If this optional parameter is not declared, MSB_LAST (Intel format) is used as a default
		optionalsParameters.put(DATA_SIZE, null);
		optionalsParameters.put(DEPOSIT, null);
	}

	public final int getAlignment(DataType dataType) {

		switch (dataType) {
		case UBYTE:
			return (int) optionalsParameters.get(ALIGNMENT_BYTE);
		case SBYTE:
			return (int) optionalsParameters.get(ALIGNMENT_BYTE);
		case UWORD:
			return (int) optionalsParameters.get(ALIGNMENT_WORD);
		case SWORD:
			return (int) optionalsParameters.get(ALIGNMENT_WORD);
		case ULONG:
			return (int) optionalsParameters.get(ALIGNMENT_LONG);
		case SLONG:
			return (int) optionalsParameters.get(ALIGNMENT_LONG);
		case FLOAT16_IEEE:
			return (int) optionalsParameters.get(ALIGNMENT_FLOAT16_IEEE);
		case FLOAT32_IEEE:
			return (int) optionalsParameters.get(ALIGNMENT_FLOAT32_IEEE);
		case FLOAT64_IEEE:
			return (int) optionalsParameters.get(ALIGNMENT_FLOAT64_IEEE);
		case A_UINT64:
			return (int) optionalsParameters.get(ALIGNMENT_INT64);
		case A_INT64:
			return (int) optionalsParameters.get(ALIGNMENT_INT64);
		default:
			return 0;
		}
	}

	public final ByteOrder getByteOrder() {
		String sByteOrder = (String) optionalsParameters.get(BYTE_ORDER);
		if ("MSB_LAST".equals(sByteOrder) || "BIG_ENDIAN".equals(sByteOrder)) {
			return ByteOrder.LITTLE_ENDIAN;
		}
		return ByteOrder.BIG_ENDIAN;
	}

	public final String getComment() {
		return this.comment;
	}

	@Override
	public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

		final int nbParams = parameters.size();

		if (nbParams >= 1) {
			for (int n = 2; n < nbParams; n++) {
				switch (n) {
				case 2:
					this.comment = parameters.get(n);
					break;
				default: // Cas de parametres optionels
				Set<SecondaryKeywords> keys = optionalsParameters.keySet();
				for (int nPar = n; nPar < nbParams; nPar++) {
					if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
						switch (parameters.get(nPar)) {
						case "ALIGNMENT_BYTE":
							optionalsParameters.put(ALIGNMENT_BYTE, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "ALIGNMENT_FLOAT16_IEEE":
							optionalsParameters.put(ALIGNMENT_FLOAT16_IEEE, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "ALIGNMENT_FLOAT32_IEEE":
							optionalsParameters.put(ALIGNMENT_FLOAT32_IEEE, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "ALIGNMENT_FLOAT64_IEEE":
							optionalsParameters.put(ALIGNMENT_FLOAT64_IEEE, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "ALIGNMENT_INT64":
							optionalsParameters.put(ALIGNMENT_INT64, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "ALIGNMENT_LONG":
							optionalsParameters.put(ALIGNMENT_LONG, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "ALIGNMENT_WORD":
							optionalsParameters.put(ALIGNMENT_WORD, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "BYTE_ORDER":
							optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
							break;
						case "DATA_SIZE":
							optionalsParameters.put(DATA_SIZE, Integer.parseInt(parameters.get(nPar + 1)));
							break;
						case "DEPOSIT":
							optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
							break;
						default:
							break;
						}
					}
				}
				n = nbParams;
				break;
				}
			}

		} else {
			throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
		}

	}

	@Override
	public String getProperties() {
		StringBuilder sb = new StringBuilder("...");

		return sb.toString();
	}

}
