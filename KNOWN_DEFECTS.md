# gina — Known defects

Bugs of GINA that are known. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `GINA-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## Class and type selection

### GINA-D-1 — `ClassScope` cannot list classes on Java 9+: the type field of the inspector throws  ·  `DONE`

**Symptom (Java 17).** Selecting an object in a browser makes the inspector build a `TypeSelector`, which opens a `ClassEditor`; the exception is thrown on the Swing thread:

    java.lang.RuntimeException: ClassScope::getLoadedClasses() cannot be used in this JRE
        at org.openflexo.gina.swing.utils.ClassScope.getLoadedClasses(ClassScope.java:117)
        at org.openflexo.gina.swing.utils.LoadedClassesInfo.init(LoadedClassesInfo.java:171)
        at org.openflexo.gina.swing.utils.LoadedClassesInfo.<init>(LoadedClassesInfo.java:110)
        at org.openflexo.gina.swing.utils.ClassEditor.<init>(ClassEditor.java:79)
        at org.openflexo.gina.swing.utils.TypeSelector.getClassEditor(TypeSelector.java:681)
    Caused by: java.lang.NoSuchFieldException
        at java.lang.Class.getDeclaredField(Class.java:2610)
        at org.openflexo.gina.swing.utils.ClassScope.<clinit>(ClassScope.java:75)

**Cause (verified).** `ClassScope` read the private field `ClassLoader.classes` by reflection. That field no longer exists in the JDK (`--add-opens` cannot help); `<clinit>` swallowed the failure and every call then threw. The list only feeds `LoadedClassesInfo.search(partialName)` (substring search of a class by name, last resort of `ClassEditor`); the package tree and the selection do not depend on it.

**Fix.** The list is now the classes *available* (not only the loaded ones): `ClassScope.getAvailableClassNames(loaders)` scans `java.class.path`, the `URLClassLoader` URLs and the `java.*` / `javax.*` classes of the runtime image (`jrt:/`), names only. `LoadedClassesInfo` scans lazily at the first `search(...)` and loads (without initializing) only the matches. An unreadable entry is skipped; `getLoadedClasses` returns an empty array instead of throwing when the field is absent. Consequence for the user: the search also finds classes not loaded yet.

**Test.** `gina-swing` `ClassScopeTest` (headless-safe): 4 failures before the fix, 5 successes after.

**Same defect elsewhere (not fixed here).** `connie-core` carries an identical copy of `ClassScope` and `LoadedClassesInfo` (`org.openflexo.connie.type`), used by `TypeMustBeResolved` in `flexo-foundation` (the fix proposal for an unresolved type), and a test copy in `gina-swing-editor-test`. The connie copy throws the same way on Java 9+.
