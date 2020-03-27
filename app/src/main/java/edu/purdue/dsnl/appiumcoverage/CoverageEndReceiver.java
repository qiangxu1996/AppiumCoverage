package edu.purdue.dsnl.appiumcoverage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.CountDownLatch;

class CoverageEndReceiver extends BroadcastReceiver {
	private CountDownLatch latch = new CountDownLatch(1);

	@Override
	public void onReceive(Context context, Intent intent) {
		latch.countDown();
	}

	void await() throws InterruptedException {
		latch.await();
	}
}
