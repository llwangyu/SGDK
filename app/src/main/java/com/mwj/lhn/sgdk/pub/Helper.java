package com.mwj.lhn.sgdk.pub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class Helper {
	public static void log(String title, String info) {
		Log.i("info", title + ":  " + info);
	}

	public static String[] splitStr(String srcStr, String spChar,
									int maxNumSplit) {
		if (srcStr == null) {// 若是空串，则返回null
			return null;
		}
		if (spChar == null) {// 如果未指定分隔符号，则返回源串
			return new String[] { srcStr };
		} else {
			final int SIZE = 10;
			int index = 0;
			String[] result = new String[SIZE];
			int delimLen = spChar.length();
			int nextIndex = 0;
			int foundIndex = -1;
			int resultArraySize = result.length;

			while ((foundIndex = srcStr.indexOf(spChar, nextIndex)) != -1) {
				if (index == resultArraySize) {// 预分配空间不够，则增加空间
					String[] temp = result;
					result = new String[result.length + SIZE];
					System.arraycopy(temp, 0, result, 0, temp.length);
					resultArraySize = result.length;
				}
				result[index++] = srcStr.substring(nextIndex, foundIndex);
				nextIndex = foundIndex + delimLen;
				if (maxNumSplit > 0 && index >= maxNumSplit) {
					break;
				}
			}

			if (result.length >= index) {
				String[] temp = result;
				result = new String[index + 1];
				System.arraycopy(temp, 0, result, 0, index);
			}

			result[index++] = srcStr.substring(nextIndex);

			return result;
		}
	}

	public static String unzip(byte[] values) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(values);
		ZipInputStream zis = new ZipInputStream(bis);
		zis.getNextEntry();
		ObjectInputStream ois = new ObjectInputStream(zis);
		Object obj = ois.readObject();
		ois.close();
		zis.close();
		bis.close();
		return new String(hexstr2bytes(obj.toString()));
	}

	public static byte[] hexstr2bytes(String hexstr) {
		if (hexstr != null) {
			int len = hexstr.length() / 2;
			byte[] result = new byte[len];
			char[] array = hexstr.toCharArray();
			for (int i = 0; i < len; i++) {
				int pos = i * 2;
				result[i] = (byte) (toByte(array[pos]) << 4 | toByte(array[pos + 1]));
			}
			return result;
		}
		return null;
	}

	public static String bytes2hexstr(byte[] array) {
		if (array != null) {
			StringBuffer buffer = new StringBuffer();
			int len = array.length;
			for (int i = 0; i < len; i++) {
				String str = Integer.toHexString(0xFF & array[i]);
				if (str.length() < 2) {
					buffer.append(0);
				}
				buffer.append(str.toUpperCase());
			}
			return buffer.toString();
		}
		return null;
	}

	public static byte toByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**utf编码转换为汉字*/
	public static String utf2gb(String s) {
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			switch (ch = s.charAt(i)) {
				case '%':
					ch = s.charAt(++i);
					int hb = (Character.isDigit((char) ch) ? ch - '0'
							: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					ch = s.charAt(++i);
					int lb = (Character.isDigit((char) ch) ? ch - '0'
							: 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
					b = (hb << 4) | lb;
					break;
				case '+':
					b = ' ';
					break;
				default:
					b = ch;
			}

			if ((b & 0xc0) == 0x80) {
				sumb = (sumb << 6) | (b & 0x3f);
				if (--more == 0)
					sbuf.append((char) sumb);
			} else if ((b & 0x80) == 0x00) {
				sbuf.append((char) b);
			} else if ((b & 0xe0) == 0xc0) {
				sumb = b & 0x1f;
				more = 1;
			} else if ((b & 0xf0) == 0xe0) {
				sumb = b & 0x0f;
				more = 2;
			} else if ((b & 0xf8) == 0xf0) {
				sumb = b & 0x07;
				more = 3;
			} else if ((b & 0xfc) == 0xf8) {
				sumb = b & 0x03;
				more = 4;
			} else {
				sumb = b & 0x01;
				more = 5;
			}
		}
		return sbuf.toString();
	}

	/**压缩二进制文件*/
	public static String zipBinFile(String filename) throws IOException{
		byte [] value = loadBinFile(filename);
		return zip(bytes2hexstr(value));
	}

	/**压缩字符串*/
	public static String zip(String value) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);
		zos.setMethod(8);
		zos.putNextEntry(new ZipEntry("zip"));
		ObjectOutputStream oos = new ObjectOutputStream(zos);
		oos.writeObject(value);
		oos.flush();
		oos.close();

		zos.close();
		byte [] values = bos.toByteArray();
		return bytes2hexstr(values);
	}

	/**以二进制形式读取文件*/
	public static byte[] loadBinFile(String filename) throws IOException{
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		int size = fis.available();
		byte [] content = new byte[size];
		fis.read(content);
		fis.close();
		return content;
	}

	/**汉字转为utf编码*/
	public static String gb2utf(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = String.valueOf(c).getBytes("utf-8");
				} catch (Exception ex) {
					System.out.println(ex);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}


}
