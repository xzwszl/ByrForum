package xzw.szl.byr.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
	
	
	//解析json数据，利用反射机制
	public static <T> Object toBean(String jsonString,Class<T> A) {
		
		
		T t = null;
		boolean isMatched = false;
		try {
			
			if (jsonString == null)  return null;
			
			
			if (jsonString.equals("false")) {
				return null;
			}
			JSONObject jb = new JSONObject(jsonString);
			
			t = A.newInstance();
			Field[] field = A.getDeclaredFields();
			
			
			Iterator<String> keyIter = jb.keys();
			while (keyIter.hasNext()) {
				String key = (String) keyIter.next();
				for (int i=0;i<field.length;i++) {
					String name = field[i].getName();
					if (name.equals("classes")) name = "class"; //针对所属专区的特殊处理
					
					if (name.equals(key)) {
						isMatched = true;
						break;
					}
				}
				if (isMatched) break;
			}
			for (int i=0;i<field.length;i++) {
				
				String name = field[i].getName();
				

				if (name.equals("classes")) name = "class"; //针对所属专区的特殊处理
				
				if (jb.has(name)) {
					
					Object obj = jb.get(name);
					if (obj.equals(null)) continue;
					
					Object object = null;
					Class<?> fieldClazz = field[i].getType();
					
					//分情况讨论，是复合类型或者容器类型,
					if (fieldClazz.isAssignableFrom(ArrayList.class)) {  // List<T>...
						
						Type fc = field[i].getGenericType();
						
						
						if (fc == null) continue;
						
						if (fc instanceof ParameterizedType) {
							Class<?> actualClazz = (Class<?>) ((ParameterizedType) fc).getActualTypeArguments()[0];
							
							object = fieldClazz.newInstance();																		
							JSONArray ja = (JSONArray) obj;
							
							Method method = fieldClazz.getMethod("add",Object.class);
							for (int j=0;j<ja.length();j++) {
								Object item = null;
								if (actualClazz.getName().startsWith("java.lang")) {
									//Class actualClazz
									item = actualClazz.getConstructor(actualClazz).newInstance(ja.get(j));
								} else {
									item = toBean(ja.getString(j),actualClazz);
								}
								method.invoke(object, item);
							}
						}
					} else if (fieldClazz.isPrimitive() || fieldClazz.getName().startsWith("java.lang")){
						object = obj;
					} else {
						
							object = toBean(jb.getString(name) , fieldClazz);
							
							if (object == null && name.equals("user")) { 
								String userStr = "{\"id\":\""+jb.getString(name)+"\"}";          //位置用户
								object = toBean(userStr , fieldClazz);
							}
					}
					
					if (fieldClazz.isInstance(String.valueOf(object))) {
						A.getMethod("set" + upFirst(field[i].getName()), fieldClazz).invoke(t, String.valueOf(object));
					} else {
						A.getMethod("set" + upFirst(field[i].getName()), fieldClazz).invoke(t, object);
					}
				}
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isMatched)
			return t;
		return null;
	}
	
	public static String upFirst(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	
	public static Object getValue(String attr,Object object) {
		Class<?> cls = object.getClass();
		
	//	Class<?> clazz;
		try {
			//clazz = cls.getDeclaredField(attr).getType();
			Method method = cls.getMethod("get" + upFirst(attr),(Class<?>)null);
			return method.invoke(object, (Class<?>)null);
		}  catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
