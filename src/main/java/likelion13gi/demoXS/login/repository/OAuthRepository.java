package likelion13gi.demoXS.login.repository;

import likelion13gi.demoXS.domain.OAuth;
import likelion13gi.demoXS.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    Optional<Object> findByUser(User user);
}
