package gyun.sample.domain.member.service.read;

import gyun.sample.domain.member.payload.request.admin.GetMemberListRequest;
import gyun.sample.domain.member.payload.response.admin.AllMemberResponse;
import gyun.sample.global.enums.GlobalFilterEnums;
import gyun.sample.global.enums.GlobalOrderEnums;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ReadAdminServiceTest {

    @Autowired
    ReadAdminService readAdminService;

    @Test
    void getList() {
        // Given
        GetMemberListRequest request = new GetMemberListRequest(1, 10, GlobalOrderEnums.CREATE_DESC, "", GlobalFilterEnums.ALL);

        // When
        Page<AllMemberResponse> result = readAdminService.getList(request);

        // Then
        assertEquals(2, result.getTotalElements(), "총 요소 수는 2이어야 합니다");
    }
}