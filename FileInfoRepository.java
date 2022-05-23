package org.prod.tgk.repositories;

import org.prod.tgk.entitys.Apartment;
import org.prod.tgk.entitys.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

}
