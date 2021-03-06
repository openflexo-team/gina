/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.gina.model;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.openflexo.gina.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(FIBLocalizedDictionary.FIBLocalizedDictionaryImpl.class)
@XMLElement(xmlTag = "LocalizedDictionary")
public interface FIBLocalizedDictionary extends FIBModelObject, LocalizedDelegate, LocalizationEntryRetriever {

	@PropertyIdentifier(type = FIBComponent.class)
	public static final String OWNER_KEY = "owner";

	@PropertyIdentifier(type = List.class)
	public static final String LOCALIZED_ENTRIES_KEY = "localizedEntries";

	public static final String ENTRIES_KEY = "entries";

	@Getter(value = OWNER_KEY, inverse = FIBComponent.LOCALIZED_DICTIONARY_KEY)
	public FIBComponent getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBComponent owner);

	@Getter(value = LOCALIZED_ENTRIES_KEY, cardinality = Cardinality.LIST, inverse = FIBLocalizedEntry.DICTIONARY_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@XMLElement
	@Embedded
	public List<FIBLocalizedEntry> getLocalizedEntries();

	@Setter(LOCALIZED_ENTRIES_KEY)
	public void setLocalizedEntries(List<FIBLocalizedEntry> entries);

	@Adder(LOCALIZED_ENTRIES_KEY)
	public void addToLocalizedEntries(FIBLocalizedEntry aEntrie);

	@Remover(LOCALIZED_ENTRIES_KEY)
	public void removeFromLocalizedEntries(FIBLocalizedEntry aEntrie);

	public void append(FIBLocalizedDictionary aDict);

	public void setParent(LocalizedDelegate parentLocalizedDelegate);

	// public void beginSearchNewLocalizationEntries();

	// public void endSearchNewLocalizationEntries();

	public void refresh();

	public static abstract class FIBLocalizedDictionaryImpl extends FIBModelObjectImpl implements FIBLocalizedDictionary {

		private static final Logger logger = Logger.getLogger(FIBLocalizedDictionary.class.getPackage().getName());

		private final List<FIBLocalizedEntry> entries;
		private final Map<Language, Hashtable<String, String>> values;
		private List<DynamicEntry> dynamicEntries = null;

		private final WeakHashMap<Component, String> _storedLocalizedForComponents = new WeakHashMap<>();
		private final WeakHashMap<JComponent, String> _storedLocalizedForComponentTooltips = new WeakHashMap<>();
		private final WeakHashMap<TitledBorder, String> _storedLocalizedForBorders = new WeakHashMap<>();
		private final WeakHashMap<TableColumn, String> _storedLocalizedForTableColumn = new WeakHashMap<>();

		// private final boolean isSearchingNewEntries = false;

		public FIBLocalizedDictionaryImpl() {
			entries = new ArrayList<>();
			values = new HashMap<>();
			setParent(FlexoLocalization.getMainLocalizer());
		}

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		/*@Override
		public List<FIBLocalizedEntry> getLocalizedEntries() {
			return entries;
		}
		
		public void setLocalizedEntries(Vector<FIBLocalizedEntry> someEntries) {
			entries = someEntries;
		}*/

		@Override
		public void addToLocalizedEntries(FIBLocalizedEntry entry) {

			performSuperAdder(LOCALIZED_ENTRIES_KEY, entry);

			// entry.setLocalizedDictionary(this);
			// entries.add(entry);
			// logger.info("Add entry key:"+entry.getKey()+" lang="+entry.getLanguage()+" value:"+entry.getValue());
			Language lang = Language.retrieveLanguage(entry.getLanguage());
			if (lang == null) {
				logger.warning("Undefined language: " + entry.getLanguage());
				return;
			}
			if (entry.getValue() != null && entry.getKey() != null) {
				getDictForLang(lang).put(entry.getKey(), entry.getValue());
			}

		}

		/*@Override
		public void removeFromLocalizedEntries(FIBLocalizedEntry entry) {
			entry.setLocalizedDictionary(null);
			entries.remove(entry);
		}*/

		@Override
		public void append(FIBLocalizedDictionary aDict) {
			if (aDict == null) {
				return;
			}
			for (FIBLocalizedEntry entry : aDict.getLocalizedEntries()) {
				addToLocalizedEntries(entry);
			}
		}

		private FIBLocalizedEntry getEntry(Language language, String key) {
			for (FIBLocalizedEntry entry : getLocalizedEntries()) {
				if (Language.retrieveLanguage(entry.getLanguage()) == language && key.equals(entry.getKey())) {
					return entry;
				}
			}
			return null;
		}

		private Hashtable<String, String> getDictForLang(Language lang) {
			Hashtable<String, String> dict = values.get(lang);
			if (dict == null) {
				dict = new Hashtable<>();
				values.put(lang, dict);
			}
			return dict;
		}

		/**
		 * Return String matching specified key and language set as default language<br>
		 * 
		 * This is general and main method to use localized in Flexo.<br>
		 * Applicable language is chosen from the one defined in FlexoLocalization (configurable from GeneralPreferences).<br>
		 * Use english names for keys, such as 'some_english_words'<br>
		 * 
		 * Usage example: <code>localizedForKey("some_english_words")</code>
		 * 
		 * @param key
		 * @return String matching specified key and language defined as default in {@link FlexoLocalization}
		 */
		@Override
		public String localizedForKey(String key) {
			return localizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage());
		}

		/**
		 * Return String matching specified key and language<br>
		 * 
		 * @param key
		 * @param language
		 * @return
		 */
		@Override
		public String localizedForKeyAndLanguage(String key, Language language) {
			return localizedForKeyAndLanguage(key, language, handleNewEntry(key, language));
		}

		/**
		 * Return String matching specified key and language<br>
		 * If #createsNewEntryInFirstEditableParent set to true, will try to enter a new traduction.<br>
		 * LocalizedDelegate are recursively requested to their parents, and the first one who respond true to
		 * {@link #handleNewEntry(String, Language)} will add a new entry
		 * 
		 * @param key
		 * @param language
		 * @return
		 */
		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public String localizedForKeyAndLanguage(String key, Language language, boolean createsNewEntryInFirstEditableParent) {

			if (key == null || StringUtils.isEmpty(key)) {
				return null;
			}

			String localized = getDictForLang(language).get(key);

			if (localized == null) {
				// Not found in this localizer what about parent ?
				if (getParent() != null) {
					if (getParent().hasKey(key, language, true)) {
						// This is defined in parent localizer
						// Nice, we forward the request to the parent
						return getParent().localizedForKeyAndLanguage(key, language, false);
					}
					else if (createsNewEntryInFirstEditableParent && handleNewEntry(key, language)) {
						addEntry(key);
						return getDictForLang(language).get(key);
					}
					else {
						return getParent().localizedForKeyAndLanguage(key, language, true);
					}
				}
				else {
					// parent is null
					if (handleNewEntry(key, language)) {
						addEntry(key);
						return getDictForLang(language).get(key);
					}
					return key;
				}
			}

			return localized;
		}

		/**
		 * Return boolean indicating if this delegate defines a translation for supplied key and language
		 * 
		 * @return
		 */
		@Override
		public boolean hasKey(String key, Language language, boolean recursive) {
			String localized = getDictForLang(language).get(key);
			if (localized != null) {
				return true;
			}
			if (recursive && getParent() != null) {
				return getParent().hasKey(key, language, recursive);
			}
			return false;
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public String localizedForKeyWithParams(String key, Object... object) {
			String base = localizedForKey(key);
			return FlexoLocalization.replaceAllParamsInString(base, object);
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public String localizedForKey(String key, Component component) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
			}
			_storedLocalizedForComponents.put(component, key);
			return localizedForKey(key);
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public String localizedTooltipForKey(String key, JComponent component) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
			}
			_storedLocalizedForComponentTooltips.put(component, key);
			return localizedForKey(key);
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public String localizedForKey(String key, TitledBorder border) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("localizedForKey called with " + key + " for border " + border.getClass().getName());
			}
			_storedLocalizedForBorders.put(border, key);
			return localizedForKey(key);
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public String localizedForKey(String key, TableColumn column) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("localizedForKey called with " + key + " for border " + column.getClass().getName());
			}
			_storedLocalizedForTableColumn.put(column, key);
			return localizedForKey(key);
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public void clearStoredLocalizedForComponents() {
			_storedLocalizedForComponents.clear();
			_storedLocalizedForBorders.clear();
			// _storedAdditionalStrings.clear();
			_storedLocalizedForTableColumn.clear();
			// localizationListeners.clear();
		}

		// TODO: duplicated code as in LocalizedDelegateImpl, please refactor this to avoid code duplication
		@Override
		public void updateGUILocalized() {
			for (Map.Entry<Component, String> e : _storedLocalizedForComponents.entrySet()) {
				Component component = e.getKey();
				String string = e.getValue();
				String text = localizedForKey(string);
				/*String additionalString = _storedAdditionalStrings.get(component);
				if (additionalString != null) {
					text = text + additionalString;
				}*/
				if (component instanceof AbstractButton) {
					((AbstractButton) component).setText(text);
				}
				if (component instanceof JLabel) {
					((JLabel) component).setText(text);
				}
				component.setName(text);
				if (component.getParent() instanceof JTabbedPane) {
					if (((JTabbedPane) component.getParent()).indexOfComponent(component) > -1) {
						((JTabbedPane) component.getParent()).setTitleAt(((JTabbedPane) component.getParent()).indexOfComponent(component),
								text);
					}
				}
				if (component.getParent() != null && component.getParent().getParent() instanceof JTabbedPane) {
					if (((JTabbedPane) component.getParent().getParent()).indexOfComponent(component) > -1) {
						((JTabbedPane) component.getParent().getParent())
								.setTitleAt(((JTabbedPane) component.getParent().getParent()).indexOfComponent(component), text);
					}
				}
			}
			for (Map.Entry<JComponent, String> e : _storedLocalizedForComponentTooltips.entrySet()) {
				JComponent component = e.getKey();
				String string = e.getValue();
				String text = localizedForKey(string);
				component.setToolTipText(text);
			}
			for (Map.Entry<TitledBorder, String> e : _storedLocalizedForBorders.entrySet()) {
				String string = e.getValue();
				String text = localizedForKey(string);
				e.getKey().setTitle(text);
			}
			for (Map.Entry<TableColumn, String> e : _storedLocalizedForTableColumn.entrySet()) {
				String string = e.getValue();
				String text = localizedForKey(string);
				e.getKey().setHeaderValue(text);
			}
			for (Frame f : Frame.getFrames()) {
				f.repaint();
			}

		}

		public void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
			if (value == null) {
				value = key;
			}
			getDictForLang(language).put(key, value);
			FIBLocalizedEntry entry = getEntry(language, key);
			if (entry == null) {
				FIBLocalizedEntry newEntry = getModelFactory().newInstance(FIBLocalizedEntry.class);
				newEntry.setKey(key);
				newEntry.setLanguage(language.getName());
				newEntry.setValue(value);
				addToLocalizedEntries(newEntry);
			}
			else {
				entry.setValue(value);
			}
		}

		@Override
		public boolean handleNewEntry(String key, Language language) {
			// logger.warning(">>>>>>>>>>>>>>>>>>>>> Cannot find key "+key+" for language "+language);
			return false;
			// return false;
		}

		public class DynamicEntry implements LocalizedEntry {

			private String key;
			private final PropertyChangeSupport pcSupport;

			public DynamicEntry(String aKey) {
				pcSupport = new PropertyChangeSupport(this);
				key = aKey;
			}

			@Override
			public PropertyChangeSupport getPropertyChangeSupport() {
				return pcSupport;
			}

			@Override
			public String getDeletedProperty() {
				// TODO
				return null;
			}

			@Override
			public void delete() {
				if (dynamicEntries != null) {
					dynamicEntries.remove(this);
					for (Language l : Language.getAvailableLanguages()) {
						FIBLocalizedEntry e = getEntry(l, key);
						if (e != null) {
							// System.out.println("Removing " + e.getValue() + " for key " + key + " language=" + l);
							removeFromLocalizedEntries(e);
						}
					}
					FIBLocalizedDictionaryImpl.this.getPropertyChangeSupport().firePropertyChange("entries", null, getEntries());
				}
			}

			@Override
			public String getKey() {
				return key;
			}

			public void setKey(String aKey) {
				String englishValue = getEnglish();
				String frenchValue = getFrench();
				String dutchValue = getDutch();
				key = aKey;
				setEnglish(englishValue);
				setFrench(frenchValue);
				setDutch(dutchValue);
			}

			@Override
			public String getEnglish() {
				// The locale might be found in parent localizer
				String returned = localizedForKeyAndLanguage(key, Language.ENGLISH);
				if (returned == null) {
					returned = key;
				}
				return returned;
			}

			@Override
			public void setEnglish(String value) {
				String oldValue = getEnglish();
				setLocalizedForKeyAndLanguage(key, value, Language.ENGLISH);
				getPropertyChangeSupport().firePropertyChange("english", oldValue, getEnglish());
			}

			@Override
			public String getFrench() {
				// The locale might be found in parent localizer
				String returned = localizedForKeyAndLanguage(key, Language.FRENCH);
				if (returned == null) {
					returned = key;
				}
				return returned;
			}

			@Override
			public void setFrench(String value) {
				String oldValue = getFrench();
				setLocalizedForKeyAndLanguage(key, value, Language.FRENCH);
				getPropertyChangeSupport().firePropertyChange("french", oldValue, getFrench());
			}

			@Override
			public String getDutch() {
				// The locale might be found in parent localizer
				String returned = localizedForKeyAndLanguage(key, Language.DUTCH);
				if (returned == null) {
					returned = key;
				}
				return returned;
			}

			@Override
			public void setDutch(String value) {
				String oldValue = getDutch();
				setLocalizedForKeyAndLanguage(key, value, Language.DUTCH);
				getPropertyChangeSupport().firePropertyChange("dutch", oldValue, getDutch());
			}

			@Override
			public boolean getIsHTML() {
				return getFrench().startsWith("<html>") || getEnglish().startsWith("<html>") || getDutch().startsWith("<html>");
			}

			@Override
			public void setIsHTML(boolean flag) {
				if (flag) {
					setEnglish(addHTMLSupport(getEnglish()));
					setFrench(addHTMLSupport(getFrench()));
					setDutch(addHTMLSupport(getDutch()));
				}
				else {
					setEnglish(removeHTMLSupport(getEnglish()));
					setFrench(removeHTMLSupport(getFrench()));
					setDutch(removeHTMLSupport(getDutch()));
				}
				getPropertyChangeSupport().firePropertyChange("isHTML", !flag, flag);
			}

			private String addHTMLSupport(String value) {
				return "<html>" + StringUtils.LINE_SEPARATOR + "<head>" + StringUtils.LINE_SEPARATOR + "</head>"
						+ StringUtils.LINE_SEPARATOR + "<body>" + StringUtils.LINE_SEPARATOR + value + StringUtils.LINE_SEPARATOR
						+ "</body>" + StringUtils.LINE_SEPARATOR + "</html>";
			}

			private String removeHTMLSupport(String value) {
				return HTMLUtils.convertHTMLToPlainText(HTMLUtils.extractBodyContent(value, true).trim(), true);
				/*System.out.println("From " + value);
				System.out.println("To" + HTMLUtils.extractSourceFromEmbeddedTag(value));
				return HTMLUtils.extractSourceFromEmbeddedTag(value);*/
			}

			@Override
			public boolean hasInvalidValue() {
				return !isFrenchValueValid() || !isEnglishValueValid() || !isDutchValueValid();
			}

			@Override
			public boolean isFrenchValueValid() {
				return isValueValid(getKey(), getFrench());
			}

			@Override
			public boolean isEnglishValueValid() {
				return isValueValid(getKey(), getEnglish());
			}

			@Override
			public boolean isDutchValueValid() {
				return isValueValid(getKey(), getDutch());
			}

			public boolean isValueValid(String aKey, String aValue) {
				if (aValue == null || aValue.length() == 0) {
					return false;
				} // null or empty value is not valid
				if (aValue.equals(aKey)) {
					return false;
				} // not the same value > means not translated
				if (aValue.lastIndexOf("_") > -1) {
					return false;
				} // should not contains UNDERSCORE char
				return true;
			}

			@Override
			public String toString() {
				return "(key=" + key + "{en=" + getEnglish() + ";fr=" + getFrench() + ";du=" + getDutch() + "})";
			}
		}

		// This method is really not efficient, but only called in the context of locales editor
		// This issue is not really severe.
		private Vector<String> buildAllKeys() {
			Vector<String> returned = new Vector<>();
			for (Language l : values.keySet()) {
				for (String key : values.get(l).keySet()) {
					if (!returned.contains(key)) {
						returned.add(key);
					}
				}
			}
			return returned;
		}

		// This method is really not efficient, but only called in the context of locales editor
		// Impact of this issue is not really severe.
		@Override
		public List<DynamicEntry> getEntries() {
			if (dynamicEntries == null) {
				dynamicEntries = new Vector<>();
				for (String key : buildAllKeys()) {
					dynamicEntries.add(new DynamicEntry(key));
				}
				Collections.sort(dynamicEntries, new Comparator<DynamicEntry>() {
					@Override
					public int compare(DynamicEntry o1, DynamicEntry o2) {
						return Collator.getInstance().compare(o1.key, o2.key);
					}
				});
			}
			return dynamicEntries;
		}

		/*private DynamicEntry getDynamicEntry(String key) {
			if (key == null) {
				return null;
			}
			for (DynamicEntry entry : getDynamicEntries()) {
				if (key.equals(entry.key)) {
					return entry;
				}
			}
			return null;
		}*/

		@Override
		public void refresh() {
			logger.fine("Refresh called on FIBLocalizedDictionary " + Integer.toHexString(hashCode()));
			dynamicEntries = null;
			getPropertyChangeSupport().firePropertyChange(LOCALIZED_ENTRIES_KEY, null, getEntries());
			getPropertyChangeSupport().firePropertyChange(ENTRIES_KEY, null, getEntries());
		}

		@Override
		public DynamicEntry addEntry(String key) {

			DynamicEntry newDynamicEntry = new DynamicEntry(key);
			dynamicEntries.add(newDynamicEntry);
			Collections.sort(dynamicEntries, new Comparator<DynamicEntry>() {
				@Override
				public int compare(DynamicEntry o1, DynamicEntry o2) {
					return Collator.getInstance().compare(o1.key, o2.key);
				}
			});
			searchTranslation(newDynamicEntry);
			return null;
		}

		public void deleteEntry(DynamicEntry entry) {
			for (Language l : Language.availableValues()) {
				values.get(l).remove(entry.key);
				FIBLocalizedEntry e = getEntry(l, entry.key);
				if (e != null) {
					entries.remove(e);
				}
			}
			refresh();
		}

		/*@Override
		public void beginSearchNewLocalizationEntries() {
			isSearchingNewEntries = true;
		}
		
		@Override
		public void endSearchNewLocalizationEntries() {
			isSearchingNewEntries = false;
			refresh();
		}*/

		/**
		 * Register new localization entry with supplied value for specified key and language
		 * 
		 * @param key
		 * @param language
		 * @param value
		 * @return boolean indicating if registration was successfully performed
		 */
		@Override
		public boolean registerNewEntry(String key, Language language, String value) {
			if (StringUtils.isNotEmpty(key)) {
				// System.out.println("> register entry " + key);
				/*Thread.dumpStack();
				if (key.contains(" ")) {
					System.out.println("localized key with blank = " + key);
					Thread.dumpStack();
				}*/
				setLocalizedForKeyAndLanguage(key, value, language);
				return true;
			}
			return false;
		}

		private LocalizedDelegate parentLocalizedDelegate;

		@Override
		public LocalizedDelegate getParent() {
			return parentLocalizedDelegate;
		}

		@Override
		public void setParent(LocalizedDelegate parentLocalizedDelegate) {
			this.parentLocalizedDelegate = parentLocalizedDelegate;
		}

		@Override
		public File getLocalizedDirectory() {
			return null;
		}

		@Override
		public void searchTranslation(LocalizedEntry entry) {
			if (getParent() != null) {
				String englishTranslation = getParent().localizedForKeyAndLanguage(entry.getKey(), Language.ENGLISH, false);
				if (entry.getKey().equals(englishTranslation)) {
					englishTranslation = automaticEnglishTranslation(entry.getKey());
				}
				entry.setEnglish(englishTranslation);
				String dutchTranslation = getParent().localizedForKeyAndLanguage(entry.getKey(), Language.DUTCH, false);
				if (entry.getKey().equals(dutchTranslation)) {
					dutchTranslation = automaticDutchTranslation(entry.getKey());
				}
				entry.setDutch(dutchTranslation);
				String frenchTranslation = getParent().localizedForKeyAndLanguage(entry.getKey(), Language.FRENCH, false);
				if (entry.getKey().equals(frenchTranslation)) {
					frenchTranslation = automaticFrenchTranslation(entry.getKey());
				}
				entry.setFrench(frenchTranslation);
			}
			else {
				String englishTranslation = entry.getKey().toString();
				englishTranslation = englishTranslation.replace("_", " ");
				englishTranslation = englishTranslation.substring(0, 1).toUpperCase() + englishTranslation.substring(1);
				entry.setEnglish(englishTranslation);
				entry.setDutch(englishTranslation);
			}
		}

		private static String automaticEnglishTranslation(String key) {
			String englishTranslation = key.toString();
			englishTranslation = englishTranslation.replace("_", " ");
			englishTranslation = englishTranslation.substring(0, 1).toUpperCase() + englishTranslation.substring(1);
			return englishTranslation;
		}

		private static String automaticDutchTranslation(String key) {
			return key;
			// return automaticEnglishTranslation(key);
		}

		private static String automaticFrenchTranslation(String key) {
			return key;
			// return automaticEnglishTranslation(key);
		}

		private DynamicEntry getEntry(String key) {
			if (key == null) {
				return null;
			}
			for (DynamicEntry entry : getEntries()) {
				if (key.equals(entry.getKey())) {
					return entry;
				}
			}
			return null;
		}

		@Override
		public void foundLocalized(String key) {
			if (StringUtils.isNotEmpty(key)) {
				// System.out.println("Declared key: " + key);
				DynamicEntry entry = getEntry(key);
				if (entry == null) {
					// System.out.println("Created key: " + key);
					entry = new DynamicEntry(key);
					dynamicEntries.add(entry);
					searchTranslation(entry);
				}
				else {
					// System.out.println("Found key: " + key);
				}
			}
		}

		@Override
		public void searchLocalized() {
			getOwner().searchAndRegisterAllLocalized();
		}

	}
}
