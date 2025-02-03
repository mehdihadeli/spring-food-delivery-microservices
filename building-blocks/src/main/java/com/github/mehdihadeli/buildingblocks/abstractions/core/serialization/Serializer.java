package com.github.mehdihadeli.buildingblocks.abstractions.core.serialization;

public interface Serializer {

    String getContentType();

    /**
     * Serializes the given object into a string.
     *
     * @param obj the object to serialize
     * @return the serialized string
     */
    String serialize(Object obj);

    String serializePretty(Object object);

    /**
     * Deserialize the given string into an object of type T.
     *
     * @param payload the string to deserialize
     * @param <T> the type of the object to return
     * @return the deserialized object of type T
     */
    <T> T deserialize(String payload);

    /**
     * Deserialize the given string into an object of a specified type.
     *
     * @param payload the string to deserialize
     * @param type the Class type of the object to return
     * @return the deserialized object
     */
    Object deserialize(String payload, Class<?> type);
}
