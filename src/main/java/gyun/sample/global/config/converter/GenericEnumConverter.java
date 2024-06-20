package gyun.sample.global.config.converter;

import org.springframework.core.convert.converter.Converter;

public class GenericEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumType;

    public GenericEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T convert(String source) {
        return Enum.valueOf(enumType, source.toUpperCase());
    }
}