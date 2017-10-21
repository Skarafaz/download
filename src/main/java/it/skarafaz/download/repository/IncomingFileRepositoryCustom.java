package it.skarafaz.download.repository;

import java.util.List;

import it.skarafaz.download.model.Sort;
import it.skarafaz.download.model.entity.IncomingFile;

public interface IncomingFileRepositoryCustom {

    Long count(Boolean showHidden, String search);

    List<IncomingFile> list(Integer start, Integer count, Sort sort, Boolean showHidden, String search);
}
