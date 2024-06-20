package gyun.sample.global.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class GenericEnumConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new GenericEnumConverter<>(targetType);
    }

    private static class GenericEnumConverter<T extends Enum<T>> implements Converter<String, T> {

        private final Class<T> enumType;

        public GenericEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            try {
                return Enum.valueOf(enumType, source.toUpperCase());
            }catch (Exception exception){
                return null;
            }
        }
    }
}
