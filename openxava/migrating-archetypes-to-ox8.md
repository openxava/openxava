# Guía para migrar un arquetipo OpenXava a OpenXava 8

Pasos aplicados con éxito a `openxava-archetype(-spanish)`, `openxava-crm-archetype(-spanish)`, `openxava-invoicing-archetype(-spanish)` y `openxava-master-detail-archetype(-spanish)`. Usa `openxava-archetype` ya migrado como referencia.

Convenciones de nombres:

- **Inglés**: paquete `com.yourcompany.yourapp`, DB `yourapp-db`, context-path `/yourapp`, ficheros i18n `yourapp-*`, marcador `.something`, paquete destino de `DBManager`: `tools`.
- **Español**: paquete `com.tuempresa.tuaplicacion`, DB `tuaplicacion-db`, context-path `/tuaplicacion`, i18n `tuaplicacion-*`, marcador `.algo`, paquete `herramientas`. **Los fuentes están en ISO-8859-1** (ver `project.build.sourceEncoding`); algunos tienen fin de línea CRLF. No uses herramientas de edición que reescriban como UTF-8; usa `sed`/`python` con `encoding='iso-8859-1'`.

## 1. `pom.xml`

- Elimina el bloque `<annotationProcessorPaths>` con Lombok del `maven-compiler-plugin` (Lombok viene por classpath vía openxava).
- Elimina `WEB-INF/lib/tomcat-*.jar,` de `packagingExcludes` del `maven-war-plugin`.
- Sustituye el plugin `exec-maven-plugin` por:

```xml
<plugin>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-maven-plugin</artifactId>
	<version>4.1.0</version>
	<executions>
		<execution>
			<goals>
				<goal>repackage</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<mainClass>${project.groupId}.${project.artifactId}.Application</mainClass>
	</configuration>
</plugin>
```

- Añade antes de `</project>` el perfil para JDK 23+:

```xml
<!-- Enable annotation processing from classpath (Lombok via openxava); required since JDK 25.
     -proc:full only since JDK 23, so it lives in this profile to keep JDK 17 builds working. -->
<profiles>
	<profile>
		<id>jdk23-annotation-processing</id>
		<activation>
			<jdk>[23,)</jdk>
		</activation>
		<build>
			<plugins>
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-compiler-plugin</artifactId>
					<configuration>
						<proc>full</proc>
					</configuration>
				</plugin>
			</plugins>
		</build>
	</profile>
</profiles>
```

- Actualiza el comentario de versiones Java a `17, 21 y 25` (quita 1.8 y 11).
- **Si el código usa JAX-RS** (`ClientBuilder`, `javax.ws.rs.*`), añade:

```xml
<dependency>
	<groupId>org.glassfish.jersey.core</groupId>
	<artifactId>jersey-client</artifactId>
	<version>4.0.2</version>
</dependency>
<dependency>
	<groupId>org.glassfish.jersey.inject</groupId>
	<artifactId>jersey-hk2</artifactId>
	<version>4.0.2</version>
</dependency>
```

## 2. Código Java

- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.validation.*` / `javax.validation.constraints.*` → `jakarta.validation.*` (también usos fully-qualified como `javax.validation.ValidationException`).
- `javax.ws.rs.client.*` → `jakarta.ws.rs.client.*`
- **`javax.ejb.*`**: no existe en Jakarta EE 11. Ojo: `ObjectNotFoundException` y `FinderException` venían de `javax.ejb`; en OX8 están en `org.openxava.model` → añade `import org.openxava.model.*;` donde se usen.
- **`java.rmi.RemoteException`**: elimínalo de imports y de cláusulas `throws` (los métodos padre de OpenXava ya no lo declaran; un override que lo declare no compila).
- Elimina la clase lanzadora antigua `run/yourapp.java` / `run/tuaplicacion.java` (usa `AppServer`).
- Mueve `run/DBManager.java` a `tools/` (inglés) o `herramientas/` (español) y corrige su `package`.
- Elimina los marcadores `__artifactId__/run/.something` / `.algo` y el directorio `run` vacío.
- Crea `src/main/java/com/yourcompany/__artifactId__/Application.java` (o `com/tuempresa/__artifactId__/` en español, **en ISO-8859-1**):

```java
package com.yourcompany.__artifactId__;

