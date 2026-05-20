package restgateway.json;

public interface JsonMapper {
    String toJson(Object obj);
    <T> T fromJson(String json, Class<T> clazz);
    <T> T fromJson(byte[] jsonBytes, Class<T> clazz);
}