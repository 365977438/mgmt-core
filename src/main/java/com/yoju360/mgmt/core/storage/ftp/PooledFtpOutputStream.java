/**
 * 
 */
package com.yoju360.mgmt.core.storage.ftp;

import java.io.IOException;
import java.io.OutputStream;

import com.enterprisedt.net.ftp.FileTransferClient;

/**
 * @author evan.wu
 *
 */
public class PooledFtpOutputStream extends OutputStream {
	private FtpClientPool pool;
	private OutputStream stream;
	private FileTransferClient ftp;

	public PooledFtpOutputStream(FtpClientPool pool, FileTransferClient ftp, OutputStream stream) {
		this.pool = pool;
		this.ftp = ftp;
		this.stream = stream;
	}

	@Override
	public void write(int b) throws IOException {
		stream.write(b);
	}
	
	@Override
	public void close() throws IOException {
		stream.close();
		pool.returnObject(ftp);
	}
}