import org.openxava.util.DBServer;
import org.openxava.spring.OpenXavaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends OpenXavaApplication {

	public static void main(String[] args) throws Exception {
		DBServer.start("yourapp-db"); // To use your own database comment this line and configure src/main/resources/application.properties
		SpringApplication.run(Application.class, args);
	}

}
```

**Importante**: el directorio `__artifactId__` con `Application.java` es imprescindible para que `archetype:create-from-project` mantenga el nivel de paquete del artifactId en el proyecto generado.

## 3. Recursos y webapp

- Elimina `src/main/webapp/WEB-INF/` entero (incluido `web.xml`).
- Sustituye `src/main/webapp/META-INF/context.xml` por:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<Context>

	<JarScanner scanClassPath="false" />
	<CookieProcessor sameSiteCookies="lax" /> <!-- To be compliant with OWASP -->

</Context>
```

- Crea `src/main/resources/application.properties` (copia el de `openxava-archetype`; en español usa el de `openxava-archetype-spanish`, con `context-path=/tuaplicacion`).
- `src/main/resources/META-INF/persistence.xml`: esquema JPA 3.2 con namespace `https://jakarta.ee/xml/ns/persistence`, corrige `java://comp/env` → `java:comp/env`, y en el persistence-unit `default` añade:

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="update"/>
<property name="jakarta.persistence.create-database-schemas" value="true"/>
<property name="hibernate.jdbc.use_streams_for_binary" value="true"/>
```

- `src/main/resources/xava.properties`: elimina `#applicationPort=8080`, cambia `# styleCSS=terra.css` → `# styleCSS=auto.css`, elimina las líneas `black-and-white.css` y `blue.css`, y pon `themes=auto.css, light.css, dark.css`.
- `src/test/resources/xava-junit.properties`: añade `host=localhost` al principio.
- Renombra los i18n: `yourapp-*.properties` → `__artifactId__-*.properties` (igual con `tuaplicacion-*`).

## 4. Ficheros del arquetipo

- Copia `archetype-post-generate.groovy` desde `openxava-archetype` a la raíz del arquetipo.
- `archetype.properties`: añade `archetype-post-generate.groovy` a `excludePatterns` y cambia el paso 5 de las instrucciones por:

```
#   5. copy archetype-post-generate.groovy target\generated-sources\archetype\src\main\resources\META-INF
#   6. In target/generated-sources/archetype do: mvn clean deploy
```

- `archetype-metadata.xml`: elimina el `fileSet` de `**/*.something` / `**/*.algo`. Asegúrate de que el `fileSet` de `src/main/webapp` incluye `**/*.js`.
- `.gitignore`: déjalo como `/target/`, `/temp/`, `/.idea/`, `.classpath`, `.project`, `.settings/`.
- `AGENTS.md` y `archetype-pom.xml`: normalmente ya están migrados; no toques los `javax.*` que aparecen en `AGENTS.md` (son intencionados, instructivos).

## 5. Verificación

```bash
# No debe quedar nada de esto fuera de AGENTS.md:
grep -rln "javax\.\(persistence\|validation\|servlet\|inject\|ejb\|ws\)\|AppServer\|exec-maven\|terra\.css\|blue\.css\|black-and-white\|applicationPort\|javaLoggingLevel\|java://comp\|RemoteException\|java\.rmi" src pom.xml
# Deben existir:
ls archetype-post-generate.groovy src/main/resources/application.properties src/main/java/com/*/__artifactId__/Application.java
# No debe existir WEB-INF:
ls src/main/webapp/WEB-INF   # debe fallar
```

Prueba real: `mvn clean archetype:create-from-project -Darchetype.properties="archetype.properties"`, genera un proyecto desde el arquetipo instalado y arráncalo con `mvn spring-boot:run`.

## Errores ya vistos (para anticiparte)

- `cannot find symbol: ObjectNotFoundException` → falta `import org.openxava.model.*;` (venía de `javax.ejb`).
- `associateEntity(...) cannot override ... overridden method does not throw java.rmi.RemoteException` → quita `RemoteException` del `throws` y el `import java.rmi.*`.
- `iconv` a ISO-8859-1 puede fallar si el fichero ya está en ISO-8859-1; comprueba con `file` antes de convertir.
