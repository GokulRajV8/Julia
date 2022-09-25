package app;

import jcuda.Pointer;
import jcuda.Sizeof;

import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;

public class GPUThread extends Thread {
    private int threadId;
    public ComplexPlaneCanvas canvas;
    public Core core;
    public int xStart;
    public int yStart;
    public int width;
    public int height;
    public boolean isJulia;
    // CUDA objects
    private CUdevice device;
    private CUcontext context;
    private CUmodule module;
    private CUfunction function;

    public GPUThread(int threadId, Core core, ComplexPlaneCanvas canvas, int xStart, int yStart, int width, int height) {
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

        // enabling exceptions
        JCudaDriver.setExceptionsEnabled(true);

        // initializing the driver and creating a context for the first device.
        JCudaDriver.cuInit(0);
        this.device = new CUdevice();
        JCudaDriver.cuDeviceGet(this.device, 0);
        this.context = new CUcontext();
        JCudaDriver.cuCtxCreate(this.context, 0, this.device);

        // creation of module with .ptx file and function pointer to the kernel function
        this.module = new CUmodule();
        JCudaDriver.cuModuleLoad(this.module, "../resources/GPUIterator.ptx");
        this.function = new CUfunction();
        JCudaDriver.cuModuleGetFunction(this.function, this.module, "iterate");
    }

    // returns the number of iterations when the result leaves the 2 units radius circle (max iteration 500)
    public int[][] iterateGrid(ComplexNumber[][] z, ComplexNumber[][] c) {
        // grid size
        final int size = z.length;

        // linearising vectors
        float[] tempFloat = new float[size * size];
        int[] tempInt = new int[size * size];

        // output
        int[][] n = new int[size][size];

        // compiling of .cu file, enabling exceptions, initializing driver, creating and setting context to current thread,
        // creating module and function for the kernel needs to be done before proceeding

        // allocating device memory vectors
        CUdeviceptr zRealMirror = new CUdeviceptr();
        JCudaDriver.cuMemAlloc(zRealMirror, size * size * Sizeof.FLOAT);
        CUdeviceptr zImagMirror = new CUdeviceptr();
        JCudaDriver.cuMemAlloc(zImagMirror, size * size * Sizeof.FLOAT);
        CUdeviceptr cRealMirror = new CUdeviceptr();
        JCudaDriver.cuMemAlloc(cRealMirror, size * size * Sizeof.FLOAT);
        CUdeviceptr cImagMirror = new CUdeviceptr();
        JCudaDriver.cuMemAlloc(cImagMirror, size * size * Sizeof.FLOAT);
        CUdeviceptr nMirror = new CUdeviceptr();
        JCudaDriver.cuMemAlloc(nMirror, size * size * Sizeof.INT);

        // linearising host memory and copying to device
        for(int x = 0; x < size; ++x)
            for(int y = 0; y < size; ++y)
                tempFloat[size * y + x] = z[x][y].real;
        JCudaDriver.cuMemcpyHtoD(zRealMirror, Pointer.to(tempFloat), size * size * Sizeof.FLOAT);
        for(int x = 0; x < size; ++x)
            for(int y = 0; y < size; ++y)
                tempFloat[size * y + x] = z[x][y].imag;
        JCudaDriver.cuMemcpyHtoD(zImagMirror, Pointer.to(tempFloat), size * size * Sizeof.FLOAT);
        for(int x = 0; x < size; ++x)
            for(int y = 0; y < size; ++y)
                tempFloat[size * y + x] = c[x][y].real;
        JCudaDriver.cuMemcpyHtoD(cRealMirror, Pointer.to(tempFloat), size * size * Sizeof.FLOAT);
        for(int x = 0; x < size; ++x)
            for(int y = 0; y < size; ++y)
                tempFloat[size * y + x] = c[x][y].imag;
        JCudaDriver.cuMemcpyHtoD(cImagMirror, Pointer.to(tempFloat), size * size * Sizeof.FLOAT);

        // setting up kernel parameters : A pointer to an array of pointers which point to the actual values.
        Pointer kernelParameters = Pointer.to(
            Pointer.to(new int[]{size}),
            Pointer.to(zRealMirror),
            Pointer.to(zImagMirror),
            Pointer.to(cRealMirror),
            Pointer.to(cImagMirror),
            Pointer.to(nMirror)
        );

        // processing device memory : Launching kernel
        JCudaDriver.cuLaunchKernel(
            this.function,         // function to be run in kernel
            50, 50, 1,             // grid dimensions in blocks
            32, 32, 1,             // block dimensions in threads
            0, null,               // shared memory and stream
            kernelParameters, null // kernel and extra parameters
        );

        // waiting until GPU completes processing
        JCudaDriver.cuCtxSynchronize();

        // copying device memory to host and delinearising
        JCudaDriver.cuMemcpyDtoH(Pointer.to(tempInt), nMirror, size * size * Sizeof.INT);
        for(int x = 0; x < size; ++x)
            for(int y = 0; y < size; ++y)
                    n[x][y] = tempInt[size * y + x];

        // releasing device memory
        JCudaDriver.cuMemFree(zRealMirror);
        JCudaDriver.cuMemFree(zImagMirror);
        JCudaDriver.cuMemFree(cRealMirror);
        JCudaDriver.cuMemFree(cImagMirror);
        JCudaDriver.cuMemFree(nMirror);

        // returning result
        return n;
    }

