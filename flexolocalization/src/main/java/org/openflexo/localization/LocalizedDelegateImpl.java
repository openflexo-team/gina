/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.localization;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.FlexoProperties;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

/**
 * Provides a default implementation for a localized delegate.<br>
 * Keys and values are managed and retrieved from language-specific dictionaries stored in a given directory<br>
 * 
 * This class provides also a basic support for new entries management: this software part automatically add entries in all languages for
 * all new entries, so provides an efficient and soft localized managing.
 * 
 * @author sylvain
 * 
 */
public class LocalizedDelegateImpl extends Observable implements LocalizedDelegate {

	private static final Logger logger = Logger.getLogger(LocalizedDelegateImpl.class.getPackage().getName());

	private final LocalizedDelegate parent;
	private final Resource _localizedDirectory;
	private final Hashtable<Language, Properties> _localizedDictionaries;

	private boolean automaticSaving = false;

	private Vector<Entry> entries;
	private Vector<Entry> issuesEntries;
	private Vector<Entry> matchingEntries;

	public static enum SearchMode {
		Contains, BeginsWith, EndsWith
	}

	public LocalizedDelegateImpl(Resource localizedDirectory, LocalizedDelegate parent, boolean automaticSaving) {
		this.automaticSaving = automaticSaving;
		this.parent = parent;
		// If we want to update locales, we have to retrieve source code dictionaries
		if (automaticSaving) {
			_localizedDirectory = ResourceLocator.locateSourceCodeResource(localizedDirectory);
		} else {
			_localizedDirectory = localizedDirectory;
		}
		_localizedDictionaries = new Hashtable<Language, Properties>();
	}

	private Properties getDictionary(Language language) {
		Properties dict = _localizedDictionaries.get(language);
		if (dict == null) {
			dict = createNewDictionary(language);
		}
		return dict;
	}

	private Properties createNewDictionary(Language language) {
		Properties newDict = loadDictionary(language);
		_localizedDictionaries.put(language, newDict);
		saveDictionary(language, newDict);
		return newDict;
	}

