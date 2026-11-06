# GINA — Backlog

Features and evolutions of GINA. Bugs go to `KNOWN_DEFECTS.md`.

Identifiers are stable and never reused: `GINA-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### GINA-F-1 — A colour selector for the cells of a table  ·  `DONE`

**Problem.** The colour widget (`<Color>`) cannot be a table column. A `CustomColumn` needs a custom component, and the existing `ColorSelector` (flexographicutils) is not one; used as a rendering component it also showed white cells, because the table renderer sets the background of the component, which is how the selector shows its colour.

**Done.** `org.openflexo.gina.swing.utils.FIBColorSelector`: a `ColorSelector` that is a `FIBCustomComponent<Color>` and keeps its colour when the renderer sets colours on it. Use it as `<CustomColumn componentClassName="org.openflexo.gina.swing.utils.FIBColorSelector" customRendering="true" …/>`, or as a `<Custom>` widget.

### GINA-F-2 — Localizers with several parents and a context for the creation of entries  ·  `IN PROGRESS`

**Problem.** A localizer has a single parent, and a key it does not know is created in the localizer which was asked. A component that relies on several dictionaries (a module and the technology adapters it uses, a component and the VirtualModel it is bound to) cannot say where a new entry belongs; in an installation, where dictionaries are read-only, an unknown key still modified the in-memory dictionaries.

**Done.** `LocalizedDelegate.getParents()` (ordered; `getParent()` stays the first), `getContext()` and `localizedForKeyInContext(context, key)`: a key is looked up in the localizer, then in each parent in order; an unknown key is created in the first localizer, searching the ancestors depth-first, whose context is the one asked, else in the localizer asked. `LocalizedDelegateImpl.addParent(...)` and `setContext(...)`. A localizer creates entries only in development (automatic saving of the source dictionaries) or when held in memory; otherwise an unknown key answers its text as English words (`some_key` gives `Some key`) and nothing changes.

**Contexts wired** (openflexo-core, openflexo-ui): `platform` (main dictionary), `flexo`, `ta:<identifier>`, `module:<name>`, `plugin:<controller>`, `vm:<uri>` (dictionary of a VirtualModel, which always accepts new entries since its resource saves them). A localizer opened from a jar no longer tries to save: the constructor reset the parameter instead of the field.

**Left.** Give the GINA, diana and pamela-editor static dictionaries their contexts, and the modules their technology adapters as extra parents; the call sites that must create in another dictionary (`localizedForKeyInContext`); apply the same resolution to `FIBLocalizedDictionary` through a common base class; the hot update of Swing components.

### GINA-F-3 — Swing components follow the language and the edition of the dictionaries  ·  `IN PROGRESS`

**Problem.** Only the views of the inspectors were updated when the language changed. The components a localizer was asked a text for (`localizedForKey(key, component)`, tooltips, borders, table columns) were registered but nothing ever asked the localizer to update them, the registries were read from any thread, and editing a translation updated nothing on screen.

**Done.** One event, `LocalizationListener.languageChanged`, sent when the language changes and, once for a group of edits, on the event dispatch thread when a translation is edited (`FlexoLocalization.fireLocalizationChanged()`); the listeners list is safe to use from several threads, registers a listener once, and a failing listener no longer stops the others. Every `LocalizedDelegateImpl` listens to it and updates the components it knows (on the event dispatch thread, from a copy of its registries). The view of the root component of every FIB built by a `FIBController` listens to it too, and stops when the controller is deleted.

**Migrated.** `LocalizedSwing.bind(component, localizer, key)` and `LocalizedSwing.titledBorder(localizer, key)` register a component built from a key; the menus, labels and buttons of the FIB editor, of the diana editors (drawing, GeomEdit, PowerPoint), of the binding panels of gina, of the print preview, of the project save and auto-save dialogs and of the FML terminal use them, as do the tooltips and the titles of a few dialogs (about 100 sites; frames and dialogs are updated too). Not registered on purpose: a text that is set again later (a label showing a changing value, a text area), which an update would reset, and the 40 calls of the merge panels (GINA-D-10). The other calls of `localizedForKey` build messages and dialogs shown once.

**Left.** A binder that also carries the parameters of a text (`localizedForKeyWithParams`) and removes a component when it leaves the screen; the buttons and labels set with `setText(localizedForKey(...))` (about 20) which may later get another text: check each before registering it.
