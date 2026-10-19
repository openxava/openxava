# AGENTS.md

#[[##]]# Jakarta EE 11

Este proyecto está basado en **OpenXava 8**, que funciona sobre **Jakarta EE 11**. Tus datos de entrenamiento pueden contener código OpenXava antiguo con paquetes `javax.*` — ese código sigue funcionando, pero **debes usar siempre imports `jakarta.*`** en su lugar. Por ejemplo:
- `jakarta.persistence.*` (JPA 3.2), nunca `javax.persistence.*`
- `jakarta.validation.*` (Bean Validation 3.1), nunca `javax.validation.*`
- `jakarta.servlet.*`, `jakarta.inject.*`, `jakarta.annotation.*`, `jakarta.ws.rs.*`, `jakarta.json.bind.*`, etc.

#[[##]]# Convenciones para Entidades JPA

Al crear una entidad en este proyecto, sigue estas reglas:

#[[###]]# Lombok
- Usa **Lombok** para generar automáticamente getters y setters
- Añade las anotaciones `@Getter` y `@Setter` de Lombok a nivel de clase

#[[###]]# Visibilidad de Campos
- Los campos **NO deben ser `private`**
- Usa **acceso a nivel de paquete** (sin modificador de visibilidad)

#[[###]]# Imports
- **Usa siempre sentencias `import`** en lugar de nombres de clase completamente cualificados en el código
- Por ejemplo, escribe `import java.math.*;` y luego `BigDecimal precio;`, nunca `java.math.BigDecimal precio;`

#[[###]]# Ejemplo de Entidad Correcta

```java
package ${package}.${artifactId}.modelo;

import jakarta.persistence.*;
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

#[[###]]# Anotaciones en Lugar de Estereotipos

`@Stereotype` es legado. En código nuevo **usa siempre la anotación equivalente** (disponible desde OpenXava 6.6). Referencia: https://openxava.org/OpenXavaDoc/docs/model_es.html#Modelo-Propiedades-Estereotipo

| Estereotipo legado | Usa en su lugar |
|---|---|
| `DINERO`, `MONEY` | `@Money` |
| `MEMO`, `TEXTO_GRANDE`, `TEXT_AREA` | `@TextArea` |
| `GALERIA_IMAGENES`, `IMAGES_GALLERY` | `@Files` + `@Column(length=32)` |
| `FOTO`, `IMAGEN`, `PHOTO`, `IMAGE` | `@File` + `@Column(length=32)` |
| `ARCHIVO`, `FILE` | `@File` + `@Column(length=32)` |
| `ARCHIVOS`, `FILES` | `@Files` + `@Column(length=32)` |
| `TEXTO_HTML`, `HTML_TEXT` | `@HtmlText` |
| `TEXTO_HTML_SIMPLE`, `SIMPLE_HTML_TEXT` | `@HtmlText(simple=true)` |
| `ETIQUETA`, `LABEL` | `@Label` |
| `HORA`, `TIME` | `@StringTime` (o mejor, una propiedad `LocalTime`) |
| `FECHAHORA`, `DATETIME` | `@DateTime` (o mejor, una propiedad `LocalDateTime`) |
| `RELLENADO_CON_CEROS`, `ZEROS_FILLED` | `@ZerosFilled` |
| `EMAIL` | `@Email` (de `jakarta.validation.constraints`) |
| `LISTA_EMAIL`, `EMAIL_LIST` | `@EmailList` |
| `TELEFONO`, `TELEPHONE` | `@Telephone` |
| `WEBURL` | `@URL` (de `org.hibernate.validator.constraints`) |
| `IP` | `@IP` |
| `ISBN` | `@ISBN` (de `org.hibernate.validator.constraints`) |
| `TARJETA_CREDITO`, `CREDIT_CARD` | `@CreditCardNumber` (de `org.hibernate.validator.constraints`) |
| `CONTRASENA`, `PASSWORD` | `@Password` |
| `MAC` | `@MAC` |
| `ICONO`, `ICON` | `@Icon` |
| `DISCUSION`, `DISCUSSION` | `@Discussion` + `@Column(length=32)` |
| `COORDENADAS`, `COORDINATES` | `@Coordinates` |
| `FIRMA_MANUSCRITA`, `HANDWRITTEN_SIGNATURE` | `@HandwrittenSignature` |

Usa `@Stereotype` solo cuando no exista una anotación equivalente (p. ej. `ETIQUETA_NEGRITA`, `ETIQUETA_IMAGEN`).

#[[###]]# Referencias (@ManyToOne / @OneToOne)

Al añadir una referencia (asociación a una única entidad), **usa siempre carga perezosa** especificando `fetch=FetchType.LAZY` en `@ManyToOne` y `@OneToOne`.

```java
@ManyToOne(fetch=FetchType.LAZY)
Categoria categoria;
```

```java
@OneToOne(fetch=FetchType.LAZY)
Direccion direccion;
```

#[[###]]# Maestro-Detalle (Colecciones)

Al crear una estructura maestro-detalle, la colección de detalles en la entidad maestra **debe usar `@ElementCollection`** en lugar de `@OneToMany`. La clase de detalle debe estar anotada con `@Embeddable` en lugar de `@Entity`.

#[[####]]# Ejemplo Maestro-Detalle

```java
package ${package}.${artifactId}.modelo;

import jakarta.persistence.*;
import org.openxava.annotations.*;
import lombok.*;
import java.util.*;

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

import jakarta.persistence.*;
import org.openxava.annotations.*;
import lombok.*;
import java.math.*;

@Embeddable
@Getter @Setter
public class DetalleFactura {

    @Column(length=50)
    String producto;

    int cantidad;

