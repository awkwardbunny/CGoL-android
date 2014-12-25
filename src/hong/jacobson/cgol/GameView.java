package hong.jacobson.cgol;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class GameView extends View{

	boolean running = false;
	
	boolean havemeasured = false;
	
	final int SIZE = 25;
	int grid_s;
	int speed = 4;
	int measured;
	
	Paint paint = new Paint();
	
	boolean life[][];
	
//	Task task;
	final Handler handler;
	Task task;
	
	public GameView(Context context) {
		this(context, null);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		setBackgroundColor(Color.LTGRAY);
		life = new boolean[SIZE][SIZE];
//		life[2][3] = true; //testing before touch input implementation
		
		handler = new Handler();
		task = new Task(handler);
		
	}
	
	public void reset(){
		life = new boolean[SIZE][SIZE];
		invalidate();
	}
	
	int prev_x, prev_y;
	boolean set = false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();
			int xc = (int) ((x-(x%grid_s))/grid_s);
			int yc = (int) ((y-(y%grid_s))/grid_s);
			Log.d("X:Y", xc+":"+yc);
			
			if(xc >= SIZE || yc >= SIZE || xc < 0 || yc < 0){
				return true;
			}

			set = !life[xc][yc];
			life[xc][yc] = !life[xc][yc];
			prev_x = xc;
			prev_y = yc;
			invalidate();
			return true;
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			float x = event.getX();
			float y = event.getY();
			int xc = (int) ((x-(x%grid_s))/grid_s);
			int yc = (int) ((y-(y%grid_s))/grid_s);
			
			if(xc >= SIZE || yc >= SIZE || xc < 0 || yc < 0){
				return true;
			}
			
			if(xc != prev_x || yc != prev_y){
				life[xc][yc] = set;
				prev_x = xc;
				prev_y = yc;
				invalidate();
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(2);
		
		//draw grid
		for(int x = 0; x < SIZE+1; x++){
			canvas.drawLine(0, (grid_s*x)+1, measured, (grid_s*x)+1, paint);
			canvas.drawLine((grid_s*x)+1, 0, (grid_s*x)+1, measured, paint);
		}
		
		paint.setColor(Color.BLACK);
		//draw cells
		for(int y = 0; y < SIZE; y++){
			for(int x = 0; x < SIZE; x++){
				if(life[x][y] == true){
					canvas.drawRect(grid_s*x+2, grid_s*y+2, grid_s*(x+1), grid_s*(y+1), paint);
				}
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		   int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		   
		   if(!havemeasured){
		   
			   grid_s = (parentWidth-10)/SIZE;
			   
			   int dimension = (grid_s*SIZE)+2;
			   measured = dimension;
			   
			   int margin = (parentWidth - dimension)/2;
			   
			   this.setMeasuredDimension(dimension, dimension);
			   LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dimension, dimension);
			   lp.setMargins(margin, 25, margin, 0);
			   this.setLayoutParams(lp);
			   havemeasured = true;
		   }
		   super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int getSpeed(){
		return speed;
	}

	public void setSpeed(int s){
		Log.i("SPEED", "Set to "+speed);
		speed = s;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public void run(){
		running = true;
		
		//testing
//		tick();
//		stop();
		
		//run
		task.execute(this);
	}

	public void stop(){
		running = false;
		//stop
		task.cancel(true);
		task = new Task(handler);
	}
	
	public void tick() {
		//TODO Implement game logic
		
		//testing logic (inverts everything)
//		for(int y = 0; y < SIZE; y++){
//			for(int x = 0; x < SIZE; x++){
//				life[x][y] = !life[x][y];
//			}
//		}
		/**///start
		//first, make a new, clean map
		boolean secondGen[][] = new boolean[SIZE][SIZE];
		
		for(int y = 0; y < SIZE; y++){
			for(int x = 0; x < SIZE; x++){
				int nc = neighborCount(x, y);
				if(life[x][y]){
					if(nc < 2 || nc > 3){
						secondGen[x][y] = false;
					}else{
						secondGen[x][y] = true;
					}
				}else{
					if(nc == 3){
						secondGen[x][y] = true;
					}
				}
			}
		}
		
		life = secondGen;
		
		/**///end
		invalidate();
		Log.d("TICKED", "tick");
	}
	
	public int neighborCount(int x, int y)
	{
		int count = 0;
		//top-left
		if (x - 1 >= 0 && y - 1 >= 0 && life[x - 1][y - 1])
			count++;
		//bottom-left
		if (x - 1 >= 0 && y + 1 < life[0].length && life[x - 1][y + 1])
			count++;
		//middle-left
		if (x - 1 >= 0 && life[x - 1][y])
			count++;
		//top-center
		if (y - 1 >= 0 && life[x][y - 1])
			count++;
		//bottom-center
		if (y + 1 < life[0].length && life[x][y + 1])
			count++;
		//top-right
		if (x + 1 < life.length && y - 1 >= 0 && life[x + 1][y - 1])
			count++;
		//bottom-right
		if (x + 1 < life.length && y + 1 < life[0].length && life[x + 1][y + 1])
			count++;
		//middle-right
		if (x + 1 < life.length && life[x + 1][y])
			count++;
		return count;
	}
}