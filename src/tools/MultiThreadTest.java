package tools;

import java.util.concurrent.CountDownLatch;

import abstractmath.Field28;

public class MultiThreadTest extends Thread {
	private CountDownLatch cdl;
	private Counter counter;
	private final int times;
	private final Field28 field;
	private byte minus1 = -1;
	private byte b  = 123;
	public MultiThreadTest(Counter c, int times, Field28 f, CountDownLatch cdl) {
		counter = c;
		this.times = times;
		field = f;
		this.cdl = cdl;
	}
	@Override
	public final void run() {
		for (int i = 0; i < times; ++i)
			b = field.multiply(b, minus1);
		counter.setEnd();
		cdl.countDown();
	}
	
	public final byte getB() {
		return b;
	}
}
