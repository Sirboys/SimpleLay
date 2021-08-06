// Original code by https://github.com/aadnk; modified by https://github.com/epserv

package com.comphenix.tinyprotocol;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class Reflection {
	private static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
	private static final String NMS_PREFIX;
	private static final String VERSION;

	private Reflection() {
	}

	public static <T> Reflection.FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
		return getField(target, name, fieldType, 0);
	}

	public static <T> Reflection.FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
		return getField(getClass(className), name, fieldType, 0);
	}

	public static <T> Reflection.FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
		return getField(target, null, fieldType, index);
	}

	public static <T> Reflection.FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
		return getField(getClass(className), fieldType, index);
	}

	@SuppressWarnings("unchecked")
	private static <T> Reflection.FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {

		for (final Field field : target.getDeclaredFields()) {
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType())
					&& index-- <= 0) {
				field.setAccessible(true);
				return new Reflection.FieldAccessor<T>() {
					public T get(Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException ex) {
							throw new RuntimeException("Cannot access reflection.", ex);
						}
					}

					public void set(Object target, T value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException ex) {
							throw new RuntimeException("Cannot access reflection.", ex);
						}
					}

					public boolean hasField(Object target) {
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}

		if (target.getSuperclass() != null) {
			return (FieldAccessor<T>) getField(target.getSuperclass(), name, fieldType, index);
		} else {
			throw new IllegalArgumentException("Cannot find field with type " + fieldType);
		}
	}

	public static Reflection.MethodInvoker<?> getMethod(String className, String methodName, Class<?>... params) {
		return getTypedMethod(getClass(className), methodName, null, params);
	}

	public static Reflection.MethodInvoker<?> getMethod(Class<?> clazz, String methodName, Class<?>... params) {
		return getTypedMethod(clazz, methodName, null, params);
	}

	@SuppressWarnings("unchecked")
	public static <R> Reflection.MethodInvoker<R> getTypedMethod(Class<?> clazz, String methodName, Class<R> returnType,
			Class<?>... params) {
		for (final Method method : clazz.getDeclaredMethods()) {
			if ((methodName == null || method.getName().equals(methodName))
					&& (returnType == null || method.getReturnType().equals(returnType))
					&& Arrays.equals(method.getParameterTypes(), params)) {
				method.setAccessible(true);
				return (target, arguments) -> {
					try {
						return (R) method.invoke(target, arguments);
					} catch (Exception ex) {
						throw new RuntimeException("Cannot invoke method " + method, ex);
					}
				};
			}
		}

		if (clazz.getSuperclass() != null) {
			return getTypedMethod(clazz.getSuperclass(), methodName, returnType, params);
		} else {
			throw new IllegalStateException(
					String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
		}
	}

	public static Reflection.ConstructorInvoker<?> getConstructor(String className, Class<?>... params) {
		return getConstructor(getClass(className), params);
	}

	@SuppressWarnings("unchecked")
	public static <R> Reflection.ConstructorInvoker<R> getConstructor(Class<R> clazz, Class<?>... params) {
		for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (Arrays.equals(constructor.getParameterTypes(), params)) {
				constructor.setAccessible(true);
				return arguments -> {
					try {
						return (R) constructor.newInstance(arguments);
					} catch (Exception ex) {
						throw new RuntimeException("Cannot invoke constructor " + constructor, ex);
					}
				};
			}
		}

		throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
	}

	public static Class<?> getUntypedClass(String lookupName) {
		return getClass(lookupName);
	}

	public static Class<?> getClass(String lookupName) {
		return getCanonicalClass(expandVariables(lookupName));
	}

	public static Class<?> getMinecraftClass(String name) {
		return getCanonicalClass(NMS_PREFIX + "." + name);
	}
	public static Class<?> getMinecraftClass(String name, String pack) {
		try {
			return getMinecraftClass(name);
		} catch (Exception e) {
			return getCanonicalClass(pack + "." + name);
		}
	}
	public static Class<?> getArrayOfMinecraftClass(String name) {
		return getCanonicalClass("[L" + NMS_PREFIX + "." + name + ";");
	}
	//fixme
	public static Class<?> getArrayOfMinecraftClass(String name, String pack) {
		try {
			return getCanonicalClass("[L" + NMS_PREFIX + "." + name + ";");
		} catch (Exception e) {
			return getCanonicalClass("[L" + pack + "." + name + ";");
		}
	}
	public static Class<?> getCraftBukkitClass(String name) {
		return getCanonicalClass(OBC_PREFIX + "." + name);
	}

	public static Object getEnumConstant(String className, String constantName) {
		return getEnumConstant(getClass(className), constantName);
	}

	public static <T> T getEnumConstant(Class<T> clazz, String constantName) {
		for (T constant : clazz.getEnumConstants()) {
			if (constant.toString().equals(constantName)) {
				return constant;
			}
		}
		return null;
	}

	private static Class<?> getCanonicalClass(String canonicalName) {
		try {
			return Class.forName(canonicalName);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Cannot find " + canonicalName, ex);
		}
	}

	private static String expandVariables(String name) {
		String str = name.replace("{nms}", NMS_PREFIX + ".").replace("{obc}", OBC_PREFIX + ".")
				.replace("{version}", VERSION + ".");
		str = str.replaceAll("\\.{2,}", ".");
		if (str.endsWith("."))
			str = str.substring(0, str.length() - 1);
		return str;
	}

	static {
		NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
		VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
	}
	
	public static String getVersion() {
		return VERSION;
	}

	public interface FieldAccessor<T> {
		T get(Object target);

		void set(Object target, T value);

		boolean hasField(Object target);
	}

	public interface MethodInvoker<R> {
		R invoke(Object target, Object... arguments);
	}

	public interface ConstructorInvoker<T> {
		T invoke(Object... arguments);
	}
}
