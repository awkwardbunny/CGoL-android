package hong.jacobson.cgol;

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

	boolean havemeasured = false;
	
	//game variables
	final int SIZE = 25;
	boolean running = false;	
	int grid_s;
	int speed = 4;
	int measured;

	//create paint
	Paint paint = new Paint();
	
	//array to hold current state of the world
	boolean life[][];
	
	//handler for task handling
	final Handler handler;
	Task task;
	
	//constructor 1
	public GameView(Context context) {
		this(context, null);
	}

	//constructor 2
	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	//constructor 3
	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		setBackgroundColor(Color.LTGRAY); //if you want to
		life = new boolean[SIZE][SIZE]; //initialize empty grid
//		life[2][3] = true; //testing before touch input implementation
		
		//create handler and pass to task
		handler = new Handler();
		task = new Task(handler);
	}

	//set the current world state (load)
	public void setLife(boolean l[][]){
		life = l;
	}
	
	//reset the world (better build an arc)
	public void reset(){
		//wipe out #extinction
		life = new boolean[SIZE][SIZE];
		//re-draw
		invalidate();
	}
	
	//variables for handling touch input
	int prev_x, prev_y;
	boolean set = false;
	
	
	//overriden to implement custom touch behaviors
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//if tapped
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//get x and y
			float x = event.getX();
			float y = event.getY();
			
			//calculate with cell it corresponds to
			int xc = (int) ((x-(x%grid_s))/grid_s);
			int yc = (int) ((y-(y%grid_s))/grid_s);
//			Log.d("X:Y", xc+":"+yc);
			
			//birth the cell at that coordinate
			if(xc >= SIZE || yc >= SIZE || xc < 0 || yc < 0){
				return true;
			}

			//set which cell has been birth-ed so that input doesnt get processed multiple times when finger is dragged
			set = !life[xc][yc];
			life[xc][yc] = !life[xc][yc];
			prev_x = xc;
			prev_y = yc;
			
			//update/re-draw
			invalidate();
			
			return true; //true for consume touch event
		}else if(event.getAction() == MotionEvent.ACTION_MOVE){
			//get xy and corresponding cell
			float x = event.getX();
			float y = event.getY();
			int xc = (int) ((x-(x%grid_s))/grid_s);
			int yc = (int) ((y-(y%grid_s))/grid_s);
			
			//set it alive
			if(xc >= SIZE || yc >= SIZE || xc < 0 || yc < 0){
				return true;
			}
			
			//same logic as above.
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
	
	//overriden to implement custom drawing
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//set color for the grid
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(2);
		
		//draw grid
		for(int x = 0; x < SIZE+1; x++){
			canvas.drawLine(0, (grid_s*x)+1, measured, (grid_s*x)+1, paint);
			canvas.drawLine((grid_s*x)+1, 0, (grid_s*x)+1, measured, paint);
		}
		
		//set color for the (alive) cells
		paint.setColor(Color.BLACK);
		//draw cells
		for(int y = 0; y < SIZE; y++){
			for(int x = 0; x < SIZE; x++){
				if(life[x][y] == true){
					//swag calculations
					canvas.drawRect(grid_s*x+2, grid_s*y+2, grid_s*(x+1), grid_s*(y+1), paint);
				}
			}
		}
	}
	
	//overriden to implement custom sizing
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		   int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		   int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		   
		   //if it is wider than it is higher, use height as the smaller dimension
		   //this basically makes sure that 'parentWidth' holds the smaller of the two
		   if(parentWidth > parentHeight)
			   parentWidth = parentHeight;
		   
		   //measure once
		   if(!havemeasured){
		   
			   //calculate the biggest cell side length each cell can have
			   grid_s = (parentWidth-10)/SIZE; // -10 to give it some space near it 
			   int dimension = (grid_s*SIZE)+2; //total side length of the view
			   measured = dimension; //new measured should be that
			   this.setMeasuredDimension(dimension, dimension);
			   
			   //set layout parameters
			   //make it square
			   LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dimension, dimension);

			   //calculate margin so that same on both sides (either top/bottom or left/right)
			   int margin = (parentWidth - dimension)/2;

			   //if it is landscape (we know this because we set parentWidth equal to parentHeight in this case
			   if(parentWidth == parentHeight){
				   //set margins as so:
				   lp.setMargins(25, margin, 0, margin);
			   }else{
				   //if not (portrait)
				   //set margins as aso:
				   lp.setMargins(margin, 25, margin, 0);
			   }
			   this.setLayoutParams(lp);
			   
			   //so that we only measure once
			   havemeasured = true;
		   }
		   //stuff
		   super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	//get method for speed
	public int getSpeed(){
		return speed;
	}

	//set method for speed
	public void setSpeed(int s){
		Log.i("SPEED", "Set to "+speed); //debug
		speed = s;
	}
	
	//get method for running
	//I think convention for boolean is the prefix is-
	public boolean isRunning(){
		return running;
	}
	
	//run the game
	public void run(){
		running = true;
		
		//testing
//		tick();
//		stop();
		
		//run
		//ask the task nicely to execute this
		task.execute(this);
	}

	//stop/pause the game
	public void stop(){
		running = false;
		//stop
		//cancel the task
		task.cancel(true);
		//cannot restart once canceled, so re-create
		task = new Task(handler);
	}
	
	public void tick() {
		//TODO Implement game logic #done
		
		//testing logic (inverts everything)
//		for(int y = 0; y < SIZE; y++){
//			for(int x = 0; x < SIZE; x++){
//				life[x][y] = !life[x][y];
//			}
//		}
		
		/**///start
		//first, make a new, clean map
		boolean secondGen[][] = new boolean[SIZE][SIZE];
		
		//loop through each cell
		for(int y = 0; y < SIZE; y++){
			for(int x = 0; x < SIZE; x++){
				int nc = neighborCount(x, y); //get neighbor count
				
				//and then apply the four rules
				//simplified version
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
		
		//set the new world state
		life = secondGen;
		
		/**///end
		invalidate(); //update
		Log.d("TICKED", "tick"); //tick
	}
	
	//get the number of alive neighbors given the coordinates of the cell 
	public int neighborCount(int x, int y)
	{
		int count = 0; //counter
		
		//top-left
		if (x - 1 >= 0 && y - 1 >= 0 && life[x - 1][y - 1]) //check if in range
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