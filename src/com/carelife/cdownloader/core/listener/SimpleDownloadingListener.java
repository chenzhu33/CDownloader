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
 * A convenient class to extend when you only want to listen for a subset of all the image loading events. This
 * implements all methods in the {@link DownloadingListener} but does nothing.
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.0.0
 */
public class SimpleDownloadingListener implements DownloadingListener {

	@Override
	public void onLoadingStarted(String uri) {
		
	}

	@Override
	public void onLoadingFailed(String uri, FailReason failReason) {
		
	}

	@Override
	public void onLoadingComplete(String uri, String path) {
		
	}

	@Override
	public void onLoadingCancelled(String uri) {
		
	}

	@Override
	public void onLoadingProcess(long size) {
		
	}
	
}
