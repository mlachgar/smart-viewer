package fr.mla.swt.smart.viewer.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public abstract class MouseClickTimer {

	private Event event = null;
	private int currentPeriod;
	private static final int[] DEFAULT_PERIODS = { 400, 100, 50, 20 };
	private final int[] periods;
	private Runnable job;
	private Display display;

	public MouseClickTimer(final Display display, int... periodValues) {
		this.display = display;
		if (periodValues != null && periodValues.length > 0) {
			this.periods = periodValues;
		} else {
			this.periods = DEFAULT_PERIODS;
		}
		job = new Runnable() {

			@Override
			public void run() {
				if (event != null) {
					changed(event);
					if (currentPeriod < periods.length - 1) {
						currentPeriod++;
					}
					display.timerExec(periods[currentPeriod], this);
				}
			}
		};
	}

	public void start(Event e) {
		event = e;
		currentPeriod = 0;
		display.timerExec(periods[currentPeriod], job);
		started(e);
	}

	public void mouseUp(Event e) {
		stop();
	}

	public void stop() {
		if (event != null) {
			event = null;
			currentPeriod = 0;
			stopped();
		}
	}

	public boolean isStarted() {
		return event != null;
	}
	
	protected void stopped() {

	}

	protected void started(Event e) {

	}

	protected abstract void changed(Event event);

}
