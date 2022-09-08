package app;

public class Linker {
    // App modules
    private Core core1;
    private Core core2;
    private UI ui;

    public void setCore1(Core core) {
        this.core1 = core;
    }

    public void setCore2(Core core) {
        this.core2 = core;
    }

    public void setUI(UI ui) {
        this.ui = ui;
        this.ui.linker = this;
    }

    // Links for app modules
    // Core 1
    public boolean Core1GetIsGPU() {
        return this.core1.isGPU;
    }

    public int[] Core1GetThreadProgress() {
        return this.core1.threadProgress;
    }

    public int Core1GetThreadCount() {
        return this.core1.threadCount;
    }

    public boolean[] Core1GetThreadIsActive() {
        return this.core1.threadIsActive;
    }

    public double Core1GetExecutionStart() {
        return this.core1.executionStart;
    }

    public double Core1GetExecutionEnd() {
        return this.core1.executionEnd;
    }

    public void Core1Update() {
        this.core1.update();
    }

    public void Core1Run() {
        this.core1.run();
    }

    public ComplexPlaneCanvas Core1GetCanvas() {
        return this.core1.canvas;
    }

    // Core 2
    public boolean Core2GetIsGPU() {
        return this.core2.isGPU;
    }

    public int[] Core2GetThreadProgress() {
        return this.core2.threadProgress;
    }

    public int Core2GetThreadCount() {
        return this.core2.threadCount;
    }

    public boolean[] Core2GetThreadIsActive() {
        return this.core2.threadIsActive;
    }

    public double Core2GetExecutionStart() {
        return this.core2.executionStart;
    }

    public double Core2GetExecutionEnd() {
        return this.core2.executionEnd;
    }

    public void Core2Update() {
        this.core2.update();
    }

    public void Core2Run() {
        this.core2.run();
    }

    public ComplexPlaneCanvas Core2GetCanvas() {
        return this.core2.canvas;
    }

    // UI
    public void UIStart(javafx.stage.Stage mainStage) {
        this.ui.start(mainStage);
    }
}
