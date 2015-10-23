package com.yoju360.mgmt.core.util;

import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HashUtils {

	private static final Log logger = LogFactory.getLog(HashUtils.class);

	static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String getMD5(String source) {
		try {
			return getMD5(source.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getMD5(byte[] source) {
		String s = null;
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);

		} catch (Exception e) {
			logger.error("", e);
		}
		return s;
	}

	/**
	 * 生成md5校验码
	 * 
	 * @param srcContent
	 *            需要加密的数据
	 * @return 加密后的md5校验码。出错则返回null。
	 */
	public static String makeMd5Sum(byte[] srcContent) {
		if (srcContent == null) {
			return null;
		}

		String strDes = null;

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(srcContent);
			strDes = bytes2Hex(md5.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return strDes;
	}

	/**
	 * 对流进行Md5
	 * 
	 * @param in
	 *            需要加密的流
	 * @return
	 */
	public static String makeStreamHash(InputStream in) {

		BufferedInputStream bfs = null;
		String hash = null;
		byte[] buffer = new byte[1024];
		int readNum = 0;
		try {
			bfs = new BufferedInputStream(in);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			while ((readNum = bfs.read(buffer)) > 0) {
				md5.update(buffer, 0, readNum);
			}
			bfs.close();
			hash = bytes2Hex(md5.digest());
		} catch (Exception e) {
			return null;
		}
		return hash;
	}

	/**
	 * bytes2Hex方法
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String bytes2Hex(byte[] byteArray) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] >= 0 && byteArray[i] < 16) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));
		}
		return strBuf.toString();
	}

	public static void saveMd5File(String md5fileName, InputStream inputStream)
			throws Exception {
		try {
			String strMd5 = "";
			strMd5 = HashUtils.makeStreamHash(inputStream);
			FileWriter fileWriter = new FileWriter(md5fileName + ".md5");
			fileWriter.write(strMd5);
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			logger.error("saveMd5File 保存md5文件出错了：", e);
		}
	}

	/**
	 * 计算参数的md5信息
	 * 
	 * @param str
	 *            待处理的字节数组
	 * @return md5摘要信息
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] md5(byte[] str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str);
		return md.digest();
	}

	/**
	 * 将待加密数据data，通过密钥key，使用hmac-md5算法进行加密，然后返回加密结果。 参照rfc2104 HMAC算法介绍实现。
	 * 
	 * @author 尹星
	 * @param key
	 *            密钥
	 * @param data
	 *            待加密数据
	 * @return 加密结果
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getHmacMd5Bytes(byte[] key, byte[] data)
			throws NoSuchAlgorithmException {
		/*
		 * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
		 * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
		 * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据 ipad为0x36，opad为0x5C。
		 */
		int length = 64;
		byte[] ipad = new byte[length];
		byte[] opad = new byte[length];
		for (int i = 0; i < 64; i++) {
			ipad[i] = 0x36;
			opad[i] = 0x5C;
		}
		byte[] actualKey = key; // Actual key.
		byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length
		/*
		 * If key's length is longer than 64,then use hash to digest it and use
		 * the result as actual key. 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
		 */
		if (key.length > length) {
			actualKey = md5(key);
		}
		for (int i = 0; i < actualKey.length; i++) {
			keyArr[i] = actualKey[i];
		}

		/*
		 * append zeros to K 如果密钥长度不足64字节，就使用0x00补齐到64字节。
		 */
		if (actualKey.length < length) {
			for (int i = actualKey.length; i < keyArr.length; i++)
				keyArr[i] = 0x00;
		}

		/*
		 * calc K XOR ipad 使用密钥和ipad进行异或运算。
		 */
		byte[] kIpadXorResult = new byte[length];
		for (int i = 0; i < length; i++) {
			kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
		}

		/*
		 * append "text" to the end of "K XOR ipad" 将待加密数据追加到K XOR ipad计算结果后面。
		 */
		byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
		for (int i = 0; i < kIpadXorResult.length; i++) {
			firstAppendResult[i] = kIpadXorResult[i];
		}
		for (int i = 0; i < data.length; i++) {
			firstAppendResult[i + keyArr.length] = data[i];
		}

		/*
		 * calc H(K XOR ipad, text) 使用哈希算法计算上面结果的摘要。
		 */
		byte[] firstHashResult = md5(firstAppendResult);

		/*
		 * calc K XOR opad 使用密钥和opad进行异或运算。
		 */
		byte[] kOpadXorResult = new byte[length];
		for (int i = 0; i < length; i++) {
			kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
		}

		/*
		 * append "H(K XOR ipad, text)" to the end of "K XOR opad" 将H(K XOR
		 * ipad, text)结果追加到K XOR opad结果后面
		 */
		byte[] secondAppendResult = new byte[kOpadXorResult.length
				+ firstHashResult.length];
		for (int i = 0; i < kOpadXorResult.length; i++) {
			secondAppendResult[i] = kOpadXorResult[i];
		}
		for (int i = 0; i < firstHashResult.length; i++) {
			secondAppendResult[i + keyArr.length] = firstHashResult[i];
		}

		/*
		 * H(K XOR opad, H(K XOR ipad, text)) 对上面的数据进行哈希运算。
		 */
		byte[] hmacMd5Bytes = md5(secondAppendResult);

		return hmacMd5Bytes;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		byte[] macmd5 = HashUtils.getHmacMd5Bytes(
				"7e62ff29f6548e2e7cf5307315fb2b1".getBytes(), "".getBytes());
		System.out.println(HashUtils.bytes2Hex(macmd5));
		System.out.println(HashUtils.bytes2Hex("xxoo".getBytes()));
		System.out
				.println(HashUtils.bytes2Hex(HashUtils.getHmacMd5Bytes(
						"7e62ff29f6548e2e7cf5307315fb2b1".getBytes(),
						"xxoo".getBytes())));
	}

//	public static void main(String[] args) throws UnsupportedEncodingException {
//		String org = "ff800d072000e599f06b4e9379452f7c2015-05-01 11:12:341.06fec1aa9844f808510cb7345def96e08";
//		System.out.println(HashUtils.getMD5(org.getBytes("UTF-8")));
//	}
}
