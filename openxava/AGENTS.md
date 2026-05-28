# Instructions for AI Agents

This document contains guidelines for AI agents working on the OpenXava codebase.

## Version Annotations

All new public or protected methods must include a `@since` Javadoc tag with the current version:

```java
/**
 * @since 8.0
 */
public void myNewMethod() {
    // ...
}
```

The current version is **8.0**.
