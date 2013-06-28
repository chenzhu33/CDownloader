/*******************************************************************************
 * Copyright 2011-2013 Chen Zhuwei
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
 *******************************************************************************/
package com.carelife.cdownloader.core.listener;

import com.carelife.cdownloader.core.assist.FailReason;

/**
 * Listener for downloading process.<br />
 * You can use {@link SimpleDownloadingListener} for implementing only needed methods.
 * 
 * @author Carelife
 * @since 1.0.0
 * @see SimpleDownloadingListener
 * @see FailReason
 */
public interface DownloadingListener {

	/**
	 * Is called when downloading task was started
	 * @param uri
	 */
	void onLoadingStarted(String uri);

	/**
	 * Is called when an error was occurred during downloading
	 * @param uri
	 * @param failReason
	 */
	void onLoadingFailed(String uri, FailReason failReason);

	/**
	 * Is called when file is loaded successfully
	 * @param uri
	 * @param path
	 */
	void onLoadingComplete(String uri, String path);

	/**
	 * Is called when file loading task was cancelled
	 * @param uri
	 */
	void onLoadingCancelled(String uri);
	
	/**
	 * Is called when file is downloading
	 * @param size current downloaded file size, in byte
	 */
	void onLoadingProcess(long size);
}
