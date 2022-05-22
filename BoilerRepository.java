package org.prod.tgk.repositories;

import org.prod.tgk.entitys.Boiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoilerRepository extends JpaRepository<Boiler, Long> {

}
