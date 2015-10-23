/**
 * 
 */
package com.yoju360.mgmt.core.storage;

import java.io.IOException;

/**
 * 统一的文件存储接口，代表一个服务器上的存储服务。 提供给图片、静态网页等存储使用。
 * 这些文件由静态内容服务器发布，使用http的方式访问。
 * <p>
 * 每个存储的文件由一个包含路径的完全文件名标识。
 * </p>
 * 
 * @author evan.wu
 *
 */
public interface Storage {
	/**
	 * 返回不重复的文件名，不包含扩展名。
	 * @return
	 */
	public String getNewFileName();
	
	/**
	 * 删除存储服务器上的一个文件。
	 * @param fullName 文件的全名标识
	 * @throws IOException
	 */
	void deleteFile(String fullName) throws IOException;
	/**
	 * 创建一个文件，指定一个路径。文件名随机生成。
	 * @param path 在服务器上的路径，以/开头和结尾
	 * @param contentType MIME type
	 * @return
	 */
	StorageFile createFile(String path, String contentType);
	/**
	 * 创建一个文件，指定一个路径和文件扩展名。文件名随机生成。
	 * @param path 在服务器上的路径，以/开头和结尾
	 * @param contentType MIME type
	 * @param fileExt 文件扩展名
	 * @return
	 */
	StorageFile createFile(String path, String contentType, String fileExt);
	/**
	 * 创建一个文件，指定一个路径
	 * @param path
	 * @param name
	 * @return
	 */
	StorageFile createFileWithName(String path, String name);
	/**
	 * 创建一个文件，指定一个路径和mime type
	 * @param path
	 * @param name
	 * @param contentType
	 * @return
	 */
	StorageFile createFileWithName(String path, String name, String contentType);
	/**
	 * 创建一个文件，指定一个路径和mime type和扩展名
	 * @param path
	 * @param name
	 * @param contentType
	 * @param fileExt
	 * @return
	 */
	StorageFile createFileWithName(String path, String name, String contentType, String fileExt);
	/**
	 * 创建一个文件，指定一个完全文件名
	 * @param fullName
	 * @return
	 */
	StorageFile getFile(String fullName);
	
}
