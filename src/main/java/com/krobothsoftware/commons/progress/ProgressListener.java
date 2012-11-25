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

/**
 * The listener interface for receiving progress events. When a progress event
 * occurs, that object's appropriate method is invoked.
 * 
 * @see ProgressHelper
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public interface ProgressListener {

	/**
	 * Called when progress is updated
	 * 
	 * @param value
	 *            value
	 * @param text
	 *            update text
	 */
	void onProgressUpdate(float value, String text);

	/**
	 * Gets progress Length
	 * 
	 * @return progress length
	 */
	int getProgressLength();

}
