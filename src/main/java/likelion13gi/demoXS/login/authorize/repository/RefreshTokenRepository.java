package likelion13gi.demoXS.login.authorize.repository;

import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.login.authorize.jwt.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RefreshToken 저장소
 * - 사용자(User)와 1:1로 매핑된 RefreshToken을 조회/삭제한다.
 * - Spring Data JPA의 파생 쿼리와 @Query(JPQL)를 혼용.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 사용자 엔티티로 RefreshToken 한 건을 조회
    // - 존재하지 않을 수 있으므로 Optional로 감싼다.
    Optional<RefreshToken> findByUser(User user);

    // 사용자 기준으로 RefreshToken을 삭제 (JPQL 직접 정의)
    // - @Modifying: DML(DELETE/UPDATE) 쿼리임을 명시
    // - 트랜잭션 경계(@Transactional)는 서비스 레이어에서 감싸는 것을 권장
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);

    void deleteById(Long userId);
}

/* 1) 왜 필요한가?
 - 유저의 RefreshToken을 조회하기 편합니다.
 - 또한, 로그아웃을 하면 RefreshToken은 DB에서 삭제되어야 하기에, 위 두 메서드를 선언하는 인터페이스가 필요합니다.
 2) 없다면/틀리면?
 - 로그인할 땐 생성되던 RefreshToken이 로그아웃할 때 안 사라지면 보안 이슈가 발생하기 쉽습니다.
 - 이 코드 없이 위 상황을 방지하려면 서비스 단에서 일일이 DB의 RefreshToken을 삭제하는 코드를 써야 합니다.
 - 심지어는 RefreshToken을 조회할 때도 마찬가지이죠. 이렇게 하면 코드가 아주 못생겨집니다.
 */
