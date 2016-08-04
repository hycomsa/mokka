package pl.hycom.mokka.security.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import pl.hycom.mokka.security.AuditingRevisionListener;

import javax.persistence.Entity;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof AuditedRevisionEntity)) {
			return false;
		}

		AuditedRevisionEntity that = (AuditedRevisionEntity) o;

		return new EqualsBuilder().appendSuper(super.equals(o))
				.append(getAuthor(), that.getAuthor())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
				.append(getAuthor())
				.toHashCode();
	}
}
