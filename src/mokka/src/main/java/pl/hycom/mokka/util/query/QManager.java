package pl.hycom.mokka.util.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Service
public class QManager {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public <T> List<T> execute(Q q, Class<T> type) {
		Query query = em.createQuery(q.query());

		int i = 0;
		for (Object o : q.params()) {
			query.setParameter(i++, o);
		}

		if (q.getStartingIndex() != null) {
			query.setFirstResult(q.getStartingIndex());
		}

		if (q.getMaxResults() != null) {
			query.setMaxResults(q.getMaxResults());
		}

		return query.getResultList();
	}
}
