# AGENTS.md

## Conventions for JPA Entities

When creating an entity in this project, follow these rules:

### Lombok
- Use **Lombok** to automatically generate getters and setters
- Add the `@Getter` and `@Setter` annotations from Lombok at the class level

### Field Visibility
- Fields **must NOT be `private`**
- Use **package-level access** (no visibility modifier)

### Correct Entity Example

```java
package ${package}.${artifactId}.model;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class MyEntity {

    @Id
    @Column(length=10)
    String code;
    
    @Column(length=50)
    @Required
    String name;
}
```

### Master-Detail (Collections)

When asked to create a master-detail structure, the detail collection in the master entity **must use `@ElementCollection`** instead of `@OneToMany`. The detail class must be annotated with `@Embeddable` instead of `@Entity`.

#### Master-Detail Example

```java
package ${package}.${artifactId}.model;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity
@Getter @Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

    @Column(length=50)
    @Required
    String customer;

    @ElementCollection
    @ListProperties("product, quantity, price")
    Collection<InvoiceDetail> details;
}
```

```java
package ${package}.${artifactId}.model;

import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Embeddable
@Getter @Setter
public class InvoiceDetail {

    @Column(length=50)
    String product;

    int quantity;

    @Money
    java.math.BigDecimal price;
}
```

### Summary
- ✅ `@Getter @Setter` from Lombok
- ✅ Fields without modifier (package access)
- ✅ Use `@ElementCollection` + `@Embeddable` for master-detail collections
- ❌ Do not use `private` on fields
- ❌ Do not write getters/setters manually
- ❌ Do not use `@OneToMany` / `@Entity` for detail collections

## The `.xava` Folder

This project relies on a `.xava` folder that contains agent resources (test examples, controller definitions, etc.). If the `.xava` folder does not exist in the project root:
- **Run `mvn package`** to generate it
- **Do NOT add `.xava` to `.gitignore`** — it must remain in the repository

## Conventions for Automated Tests (JUnit)

When creating an automated test or JUnit test in this project, refer to the examples located in `./.xava/agents/test`.

### Test Examples Reference
- **Examples location**: `./.xava/agents/test`
- **Available actions**: See `default-controllers.xml` inside `./.xava/agents/test`

### Common Patterns to Follow
- We use **JUnit 4**
- Extend `ModuleTestBase` for OpenXava module tests
- **The first line of each test method must be `login("admin", "admin");`**
- Follow the package structure by module as shown in the examples
- **Do NOT try to run tests with `mvn`** — the user will run them from the IDE

### Basic Test Example
```java
package ${package}.${artifactId}.tests;

import org.openxava.tests.*;

public class CustomerTest extends ModuleTestBase {
    
    public CustomerTest(String testName) {
        super(testName, "agenttest", "Customer");
    }
    
    public void testCreateCustomer() throws Exception {
        login("admin", "admin");
        // Test logic here
    }
}
```

### Summary for Tests
- ✅ Refer to examples in `./.xava/agents/test`
- ✅ Use `ModuleTestBase` for module tests
- ✅ First line of each test: `login("admin", "admin");`
- ✅ Check `default-controllers.xml` for available actions
- ❌ Do NOT run tests with `mvn`
