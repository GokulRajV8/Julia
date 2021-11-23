package app;

public class Container {
	
	public static void main(String[] args) {
		Core core = new Core();
		
		double time = System.currentTimeMillis();
		core.start();
		while(core.isAlive()) {
			for(int i = 0; i < 10; ++i)
				System.out.println("Thread 0" + i + " -> " + core.thread[i].progress + "%");
			for(int i = 10; i < 29; ++i)
				System.out.println("Thread " + i + " -> " + core.thread[i].progress + "%");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.print("Time taken ... " + (System.currentTimeMillis() - time)/60000.0d + " mins");
		
		core.canvas.fileSave();
		System.out.print(" : File saved");
	}
}
