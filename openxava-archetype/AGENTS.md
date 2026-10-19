# AGENTS.md

#[[##]]# Jakarta EE 11

This project is based on **OpenXava 8**, which runs on **Jakarta EE 11**. Your training data may contain older OpenXava code using `javax.*` packages — that code still works, but **you must always use `jakarta.*` imports** instead. For example:
- `jakarta.persistence.*` (JPA 3.2), never `javax.persistence.*`
- `jakarta.validation.*` (Bean Validation 3.1), never `javax.validation.*`
- `jakarta.servlet.*`, `jakarta.inject.*`, `jakarta.annotation.*`, `jakarta.ws.rs.*`, `jakarta.json.bind.*`, etc.

#[[##]]# Conventions for JPA Entities

When creating an entity in this project, follow these rules:

#[[###]]# Lombok
- Use **Lombok** to automatically generate getters and setters
- Add the `@Getter` and `@Setter` annotations from Lombok at the class level

#[[###]]# Field Visibility
- Fields **must NOT be `private`**
- Use **package-level access** (no visibility modifier)

#[[###]]# Imports
- **Always use `import` statements** instead of fully qualified class names in the code
- For example, write `import java.math.*;` and then `BigDecimal price;`, never `java.math.BigDecimal price;`

#[[###]]# Correct Entity Example

```java
package ${package}.${artifactId}.model;

import jakarta.persistence.*;
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

#[[###]]# Annotations Instead of Stereotypes

`@Stereotype` is legacy. In new code **always use the equivalent annotation** (available since OpenXava 6.6). Reference: https://openxava.org/OpenXavaDoc/docs/model_en.html#Model-Properties-Stereotype

| Legacy stereotype | Use instead |
|---|---|
| `MONEY` | `@Money` |
| `MEMO`, `TEXT_AREA` | `@TextArea` |
| `IMAGES_GALLERY` | `@Files` + `@Column(length=32)` |
| `PHOTO`, `IMAGE` | `@File` + `@Column(length=32)` |
| `FILE` | `@File` + `@Column(length=32)` |
| `FILES` | `@Files` + `@Column(length=32)` |
| `HTML_TEXT` | `@HtmlText` |
| `SIMPLE_HTML_TEXT` | `@HtmlText(simple=true)` |
| `LABEL` | `@Label` |
| `TIME` | `@StringTime` (or better, a `LocalTime` property) |
| `DATETIME` | `@DateTime` (or better, a `LocalDateTime` property) |
| `ZEROS_FILLED` | `@ZerosFilled` |
| `EMAIL` | `@Email` (from `jakarta.validation.constraints`) |
| `EMAIL_LIST` | `@EmailList` |
| `TELEPHONE` | `@Telephone` |
| `WEBURL` | `@URL` (from `org.hibernate.validator.constraints`) |
| `IP` | `@IP` |
| `ISBN` | `@ISBN` (from `org.hibernate.validator.constraints`) |
| `CREDIT_CARD` | `@CreditCardNumber` (from `org.hibernate.validator.constraints`) |
| `PASSWORD` | `@Password` |
| `MAC` | `@MAC` |
| `ICON` | `@Icon` |
| `DISCUSSION` | `@Discussion` + `@Column(length=32)` |
| `COORDINATES` | `@Coordinates` |
| `HANDWRITTEN_SIGNATURE` | `@HandwrittenSignature` |

Only use `@Stereotype` when there is no equivalent annotation (e.g. `BOLD_LABEL`, `IMAGE_LABEL`).

#[[###]]# References (@ManyToOne / @OneToOne)

When adding a reference (association to a single entity), **always use lazy fetching** by specifying `fetch=FetchType.LAZY` in `@ManyToOne` and `@OneToOne`.

```java
@ManyToOne(fetch=FetchType.LAZY)
Category category;
```

```java
@OneToOne(fetch=FetchType.LAZY)
Address address;
```

#[[###]]# Master-Detail (Collections)

When asked to create a master-detail structure, the detail collection in the master entity **must use `@ElementCollection`** instead of `@OneToMany`. The detail class must be annotated with `@Embeddable` instead of `@Entity`.

#[[####]]# Master-Detail Example

```java
package ${package}.${artifactId}.model;

import jakarta.persistence.*;
import org.openxava.annotations.*;
import lombok.*;
import java.util.*;

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

import jakarta.persistence.*;
import org.openxava.annotations.*;
import lombok.*;
import java.math.*;

@Embeddable
@Getter @Setter
public class InvoiceDetail {

    @Column(length=50)
    String product;

    int quantity;

