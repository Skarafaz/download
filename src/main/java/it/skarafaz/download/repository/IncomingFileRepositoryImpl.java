package it.skarafaz.download.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.skarafaz.download.model.Sort;
import it.skarafaz.download.model.entity.IncomingFile;

public class IncomingFileRepositoryImpl implements IncomingFileRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long count(Boolean showHidden) {
        StringBuilder hql = new StringBuilder();
        hql.append("select count(id) ");
        hql.append("from IncomingFile ");
        if (showHidden == false) {
            hql.append("where hidden = false");
        }

        return (Long) this.entityManager.createQuery(hql.toString()).getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IncomingFile> list(Integer start, Integer count, Sort sort, Boolean showHidden) {
        StringBuilder hql = new StringBuilder();
        hql.append("from IncomingFile ");
        if (showHidden == false) {
            hql.append("where hidden = false ");
        }
        hql.append(String.format("order by %s %s", sort.getProperty(), sort.getDirection()));

        Query query = this.entityManager.createQuery(hql.toString());
        query.setFirstResult(start);
        query.setMaxResults(count);

        return query.getResultList();
    }

    @Override
    public void clear() {
        this.entityManager.clear();
    }
}
