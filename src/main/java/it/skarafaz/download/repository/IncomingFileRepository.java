package it.skarafaz.download.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import it.skarafaz.download.model.db.IncomingFile;

public interface IncomingFileRepository extends JpaRepository<IncomingFile, Long> {

    IncomingFile findByPath(String path);

    void deleteByPath(String path);

    @Modifying
    @Query("delete from IncomingFile where path like ?1%")
    void deleteDirectoryChildren(String path);
}
