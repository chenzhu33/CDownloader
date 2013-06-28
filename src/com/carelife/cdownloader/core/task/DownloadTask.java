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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Handler;
import com.carelife.cdownloader.cache.disc.DiscCacheAware;
import com.carelife.cdownloader.core.FileDownloadingInfo;
import com.carelife.cdownloader.core.CDownloaderConfiguration;
import com.carelife.cdownloader.core.CDownloaderEngine;
import com.carelife.cdownloader.core.download.ImageDownloader;
import com.carelife.cdownloader.core.download.ImageDownloader.Scheme;
import com.carelife.cdownloader.core.listener.DownloadingListener;
import com.carelife.cdownloader.utils.IoUtils;
import com.carelife.cdownloader.utils.L;

/**
 * Presents download task. Used to download file from Internet or file system.
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.0.0
 * @see FileDownloadingInfo
 */
public final class DownloadTask implements Runnable {

	private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	private static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
	private static final int BUFFER_SIZE = 8 * 1024; // 8 Kb

	private final CDownloaderEngine engine;
	private final FileDownloadingInfo downloadingInfo;

	// Helper references
	private final CDownloaderConfiguration configuration;
	private final ImageDownloader downloader;
	private final ImageDownloader networkDeniedDownloader;
	private final ImageDownloader slowNetworkDownloader;
	private final boolean loggingEnabled;
	final String uri;
	final DownloadingListener listener;
	final Handler handler;

	public DownloadTask(CDownloaderEngine engine,
			FileDownloadingInfo downloadingInfo, Handler handler) {
		this.engine = engine;
		this.downloadingInfo = downloadingInfo;
		configuration = engine.configuration;
		downloader = configuration.downloader;
		networkDeniedDownloader = configuration.networkDeniedDownloader;
		slowNetworkDownloader = configuration.slowNetworkDownloader;
		loggingEnabled = configuration.loggingEnabled;
		uri = downloadingInfo.uri;
		listener = downloadingInfo.listener;
		this.handler = handler;
	}

	@Override
	public void run() {
		ReentrantLock loadFromUriLock = downloadingInfo.loadFromUriLock;
		log(LOG_START_DISPLAY_IMAGE_TASK);
		if (loadFromUriLock.isLocked()) {
			log(LOG_WAITING_FOR_IMAGE_LOADED);
		}

		loadFromUriLock.lock();
		final File file = tryLoadFile();
		handler.post(new Runnable() {

			@Override
			public void run() {
				listener.onLoadingComplete(uri, file.getAbsolutePath());
			}
		});
		loadFromUriLock.unlock();
	}

	/**
	 * try to load file from discCache or network;
	 * 
	 * @return the file to be loaded
	 */
	private File tryLoadFile() {
		File tmpfile = getFileInDiscCache();
		if (tmpfile.exists()) {
			log(LOG_LOAD_IMAGE_FROM_DISC_CACHE);
			return tmpfile;
		} else {
			try {
				log(LOG_LOAD_IMAGE_FROM_NETWORK);
				String fileUriForDecoding = tryCacheFileOnDisc(tmpfile);
				tmpfile = new File(new URI(fileUriForDecoding));
			} catch (IllegalStateException e) {
				L.e(e);
			} catch (OutOfMemoryError e) {
				L.e(e);
			} catch (Throwable e) {
				L.e(e);
			}
		}
		return tmpfile;
	}

	private File getFileInDiscCache() {
		DiscCacheAware discCache = configuration.discCache;
		File file = discCache.get(uri);
		File cacheDir = file.getParentFile();
		if (cacheDir == null || (!cacheDir.exists() && !cacheDir.mkdirs())) {
			file = configuration.reserveDiscCache.get(uri);
			cacheDir = file.getParentFile();
			if (cacheDir != null && !cacheDir.exists()) {
				cacheDir.mkdirs();
			}
		}
		return file;
	}

	/**
	 * @return Cached image URI; or original image URI if caching failed
	 */
	private String tryCacheFileOnDisc(File targetFile) {
		log(LOG_CACHE_IMAGE_ON_DISC);

		try {
			downloadFile(targetFile);
			configuration.discCache.put(uri, targetFile);
			return Scheme.FILE.wrap(targetFile.getAbsolutePath());
		} catch (IOException e) {
			L.e(e);
			return uri;
		}
	}

	private void downloadFile(File targetFile) throws IOException {
		InputStream is = getDownloader().getStream(uri, null);
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					targetFile), BUFFER_SIZE);
			try {
				IoUtils.copyStream(is, os, handler, listener);
			} finally {
				IoUtils.closeSilently(os);
			}
		} finally {
			IoUtils.closeSilently(is);
		}
	}

	private ImageDownloader getDownloader() {
		ImageDownloader d;
		if (engine.isNetworkDenied()) {
			d = networkDeniedDownloader;
		} else if (engine.isSlowNetwork()) {
			d = slowNetworkDownloader;
		} else {
			d = downloader;
		}
		return d;
	}

	public String getLoadingUri() {
		return uri;
	}

	private void log(String message) {
		if (loggingEnabled)
			L.i(message);
	}
}
