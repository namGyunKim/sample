package gyun.sample.domain.s3.enums;

import lombok.Getter;

@Getter
public enum UploadDirect {
    PROFILE("profile"),
    CUSTOM_GAME("custom_game");

    private final String value;

    UploadDirect(String value) {
        this.value = value;
    }
}
