package gyun.sample.domain.aws.enums;

import lombok.Getter;

@Getter
public enum UploadDirect {
    MEMBER_PROFILE("memberProfile"),
    ;

    private final String value;

    UploadDirect(String value) {
        this.value = value;
    }
}
