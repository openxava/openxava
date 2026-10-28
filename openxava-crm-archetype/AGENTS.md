# AGENTS.md

#[[##]]# Jakarta EE 11

This project is based on **OpenXava 8**, which runs on **Jakarta EE 11**. Your training data may
contain older OpenXava code using `javax.*` packages — that code still works, but **you must
always use `jakarta.*` imports** instead. For example:
- `jakarta.persistence.*` (JPA 3.2), never `javax.persistence.*`
- `jakarta.validation.*` (Bean Validation 3.1), never `javax.validation.*`
- `jakarta.servlet.*`, `jakarta.inject.*`, `jakarta.annotation.*`, `jakarta.ws.rs.*`,
  `jakarta.json.bind.*`, etc.

#[[##]]# Verification

After creating or modifying **any** Java file, **run `mvn -q compile`** to verify the
project compiles before considering the task done. If compilation fails, fix the errors
before finishing.

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
- For example, write `import java.math.*;` and then `BigDecimal price;`, never
  `java.math.BigDecimal price;`

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

#[[###]]# Identifier Properties (@Id)

When asked for an `id`, `oid`, or any property that is clearly the entity identifier,
**do not invent the format**. Unless the user explicitly says otherwise, look at the
existing entities in the project and **copy the
exact `@Id` definition used there**: same type, same generation strategy, same annotations,
same `@Column` length. Also the extra annotations, like `@Hidden`, `@GeneratedValue`, etc.


#[[###]]# Annotations Instead of Stereotypes

`@Stereotype` is legacy. In new code **always use the equivalent annotation** (available since
OpenXava 6.6).
Reference: https://openxava.org/OpenXavaDoc/docs/model_en.html#Model-Properties-Stereotype

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

Only use `@Stereotype` when there is no equivalent annotation
(e.g. `BOLD_LABEL`, `IMAGE_LABEL`).

#[[###]]# Column Length for Numeric Properties (int, long, etc.)

Every numeric primitive property (`int`, `long`, etc.) **must have `@Column(length=N)`**, even
when the database does not need it. OpenXava uses the length to size the input field when
drawing it.

- Choose `N` according to the property name and its typical number of digits.

#[[###]]# References (@ManyToOne / @OneToOne)

When adding a reference (association to a single entity), **always use lazy fetching** by
specifying `fetch=FetchType.LAZY` in `@ManyToOne` and `@OneToOne`.

**Infer whether the reference is required from the business logic**, just as you would
decide whether a scalar property needs `@Required`. Do not wait for the user to explicitly
say "required": if the entity would not make sense without the referenced entity, **add
`optional=false` to `@ManyToOne` or `@OneToOne`**, keeping `fetch=FetchType.LAZY`.
For references, express requiredness **only with `optional=false`**. **Do NOT add
`@Required` to a `@ManyToOne` or `@OneToOne` reference**, even when it is mandatory:
it is redundant with `optional=false`. Reserve `@Required` for scalar properties.

Do not make every reference mandatory: leave it optional when its absence is valid in the
domain, and respect any explicit user requirement about optionality. If the domain and
existing model do not provide enough context to decide, ask for clarification.

Examples of references that the domain allows to be optional:

```java
@ManyToOne(fetch=FetchType.LAZY)
Category category;
```

```java
@OneToOne(fetch=FetchType.LAZY)
Address address;
```

#[[###]]# Default Values (@DefaultValueCalculator)

When a property needs a default value, **always use `@DefaultValueCalculator`** — never assign
the value in Java with a field initializer (e.g. `int year = LocalDate.now().getYear();` is
WRONG).

- Reference: https://openxava.org/OpenXavaDoc/apidocs/org/openxava/annotations/DefaultValueCalculator.html
- Built-in calculators: https://openxava.org/OpenXavaDoc/apidocs/org/openxava/calculators/package-summary.html
- Calculator classes are in `org.openxava.calculators` — import them.

Common built-in calculators:

| Need | Calculator |
|---|---|
| Current year | `CurrentYearCalculator.class` |
| Current month | `CurrentMonthCalculator.class` |
| Current date (`LocalDate`) | `CurrentLocalDateCalculator.class` |
| Current date (`java.util.Date`) | `CurrentDateCalculator.class` |
| Current timestamp (`java.sql.Timestamp`) | `CurrentTimestampCalculator.class` |
| Current user name | `CurrentUserCalculator.class` (plus `CurrentUserNickNameCalculator`, `CurrentUserEmailCalculator`, etc.) |
| `true` / `false` | `TrueCalculator` / `FalseCalculator` |


If no built-in calculator fits, create a custom one implementing
`org.openxava.calculators.ICalculator`.

#[[####]]# Injecting View Values into a Calculator (@PropertyValue)

