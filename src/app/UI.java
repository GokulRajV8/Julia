package app;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UI {
    public Linker linker;
    // Scenes
    private MainScene mainScene;

    // starting the UI module
    public void start(Stage mainStage) throws java.io.IOException {
        // creating scenes as separate threads
        this.mainScene = new MainScene(this);
        this.mainScene.start();

        // setting up stage and displaying
        mainStage.setTitle("Julia");
        mainStage.setResizable(false);
        mainStage.setScene(this.mainScene.scene);
        mainStage.show();

        // shutdown event for window close
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                // stopping scene updates
                mainScene.isActive = false;
                try {
                    // wait for a second to update the console
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                javafx.application.Platform.exit();
                System.exit(0);
            }
        });
    }
}
