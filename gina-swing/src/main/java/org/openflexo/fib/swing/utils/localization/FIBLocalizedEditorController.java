/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.fib.swing.utils.localization;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegate.LocalizedEntry;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.localization.LocalizedDelegateImpl.SearchMode;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.StringUtils;

/**
 * Provides a default implementation for a localized delegate enriched with the possibility to connect it with a SWING graphical editor
 * allowing search features
 * 
 * @author sylvain
 * 
 */
public class FIBLocalizedEditorController extends FIBController {

	private static final Logger LOGGER = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	private SearchMode searchMode = SearchMode.Contains;
	private String searchedString;
	private List<LocalizedEntry> matchingEntries;
	private List<LocalizedEntry> issuesEntries;

	public final Language ENGLISH = Language.ENGLISH;
	public final Language FRENCH = Language.FRENCH;
	public final Language DUTCH = Language.DUTCH;

	private LocalizedEntry selectedEntry;

	public FIBLocalizedEditorController(FIBComponent rootComponent) {
		super(rootComponent);
	}

	@Override
	public LocalizedDelegate getDataObject() {
		return (LocalizedDelegate) super.getDataObject();
	}

	public Icon getIconForEntry(LocalizedEntry entry) {
		if (entry.hasInvalidValue()) {
			return UtilsIconLibrary.WARNING_ICON;
		}
		return null;
	}

	public Color getColorForFrenchValue(LocalizedEntry entry) {
		if (!entry.isFrenchValueValid()) {
			return Color.ORANGE;
		}
		// No specific color in this case
		return null;
	}

	public Color getColorForEnglishValue(LocalizedEntry entry) {
		if (!entry.isEnglishValueValid()) {
			return Color.ORANGE;
		}
		// No specific color in this case
		return null;
	}

	public Color getColorForDutchValue(LocalizedEntry entry) {
		if (!entry.isDutchValueValid()) {
			return Color.ORANGE;
		}
		// No specific color in this case
		return null;
	}

	public Image getFrenchIconImage() {
		return UtilsIconLibrary.FR_FLAG.getImage();
	}

	public Image getEnglishIconImage() {
		return UtilsIconLibrary.UK_FLAG.getImage();
	}

