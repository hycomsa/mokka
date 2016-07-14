package pl.hycom.mokka.security.model;

import javax.persistence.Entity;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import pl.hycom.mokka.security.AuditingRevisionListener;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@RevisionEntity(AuditingRevisionListener.class)
@Entity
public class AuditedRevisionEntity extends DefaultRevisionEntity {

	private static final long serialVersionUID = 1L;

	private String author;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
