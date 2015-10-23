/**
 * 
 */
package com.yoju360.mgmt.core.storage.ftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.yoju360.mgmt.core.storage.StorageFile;

/**
 * @author evan.wu
 *
 */
public class FtpStorageFile implements StorageFile {

	private FtpClientPool pool;
	private String path;
	private String name;

	public FtpStorageFile(FtpClientPool pool, String path,
			String name) {
		this.pool = pool;
		this.path = path;
		if (!path.startsWith("/"))
			this.path = "/" + this.path;
		if (!path.endsWith("/"))
			this.path = this.path + "/";
		this.name = name;
	}

	public FtpStorageFile(FtpClientPool pool, String fullName) {
		this.pool = pool;
		int idx = fullName.lastIndexOf("/");
		this.path = fullName.substring(0, idx+1);
		if (!path.startsWith("/"))
			this.path = "/" + this.path;
		this.name = fullName.substring(idx+1);
	}

	/* (non-Javadoc)
	 * @see com.mallgo.framework.core.storage.StorageFile#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.mallgo.framework.core.storage.StorageFile#getFullName()
	 */
	@Override
	public String getFullName() {
		return path + name;
	}

	/* (non-Javadoc)
	 * @see com.mallgo.framework.core.storage.StorageFile#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}
	
	/* (non-Javadoc)
	 * @see com.mallgo.framework.core.storage.StorageFile#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		FileTransferClient ftp = null;
		try {
			ftp = pool.borrowObject();
			ftp.changeDirectory("~/");
			String dirPath = path;
			if (path.startsWith("/"))
				dirPath = dirPath.substring(1);
			if (path.endsWith("/"))
				dirPath = dirPath.substring(0, path.length()-2);
			
			String[] dirs = dirPath.split("/");
			for (int i=0; i<dirs.length; i++) {
				while (true) {
					try {
						ftp.changeDirectory(dirs[i]);
						break;
					} catch (FTPException e) {
						try {
							ftp.createDirectory(dirs[i]);
						} catch (FTPException e1) {
							break;
						}
					}
				}
			}
			return new PooledFtpOutputStream(pool, ftp, ftp.uploadStream(getName()));
		} catch (Exception e) {
			if (ftp!=null)
				pool.returnObject(ftp);
			throw new IOException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.mallgo.framework.core.storage.StorageFile#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		FileTransferClient ftp = null;
		try {
			ftp = pool.borrowObject();
			ftp.changeDirectory("~/");
			String dirPath = path;
			if (path.startsWith("/"))
				dirPath = dirPath.substring(1);
			if (path.endsWith("/"))
				dirPath = dirPath.substring(0, path.length()-2);
			
			String[] dirs = dirPath.split("/");
			for (int i=0; i<dirs.length; i++) {
				try {
					ftp.changeDirectory(dirs[i]);
				} catch (FTPException e) {
					throw new FileNotFoundException(getFullName());
				}
			}
			
			return new PooledFtpInputStream(pool, ftp, ftp.downloadStream(getName()));
		} catch (Exception e) {
			if (ftp!=null)
				pool.returnObject(ftp);
			throw new IOException(e);
		}
	}

	@Override
	public void copyFrom(InputStream in) throws IOException {
		OutputStream out = this.getOutputStream();
		int i = -1;
		while ((i=in.read())!=-1) {
			out.write(i);
		}
		in.close();
		out.close();
	}

	@Override
	public void copyFrom(StorageFile file) throws IOException {
		OutputStream out = this.getOutputStream();
		InputStream in = file.getInputStream();
		int i = -1;
		while ((i=in.read())!=-1) {
			out.write(i);
		}
		in.close();
		out.close();
	}

	@Override
	public void copyFrom(String fullName) throws IOException {
		OutputStream out = this.getOutputStream();
		InputStream in = new FtpStorageFile(this.pool, fullName).getInputStream();
		int i = -1;
		while ((i=in.read())!=-1) {
			out.write(i);
		}
		in.close();
		out.close();
	}
}
