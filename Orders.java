package org.prod.tgk.entitys;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.File;
import java.util.Date;
import java.util.Set;

@ToString
@Getter
@Setter
@Entity
public class Orders { //заявка на починку оборудования
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String surname;
    private String reason;
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date orderDate;
    private boolean status;
    @ManyToMany
    private Set<FileInfo> files;
}
