package abstractmath;

public final class Field28 {
	
	private final byte p;
	
	/**
	 * @param identifier an 8-bit number identified the first eight coefficients of
	 * the prime polynomial
	 */
	public Field28(byte identifier) {
		this.p = identifier;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return (a * b) % identifier in GF(2^8)
	 */
	public final byte multiply(byte a, byte b) {
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
		a = (byte) ((((a >> 7) & 0b1) == 0) ? 0b0 : -0b1);
		ret ^= (a & p);
		ret <<= 1;
		ret |= ((a >> 7) & 0b1);
		return ret;
	}
	
	public final byte getIdentifier() {
		return p;
	}
}
