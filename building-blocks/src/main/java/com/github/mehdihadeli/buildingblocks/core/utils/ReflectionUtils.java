package com.github.mehdihadeli.buildingblocks.core.utils;

import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class ReflectionUtils {

    private static List<String> cachedPackages = null;

    private ReflectionUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Gets all declared fields of a class, including inherited fields.
     *
     * @param clazz the class to analyze
     * @return a list of fields
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Invokes a method by name, even if it's private.
     *
     * @param target     the object to invoke the method on
     * @param methodName the method name
     * @param args       arguments to pass to the method
     * @return the result of the method call
     * @throws Exception if an error occurs
     */
    public static Object invokeMethod(Object target, String methodName, Object... args) throws Exception {
        Class<?> clazz = target.getClass();
        Method method = findMethod(clazz, methodName, getParameterTypes(args));
        if (method == null) {
            throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz.getName());
        }
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    /**
     * Finds a method in the class or its superclasses.
     *
     * @param clazz          the class to search
     * @param methodName     the method name
     * @param parameterTypes the method parameter types
     * @return the method or null if not found
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Gets a field value, even if it's private.
     *
     * @param target    the object to extract value from
     * @param fieldName the field name
     * @return the field value
     * @throws Exception if an error occurs
     */
    public static Object getFieldValue(Object target, String fieldName) throws Exception {
        Field field = getField(target.getClass(), fieldName);
        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found.");
        }
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * Sets a field value, even if it's private.
     *
     * @param target    the object whose field is to be modified
     * @param fieldName the field name
     * @param value     the value to set
     * @throws Exception if an error occurs
     */
    public static void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = getField(target.getClass(), fieldName);
        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found.");
        }
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Finds a field in the class or its superclasses.
     *
     * @param clazz     the class to search
     * @param fieldName the field name
     * @return the field or null if not found
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Creates a new instance of a class via reflection.
     *
     * @param clazz the class to instantiate
     * @param args  constructor arguments
     * @return new instance of the class
     * @throws Exception if instantiation fails
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) throws Exception {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == args.length) {
                constructor.setAccessible(true);
                return (T) constructor.newInstance(args);
            }
        }
        throw new NoSuchMethodException("No matching constructor found for " + clazz.getName());
    }

    /**
     * Checks if a class implements a given interface.
     *
     * @param clazz          the class to check
     * @param interfaceClass the interface to check for
     * @return true if the class implements the interface, false otherwise
     */
    public static boolean hasInterface(Class<?> clazz, Class<?> interfaceClass) {
        for (Class<?> iface : clazz.getInterfaces()) {
            if (iface.equals(interfaceClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a class or method is annotated with a specific annotation.
     *
     * @param clazz      the class to check
     * @param annotation the annotation to look for
     * @return true if present, false otherwise
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    /**
     * Gets the parameter types of the given arguments.
     *
     * @param args method arguments
     * @return an array of parameter types
     */
    private static Class<?>[] getParameterTypes(Object... args) {
        return args == null
                ? new Class<?>[0]
                : java.util.Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
    }

    /**
     * Retrieves all implemented interfaces of a given class, including inherited ones.
     */
    public static Set<Type> getAllImplementedInterfaces(Class<?> clazz) {
        Set<Type> interfaces = new HashSet<>();
        collectInterfaces(clazz, interfaces);
        return interfaces;
    }

    /**
     * Retrieves all superclasses of a given class up to Object.
     */
    public static Set<Class<?>> getAllSuperclasses(Class<?> clazz) {
        Set<Class<?>> superclasses = new HashSet<>();
        collectSuperclasses(clazz, superclasses);
        return superclasses;
    }

    /**
     * Retrieves the generic type parameter of a given object using Guava's TypeToken.
     *
     * @param object The object whose generic type is to be determined.
     * @param genericInterface The generic interface to inspect (e.g. ICommand.class).
     * @return The Class representing the actual generic type argument, or null if not found.
     */
    public static Class<?> getGenericTypeUsingGuava(Object object, Class<?> genericInterface) {
        TypeToken<?> typeToken = TypeToken.of(object.getClass()).getSupertype((Class<? super Object>) genericInterface);
        Type type = typeToken.getType();

        if (type instanceof ParameterizedType parameterizedType) {
            Type actualType = parameterizedType.getActualTypeArguments()[0];
            if (actualType instanceof Class<?>) {
                return (Class<?>) actualType;
            }
        }
        return null;
    }

    /**
     * Retrieves the generic type parameter of a given object using raw reflection.
     *
     * @param object The object whose generic type is to be determined.
     * @param genericInterface The generic interface to inspect (e.g. ICommand.class).
     * @return The Class representing the actual generic type argument, or null if not found.
     */
    public static Class<?> getGenericTypeUsingReflection(Object object, Class<?> genericInterface) {
        // get all base level interfaces for the object
        Set<Type> interfaces = new HashSet<>();
        collectInterfaces(object.getClass(), interfaces);

        for (Type type : interfaces) {
            if (type instanceof ParameterizedType parameterizedType) {
                if (genericInterface.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class<?>) {
                        return (Class<?>) actualType;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all generic type parameters of a given object using Guava's TypeToken.
     *
     * @param object The object whose generic types are to be determined.
     * @param genericInterface The generic interface to inspect (e.g. ICommand.class).
     * @return A list of Classes representing the actual generic type arguments, or an empty list if not found.
     */
    public static List<Class<?>> getGenericTypesUsingGuava(Object object, Class<?> genericInterface) {
        List<Class<?>> genericTypes = new ArrayList<>();

        // Use unchecked cast to satisfy generic type requirements
        @SuppressWarnings("unchecked")
        TypeToken<?> typeToken = TypeToken.of(object.getClass()).getSupertype((Class<? super Object>) genericInterface);

        Type type = typeToken.getType();

        if (type instanceof ParameterizedType parameterizedType) {
            for (Type actualType : parameterizedType.getActualTypeArguments()) {
                if (actualType instanceof Class<?>) {
                    genericTypes.add((Class<?>) actualType);
                }
            }
        }
        return genericTypes;
    }

    /**
     * Retrieves all generic type parameters of a given object using raw reflection.
     *
     * @param object The object whose generic types are to be determined.
     * @param genericInterface The generic interface to inspect (e.g. ICommand.class).
     * @return A list of Classes representing the actual generic type arguments, or an empty list if not found.
     */
    public static List<Class<?>> getGenericTypesUsingReflection(Object object, Class<?> genericInterface) {
        // get all base level interfaces for the object
        Set<Type> interfaces = new HashSet<>();
        collectInterfaces(object.getClass(), interfaces);

        List<Class<?>> genericTypes = new ArrayList<>();

        for (Type type : interfaces) {
            if (type instanceof ParameterizedType parameterizedType) {
                if (genericInterface.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    for (Type actualType : parameterizedType.getActualTypeArguments()) {
                        if (actualType instanceof Class<?>) {
                            genericTypes.add((Class<?>) actualType);
                        }
                    }
                }
            }
        }
        return genericTypes;
    }

    /**
     * Gets the generic type parameter from a class that implements a generic interface.
     *
     * @param clazz The class to inspect
     * @return The generic type parameter, or null if not found
     */
    public static Class<?> getGenericInterfaceTypeParameter(Class<?> clazz) {
        return Arrays.stream(clazz.getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .map(type -> type.getActualTypeArguments()[0])
                .filter(type -> type instanceof Class<?>)
                .map(type -> (Class<?>) type)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the generic type arguments of a class.
     *
     * @param clazz the class to analyze
     * @return an array of generic type arguments
     */
    public static Class<?>[] getGenericTypeArguments(Class<?> clazz) {
        Type superclass = clazz.getGenericSuperclass();

        if (superclass instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
            List<Class<?>> classArguments = new ArrayList<>();

            for (Type type : typeArguments) {
                if (type instanceof Class<?>) {
                    classArguments.add((Class<?>) type);
                } else {
                    // Handle cases where the type is not a Class (e.g. TypeVariable, WildcardType)
                    classArguments.add(Object.class); // Fallback for unknown types
                }
            }
            return classArguments.toArray(new Class<?>[0]);
        }
        return new Class<?>[] {};
    }

    /**
     * Retrieves the actual generic type argument of type (Class<TEntity>) from a constructor parameter.
     *
     * <p>This method inspects the constructors of the provided class to find a parameter of type
     * {@code Class<TEntity>} and extracts the actual entity class type.
     * It is useful when working with generic repositories, factories, or services that accept
     * an entity type as a constructor argument.</p>
     *
     * @param clazz      The class to inspect for the generic parameter.
     * @param paramIndex The index of the constructor parameter to check.
     * @return The actual generic type argument (e.g., TEntity's class), or {@code null} if not found.
     */
    public static Class<?> getGenericClassArgument(Class<?> clazz, int paramIndex) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            Type[] paramTypes = constructor.getGenericParameterTypes();

            if (paramIndex < paramTypes.length && paramTypes[paramIndex] instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) paramTypes[paramIndex];
                Type rawType = parameterizedType.getRawType();

                if (rawType instanceof Class<?> && rawType.equals(Class.class)) {
                    Type[] typeArgs = parameterizedType.getActualTypeArguments();
                    if (typeArgs.length == 1 && typeArgs[0] instanceof Class<?>) {
                        return (Class<?>) typeArgs[0]; // Return the actual entity class
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts the generic type argument from a given class instance of the form {@code Class<TEntity>}.
     *
     * <p>This method checks if the provided type is parameterized (e.g., {@code Class<Product>})
     * and extracts the actual type argument (e.g., {@code Product.class}). It is useful
     * when dealing with generic class references in repositories or services.</p>
     *
     * @param type The parameterized class reference (e.g., Product.class).
     * @return The generic type argument {@code TEntity}, or {@code null} if not found.
     */
    public static Class<?> getGenericTypeArgument(Class<?> type) {
        Type genericSuperclass = type.getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?>) {
                return (Class<?>) typeArguments[0];
            }
        }
        return null;
    }

    /**
     * Retrieves the raw class type of a generic type using Guava.
     *
     * @param genericType The generic type to analyze.
     * @return The raw class of the provided generic type.
     */
    public static Class<?> getRawTypeUsingGuava(Type genericType) {
        return TypeToken.of(genericType).getRawType();
    }

    /**
     * Gets all superclasses and interfaces of a given class using Guava.
     *
     * @param clazz The class to inspect.
     * @return A list of classes representing all superclasses and implemented interfaces.
     */
    public static List<Class<?>> getAllSuperTypesUsingGuava(Class<?> clazz) {
        List<Class<?>> superTypes = new ArrayList<>();
        TypeToken<?> typeToken = TypeToken.of(clazz);

        for (TypeToken<?> type : typeToken.getTypes()) {
            superTypes.add(type.getRawType());
        }
        return superTypes;
    }

    /**
     * Checks if a class is a subclass of another class using Guava.
     *
     * @param subClass The class to check.
     * @param superClass The superclass/interface to check against.
     * @return true if subClass is assignable to superClass, false otherwise.
     */
    public static boolean isSubclassOfUsingGuava(Class<?> subClass, Class<?> superClass) {
        return TypeToken.of(subClass).isSubtypeOf(superClass);
    }

    /**
     * Finds all implemented interfaces of a given class using Guava.
     *
     * @param clazz The class to inspect.
     * @return A list of implemented interfaces.
     */
    public static List<Class<?>> getImplementedInterfacesUsingGuava(Class<?> clazz) {
        List<Class<?>> interfaces = new ArrayList<>();
        TypeToken<?> typeToken = TypeToken.of(clazz);

        for (TypeToken<?> type : typeToken.getTypes()) {
            if (type.getRawType().isInterface()) {
                interfaces.add(type.getRawType());
            }
        }
        return interfaces;
    }

    public static Class<?> findClassByName(String className) {
        // Try to find the class directly (fully qualified name)
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            // If it's not found, continue searching in known packages
        }

        // Retrieve all packages dynamically
        var packages = getAllPackages();

        // Try to find the class using its simple name in known packages
        for (String packageName : packages) {
            String fullClassName = packageName + "." + className;
            try {
                // attempt to load the class
                return Class.forName(fullClassName);
            } catch (ClassNotFoundException ignored) {
                // Continue searching in other packages
            }
        }

        return null;
    }

    public static List<String> getAllPackages() {
        // Return cached packages if already loaded
        if (cachedPackages != null) {
            return cachedPackages;
        }

        // Load and cache packages
        Package[] packages = Package.getPackages();
        cachedPackages = Arrays.stream(packages).map(Package::getName).toList();

        return cachedPackages;
    }

    public static List<String> getAllPackages(String packagePrefix) {
        Set<String> applicationPackages = new HashSet<>();

        for (Package pkg : Package.getPackages()) {
            String packageName = pkg.getName();

            // Only keep packages that start with our application’s base package (e.g., "com.myapp")
            if (packageName.startsWith(packagePrefix)) {
                applicationPackages.add(packageName);
            }
        }

        return applicationPackages.stream().toList();
    }

    /**
     * Helper method to recursively collect interfaces from a class and its hierarchy.
     */
    private static void collectInterfaces(Class<?> clazz, Set<Type> interfaces) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        for (Type genericInterface : clazz.getGenericInterfaces()) {
            if (interfaces.add(genericInterface)) {
                if (genericInterface instanceof Class) {
                    collectInterfaces((Class<?>) genericInterface, interfaces);
                }
            }
        }

        collectInterfaces(clazz.getSuperclass(), interfaces);

        for (Class<?> iface : clazz.getInterfaces()) {
            collectInterfaces(iface, interfaces);
        }
    }

    /**
     * Helper method to recursively collect all superclasses.
     */
    private static void collectSuperclasses(Class<?> clazz, Set<Class<?>> superclasses) {
        while (clazz != null && clazz != Object.class) {
            clazz = clazz.getSuperclass();
            if (clazz != null && clazz != Object.class) {
                superclasses.add(clazz);
            }
        }
    }
}
