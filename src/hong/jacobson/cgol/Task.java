package hong.jacobson.cgol;

import android.os.AsyncTask;
import android.os.Handler;

public class Task extends AsyncTask<GameView, Void, Void> {

	Handler h;
	
	public Task(Handler handler) {
		h = handler;
	}

	@Override
	protected Void doInBackground(final GameView... gv) {
		while(!isCancelled()){
			h.post(new Runnable(){
				@Override
				public void run() {
					gv[0].tick();
				}
			});

			try {
				Thread.sleep(1000/gv[0].getSpeed());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;		
	}

}

