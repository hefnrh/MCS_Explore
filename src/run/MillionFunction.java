package run;

import java.util.Random;

import abstractmath.Field28;

public class MillionFunction {
	private int xFunction = 0, yFunction = 0;
	private int x = 0, y = 0;
	private int[] highRankXFunction, highRankYFunction;
	private float[] deviation;
	final Random r = new Random();
	final Field28 field = new Field28((byte) 0b1000_1101);
	final byte[][] matrix = { { 2, 3, 1, 1 }, { 1, 2, 3, 1 }, { 1, 1, 2, 3 },
			{ 3, 1, 1, 2 } };

	final private byte[] createA() { // create a0~a3

		x = r.nextInt();
		int integer = x;
		byte a[] = new byte[4];
		for (int i = 0; i < 4; ++i) {
			a[i] = (byte) integer;
			integer >>= 8;
		}
		return a;
	}

	final private void createY(byte[] a) {
		byte[] out = field.AESMixColumns(matrix, a);
		y = out[0] & 0b1111_1111;
		y |= (out[1] & 0b1111_1111) << 8;
		y |= (out[2] & 0b1111_1111) << 16;
		y |= (out[3] & 0b1111_1111) << 24;
	}

	final private void createFunctions() {
		xFunction = r.nextInt();
		yFunction = r.nextInt();
	}

	final public void displayx() {
		System.out.println("x = " + x);
	}

	final public void displayy() {
		System.out.println("y = " + y);
	}

	final public double calcDeviation(int runTime) {
		double simpleTime = 0;

		for (int i = 0; i < runTime; ++i) {
                        createY(createA());
			int answer0 = x & xFunction;
			int answer1 = y & yFunction;
			byte sign0 = 0,sign1 = 0;
			for (int j = 0; j < 32; ++j) {
				if((answer0 & 1) ==1) ++sign0;
    			        if((answer1 & 1) ==1) ++sign1;
				answer0 >>= 1;
				answer1 >>= 1;
			}
			if (((sign0 + sign1) & 1) == 1)
				++simpleTime;
		}
		return Math.abs(simpleTime / runTime - 0.5);
	}

	final void sort(double deviationx) {

		int i = 0;
		while (i < 10) {
			if (deviationx > deviation[i]) {
				for (int j = 9; j > i; --j) {
					highRankXFunction[j] = highRankXFunction[j - 1];
					highRankYFunction[j] = highRankYFunction[j - 1];
					deviation[j] = deviation[j - 1];
				}
				highRankXFunction[i] = xFunction;
				highRankYFunction[i] = yFunction;
				deviation[i] = (float) deviationx;
			}
			++i;
		}

	}

	final public void runFuctions(int runTime, int FunctionNmubers) {
		for (int k = 0; k < FunctionNmubers; ++k) {
			createFunctions();
			sort(calcDeviation(runTime));
		}

	}
}
