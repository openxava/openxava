# Plan: añadir Spring Boot 4.1 a invoicedemo (OpenXava 8.0 / Jakarta EE 11)

Objetivo: que `invoicedemo` arranque con **Spring Boot 4.1** y Tomcat embebido (desde un `main()` de Spring Boot),
en lugar de usar el lanzador actual `AppServer.run(...)` de OpenXava
(ver `src/main/java/org/openxava/invoicedemo/run/invoicedemo.java`).

Seguimos como base el tutorial `openxava-doc/web/docs/springboot_en.html`, **adaptándolo** porque ese
documento está escrito para OpenXava 7.7 + Spring Boot 2.7 (`javax.*`), y aquí tenemos OpenXava 8.0 + Spring Boot 4.1 (`jakarta.*`).

---

## 1. Análisis de compatibilidad (por qué Spring Boot 4.1 sí encaja)

| Aspecto | OpenXava 8.0 (invoicedemo) | Spring Boot 4.1 | ¿Compatible? |
|---|---|---|---|
| Espacio de nombres | `jakarta.*` (Jakarta EE 11) | `jakarta.*` (Jakarta EE 11) | Sí |
| Servlet API | 6.1 (`jakarta.servlet-api` 6.1.0) | requiere contenedor Servlet 6.1 | Sí |
| Tomcat embebido | `tomcat-embed-*` 11.0.22 | Tomcat 11.0.x gestionado por el BOM | Sí (lo unifica el BOM) |
| WebSocket | `jakarta.websocket-api` 2.2.0 | Jakarta WebSocket | Sí |
| Java | source/target 17 | baseline Java 17 (hasta Java 25 LTS) | Sí |
| Hibernate | 7.2.x | — (OX gestiona su JPA) | Sí |

Conclusión: a diferencia del doc original (SB 2.7 era obligatorio en OX 7.7 por `javax.*`),
aquí **debemos** usar Spring Boot 3.x+/4.x porque OX 8.0 ya es `jakarta.*`. Usamos la 4.1.

> El tutorial original advierte "usa SB 2.x, no 3.x". Esa advertencia **NO aplica** a OX 8.0: es justo al revés.

---

## 2. Diferencias clave respecto al tutorial (OX 7.7 / SB 2.7)

1. **Versiones**: `spring-boot.version` = `4.1.0` (ajustar al patch real publicado), `openxava.version` = `8.0-SNAPSHOT`.
2. **Imports `javax` → `jakarta`**: en la clase de arranque del doc aparece `import javax.servlet.DispatcherType;`.
   En OX 8.0 debe ser `import jakarta.servlet.DispatcherType;`.
3. **Dependencia de chat**: el doc añade `openxava-7.7-chat-jdk17`. En OX 8.0 **NO existe ni hace falta**:
   `org.openxava.chat.ChatEndpoint` y langchain4j ya vienen dentro del jar `openxava`. Eliminar esa dependencia del ejemplo.
4. **Tipo del datasource**: el doc usa `type = "javax.sql.DataSource"`. Eso se mantiene
   (`javax.sql.DataSource` es JDBC, no Jakarta EE; no cambia). Igual que en `webapp/META-INF/context.xml` actual.
5. **Driver HSQLDB**: alinear con `context.xml` actual (`org.hsqldb.jdbcDriver`) o usar `org.hsqldb.jdbc.JDBCDriver` (ambos válidos).
6. **Nombre de app y paquete**: el doc usa `com.yourcompany.invoicing` / `InvoicingApplication`.
   Aquí: contexto `/invoicedemo`, datasource `jdbc/invoicedemoDS`, BD embebida `invoicedemo-db`,
   y la clase irá en el paquete raíz `org.openxava.invoicedemo`.

---

## 3. Cambios en `pom.xml`

Archivo: `invoicedemo/pom.xml`.

### 3.1 `<properties>`
Añadir la versión de Spring Boot (mantener el resto):

```xml
<properties>
    <openxava.version>8.0-SNAPSHOT</openxava.version>
    <spring-boot.version>4.1.0</spring-boot.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### 3.2 `<dependencyManagement>` (nuevo, al nivel de `<dependencies>` y `<build>`)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3.3 `<dependencies>`
Añadir junto a la dependencia `openxava` ya existente. **No** añadir el artefacto de chat (ya va dentro de `openxava`):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
```

### 3.4 `maven-war-plugin`: quitar la exclusión de Tomcat
En el `<packagingExcludes>` actual hay que **eliminar** la línea:

```
WEB-INF/lib/tomcat-*.jar,
```

Con Tomcat embebido esas librerías son necesarias. El resto de exclusiones se mantienen igual.

### 3.5 `spring-boot-maven-plugin` (nuevo, en `<plugins>`)

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>${spring-boot.version}</version>
    <configuration>
        <mainClass>org.openxava.invoicedemo.InvoicedemoApplication</mainClass>
    </configuration>
</plugin>
```

> Mantener el `maven-dependency-plugin` (unpack de `xava/dtds`) y el `maven-surefire-plugin` tal cual.

---

## 4. `src/main/resources/application.properties` (nuevo)

```properties
server.servlet.context-path=/invoicedemo
server.tomcat.additional-tld-skip-patterns=htmlunit-*.jar,jetty-*.jar
```

El `context-path` debe coincidir con el nombre de la app en `xava/application.xml` y con el `finalName` del WAR
para conservar la misma URL si algún día se despliega en un Tomcat externo.

---

## 5. Clase de arranque Spring Boot (nueva)

Archivo: `src/main/java/org/openxava/invoicedemo/InvoicedemoApplication.java`.

Adaptación de la clase del doc con: paquete `org.openxava.invoicedemo`, import `jakarta.servlet.DispatcherType`,
BD `invoicedemo-db`, datasource `jdbc/invoicedemoDS` y registro de `ChatEndpoint`.

```java
package org.openxava.invoicedemo;

