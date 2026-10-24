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

**Same defect elsewhere.** `connie-core` carries a copy of `ClassScope` and `LoadedClassesInfo` (`org.openflexo.connie.type`), used by `TypeMustBeResolved`; fixed there as `CONNIE-D-2`. Removing the duplication is `CONNIE-F-1`. A test copy in `gina-swing-editor-test` still calls `ClassScope.getLoadedClasses` (empty on Java 9+).

### GINA-D-2 — `FIBListWidgetTest.test2InstanciateComponent` fails when the event queue is slow  ·  `DONE`

**Symptom.** On Jenkins, `gina-2.4-ui-test` #29 to #32 failed on one assertion: `expected:<Robert Smith aged 39 (Male)> but was:<null>` (`FIBListWidgetTest`, the line reading the represented value of `list5`). The same class passed locally, and #33 passed on Jenkins without any change of the test.

**Cause.** `JFIBListWidget.performSelect` selects the row with `SwingUtilities.invokeLater`, so the represented value is only set once the event dispatch thread has run that task. The test reads it from the JUnit thread right after `buildView()`: it passes when the event queue is fast and fails when it is not. Verified by blocking the event dispatch thread for one second before `buildView()`: the test then failed (and `test6PerfomSomeTestsWithSelection` with it).

**Fix.** The test waits for the event dispatch thread (`SwingUtilities.invokeAndWait`) after the steps that select a row: the view is built, the model is changed in `test3` and `test5`. With the same one-second block and the fix, the 6 tests pass. The widget keeps its asynchronous selection: a caller on the event dispatch thread that reads the value right after `buildView()` still sees the previous one (existing behaviour, see the commit "Improve selected management in FIBList").
