package app;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ProgressBlock {
	
	private JuliaThread thread;
	public float xStart;
	public float yStart;
	public float width;
	public float height;
	public Rectangle completed;
	public Rectangle incomplete;
	
	public ProgressBlock(JuliaThread thread, float scale) {
		this.thread = thread;
		// 0.5 added and subtracted to use background as 1 px wide border
		this.xStart = this.thread.xStart / scale + 0.5f;
		this.yStart = this.thread.yStart / scale + 0.5f;
		this.width = this.thread.width / scale - 0.5f * 2;
		this.height = this.thread.height / scale - 0.5f * 2;
		
		this.completed = new Rectangle();
		this.completed.setFill(Color.AQUA);
		this.incomplete = new Rectangle();
		this.incomplete.setFill(Color.DARKGRAY);
		
		// 1 added to give clearance of 1px from top and left borders of the application
		this.incomplete.setX(this.xStart + 1);
		this.incomplete.setY(this.yStart + 1);
		this.incomplete.setWidth(this.width);
		this.incomplete.setHeight(this.height);
		this.completed.setX(this.xStart + 1);
		this.completed.setY(this.yStart + 1);
		this.completed.setWidth(0);
		this.completed.setHeight(this.height);
	}
	
	public void setProgress(int progress) {
		// progress converted from percentage to pixel width
		this.completed.setWidth(this.width * (progress / 100.0f));
	}
}