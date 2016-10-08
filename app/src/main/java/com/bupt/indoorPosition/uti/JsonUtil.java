package com.bupt.indoorPosition.uti;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	public static String serilizeJavaObject(Object value) {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = null;
		try {
			json = objectMapper.writeValueAsString(value);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	public static <T> List<T> convertListFromMap(Map<String, Object> map,
			String key, Class<T> tClass) {
		Object obj = map.get(key);
		String json = null;
		if (obj != null)
			json = serilizeJavaObject(obj);
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(
				List.class, tClass);
		// 如果是Map类型
		// mapper.getTypeFactory().constructParametricType(HashMap.class,String.class,
		// Bean.class);
		List<T> lst = null;
		try {
			lst = (List<T>) mapper.readValue(json, javaType);
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
		return lst;
	}

	public static <T> T convertObjectFromMap(Map<String, Object> map,
			String key, Class<T> tClass) {
		if (map == null)
			return null;
		Object obj = map.get(key);
		String json = null;
		if (obj != null)
			json = serilizeJavaObject(obj);
		else 
			return null;
		
		ObjectMapper mapper = new ObjectMapper();

		T returnVal = null;
		try {
			returnVal = (T) mapper.readValue(json, tClass);
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
		return returnVal;
	}

	private static String test = "{\"code\":1,\"list\":[{\"h\":1},{\"h\":0},{\"h\":2}]}";

	static class A {
		private int h;

		public A() {
		}

		public int getH() {
			return h;
		}

		public void setH(int h) {
			this.h = h;
		}

	}

	public static void main(String... arg) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> map = mapper.readValue(test, Map.class);
			for (String k : map.keySet()) {
				System.out.println(k);
				System.out.println(map.get(k).getClass());
				System.out.println(map.get(k));
			}
			List<A> list = convertListFromMap(map, "list", A.class);
			for (A i : list)
				System.out.println(i);
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
}
