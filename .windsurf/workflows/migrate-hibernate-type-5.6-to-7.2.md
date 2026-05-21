---
description: Migrate custom Type implementations from Hibernate 5.6 to 7.2
---

# Guía para migrar Type personalizados de Hibernate 5.6 a 7.2

## Contexto

TMR BORRAR

En Hibernate 6.0+ (incluyendo 7.2), la forma de usar tipos personalizados cambió significativamente:
- `@TypeDef` y `@TypeDefs` fueron eliminados
- El atributo `type` (String) de `@Type` fue eliminado
- `@Type` ahora espera un `Class<? extends UserType<?>>` en lugar de un String

## Pasos para migrar un Type personalizado

### 1. Identificar el Type personalizado

Busca clases que extiendan `AbstractSingleColumnStandardBasicType` o implementen interfaces de tipos de Hibernate 5.6.

Ejemplo de código Hibernate 5.6:
```java
public class SiNoType extends AbstractSingleColumnStandardBasicType<Boolean> {
    public SiNoType() {
        super(CharJdbcType.INSTANCE, new BooleanJavaType('S', 'N'));
    }
    // ...
}
```

### 2. Cambiar a implementación de UserType

Reemplaza la extensión de `AbstractSingleColumnStandardBasicType<T>` por la implementación de `UserType<T>`.

Antes (Hibernate 5.6):
```java
public class SiNoType extends AbstractSingleColumnStandardBasicType<Boolean> {
    // ...
}
```

Después (Hibernate 7.2):
```java
public class SiNoType implements UserType<Boolean> {
    public static final SiNoType INSTANCE = new SiNoType();
    // ...
}
```

### 3. Implementar los métodos de UserType

Implementa todos los métodos requeridos por la interfaz `UserType<T>`:

```java
@Override
public int getSqlType() {
    return SqlTypes.CHAR; // o el tipo SQL apropiado
}

@Override
public Class<Boolean> returnedClass() {
    return Boolean.class;
}

@Override
public boolean equals(Boolean x, Boolean y) {
    if (x == y) return true;
    if (x == null || y == null) return false;
    return x.equals(y);
}

@Override
public int hashCode(Boolean x) {
    return x == null ? 0 : x.hashCode();
}

@Override
public Boolean nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
    String value = rs.getString(position);
    if (value == null) return null;
    return "S".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
}

@Override
public void nullSafeSet(PreparedStatement st, Boolean value, int index, WrapperOptions options) throws SQLException {
    if (value == null) {
        st.setNull(index, SqlTypes.CHAR);
    } else {
        st.setString(index, value ? "S" : "N");
    }
}

@Override
public Boolean deepCopy(Boolean value) {
    return value; // Boolean es inmutable
}

@Override
public boolean isMutable() {
    return false;
}

@Override
public Serializable disassemble(Boolean value) {
    return value;
}

@Override
public Boolean assemble(Serializable cached, Object owner) {
    return (Boolean) cached;
}

@Override
public Boolean replace(Boolean original, Boolean target, Object owner) {
    return original;
}
```

### 4. Agregar los imports necesarios

```java
import java.io.*;
import java.sql.*;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.*;
```

### 5. Actualizar las entidades

Cambia el uso de `@Type` en las entidades para usar la clase directamente en lugar de un String.

Antes (Hibernate 5.6):
```java
@Type(type="org.openxava.types.SiNoType")
private boolean paid;
```

Después (Hibernate 7.2):
```java
@Type(org.openxava.types.SiNoType.class)
private boolean paid;
```

Nota: El parámetro `value=` es redundante y puede omitirse.

### 6. Recompilar e instalar el framework

Después de actualizar el Type personalizado en el framework:

```bash
cd /path/to/openxava
mvn compile
mvn install
```

### 7. Verificar que las entidades compilen

```bash
cd /path/to/openxavatest
mvn compile
```

## Consideraciones importantes

- **Tipos inmutables**: Si tu tipo es inmutable (como Boolean, String, Integer), `deepCopy` puede retornar el mismo valor.
- **Tipos mutables**: Si tu tipo es mutable, `deepCopy` debe crear una copia profunda del valor.
- **isMutable()**: Retorna `true` si el tipo puede cambiar después de ser creado, `false` si es inmutable.
- **getSqlType()**: Usa constantes de `SqlTypes` en lugar de `java.sql.Types`.
- **WrapperOptions**: Es un parámetro nuevo en Hibernate 6+ para opciones de wrapper JDBC.

## Referencias

- [Hibernate 7.2 Javadoc: UserType](https://docs.hibernate.org/orm/7.2/javadocs/org/hibernate/usertype/UserType.html)
- [Hibernate 7.2 Javadoc: Type annotation](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/annotations/Type.html)
- [Hibernate 6.0 Migration Guide](https://docs.hibernate.org/orm/6.0/migration-guide/migration-guide.html)