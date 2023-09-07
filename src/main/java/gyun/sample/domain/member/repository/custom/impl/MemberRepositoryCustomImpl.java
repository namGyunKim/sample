package gyun.sample.domain.member.repository.custom.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gyun.sample.domain.account.enums.AccountRole;
import gyun.sample.domain.member.entity.Member;
import gyun.sample.domain.member.entity.QMember;
import gyun.sample.domain.member.repository.custom.MemberRepositoryCustom;
import gyun.sample.domain.member.repository.utils.MemberRepositoryCustomUtil;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    //    querydsl
    private final JPAQueryFactory jpaQueryFactory;
    //    querydsl util
    private final MemberRepositoryCustomUtil memberRepositoryCustomUtil;

    QMember member = QMember.member;


    //    로그인 아이디로 활성화 및 비활성화 된 유저 존재여부 확인
    @Override
    public boolean existByLoginIdAndActiveAll(String loginId) {
        BooleanBuilder builder = memberRepositoryCustomUtil.existByLoginIdAndActiveAllBuilder(loginId);
        return !jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetch().isEmpty();
    }

    //    로그인 아이디와 권환으로 활성화 된 유저 존재여부 확인
    @Override
    public boolean existByLoginIdAndRole(String loginId,AccountRole role) {
        BooleanBuilder builder = memberRepositoryCustomUtil.existByLoginIdAndRoleBuilder(loginId,role);
        return !jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetch().isEmpty();
    }

    //    권한으로 활성화 된 유저 존재여부 확인
    @Override
    public boolean existByRole(AccountRole role) {
        BooleanBuilder builder = memberRepositoryCustomUtil.existByRoleBuilder(role);
        return !jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetch().isEmpty();
    }

    //    로그인 아이디와 권한으로 활성화 된 유저 찾기
    @Override
    public Optional<Member> findByLoginIdAndRole(String loginId, AccountRole role) {
        BooleanBuilder builder = memberRepositoryCustomUtil.findByLoginIdAndRoleBuilder(loginId,role);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetchOne());
    }

    //    로그인 아이디로 활성화 된 유저 찾기
    @Override
    public Optional<Member> findByLoginId(String loginId) {
        BooleanBuilder builder = memberRepositoryCustomUtil.findByLoginIdBuilder(loginId);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(builder)
                .fetchOne());
    }
}
