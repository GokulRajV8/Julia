package app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

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
	
	// flag for button state
	public static int buttonState = 0;
	// Application core
	public static Core core;
	
	// start method should complete for UI to be displayed, hence all the processes are started as background threads
	public void start(Stage stage) {
		// creation of UI nodes
		ProgressBlock[] blocks = new ProgressBlock[core.threadCount];
		Label threadsActive = new Label("Threads active : 0 / " + core.thread.length);
		Label timeElapsed = new Label("Time elapsed : 0.00 seconds");
		Label completed = new Label("Completed : 0.0 %");
		Label buttonText = new Label();
		Pane pane = new Pane();
		
		// setting up the elements - numbers added and subtracted to give correct locations and sizes
		Scene scene = new Scene(pane, core.canvasSize + 2 + 150 + 1, core.canvasSize + 2, true);
		for(int i = 0; i < core.threadCount; ++i) {
			blocks[i] = new ProgressBlock(core.thread[i], (float)core.widthInPixels / core.canvasSize);
		}
		threadsActive.setLayoutX(core.canvasSize + 2);
		threadsActive.setLayoutY(1);
		threadsActive.setPrefSize(150, 50);
		threadsActive.setAlignment(Pos.CENTER);
		timeElapsed.setLayoutX(core.canvasSize + 2);
		timeElapsed.setLayoutY(50 + 2);
		timeElapsed.setPrefSize(150, 50);
		timeElapsed.setAlignment(Pos.CENTER);
		completed.setLayoutX(core.canvasSize + 2);
		completed.setLayoutY(100 + 3);
		completed.setPrefSize(150, 50);
		completed.setAlignment(Pos.CENTER);
		buttonText.setLayoutX(core.canvasSize + 2);
		buttonText.setLayoutY(core.canvasSize + 1 - 50);
		buttonText.setPrefSize(150, 50);
		buttonText.setAlignment(Pos.CENTER);
		
		// dark theme
		pane.setStyle("-fx-base: rgba(0, 0, 0, 255);");
		// adding elements
		pane.getChildren().add(threadsActive);
		pane.getChildren().add(timeElapsed);
		pane.getChildren().add(completed);
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
							float completedVal = 0.0f;
							for(int i = 0; i < blocks.length; ++i) {
								blocks[i].setProgress(core.thread[i].progress);
								completedVal += (float)core.thread[i].progress * (core.thread[i].width * core.thread[i].height) / (core.widthInPixels * core.widthInPixels);
								if(core.thread[i].isActive)
								++activeThreads;
							}
							threadsActive.setText("Threads active : " + activeThreads + " / " + core.thread.length);
							// calculate elapsed time for timeElapsed label
							timeElapsed.setText("Time elapsed : " +
							                    String.format("%.2f",
							                    		      ((core.executionEnd == 0.0 ? System.currentTimeMillis() : core.executionEnd) -
							                    		       (core.executionStart == 0.0 ? System.currentTimeMillis() : core.executionStart)) / 1000.0) + " s");
							completed.setText("Completed : " + String.format("%.2f", completedVal) + " %");
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
		
		// creation of events
		// click event for the button to start core thread
		buttonText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if(buttonState == 0)
					coreThread.start();
			}
		});
		// shutdown event on window close
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
	}
	
	public static void main(String[] args) throws IOException {
		Gson gson = new Gson();
		String settingsJson = Files.readString(Paths.get("src/app/settings.json"), StandardCharsets.UTF_8);
		SettingsBean settingsBean = gson.fromJson(settingsJson, SettingsBean.class);
		core = new Core(settingsBean);
		launch();
	}
}