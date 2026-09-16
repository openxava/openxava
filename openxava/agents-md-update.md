# Actualización de AGENTS.md de los arquetipos para OpenXava 8

Resumen de los cambios realizados en los 10 arquetipos (openxava-archetype, openxava-archetype-spanish, openxava-crm-archetype, openxava-crm-archetype-spanish, openxava-invoicing-archetype, openxava-invoicing-archetype-spanish, openxava-master-detail-archetype, openxava-master-detail-archetype-spanish, openxava-project-management-archetype, openxava-project-management-archetype-spanish).

## Cambios en AGENTS.md

- **Placeholders corregidos**: ejemplos que usaban paquetes y nombres de aplicación hardcodeados ahora usan `${package}` y `${artifactId}` (imports, paquetes de acciones/tests, `super(testName, "${artifactId}", ...)`).
- **Nueva sección "Jakarta EE 11"** al inicio: instruye al LLM a usar siempre imports `jakarta.*` (persistence, validation, servlet, inject, annotation, ws.rs, json.bind), nunca `javax.*`, aunque su entrenamiento contenga código OpenXava antiguo.
- **Sección `.xava` eliminada**: sustituida por "Code Examples for Agents" / "Ejemplos de Código para Agentes" con enlaces a GitHub:
  - Ejemplos de tests: https://github.com/openxava/openxava/tree/master/openxavatest/src/test/java/org/openxava/test/tests/bymodule
  - Acciones disponibles: `default-controllers.xml` en https://github.com/openxava/openxava/blob/master/openxava/src/main/resources/xava/default-controllers.xml
- Las referencias a `./.xava/agents/test` en la sección de tests apuntan ahora a esas URLs.

## Cambios en pom.xml

- Eliminada la ejecución `unpack-agents-examples` del `maven-dependency-plugin` en los 10 arquetipos (ya no se genera la carpeta `.xava`). Queda solo el unpack de los DTDs (`xava/dtds/*`).

## Cambios en .gitignore

- Eliminada la entrada `/.xava/` en los 10 arquetipos.

## Decisiones

- `openxava-agents-examples` desaparece: no estará en GitHub ni en la 8 ni en master, y no se publicará versión 8 en Maven Central (la 7 permanece). Los ejemplos se obtienen directamente de `openxavatest` en GitHub.
- Las coincidencias de `.xava` en `welcome.jsp`, `WebDriverTestBase.java` y `custom.css` eran falsos positivos (clases CSS `xava_*`, imports `XavaStyle`); sin cambios.

## Pendiente

1. Actualizar la guía de migración (migration_en/es.html).
2. Actualizar la doc de "Asistentes de codificación IA".
3. Buscar más documentación susceptible de ser actualizada.
4. Eliminar el proyecto `openxava-agents-examples`.
