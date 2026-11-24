# gina — Known defects

Bugs of GINA that are known. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `GINA-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

Fixed defects are moved to [`FIXED_DEFECTS.md`](./FIXED_DEFECTS.md) once their status is `DONE`.

---

## Localization

### GINA-D-10 — The merge panels display raw keys  ·  `DEFERRED`

**GitHub:** [openflexo-team/gina#7](https://github.com/openflexo-team/gina/issues/7)

**Symptom.** The merge UI of `flexographicutils` (`org.openflexo.swing.merge`: `MergePanelElements`, `DetailedMergeAnalysisPanel`, `HandConflictResolver`) shows keys such as `choose_left`, `merged_result` or `please_resolve_conflict` instead of text.

**Verified.** `MergePanelElements.localizedForKey` returns its argument and nothing overrides it; the 25 keys it uses are in no dictionary. `MergeEditor` and `DefaultMergePanel` have no caller in the workspace, so the code may be dead.

**Decision.** Left as is (2026-11-06). Either delete the package, or route `localizedForKey` to a localizer and add the keys to a dictionary.
