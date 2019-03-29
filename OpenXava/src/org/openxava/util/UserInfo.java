package org.openxava.util;

/**
 * Data about the current logged user, obtained via {@link Users}. <p>
 * 
 * This data is obtained from the portal where the OpenXava module
 * is executing.
 * 
 * @author Javier Paniza
 * @author Yerik Alarcón
 */

public class UserInfo implements java.io.Serializable {
	
	private String id;
	private String givenName;
	private String familyName;
	private String email;
	private String jobTitle;
	private String middleName;
	private String nickName;
	private int birthDateYear;
	private int birthDateMonth;
	private int birthDateDay;
	private String organization; 

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGivenName() {
		return givenName==null?"":givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getFamilyName() {
		return familyName==null?"":familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getEmail() {
		return email==null?"":email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	// New geters and seters to attributes proposed for OpenXava 4.5
	public String getJobTitle() {
		return jobTitle==null?"":jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getMiddleName() {
		return middleName==null?"":middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getNickName() {
		return nickName==null?"":nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getBirthDateYear() {
		return birthDateYear;
	}
	public void setBirthDateYear(int birthDateYear) {
		this.birthDateYear = birthDateYear;
	}
	public void setBirthDateYear(Object birthDateYear) {
		this.birthDateYear = toNumber(birthDateYear);
	}
	
	public int getBirthDateMonth() {
		return birthDateMonth;
	}
	public void setBirthDateMonth(int birthDateMonth) {
		this.birthDateMonth = birthDateMonth;
	}
	public void setBirthDateMonth(Object birthDateMonth) {
		this.birthDateMonth = toNumber(birthDateMonth);
	}
	
	public int getBirthDateDay() {
		return birthDateDay;
	}
	public void setBirthDateDay(int birthDateDay) {
		this.birthDateDay = birthDateDay;
	}
	public void setBirthDateDay(Object birthDateDay) {
		this.birthDateDay = toNumber(birthDateDay);
	}	
	
	private int toNumber(Object value) {
		if (value instanceof Number) return ((Number) value).intValue();
		if (value == null) return 0;
		return Integer.parseInt(value.toString());
	}
	
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
}