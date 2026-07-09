# Plan: eliminar el uso de `RemoteException` en OpenXava (8.0)

## Objetivo

Retirar el último vestigio de la era EJB: el uso de `java.rmi.RemoteException` como
señal de "error de sistema / algo inesperado". No hay ningún uso real de RMI detrás;
`RemoteException` se emplea únicamente con la semántica EJB clásica.

**Reemplazo elegido:** `org.openxava.util.SystemException` (ya existe, es
`RuntimeException`). Al ser *unchecked*, permite eliminar las cláusulas `throws` de
las interfaces públicas sin obligar a los programadores a capturar nada.

> No tocar `changelog.txt` ni `migration_en.html` todavía. Documentaremos cuando todo
> funcione. Las notas de migración van más abajo en este mismo plan, listas para
> volcarlas después.

---

## Situación actual (resumen del análisis)

- **225 usos en 33 archivos**, todos con sentido "error de sistema", ninguno RMI real.
- El patrón dominante es *envolver-y-convertir*:
  - La capa `impl` declara `throws ... RemoteException` y hace
    `throw new RemoteException(ex.getMessage())` tras el rollback.
  - La fachada pública (`MapFacade`) captura y reconvierte a `SystemException`.
- Impacto en programadores ya evaluado con `openxavatest`: **mínimo**. Los puntos de
  extensión modernos (`ICalculator`, validadores) ya usan `throws Exception`, no
  `RemoteException`.

---

## Clasificación de los cambios

### Grupo A — Interno (impacto en usuarios: NINGUNO)

Reemplazar `RemoteException` por `SystemException` y eliminar los `throws RemoteException`
internos. Como `SystemException` es runtime, se puede quitar de las firmas sin más.
Sustituir cada `throw new RemoteException(msg)` por `throw new SystemException(msg)`.

Archivos:

- `model/MapFacade.java` — quitar los `catch (RemoteException) { throw new SystemException(ex); }`
  (ya no serán necesarios porque el `impl` lanzará `SystemException` directamente),
  quitar `import java.rmi.*;`, actualizar Javadoc que menciona RemoteException/EJB.
- `model/impl/MapFacadeBean.java` — ~116 usos. Cambiar firmas y todos los
  `throw new RemoteException(...)` → `throw new SystemException(...)`.
- `model/impl/IPersistenceProvider.java` — `createAggregate(...) throws ... RemoteException`.
- `model/impl/POJOPersistenceProviderBase.java` — impl de lo anterior.
- `model/impl/TransientPersistenceProvider.java` — impl de lo anterior.
- `model/meta/MetaProperty.java` — `validate(...) throws RemoteException` (3 sobrecargas)
  y los `throw new RemoteException(...)`.
- `model/meta/MetaCollection.java` — `validate(...) throws RemoteException`.
- `model/meta/MetaModel.java` — `catch (RemoteException)` al llamar a `IModel.getMetaModel()`
  (ver Grupo B: al quitar el throws de `IModel`, este catch pasa a ser código muerto y
  hay que eliminarlo).
- `validators/TolerantValidator.java` — método vacío `throws java.rmi.RemoteException`.
- Capa `tab/impl/`:
  - `EntityTab.java`, `EntityTabBean.java`, `EntityTabDataProvider.java`,
    `EntityTabFactory.java`, `TabProviderBase.java`, `TableModelBean.java`,
    `JPATabProvider.java`, `XTableModelDecoratorBase.java` (firmas + `throw new` + `catch`).
  - Interfaces internas: `IDataReader.java`, `ISearch.java`, `IXTableModel.java`,
    `IWithXTableModel.java`, `IEntityTabImpl.java`, `IEntityTabDataProvider.java`.

### Grupo B — Interfaces públicas (impacto: BAJO, *source-breaking* menor)

Quitar `throws RemoteException` de la firma. Regla Java: una implementación de usuario
que declare `throws RemoteException` sobre estos métodos **dejará de compilar** cuando la
interfaz ya no lo declare (no se puede lanzar una *checked* más amplia que la interfaz).
El usuario solo tiene que **borrar** ese `throws`.

- `model/IModel.java` — `getMetaModel() throws XavaException, RemoteException`.
- `calculators/IModelCalculator.java` — `setModel(Object) throws RemoteException`.
- `calculators/IEntityCalculator.java` — `setEntity(Object) throws RemoteException`
  (ya `@Deprecated`; quitar el throws igualmente por coherencia).
- `calculators/ModelPropertyCalculator.java` — impl de `setModel`.
- `util/ILiberate.java` — `liberate() throws RemoteException` + actualizar Javadoc que
  menciona "remote object".
