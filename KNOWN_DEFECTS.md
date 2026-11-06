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

**Fix.** The test waits for the event dispatch thread (`flushEventQueue()`, now a method of `FIBTestCase` that every widget test can call) after the steps that select a row: the view is built, the model is changed in `test3` and `test5`. The other UI tests of `gina-swing` (77 tests) were run with the event dispatch thread kept busy (40 and 100 ms): none failed, and the same simulation makes the original `FIBListWidgetTest` fail, so this was the only test depending on the timing. With the same one-second block and the fix, the 6 tests pass. The widget keeps its asynchronous selection: a caller on the event dispatch thread that reads the value right after `buildView()` still sees the previous one (existing behaviour, see the commit "Improve selected management in FIBList").

## Browser

### GINA-D-3 — A browser cell sometimes shows no children although its object has some  ·  `DONE`

**Symptom.** In the slide browser of the PowerPoint technology adapter, a text box ("Market drivers", slide 3 of
`CycloCity-Company-Presentation.pptx`) was sometimes shown as a leaf, without the 4 paragraphs that its inspector listed;
in other runs of the application they were shown. Not reproducible on demand.

**Cause (verified).** Two defects of `FIBBrowserModel` / `FIBBrowserElementType` combined:
- `FIBBrowserElementType` evaluated every binding of an element (children, label, icon, visibility…) on itself, after
  storing the object in a field (`setIteratorObject`). The element type is shared by all the cells of its element, and the
  bindings are evaluated from the event dispatch thread and from the threads notifying changes of the model: two
  evaluations at the same time could evaluate one object with the other's (an object without children, here). The
  listener of a single children binding also observed it in the shared element type rather than in the context of its
  cell. Measured: 4 threads evaluating the children and label of two objects, 5139 wrong results of 8000.
- A `BrowserCell` whose children are found empty when it is created is marked loaded and stays a leaf: its parent only
  updates again the cells that are not loaded yet, and a binding listener only fires when the list changes. One wrong
  evaluation at creation was therefore permanent.

**Fix.**
- Each evaluation runs in a context of its own (`FIBBrowserElementType.ObjectContext`, holding the object); the shared
  iterator object is gone, and the listeners of a cell observe in the context of the cell. The cast of the children is
  still evaluated in its `CastFunction`, which gives `child` and delegates the other variables to the context of the
  object (a first version evaluated it in that context, where `child` does not exist: every cast threw a
  `NullReferenceException`, e.g. `child.rootFolder` in the resource browser at start-up).
- When a cell is updated, its loaded children without children look them up again, cheaply: a cell is only updated when
  children appear (`BrowserCell.updateIfChildrenAppeared`), so that leaves do not cause the repaints the loaded state
  was avoiding.

**Test.** `gina-swing` `FIBBrowserLeafCellTest` (UI test): the first two checks fail on the former code (a cell staying
without its new child; 5139 mixed evaluations) and pass after the fix; a third one checks the cast of the children (the
child, and a variable of the component), and fails on the first version of the fix.

### GINA-D-5 — A browser chooses the element of a super type for some objects, depending on the run  ·  `DONE`

**Symptom.** In the slide browser of the PowerPoint technology adapter, the text boxes sometimes showed no paragraphs
(the remaining cause of the symptom of GINA-D-3, still seen after its fix). Inspected in the running application with
a read-only agent: the text boxes (`PowerpointTextBox`) had the element `shape`, without children, instead of
`textShape`, while the auto-shapes had `textShape`.

**Cause (verified).** `FIBBrowser.elementForObject` / `elementForType` used `TypeUtils.objectForClass` (connie) on
the table of the declared types, with caching: the classes met during a lookup were stored in that same table. The
table of the running application held `PowerpointTextShapeImpl -> shape` and `PowerpointSimpleShapeImpl -> shape`. Such
cached implementation classes then compete with the declared interfaces: a class whose super class is cached and whose
interface is declared has two direct matches, and `objectForClass` returns "the first" of a `HashMap` keyed by
classes, whose order changes from one run to the next. Hence `PowerpointTextBox$$Pamela -> shape`.

