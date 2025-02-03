package com.github.mehdihadeli.buildingblocks.core.utils;

import java.util.*;

public class TypeMapperUtils {

    private static final Map<Class<?>, Set<String>> typeToNames = new HashMap<>();
    private static final Map<String, Class<?>> nameToType = new HashMap<>();

    /**
     * Adds a type mapping using the class's simple name
     * @param type The class type to be mapped
     * @return The simple name that was added
     * @throws IllegalArgumentException if type is null or if simple name is already mapped to a different type
     */
    public static String addShortTypeName(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        String simpleName = type.getSimpleName();
        addTypeNameInternal(type, simpleName);
        return simpleName;
    }

    /**
     * Adds a type mapping with a custom type name
     * @param type The class type to be mapped
     * @param typeName The custom name for the type
     * @return The custom type name that was added
     * @throws IllegalArgumentException if parameters are invalid or if name is already mapped to a different type
     */
    public static String addShortTypeName(Class<?> type, String typeName) {
        if (type == null || typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("Type and typeName cannot be null or empty");
        }
        addTypeNameInternal(type, typeName);
        return typeName;
    }

    /**
     * Adds a type mapping with the full qualified class name
     * @param type The class type to be mapped
     * @return The full qualified name that was added
     * @throws IllegalArgumentException if type is null
     */
    public static String addFullTypeName(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        String fullName = type.getName();
        addTypeNameInternal(type, fullName);
        return fullName;
    }

    private static void addTypeNameInternal(Class<?> type, String typeName) {
        // Check if typeName is already mapped to a different type
        Class<?> existingType = nameToType.get(typeName);
        if (existingType != null && !existingType.equals(type)) {
            throw new IllegalArgumentException(
                    "TypeName '" + typeName + "' is already mapped to type " + existingType.getName());
        }

        // Add to both maps
        typeToNames
                .computeIfAbsent(type, k -> {
                    // create a new set if type not already registered
                    return new HashSet<>();
                })
                .add(typeName);
        nameToType.put(typeName, type);
    }

    /**
     * Gets the type class based on the type name
     * @param typeName The name to look up
     * @return The corresponding Class object, or null if not found
     */
    public static Class<?> getType(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("TypeName cannot be null or empty");
        }
        return nameToType.get(typeName);
    }

    /**
     * Gets all registered type names for a given type
     * @param type The class type to look up
     * @return Set of all registered type names, or empty set if none found
     */
    public static Set<String> getAllTypeNames(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        return Collections.unmodifiableSet(typeToNames.getOrDefault(type, Collections.emptySet()));
    }
}
