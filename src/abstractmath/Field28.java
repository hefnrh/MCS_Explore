package abstractmath;

public final class Field28 {

	private final byte p;

	/**
	 * @param identifier
	 *            an 8-bit number identified the first eight coefficients of the
	 *            prime polynomial
	 */
	public Field28(final byte identifier) {
		this.p = identifier;
	}

	/**
	 * @param a
	 * @param b
	 * @return (a * b) % identifier in GF(2^8)
	 */
	public final byte multiply(final byte a, final byte b) {
		byte ret = 0, tmp;
		for (int i = 0, j; i < 8; ++i) {
			if (((b >> i) & 1) == 0)
				continue;
			tmp = a;
			for (j = 0; j < i; ++j)
				tmp = rotateLeft(tmp);
			ret ^= tmp;
		}
		return ret;
	}

	private final byte rotateLeft(byte a) {
		byte ret = a;
		a = (byte) (((a & 0b1000_0000) == 0) ? 0 : -1);
		ret ^= (a & p);
		ret <<= 1;
		ret |= (a & 0b1);
		return ret;
	}

	public final byte getIdentifier() {
		return p;
	}

	public final byte[] AESMixColumns(final byte[][] matrix, final byte[] input) {
		byte[] output = new byte[4];
		output[0] = (byte) (multiply(matrix[0][0], input[0])
				^ multiply(matrix[0][1], input[1])
				^ multiply(matrix[0][2], input[2]) ^ multiply(matrix[0][3],
				input[3]));
		output[1] = (byte) (multiply(matrix[1][0], input[0])
				^ multiply(matrix[1][1], input[1])
				^ multiply(matrix[1][2], input[2]) ^ multiply(matrix[1][3],
				input[3]));
		output[2] = (byte) (multiply(matrix[2][0], input[0])
				^ multiply(matrix[2][1], input[1])
				^ multiply(matrix[2][2], input[2]) ^ multiply(matrix[2][3],
				input[3]));
		output[3] = (byte) (multiply(matrix[3][0], input[0])
				^ multiply(matrix[3][1], input[1])
				^ multiply(matrix[3][2], input[2]) ^ multiply(matrix[3][3],
				input[3]));
		return output;
	}
}
