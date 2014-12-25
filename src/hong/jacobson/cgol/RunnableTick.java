package hong.jacobson.cgol;

import android.util.Log;

public class RunnableTick implements Runnable {

	public RunnableTick() {
	}

	@Override
	public void run() {
		Log.i("Finally", "I dont even know what im doing anymore");
	}

}
