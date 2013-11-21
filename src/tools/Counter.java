package tools;

public final class Counter {
	private long start;
	private long end;
	public final void setStart() {
		start = System.currentTimeMillis();
	}
	
	public final void setEnd() {
		synchronized (this) {
			end = System.currentTimeMillis();
		}
	}
	
	public final long getTime() {
		return end - start;
	}
}
