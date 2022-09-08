package app;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UI {
    public Linker linker;
    // Scenes
    private MainScene mainScene;

    // starting the UI module
    public void start(Stage mainStage) {
        // creating scenes as separate threads
        try {
            this.mainScene = new MainScene(this);
        } catch (java.io.IOException e){
            System.out.println("Cannot create Main scene since .fxml file is not present");
            System.exit(1);
        }
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
