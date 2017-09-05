package pl.hycom.mokka.emulator.mock.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.envers.Audited;
import pl.hycom.mokka.emulator.mock.TrackChanges;
import pl.hycom.mokka.web.json.View;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Tomasz Wozniak (tomasz.wozniak@hycom.pl
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JmsMockConfiguration implements Serializable {

	@JsonView(View.General.class)
	private Integer order;

	@JsonView(View.General.class)
	private boolean replyToHeader;

	@JsonView(View.General.class)
	private String description;

	@JsonView(View.General.class)
	private String id;

	@JsonView(View.General.class)
	private boolean enabled;

	@JsonView(View.General.class)
	private String responseQueueName;

	@JsonView(View.General.class)
	private Integer timeout;

	@JsonView(View.General.class)
	private String requestQueueName;

	@JsonView(View.General.class)
	private String name;

	@JsonView(View.General.class)
	private String status;

	@JsonView(View.General.class)
	private String editMode;

	@JsonView(View.General.class)
	private String type;

	@JsonView(View.General.class)
	private String configurationContent;

	public String getType() {
		return type;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public boolean isReplyToHeader() {
		return replyToHeader;
	}

	public void setReplyToHeader(boolean replyToHeader) {
		this.replyToHeader = replyToHeader;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseQueueName() {
		return responseQueueName;
	}

	public void setResponseQueueName(String responseQueueName) {
		this.responseQueueName = responseQueueName;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getRequestQueueName() {
		return requestQueueName;
	}

	public void setRequestQueueName(String requestQueueName) {
		this.requestQueueName = requestQueueName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEditMode() {
		return editMode;
	}

	public void setEditMode(String editMode) {
		this.editMode = editMode;
	}

	public String getConfigurationContent() {
		return configurationContent;
	}

	public void setConfigurationContent(String configurationContent) {
		this.configurationContent = configurationContent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setType(String type) {
		this.type = type;
	}
}
