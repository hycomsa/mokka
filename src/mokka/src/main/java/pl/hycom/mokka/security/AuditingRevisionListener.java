package pl.hycom.mokka.security;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

import pl.hycom.mokka.security.model.AuditedRevisionEntity;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class AuditingRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		AuditedRevisionEntity auditedRevisionEntity = (AuditedRevisionEntity) revisionEntity;

		String userName = SecurityContextHolder.getContext().getAuthentication().getName();

		auditedRevisionEntity.setAuthor(userName);
	}

}
