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
package com.carelife.cdownloader.core.task;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.carelife.cdownloader.core.CDownloaderEngine;
import com.carelife.cdownloader.core.ImageLoadingInfo;
import com.carelife.cdownloader.core.assist.LoadedFrom;
import com.carelife.cdownloader.core.process.BitmapProcessor;
import com.carelife.cdownloader.utils.L;

/**
 * Presents process'n'display image task. Processes image {@linkplain Bitmap} and display it in {@link ImageView} using
 * {@link DisplayBitmapTask}.
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.8.0
 */
public class ProcessAndDisplayImageTask implements Runnable {

	private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";

	private final CDownloaderEngine engine;
	private final Bitmap bitmap;
	private final ImageLoadingInfo imageLoadingInfo;
	private final Handler handler;

	public ProcessAndDisplayImageTask(CDownloaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, Handler handler) {
		this.engine = engine;
		this.bitmap = bitmap;
		this.imageLoadingInfo = imageLoadingInfo;
		this.handler = handler;
	}

	@Override
	public void run() {
		if (engine.configuration.loggingEnabled) L.i(LOG_POSTPROCESS_IMAGE, imageLoadingInfo.memoryCacheKey);
		BitmapProcessor processor = imageLoadingInfo.options.getPostProcessor();
		final Bitmap processedBitmap = processor.process(bitmap);
		handler.post(new DisplayBitmapTask(processedBitmap, imageLoadingInfo, engine, LoadedFrom.MEMORY_CACHE));
	}
}
