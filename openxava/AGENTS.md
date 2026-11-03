# Instructions for AI Agents

This document contains guidelines for AI agents working on the OpenXava codebase.

## Version Annotations

All new public or protected methods must include a `@since` Javadoc tag with the current version:

```java
/**
 * @since 8.0.2
 */
public void myNewMethod() {
    // ...
}
```

The current version is **8.0.2**.

## Changelog Entries

When asked to add a changelog entry:
- Edit `openxava/changelog.txt` (relative to the repository root), not `openxava-doc/web/changelog.txt`.
- Add the entry at the beginning of the list, immediately below the changelog title.
- For bug fixes, start the entry with `- Fix:`.
- Write in English and use the present tense.
- Keep each line within 108 columns.
