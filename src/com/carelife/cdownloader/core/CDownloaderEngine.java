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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;

import com.carelife.cdownloader.core.assist.FailReason;
import com.carelife.cdownloader.core.assist.FlushedInputStream;
import com.carelife.cdownloader.core.listener.ImageLoadingListener;
import com.carelife.cdownloader.core.task.DownloadTask;
import com.carelife.cdownloader.core.task.LoadAndDisplayImageTask;
import com.carelife.cdownloader.core.task.ProcessAndDisplayImageTask;

/**
 * {@link CDownloader} engine which responsible for {@linkplain LoadAndDisplayImageTask display task} execution.
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.7.1
 */
public class CDownloaderEngine {

	public final CDownloaderConfiguration configuration;

	private Executor taskExecutor;
	private Executor taskExecutorForCachedImages;
	private ExecutorService taskDistributor;

	@SuppressLint("UseSparseArrays")
	private final Map<Integer, String> cacheKeysForImageViews = Collections.synchronizedMap(new HashMap<Integer, String>());
	private final Map<String, ReentrantLock> uriLocks = new WeakHashMap<String, ReentrantLock>();

	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final AtomicBoolean networkDenied = new AtomicBoolean(false);
	private final AtomicBoolean slowNetwork = new AtomicBoolean(false);

	public CDownloaderEngine(CDownloaderConfiguration configuration) {
		this.configuration = configuration;

		taskExecutor = configuration.taskExecutor;
		taskExecutorForCachedImages = configuration.taskExecutorForCachedImages;

		taskDistributor = Executors.newCachedThreadPool();
	}

	public void submit(final DownloadTask task) {
		taskDistributor.execute(new Runnable() {
			@Override
			public void run() {
				boolean isImageCachedOnDisc = configuration.discCache.get(task.getLoadingUri()).exists();
				initExecutorsIfNeed();
				if (isImageCachedOnDisc) {
					taskExecutorForCachedImages.execute(task);
				} else {
					taskExecutor.execute(task);
				}
			}
		});
	}

	/** Submits task to execution pool */
	public void submit(final LoadAndDisplayImageTask task) {
		taskDistributor.execute(new Runnable() {
			@Override
			public void run() {
				boolean isImageCachedOnDisc = configuration.discCache.get(task.getLoadingUri()).exists();
				initExecutorsIfNeed();
				if (isImageCachedOnDisc) {
					taskExecutorForCachedImages.execute(task);
				} else {
					taskExecutor.execute(task);
				}
			}
		});
	}

	/** Submits task to execution pool */
	public void submit(ProcessAndDisplayImageTask task) {
		initExecutorsIfNeed();
		taskExecutorForCachedImages.execute(task);
	}

	private void initExecutorsIfNeed() {
		if (taskExecutor == null) {
			taskExecutor = createTaskExecutor();
		}
		if (taskExecutorForCachedImages == null) {
			taskExecutorForCachedImages = createTaskExecutor();
		}
	}

	private Executor createTaskExecutor() {
		return DefaultConfigurationFactory.createExecutor(configuration.threadPoolSize, configuration.threadPriority, configuration.tasksProcessingType);
	}

	/** Returns URI of image which is loading at this moment into passed {@link ImageView} */
	public String getLoadingUriForView(ImageView imageView) {
		return cacheKeysForImageViews.get(imageView.hashCode());
	}

	/**
	 * Associates <b>memoryCacheKey</b> with <b>imageView</b>. Then it helps to define image URI is loaded into
	 * ImageView at exact moment.
	 */
	public void prepareDisplayTaskFor(ImageView imageView, String memoryCacheKey) {
		cacheKeysForImageViews.put(imageView.hashCode(), memoryCacheKey);
	}

	/**
	 * Cancels the task of loading and displaying image for incoming <b>imageView</b>.
	 * 
	 * @param imageView {@link ImageView} for which display task will be cancelled
	 */
	public void cancelDisplayTaskFor(ImageView imageView) {
		cacheKeysForImageViews.remove(imageView.hashCode());
	}

	/**
	 * Denies or allows engine to download images from the network.<br />
	 * <br />
	 * If downloads are denied and if image isn't cached then
	 * {@link ImageLoadingListener#onLoadingFailed(String, View, FailReason)} callback will be fired with
	 * {@link FailReason#NETWORK_DENIED}
	 * 
	 * @param denyNetworkDownloads pass <b>true</b> - to deny engine to download images from the network; <b>false</b> -
	 *            to allow engine to download images from network.
	 */
	public void denyNetworkDownloads(boolean denyNetworkDownloads) {
		networkDenied.set(denyNetworkDownloads);
	}

	/**
	 * Sets option whether ImageLoader will use {@link FlushedInputStream} for network downloads to handle <a
	 * href="http://code.google.com/p/android/issues/detail?id=6066">this known problem</a> or not.
	 * 
	 * @param handleSlowNetwork pass <b>true</b> - to use {@link FlushedInputStream} for network downloads; <b>false</b>
	 *            - otherwise.
	 */
	public void handleSlowNetwork(boolean handleSlowNetwork) {
		slowNetwork.set(handleSlowNetwork);
	}

	/**
	 * Pauses engine. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.<br />
	 * Already running tasks are not paused.
	 */
	public void pause() {
		paused.set(true);
	}

	/** Resumes engine work. Paused "load&display" tasks will continue its work. */
	public void resume() {
		synchronized (paused) {
			paused.set(false);
			paused.notifyAll();
		}
	}

	/** Stops engine, cancels all running and scheduled display image tasks. Clears internal data. */
	public void stop() {
		if (!configuration.customExecutor) {
			taskExecutor = null;
		}
		if (!configuration.customExecutorForCachedImages) {
			taskExecutorForCachedImages = null;
		}

		cacheKeysForImageViews.clear();
		uriLocks.clear();
	}

	public ReentrantLock getLockForUri(String uri) {
		ReentrantLock lock = uriLocks.get(uri);
		if (lock == null) {
			lock = new ReentrantLock();
			uriLocks.put(uri, lock);
		}
		return lock;
	}

	public AtomicBoolean getPause() {
		return paused;
	}

	public boolean isNetworkDenied() {
		return networkDenied.get();
	}

	public boolean isSlowNetwork() {
		return slowNetwork.get();
	}
}