- `util/IPropertiesContainer.java` — `executeGets/executeSets ... throws RemoteException`.
  Ya recomienda `PropertiesContainerException` desde 6.5.2; retirar `RemoteException`.
- `model/impl/POJOPropertiesContainerAdapter.java` — impl; sustituir `throw new
  RemoteException(...)` por `SystemException` (o `PropertiesContainerException`) y quitar throws.

### Grupo C — Clases base de acciones (extension points, impacto BAJO)

Métodos `protected` que un usuario podría heredar/llamar. Quitar `throws RemoteException`.

- `actions/CollectionBaseAction.java` — `getObjects()`, `getSelectedObjects()`.
- `actions/SaveElementInCollectionAction.java` — `associateEntity(Map)`.

### Grupo D — `SystemException` (la clase reemplazo)

- `util/SystemException.java` — conserva el constructor `SystemException(RemoteException)`.
  Opcional: marcarlo `@Deprecated` para futura retirada, o eliminarlo si ya no queda
  ninguna `RemoteException` que envolver (decisión al final, cuando A/B/C estén hechos).
  Quitar `import java.rmi.*;` cuando proceda.

---

## Orden de ejecución recomendado

1. **Grupo A primero** (interno). Compilar. No debe romper nada externo.
   - Empezar por `MapFacadeBean` + `MapFacade` (el bloque más grande y aislado).
   - Después la capa `tab/impl`.
   - Después validación (`MetaProperty`, `MetaCollection`, `TolerantValidator`).
   - Persistence providers.
2. **Grupo B** (interfaces públicas) + sus implementaciones internas. Al quitar el throws
   de `IModel`, eliminar el `catch (RemoteException)` de `MetaModel` (código muerto).
3. **Grupo C** (acciones base).
4. **Grupo D**: limpiar `SystemException` y los `import java.rmi.*` residuales.
5. Búsqueda final: `grep -rn "rmi" src/main/java` para no dejar imports ni menciones.
6. Compilar `openxava` y `openxavatest`; ejecutar los tests de `openxavatest`
   (el usuario los lanza desde su IDE).

---

## Criterios / decisiones

- **No** crear excepciones nuevas: reutilizar `SystemException` para todo lo de sistema.
  En `IPropertiesContainer`/`POJOPropertiesContainerAdapter` valorar
  `PropertiesContainerException` (ya recomendada ahí) en vez de `SystemException`.
- **No** invertir esfuerzo en lo `@Deprecated` más allá de quitar el throws.
- Cada método público/protegido nuevo o modificado que lo requiera debe llevar
  `@since 8.0` (según `AGENTS.md`).
- Mantener los mensajes i18n existentes (`validate_error`, `tab_next_chunk_error`, etc.);
  solo cambia la clase de excepción, no el texto.

---

## Riesgos y verificación

- **Código muerto tras el cambio**: `catch (RemoteException)` que dejan de poder
  capturar nada → error de compilación "exception is never thrown". Se resuelven al
  eliminar el catch (ej.: `MetaModel`, `MapFacade`, `TableModelBean`).
- **Imports sin usar** de `java.rmi.*` → limpiar.
- Verificación: compilación limpia de `openxava` + `openxavatest` y suite de tests de
  `openxavatest` en verde.

---

## Notas de migración para programadores (borrador para `migration_en` — NO publicar aún)

> Draft, in English, ready to adapt into `migration_en.html` once everything works.

**OpenXava no longer uses `java.rmi.RemoteException`.** It has been replaced by the
runtime exception `org.openxava.util.SystemException` for system-level errors. This only
affects you if your code referenced `RemoteException` explicitly. Most applications need
no changes.

1. **If you implemented an OpenXava interface declaring `throws RemoteException`** — for
   example `IModel.getMetaModel()`, `IModelCalculator.setModel()`,
   `ILiberate.liberate()`, or `IPropertiesContainer.executeGets/executeSets()` — remove
   the `throws java.rmi.RemoteException` clause from your implementing method. Since
   `SystemException` is a `RuntimeException`, no `throws` clause is required.

2. **If you extended an OpenXava action base class** (e.g. `CollectionBaseAction`,
   `SaveElementInCollectionAction`) and overrode a method that declared
   `throws RemoteException`, remove that clause from your override.

3. **If you catch `RemoteException` around OpenXava calls**, replace the catch with
   `org.openxava.util.SystemException`, or simply remove it (it is a runtime exception,
   so it is optional to catch). Note: most calls (e.g. via `MapFacade`) already threw
   `SystemException`, so you probably have nothing to change here.

4. **Remove now-unused `import java.rmi.RemoteException;`** statements.

5. Your **own** interfaces or classes that declared `throws RemoteException` by imitating
   the old OpenXava style (they don't extend OpenXava types) are not required to change,
   but you may clean them up for consistency.
