package org.openxava.model;

/**
 * To associate an icon to each enum option. <br>
 * 
 * For example: 
 * <pre>
	public enum Priority implements IIconEnum {  
		LOW("transfer-down"), MEDIUM("square-medium"), HIGH("transfer-up");

		private String icon;

		private Priority(String icon) {
			this.icon = icon;
		}

		public String getIcon() {
			return icon;
		} 
		
	};
 * </pre>
 * Just make your enum to implement IIconEnum that forces you to have a getIcon() method. 
 * This method has to return an icon id from <a href="https://materialdesignicons.com/">Material Design Icons</a>. 
 * OpenXava can use these icons in several parts of the UI.
 * 
 * @since 6.3
 * @author Javier Paniza
 */
public interface IIconEnum {
	
	/**
	 * Icon id from <a href="https://materialdesignicons.com/">Material Design Icons</a>.
	 */
	String getIcon();

}
