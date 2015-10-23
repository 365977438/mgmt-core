/**
 * 
 */
package com.yoju360.mgmt.core.storage.ftp;

import java.io.IOException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;

/**
 * @author evan.wu
 *
 */
public class FtpClientFactory extends BasePooledObjectFactory<FileTransferClient>{
	private static final Logger logger = LoggerFactory.getLogger(FtpStorage.class);
	
	private String host;
	private int port = 21;
	private String user;
	private String password;
	private String mode = "passive";
	
	@Override
	public boolean validateObject(PooledObject<FileTransferClient> client) {
		try {
			if (!client.getObject().isConnected())
				return false;
			String ret = client.getObject().executeCommand("NOOP");
			if (ret==null || !ret.startsWith("200"))
				return false;
			return true;
		} catch (FTPException | IOException e) {
			if (logger.isDebugEnabled())
				logger.debug("FTPClient is invalidated " + client, e);
			return false;
		}
	}
	
	@Override
	public void destroyObject(PooledObject<FileTransferClient> client) {
		try {
			client.getObject().disconnect();
		} catch (FTPException | IOException e) {
			//
		}
	}
	
	@Override
	public FileTransferClient create() throws Exception {
		try {
			FileTransferClient ftp = new FileTransferClient();
	        // set remote host
	        ftp.setRemoteHost(host);
	        ftp.setRemotePort(port);
	        ftp.setUserName(user);
	        ftp.setPassword(password);
	        
	        if (mode.equals("passive"))
	        	ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
	        
	        // connect to the server
	        if (logger.isDebugEnabled())
	        	logger.debug("Connecting to FTP server:" + host + ", port: " + port);
	        ftp.connect();
	        if (logger.isDebugEnabled())
	        	logger.debug("Connected and logged in to FTP server.");
	        return ftp;
		} catch (FTPException e) {
			throw new IOException(e);
		}
	}

	@Override
	public PooledObject<FileTransferClient> wrap(FileTransferClient client) {
		return new DefaultPooledObject<FileTransferClient>(client);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
