package com.mwj.lhn.sgdk.pub;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import android.util.Log;



public class SocketHelper {
	public final static String FLAG = "@@@";

	private SocketHelper() {
	}

	public static String login(String device_id, String user_id, String user_pwd,String snum,String ag_name) {
		String command = "login" + FLAG + device_id + FLAG + user_id + FLAG + user_pwd+FLAG+snum+FLAG+ag_name;
		return request(command);
	}

	public static String login(String device_id, String user_id, String user_pwd,String snum,String ag_name,String flag) {
		String command = "login" + FLAG + device_id + FLAG + user_id + FLAG + user_pwd+FLAG+snum+FLAG+ag_name+FLAG+flag;
		return request(command);
	}
	public static String execAction(String login_id, String url_paras, String code,
									String filename, boolean hex) {
		if (url_paras==null || url_paras.length()<=0){
			url_paras = "code="+code;
		}
		String str = "exec_action" + FLAG + login_id + FLAG + url_paras;
		if (code != null && code.length() > 0) {
			str += (FLAG+code);
			if (filename != null && filename.length() > 0) {
				str += (FLAG + filename);
			}
		}

		String v = request(str);
		if (!hex)
			v = new String(Helper.hexstr2bytes(v));
		return v;
	}

	/** 连接到服务器 */
	public static byte[] connect(String command) {
		//	return connect("10.208.14.135", 3011, command, 20, 0);
		return connect("61.178.243.175", 3011, command, 20, 0);
	}

	public static String request(String command) {

		try {
			return new String(connect(command), "gb2312").trim();
		} catch (Exception ex) {
			return new String(connect(command));
		}
	}

	public static byte[] connect(String server_ip, int server_port,
								 String command, int timeout, int size) {
		Socket client = null;
		DataOutputStream out = null;
		DataInputStream in = null;
		try {
			client = new Socket(server_ip, server_port);
			client.setSoTimeout(timeout * 1000);
			out = new DataOutputStream((client.getOutputStream()));
			byte[] request = command.getBytes();
			out.write(request);
			out.flush();
			client.shutdownOutput();// 发送请求完毕

			in = new DataInputStream(client.getInputStream());
			byte[] reply = null;
			if (size <= 0) {// 未指定大小，则
				size = 1024;
				StringBuffer buffer = new StringBuffer();
				byte[] buf = new byte[size];
				int size1 = 0;
				while ((size1 = in.read(buf)) != -1) {
					if (size != size1) {// 读入的字节数<约定的长度
						byte[] buf1 = sub(buf, size1);
						buffer.append(bytes2hexstr(buf1));
					} else {
						buffer.append(bytes2hexstr(buf));
					}
				}
				reply = Helper.hexstr2bytes(buffer.toString());
			} else {
				reply = new byte[size];
				in.read(reply);
			}
			in.close();
			out.close();
			client.close();
			return reply;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] sub(byte[] buf, int len) {
		byte[] reply = new byte[len];
		for (int i = 0; i < len; i++) {
			reply[i] = buf[i];
		}
		return reply;
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

}