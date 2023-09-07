package gyun.sample.global.annotaion;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

// 현재 로그인한 유저의 정보를 가져오기 위한 어노테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Schema(hidden = true)
@Documented
public @interface CurrentAccount {
}
