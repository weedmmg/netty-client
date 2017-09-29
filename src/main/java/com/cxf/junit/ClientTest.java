package com.cxf.junit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.junit.Test;

import com.cxf.netty.client.util.HttpUtil;

public class ClientTest extends HttpUtil {
	String url = "http://127.0.0.1:8888";

	// String url = "http://122.144.133.20:58887";

	String apiurl = "http://127.0.0.1:8080";

	@Test
	public void test() {
		System.out.println("this is test");
	}

	@Test
	public void httpLogin() {
		String str = "this is netty test";
		try {
			byte[] buff = str.getBytes("UTF8");
			HttpURLConnection conn = doOutputJson(initHttp(url, ""), "POST", str);

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			System.out.println(sb);
			reader.close();
			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
