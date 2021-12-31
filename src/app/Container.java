package app;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Container extends Application {
    // Flags for buttons state
	public static int button1State = 0;
    public static int button2State = 0;
	// Application cores
	public static Core core1;
    public static Core core2;

    public static void updateCore(Core core) {
        Gson gson = new Gson();
        try {
            SettingsBean settingsBean = gson.fromJson(java.nio.file.Files.readString(java.nio.file.Paths.get("settings.json"),
                                                      java.nio.charset.StandardCharsets.UTF_8), SettingsBean.class);
            core.update(settingsBean);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start(Stage stage) {
        // Renderer1
        Rectangle renderer1Bound = new Rectangle();
        Label renderer1Label = new Label(core1.isGPU ? "GPU" : "CPU");
        Label renderer1Threads = new Label("  Threads active : 0 / " + core1.threadCount);
        Label renderer1Completion = new Label("  Completed : 0.0 %");
        Label renderer1Timer = new Label("  Time elapsed : 0.00 seconds");
        Label renderer1Button = new Label("Load");

        // Renderer2
        Rectangle renderer2Bound = new Rectangle();
        Label renderer2Label = new Label(core2.isGPU ? "GPU" : "CPU");
        Label renderer2Threads = new Label("  Threads active : 0 / " + core2.threadCount);
        Label renderer2Completion = new Label("  Completed : 0.0 %");
        Label renderer2Timer = new Label("  Time elapsed : 0.00 seconds");
        Label renderer2Button = new Label("Save");

        // Image
        ImageView canvas = new ImageView();

        // creating pane and scene
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 665, 514, true);

        // setting up elements
        canvas.setX(1);
        canvas.setY(1);
        canvas.setFitWidth(512);
        canvas.setFitHeight(512);
        renderer1Bound.setFill(Color.LIGHTBLUE);
        renderer1Bound.setX(514);
        renderer1Bound.setY(11);
        renderer1Bound.setWidth(150);
        renderer1Bound.setHeight(245);
        renderer2Bound.setFill(Color.LIGHTBLUE);
        renderer2Bound.setX(514);
        renderer2Bound.setY(268);
        renderer2Bound.setWidth(150);
        renderer2Bound.setHeight(245);
        renderer1Label.setBackground(new Background(new BackgroundFill(Color.AQUAMARINE, null, null)));
        renderer1Label.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        renderer1Label.setLayoutX(524);
        renderer1Label.setLayoutY(1);
        renderer1Label.setPrefSize(130, 20);
        renderer1Label.setAlignment(Pos.CENTER);
        renderer2Label.setBackground(new Background(new BackgroundFill(Color.AQUAMARINE, null, null)));
        renderer2Label.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        renderer2Label.setLayoutX(524);
        renderer2Label.setLayoutY(258);
        renderer2Label.setPrefSize(130, 20);
        renderer2Label.setAlignment(Pos.CENTER);
        renderer1Threads.setLayoutX(515);
        renderer1Threads.setLayoutY(31);
        renderer1Threads.setPrefSize(148, 50);
        renderer2Threads.setLayoutX(515);
        renderer2Threads.setLayoutY(288);
        renderer2Threads.setPrefSize(148, 50);
        renderer1Completion.setLayoutX(515);
        renderer1Completion.setLayoutY(82);
        renderer1Completion.setPrefSize(148, 50);
        renderer2Completion.setLayoutX(515);
        renderer2Completion.setLayoutY(339);
        renderer2Completion.setPrefSize(148, 50);
        renderer1Timer.setLayoutX(515);
        renderer1Timer.setLayoutY(133);
        renderer1Timer.setPrefSize(148, 50);
        renderer2Timer.setLayoutX(515);
        renderer2Timer.setLayoutY(390);
        renderer2Timer.setPrefSize(148, 50);
        renderer1Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#8888ff"), null, null)));
        renderer1Button.setLayoutX(515);
        renderer1Button.setLayoutY(204);
        renderer1Button.setPrefSize(148, 50);
        renderer1Button.setAlignment(Pos.CENTER);
        renderer2Button.setBackground(new Background(new BackgroundFill(Color.valueOf("#8888ff"), null, null)));
        renderer2Button.setLayoutX(515);
        renderer2Button.setLayoutY(461);
        renderer2Button.setPrefSize(148, 50);
        renderer2Button.setAlignment(Pos.CENTER);

        // adding elements
        pane.getChildren().add(canvas);
        pane.getChildren().add(renderer1Bound);
        pane.getChildren().add(renderer2Bound);
        pane.getChildren().add(renderer1Label);
        pane.getChildren().add(renderer2Label);
        pane.getChildren().add(renderer1Threads);
        pane.getChildren().add(renderer2Threads);
        pane.getChildren().add(renderer1Completion);
        pane.getChildren().add(renderer2Completion);
        pane.getChildren().add(renderer1Timer);
        pane.getChildren().add(renderer2Timer);
        pane.getChildren().add(renderer1Button);
        pane.getChildren().add(renderer2Button);

        // setting up stage and displaying
        stage.setTitle("Julia");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

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
                            // updating core with latest data
                            Container.updateCore(core1);
                            button1State = 1;
                            core1.run();

                            // updating the canvas
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    BufferedImage temp = new BufferedImage(512, 512, BufferedImage.TYPE_3BYTE_BGR);
                                    java.awt.Image image = core1.canvas.canvas.getScaledInstance(512, 512, java.awt.Image.SCALE_SMOOTH);
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
                            // updating core with latest data
                            Container.updateCore(core2);
                            button2State = 1;
                            core2.run();

                            // saving the file
                            button2State = 2;
                            core2.canvas.fileSave();

                            // resetting button
                            button2State = 0;
                        }
                    }.start();
                }
			}
		});

        // shutdown event for window close
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});

        // start method should complete for UI to be displayed,
        // hence UI updater started as a background thread that polls the FX application(UI) thread with runnables
        new Thread() {
            @Override
            public void run() {
                // polling UI thread unconditionally
				while(true) {
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
							for(int i = 0; i < core1.threadCount; ++i) {
                                completedVal += (float)core1.threadProgress[i] / core1.threadCount;
								if(core1.threadIsActive[i])
								++activeThreads;
							}

                            // count the active threads for threadsActive label
							renderer1Threads.setText("  Threads active : " + activeThreads + " / " + core1.threadCount);

                            // update the progress
                            renderer1Completion.setText("  Completed : " + String.format("%.2f", completedVal) + " %");

							// calculate elapsed time for timeElapsed label
							renderer1Timer.setText("  Time elapsed : " +
							                    String.format("%.2f",
							                    		      ((core1.executionEnd == 0.0 ? System.currentTimeMillis() : core1.executionEnd) -
							                    		       (core1.executionStart == 0.0 ? System.currentTimeMillis() : core1.executionStart)) / 1000.0) + " s");
                            
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
							for(int i = 0; i < core2.threadCount; ++i) {
								completedVal += (float)core2.threadProgress[i] / core2.threadCount;
								if(core2.threadIsActive[i])
								++activeThreads;
							}

                            // count the active threads for threadsActive label
							renderer2Threads.setText("  Threads active : " + activeThreads + " / " + core2.threadCount);

                            // update the progress
                            renderer2Completion.setText("  Completed : " + String.format("%.2f", completedVal) + " %");

							// calculate elapsed time for timeElapsed label
							renderer2Timer.setText("  Time elapsed : " +
							                    String.format("%.2f",
							                    		      ((core2.executionEnd == 0.0 ? System.currentTimeMillis() : core2.executionEnd) -
							                    		       (core2.executionStart == 0.0 ? System.currentTimeMillis() : core2.executionStart)) / 1000.0) + " s");
                            
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
            }
        }.start();
    }

    public static void main(String[] args) throws IOException {
        core1 = new Core(false);
        core2 = new Core(true);
        launch();
    }
}