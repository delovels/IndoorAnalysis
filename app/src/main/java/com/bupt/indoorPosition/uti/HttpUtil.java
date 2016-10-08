package com.bupt.indoorPosition.uti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

public class HttpUtil {
	/**
	 * 保存Cookie
	 * 
	 * @param resp
	 */
	public static void saveCookies(HttpResponse httpResponse) {
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		if (headers == null)
			return;

		for (int i = 0; i < headers.length; i++) {
			String cookie = headers[i].getValue();
			String[] cookievalues = cookie.split(";");
			for (int j = 0; j < cookievalues.length; j++) {
				String[] keyPair = cookievalues[j].split("=");
				String key = keyPair[0].trim();
				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
				Global.cookieContainer.put(key, value);
			}
		}
	}

	/**
	 * 增加Cookie
	 * 
	 * @param request
	 */
	public static void addCookies(HttpPost request) {
		StringBuilder sb = new StringBuilder();
		Iterator iter = Global.cookieContainer.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();
			sb.append(key);
			sb.append("=");
			sb.append(val);
			sb.append(";");
		}
		request.addHeader("cookie", sb.toString());
	}

	/**
	 * 带cookie的发起http连接，并把返回的map结果转化为java对象
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static Map<String, Object> post(String url,
			Map<String, String> params) {

		HttpPost request = new HttpPost(url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			p.add(new BasicNameValuePair(key, params.get(key)));
		}
		addCookies(request);
		HttpClient httpClient = new DefaultHttpClient();
		String result = null;

		try {
			request.setEntity(new UrlEncodedFormEntity(p, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				saveCookies(response);
				result = new String(EntityUtils.toString(response.getEntity())
						.getBytes("UTF-8"), "UTF-8");// ISO_8859_1
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}
		// System.out.println(result);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (result != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				resultMap = mapper.readValue(result, Map.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultMap;
	}

	/**
	 * post 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String getRepsonseString(String url,
			Map<String, String> params) {
		HttpPost request = new HttpPost(url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			p.add(new BasicNameValuePair(key, params.get(key)));
		}
		HttpClient httpClient = new DefaultHttpClient();
		String result = null;

		try {
			request.setEntity(new UrlEncodedFormEntity(p, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = new String(EntityUtils.toString(response.getEntity())
						.getBytes("ISO_8859_1"), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	/**
	 * 发送请求，返回字符串响应
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String getResponseString(String url, String params) {
		String getUrl = "";
		// try {
		// getUrl = url + URLEncoder.encode(params, "utf-8");
		// } catch (UnsupportedEncodingException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// } // 将参数转为url编码
		getUrl = url + "?" + params;

		/** 发送httpget请求 */
		HttpGet request = new HttpGet(getUrl);
		// System.out.println(getUrl);
		String result = "";
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = new String(EntityUtils.toString(response.getEntity())
						.getBytes("ISO_8859_1"), "UTF-8");
				// result = EntityUtils.toString(response.getEntity());
				// System.out.println(result);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	/**
	 * 
	 * @param url
	 * @param params
	 * @param retKey
	 *            返回的键值对的键构成的数组
	 * @return 返回的是Json解析后的map对象
	 */
	public static Map<String, Object> getResponseJsonMap(String url,
			Map<String, Object> params, String[] retKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		for (String key : params.keySet()) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		// 去掉最后的&,如果为空，刚好去掉?
		String param = sb.substring(0, sb.length() - 1);
		String result = getResponseString(url, param);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = null;
		try {
			data = mapper.readValue(result, Map.class);
			System.out.println(data);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (data != null) {
			for (String key : retKey)
				map.put(key, data.get(key));
		}
		return map;
	}

	/**
	 * 
	 * @param url
	 * @param params
	 * @param saveParentPath
	 *            文件保存的父目录
	 * @param unique
	 *            文件名是否需要唯一，如果唯一，文件在本地的文件名唯一
	 * @return 文件名
	 */
	public static String getResponseFile(String url, String params,
			String saveParentPath, boolean unique) {

		/** 发送httpget请求 */
		HttpGet request = new HttpGet(url + params);

		HttpClient httpClient = new DefaultHttpClient();
		String fileName = null;
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();
				fileName = getFileName(response);
				String suffix = "";
				if (unique) {
					String[] datas = fileName.split("\\.");
					if (datas.length == 2) {
						suffix = datas[1];
						suffix = suffix.trim();
					}

					try {
						fileName = getUniqueFileName() + suffix;
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				File file = new File(saveParentPath, fileName);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				FileOutputStream fout = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = in.read(buffer)) != -1) {
					fout.write(buffer, 0, length);
				}
				fout.close();
				in.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}
		return fileName;
	}

	/**
	 * 32位哈希编码文件名
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String getUniqueFileName() throws NoSuchAlgorithmException {
		Date dt = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random rand = new Random();
		int nextInt = Math.abs(rand.nextInt()) % 99999;
		String timeName = sdf.format(dt) + nextInt;
		// System.out.println(timeName);
		// System.out.println(timeName.length());
		byte[] resultBytes = eccrypt(timeName);
		String fileName = hexString(resultBytes);
		// System.out.println("密文是：" + hexString(resultBytes));
		// System.out.println(fileName.length());
		return fileName;
	}

	/**
	 * 待编码字符串的处理
	 * 
	 * @param bytes
	 * @return
	 */
	public static String hexString(byte[] bytes) {
		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			int val = ((int) bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	/**
	 * 完成文件哈希计算
	 * 
	 * @param info
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] eccrypt(String info) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] srcBytes = info.getBytes();
		// 使用srcBytes更新摘要
		md5.update(srcBytes);
		// 完成哈希计算，得到result
		byte[] resultBytes = md5.digest();
		return resultBytes;
	}

	private static String getFileName(HttpResponse response) {
		Header contentHeader = response.getFirstHeader("Content-Disposition");
		String filename = null;
		if (contentHeader != null) {
			HeaderElement[] values = contentHeader.getElements();
			if (values.length == 1) {
				NameValuePair param = values[0].getParameterByName("filename");
				if (param != null) {
					try {
						filename = param.getValue();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return filename;
	}
}