import java.util.EnumSet;

import jakarta.servlet.DispatcherType;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.openxava.chat.ChatEndpoint;
import org.openxava.util.DBServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.openxava.naviox.web.NaviOXFilter;

@SpringBootApplication
@ServletComponentScan(basePackages = { "org.openxava", "com.openxava" })
public class InvoicedemoApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

    public static void main(String[] args) throws Exception {
        DBServer.start("invoicedemo-db"); // BD HSQLDB embebida de desarrollo (como en el lanzador actual)
        SpringApplication.run(InvoicedemoApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InvoicedemoApplication.class);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.jsp");
    }

    @Bean
    public FilterRegistrationBean<NaviOXFilter> naviOXFilter() {
        FilterRegistrationBean<NaviOXFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new NaviOXFilter());
        registration.setName("naviox");
        registration.addUrlPatterns("*.jsp", "/modules/*", "/phone/index.jsp", "/m/*");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        return registration;
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        exporter.setAnnotatedEndpointClasses(ChatEndpoint.class);
        return exporter;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();
                resource.setName("jdbc/invoicedemoDS");
                resource.setType("javax.sql.DataSource");
                resource.setProperty("driverClassName", "org.hsqldb.jdbcDriver");
                resource.setProperty("url", "jdbc:hsqldb:hsql://localhost:1666");
                resource.setProperty("username", "sa");
                resource.setProperty("password", "");
                resource.setProperty("maxTotal", "20");
                resource.setProperty("maxIdle", "5");
                resource.setProperty("maxWaitMillis", "10000");
                context.getNamingResources().addResource(resource);
            }
        };
    }
}
```

Puntos importantes (igual que en el doc, pero válidos para OX 8.0):
- `@ServletComponentScan` escanea los componentes web anotados de OpenXava y NaviOX.
- `SpringBootServletInitializer` mantiene la app desplegable como WAR.
- `NaviOXFilter` se registra a mano porque OpenXava lo define en un *web fragment*, no como bean de Spring.
- `ServerEndpointExporter` registra el endpoint WebSocket de chat (`ChatEndpoint`) en el Tomcat embebido.
- `tomcat.enableNaming()` habilita JNDI en el Tomcat embebido.
- `postProcessContext()` crea el datasource JNDI que OX espera desde `persistence.xml`
  (`java:comp/env/jdbc/invoicedemoDS`), porque el Tomcat embebido **no** usa `webapp/META-INF/context.xml`.

---

## 6. Qué hacer con el lanzador actual

`org.openxava.invoicedemo.run.invoicedemo` (`AppServer.run`) puede:
- **Conservarse** como alternativa para arranque "clásico" sin Spring Boot, o
- **Retirarse** si se quiere usar exclusivamente Spring Boot.

Recomendación: conservarlo de momento; el arranque pasa a hacerse por `InvoicedemoApplication`.

---

## 7. Puntos a verificar / posibles incidencias

1. **Versión exacta de Spring Boot 4.1**: confirmar el patch publicado (p. ej. `4.1.0`) y ajustar `spring-boot.version`.
2. **Paquetes de las clases de servidor embebido en SB 4**: Spring Boot 4 modularizó el proyecto.
   Verificar que los imports `org.springframework.boot.web.embedded.tomcat.*`,
   `org.springframework.boot.web.servlet.*` y `...web.servlet.support.SpringBootServletInitializer`
   siguen en esos paquetes en 4.1 (en `main` siguen así); si no compila, ajustar import al módulo correspondiente.
3. **Conflictos de versiones por el BOM**: `spring-boot-dependencies` puede realinear versiones de Tomcat,
   logging (`commons-logging`/`spring-jcl`), Jackson, etc. que OX ya trae. Revisar `mvn dependency:tree`
   si aparecen errores de arranque o de clases duplicadas.
4. **Autoconfiguración de DataSource**: como NO añadimos `spring-boot-starter-data-jpa`, Spring no debería
   autoconfigurar un datasource. Si se diese el caso, excluir `DataSourceAutoConfiguration`/`HibernateJpaAutoConfiguration`.
5. **Escaneo de TLD/JARs**: si hay warnings o lentitud al arrancar JSP, ampliar `additional-tld-skip-patterns`.
6. **`finalName` y contexto**: `finalName=invoicedemo` ya coincide con `context-path=/invoicedemo`. No tocar.

---

## 8. Cómo ejecutar

- **Desde el IDE**: ejecutar `org.openxava.invoicedemo.InvoicedemoApplication` (método `main`).
- **Desde Maven**:

```
mvn spring-boot:run
```

Luego abrir:

```
http://localhost:8080/invoicedemo
```

---

## 9. Checklist de implementación

- [ ] `pom.xml`: añadir `spring-boot.version` en `<properties>`.
- [ ] `pom.xml`: añadir `<dependencyManagement>` con `spring-boot-dependencies`.
- [ ] `pom.xml`: añadir `spring-boot-starter-web`, `spring-boot-starter-websocket`, `tomcat-embed-jasper`.
- [ ] `pom.xml`: quitar `WEB-INF/lib/tomcat-*.jar` de `<packagingExcludes>`.
- [ ] `pom.xml`: añadir `spring-boot-maven-plugin` con `mainClass`.
- [ ] Crear `src/main/resources/application.properties`.
- [ ] Crear `src/main/java/org/openxava/invoicedemo/InvoicedemoApplication.java`.
- [ ] Arrancar y validar login + módulos + chat en `http://localhost:8080/invoicedemo`.
