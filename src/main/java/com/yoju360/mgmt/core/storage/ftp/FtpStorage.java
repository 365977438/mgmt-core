/**
 * 
 */
package com.yoju360.mgmt.core.storage.ftp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.yoju360.mgmt.core.storage.Storage;
import com.yoju360.mgmt.core.storage.StorageFile;

/**
 * 使用FTP进行存储服务。
 * @author evan.wu
 *
 */
public class FtpStorage implements Storage{
	private static final Logger logger = LoggerFactory.getLogger(FtpStorage.class);
	private FtpClientPool ftpClientPool;
	
	/**
	 * 返回不重复的文件名，不包含扩展名。
	 * @return
	 */
	public String getNewFileName() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");  
        String imgFileId = formatter.format(new Date());
        return imgFileId + System.currentTimeMillis();
	}
	
	@Override
	public void deleteFile(String fullName) throws IOException {
		FileTransferClient ftp = null;
		try {
			ftp = ftpClientPool.borrowObject();
			ftp.changeDirectory("~/");

			int idx = fullName.lastIndexOf("/");
			String dirPath = fullName.substring(0, idx);
			if (dirPath.startsWith("/"))
				dirPath = dirPath.substring(1);
			
			String[] dirs = dirPath.split("/");
			for (int i=0; i<dirs.length; i++) {
				try {
					ftp.changeDirectory(dirs[i]);
				} catch (FTPException e) {
					return;
				}
			}
			if (ftp.exists(fullName.substring(idx+1)))
				ftp.deleteFile(fullName.substring(idx+1));
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if (ftp!=null)
				ftpClientPool.returnObject(ftp);
		}
	}

	@Override
	public StorageFile createFile(String path, String contentType) {
		return new FtpStorageFile(ftpClientPool, path, getNewFileName());
	}

	@Override
	public StorageFile createFile(String path, String contentType,
			String fileExt) {
		return new FtpStorageFile(ftpClientPool, path, getNewFileName() + fileExt);
	}

	@Override
	public StorageFile createFileWithName(String path, String name) {
		return new FtpStorageFile(ftpClientPool, path, name);
	}

	@Override
	public StorageFile createFileWithName(String path, String name,
			String contentType) {
		return new FtpStorageFile(ftpClientPool, path, name);
	}

	@Override
	public StorageFile createFileWithName(String path, String name,
			String contentType, String fileExt) {
		return new FtpStorageFile(ftpClientPool, path, name + fileExt);
	}

	@Override
	public StorageFile getFile(String fullName) {
		return new FtpStorageFile(ftpClientPool, fullName);
	}

	public FtpClientPool getFtpClientPool() {
		return ftpClientPool;
	}

	public void setFtpClientPool(FtpClientPool ftpClientPool) {
		this.ftpClientPool = ftpClientPool;
	}
}
