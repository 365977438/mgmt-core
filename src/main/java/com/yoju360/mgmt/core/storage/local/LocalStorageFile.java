/**
 * 
 */
package com.yoju360.mgmt.core.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.yoju360.mgmt.core.storage.StorageFile;

/**
 * @author evan.wu
 *
 */
public class LocalStorageFile implements StorageFile{
	private String startLocation;
	private String relativePath;
	private String name;
	
	public LocalStorageFile(String startLocation, String relativePath, String name) {
		this.startLocation = startLocation;
		this.relativePath = relativePath;
		if (!relativePath.startsWith("/"))
			this.relativePath = "/" + this.relativePath;
		if (!relativePath.endsWith("/"))
			this.relativePath = this.relativePath + "/";
		this.name = name;
	}

	public LocalStorageFile(String startLocation, String fullName) {
		this.startLocation = startLocation;
		int idx = fullName.lastIndexOf("/");
		this.relativePath = fullName.substring(0, idx+1);
		if (!relativePath.startsWith("/"))
			this.relativePath = "/" + this.relativePath;
		this.name = fullName.substring(idx+1);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFullName() {
		return relativePath + name;
	}

	@Override
	public String getPath() {
		return relativePath;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		File f = new File(startLocation + relativePath, name);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		return new FileOutputStream(f);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(startLocation + relativePath + name);
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
		InputStream in = new LocalStorageFile(this.startLocation, fullName).getInputStream();
		int i = -1;
		while ((i=in.read())!=-1) {
			out.write(i);
		}
		in.close();
		out.close();
	}
}
