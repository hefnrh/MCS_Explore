import java.util.concurrent.CountDownLatch;

import abstractmath.Field28;

class Counter {
	private long start;
	private long end;
	public void setStart() {
		start = System.currentTimeMillis();
	}
	
	public void setEnd() {
		synchronized (this) {
			end = System.currentTimeMillis();
		}
	}
	
	public long getTime() {
		return end - start;
	}
}
class MultiThreadTest extends Thread {
	private CountDownLatch cdl;
	private Counter counter;
	private int times;
	private Field28 field;
	private byte minus1 = -1;
	private byte b  = 123;
	public MultiThreadTest(Counter c, int times, Field28 f, CountDownLatch cdl) {
		counter = c;
		this.times = times;
		field = f;
		this.cdl = cdl;
	}
	@Override
	public void run() {
		for (int i = 0; i < times; ++i)
			b = field.multiply(b, minus1);
		counter.setEnd();
		cdl.countDown();
	}
}
public class Test {
	public static void main(String[] args) {
		singleThread();
		multiThread(2);
	}
	public static void multiThread(int n) {
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
	public static void singleThread() {
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
