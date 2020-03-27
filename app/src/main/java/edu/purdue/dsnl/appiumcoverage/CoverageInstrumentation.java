package edu.purdue.dsnl.appiumcoverage;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import lombok.SneakyThrows;
import lombok.var;

public class CoverageInstrumentation extends Instrumentation {
	@Override
	public void onCreate(Bundle arguments) {
		start();
	}

	@SneakyThrows({InterruptedException.class, PackageManager.NameNotFoundException.class})
	@Override
	public void onStart() {
		var context = getContext();
		var packageManager = context.getPackageManager();
		var instrumentationInfo = packageManager.getInstrumentationInfo(
				new ComponentName(context, CoverageInstrumentation.class), 0);
		var intent = packageManager.getLaunchIntentForPackage(instrumentationInfo.targetPackage);
		assert intent != null;
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivitySync(intent);

		var targetContext = getTargetContext();
		var coverageEndReceiver = new CoverageEndReceiver();
		var filter = new IntentFilter(context.getPackageName() + ".END_EMMA");
		targetContext.registerReceiver(coverageEndReceiver, filter);
		coverageEndReceiver.await();
		targetContext.unregisterReceiver(coverageEndReceiver);
		generateCoverageReport(targetContext);
		finish(0, new Bundle());
	}

	private void generateCoverageReport(Context context) {
		/* https://github.com/android/android-test/blob/master/runner/android_junit_runner/java/androidx/test/internal/runner/listener/CoverageListener.java */
		try {
			var emmaRtClass = Class.forName("com.vladium.emma.rt.RT", true, context.getClassLoader());
			var dumpCoverageMethod = emmaRtClass.getMethod(
					"dumpCoverageData", File.class, boolean.class, boolean.class);
			var coverageFile = new File(context.getExternalFilesDir(null), "coverage.ec");
			dumpCoverageMethod.invoke(null, coverageFile, false, false);
		} catch (ClassNotFoundException | NoSuchMethodException
				| IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
