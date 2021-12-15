package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

public class Container extends Application {
	// creation of application parameters
	public static int buttonState = 0;
	public static int scale = 40;
	// creation of application core
	public static Core core = new Core();
	// creation of UI nodes
	public static ProgressBlock[] blocks = new ProgressBlock[core.threadCount];
	public static Label threadsActive = new Label("Threads active : 0 / " + core.thread.length);
	public static Label timeElapsed = new Label("Time elapsed : 0.00 seconds");
	public static Label buttonText = new Label();
	public static Pane pane = new Pane();
	public static Scene scene = new Scene(pane, (core.canvas.xWidth / (scale * core.canvas.pixelSide)) + 2 + 150 + 1,
			                                     (core.canvas.yWidth / (scale * core.canvas.pixelSide)) + 2, true);
	
	// start method should complete for UI to be displayed, hence all the processes are started as background threads
	public void start(Stage stage) {
		// setting up the elements - numbers added and subtracted to give correct locations and sizes
		for(int i = 0; i < core.threadCount; ++i) {
			blocks[i] = new ProgressBlock(core.thread[i], scale);
		}
		threadsActive.setLayoutX((core.canvas.xWidth / (scale * core.canvas.pixelSide)) + 2);
		threadsActive.setLayoutY(1);
		threadsActive.setPrefSize(150, 50);
		threadsActive.setAlignment(Pos.CENTER);
		timeElapsed.setLayoutX((core.canvas.xWidth / (scale * core.canvas.pixelSide)) + 2);
		timeElapsed.setLayoutY(50 + 2);
		timeElapsed.setPrefSize(150, 50);
		timeElapsed.setAlignment(Pos.CENTER);
		buttonText.setLayoutX((core.canvas.xWidth / (scale * core.canvas.pixelSide)) + 2);
		buttonText.setLayoutY((core.canvas.yWidth / (scale * core.canvas.pixelSide)) + 1 - 50);
		buttonText.setPrefSize(150, 50);
		buttonText.setAlignment(Pos.CENTER);
		
		// dark theme
		pane.setStyle("-fx-base: rgba(0, 0, 0, 255);");
		// adding elements
		pane.getChildren().add(threadsActive);
		pane.getChildren().add(timeElapsed);
		pane.getChildren().add(buttonText);
		for(int i = 0; i < core.threadCount; ++i) {
			pane.getChildren().add(blocks[i].incomplete);
			pane.getChildren().add(blocks[i].completed);
		}
		// setting up the stage
		stage.setTitle("Julia");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
		
		// creation of core thread
		Thread coreThread = new Thread() {
			@Override
			public void run() {
				buttonState = 1;
				core.run();
				buttonState = 2;
				core.canvas.fileSave();
				// final state
				buttonState = 9;
			}
		};
		
		// creation and starting of UI update thread that polls the FX application(UI) thread with runnables
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
					// UI update logic as a runnable polled through Platform.runLater
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// count the active threads for threadsActive label
							int activeThreads = 0;
							for(int i = 0; i < blocks.length; ++i) {
								blocks[i].setProgress(core.thread[i].progress);
								if(core.thread[i].isActive)
								++activeThreads;
							}
							threadsActive.setText("Threads active : " + activeThreads + " / " + core.thread.length);
							// calculate elapsed time for timeElapsed label
							timeElapsed.setText("Time elapsed : " +
							                    String.format("%.2f",
							                    		      ((core.executionEnd == 0.0 ? System.currentTimeMillis() : core.executionEnd)
							                    		       - (core.executionStart == 0.0 ? System.currentTimeMillis() : core.executionStart)) / 1000.0)
							                    + " s");
							// updating of button looks based on buttonState
							if(buttonState == 0) {
								buttonText.setText("Start");
								buttonText.setBackground(new Background(new BackgroundFill(Color.valueOf("#8888ff"), null, null)));
							}
							else if(buttonState == 1) {
								buttonText.setText("Running ...");
								buttonText.setBackground(new Background(new BackgroundFill(Color.valueOf("#88ff88"), null, null)));
							}
							else if(buttonState == 2) {
								buttonText.setText("Saving ...");
							}
							else {
								buttonText.setText("Completed");
								buttonText.setBackground(new Background(new BackgroundFill(Color.valueOf("#ff8888"), null, null)));
							}
						}
					});
				}
			}
		}.start();
		
		// creation of click event for the button to start core thread
		buttonText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if(buttonState == 0)
					coreThread.start();
			}
		});
		// creation of shutdown event on window close
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
	}
	
	public static void main(String[] args) {
		launch();
	}
}