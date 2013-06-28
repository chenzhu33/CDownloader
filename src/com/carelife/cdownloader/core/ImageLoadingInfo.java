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

import android.widget.ImageView;

import com.carelife.cdownloader.core.assist.ImageSize;
import com.carelife.cdownloader.core.assist.MemoryCacheUtil;
import com.carelife.cdownloader.core.listener.ImageLoadingListener;

/**
 * Information for load'n'display image task
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.3.1
 * @see MemoryCacheUtil
 * @see DisplayImageOptions
 * @see ImageLoadingListener
 */
public final class ImageLoadingInfo {

	public final String uri;
	public final String memoryCacheKey;
	public final ImageView imageView;
	public final ImageSize targetSize;
	public final DisplayImageOptions options;
	public final ImageLoadingListener listener;
	public final ReentrantLock loadFromUriLock;

	public ImageLoadingInfo(String uri, ImageView imageView, ImageSize targetSize, String memoryCacheKey, DisplayImageOptions options, ImageLoadingListener listener, ReentrantLock loadFromUriLock) {
		this.uri = uri;
		this.imageView = imageView;
		this.targetSize = targetSize;
		this.options = options;
		this.listener = listener;
		this.loadFromUriLock = loadFromUriLock;
		this.memoryCacheKey = memoryCacheKey;
	}
}
