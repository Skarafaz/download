package it.skarafaz.download.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import it.skarafaz.download.model.entity.IncomingFile;

public interface IncomingFileRepository extends JpaRepository<IncomingFile, Long> {

    @Query("from IncomingFile f where f.hidden = false")
    Page<IncomingFile> findVisible(Pageable pageable);

    IncomingFile findByPath(String path);

    void deleteByPath(String path);

    @Modifying
    @Query("delete from IncomingFile where path like ?1%")
    void deleteDirectoryChildren(String path);

    @Modifying
    @Query("update IncomingFile set hidden = true where id in ?1")
    void hide(List<Long> ids);

    @Modifying
    @Query("update IncomingFile set hidden = false where id in ?1")
    void show(List<Long> ids);
}
