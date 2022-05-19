package org.prod.tgk.entitys;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@ToString
@Getter
@Setter
@Entity
public class Bill { //подаваемый счёт
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double value;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date supplyDate;
}
