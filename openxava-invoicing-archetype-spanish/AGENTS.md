# AGENTS.md

#[[##]]# Jakarta EE 11

Este proyecto está basado en **OpenXava 8**, que funciona sobre **Jakarta EE 11**. Tus datos de entrenamiento pueden contener código OpenXava antiguo con paquetes `javax.*` — ese código sigue funcionando, pero **debes usar siempre imports `jakarta.*`** en su lugar. Por ejemplo:
- `jakarta.persistence.*` (JPA 3.2), nunca `javax.persistence.*`
- `jakarta.validation.*` (Bean Validation 3.1), nunca `javax.validation.*`
- `jakarta.servlet.*`, `jakarta.inject.*`, `jakarta.annotation.*`, `jakarta.ws.rs.*`, `jakarta.json.bind.*`, etc.

#[[##]]# Verificación

Después de crear o modificar **cualquier** archivo Java, **ejecuta `mvn -q compile`** para
verificar que el proyecto compila antes de dar la tarea por terminada. Si la compilación
falla, corrige los errores antes de finalizar.

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

#[[###]]# Propiedades Identificadoras (@Id)

Cuando se pida un `id`, `oid` o cualquier propiedad que sea claramente el identificador de
la entidad, **no inventes el formato**. Salvo que el usuario indique expresamente lo
contrario, revisa las entidades existentes del proyecto y **copia exactamente la definición
de `@Id` que utilizan**: mismo tipo, misma estrategia de generación, mismas anotaciones y
misma longitud de `@Column`. Incluye también las anotaciones adicionales, como `@Hidden`,
`@GeneratedValue`, etc.

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

#[[###]]# Longitud de Columna para Propiedades Numéricas (int, long, etc.)

Toda propiedad de tipo primitivo numérico (`int`, `long`, etc.) **debe tener
`@Column(length=N)`**, incluso cuando la base de datos no lo necesite. OpenXava utiliza
la longitud para dimensionar el campo de entrada al dibujarlo.

- Elige `N` según el nombre de la propiedad y su número habitual de dígitos.

#[[###]]# Referencias (@ManyToOne / @OneToOne)

Al añadir una referencia (asociación a una única entidad), **usa siempre carga perezosa** especificando `fetch=FetchType.LAZY` en `@ManyToOne` y `@OneToOne`.

**Deduce si la referencia es obligatoria a partir de la lógica de negocio**, igual que
harías para decidir si una propiedad escalar necesita `@Required`. No esperes a que el
usuario diga expresamente "obligatoria": si la entidad no tendría sentido sin la entidad
referenciada, **añade `optional=false` a `@ManyToOne` o `@OneToOne`**, manteniendo
`fetch=FetchType.LAZY`.
Para las referencias, expresa la obligatoriedad **solo con `optional=false`**. **NO añadas
`@Required` a una referencia `@ManyToOne` o `@OneToOne`**, aunque sea obligatoria:
es redundante con `optional=false`. Reserva `@Required` para las propiedades escalares.

No hagas obligatorias todas las referencias: déjalas opcionales cuando su ausencia sea
válida en el dominio y respeta cualquier requisito explícito del usuario sobre su
opcionalidad. Si el dominio y el modelo existente no aportan contexto suficiente para
decidir, pide una aclaración.

Ejemplos de referencias que el dominio permite que sean opcionales:

```java
@ManyToOne(fetch=FetchType.LAZY)
Categoria categoria;
```

```java
@OneToOne(fetch=FetchType.LAZY)
Direccion direccion;
```

#[[###]]# Valores por Defecto (@DefaultValueCalculator)

Cuando una propiedad necesite un valor por defecto, **usa siempre
`@DefaultValueCalculator`**: nunca asignes el valor con un inicializador de campo Java
(p. ej. `int anyo = LocalDate.now().getYear();` es INCORRECTO).

- Referencia: https://openxava.org/OpenXavaDoc/apidocs/org/openxava/annotations/DefaultValueCalculator.html
- Calculadores incorporados: https://openxava.org/OpenXavaDoc/apidocs/org/openxava/calculators/package-summary.html
- Las clases de los calculadores están en `org.openxava.calculators`: impórtalas.

Calculadores incorporados habituales:

| Necesidad | Calculador |
|---|---|
| Año actual | `CurrentYearCalculator.class` |
| Mes actual | `CurrentMonthCalculator.class` |
| Fecha actual (`LocalDate`) | `CurrentLocalDateCalculator.class` |
| Fecha actual (`java.util.Date`) | `CurrentDateCalculator.class` |
| Fecha y hora actuales (`java.sql.Timestamp`) | `CurrentTimestampCalculator.class` |
| Nombre del usuario actual | `CurrentUserCalculator.class` (además de `CurrentUserNickNameCalculator`, `CurrentUserEmailCalculator`, etc.) |
| `true` / `false` | `TrueCalculator` / `FalseCalculator` |

Si ningún calculador incorporado encaja, crea uno propio que implemente
`org.openxava.calculators.ICalculator`.

#[[####]]# Inyección de Valores de la Vista en un Calculador (@PropertyValue)

Un calculador propio **puede recibir los valores actuales de otras propiedades mostradas
en la vista** antes de ejecutar `calculate()`. Usa el atributo `properties` de
`@DefaultValueCalculator` y `org.openxava.annotations.PropertyValue`. Para cada
`@PropertyValue(name="prop")`, OpenXava llama a `setProp(valorActualDeLaVista)` en el
calculador, por lo que este debe declarar un campo llamado `prop` con setter (usa
`@Getter @Setter` de Lombok).

