package gyun.sample.global.annotaion;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Schema(hidden = true)
@Documented
public @interface CurrentAccount {
}
