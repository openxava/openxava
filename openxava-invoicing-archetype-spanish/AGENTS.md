# AGENTS.md

## Convenciones para Entidades JPA

Al crear una entidad en este proyecto, sigue estas reglas:

### Lombok
- Usa **Lombok** para generar automáticamente getters y setters
- Añade las anotaciones `@Getter` y `@Setter` de Lombok a nivel de clase

### Visibilidad de Campos
- Los campos **NO deben ser `private`**
- Usa **acceso a nivel de paquete** (sin modificador de visibilidad)

### Ejemplo de Entidad Correcta

```java
package ${package}.${artifactId}.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class MiEntidad {

    @Id
    @Column(length=10)
    String codigo;
    
    @Column(length=50)
    @Required
    String nombre;
}
```

### Maestro-Detalle (Colecciones)

Al crear una estructura maestro-detalle, la colección de detalles en la entidad maestra **debe usar `@ElementCollection`** en lugar de `@OneToMany`. La clase de detalle debe estar anotada con `@Embeddable` en lugar de `@Entity`.

#### Ejemplo Maestro-Detalle

```java
package ${package}.${artifactId}.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Factura {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @Column(length=50)
    @Required
    String cliente;

    @ElementCollection
    @ListProperties("producto, cantidad, precio")
    Collection<DetalleFactura> detalles;
}
```

```java
package ${package}.${artifactId}.modelo;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Embeddable
@Getter @Setter
public class DetalleFactura {

    @Column(length=50)
    String producto;

    int cantidad;

    @Money
    java.math.BigDecimal precio;
}
```

### Resumen
- ✅ `@Getter @Setter` de Lombok
- ✅ Campos sin modificador (acceso de paquete)
- ✅ Usa `@ElementCollection` + `@Embeddable` para colecciones maestro-detalle
- ❌ No uses `private` en los campos
- ❌ No escribas getters/setters manualmente
- ❌ No uses `@OneToMany` / `@Entity` para colecciones de detalles

## La Carpeta `.xava`

Este proyecto depende de una carpeta `.xava` que contiene recursos para agentes (ejemplos de tests, definiciones de controladores, etc.). Si la carpeta `.xava` no existe en la raíz del proyecto:
- **Ejecuta `mvn package`** para generarla
- **NO añadas `.xava` a `.gitignore`** — debe permanecer en el repositorio

## Convenciones para Tests Automatizados (JUnit)

Al crear un test automatizado o test JUnit en este proyecto, consulta los ejemplos ubicados en `./.xava/agents/test`.

### Referencia de Ejemplos de Tests
- **Ubicación de ejemplos**: `./.xava/agents/test`
- **Acciones disponibles**: Consulta `default-controllers.xml` dentro de `./.xava/agents/test`

### Patrones Comunes a Seguir
- Usamos **JUnit 4**
- Extiende `ModuleTestBase` para tests de módulos OpenXava
- **La primera línea de cada método de test debe ser `login("admin", "admin");`**
- Sigue la estructura de paquetes por módulo como se muestra en los ejemplos
- **NO intentes ejecutar tests con `mvn`** — el usuario los ejecutará desde el IDE

### Ejemplo de Test Básico
```java
package ${package}.${artifactId}.pruebas;

import org.openxava.tests.*;

public class ClienteTest extends ModuleTestBase {
    
    public ClienteTest(String testName) {
        super(testName, "agenttest", "Cliente");
    }
    
    public void testCrearCliente() throws Exception {
        login("admin", "admin");
        // Lógica del test aquí
    }
}
```

### Resumen para Tests
- ✅ Consulta ejemplos en `./.xava/agents/test`
- ✅ Usa `ModuleTestBase` para tests de módulos
- ✅ Primera línea de cada test: `login("admin", "admin");`
- ✅ Consulta `default-controllers.xml` para acciones disponibles
- ❌ NO ejecutes tests con `mvn`
