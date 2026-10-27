package su.nightexpress.nightcore.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class Reflex {

    private Reflex() {
    }

    public static boolean classExists(String path) {
        return findClass(path).isPresent();
    }

    @Nullable
    @Deprecated
    public static Class<?> getClass(String path, String name) {
        return getClass(path + "." + name);
    }

    @Nullable
    @Deprecated
    public static Class<?> getInnerClass(String path, String name) {
        return getClass(path + "$" + name);
    }

    @Nullable
    @Deprecated
    public static Class<?> getNMSClass(String path, String realName) {
        return getNMSClass(path, realName, null);
    }

    @Nullable
    @Deprecated
    public static Class<?> getNMSClass(String path, String realName, @Nullable String obfName) {
        Class<?> byRealName = getClass(path + "." + realName, false);
        if (byRealName != null) {
            return byRealName;
        }

        if (obfName != null) {
            return getClass(path + "." + obfName, false);
        }

        return null;
    }

    @Deprecated
    private static Class<?> getClass(String path) {
        return getClass(path, true);
    }

    @Deprecated
    private static Class<?> getClass(String path, boolean printError) {
        try {
            return Class.forName(path);
        }
        catch (ClassNotFoundException exception) {
            if (printError) exception.printStackTrace();
            return null;
        }
    }


    public static Class<?> safeClass(String path, String name, String altName) {
        return findClass(path, name, altName).orElseThrow(() -> new IllegalStateException("Could not load classes: '" +
            name + "' and '" + altName + "' in '" + path + "'"));
    }


    public static Class<?> safeClass(String path, String name) {
        return findClass(path, name).orElseThrow(() -> new IllegalStateException("Could not load class: '" + name +
            "' in '" + path + "'"));
    }


    public static Class<?> safeInnerClass(String path, String name) {
        return findInnerClass(path, name).orElseThrow(() -> new IllegalStateException("Could not load inner class: '" +
            name + "' in '" + path + "'"));
    }


    public static Class<?> safeClass(String path) {
        return findClass(path).orElseThrow(() -> new IllegalStateException("Could not load class: '" + path + "'"));
    }


    public static Optional<Class<?>> findClass(String path, String name, String altName) {
        return findClass(path, name).or(() -> findClass(path, altName));
    }


    public static Optional<Class<?>> findClass(String path, String name) {
        return findClass(path + "." + name);
    }


    public static Optional<Class<?>> findInnerClass(String path, String name) {
        return findClass(path + "$" + name);
    }


    public static Optional<Class<?>> findClass(String path) {
        try {
            return Optional.of(Class.forName(path));
        }
        catch (ClassNotFoundException exception) {
            return Optional.empty();
        }
    }

    public static Constructor<?> getConstructor(Class<?> source, Class<?>... types) {
        try {
            Constructor<?> constructor = source.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Object invokeConstructor(Constructor<?> constructor, Object... obj) {
        try {
            return constructor.newInstance(obj);
        }
        catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    public static <T> List<T> getStaticFields(Class<?> source, Class<T> type, boolean includeParent) {
        List<T> list = new ArrayList<>();

        for (Field field : Reflex.getFields(source, includeParent)) {
            //if (!field.getDeclaringClass().equals(source)) continue;
            //if (!field.canAccess(null)) continue;
            if (!Modifier.isStatic(field.getModifiers())) continue;
            //if (!Modifier.isFinal(field.getModifiers())) continue;
            if (!type.isAssignableFrom(field.getType())) continue;
            if (!field.trySetAccessible()) continue;

            try {
                list.add(type.cast(field.get(null)));
            }
            catch (IllegalArgumentException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        return list;
    }


    public static List<Field> getFields(Class<?> source) {
        return getFields(source, true);
    }


    public static List<Field> getFields(Class<?> source, boolean includeParent) {
        List<Field> result = new ArrayList<>();

        Class<?> lookupClass = source;
        while (lookupClass != null && lookupClass != Object.class) {
            if (!result.isEmpty()) {
                result.addAll(0, Arrays.asList(lookupClass.getDeclaredFields()));
            }
            else {
                Collections.addAll(result, lookupClass.getDeclaredFields());
            }
            if (!includeParent) {
                break;
            }
            lookupClass = lookupClass.getSuperclass();
        }

        return result;
    }

    public static Field getField(Class<?> source, String name) {
        try {
            return source.getDeclaredField(name);
        }
        catch (NoSuchFieldException exception) {
            Class<?> superClass = source.getSuperclass();
            return superClass == null ? null : getField(superClass, name);
        }
    }

    public static Object getFieldValue(Object source, String realName, String obfName) {
        Object byName = getFieldValue(source, realName);
        return byName == null ? getFieldValue(source, obfName) : byName;
    }

    public static Object getFieldValue(Object source, String name) {
        try {
            Class<?> clazz = source instanceof Class<?> ? (Class<?>) source : source.getClass();
            Field field = getField(clazz, name);
            if (field == null) return null;

            field.setAccessible(true);
            return field.get(source);
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean setFieldValue(Object source, String name, @Nullable Object value) {
        try {
            boolean isStatic = source instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) source : source.getClass();

            Field field = getField(clazz, name);
            if (field == null) return false;

            field.setAccessible(true);
            field.set(isStatic ? null : source, value);
            return true;
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Deprecated
    public static Method getMethod(Class<?> source, String realName, String obfName,
                                   Class<?>... params) {
        Method byName = getMethod(source, realName, params);
        return byName == null ? getMethod(source, obfName, params) : byName;
    }

    @Deprecated
    public static Method getMethod(Class<?> source, String name, Class<?>... params) {
        try {
            return source.getDeclaredMethod(name, params);
        }
        catch (NoSuchMethodException exception) {
            Class<?> superClass = source.getSuperclass();
            return superClass == null ? null : getMethod(superClass, name, params);
        }
    }


    public static Method safeMethod(Class<?> source, String name, String altName, Class<?>... params) {
        return findMethod(source, name, altName, params)
            .orElseThrow(() -> new IllegalStateException(
                "Could not find methods: '" + name + "' and '" + altName + "' in '" + source.getName() + "'")
            );
    }


    public static Method safeMethod(Class<?> source, String name, Class<?>... params) {
        return findMethod(source, name, params).orElseThrow(() -> new IllegalStateException("Could not find method: '" +
            name + "' in '" + source.getName() + "'"));
    }


    public static Optional<Method> findMethod(Class<?> source, String name, String altName, Class<?>... params) {
        return findMethod(source, name, params).or(() -> findMethod(source, altName, params));
    }


    public static Optional<Method> findMethod(Class<?> source, String name, Class<?>... params) {
        try {
            return Optional.of(source.getDeclaredMethod(name, params));
        }
        catch (NoSuchMethodException exception) {
            Class<?> superClass = source.getSuperclass();
            return superClass == null ? Optional.empty() : findMethod(superClass, name, params);
        }
    }


    public static Optional<Object> safeInvoke(Method method, @Nullable Object by, @Nullable Object... param) {
        try {
            method.setAccessible(true);
            return Optional.ofNullable(method.invoke(by, param));
        }
        catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object by, @Nullable Object... param) {
        return safeInvoke(method, by, param).orElse(null);
    }
}
