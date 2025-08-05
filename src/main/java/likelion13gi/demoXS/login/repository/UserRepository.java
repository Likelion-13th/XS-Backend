package likelion13gi.demoXS.login.repository;

import likelion13gi.demoXS.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // 1. 사용자 계정이름으로 사용자 정보를 회수하는 기능
    Optional<User> findByUsernickname(String usernickname);

    // 2. 사용자 이메일으로 사용자 정보를 회수하는 기능
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 3. 사용자 계정이름을 가진 사용자 정보가 존재하는지 판단하는 기능
    boolean existsByUsernickname(String usernickname);

    // 4. 사용자 이메일을 가진 사용자 정보가 존재하는지 판단하는 기능
    boolean existsByPhoneNumber(String phoneNumber);

    User findByUid(String uid);

    User findByProviderId(String providerId);
}