**Fix.** The browser no longer writes into the table of its declared types. It looks up the most specialized declared
type of the class (assignable from it, and not a super type of another candidate; among unrelated ones, the element
declared first wins), and caches the result in a table of its own, cleared when the elements change.

**Test.** `gina-swing` `BrowserElementLookupTest` (headless): six hierarchies shaped like the PAMELA classes, a picture
looked up before a text box; fails on the former code (twice out of two runs, `shape0` instead of `textShape0`),
passes after the fix.

**Same defect elsewhere.** `TypeUtils.objectForClass` with caching is also used by `InspectorGroup.inspectorForClass`:
an inspector may be chosen the same way. Not fixed here.

### GINA-D-6 — A browser node misses children added while it was being updated  ·  `DONE`

**Symptom.** In the resource browser of Openflexo, the folder `PPT/Business` of the PowerPoint test resource center
showed 6 or 7 of its 8 presentations, depending on the run (PPTX-D-3 of openflexo-pptx). Inspected in the running
application with a read-only agent: the 8 resources were registered everywhere (resource center, its folder, the
repositories of the technology adapter, the resource manager), and the element of the `Business` node gave 8 children,
but the node had 6: the last two registered were missing.

**Cause (verified).** `BrowserCell.update()` dropped a request when an update of the cell was running (`isUpdating`) or
already pending (`updateRequested`). The resources are registered by the thread scanning the resource center, each
registration notifying the folder; the update running or pending on the event dispatch thread may have read the
children before the last registrations, whose notifications were then dropped.

**Fix.** A request arriving while an update is running or pending is recorded (`updateAgain`, recursively or not) and
the cell is updated again once that update ends. The flags are guarded by a lock, as the requests come from any thread.

**Test.** `gina-swing` `FIBBrowserLeafCellTest.test5ChangeNotifiedDuringAnUpdateIsNotLost` (UI test): a node adds and
notifies a child while its children are being read; fails on the former code (2 children instead of 3), passes after.

### GINA-D-7 — A browser update throws `ArrayIndexOutOfBoundsException: node has no children`  ·  `DONE`

**Symptom.** In Openflexo, after opening a free-modelling project and focusing its free model several times in a row,
a browser cell update logged `ArrayIndexOutOfBoundsException: node has no children`, thrown by
`VariableHeightLayoutCache.treeNodesRemoved` from `BrowserCell.updateSync` (`nodesWereRemoved`). Not on every run.

**Cause (verified).** Watched in the running application with a passive agent recording, for every browser, the
notifications, the expansions with their stacks, and the layout cache of the `JTree` before each notification. In the
browser of the free model (hidden root `Example`, single child `Velo`), `JFIBBrowserWidget.ensureRootExpanded` expands
that single child through `FIBWidgetViewImpl.invokeLater(Runnable, long)`, which runs its runnable in a thread of its
own (`InvokeLaterThread`), not on the event dispatch thread. The tree, expanded from that thread, read the children of
`Velo` while the event dispatch thread was updating them: after `updateSync` had removed the `Loading...` placeholder,
before it inserted the real children. The layout cache recorded `Velo` as expanded with no children, and the
notification of the removal of the placeholder then referred to a child it did not hold.

**Fix.** The delayed expansion of `ensureRootExpanded` is sent back to the event dispatch thread
(`SwingUtilities.invokeLater`). As a safety net, when a removal or insertion notification cannot be applied by the tree,
the subtree of the cell is now rebuilt (`nodeStructureChanged`) from its current children, with a warning naming
GINA-D-7, instead of leaving the tree out of sync.

**Tests.** `gina-swing` `FIBBrowserLeafCellTest` (UI tests), both failing on the former code:
- `test7SingleChildOfHiddenRootIsExpandedOnTheEventDispatchThread`: the single child of a hidden root is expanded on the
  event dispatch thread;
- `test6TreeOutOfSyncIsRebuilt`: an expanded node loses its only child, then gets a child the tree is not told about;
  the next update rebuilds the subtree instead of failing.

## Panels

