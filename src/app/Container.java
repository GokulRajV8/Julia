package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Container extends Application {
    // App modules
    public static Core core1;
    public static Core core2;
    public static UI ui;
    // Linker to connect all app modules
    public static Linker linker;

    // start method to create JavaFX UI
    // start method should complete for UI to be displayed,
    public void start(Stage mainStage) {
        // creating UI module
        ui = new UI();

        // linking UI module to Linker
        linker.setUI(ui);

        // starting UI module
        linker.UIStart(mainStage);
    }

    public static void main(String[] args) {
        /*
        // Removed due to removal of CUDA compilers
        // compiling .cu file to .ptx file
        System.out.println("CUDA file compilation started");
        try {
            Process process = Runtime.getRuntime().exec("nvcc " +
                                                        "-ptx " +
                                                        "\"src/app/GPUIterator.cu\" " +
                                                        "-ccbin " +
                                                        "\"C:/Program Files (x86)/Microsoft Visual Studio/2019/Community/VC/Tools/MSVC/14.29.30133/bin/Hostx64/x64\""
                                                        );
            process.waitFor();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("CUDA file compilation completed");
        */

        // creating Linker
        linker = new Linker();

        // creating Core modules
        core1 = new Core(false);
        core2 = new Core(true);

        // linking Core modules to Linker
        linker.setCore1(core1);
        linker.setCore2(core2);

        // launching the app
        launch();
    }
}