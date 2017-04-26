package com.imuhao.common.utils;

import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dafan on 2016/11/13 0013.
 */

public class AESCrypt {
	private final Cipher cipher;
	private final SecretKeySpec key;
	private AlgorithmParameterSpec spec;
	public static final String SEED_16_CHARACTER = "DKN#IK@NFKLS*&FN";

	public AESCrypt() throws Exception {
		// hash password with SHA-256 and crop the output to 128-bit for key
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(SEED_16_CHARACTER.getBytes("UTF-8"));
		byte[] keyBytes = new byte[32];
		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
		cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
		key = new SecretKeySpec(keyBytes, "AES");
		spec = getIV();
	}

	public AlgorithmParameterSpec getIV() {
		byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
		IvParameterSpec ivParameterSpec;
		ivParameterSpec = new IvParameterSpec(iv);
		return ivParameterSpec;
	}

	public void setIV(String iv) {
		spec = new IvParameterSpec(getBytes(iv.toCharArray()));
	}

	private byte[] getBytes(char[] chars) {
		Charset cs = Charset.forName("UTF-8");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);
		byte[] bytes = bb.array();
		return bytes;
	}

	/**
	 * 加密
	 *
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String plainText) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
		String encryptedText = new String(Base64.encode(encrypted,
				Base64.DEFAULT), "UTF-8");
		return encryptedText;
	}

	/**
	 * 解密
	 *
	 * @param cryptedText
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String cryptedText) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
		byte[] decrypted = cipher.doFinal(bytes);
		String decryptedText = new String(decrypted, "UTF-8");
		return decryptedText;
	}
}