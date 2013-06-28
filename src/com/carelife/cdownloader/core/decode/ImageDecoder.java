/*******************************************************************************
 * Copyright 2013 Chen Zhuwei
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
package com.carelife.cdownloader.core.decode;

import java.io.IOException;


import android.graphics.Bitmap;

/**
 * Provide decoding image to result {@link Bitmap}.
 * 
 * @author Chen Zhuwei (czwcarelife[at]gmail[dot]com)
 * @since 1.8.3
 * @see ImageDecodingInfo
 */
public interface ImageDecoder {

	/**
	 * Decodes image to {@link Bitmap} according target size and other parameters.
	 * 
	 * @param imageDecodingInfo 
	 * @return
	 * @throws IOException
	 */
	Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException;
}