	public Image getDutchIconImage() {
		return UtilsIconLibrary.NE_FLAG.getImage();
	}

	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
		searchMatchingEntries();
	}

	public String getSearchedString() {
		return searchedString;
	}

	public void setSearchedString(String searchedString) {
		this.searchedString = searchedString;
		searchMatchingEntries();
	}

	public void searchMatchingEntries() {
		computeMatchingEntries(getSearchedString(), getSearchMode());
	}

	public List<LocalizedEntry> getMatchingEntries() {
		if (matchingEntries == null) {
			matchingEntries = new ArrayList<LocalizedEntry>();
		}
		return matchingEntries;
	}

	protected List<LocalizedEntry> computeMatchingEntries(String text, SearchMode searchMode) {
		// getDataObject().loadLocalizedDictionaries();
		matchingEntries = new ArrayList<LocalizedEntry>();
		if (StringUtils.isNotEmpty(text)) {
			for (LocalizedEntry e : getDataObject().getEntries()) {
				switch (searchMode) {
				case Contains:
					if (e.getKey().contains(text)) {
						if (!matchingEntries.contains(e)) {
							matchingEntries.add(e);
						}
					}
					for (Language l : Language.getAvailableLanguages()) {
						if (getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l) != null
								&& getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l).contains(text)) {
							if (!matchingEntries.contains(e)) {
								matchingEntries.add(e);
							}
						}
					}
					break;
				case BeginsWith:
					if (e.getKey().startsWith(text)) {
						matchingEntries.add(e);
					}
					for (Language l : Language.getAvailableLanguages()) {
						if (getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l) != null
								&& getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l).startsWith(text)) {
							if (!matchingEntries.contains(e)) {
								matchingEntries.add(e);
							}
						}
					}
					break;
				case EndsWith:
					if (e.getKey().endsWith(text)) {
						matchingEntries.add(e);
					}
					for (Language l : Language.getAvailableLanguages()) {
						if (getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l) != null
								&& getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l).endsWith(text)) {
							if (!matchingEntries.contains(e)) {
								matchingEntries.add(e);
							}
						}
					}
					break;

				default:
					break;
				}
			}
		}
		getPropertyChangeSupport().firePropertyChange("matchingEntries", null, matchingEntries);
		return matchingEntries;
	}

	public List<LocalizedEntry> getIssuesEntries() {
		if (issuesEntries == null) {
			issuesEntries = computeIssuesEntries();
		}
		return issuesEntries;
	}

	private List<LocalizedEntry> computeIssuesEntries() {
		// getDataObject().loadLocalizedDictionaries();
		issuesEntries = new ArrayList<LocalizedEntry>();
		if (getDataObject() != null) {
			for (LocalizedEntry e : getDataObject().getEntries()) {
				if (e.hasInvalidValue()) {
					issuesEntries.add(e);
				}
			}
		}
		getPropertyChangeSupport().firePropertyChange("issuesEntries", null, issuesEntries);
		return issuesEntries;
	}

	private LocalizedEditor parentLocalizedEditor = null;

	public void showParentLocalizedEditor() {
		if (parentLocalizedEditor == null && getDataObject().getParent() instanceof LocalizedDelegateImpl) {
			LocalizedDelegateImpl parent = (LocalizedDelegateImpl) getDataObject().getParent();
			Resource sourceCodeDirectoryResource = ResourceLocator.locateSourceCodeResource(parent.getLocalizedDirectoryResource());
			LocalizedDelegate sourceLocalized = FlexoLocalization.getLocalizedDelegate(sourceCodeDirectoryResource, parent.getParent(),
					true, true);
			parentLocalizedEditor = new LocalizedEditor(null, "localized_editor", sourceLocalized, getLocalizer(), true, false);
		}
		parentLocalizedEditor.setVisible(true);
	}

	public void apply() {
		LOGGER.info("Applying localized to GUI");
		FlexoLocalization.updateGUILocalized();
	}

	public void save() {
		if (getDataObject() instanceof LocalizedDelegateImpl) {
			((LocalizedDelegateImpl) getDataObject()).saveAllDictionaries();
		}
	}

	public void refresh() {
		if (getDataObject() instanceof LocalizedDelegateImpl) {
			((LocalizedDelegateImpl) getDataObject()).refresh();
		}
		computeMatchingEntries(getSearchedString(), getSearchMode());
		computeIssuesEntries();
	}

	public void translateAll() {
		for (LocalizedEntry entry : getDataObject().getEntries()) {
			getDataObject().searchTranslation(entry);
		}
	}

	public void searchTranslation(LocalizedEntry entry) {
		getDataObject().searchTranslation(entry);
	}

	public LocalizedEntry getSelectedEntry() {
		return selectedEntry;
	}

	public void setSelectedEntry(LocalizedEntry selectedEntry) {
		if (this.selectedEntry != selectedEntry) {
			LocalizedEntry oldValue = this.selectedEntry;
			this.selectedEntry = selectedEntry;
			getPropertyChangeSupport().firePropertyChange("selectedEntry", oldValue, selectedEntry);
		}
	}

	public LocalizedEntry addEntry(String key) {
		if (getDataObject() instanceof LocalizedDelegateImpl) {
			LocalizedEntry returned = ((LocalizedDelegateImpl) getDataObject()).addEntry(key);
			// refresh();
			return returned;
		}
		return null;
	}

	public LocalizedEntry addEntry() {
		if (getDataObject() instanceof LocalizedDelegateImpl) {
			LocalizedEntry returned = ((LocalizedDelegateImpl) getDataObject()).addEntry();
			// refresh();
			getPropertyChangeSupport().firePropertyChange("matchingEntries", null, getMatchingEntries());
			getPropertyChangeSupport().firePropertyChange("issuesEntries", null, getIssuesEntries());
			return returned;
		}
		return null;
	}

	public void removeEntry(LocalizedEntry entry) {
		entry.delete();
		getMatchingEntries().remove(entry);
		getIssuesEntries().remove(entry);
		getPropertyChangeSupport().firePropertyChange("matchingEntries", null, getMatchingEntries());
		getPropertyChangeSupport().firePropertyChange("issuesEntries", null, getIssuesEntries());
		// refresh();
	}

	public boolean displaySaveButton() {
		return false;
	}

	public boolean displaySearchLocalizedButton() {
		return true;
	}

	public void searchLocalized() {
		getDataObject().searchLocalized();
	}

	public void removeUnecessaryLocalized() {
		if (getDataObject().getParent() != null) {
			for (LocalizedEntry e : new ArrayList<LocalizedEntry>(getDataObject().getEntries())) {
				boolean overrideWithDifferentValues = false;
				for (Language l : Language.getAvailableLanguages()) {
					String localValue = getDataObject().getLocalizedForKeyAndLanguage(e.getKey(), l, false);
					String parentValue = getDataObject().getParent().getLocalizedForKeyAndLanguage(e.getKey(), l, false);
					if (parentValue == null || !parentValue.equals(localValue)) {
						overrideWithDifferentValues = true;
					}
				}
				if (!overrideWithDifferentValues) {
					removeEntry(e);
				}
			}
		}
	}

}
