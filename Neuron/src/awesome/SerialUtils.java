package awesome;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Base64;

public class SerialUtils {
	
	public static String exportObject(Object object) {
		try {
			ByteArrayOutputStream str = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(str);
			stream.writeObject(object);
			return Base64.getEncoder().encodeToString(str.toByteArray());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T importObject(String s) {
		try {
			byte[] bytes = Base64.getDecoder().decode(s);
			ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return (T)stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static String debugString(Object o) {
		StringBuilder sb = new StringBuilder(o.getClass() + "[\n");
		for (Field f : o.getClass().getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers())) {
				sb.append(f.getName()).append('=');
				try {
					f.setAccessible(true);
					Object obj = f.get(o);
					Class<?> clazz = f.getType();
					if (clazz.isArray()) {
						if (clazz == boolean[].class)
							sb.append(Arrays.toString((boolean[])obj));
						else if (clazz == byte[].class)
							sb.append(Arrays.toString((byte[])obj));
						else if (clazz == short[].class)
							sb.append(Arrays.toString((short[])obj));
						else if (clazz == int[].class)
							sb.append(Arrays.toString((int[])obj));
						else if (clazz == long[].class)
							sb.append(Arrays.toString((long[])obj));
						else if (clazz == float[].class)
							sb.append(Arrays.toString((float[])obj));
						else if (clazz == double[].class)
							sb.append(Arrays.toString((double[])obj));
						else if (clazz == char[].class)
							sb.append(Arrays.toString((char[])obj));
						else
							sb.append(Arrays.deepToString((Object[])obj));
					} else
						sb.append(obj);
				} catch (Exception e) {
					sb.append("Unknown");
				}
				sb.append("\n");
			}
		}
		return sb.toString() + "]\n";
	}

}
