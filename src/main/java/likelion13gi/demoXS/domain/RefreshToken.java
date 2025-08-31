package likelion13gi.demoXS.domain;

import jakarta.persistence.*;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
}
