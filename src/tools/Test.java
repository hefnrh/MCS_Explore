package tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import run.MillionFunction;
import abstractmath.Field28;

final public class Test {
	public static final void main(String[] args) {
		// multiThread(1);
		// int n = Integer.parseInt(args[0]);
		// multiThread(n);
		// mixTest(1 << 24);
		runMillionFunction(24, 1_000_000, 50000 / 24);
		// testSort();
	}

	public static final void multiThread(int n) {
		System.out.println("-----------test multiple thread------------");
		byte p = 0b0001110;
		p |= -128;
		Field28 field = new Field28(p);
		Counter c = new Counter();
		CountDownLatch cdl = new CountDownLatch(n);
		MultiThreadTest[] ms = new MultiThreadTest[n];
		for (int i = 0; i < n; ++i)
			ms[i] = new MultiThreadTest(c, 1000000 / n, field, cdl);
		c.setStart();
		for (int i = 0; i < n; ++i)
			ms[i].start();
		try {
			cdl.await();
			System.out.println(c.getTime() + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static final void singleThread() {
		System.out.println("-----------test single thread------------");
		byte b = 0b001110;
		b |= -128;
		byte p = 0b0001110;
		p |= -128;
		byte minus1 = -1;
		Field28 field = new Field28(p);
		System.out.println("p = " + toBin(p));
		System.out.println("b = " + toBin(b));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; ++i) {
			b = field.multiply(b, minus1);
		}
		long end = System.currentTimeMillis();
		System.out.println("after multiply b = " + toBin(b));
		System.out.println("time used for 1000000 mutiply: " + (end - start)
				+ " ms");
	}

	public static final String toBin(byte b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; ++i) {
			sb.append((b >> (7 - i)) & 1);
		}
		return sb.toString();
	}

	public static final void mixTest(final int n) {
		final byte[][] matrix = { { 2, 3, 1, 1 }, { 1, 2, 3, 1 },
				{ 1, 1, 2, 3 }, { 3, 1, 1, 2 } };
		final byte p = (byte) 0b10001101;
		final Field28 f = new Field28(p);
		byte[] data = new byte[4];
		data[0] = data[1] = data[2] = data[3] = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; i < n; ++i) {
			data = f.AESMixColumns(matrix, data);
		}
		long end = System.currentTimeMillis();
		System.out.println(n + " times " + "time used: " + (end - start)
				+ " ms");
	}

	public static final void runMillionFunction(final int nThread,
			final int runTime, final int functionNumber) {
		final int length = 10;
		final Object lock = new Object(), rLock = new Object();
		final CountDownLatch cdl = new CountDownLatch(nThread);
		final int[] highRankXFunction = new int[length];
		final int[] highRankYFunction = new int[length];
		final double[] deviation = new double[length];
		final MillionFunction[] mf = new MillionFunction[nThread];
		final Random r = new Random();
		String[] lastResult = new String[11];
		try (BufferedReader br = new BufferedReader(new FileReader("mcs.txt"));) {
			for (int i = 0; i < 11; ++i)
				lastResult[i] = br.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (lastResult[1] == null)
			for (int i = 0; i < length; ++i) {
				highRankXFunction[i] = 0;
				highRankYFunction[i] = 0;
				deviation[i] = -1;
			}
		else {
			for (int i = 1; i < 11; ++i) {
				highRankXFunction[i - 1] = toInt(lastResult[i].substring(0, 32));
				highRankYFunction[i - 1] = toInt(lastResult[i].substring(35, 67));
				deviation[i - 1] = Double.parseDouble(lastResult[i]
						.substring(70));
			}
		}
		for (int i = 0; i < nThread; ++i) {
			mf[i] = new MillionFunction(highRankXFunction, highRankYFunction,
					deviation, lock, cdl, runTime, functionNumber, r, rLock);
		}
		final Thread[] thread = new Thread[nThread];
		for (int i = 0; i < nThread; ++i)
			thread[i] = new Thread(mf[i]);
		int i = 0;
		final long start = System.currentTimeMillis();
		for (; i < nThread; ++i)
			thread[i].start();
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		final long end = System.currentTimeMillis();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter("mcs.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int fNumber = functionNumber;
		if (lastResult[0] != null)
			fNumber += Integer.parseInt(lastResult[0].split("[^0-9]+")[2]);
		long time = end - start;
		if (lastResult[0] != null)
			time += Long.parseLong(lastResult[0].split("[^0-9]+")[3]);
		String msg = nThread + " threads * " + runTime + " input * "
				+ fNumber + " functions: " + time + " ms";
		System.out.println(msg);
		pw.println(msg);
		for (i = 0; i < length; ++i) {
			System.out.println(toBin(highRankXFunction[i]) + " = "
					+ toBin(highRankYFunction[i]) + " : " + deviation[i]);
			pw.println(toBin(highRankXFunction[i]) + " = "
					+ toBin(highRankYFunction[i]) + " : " + deviation[i]);
		}
		pw.flush();
		pw.close();
	}

	final static String toBin(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; ++i) {
			sb.append((n >> (31 - i)) & 1);
		}
		return sb.toString();
	}

	final static void testSort() {
		int[] hx = new int[10];
		int[] hy = new int[10];
		double[] d = new double[10];
		for (int i = 0; i < 10; ++i) {
			hx[i] = hy[i] = i;
			d[i] = (int) (0.34 * (i + 100));
		}
		double tmp;
		for (int i = 0; i < 10; ++i) {
			System.out.print(d[i] + " ");
		}
		tmp = 36;
		MillionFunction mf = new MillionFunction(hx, hy, d, new Object(), null,
				0, 0, null, null);
		System.out.println(tmp);
		mf.sort(tmp);
		for (int i = 0; i < 10; ++i) {
			System.out.print(d[i] + " ");
		}
	}

	final static void testSingleFunc() {
	}

	final static int toInt(String s) {
		int ret = 0;
		for (int i = 0; i < 32; ++i) {
			ret = (ret << 1) | (s.charAt(i) - '0');
		}
		return ret;
	}
}
