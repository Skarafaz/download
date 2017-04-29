package it.skarafaz.download.repository;

import java.nio.file.Path;

import org.springframework.data.jpa.repository.JpaRepository;

import it.skarafaz.download.model.db.IncomingFile;

public interface IncomingFileRepository extends JpaRepository<IncomingFile, Long> {

    IncomingFile findByPath(Path path);
}
