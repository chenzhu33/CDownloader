package com.carelife.cdownloader.core;

import java.util.HashMap;

public class HttpConfiguration {

	final HashMap<String, String> httpHeaders;
	
	private HttpConfiguration(final Builder builder) {
		httpHeaders = builder.httpHeaders;
	}
	
	public static HttpConfiguration createDefault() {
		return new Builder().build();
	}
	
	public static class Builder {
		private HashMap<String, String> httpHeaders = new HashMap<String, String>();
		
		public Builder addHttpHead(String key, String value) {
			httpHeaders.put(key, value);
			return this;
		}
		
		public HttpConfiguration build() {
			if(httpHeaders == null)
				httpHeaders = new HashMap<String, String>();
			return new HttpConfiguration(this);
		}
		
	}
}
