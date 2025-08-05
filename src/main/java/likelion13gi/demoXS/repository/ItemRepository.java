package likelion13gi.demoXS.repository;

import likelion13gi.demoXS.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByPrice(int price);

    List<Item> findByName(String name);

    List<Item> findByCompany(String company);
}

// Item 테이블에서 price, name, company는