	private Properties loadDictionary(Language language) {
		Properties loadedDict = new FlexoProperties();
		InputStream dict = getInputStreamForLanguage(language);
		if (dict == null) {
			logger.warning("Could not find dictionary for " + language + " in " + _localizedDirectory);
		} else {
			try {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Loading dictionary for language " + language.getName() + " Dir=" + _localizedDirectory.toString());
				}
				loadedDict.load(dict);
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unable to load Dictionary Resource for Language" + language.getName());
				}
			}
		}
		return loadedDict;
	}

	private InputStream getInputStreamForLanguage(Language language) {
		Resource dictResource = (ResourceLocator.locateResourceWithBaseLocation(_localizedDirectory, language.getName() + ".dict"));
		if (dictResource != null) {
			return dictResource.openInputStream();
		}
		if (_localizedDirectory instanceof FileResourceImpl) {
			// Dictionary was not found, creates it from parent file
			File newFile = new File(((FileResourceImpl) _localizedDirectory).getFile(), language.getName() + ".dict");
			if (!newFile.exists()) {
				try {
					newFile.createNewFile();
					saveDictionary(language, new Properties());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			dictResource = (ResourceLocator.locateResourceWithBaseLocation(_localizedDirectory, language.getName() + ".dict"));
			return dictResource.openInputStream();
		}
		return null;
	}

	private File getDictionaryFileForLanguage(Language language) {
		return ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResourceWithBaseLocation(_localizedDirectory,
				language.getName() + ".dict"));
	}

	private void saveDictionary(Language language, Properties dict) {
		File dictFile = getDictionaryFileForLanguage(language);
		if (dictFile == null) {
			// IN Jar dict file is null;
			return;
		}
		try {
			final FileOutputStream fos = new FileOutputStream(dictFile);
			try {
				if (!dictFile.exists()) {
					dictFile.createNewFile();
				}
				dict.store(new JavaPropertiesOutputStream(fos), language.getName());
				logger.info("Saved " + dictFile.getAbsolutePath());
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unable to save file " + dictFile.getAbsolutePath() + " " + e.getClass().getName());
					// e.printStackTrace();
				}
			} finally {
				IOUtils.closeQuietly(fos);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadLocalizedDictionaries() {
		for (Language language : Language.availableValues()) {
			getDictionary(language);
		}
	}

	/*private void loadLocalizedDictionaries() {
		_localizedDictionaries = new Hashtable<Language, Properties>();

		Thread.dumpStack();

		for (Language language : Language.availableValues()) {
			InputStream dict = getInputStreamForLanguage(language);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Checking dictionary for language " + language.getName() + " Dir=" + _localizedDirectory.toString());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Looking for dictionary in  " + _localizedDirectory.toString());
			}
			if (dict != null) {
				Properties loadedDict = loadDictionary(language);
				_localizedDictionaries.put(language, loadedDict);
			}
		}
	}*/

	private void addEntryInDictionary(Language language, String key, String value, boolean required) {
		Properties dict = getDictionary(language);
		if (!required && dict.getProperty(key) == null || required) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Adding entry '" + key + "' in " + language + " dictionary, in directory " + _localizedDirectory.toString());
			}
			dict.setProperty(key, value);
			// saveDictionary(language, dict);
		}
	}

	public Entry addEntry(String key) {
		// Add in all dictionaries, when required
		for (Language language : Language.availableValues()) {
			addEntryInDictionary(language, key, key, false);
		}
		entries = null;
		setChanged();
		notifyObservers();
		Entry returned = getEntry(key);
		searchTranslation(returned);
		return returned;
	}

	public void removeEntry(String key) {
		// Remove from all dictionaries
		for (Language language : Language.availableValues()) {
			Properties dict = getDictionary(language);
			dict.remove(key);
			// saveDictionary(language, dict);
		}
		entries = null;
		setChanged();
		notifyObservers();
	}

	public void saveAllDictionaries() {
		for (Language language : Language.availableValues()) {
			Properties dict = getDictionary(language);
			// NPE if not dev and using jar
			if (dict != null) {
				saveDictionary(language, dict);
			}
		}
	}

	@Override
	public boolean handleNewEntry(String key, Language language) {
		return true;
	}

	/**
	 * Return String matching specified key and language<br>
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	@Override
	public String getLocalizedForKeyAndLanguage(String key, Language language, boolean createsNewEntriesIfNonExistant) {
		if (!createsNewEntriesIfNonExistant) {
			return getLocalizedForKeyAndLanguage(key, language);
		}

		Properties currentLanguageDict = getDictionary(language);

		String localized = currentLanguageDict.getProperty(key);

		if (localized == null) {
			addEntry(key);
			if (automaticSaving) {
				save();
			}
			return currentLanguageDict.getProperty(key);
		} else {
			return localized;
		}
	}

	@Override
	public String getLocalizedForKeyAndLanguage(String key, Language language) {
		Properties currentLanguageDict = getDictionary(language);
		// String localized = currentLanguageDict.getProperty(key);

		return currentLanguageDict.getProperty(key);

		/*if (localized == null) {
			addEntry(key);
			save();
			return currentLanguageDict.getProperty(key);
		} else {
			return localized;
		}*/
	}

	public void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
		if (_localizedDictionaries == null) {
			loadLocalizedDictionaries();
		}
		Properties currentLanguageDict = getDictionary(language);
		currentLanguageDict.setProperty(key, value);
		// saveDictionary(language, currentLanguageDict);
	}

	public class Entry implements HasPropertyChangeSupport {
		private static final String DELETED_PROPERTY = "deleted";
		private String key;
		private final PropertyChangeSupport pcSupport;

		public Entry(String aKey) {
			key = aKey;
			pcSupport = new PropertyChangeSupport(this);
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public String getEnglish() {
			String localized = getLocalizedForKeyAndLanguage(key, Language.ENGLISH);
			return localized != null ? localized : key;
			// return localizedForKeyAndLanguage(key, Language.ENGLISH);
		}

		public void setEnglish(String value) {
			// System.out.println("setEnglish with " + value);
			String oldValue = getEnglish();
			setLocalizedForKeyAndLanguage(key, value, Language.ENGLISH);
			pcSupport.firePropertyChange("english", oldValue, value);
		}

		public String getFrench() {
			String localized = getLocalizedForKeyAndLanguage(key, Language.FRENCH);
			return localized != null ? localized : key;
			// return localizedForKeyAndLanguage(key, Language.FRENCH);
		}

		public void setFrench(String value) {
			// System.out.println("setFrench with " + value);
			String oldValue = getFrench();
			setLocalizedForKeyAndLanguage(key, value, Language.FRENCH);
			pcSupport.firePropertyChange("french", oldValue, value);
		}

		public String getDutch() {
			String localized = getLocalizedForKeyAndLanguage(key, Language.DUTCH);
			return localized != null ? localized : key;
			// return localizedForKeyAndLanguage(key, Language.DUTCH);
		}

		public void setDutch(String value) {
			// System.out.println("setDutch with " + value);
			String oldValue = getDutch();
			setLocalizedForKeyAndLanguage(key, value, Language.DUTCH);
			pcSupport.firePropertyChange("dutch", oldValue, value);
		}

		public void delete() {
			removeEntry(key);
			pcSupport.firePropertyChange(DELETED_PROPERTY, false, true);
		}

		@Override
		public String getDeletedProperty() {
			return DELETED_PROPERTY;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String aNewKey) {
			String oldKey = key;
			for (Language l : Language.availableValues()) {
				Properties dict = getDictionary(l);
				String oldValue = dict.getProperty(oldKey);
				dict.remove(oldKey);
				if (oldValue != null) {
					dict.setProperty(aNewKey, oldValue);
				}
			}
			key = aNewKey;
			entries = null;
		}

		public boolean hasInvalidValue() {
			return !isFrenchValueValid() || !isEnglishValueValid() || !isDutchValueValid();
		}

		public boolean isFrenchValueValid() {
			return isValueValid(getKey(), getFrench());
		}

		public boolean isEnglishValueValid() {
			return isValueValid(getKey(), getEnglish());
		}

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

		public boolean getIsHTML() {
			return getFrench().startsWith("<html>") || getEnglish().startsWith("<html>") || getDutch().startsWith("<html>");
		}

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
		}

		private String addHTMLSupport(String value) {
			return "<html>" + StringUtils.LINE_SEPARATOR + "<head>" + StringUtils.LINE_SEPARATOR + "</head>" + StringUtils.LINE_SEPARATOR
					+ "<body>" + StringUtils.LINE_SEPARATOR + value + StringUtils.LINE_SEPARATOR + "</body>" + StringUtils.LINE_SEPARATOR
					+ "</html>";
		}

		private String removeHTMLSupport(String value) {
			return HTMLUtils.convertHTMLToPlainText(HTMLUtils.extractBodyContent(value, true).trim(), true);
			/*System.out.println("From " + value);
			System.out.println("To" + HTMLUtils.extractSourceFromEmbeddedTag(value));
			return HTMLUtils.extractSourceFromEmbeddedTag(value);*/
		}

		private boolean contains(String s) {
			if (s == null) {
				return false;
			}
			if (getKey().indexOf(s) >= 0) {
				return true;
			}
			if (getEnglish().indexOf(s) >= 0) {
				return true;
			}
			if (getFrench().indexOf(s) >= 0) {
				return true;
			}
			if (getDutch().indexOf(s) >= 0) {
				return true;
			}
			return false;
		}

		private boolean startsWith(String s) {
			if (s == null) {
				return false;
			}
			if (getKey().startsWith(s)) {
				return true;
			}
			if (getEnglish().startsWith(s)) {
				return true;
			}
			if (getFrench().startsWith(s)) {
				return true;
			}
			if (getDutch().startsWith(s)) {
				return true;
			}
			return false;
		}

		private boolean endsWith(String s) {
			if (s == null) {
				return false;
			}
			if (getKey().endsWith(s)) {
				return true;
			}
			if (getEnglish().endsWith(s)) {
				return true;
			}
			if (getFrench().endsWith(s)) {
				return true;
			}
			if (getDutch().endsWith(s)) {
				return true;
			}
			return false;
		}
	}

	public Vector<Entry> getEntries() {
		if (entries == null) {
			loadLocalizedDictionaries();
			entries = new Vector<Entry>();
			if (_localizedDictionaries.size() > 0) {
				Enumeration en = _localizedDictionaries.values().iterator().next().propertyNames();
				while (en.hasMoreElements()) {
					entries.add(new Entry((String) en.nextElement()));
				}
			}
			Collections.sort(entries, new Comparator<Entry>() {
				@Override
				public int compare(Entry o1, Entry o2) {
					return Collator.getInstance().compare(o1.key, o2.key);
				}
			});
			computeIssuesEntries();
		}
		return entries;
	}

	public Vector<Entry> getIssuesEntries() {
		if (issuesEntries == null) {
			issuesEntries = computeIssuesEntries();
		}
		return issuesEntries;
	}

	private Vector<Entry> computeIssuesEntries() {
		loadLocalizedDictionaries();
		issuesEntries = new Vector<Entry>();
		for (Entry e : getEntries()) {
			if (e.hasInvalidValue()) {
				issuesEntries.add(e);
			}
		}
		return issuesEntries;
	}

	public Vector<Entry> getMatchingEntries() {
		if (matchingEntries == null) {
			matchingEntries = new Vector<Entry>();
		}
		return matchingEntries;
	}

	protected Vector<Entry> computeMatchingEntries(String text, SearchMode searchMode) {
		loadLocalizedDictionaries();
		matchingEntries = new Vector<Entry>();
		for (Entry e : getEntries()) {
			switch (searchMode) {
			case Contains:
				if (e.contains(text)) {
					matchingEntries.add(e);
				}
				break;
			case BeginsWith:
				if (e.startsWith(text)) {
					matchingEntries.add(e);
				}
				break;
			case EndsWith:
				if (e.endsWith(text)) {
					matchingEntries.add(e);
				}
				break;

			default:
				break;
			}
		}
		return matchingEntries;
	}

	private Entry getEntry(String key) {
		if (key == null) {
			return null;
		}
		for (Entry entry : getEntries()) {
			if (key.equals(entry.key)) {
				return entry;
			}
		}
		return null;
	}

	public void save() {
		saveAllDictionaries();
	}

	public void refresh() {
		entries = null;
		setChanged();
		notifyObservers();
	}

	public Entry addEntry() {
		addEntry("key");
		return getEntry("key");
	}

	public void deleteEntry(Entry entry) {
		entry.delete();
	}

	public void searchTranslation(Entry entry) {
		if (getParent() != null) {
			String englishTranslation = FlexoLocalization.localizedForKeyAndLanguage(parent, entry.key, Language.ENGLISH);
			if (entry.key.equals(englishTranslation)) {
				englishTranslation = automaticEnglishTranslation(entry.key);
			}
			entry.setEnglish(englishTranslation);
			String dutchTranslation = FlexoLocalization.localizedForKeyAndLanguage(parent, entry.key, Language.DUTCH);
			if (entry.key.equals(dutchTranslation)) {
				dutchTranslation = automaticDutchTranslation(entry.key);
			}
			entry.setDutch(dutchTranslation);
			String frenchTranslation = FlexoLocalization.localizedForKeyAndLanguage(parent, entry.key, Language.FRENCH);
			entry.setFrench(frenchTranslation);
		} else {
			String englishTranslation = entry.key.toString();
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
		return automaticEnglishTranslation(key);
	}

	@Override
	public boolean registerNewEntry(String key, Language language, String value) {
		addEntryInDictionary(language, key, value, true);
		if (automaticSaving) {
			save();
		}
		return true;
	}

	@Override
	public LocalizedDelegate getParent() {
		return parent;
	}

	public void translateAll() {
		for (Entry entry : getEntries()) {
			searchTranslation(entry);
		}
	}

	protected Hashtable<Language, Properties> getLocalizedDictionaries() {
		return _localizedDictionaries;
	}

	@Deprecated
	public File getLocalizedDirectory() {
		return ResourceLocator.retrieveResourceAsFile(_localizedDirectory);
	}

	public String getParentDelegateDescription() {
		if (getParent() == null) {
			return "none";
		} else {
			return getParent().toString();
		}
	}

	@Override
	public String toString() {
		return "Localization stored in " + getLocalizedDirectory().getAbsolutePath();
	}

}
