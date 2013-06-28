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
package com.carelife.cdownloader.core;

import java.util.concurrent.locks.ReentrantLock;

import com.carelife.cdownloader.core.listener.DownloadingListener;

/**
 * Information for download file task
 * 
 * @author Carelife
 * 
 */
public final class FileDownloadingInfo {

	public final String uri;
	public final DownloadingListener listener;
	public final ReentrantLock loadFromUriLock;

	public FileDownloadingInfo(String uri, DownloadingListener listener,
			ReentrantLock loadFromUriLock) {
		this.uri = uri;
		this.listener = listener;
		this.loadFromUriLock = loadFromUriLock;
	}
}
