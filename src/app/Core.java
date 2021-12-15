package app;

public class Core {
	
	public ComplexNumber juliaCenter;
	public ComplexPlaneCanvas canvas;
	public final int threadCount = 100;
	public JuliaThread thread[];
	public double executionStart;
	public double executionEnd;
	
	public Core() {
		this.juliaCenter = new ComplexNumber();
		this.juliaCenter.value(-0.8f, 0.156f);
		this.canvas = new ComplexPlaneCanvas(-2.0f, 2.0f, 0.0002f, 20000, 20000, juliaCenter, "output.png");
		this.executionStart = 0.0;
		this.executionEnd = 0.0;
		
		this.thread = new JuliaThread[this.threadCount];
		
		// Thread allocations with bounds
		/*
		// 29 threads
		this.thread[0] = new JuliaThread(0, this.canvas, 0, 0, 12000, 6000);
		this.thread[1] = new JuliaThread(1, this.canvas, 0, 14000, 12000, 6000);
		this.thread[2] = new JuliaThread(2, this.canvas, 12000, 0, 8000, 20000);
		this.thread[3] = new JuliaThread(3, this.canvas, 0, 6000, 4000, 8000);
		this.thread[0].isMonoChrome = false; this.thread[1].isMonoChrome = false; this.thread[2].isMonoChrome = false; this.thread[3].isMonoChrome = false;
		this.thread[0].isJulia = false; this.thread[1].isJulia = false; this.thread[2].isJulia = false; this.thread[3].isJulia = false;
		for(int i = 0; i < 8000; i = i + 1600) {
			for(int j = 0; j < 8000; j = j + 1600) {
				thread[5 * i/1600 + j/1600 + 4] = new JuliaThread(5 * i/1600 + j/1600 + 4, canvas, i + 4000 , j + 6000, 1600, 1600);
				thread[5 * i/1600 + j/1600 + 4].isMonoChrome = false;
				thread[5 * i/1600 + j/1600 + 4].isJulia = false;
			}
		}
		*/
		// 100 threads
		for(int i = 0; i < this.threadCount; ++i) {
			this.thread[i] = new JuliaThread(i, this.canvas, (i - 10 * (i / 10)) * 2000, (i / 10) * 2000, 2000, 2000);
			this.thread[i].isJulia = true;
			this.thread[i].isMonoChrome = false;
		}
	}
	
	public void run() {
		this.executionStart = System.currentTimeMillis();
		// starting all threads
		for(int i = 0; i < this.threadCount; ++i)
			thread[i].start();
		// to return only after all threads are completed
		boolean flag = true;
		while(flag) {
			flag = false;
			for(int i = 0; i < this.threadCount; ++i)
				if(thread[i].isAlive())
					flag = flag || thread[i].isAlive();
		}
		this.executionEnd = System.currentTimeMillis();
	}
}