/* ===================================================
 * Copyright 2012 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== 
 */

package com.krobothsoftware.commons.progress;

import org.slf4j.helpers.MessageFormatter;

/**
 * ProgressHelper calculates progress from amount of increments
 * 
 * @see ProgressListener
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class ProgressHelper {
	private static final ProgressListener DUMMY;
	private final ProgressListener progressListener;
	private float progressLength;
	private float totalCounter;
	private float currentCounter;
	private float currentValue;
	private float lastValue;
	private boolean enabled = true;

	/**
	 * Creates new ProgressHelper from listener. {@link ProgressListener} can be
	 * null
	 * 
	 * @param listener
	 *            listener, if null it will use an internal dummy
	 * @return progress helper
	 */
	public static ProgressHelper newInstance(final ProgressListener listener) {
		ProgressHelper helper;
		if (listener != null) helper = new ProgressHelper(listener);
		else {
			helper = new ProgressHelper(DUMMY);
			helper.enabled = false;
		}
		return helper;
	}

	private ProgressHelper(final ProgressListener listener) {
		progressListener = listener;
	}

	/**
	 * Sets the size for total increments.
	 * 
	 * @param totalCounter
	 * 
	 */
	public void setup(final int totalCounter) {
		if (!enabled) return;
		this.totalCounter = totalCounter;
		progressLength = progressListener.getProgressLength();
		currentValue = 0.0F;
		lastValue = 0.0F;
		currentCounter = 0.0F;
	}

	/**
	 * Increments the progress.
	 */
	public synchronized void update() {
		if (!enabled) return;
		update("");
	}

	/**
	 * Increments the progress and adds text
	 * 
	 * @param text
	 *            the text
	 */
	public synchronized void update(final String text) {
		if (!enabled) return;
		updateProgress(text);
	}

	public synchronized void update(final String format, final Object arg) {
		if (!enabled) return;
		updateProgress(MessageFormatter.format(format, arg).getMessage());
	}

	public synchronized void update(final String format, final Object arg1,
			final Object arg2) {
		if (!enabled) return;
		updateProgress(MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	public synchronized void update(final String format, final Object... args) {
		if (!enabled) return;
		updateProgress(MessageFormatter.format(format, args).getMessage());
	}

	/**
	 * Updates the progress test <b>without</b> incrementing.
	 * 
	 * @param text
	 *            update text
	 */
	public synchronized void updateText(final String text) {
		if (!enabled) return;
		progressListener.onProgressUpdate(currentValue, text);

	}

	public synchronized void updateText(final String format, final Object arg) {
		if (!enabled) return;
		progressListener.onProgressUpdate(currentValue, MessageFormatter
				.format(format, arg).getMessage());

	}

	public synchronized void updateText(final String format, final Object arg1,
			final Object arg2) {
		if (!enabled) return;
		progressListener.onProgressUpdate(currentValue, MessageFormatter
				.format(format, arg1, arg2).getMessage());

	}

	public synchronized void updateText(final String format,
			final Object... args) {
		if (!enabled) return;
		progressListener.onProgressUpdate(currentValue, MessageFormatter
				.format(format, args).getMessage());

	}

	/**
	 * Finishes the progress. Sets the progress to it's length
	 */
	public synchronized void finish() {
		finish("");
	}

	/**
	 * Finishes the progress. Sets the progress to it's length
	 * 
	 * @param text
	 *            finish text
	 */
	public synchronized void finish(final String text) {
		if (!enabled) return;
		progressListener.onProgressUpdate(progressLength, text);

	}

	public synchronized void finish(final String format, final Object arg) {
		if (!enabled) return;
		progressListener.onProgressUpdate(progressLength, MessageFormatter
				.format(format, arg).getMessage());

	}

	public synchronized void finish(final String format, final Object arg1,
			final Object arg2) {
		if (!enabled) return;
		progressListener.onProgressUpdate(progressLength, MessageFormatter
				.format(format, arg1, arg2).getMessage());

	}

	public synchronized void finish(final String format, final Object... args) {
		if (!enabled) return;
		progressListener.onProgressUpdate(progressLength, MessageFormatter
				.format(format, args).getMessage());

	}

	/**
	 * Adds increments
	 * 
	 * @param counts
	 *            added counter
	 */
	public synchronized void addIncrement(final int counts) {
		if (!enabled) return;
		lastValue = currentValue;
		totalCounter += counts;
	}

	public synchronized void disable() {
		enabled = false;
	}

	public synchronized void enable() {
		enabled = true;
	}

	private void updateProgress(final String text) {
		if (lastValue <= 0.0F) {
			if (!(currentCounter + 1 >= totalCounter)) progressListener
					.onProgressUpdate(computeMultiProgress(), text);
			else
				finish(text);
			return;
		}

		if (!(currentCounter + 1 >= totalCounter)) progressListener
				.onProgressUpdate(computeProgress(), text);
		else
			finish(text);
	}

	private float computeProgress() {
		return currentValue = progressLength / totalCounter * ++currentCounter;
	}

	private float computeMultiProgress() {
		return currentValue = (progressLength - lastValue) / totalCounter
				* ++currentCounter + lastValue;
	}

	static {
		DUMMY = new ProgressListener() {

			@Override
			public void onProgressUpdate(final float value, final String text) {

			}

			@Override
			public int getProgressLength() {
				return 100;
			}

		};
	}
}
