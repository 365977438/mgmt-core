/**
 * 
 */
package com.yoju360.mgmt.core.storage.local;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yoju360.mgmt.core.storage.Storage;
import com.yoju360.mgmt.core.storage.StorageFile;

/**
 * 使用本地文件系统（本地路径）保存文件。
 * @author evan.wu
 *
 */
public class LocalStorage implements Storage{
	/**
	 * 绝对路径前缀，不以/结尾
	 */
	private String absolutePathPrefix;
	
	/**
	 * 绝对路径前缀，不以/结尾
	 */
	public void setAbsolutePathPrefix(String absolutePathPrefix) {
		this.absolutePathPrefix = absolutePathPrefix;
	}

	/**
	 * 返回路径系统配置的图片路径前缀，为绝对路径。<br>
	 * 
	 * @return
	 */
	public String getAbsolutePathPrefix() {
		return absolutePathPrefix;
	}
	
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
		File f = new File(getAbsolutePathPrefix() + fullName);
		if (f.exists())
			f.delete();
	}
	
	@Override
	public StorageFile createFile(String path, String contentType) {
		return new LocalStorageFile(getAbsolutePathPrefix(), path, getNewFileName());
	}

	@Override
	public StorageFile getFile(String fullName) {
		return new LocalStorageFile(getAbsolutePathPrefix(), fullName);
	}

	@Override
	public StorageFile createFile(String path, String contentType, String fileExt) {
		return new LocalStorageFile(getAbsolutePathPrefix(), path, getNewFileName() + fileExt);
	}

	@Override
	public StorageFile createFileWithName(String path, String name) {
		return new LocalStorageFile(getAbsolutePathPrefix(), path, name);
	}

	@Override
	public StorageFile createFileWithName(String path, String name,
			String contentType) {
		return new LocalStorageFile(getAbsolutePathPrefix(), path, name);
	}

	@Override
	public StorageFile createFileWithName(String path, String name,
			String contentType, String fileExt) {
		return new LocalStorageFile(getAbsolutePathPrefix(), path, getNewFileName() + fileExt);
	}

}