    public void run() {
        // updating state
        this.core.threadIsActive[this.threadId] = true;

        // setting context
        JCudaDriver.cuCtxSetCurrent(this.context);
        final int gridSize = 1600;
        final int gridNum = 10;

        if(this.isJulia) {
            for(int i = 0; i < gridNum; ++i) {
                for(int j = 0; j < gridNum; ++j) {
                    ComplexNumber[][] gridZ = new ComplexNumber[gridSize][gridSize];
                    ComplexNumber[][] gridC = new ComplexNumber[gridSize][gridSize];
                    int[][] gridcolorIndex = new int[gridSize][gridSize];
                    for(int x = 0; x < gridSize; ++x)
                        for(int y = 0; y < gridSize; ++y) {
                            gridZ[x][y] = new ComplexNumber();
                            gridZ[x][y].value(canvas.get_coordinate(x + xStart + i * gridSize, true), canvas.get_coordinate(y + yStart + j * gridSize, false));
                            gridC[x][y] = new ComplexNumber();
                            gridC[x][y].value(canvas.juliaCenter.real, canvas.juliaCenter.imag);
                    }
                    gridcolorIndex = this.iterateGrid(gridZ, gridC);
                    for(int x = 0; x < gridSize; ++x)
                        for(int y = 0; y < gridSize; ++y) {
                            canvas.setColor(x + xStart + i * gridSize, y + yStart + j * gridSize, gridcolorIndex[x][y]);
                    }

                    // updating progress
                    this.core.threadProgress[this.threadId] = gridNum * i + j + 1;
                }
            }
        }
        else {
            for(int i = 0; i < gridNum; ++i) {
                for(int j = 0; j < gridNum; ++j) {
                    ComplexNumber[][] gridZ = new ComplexNumber[gridSize][gridSize];
                    ComplexNumber[][] gridC = new ComplexNumber[gridSize][gridSize];
                    int[][] gridcolorIndex = new int[gridSize][gridSize];
                    for(int x = 0; x < gridSize; ++x)
                        for(int y = 0; y < gridSize; ++y) {
                            gridZ[x][y] = new ComplexNumber();
                            gridZ[x][y].value(0.0f, 0.0f);
                            gridC[x][y] = new ComplexNumber();
                            gridC[x][y].value(canvas.get_coordinate(x + xStart + i * gridSize, true), canvas.get_coordinate(y + yStart + j * gridSize, false));
                    }
                    gridcolorIndex = this.iterateGrid(gridZ, gridC);
                    for(int x = 0; x < gridSize; ++x)
                        for(int y = 0; y < gridSize; ++y)
                                canvas.setColor(x + xStart + i * gridSize, y + yStart + j * gridSize, gridcolorIndex[x][y]);

                    // updating progress
                    this.core.threadProgress[this.threadId] = gridNum * i + j + 1;
                }
            }
        }

        // updating state
        this.core.threadIsActive[this.threadId] = false;
    }
}