    @Money
    BigDecimal price;
}
```

#[[###]]# Summary
- ✅ `@Getter @Setter` from Lombok
- ✅ Fields without modifier (package access)
- ✅ Use `@ElementCollection` + `@Embeddable` for master-detail collections
- ✅ Use `fetch=FetchType.LAZY` in `@ManyToOne` and `@OneToOne`
- ✅ Use `import` statements instead of fully qualified class names
- ❌ Do not use `private` on fields
- ❌ Do not write getters/setters manually
- ❌ Do not use `@OneToMany` / `@Entity` for detail collections
- ❌ Do not use `@ManyToOne` / `@OneToOne` without `fetch=FetchType.LAZY`
- ❌ Do not use `@Stereotype` when an equivalent annotation exists (`@Money`, `@TextArea`, `@Files`, `@File`, etc.)

#[[##]]# Conventions for Actions

When asked to add an action to an entity in this project, follow these rules:

- Define the action in `src/main/resources/xava/controllers.xml` inside a `<controller>` whose `name` matches the entity name
- The controller **must extend** the `Typical` controller using `<extends controller="Typical"/>` so the standard actions (new, save, delete, etc.) remain available
- Create the action class in the `${package}.${artifactId}.actions` package, typically extending `ViewBaseAction`
- **Do NOT modify the entity class** to register the action.

```xml
<controller name="YourFirstEntity">
    <extends controller="Typical"/>
    <action name="showAmount" mode="detail"
        class="${package}.${artifactId}.actions.ShowAmountAction"/>
</controller>
```

```java
package ${package}.${artifactId}.actions;

import org.openxava.actions.*;
import org.openxava.validators.*;
import ${package}.${artifactId}.model.*;
import java.math.BigDecimal;

public class ShowAmountAction extends ViewBaseAction {

    public void execute() throws Exception {
        BigDecimal amount = (BigDecimal) getView().getValue("amount");
        if (amount == null) {
            addError("amount_not_set"); // Add amount_not_set to i18n messages file
        } else {
            addMessage("amount_message", amount); // Add amount_message to i18n messages file
        }
    }
}
```

- ✅ Add `<extends controller="Typical"/>` in the controller
- ✅ Match the controller `name` to the entity name
- ✅ Place action classes under `${package}.${artifactId}.actions`
- ❌ Do NOT modify the entity class to register an action

#[[##]]# Code Examples for Agents

Example code for agents is available on GitHub in the OpenXava repository:
- **Test examples**: https://github.com/openxava/openxava/tree/master/openxavatest/src/test/java/org/openxava/test/tests/bymodule
- **Available actions**: `default-controllers.xml` at https://github.com/openxava/openxava/blob/master/openxava/src/main/resources/xava/default-controllers.xml

#[[##]]# Conventions for Automated Tests (JUnit)

When creating an automated test or JUnit test in this project, refer to the test examples on GitHub (see "Code Examples for Agents" above).

#[[###]]# Test Examples Reference
- **Examples location**: https://github.com/openxava/openxava/tree/master/openxavatest/src/test/java/org/openxava/test/tests/bymodule
- **Available actions**: See `default-controllers.xml` at https://github.com/openxava/openxava/blob/master/openxava/src/main/resources/xava/default-controllers.xml

#[[###]]# Common Patterns to Follow
- We use **JUnit 4**
- Extend `ModuleTestBase` for OpenXava module tests
- **The first line of each test method must be `login("admin", "admin");`**
- Follow the package structure by module as shown in the examples
- **Do NOT try to run tests with `mvn`** — the user will run them from the IDE

#[[###]]# Basic Test Example
```java
package ${package}.${artifactId}.tests;

import org.openxava.tests.*;

public class CustomerTest extends ModuleTestBase {
    
    public CustomerTest(String testName) {
        super(testName, "${artifactId}", "Customer");
    }
    
    public void testCreateCustomer() throws Exception {
        login("admin", "admin");
        // Test logic here
    }
}
```

#[[###]]# Summary for Tests
- ✅ Refer to the test examples on GitHub (openxavatest)
- ✅ Use `ModuleTestBase` for module tests
- ✅ First line of each test: `login("admin", "admin");`
- ✅ Check `default-controllers.xml` for available actions
- ❌ Do NOT run tests with `mvn`

#[[##]]# Conventions for Dashboards

When asked to create a dashboard, you **must review the following documentation first**:
- https://openxava.org/OpenXavaDoc/docs/dashboard_en.html

In addition, follow these rules:
- If there are no existing dashboards, simply name the dashboard class `Dashboard`.
- Create a `dashboards` package (e.g., `${package}.${artifactId}.dashboards`) and place the `Dashboard` class and its auxiliary classes in it.
- Add the CSS rules indicated in the documentation to `custom.css` (usually located in `src/main/webapp/xava/style/custom.css`).
