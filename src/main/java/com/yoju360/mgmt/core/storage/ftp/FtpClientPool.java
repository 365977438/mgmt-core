/**
 * 
 */
package com.yoju360.mgmt.core.storage.ftp;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.enterprisedt.net.ftp.FileTransferClient;

/**
 * @author evan.wu
 *
 */
public class FtpClientPool extends GenericObjectPool<FileTransferClient>{

	public FtpClientPool(PooledObjectFactory<FileTransferClient> factory) {
		super(factory);
	}
}