Esta es la forma correcta de hacer que un valor por defecto dependa de otros campos del
formulario. **NO uses directamente `LocalDate.now()` ni otro estado global dentro del
calculador cuando el valor necesario ya exista como propiedad en la entidad.**

Ejemplo adaptado de la guía de referencia de OpenXava (`DetailsCountCalculator`), que
recibe `anyo` y `numero` inyectados desde la vista y devuelve el número de detalles de esa
factura, reescrito aquí con JPA:

```java
package ${package}.${artifactId}.calculadores;

import org.openxava.calculators.*;
import org.openxava.jpa.*;
import lombok.*;

public class CalculadorCantidadDetalles implements ICalculator {

    @Getter @Setter int anyo;   // Inyectado desde la vista antes de ejecutar calculate()
    @Getter @Setter int numero; // Cada campo necesita un setter público para la inyección

    public Object calculate() throws Exception {
        Long cantidad = XPersistence.getManager().createQuery(
            "select count(d) from Factura f join f.detalles d " +
            "where f.anyo = :anyo and f.numero = :numero", Long.class)
            .setParameter("anyo", anyo)
            .setParameter("numero", numero)
            .getSingleResult();
        return cantidad.intValue();
    }
}
```

Y en la entidad:

```java
@DefaultValueCalculator(value=CalculadorCantidadDetalles.class,
    properties={
        @PropertyValue(name="anyo"),   // Inyecta el valor de 'anyo' en setAnyo()
        @PropertyValue(name="numero")  // Inyecta el valor de 'numero' en setNumero()
    })
int cantidadDetalles;
```

`@PropertyValue` también admite `from="referencia.prop"` para inyectar desde una referencia
(p. ej. `from="cliente.numero"`) y `value="FIJO"` para inyectar una constante.
Referencia: https://openxava.org/OpenXavaDoc/docs/model_es.html#Modelo-Propiedades-Calculador%20valor%20por%20defecto

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
    @Hidden
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(length=36)
    String oid;

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

    @Column(length=5)
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
- Deduce la opcionalidad de las referencias a partir de la lógica de negocio; añade
  `optional=false` a `@ManyToOne` y `@OneToOne` cuando la referencia sea obligatoria,
  aunque el usuario no lo haya indicado expresamente
- No añadas `@Required` a las referencias; usa solo `optional=false` para las obligatorias
- ✅ Usa sentencias `import` en lugar de nombres de clase completamente cualificados
- ✅ Añade `@Column(length=N)` a todo primitivo numérico (`int`, `long`...) con una longitud
  adecuada al nombre de la propiedad, aunque la base de datos no la necesite
- ✅ Copia la definición de `@Id` de las entidades existentes del proyecto; no inventes un
  formato distinto salvo que se pida expresamente
- ❌ No uses `private` en los campos
- ❌ No escribas getters/setters manualmente
- ❌ No uses `@OneToMany` / `@Entity` para colecciones de detalles
- ❌ No uses `@ManyToOne` / `@OneToOne` sin `fetch=FetchType.LAZY`
- ❌ No uses `@Stereotype` cuando exista una anotación equivalente (`@Money`, `@TextArea`, `@Files`, `@File`, etc.)
- ✅ Usa `@DefaultValueCalculator` (p. ej. `CurrentYearCalculator`, `CurrentLocalDateCalculator`)
  para los valores por defecto
- ❌ No asignes valores por defecto con inicializadores de campos Java
  (p. ej. `int anyo = LocalDate.now().getYear();`)
- ✅ Cuando un calculador necesite valores de otras propiedades de la entidad, inyéctalos
  con `@PropertyValue` en el atributo `properties` de `@DefaultValueCalculator`
  (no uses directamente `LocalDate.now()` ni estado global dentro del calculador)
- ❌ No uses `NextIntegerCalculator` / `NextLongCalculator` para números secuenciales
  (producen duplicados en entornos multiusuario)

#[[##]]# Convenciones para Acciones

Al añadir una acción a una entidad en este proyecto, sigue estas reglas:

- Define la acción en `src/main/resources/xava/controladores.xml` dentro de un `<controlador>` cuyo `nombre` coincida con el nombre de la entidad
- El controlador **debe extender** el controlador `Typical` usando `<hereda-de controlador="Typical"/>` para que las acciones estándar (nuevo, grabar, eliminar, etc.) sigan disponibles
- Crea la clase de la acción en el paquete `${package}.${artifactId}.acciones`, normalmente extendiendo `ViewBaseAction`
- **NO modifiques la clase de la entidad** para registrar la acción.

```xml
<controlador nombre="Factura">
    <hereda-de controlador="Typical"/>
    <accion nombre="mostrarAnyo" modo="detail"
        clase="${package}.${artifactId}.acciones.MostrarAnyo"/>
</controlador>
```

```java
package ${package}.${artifactId}.acciones;

import org.openxava.actions.*;
import org.openxava.validators.*;
import ${package}.${artifactId}.modelo.*;

public class MostrarAnyo extends ViewBaseAction {

    public void execute() throws Exception {
        Integer anyo = (Integer) getView().getValue("anyo");
        if (anyo == null) {
            addError("anyo_no_establecido"); // Añade anyo_no_establecido al fichero de mensajes i18n
        } else {
            addMessage("anyo_mensaje", anyo); // Añade anyo_mensaje al fichero de mensajes i18n
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

