package org.openxava.util;




/**
 * Represents a text (or another element) align. <p>
 *
 * The number of objects of this class is finete and accesible
 * only by final variables or the method {@link #get}.<br>
 * 
 * @author: Javier Paniza
 */
 
public class Align implements java.io.Serializable {
	
	
	
	private int code;
	private String description;
	private final static int DEFAULT_CODE = 0;
	private final static int LEFT_CODE = 1;
	private final static int CENTER_CODE = 2;
	private final static int RIGHT_CODE = 3;
	
	public final static Align DEFAULT = new Align(DEFAULT_CODE, "By default");
	public final static Align LEFT = new Align(LEFT_CODE, "Left");
	public final static Align CENTER = new Align(CENTER_CODE, "Center");
	public final static Align RIGHT = new Align(RIGHT_CODE, "Right");

	private final static Align [] all = {
		DEFAULT, LEFT, CENTER, RIGHT			
	};
	
	protected Align(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Align)) {
			return false;
		}
		return code == ((Align) object).code;	
	}
	
	public int hashCode() {
		return code;
	}
	
	/**
	 * Obtain align associated to indicated code. <p>
	 * 
	 * @exception IllegalStateException  If code does not match with a existing align.
	 */
	public static Align get(int code) {
		switch (code) {
			case DEFAULT_CODE:
				return DEFAULT;
			case LEFT_CODE:
				return LEFT;
			case CENTER_CODE:
				return CENTER;
			case RIGHT_CODE:
				return RIGHT;
			default:
				throw new IllegalArgumentException(XavaResources.getString("align_invalid_code", new Integer(code)));
		}
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * All available aligns. <p>
	 *
	 * @return Not null
	 */
	public static Align [] getAll() {
		return all;
	}
	
	/**
	 * Facilitates ask about what type of align is <tt>this</tt>. <p>
	 *
	 * @return <tt>true if this.equals(Align.CENTER)</tt>
	 */
	public boolean isCenter() {
		return code == CENTER_CODE;
	}

	/**
	 * Facilitates ask about what type of align is <tt>this</tt>. <p>
	 *
	 * @return <tt>true if this.equals(Align.DEFAULT)</tt>
	 */
	public boolean isDefault() {
		return code == DEFAULT_CODE;
	}
	
	/**
	 * Facilitates ask about what type of align is <tt>this</tt>. <p>
	 *
	 * @return <tt>true if this.equals(Align.RIGHT)</tt>
	 */
	public boolean isRight() {
		return code == RIGHT_CODE;
	}
	
	/**
	 * Facilitates ask about what type of align is <tt>this</tt>. <p>
	 *
	 * @return <tt>true if this.equals(Align.LEFT)</tt>
	 */
	public boolean isLeft() {
		return code == LEFT_CODE;
	}
	
	public String toString() {
		return getDescription();
	}
	
}
