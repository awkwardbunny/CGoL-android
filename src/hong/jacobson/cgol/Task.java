package hong.jacobson.cgol;

import android.os.AsyncTask;
import android.os.Handler;

public class Task extends AsyncTask<GameView, Void, Void> {

	Handler h; //to be passed from gameview to post the runnable
	
	public Task(Handler handler) {
		h = handler; //we got it
	}

	//do in background (in a different thread) when execute() is called
	@Override
	protected Void doInBackground(final GameView... gv) {
		while(!isCancelled()){
			//post since we cannot directly manipulate stuff from non-UIThread
			h.post(new Runnable(){
				@Override
				public void run() {
					//tick
					gv[0].tick();
				}
			});

			//and then wait some time
			try {
				//calculate time to sleep based of FPS set by seekbar
				Thread.sleep(1000/gv[0].getSpeed());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;		
	}
}