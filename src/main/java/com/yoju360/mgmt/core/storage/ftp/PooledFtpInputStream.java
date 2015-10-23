package com.yoju360.mgmt.core.storage.ftp;

import java.io.IOException;
import java.io.InputStream;

import com.enterprisedt.net.ftp.FileTransferClient;

public class PooledFtpInputStream extends InputStream {
	private FtpClientPool pool;
	private InputStream stream;
	private FileTransferClient ftp;

	public PooledFtpInputStream(FtpClientPool pool, FileTransferClient ftp, InputStream stream) {
		this.pool = pool;
		this.ftp = ftp;
		this.stream = stream;
	}
	
	@Override
	public void close() throws IOException {
		stream.close();
		pool.returnObject(ftp);
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}
}
