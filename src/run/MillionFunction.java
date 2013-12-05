package run;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import abstractmath.Field28;

final public class MillionFunction implements Runnable {
	private int xFunction = 0, yFunction = 0;
	final Object lock;
	final Random r;
	final CountDownLatch cdl;
	private int x = 0, y = 0;
	final private int[] highRankXFunction, highRankYFunction;
	final private double[] deviation;
	final Field28 field = new Field28((byte) 0b1000_1101);
	final byte[][] matrix = { { 2, 3, 1, 1 }, { 1, 2, 3, 1 }, { 1, 1, 2, 3 },
			{ 3, 1, 1, 2 } };
	final int runTime;
	final int FunctionNumbers;
	final Object rLock;

	public MillionFunction(final int[] hrxf, final int[] hryf,
			final double[] d, final Object lock, final CountDownLatch cdl,
			final int runTime, final int FunctionNumbers, final Random r,
			final Object rLock) {
		highRankXFunction = hrxf;
		highRankYFunction = hryf;
		deviation = d;
		this.lock = lock;
		this.cdl = cdl;
		this.runTime = runTime;
		this.FunctionNumbers = FunctionNumbers;
		this.r = r;
		this.rLock = rLock;
	}

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

	final private void createY(final byte[] a) {
		final byte[] out = field.AESMixColumns(matrix, a);
		y = out[0] & 0b1111_1111;
		y |= (out[1] & 0b1111_1111) << 8;
		y |= (out[2] & 0b1111_1111) << 16;
		y |= (out[3] & 0b1111_1111) << 24;
	}

	final private void createFunctions(final int k) {
		if (/*k > 31*/ true) {
			xFunction = r.nextInt();
			yFunction = r.nextInt();
		} else {
			yFunction = 1;
			yFunction <<= k;
			switch (k / 8) {
			case 0:
				if (k % 8 == 0) {
					xFunction = 0b00000001000000010000000100000000;
				} else {
					xFunction = 0b00000010000000100000001100000001;
					xFunction <<= ((k % 8) - 1);
				}
				break;
			case 1:
				if (k % 8 == 0) {
					xFunction = 0b00000001000000010000000000000001;
				} else {
					xFunction = 0b00000010000000110000000100000001;
					xFunction <<= ((k % 8) - 1);
				}
				break;
			case 2:
				if (k % 8 == 0) {
					xFunction = 0b00000001000000000000000100000001;
				} else {
					xFunction = 0b00000011000000100000000100000001;
					xFunction <<= ((k % 8) - 1);
				}
				break;
			case 3:
				if (k % 8 == 0) {
					xFunction = 0b00000000000000010000000100000001;
				} else {
					xFunction = 0b00000001000000100000001000000011;
					xFunction <<= ((k % 8) - 1);
				}
				break;
			default:
				break;
			}
		}
	}

	final public void displayx() {
		System.out.println("x = " + x);
	}

	final public void displayy() {
		System.out.println("y = " + y);
	}

	final public double calcDeviation(final int runTime) {
		double simpleTime = 0;

		for (int i = 0; i < runTime; ++i) {

			int answer0 = x ^ xFunction;
			int answer1 = y ^ yFunction;
			byte sign0 = 0, sign1 = 0;
			for (int j = 0; j < 32; ++j) {
				if ((answer0 & 1) == 1)
					sign0 ^= 1;
				if ((answer1 & 1) == 1)
					sign1 ^= 1;
				answer0 >>= 1;
				answer1 >>= 1;
			}
			if (((sign1 ^ sign0) & 1) == 1)
				++simpleTime;
		}
		return Math.abs(((double)simpleTime) / ((double) runTime) - 0.5);
	}

	final public void sort(final double deviationx) {
		int indexToInsert;
		int i;
		synchronized (lock) {
			indexToInsert = Arrays.binarySearch(deviation, deviationx);
			if (indexToInsert == -1 || indexToInsert == 0)
				return;
			if (indexToInsert < 0)
				indexToInsert = -indexToInsert - 2;
			else {
				while (indexToInsert > 0
						&& deviation[indexToInsert - 1] == deviationx)
					--indexToInsert;
				--indexToInsert;
				if (indexToInsert < 0)
					return;
			}
			for (i = 0; i < indexToInsert; ++i) {
				deviation[i] = deviation[i + 1];
				highRankXFunction[i] = highRankXFunction[i + 1];
				highRankYFunction[i] = highRankYFunction[i + 1];
			}
			deviation[indexToInsert] = deviationx;
			highRankXFunction[indexToInsert] = xFunction;
			highRankYFunction[indexToInsert] = yFunction;
		}
	}

	final public void runFuctions() {
		for (int k = 0; k < FunctionNumbers; ++k) {
			createY(createA());
			createFunctions(k);
			sort(calcDeviation(runTime));
		}

	}

	@Override
	final public void run() {
		runFuctions();
		cdl.countDown();
	}
}
