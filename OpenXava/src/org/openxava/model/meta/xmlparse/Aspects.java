package org.openxava.model.meta.xmlparse;

import java.util.*;



import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class Aspects {
	
	static int ALL = -1;
	static int FOR = 0;
	static int EXCEPT_FOR = 1;
	
	static String ALL_MODEL_NAME = "__ALL__";
	
	private static boolean parsed = false;
		
	private static Map [] postCreates;
	private static Map [] postLoads;
	private static Map [] postModifys;		
	private static Map [] preRemoves;
	
	
		
	public static void fillImplicitCalculators(MetaModel container) throws XavaException {
		if (!parsed) {
			AspectsParser.configureAspects();
			parsed = true;
		}		
		if (postCreates != null) {
			fillPostCreate(container, get(postCreates[FOR], null));		
			fillPostCreate(container, get(postCreates[FOR], container));			
			fillPostCreate(container, getExcept(postCreates[EXCEPT_FOR], container));
		}

		if (postLoads != null) {
			fillPostLoad(container, get(postLoads[FOR], null));		
			fillPostLoad(container, get(postLoads[FOR], container));			
			fillPostLoad(container, getExcept(postLoads[EXCEPT_FOR], container));
		}
		
		if (postModifys != null) {
			fillPostModify(container, get(postModifys[FOR], null));		
			fillPostModify(container, get(postModifys[FOR], container));			
			fillPostModify(container, getExcept(postModifys[EXCEPT_FOR], container));
		}
		
		if (preRemoves != null) {
			fillPreRemove(container, get(preRemoves[FOR], null));		
			fillPreRemove(container, get(preRemoves[FOR], container));			
			fillPreRemove(container, getExcept(preRemoves[EXCEPT_FOR], container));
		}
		
	}
	
	private static Collection get(Map calculators, MetaModel container) {
		if (calculators == null) return null;
		String name = container == null?ALL_MODEL_NAME:container.getQualifiedName(); 
		return (Collection) calculators.get(name);
	}
	
	private static Collection getExcept(Map calculators, MetaModel container) {
		if (calculators == null) return null;
		Collection result = new ArrayList();
		String name = container.getQualifiedName();
		for (Iterator it = calculators.entrySet().iterator(); it.hasNext();) {
			Map.Entry en = (Map.Entry) it.next();
			Collection names = (Collection) en.getKey();
			if (!names.contains(name)) {
				result.addAll((Collection) en.getValue());
			}
		}
		return result;
	}	

	private static void fillPostCreate(MetaModel container, Collection calculators) {
		if (calculators == null) return;
		for (Iterator it = calculators.iterator(); it.hasNext();) {
			MetaCalculator calculator = (MetaCalculator) it.next();
			container.addMetaCalculatorPostCreate(calculator);
		}
	}
	
	private static void fillPostLoad(MetaModel container, Collection calculators) {
		if (calculators == null) return;
		for (Iterator it = calculators.iterator(); it.hasNext();) {
			MetaCalculator calculator = (MetaCalculator) it.next();
			container.addMetaCalculatorPostLoad(calculator);
		}
	}
	
	private static void fillPostModify(MetaModel container, Collection calculators) {
		if (calculators == null) return;
		for (Iterator it = calculators.iterator(); it.hasNext();) {
			MetaCalculator calculator = (MetaCalculator) it.next();
			container.addMetaCalculatorPostModify(calculator);
		}
	}
	
	private static void fillPreRemove(MetaModel container, Collection calculators) {
		if (calculators == null) return;
		for (Iterator it = calculators.iterator(); it.hasNext();) {
			MetaCalculator calculator = (MetaCalculator) it.next();
			container.addMetaCalculatorPreRemove(calculator);
		}
	}

	public static void addMetaCalculatorPostCreate(String models, int scope, MetaCalculator calculator) {
		if (postCreates == null) postCreates = new HashMap[2];
		addMetaCalculator(postCreates, models, scope, calculator);	
	}
	
	public static void addMetaCalculatorPostLoad(String models, int scope, MetaCalculator calculator) {
		if (postLoads == null) postLoads = new HashMap[2];
		addMetaCalculator(postLoads, models, scope, calculator);	
	}
	
	public static void addMetaCalculatorPostModify(String models, int scope, MetaCalculator calculator) {
		if (postModifys == null) postModifys = new HashMap[2];
		addMetaCalculator(postModifys, models, scope, calculator);	
	}
	
	public static void addMetaCalculatorPreRemove(String models, int scope, MetaCalculator calculator) {
		if (preRemoves == null) preRemoves = new HashMap[2];
		addMetaCalculator(preRemoves, models, scope, calculator);	
	}
		
	private static void addMetaCalculator(Map [] calculatorsMaps, String models, int scope, MetaCalculator calculator) {
		if (scope == ALL) {
			models = ALL_MODEL_NAME;
			scope = FOR;
		}		
		
		StringTokenizer st = new StringTokenizer(models, ",");
		Collection modelNames = new ArrayList();
		while (st.hasMoreTokens()) {
			modelNames.add(st.nextToken().trim());
		}
		
		if (scope == EXCEPT_FOR) {
			addToCalculators(calculatorsMaps, scope, modelNames, calculator);
		}
		else {
			for (Iterator it = modelNames.iterator(); it.hasNext();) {
				addToCalculators(calculatorsMaps, scope, it.next(), calculator);
			}
		}
				
	}
	
	private static void addToCalculators(Map [] calculatorsMaps, int scope, Object modelName, MetaCalculator calculator) {
		if (calculatorsMaps[scope] == null) calculatorsMaps[scope] = new HashMap();
		Collection calculators = (Collection) calculatorsMaps[scope].get(modelName);
		if (calculators == null) {
			calculators = new ArrayList();
			calculatorsMaps[scope].put(modelName, calculators);
		}
		calculators.add(calculator);		
	}
		
}
