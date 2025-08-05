package likelion13gi.demoXS.repository;

import likelion13gi.demoXS.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCategoryName(String categoryName);
}