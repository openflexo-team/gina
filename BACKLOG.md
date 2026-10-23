# GINA — Backlog

Features and evolutions of GINA. Bugs go to `KNOWN_DEFECTS.md`.

Identifiers are stable and never reused: `GINA-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### GINA-F-1 — A colour selector for the cells of a table  ·  `DONE`

**Problem.** The colour widget (`<Color>`) cannot be a table column. A `CustomColumn` needs a custom component, and the existing `ColorSelector` (flexographicutils) is not one; used as a rendering component it also showed white cells, because the table renderer sets the background of the component, which is how the selector shows its colour.

**Done.** `org.openflexo.gina.swing.utils.FIBColorSelector`: a `ColorSelector` that is a `FIBCustomComponent<Color>` and keeps its colour when the renderer sets colours on it. Use it as `<CustomColumn componentClassName="org.openflexo.gina.swing.utils.FIBColorSelector" customRendering="true" …/>`, or as a `<Custom>` widget.
