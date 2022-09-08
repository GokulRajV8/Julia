package app;

public class CPUThread extends Thread {
    private int threadId;
    public ComplexPlaneCanvas canvas;
    public Core core;
    public int xStart;
    public int yStart;
    public int width;
    public int height;
    public boolean isJulia;

    public CPUThread(int threadId, Core core, ComplexPlaneCanvas canvas, int xStart, int yStart, int width, int height) {
        // initialising object
        this.threadId = threadId;
        this.core = core;
        this.canvas = canvas;
        this.xStart = xStart;
        this.yStart = yStart;
        this.width = width;
        this.height = height;
        this.isJulia = !java.util.Objects.isNull(this.canvas.juliaCenter);
        this.core.threadProgress[this.threadId] = 0;
        this.core.threadIsActive[this.threadId] = false;
    }

    // returns the number of iterations when the result leaves the 2 units radius circle (max iteration 500)
    public int iterate(ComplexNumber z, ComplexNumber c) {
        ComplexNumber result;
        ComplexNumber zn = z.clone();
        int i = 0;
        for (; i < 500; i++) {
            result = ComplexNumber.add(ComplexNumber.multiply(zn, zn), c);
            if(result.magnitude() >= 4) {
                return i;
            }
            zn = result;
        }
        return i;
    }

    public void run() {
        // updating state
        this.core.threadIsActive[this.threadId] = true;
        ComplexNumber z = new ComplexNumber();
        ComplexNumber c = new ComplexNumber();
        float colorIndex = 0.0f;

        if(this.isJulia) {
            c.value(canvas.juliaCenter.real, canvas.juliaCenter.imag);
            for(int i = 0; i < this.width; ++i) {
                for(int j = 0; j < this.height; ++j) {
                    z.value(canvas.get_coordinate(i + xStart, true), canvas.get_coordinate(j + yStart, false));
                    colorIndex = (float)this.iterate(z, c);
                    canvas.setColor(i + xStart, j + yStart, colorIndex);
                }

                // updating progress
                this.core.threadProgress[this.threadId] = (100 * (i + 1)) / this.width;
            }
        }
        else {
            z.value(0.0f, 0.0f);
            for(int i = 0; i < this.width; ++i) {
                for(int j = 0; j < this.height; ++j) {
                    c.value(canvas.get_coordinate(i + xStart, true), canvas.get_coordinate(j + yStart, false));
                    colorIndex = (float)this.iterate(z, c);
                    canvas.setColor(i + xStart, j + yStart, colorIndex);
                }

                // updating progress
                this.core.threadProgress[this.threadId] = (100 * (i + 1)) / this.width;
            }
        }

        // updating state
        this.core.threadIsActive[this.threadId] = false;
    }
}