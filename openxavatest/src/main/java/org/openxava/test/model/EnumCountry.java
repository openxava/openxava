package org.openxava.test.model;

/**
 * 
 * @author Jeromy Altuna
 */
public enum EnumCountry {
	
	DOMINICAN_REPUBLIC() {
		@Override 
		public String drinks() {
			return "Mamajuana, Ron dominicano, ...";
		}

		@Override 
		public String foods() {
			return "Mofongo, Mangú, ...";
		}
	}, 
	PERU() {
		@Override 
		public String drinks() {
			return "Pisco, Chicha, ...";
		}

		@Override 
		public String foods() {
			return "Ají de gallina, Cebiche, ...";
		}
	},
	SPAIN() {
		@Override 
		public String drinks() {
			return "Horchata, Agua de Valencia, ...";
		}

		@Override 
		public String foods() {
			return "Paella, Tapas, ...";
		}
	}; //...
	
	abstract public String drinks();
	abstract public String foods();
	//...
}	
	