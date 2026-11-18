# gina — Known defects

Bugs of GINA that are known. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `GINA-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

Fixed defects are moved to [`FIXED_DEFECTS.md`](./FIXED_DEFECTS.md) once their status is `DONE`.

---

## Lists

### GINA-D-9 — Deleting a controller throws a `NullPointerException` in an iteration  ·  `IN PROGRESS`

**GitHub:** [openflexo-team/gina#6](https://github.com/openflexo-team/gina/issues/6)

**Symptom.** On quit (Free Modelling Editor, `FIBModuleView.deleteModuleView` → `FIBController.delete`), `FIBIterationViewImpl$IteratedContentsImpl.getValue` throws `NullPointerException: … getComponent() is null`.

**Verified.** `FIBController.delete()` deletes every view (which sets their component to null) and only then fires `DELETED`. A `BindingPathChangeListener` still observing the controller re-evaluates its binding, whose context is an iterated element of an iteration view already deleted.

**Done.** `IteratedContentsImpl.getValue` returns null once its iteration view is deleted.

**Left.** Find which listener outlives its view: the bindings of the views built for each iterated element (`subViewsMap`) may not be stopped by `FIBIterationViewImpl.delete()`. Alternatively fire `DELETED` before deleting the views.

---

## Localization

### GINA-D-10 — The merge panels display raw keys  ·  `DEFERRED`

**GitHub:** [openflexo-team/gina#7](https://github.com/openflexo-team/gina/issues/7)

**Symptom.** The merge UI of `flexographicutils` (`org.openflexo.swing.merge`: `MergePanelElements`, `DetailedMergeAnalysisPanel`, `HandConflictResolver`) shows keys such as `choose_left`, `merged_result` or `please_resolve_conflict` instead of text.

**Verified.** `MergePanelElements.localizedForKey` returns its argument and nothing overrides it; the 25 keys it uses are in no dictionary. `MergeEditor` and `DefaultMergePanel` have no caller in the workspace, so the code may be dead.

**Decision.** Left as is (2026-11-06). Either delete the package, or route `localizedForKey` to a localizer and add the keys to a dictionary.
