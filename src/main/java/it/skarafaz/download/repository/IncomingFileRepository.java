package it.skarafaz.download.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import it.skarafaz.download.model.entity.IncomingFile;

public interface IncomingFileRepository extends JpaRepository<IncomingFile, Long>, IncomingFileRepositoryCustom {

    IncomingFile findByPath(String path);

    List<IncomingFile> findByFeed(Boolean feed);

    @Modifying
    @Query("update IncomingFile set hidden = ?2 where id in ?1")
    void updateHidden(List<Long> ids, Boolean hidden);

    void deleteByPath(String path);

    @Modifying
    @Query("delete from IncomingFile where path like ?1%")
    void deletePathContent(String path);
}
