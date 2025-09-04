package gyun.sample.domain.member.service.read;

import gyun.sample.domain.member.payload.request.MemberUserListRequest;
import gyun.sample.domain.member.payload.response.MemberListResponse;
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
        MemberUserListRequest request = new MemberUserListRequest(1, 10, GlobalOrderEnums.CREATE_DESC, "", GlobalFilterEnums.ALL, GlobalActiveEnums.ALL);

        // When
        Page<MemberListResponse> result = readAdminService.getList(request);

        // Then
        assertEquals(101, result.getTotalElements(), "총 요소 수는 101이어야 합니다");
    }
}