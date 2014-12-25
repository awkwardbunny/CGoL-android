package hong.jacobson.cgol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class MainActivity extends Activity {

	static int mW;
	GameView gv;
	SeekBar sb_speed;
	Button btn, btn_reset;
	int MAX_SPEED = 20; //fps
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gv = (GameView) findViewById(R.id.gameView);
		btn = (Button) findViewById(R.id.button);
		sb_speed = (SeekBar) findViewById(R.id.sb_speed);
		btn_reset = (Button) findViewById(R.id.reset);
		
		sb_speed.setMax(MAX_SPEED-1);
		sb_speed.setProgress(gv.getSpeed()-1);
		sb_speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				gv.setSpeed(progress+1);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		btn.setText("GO");
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(gv.isRunning()){
					btn.setText("GO");
					gv.stop();
				}else{
					btn.setText("PAUSE");
					gv.run();
				}
			}
		});
		
		btn_reset.setText("Reset");
		btn_reset.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				gv.reset();
			}
		});
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
