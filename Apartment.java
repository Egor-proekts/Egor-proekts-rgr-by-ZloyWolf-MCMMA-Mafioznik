package org.prod.tgk.entitys;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@Setter
@Entity
public class Apartment { //квартира
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
