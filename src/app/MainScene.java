package app;

import java.awt.image.BufferedImage;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class MainScene extends Thread {
    private UI hostUI;
    // Scene and elements
    public Scene scene;
    private ImageView canvas;
    // Renderer 1
    private Label renderer1Label;
    private Label renderer1Threads;
    private Label renderer1Completion;
    private Label renderer1Timer;
    private Label renderer1Button;
    // Renderer 2
    private Label renderer2Label;
    private Label renderer2Threads;
    private Label renderer2Completion;
    private Label renderer2Timer;
    private Label renderer2Button;
    // flags for buttons state
    private int button1State;
    private int button2State;
    // flag for scene state
    public volatile boolean isActive;

    public MainScene(UI hostUI) throws java.io.IOException {
        this.hostUI = hostUI;
        // activating scene and setting button states
        this.isActive = true;
        this.button1State = 0;
        this.button2State = 0;
        // creating loader using FXML file and creating scene
        FXMLLoader loader = new FXMLLoader(new java.io.File("../resources/MainScene.fxml").toURI().toURL());
        this.scene = new Scene(loader.load(), 665, 514, true);

        // Renderer 1
        this.renderer1Label = (Label)loader.getNamespace().get("renderer1Label");
        this.renderer1Label.setText(hostUI.linker.Core1GetIsGPU() ? "GPU" : "CPU");
        this.renderer1Threads = (Label)loader.getNamespace().get("renderer1Threads");
        this.renderer1Threads.setText("  Threads active : 0 / " + hostUI.linker.Core1GetThreadCount());
        this.renderer1Completion = (Label)loader.getNamespace().get("renderer1Completion");
        this.renderer1Completion.setText("  Completed : 0.0 %");
        this.renderer1Timer = (Label)loader.getNamespace().get("renderer1Timer");
        this.renderer1Timer.setText("  Time elapsed : 0.00 seconds");
        this.renderer1Button = (Label)loader.getNamespace().get("renderer1Button");
        this.renderer1Button.setText("Load");

        // Renderer 2
        this.renderer2Label = (Label)loader.getNamespace().get("renderer2Label");
        this.renderer2Label.setText(hostUI.linker.Core2GetIsGPU() ? "GPU" : "CPU");
        this.renderer2Threads = (Label)loader.getNamespace().get("renderer2Threads");
        this.renderer2Threads.setText("  Threads active : 0 / " + hostUI.linker.Core2GetThreadCount());
        this.renderer2Completion = (Label)loader.getNamespace().get("renderer2Completion");
        this.renderer2Completion.setText("  Completed : 0.0 %");
        this.renderer2Timer = (Label)loader.getNamespace().get("renderer2Timer");
        this.renderer2Timer.setText("  Time elapsed : 0.00 seconds");
        this.renderer2Button = (Label)loader.getNamespace().get("renderer2Button");
        this.renderer2Button.setText("Load");

        // Image output
        this.canvas = (ImageView)loader.getNamespace().get("canvas");

        // creation of events
        // click event for the button to start Renderer 1 core
        renderer1Button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if(button1State == 0) {
                    // starting in a new thread to avoid UI update suspension
                    new Thread() {
                        @Override
                        public void run() {
                            // updating core 1 with latest data
                            hostUI.linker.Core1Update();
                            button1State = 1;
                            hostUI.linker.Core1Run();

                            // updating the canvas
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    BufferedImage temp = new BufferedImage(512, 512, BufferedImage.TYPE_3BYTE_BGR);
                                    java.awt.Image image = hostUI.linker.Core1GetCanvas().canvas.getScaledInstance(512, 512, java.awt.Image.SCALE_SMOOTH);
                                    temp.getGraphics().drawImage(image, 0, 0, null);
                                    canvas.setImage(SwingFXUtils.toFXImage(temp, null));
                                }
                            });

                            // resetting button
                            button1State = 0;
                        }
                    }.start();
                }
            }
        });

        // click event for the button to start Renderer 2 core
        renderer2Button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if(button2State == 0) {
                    // starting in a new thread to avoid UI update suspension
                    new Thread() {
                        @Override
                        public void run() {
                            // updating core 2 with latest data
                            hostUI.linker.Core2Update();
                            button2State = 1;
                            hostUI.linker.Core2Run();

                            // saving the file
                            button2State = 2;
                            hostUI.linker.Core2GetCanvas().fileSave();

                            // resetting button
                            button2State = 0;
                        }
                    }.start();
                }
            }
        });
    }

    // scene updater
    @Override
    public void run() {
        System.out.println("Main scene update started");
        
        // polling UI thread until the scene is active
        while(this.isActive) {
            // to prevent polling from happening more than 50 times a second
            try {
                Thread.sleep((long)(1000.0/50.0));
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            // UI update as a runnable polled through Platform.runLater
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    int activeThreads;
                    float completedVal;

                    // Renderer 1 pane update
                    activeThreads = 0;
                    completedVal = 0.0f;
                    for(int i = 0; i < hostUI.linker.Core1GetThreadCount(); ++i) {
                        completedVal += (float)hostUI.linker.Core1GetThreadProgress()[i] / hostUI.linker.Core1GetThreadCount();
                        if(hostUI.linker.Core1GetThreadIsActive()[i])
                        ++activeThreads;
                    }

                    // count the active threads for threadsActive label
                    renderer1Threads.setText("  Threads active : " + activeThreads + " / " + hostUI.linker.Core1GetThreadCount());

                    // update the progress
                    renderer1Completion.setText("  Completed : " + String.format("%.2f", completedVal) + " %");

                    // calculate elapsed time for timeElapsed label
                    renderer1Timer.setText("  Time elapsed : " +
                                           String.format("%.2f",
                                                         ((hostUI.linker.Core1GetExecutionEnd() == 0.0 ? System.currentTimeMillis() : hostUI.linker.Core1GetExecutionEnd()) -
                                                          (hostUI.linker.Core1GetExecutionStart() == 0.0 ? System.currentTimeMillis() : hostUI.linker.Core1GetExecutionStart())) / 1000.0) + " s");

                    // button update
                    if(button1State == 0) {
                        renderer1Button.setText("Load");
                        renderer1Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#8888ff"), null, null)));
                    }
                    else {
                        renderer1Button.setText("Loading");
                        renderer1Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#88ff88"), null, null)));
                    }

                    // Renderer 2 pane update
                    activeThreads = 0;
                    completedVal = 0.0f;
                    for(int i = 0; i < hostUI.linker.Core2GetThreadCount(); ++i) {
                        completedVal += (float)hostUI.linker.Core2GetThreadProgress()[i] / hostUI.linker.Core2GetThreadCount();
                        if(hostUI.linker.Core2GetThreadIsActive()[i])
                        ++activeThreads;
                    }

                    // count the active threads for threadsActive label
                    renderer2Threads.setText("  Threads active : " + activeThreads + " / " + hostUI.linker.Core2GetThreadCount());

                    // update the progress
                    renderer2Completion.setText("  Completed : " + String.format("%.2f", completedVal) + " %");

                    // calculate elapsed time for timeElapsed label
                    renderer2Timer.setText("  Time elapsed : " +
                                           String.format("%.2f",
                                                         ((hostUI.linker.Core2GetExecutionEnd() == 0.0 ? System.currentTimeMillis() : hostUI.linker.Core2GetExecutionEnd()) -
                                                          (hostUI.linker.Core2GetExecutionStart() == 0.0 ? System.currentTimeMillis() : hostUI.linker.Core2GetExecutionStart())) / 1000.0) + " s");
                    
                    // button update
                    if(button2State == 0) {
                        renderer2Button.setText("Save");
                        renderer2Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#8888ff"), null, null)));
                    }
                    else if(button2State == 1) {
                        renderer2Button.setText("Processing");
                        renderer2Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#88ff88"), null, null)));
                    }
                    else {
                        renderer2Button.setText("Saving");
                        renderer2Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#88ff88"), null, null)));
                    }
                }
            });
        }

        System.out.println("Main scene update stopped");
    }
}
