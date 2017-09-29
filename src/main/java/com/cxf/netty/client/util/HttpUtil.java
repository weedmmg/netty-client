package com.cxf.netty.client.util;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpUtil {

	protected HttpURLConnection initHttp(String url, String uri) throws Exception {
		URL localURL = new URL(url + uri);
		URLConnection connection = localURL.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		return httpURLConnection;
	}

	protected InputStream doOutputIn(HttpURLConnection conn, String method, byte[] protobufBytes) throws Exception {
		conn.setDoOutput(true);
		conn.setRequestMethod(method);

		conn.setRequestProperty("Content-Type", "application/x-protobuf");
		conn.setRequestProperty("Accept", "application/x-protobuf");
		OutputStream wr = conn.getOutputStream();
		wr.write(protobufBytes);
		wr.flush();
		wr.close();
		return conn.getInputStream();
	}

	protected HttpURLConnection doOutputJson(HttpURLConnection conn, String method, String json) throws Exception {
		conn.setDoOutput(true);
		conn.setRequestMethod(method);

		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Accept", "application/x-www-form-urlencoded");

		DataOutputStream out = new DataOutputStream(conn.getOutputStream());

		out.writeBytes(json);
		out.flush();
		out.close();
		return conn;
	}

	protected InputStream doUpload(HttpURLConnection conn, String method, byte[] fileBytes,
			Map<String, Object> propertys) throws Exception {
		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/x-protobuf;charset=UTF-8");
		conn.setRequestProperty("Accept", "application/x-protobuf");
		if (propertys != null) {
			for (String key : propertys.keySet()) {
				System.out.println(key);
				conn.addRequestProperty(key, String.valueOf(propertys.get(key)));
			}
		}
		OutputStream wr = conn.getOutputStream();
		wr.write(fileBytes);
		wr.flush();
		wr.close();
		return conn.getInputStream();
	}
}
