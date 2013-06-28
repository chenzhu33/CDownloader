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
package com.carelife.cdownloader.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Handler;

import com.carelife.cdownloader.core.listener.DownloadingListener;

/**
 * Provides I/O operations
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class IoUtils {

	private static final int BUFFER_SIZE = 8 * 1024; // 8 KB 

	private IoUtils() {
	}

	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		while (true) {
			int count = is.read(bytes, 0, BUFFER_SIZE);
			if (count == -1) {
				break;
			}
			os.write(bytes, 0, count);
		}
	}

	public static void copyStream(InputStream is, OutputStream os, Handler handler, final DownloadingListener listener) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		long size = 0;
		while (true) {
			int count = is.read(bytes, 0, BUFFER_SIZE);
			if (count == -1) {
				break;
			}
			size += count;
			final long s = size;
			handler.post(new Runnable() {

				@Override
				public void run() {
					listener.onLoadingProcess(s);
				}
			});
			os.write(bytes, 0, count);
		}
	}

	public static void closeSilently(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {
			// Do nothing
		}
	}
}
