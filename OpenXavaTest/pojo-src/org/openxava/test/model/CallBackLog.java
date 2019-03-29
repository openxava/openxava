package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.openxava.annotations.PostCreate;
import org.openxava.annotations.PreCreate;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;

/**
 * Holds a callback log for testing the callback behavior
 * @author Federico Alcantara
 *
 */
@Entity
@Table(name = "CALLBACK_LOG")
public class CallBackLog {
	@Id
	@Column(name = "ID")
	private long id;
	
	@Column(name = "TEST_NAME")
	private String testName;
	
	@Column(name = "CALLBACKS", length = 200)
	private String callbacks;

	@PreCreate
	public void onPreCreate() {
		addCallback("PRE_CREATE");
	}
	
	@PostCreate
	public void onPostCreate() {
		addCallback("POST_CREATE");
	}
	
	@PreRemove
	public void onPreRemove() {
		CallBackLog call = new CallBackLog();
		call.setId(id + 100);
		call.setTestName("ON_PRE_REMOVE");
		call.setCallbacks("PRE_REMOVE");
		XPersistence.getManager().persist(call);
	}
	
	@PrePersist
	public void onPrePersist() {
		if (!Is.emptyString(getCallbacks()) &&
				getCallbacks().contains("ERROR_ON_PRE_PERSIST")) {
			throw new RuntimeException("Provoked Error on PrePersist");
		}
		addCallback("PRE_PERSIST");
	}

	@PostPersist
	public void onPostPersist() {
		if (!Is.emptyString(getCallbacks()) &&
				getCallbacks().contains("ERROR_ON_POST_PERSIST")) {
			throw new RuntimeException("Provoked Error on PostPersist");
		}
	}

	@PostRemove
	public void onPostRemove() {
		if (!Is.emptyString(getCallbacks()) &&
				getCallbacks().contains("ERROR_ON_POST_REMOVE")) {
			throw new RuntimeException("Provoked Error on PostRemove");
		}
	}

	private void addCallback(String callback) {
		String call = getCallbacks();
		if (call == null) {
			call = "";
		}
		if (!Is.empty(call)) {
			call = call + ",";
		}
		call = call + callback;
		setCallbacks(call);
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the testName
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @param testName the testName to set
	 */
	public void setTestName(String testName) {
		this.testName = testName;
	}

	/**
	 * @return the callbacks
	 */
	public String getCallbacks() {
		return callbacks;
	}

	/**
	 * @param callbacks the callbacks to set
	 */
	public void setCallbacks(String callbacks) {
		this.callbacks = callbacks;
	}
	
	
}
