package hong.jacobson.cgol;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	//GAME VARIABLE; Can be changed for different game config
	int MAX_SPEED = 20; //max fps limit from the seekbar
	
	//global variables
	static int mW; //measured width
	
	//view objects
	GameView gv; //GameView
	SeekBar sb_speed; //SeekBar
	Button btn, btn_reset; //Buttons

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //remove top bar (better looking layout in landscape mode)
		setContentView(R.layout.activity_main); //create all the views
		//this will load /res/layout/activity_main if in portrait mode,
		//or /res/layout-land if in landscape mode
		
		//testing
//		if (getResources().getDisplayMetrics().widthPixels > getResources().getDisplayMetrics().heightPixels) {
//			Toast.makeText(this, "Screen switched to Landscape mode", Toast.LENGTH_SHORT).show();
//		} else {
//			Toast.makeText(this, "Screen switched to Portrait mode", Toast.LENGTH_SHORT).show();
//		}

		//initialize all the view objects
		gv = (GameView) findViewById(R.id.gameView);
		btn = (Button) findViewById(R.id.button);
		sb_speed = (SeekBar) findViewById(R.id.sb_speed);
		btn_reset = (Button) findViewById(R.id.reset);

		//Speed variable goes from 1 to MAX_SPEED, seekbar goes from 0 to MAX_SPEED-1 (conversion)
		sb_speed.setMax(MAX_SPEED - 1); //set max speed limit
		sb_speed.setProgress(gv.getSpeed() - 1); //set current speed
		//add listener to update speed
		sb_speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//update current speed with conversion
				gv.setSpeed(progress + 1);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {} //do nothing
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {} //do nothing
		});

		//initially, it should say, "GO"
		btn.setText("GO");
		//set button listeners to handler user input
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//stop
				if (gv.isRunning()) {
					btn.setText("GO");
					gv.stop();
				} else { //go
					btn.setText("PAUSE");
					gv.run();
				}
			}
		});

		//this should always say, "Reset"
		btn_reset.setText("Reset");
		//when clicked, reset gameview
		btn_reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				gv.reset();
			}
		});
		
		//if there is a saved instance state
		if (savedInstanceState != null){
			//load world if exists
			if (savedInstanceState.getBooleanArray("world") != null) {
				//saved instance state can only store single dimensional arrays
				boolean single_d[] = savedInstanceState.getBooleanArray("world");
				//convert back to 2d
				boolean life[][] = new boolean[gv.SIZE][gv.SIZE];
				for (int y = 0; y < gv.SIZE; y++) {
					for (int x = 0; x < gv.SIZE; x++) {
						life[x][y] = single_d[gv.SIZE * y + x];
					}
				}
				//actually load the world
				gv.setLife(life);
			}
			
			//if it was running before orientation changed,
			if(savedInstanceState.getBoolean("running", false)){
				//continue running
				btn.setText("PAUSE");
				gv.run();
			}
		}
	}
	
	//called when activity is paused or restarted (restarted in this case due to orientation change)
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//saveInstanceState can only store single dimensional arrays
		//convert
		boolean single_dim_array[] = new boolean[gv.SIZE * gv.SIZE];
		for (int y = 0; y < gv.SIZE; y++) {
			for (int x = 0; x < gv.SIZE; x++) {
				single_dim_array[gv.SIZE * y + x] = gv.life[x][y];
			}
		}
		//store
		outState.putBooleanArray("world", single_dim_array);

		//if the game is running
		//save and stop
		outState.putBoolean("running", gv.running);
		gv.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
