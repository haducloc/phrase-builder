package com.appslandia.core.views;

import android.os.Bundle;
import android.os.CountDownTimer;

public abstract class TimerDelegate {

	public static final String STATE_TIMER_REMAINING_MS = "timerRemainingMs";
	public static final String STATE_TIMER_RUNNING = "isTimerRunning";

	private CountDownTimer countDownTimer;
	private long timerRemainingMs;
	private boolean isTimerRunning = false;

	protected abstract long getTimerDurationMs();

	protected abstract long getTimerIntervalMs();

	public void onPause() {
		if (countDownTimer != null) {
			if (isTimerRunning) {
				countDownTimer.cancel();
				countDownTimer = null;
			}
		}
	}

	public void onResume() {
		if (isTimerRunning) {
			getCountDownTimer().start();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putLong(STATE_TIMER_REMAINING_MS, timerRemainingMs);
		outState.putBoolean(STATE_TIMER_RUNNING, isTimerRunning);
	}

	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			this.timerRemainingMs = getTimerDurationMs();
		} else {

			this.timerRemainingMs = savedInstanceState.getLong(STATE_TIMER_REMAINING_MS);
			this.isTimerRunning = savedInstanceState.getBoolean(STATE_TIMER_RUNNING);
		}
	}

	public void cancelAndResetTimer() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
			resetTimer();
		}
	}

	private void resetTimer() {
		countDownTimer = null;
		isTimerRunning = false;
		timerRemainingMs = getTimerDurationMs();
	}

	public boolean tryStartTimer() {
		if (isTimerRunning == false) {
			isTimerRunning = true;
			getCountDownTimer().start();
			return true;
		}
		return false;
	}

	public boolean isTimerRunning() {
		return isTimerRunning;
	}

	public long getTimerRemainingMs() {
		return timerRemainingMs;
	}

	public long getTimerDurationSec() {
		return (long) (getTimerDurationMs() / 1000);
	}

	public long getTimerRemainingSec() {
		return (long) (timerRemainingMs / 1000);
	}

	public CountDownTimer getCountDownTimer() {
		if (this.countDownTimer == null) {
			this.countDownTimer = this.createCountDownTimer();
		}
		return this.countDownTimer;
	}

	protected abstract void onCountDownTick();

	protected abstract void onCountDownFinish();

	protected CountDownTimer createCountDownTimer() {
		return new CountDownTimer(getTimerRemainingMs(), getTimerIntervalMs()) {

			@Override
			public void onTick(long millisUntilFinished) {
				timerRemainingMs = millisUntilFinished;
				onCountDownTick();
			}

			@Override
			public void onFinish() {
				resetTimer();
				onCountDownFinish();
			}
		};
	}
}
