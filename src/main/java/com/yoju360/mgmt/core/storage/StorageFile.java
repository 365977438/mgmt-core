/**
 * 
 */
package com.yoju360.mgmt.core.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 存储服务器上的一个文件
 * @author evan.wu
 *
 */
public interface StorageFile {
	public String getName();
	/**
	 * 返回包含路径的完全文件名。
	 * @return
	 */
	public String getFullName();

	public String getPath();
	/**
	 * 获得输入流进行写入操作，注意在使用完关闭！
	 * @return
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException ;
	/**
	 * 获得输出流进行读入操作，注意在使用完关闭！
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException ;
	/**
	 * 从一个流复制为本文件的内容。
	 * @param in
	 * @throws IOException
	 */
	public void copyFrom(InputStream in) throws IOException;
	/**
	 * 从一个存储文件内容复制为本文件的内容。
	 * @param in
	 * @throws IOException
	 */
	public void copyFrom(StorageFile file) throws IOException;
	/**
	 * 从一个存储文件复制为本文件的内容。
	 * @param in
	 * @throws IOException
	 */
	public void copyFrom(String fullName) throws IOException;
}
