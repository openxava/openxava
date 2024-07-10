package org.openxava.actions;

import java.lang.reflect.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.validators.*;

public class DeleteElementFromCollectionAction extends CollectionElementViewBaseAction {

	public void execute() throws Exception {
		System.out.println("delete");
		try {
			if (!getCollectionElementView().getKeyValuesWithValue().isEmpty()) {
				validateMinimum(1);

//				MapFacade.removeCollectionElement(getCollectionElementView().getParent().getModelName(), getCollectionElementView().getParent().getKeyValues(), getCollectionElementView().getMemberName(), getCollectionElementView().getKeyValues());
//				commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example) 
//				if (isEntityReferencesCollection()) {
//					addMessage("association_removed", getCollectionElementView().getModelName(), getCollectionElementView().getParent().getModelName());
//				}
//				else {
//					addMessage("aggregate_removed", getCollectionElementView().getModelName());
//				}

				// Recuperar el objeto a eliminar
				String parentModelName = getCollectionElementView().getModelName();
				Map<String, Object> keyValues = getCollectionElementView().getKeyValues();

				Object entityToDelete = MapFacade.findEntity(parentModelName, keyValues);

				if (entityToDelete != null) {
					// Eliminar todas las referencias hijas asociadas
					deleteChildReferences(entityToDelete);

					// Eliminar el objeto
					MapFacade.removeCollectionElement(parentModelName,
							getCollectionElementView().getParent().getKeyValues(),
							getCollectionElementView().getMemberName(), keyValues);
					XPersistence.commit();
				}

				if (isEntityReferencesCollection()) {
					addMessage("association_removed", getCollectionElementView().getModelName(),
							getCollectionElementView().getParent().getModelName());
				} else {
					addMessage("aggregate_removed", getCollectionElementView().getModelName());
				}

			}
			getCollectionElementView().setCollectionEditingRow(-1);
			getCollectionElementView().clear();
			getView().recalculateProperties();
			closeDialog();
		} catch (ValidationException ex) {
			addErrors(ex.getErrors());
		}
	}

	private void deleteChildReferences(Object entity) throws IllegalAccessException {
		EntityManager entityManager = XPersistence.getManager();
		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					for (Object childEntity : collection) {
						entityManager.remove(
								entityManager.contains(childEntity) ? childEntity : entityManager.merge(childEntity));
					}
					collection.clear();
				}
			}
		}
	}

}