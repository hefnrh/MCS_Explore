import abstractmath.Field28;

public class Test {
	public static void main(String[] args) {
		byte b = 0b001110;
		byte p = 0b0001110;
		p |= -128;
		Field28 field = new Field28(p);
		System.out.println("p = " + toBin(p));
		System.out.println("b = " + toBin(b));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; ++i) {
			b = field.multiply(b, (byte) -1);
		}
		long end = System.currentTimeMillis();
		System.out.println("after multiply b = " + toBin(b));
		System.out.println("time used for 100000 mutiply: " + (end - start) + " ms");
	}
	
	public static final String toBin(byte b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; ++i) {
			sb.append((b >> (7 - i)) & 1);
		}
		return sb.toString();
	}
}