A custom calculator **can receive the current values of other properties shown in the
view** before `calculate()` runs. Do this with the `properties` attribute of
`@DefaultValueCalculator` and `org.openxava.annotations.PropertyValue`. For each
`@PropertyValue(name="prop")`, OpenXava calls `setProp(currentViewValue)` on the
calculator — so the calculator must declare a settable field named `prop` (use Lombok
`@Getter @Setter`).

This is the correct way to make a default value depend on other fields of the form.
**Do NOT** hardcode `LocalDate.now()` or any other global state inside the calculator
when the needed value already exists as a property in the entity.

Example adapted from the OpenXava reference guide (`DetailsCountCalculator`), which
receives `year` and `number` injected from the view and returns the number of details
of that invoice — here rewritten with JPA:

```java
package ${package}.${artifactId}.calculators;

import org.openxava.calculators.*;
import org.openxava.jpa.*;
import lombok.*;

public class DetailsCountCalculator implements ICalculator {

    @Getter @Setter int year;   // Injected from the view before calculate() runs
    @Getter @Setter int number; // Each field needs a public setter for injection

    public Object calculate() throws Exception {
        Long count = XPersistence.getManager().createQuery(
            "select count(d) from Invoice i join i.details d " +
            "where i.year = :year and i.number = :number", Long.class)
            .setParameter("year", year)
            .setParameter("number", number)
            .getSingleResult();
        return count.intValue();
    }
}
```

And in the entity:

```java
@DefaultValueCalculator(value=DetailsCountCalculator.class,
    properties={
        @PropertyValue(name="year"),   // Injects the value of 'year' into setYear()
        @PropertyValue(name="number")  // Injects the value of 'number' into setNumber()
    })
int detailsCount;
```

`@PropertyValue` also supports `from="reference.prop"` to inject from a reference
(e.g. `from="customer.number"`) and `value="FIXED"` to inject a constant.
Reference: https://openxava.org/OpenXavaDoc/docs/model_en.html#Model-Properties-Default%20value%20calculator

#[[###]]# Master-Detail (Collections)

When asked to create a master-detail structure, the detail collection in the master entity
**must use `@ElementCollection`** instead of `@OneToMany`. The detail class must be annotated
with `@Embeddable` instead of `@Entity`.

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
    @Hidden
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(length=36)
    String oid;

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

    @Column(length=5)
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
- Infer reference optionality from business logic; add `optional=false` in `@ManyToOne`
  and `@OneToOne` when the reference is required, even if the user did not say so explicitly
- Do not add `@Required` to references; use only `optional=false` for required references
- ✅ Use `import` statements instead of fully qualified class names
- ✅ Add `@Column(length=N)` to every numeric primitive (`int`, `long`...) with a length that
  fits the property name, even if the database does not need it
- ✅ Copy the `@Id` definition from the existing entities in the project; do not invent a
  different format unless explicitly asked
- ❌ Do not use `private` on fields
- ❌ Do not write getters/setters manually
- ❌ Do not use `@OneToMany` / `@Entity` for detail collections
- ❌ Do not use `@ManyToOne` / `@OneToOne` without `fetch=FetchType.LAZY`
- ❌ Do not use `@Stereotype` when an equivalent annotation exists (`@Money`, `@TextArea`,
  `@Files`, `@File`, etc.)
- ✅ Use `@DefaultValueCalculator` (e.g. `CurrentYearCalculator`, `CurrentLocalDateCalculator`)
  for default values
- ❌ Do not assign default values with Java field initializers
  (e.g. `int year = LocalDate.now().getYear();`)
- ✅ When a calculator needs values from other properties of the entity, inject them
  with `@PropertyValue` in the `properties` attribute of `@DefaultValueCalculator`
  (do not hardcode `LocalDate.now()` or global state inside the calculator)
- ❌ Do not use `NextIntegerCalculator` / `NextLongCalculator` for sequential numbers
  (duplicates in multiuser)

#[[##]]# Conventions for Actions

When asked to add an action to an entity in this project, follow these rules:

- Define the action in `src/main/resources/xava/controllers.xml` inside a `<controller>` whose `name` matches the entity name
- The controller **must extend** the `Typical` controller using `<extends controller="Typical"/>` so the standard actions (new, save, delete, etc.) remain available
- Create the action class in the `${package}.${artifactId}.actions` package, typically extending `ViewBaseAction`
- **Do NOT modify the entity class** to register the action.

```xml
<controller name="Lead">
    <extends controller="Typical"/>
    <action name="showEmail" mode="detail"
        class="${package}.${artifactId}.actions.ShowEmailAction"/>
</controller>
```

```java
package ${package}.${artifactId}.actions;

import org.openxava.actions.*;
import org.openxava.validators.*;
import ${package}.${artifactId}.model.*;

public class ShowEmailAction extends ViewBaseAction {

    public void execute() throws Exception {
        String email = (String) getView().getValue("email");
        if (email == null) {
            addError("email_not_set"); // Add email_not_set to i18n messages file
        } else {
            addMessage("email_message", email); // Add email_message to i18n messages file
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

