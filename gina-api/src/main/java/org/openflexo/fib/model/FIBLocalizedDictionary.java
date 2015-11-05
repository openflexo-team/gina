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

package org.openflexo.fib.model;

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
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent.LocalizationEntryRetriever;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;
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

		// private final boolean isSearchingNewEntries = false;

		public FIBLocalizedDictionaryImpl() {
			entries = new ArrayList<FIBLocalizedEntry>();
			values = new HashMap<Language, Hashtable<String, String>>();
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
				dict = new Hashtable<String, String>();
				values.put(lang, dict);
			}
			return dict;
		}

		/*public String getDefaultValue(String key, Language language) {
		if (mainLocalizer != null) {
			return mainLocalizer.getLocalizedForKeyAndLanguage(key, language);
		}
		// Otherwise, don't know what to do, return key
		return key;
		// logger.info("Searched default value for key "+key+" return "+FlexoLocalization.localizedForKey(key));
		// return FlexoLocalization.localizedForKeyAndLanguage(key, language, false, false);
		}*/

		@Override
		public String getLocalizedForKeyAndLanguage(String key, Language language) {
			return getLocalizedForKeyAndLanguage(key, language, false);
		}

		@Override
		public String getLocalizedForKeyAndLanguage(String key, Language language, boolean createsNewEntriesIfNonExistant) {
			if (key == null || StringUtils.isEmpty(key)) {
				return null;
			}
			// if (isSearchingNewEntries) logger.info("-------> called localizedForKeyAndLanguage() key="+key+" lang="+language);

			String returned = getDictForLang(language).get(key);

			if (returned == null && createsNewEntriesIfNonExistant) {
				foundLocalized(key);
			}

			return returned;

			/*String returned = getDictForLang(language).get(key);
			if (returned == null) {
			String defaultValue = getDefaultValue(key, language);
			if (handleNewEntry(key, language)) {
				if (!key.equals(defaultValue)) {
					addToEntries(new FIBLocalizedEntry(this, key, language.getName(), defaultValue));
					logger.fine("FIBLocalizedDictionary: store value " + defaultValue + " for key " + key + " for language " + language);
				} else {
					getDictForLang(language).put(key, defaultValue);
					logger.fine("FIBLocalizedDictionary: undefined value for key " + key + " for language " + language);
				}
				// dynamicEntries = null;
			}
			return defaultValue;
			}
			return returned;*/
		}

		public void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
			if (value == null) {
				value = key;
			}
			getDictForLang(language).put(key, value);
			FIBLocalizedEntry entry = getEntry(language, key);
			if (entry == null) {
				FIBLocalizedEntry newEntry = getFactory().newInstance(FIBLocalizedEntry.class);
				newEntry.setKey(key);
				newEntry.setLanguage(language.getName());
				newEntry.setValue(value);
				addToLocalizedEntries(newEntry);
			} else {
				entry.setValue(value);
			}
		}

		@Override
		public boolean handleNewEntry(String key, Language language) {
			// logger.warning(">>>>>>>>>>>>>>>>>>>>> Cannot find key "+key+" for language "+language);
			return false;
			// return false;
		}

		public class DynamicEntry implements LocalizedEntry, HasPropertyChangeSupport {

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
				String returned = FlexoLocalization.localizedForKeyAndLanguage(FIBLocalizedDictionaryImpl.this, key, Language.ENGLISH);
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
				String returned = FlexoLocalization.localizedForKeyAndLanguage(FIBLocalizedDictionaryImpl.this, key, Language.FRENCH);
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
				String returned = FlexoLocalization.localizedForKeyAndLanguage(FIBLocalizedDictionaryImpl.this, key, Language.DUTCH);
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
				} else {
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
			Vector<String> returned = new Vector<String>();
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
				dynamicEntries = new Vector<DynamicEntry>();
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

		public DynamicEntry addEntry() {
			String key = "new_entry";
			DynamicEntry newDynamicEntry = new DynamicEntry(key);
			dynamicEntries.add(newDynamicEntry);
			Collections.sort(dynamicEntries, new Comparator<DynamicEntry>() {
				@Override
				public int compare(DynamicEntry o1, DynamicEntry o2) {
					return Collator.getInstance().compare(o1.key, o2.key);
				}
			});
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
				String englishTranslation = FlexoLocalization.localizedForKeyAndLanguage(getParent(), entry.getKey(), Language.ENGLISH,
						false);
				if (entry.getKey().equals(englishTranslation)) {
					englishTranslation = automaticEnglishTranslation(entry.getKey());
				}
				entry.setEnglish(englishTranslation);
				String dutchTranslation = FlexoLocalization.localizedForKeyAndLanguage(getParent(), entry.getKey(), Language.DUTCH, false);
				if (entry.getKey().equals(dutchTranslation)) {
					dutchTranslation = automaticDutchTranslation(entry.getKey());
				}
				entry.setDutch(dutchTranslation);
				String frenchTranslation = FlexoLocalization
						.localizedForKeyAndLanguage(getParent(), entry.getKey(), Language.FRENCH, false);
				if (entry.getKey().equals(frenchTranslation)) {
					frenchTranslation = automaticFrenchTranslation(entry.getKey());
				}
				entry.setFrench(frenchTranslation);
			} else {
				String englishTranslation = entry.getKey().toString();
				englishTranslation = englishTranslation.replace("_", " ");
				englishTranslation = englishTranslation.substring(0, 1).toUpperCase() + englishTranslation.substring(1);
				entry.setEnglish(englishTranslation);
				entry.setDutch(englishTranslation);
			}
		}

		private String automaticEnglishTranslation(String key) {
			String englishTranslation = key.toString();
			englishTranslation = englishTranslation.replace("_", " ");
			englishTranslation = englishTranslation.substring(0, 1).toUpperCase() + englishTranslation.substring(1);
			return englishTranslation;
		}

		private String automaticDutchTranslation(String key) {
			return key;
			// return automaticEnglishTranslation(key);
		}

		private String automaticFrenchTranslation(String key) {
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
				} else {
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
