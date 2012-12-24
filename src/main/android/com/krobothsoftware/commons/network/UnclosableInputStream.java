/* ===================================================
 * Copyright 2012 Kroboth Software
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
 * ========================================================== 
 */

package com.krobothsoftware.commons.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * In order to disconnect HttpURLConnection correctly the
 * {@link HttpURLConnection#disconnect()} must be called before {@link #close()}
 * on InputStream. This class makes ensures the stream is never closed unless
 * the method {@link #forceClose()} is called. <a href=
 * "http://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection/11533423#11533423"
 * > More Info</a>
 * 
 * @version 3.0.1
 * @since Dec 1 2012
 * @author Kyle Kroboth
 * 
 */
public class UnclosableInputStream extends InputStream {
	private final InputStream in;

	public UnclosableInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	public void forceClose() throws IOException {
		in.close();
	}

	/**
	 * Call {@link #forceClose()} to close stream
	 */
	@Override
	public void close() throws IOException {
		// don't close
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public int hashCode() {
		return in.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return in.equals(obj);
	}

	@Override
	public String toString() {
		return in.toString();
	}

}
