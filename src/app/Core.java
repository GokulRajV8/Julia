package app;

import com.google.gson.Gson;

public class Core {
    // core parameters
    public boolean isGPU;
    public int threadCount;
    public volatile int threadProgress[];
    public volatile boolean threadIsActive[];
    public double executionStart;
    public double executionEnd;
    // pattern parameters
    public int pixels;
    public float pixelSide;
    public float xStart;
    public float yStart;
    public ComplexNumber juliaCenter;
    public ComplexPlaneCanvas canvas;

    public Core(boolean isGPU) {
        this.isGPU = isGPU;
        this.threadCount = 0;
        this.executionStart = 0.0;
        this.executionEnd = 0.0;
    }

    public void update() {
        SettingsBean bean;
        Gson gson = new Gson();
        try {
            bean = gson.fromJson(java.nio.file.Files.readString(java.nio.file.Paths.get("../resources/settings.json"), java.nio.charset.StandardCharsets.UTF_8), SettingsBean.class);
            if(this.isGPU) {
                this.pixels = 16000;
                this.threadCount = 1;
            }
            else {
                this.pixels = 1024;
                this.threadCount = bean.cpuThreads;
            }
            this.threadProgress = new int[this.threadCount];
            this.threadIsActive = new boolean[this.threadCount];
            this.pixelSide = bean.width / this.pixels;
            this.xStart = bean.xCenter - bean.width / 2;
            this.yStart = bean.yCenter + bean.width / 2;
            this.juliaCenter = bean.juliaCenter;
            this.canvas = new ComplexPlaneCanvas(this.xStart, this.yStart, this.pixelSide, this.pixels, this.juliaCenter, "../resources/output.png");
            this.executionStart = 0.0;
            this.executionEnd = 0.0;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        // starting timer
        this.executionStart = System.currentTimeMillis();

        // creation of threads
        Thread thread[] = new Thread[this.threadCount];
        if(this.isGPU) {
            thread[0] = new GPUThread(0, this, this.canvas, 0, 0, 16000, 16000);
        }
        else {
            int blockSide = (int)((this.canvas.width / this.canvas.pixelSide) / Math.sqrt(this.threadCount));
            for(int i = 0; i < this.threadCount; ++i)
                thread[i] = new CPUThread(i, this, this.canvas, (i % (int)Math.sqrt((double)this.threadCount)) * blockSide, (i / (int)(Math.sqrt((double)this.threadCount))) * blockSide, blockSide, blockSide);
        }

        // starting all threads
        for(int i = 0; i < this.threadCount; ++i)
            thread[i].start();

        // proceed only after all threads are completed
        boolean flag = true;
        while(flag) {
            flag = false;
            for(int i = 0; i < this.threadCount; ++i)
                if(thread[i].isAlive())
                    flag = flag || thread[i].isAlive();
        }

        // stopping timer
        this.executionEnd = System.currentTimeMillis();
    }
}