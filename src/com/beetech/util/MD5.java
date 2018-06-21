package com.beetech.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	public static String md5s(String plainText) {
		String str = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
//			System.out.println("result: " + buf.toString());// 32位的加密
//			System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return str;
	}
	
	public static void main(String args[]){
		//UPDATE frame_users SET PASSWORD = 'f83e46f15a2be873c190766b96f7c24d' WHERE  username = 'demo' LIMIT 1;
		System.out.println(MD5.md5s("wx.2016")); //demo 密码
	}
}