### GINA-D-8 — `FlexoCollabsiblePanelGroup.insertContentsAtIndex` throws on an empty group, and replaces a panel otherwise  ·  `DONE`

**Symptom.** In the FreeModellingEditor, opening the annotation of a PowerPoint slideshow showed neither the browsers of the left
column nor the right column; the error log holds:

    java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
        at java.util.ArrayList.set(ArrayList.java:441)
        at org.openflexo.swing.FlexoCollabsiblePanelGroup.insertContentsAtIndex(FlexoCollabsiblePanelGroup.java:155)
        at org.openflexo.fme.controller.FMEPerspective.getInspectorPanelGroup(FMEPerspective.java:128)

**Cause (verified).** `insertContentsAtIndex(panel, index)` did `panels.set(index, panel)` instead of `panels.add(index, panel)`:
on an empty group it threw; on a group holding panels it took the place of a panel in the list of the group while the
component stayed displayed, so that panel was no longer collapsed when another one was opened.

**Fix.** `panels.add(index, panel)`. Checked by `TestFlexoCollabsiblePanelGroup`.

## Lists

### GINA-D-4 — A list widget sometimes throws a `NullPointerException` while it is painted or selected  ·  `DONE`

**Symptom.** At start-up of Openflexo (2026-11-02), on the event dispatch thread:

    java.lang.NullPointerException: Cannot invoke "java.util.List.get(int)" because "this.list" is null
        at org.openflexo.gina.view.widget.impl.FIBMultipleValueWidgetImpl$FIBMultipleValueModelImpl.getElementAt(FIBMultipleValueWidgetImpl.java:375)
        at javax.swing.plaf.basic.BasicListUI.updateLayoutState(BasicListUI.java:1443)
        ...
        at org.openflexo.gina.swing.view.widget.JFIBListWidget$1.run(JFIBListWidget.java:257)

**Cause (verified).** `FIBMultipleValueModelImpl.update()` cleared its `list` and `array` fields, then computed them again.
It runs on the thread notifying a change of the list binding, while the event dispatch thread reads the model:
`getElementAt` tested `list != null`, then read it again, cleared meanwhile. Measured: one thread updating the model
20000 times while three threads read it, 215610 failed reads (exceptions or null elements).

**Fix.** `update()` computes the contents in local variables and publishes them at once, in `volatile` fields;
`getSize` and `getElementAt` read each field once.

**Test.** `gina-swing` `FIBListModelConcurrencyTest` (UI test): fails on the former code, passes after the fix.


---

### GINA-D-9 — Deleting a controller throws a `NullPointerException` in an iteration  ·  `IN PROGRESS`

**Symptom.** On quit (Free Modelling Editor, `FIBModuleView.deleteModuleView` → `FIBController.delete`), `FIBIterationViewImpl$IteratedContentsImpl.getValue` throws `NullPointerException: … getComponent() is null`.

**Verified.** `FIBController.delete()` deletes every view (which sets their component to null) and only then fires `DELETED`. A `BindingPathChangeListener` still observing the controller re-evaluates its binding, whose context is an iterated element of an iteration view already deleted.

**Done.** `IteratedContentsImpl.getValue` returns null once its iteration view is deleted.

**Left.** Find which listener outlives its view: the bindings of the views built for each iterated element (`subViewsMap`) may not be stopped by `FIBIterationViewImpl.delete()`. Alternatively fire `DELETED` before deleting the views.

## Localization

### GINA-D-10 — The merge panels display raw keys  ·  `DEFERRED`

**Symptom.** The merge UI of `flexographicutils` (`org.openflexo.swing.merge`: `MergePanelElements`, `DetailedMergeAnalysisPanel`, `HandConflictResolver`) shows keys such as `choose_left`, `merged_result` or `please_resolve_conflict` instead of text.

**Verified.** `MergePanelElements.localizedForKey` returns its argument and nothing overrides it; the 25 keys it uses are in no dictionary. `MergeEditor` and `DefaultMergePanel` have no caller in the workspace, so the code may be dead.

**Decision.** Left as is (2026-11-06). Either delete the package, or route `localizedForKey` to a localizer and add the keys to a dictionary.