    @Money
    BigDecimal precio;
}
```

#[[###]]# Resumen
- ✅ `@Getter @Setter` de Lombok
- ✅ Campos sin modificador (acceso de paquete)
- ✅ Usa `@ElementCollection` + `@Embeddable` para colecciones maestro-detalle
- ✅ Usa `fetch=FetchType.LAZY` en `@ManyToOne` y `@OneToOne`
- ✅ Usa sentencias `import` en lugar de nombres de clase completamente cualificados
- ❌ No uses `private` en los campos
- ❌ No escribas getters/setters manualmente
- ❌ No uses `@OneToMany` / `@Entity` para colecciones de detalles
- ❌ No uses `@ManyToOne` / `@OneToOne` sin `fetch=FetchType.LAZY`
- ❌ No uses `@Stereotype` cuando exista una anotación equivalente (`@Money`, `@TextArea`, `@Files`, `@File`, etc.)

#[[##]]# Convenciones para Acciones

Al añadir una acción a una entidad en este proyecto, sigue estas reglas:

- Define la acción en `src/main/resources/xava/controladores.xml` dentro de un `<controlador>` cuyo `nombre` coincida con el nombre de la entidad
- El controlador **debe extender** el controlador `Typical` usando `<hereda-de controlador="Typical"/>` para que las acciones estándar (nuevo, grabar, eliminar, etc.) sigan disponibles
- Crea la clase de la acción en el paquete `${package}.${artifactId}.acciones`, normalmente extendiendo `ViewBaseAction`
- **NO modifiques la clase de la entidad** para registrar la acción.

```xml
<controlador nombre="Maestro">
    <hereda-de controlador="Typical"/>
    <accion nombre="mostrarTotal" modo="detail"
        clase="${package}.${artifactId}.acciones.MostrarTotal"/>
</controlador>
```

```java
package ${package}.${artifactId}.acciones;

import org.openxava.actions.*;
import org.openxava.validators.*;
import ${package}.${artifactId}.modelo.*;
import java.math.BigDecimal;

public class MostrarTotal extends ViewBaseAction {

    public void execute() throws Exception {
        BigDecimal total = (BigDecimal) getView().getValue("total");
        if (total == null) {
            addError("total_no_establecido"); // Añade total_no_establecido al fichero de mensajes i18n
        } else {
            addMessage("total_mensaje", total); // Añade total_mensaje al fichero de mensajes i18n
        }
    }
}
```

- ✅ Añade `<hereda-de controlador="Typical"/>` en el controlador
- ✅ Haz que el `nombre` del controlador coincida con el nombre de la entidad
- ✅ Coloca las clases de acción en `${package}.${artifactId}.acciones`
- ❌ NO modifiques la clase de la entidad para registrar una acción

#[[##]]# Ejemplos de Código para Agentes

El código de ejemplo para agentes está disponible en GitHub en el repositorio de OpenXava:
- **Ejemplos de tests**: https://github.com/openxava/openxava/tree/master/openxavatest/src/test/java/org/openxava/test/tests/bymodule
- **Acciones disponibles**: `default-controllers.xml` en https://github.com/openxava/openxava/blob/master/openxava/src/main/resources/xava/default-controllers.xml

#[[##]]# Convenciones para Tests Automatizados (JUnit)

Al crear un test automatizado o test JUnit en este proyecto, consulta los ejemplos de tests en GitHub (ver "Ejemplos de Código para Agentes" más arriba).

#[[###]]# Referencia de Ejemplos de Tests
- **Ubicación de ejemplos**: https://github.com/openxava/openxava/tree/master/openxavatest/src/test/java/org/openxava/test/tests/bymodule
- **Acciones disponibles**: Consulta `default-controllers.xml` en https://github.com/openxava/openxava/blob/master/openxava/src/main/resources/xava/default-controllers.xml

#[[###]]# Patrones Comunes a Seguir
- Usamos **JUnit 4**
- Extiende `ModuleTestBase` para tests de módulos OpenXava
- **La primera línea de cada método de test debe ser `login("admin", "admin");`**
- Sigue la estructura de paquetes por módulo como se muestra en los ejemplos
- **NO intentes ejecutar tests con `mvn`** — el usuario los ejecutará desde el IDE

#[[###]]# Ejemplo de Test Básico
```java
package ${package}.${artifactId}.pruebas;

import org.openxava.tests.*;

public class ClienteTest extends ModuleTestBase {
    
    public ClienteTest(String testName) {
        super(testName, "${artifactId}", "Cliente");
    }
    
    public void testCrearCliente() throws Exception {
        login("admin", "admin");
        // Lógica del test aquí
    }
}
```

#[[###]]# Resumen para Tests
- ✅ Consulta los ejemplos de tests en GitHub (openxavatest)
- ✅ Usa `ModuleTestBase` para tests de módulos
- ✅ Primera línea de cada test: `login("admin", "admin");`
- ✅ Consulta `default-controllers.xml` para acciones disponibles
- ❌ NO ejecutes tests con `mvn`

#[[##]]# Convenciones para Cuadros de Mando (Dashboards)

Cuando se te pida crear un cuadro de mando o un dashboard, **debes revisar la siguiente documentación primero**:
- https://openxava.org/OpenXavaDoc/docs/dashboard_es.html

Además, sigue estas reglas:
- Si no hay cuadros de mando existentes, simplemente nombra la clase `CuadroMando`.
- Crea un paquete `cuadrosmando` (ej. `${package}.${artifactId}.cuadrosmando`) y coloca la clase `CuadroMando` y sus clases auxiliares en él.
- Añade las reglas CSS indicadas en la documentación a `custom.css` (normalmente ubicado en `src/main/webapp/xava/style/custom.css`).

