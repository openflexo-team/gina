/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexolocalization, a component of the software infrastructure 
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

package org.openflexo.localization;

import java.awt.Component;
import java.awt.Frame;
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
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.apache.commons.io.IOUtils;
import org.openflexo.connie.BindingEvaluator;
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
public class LocalizedDelegateImpl extends Observable implements LocalizedDelegate, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(LocalizedDelegateImpl.class.getPackage().getName());

	private final LocalizedDelegate parent;
	private Resource localizedDirectoryResource;
	private final Hashtable<Language, Properties> _localizedDictionaries;

	private boolean automaticSaving = false;
	private boolean editingSupport = false;

	private List<Entry> entries;

	private final PropertyChangeSupport pcSupport;

	public static enum SearchMode {
		Contains, BeginsWith, EndsWith
	}

	public LocalizedDelegateImpl(Resource localizedDirectory, LocalizedDelegate parent, boolean automaticSaving, boolean editingSupport) {
		this.automaticSaving = automaticSaving;
		this.editingSupport = editingSupport;
		this.parent = parent;
		pcSupport = new PropertyChangeSupport(this);
		// If we want to update locales, we have to retrieve source code dictionaries
		if (editingSupport) {
			Resource sourceCodeResource = ResourceLocator.locateSourceCodeResource(localizedDirectory);
			if (sourceCodeResource == null) {
				localizedDirectoryResource = localizedDirectory;
				automaticSaving = false;
			}
			else {
				localizedDirectoryResource = sourceCodeResource;
			}
		}
		else {
			localizedDirectoryResource = localizedDirectory;
		}
		_localizedDictionaries = new Hashtable<Language, Properties>();
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public Resource getLocalizedDirectoryResource() {
		return localizedDirectoryResource;
	}

	public void setLocalizedDirectoryResource(Resource r) {
		localizedDirectoryResource = r;
	}

	private Properties getDictionary(Language language) {
		Properties dict = _localizedDictionaries.get(language);
		if (dict == null) {
			dict = loadDictionary(language);
		}
		return dict;
	}

	/*private Properties createNewDictionary(Language language) {
		Properties newDict = loadDictionary(language);
		_localizedDictionaries.put(language, newDict);
		saveDictionary(language, newDict);
		return newDict;
	}*/

	private Properties loadDictionary(Language language) {
		Properties loadedDict = new FlexoProperties();
		InputStream dict = getInputStreamForLanguage(language);
		if (dict == null) {
			logger.warning("Could not find dictionary for " + language + " in " + localizedDirectoryResource);
		}
		else {
			try {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Loading dictionary for language " + language.getName() + " Dir=" + localizedDirectoryResource.toString());
				}
				loadedDict.load(dict);
				_localizedDictionaries.put(language, loadedDict);
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unable to load Dictionary Resource for Language" + language.getName());
				}
			}
		}
		return loadedDict;
	}

	private InputStream getInputStreamForLanguage(Language language) {
		Resource dictResource = (ResourceLocator.locateResourceWithBaseLocation(localizedDirectoryResource, language.getName() + ".dict"));
		if (dictResource != null) {
			return dictResource.openInputStream();
		}
		if (localizedDirectoryResource instanceof FileResourceImpl) {
			// Dictionary was not found, creates it from parent file
			File newFile = new File(((FileResourceImpl) localizedDirectoryResource).getFile(), language.getName() + ".dict");
			if (!newFile.exists()) {
				try {
					newFile.createNewFile();
					saveDictionary(language, new Properties());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			dictResource = (ResourceLocator.locateResourceWithBaseLocation(localizedDirectoryResource, language.getName() + ".dict"));
			return dictResource.openInputStream();
		}
		return null;
	}

	private File getDictionaryFileForLanguage(Language language) {
		return ResourceLocator.retrieveResourceAsFile(
				ResourceLocator.locateResourceWithBaseLocation(localizedDirectoryResource, language.getName() + ".dict"));
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
				logger.info("Checking dictionary for language " + language.getName() + " Dir=" + localizedDirectoryResource.toString());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Looking for dictionary in  " + localizedDirectoryResource.toString());
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
				logger.fine(
						"Adding entry '" + key + "' in " + language + " dictionary, in directory " + localizedDirectoryResource.toString());
			}
			dict.setProperty(key, value);
			// saveDictionary(language, dict);
		}
	}

	@Override
	public boolean registerNewEntry(String key, Language language, String value) {
		addEntryInDictionary(language, key, value, true);
		if (automaticSaving) {
			save();
		}
		return true;
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
		getPropertyChangeSupport().firePropertyChange("entries", null, getEntries());
		return returned;
	}

	public void removeEntry(String key) {
		Entry entryToRemove = getEntry(key);
		if (entryToRemove != null) {
			// Remove from all dictionaries
			for (Language language : Language.availableValues()) {
				Properties dict = getDictionary(language);
				dict.remove(key);
				// saveDictionary(language, dict);
			}
			// entries = null;
			if (entries != null) {
				entries.remove(entryToRemove);
			}
			entryToRemove.delete();
			setChanged();
			notifyObservers();
			getPropertyChangeSupport().firePropertyChange("entries", null, getEntries());
		}
	}

	public void saveAllDictionaries() {
		for (Language language : Language.availableValues()) {
			Properties dict = getDictionary(language);
			// NPE if not dev and using jar
			if (dict != null) {
				saveDictionary(language, dict);
			}
		}
		synchronized (this) {
			saveScheduled = false;
		}
	}

	@Override
	public boolean handleNewEntry(String key, Language language) {
		return true;
	}

	/*@Override
	public String getLocalizedForKeyAndLanguage(String key, Language language) {
	
		if (key == null || StringUtils.isEmpty(key)) {
			return null;
		}
	
		Properties currentLanguageDict = getDictionary(language);
		// String localized = currentLanguageDict.getProperty(key);
	
		String returned = currentLanguageDict.getProperty(key);
	
		if (returned != null) {
			return returned;
		}
	
		if (parent != null) {
			return parent.getLocalizedForKeyAndLanguage(key, language);
		}
	
		return null;
	}*/

	public void setLocalizedForKeyAndLanguage(String key, String value, Language language) {
		if (_localizedDictionaries == null) {
			loadLocalizedDictionaries();
		}
		Properties currentLanguageDict = getDictionary(language);
		currentLanguageDict.setProperty(key, value);
		// saveDictionary(language, currentLanguageDict);
	}

	public class Entry implements LocalizedEntry {
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

		@Override
		public String getEnglish() {
			String localized = localizedForKeyAndLanguage(key, Language.ENGLISH);
			return localized != null ? localized : key;
			// return localizedForKeyAndLanguage(key, Language.ENGLISH);
		}

		@Override
		public void setEnglish(String value) {
			// System.out.println("setEnglish with " + value);
			String oldValue = getEnglish();
			setLocalizedForKeyAndLanguage(key, value, Language.ENGLISH);
			pcSupport.firePropertyChange("english", oldValue, value);
		}

		@Override
		public String getFrench() {
			String localized = localizedForKeyAndLanguage(key, Language.FRENCH);
			return localized != null ? localized : key;
			// return localizedForKeyAndLanguage(key, Language.FRENCH);
		}

		@Override
		public void setFrench(String value) {
			// System.out.println("setFrench with " + value);
			String oldValue = getFrench();
			setLocalizedForKeyAndLanguage(key, value, Language.FRENCH);
			pcSupport.firePropertyChange("french", oldValue, value);
		}

		@Override
		public String getDutch() {
			String localized = localizedForKeyAndLanguage(key, Language.DUTCH);
			return localized != null ? localized : key;
			// return localizedForKeyAndLanguage(key, Language.DUTCH);
		}

		@Override
		public void setDutch(String value) {
			// System.out.println("setDutch with " + value);
			String oldValue = getDutch();
			setLocalizedForKeyAndLanguage(key, value, Language.DUTCH);
			pcSupport.firePropertyChange("dutch", oldValue, value);
		}

		@Override
		public void delete() {
			removeEntry(key);
			pcSupport.firePropertyChange(DELETED_PROPERTY, false, true);
		}

		@Override
		public String getDeletedProperty() {
			return DELETED_PROPERTY;
		}

		@Override
		public String getKey() {
			return key;
		}

		public void setKey(String aNewKey) {
			if (aNewKey != null && !aNewKey.equals(key)) {
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
				getPropertyChangeSupport().firePropertyChange("key", oldKey, aNewKey);
			}
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

	@Override
	public List<Entry> getEntries() {
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
		}
		return entries;
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

	private boolean saveScheduled = false;
	private long lastSchedule = -1;

	public void save() {

		// Localization entries are scheduled for a save
		// When not scheduled, saving is scheduled after a 3 secs triggering
		// Any new save schedule triggers a new request, and saving is then postposed

		synchronized (this) {
			lastSchedule = System.currentTimeMillis();
			if (!saveScheduled) {
				saveScheduled = true;
				Thread saveThread = new Thread(new Runnable() {
					@Override
					public void run() {
						while (System.currentTimeMillis() < lastSchedule + 3000) {
							// We need to wait
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						logger.info("Saving dictionaries for " + localizedDirectoryResource);
						saveAllDictionaries();
					}
				}, "SaveLocales");
				saveThread.start();
			}
		}

	}

	// Test purposes
	public static void main(String[] args) {

		LocalizedDelegateImpl localization = new LocalizedDelegateImpl(ResourceLocator.locateResource("Localized"), null, true, true);
		localization.save();
		try {
			Thread.sleep(500);
			localization.save();
			Thread.sleep(1500);
			localization.save();
			Thread.sleep(600);
			localization.save();
			Thread.sleep(900);
			localization.save();
			Thread.sleep(1000);
			localization.save();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void refresh() {
		entries = null;
		setChanged();
		notifyObservers();
	}

	public Entry addEntry() {
		addEntry("key");
		Entry returned = getEntry("key");
		getPropertyChangeSupport().firePropertyChange("entries", null, getEntries());
		return returned;
	}

	public void deleteEntry(Entry entry) {
		entry.delete();
		getPropertyChangeSupport().firePropertyChange("entries", null, getEntries());
	}

	@Override
	public void searchTranslation(LocalizedEntry entry) {
		if (getParent() != null) {
			String englishTranslation = parent.localizedForKeyAndLanguage(entry.getKey(), Language.ENGLISH);
			if (entry.getKey().equals(englishTranslation)) {
				englishTranslation = automaticEnglishTranslation(entry.getKey());
			}
			entry.setEnglish(englishTranslation);
			String dutchTranslation = parent.localizedForKeyAndLanguage(entry.getKey(), Language.DUTCH);
			if (entry.getKey().equals(dutchTranslation)) {
				dutchTranslation = automaticDutchTranslation(entry.getKey());
			}
			entry.setDutch(dutchTranslation);
			String frenchTranslation = parent.localizedForKeyAndLanguage(entry.getKey(), Language.FRENCH);
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

	/**
	 * Return directory where this localized delegate is stored, when available (might be null if located in a JAR for example)
	 * 
	 * @return
	 */
	@Override
	public File getLocalizedDirectory() {
		return ResourceLocator.retrieveResourceAsFile(localizedDirectoryResource);
	}

	/*public String getParentDelegateDescription() {
		if (getParent() == null) {
			return "none";
		} else {
			return getParent().toString();
		}
	}*/

	@Override
	public String toString() {
		if (getLocalizedDirectory() != null) {
			return "Localization stored in " + getLocalizedDirectory().getAbsolutePath();
		}
		return super.toString();
	}

	@Override
	public void searchLocalized() {
	}

	/**
	 * This is general and main method to use localized in Flexo.<br>
	 * Applicable language is chosen from the one defined in Preferences.<br>
	 * Use english names for keys, such as 'some_english_words'
	 * 
	 * @param key
	 * @return localized String
	 */
	@Override
	public String localizedForKey(String key) {
		return localizedForKeyAndLanguage(key, FlexoLocalization.getCurrentLanguage());
	}

	@Override
	public String localizedForKeyAndLanguage(String key, Language language) {
		return retrieveLocalizedForKeyAndLanguage(key, language, handleNewEntry(key, language));
	}

	/**
	 * Return String matching specified key and language<br>
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	private String retrieveLocalizedForKeyAndLanguage(String key, Language language, boolean createsNewEntriesIfNonExistant) {

		if (key == null || StringUtils.isEmpty(key)) {
			return null;
		}

		/*if (!createsNewEntriesIfNonExistant) {
			return getLocalizedForKeyAndLanguage(key, language);
		}*/

		Properties currentLanguageDict = getDictionary(language);

		String localized = currentLanguageDict.getProperty(key);

		if (localized == null && createsNewEntriesIfNonExistant) {
			addEntry(key);
			if (automaticSaving) {
				logger.info("************** Save because added: " + key + " in " + this);
				save();
			}
			return currentLanguageDict.getProperty(key);
		}
		else {
			return localized;
		}
	}

	@Override
	public String localizedForKeyAndLanguage(String key, Language language, boolean createsNewEntriesIfNonExistant) {

		if (key == null) {
			return null;
		}

		String returned = retrieveLocalizedForKeyAndLanguage(key, language, false);

		if (returned == null) {
			// Not found
			if (createsNewEntriesIfNonExistant && handleNewEntry(key, language)) {
				// We have to register this new entries
				if (getParent() != null) {
					// A parent exists, we will try to use locales provided by the parent
					for (Language l : Language.availableValues()) {
						String value = getParent().localizedForKeyAndLanguage(key, l, false);
						if (value == null) {
							value = key;
						}
						if (l == language) {
							returned = value;
						}
						registerNewEntry(key, l, value);
					}
					return returned;
				}
				else {
					// No parent exists, we will use keys
					for (Language l : Language.availableValues()) {
						registerNewEntry(key, l, key);
					}
					return key;
				}
			}
			else {
				if (getParent() != null) {
					return getParent().localizedForKeyAndLanguage(key, language, false);
				}
				else {
					// logger.warning("Not found and not handling new entries for localized for key " + key + " delegate=" + delegate);
					return key;
				}
			}
		}
		else {
			return returned;
		}
	}

	@Override
	public String localizedForKeyWithParams(String key, Object... object) {
		String base = localizedForKey(key);
		return replaceAllParamsInString(base, object);
	}

	private final WeakHashMap<Component, String> _storedLocalizedForComponents = new WeakHashMap<Component, String>();

	private final WeakHashMap<JComponent, String> _storedLocalizedForComponentTooltips = new WeakHashMap<JComponent, String>();

	private final WeakHashMap<TitledBorder, String> _storedLocalizedForBorders = new WeakHashMap<TitledBorder, String>();

	private final WeakHashMap<TableColumn, String> _storedLocalizedForTableColumn = new WeakHashMap<TableColumn, String>();

	// private final WeakHashMap<Component, String> _storedAdditionalStrings = new WeakHashMap<Component, String>();

	@Override
	public String localizedForKey(String key, Component component) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponents.put(component, key);
		return localizedForKey(key);
	}

	@Override
	public String localizedTooltipForKey(String key, JComponent component) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponentTooltips.put(component, key);
		return localizedForKey(key);
	}

	@Override
	public String localizedForKey(String key, TitledBorder border) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for border " + border.getClass().getName());
		}
		_storedLocalizedForBorders.put(border, key);
		return localizedForKey(key);
	}

	@Override
	public String localizedForKey(String key, TableColumn column) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for border " + column.getClass().getName());
		}
		_storedLocalizedForTableColumn.put(column, key);
		return localizedForKey(key);
	}

	/*public String localizedForKey(String key, String additionalString, Component component) {
		if (key == null) {
			return null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.finest("localizedForKey called with " + key + " for " + component.getClass().getName());
		}
		_storedLocalizedForComponents.put(component, key);
		_storedAdditionalStrings.put(component, additionalString);
		return localizedForKey(key) + additionalString;
	}*/

	@Override
	public void clearStoredLocalizedForComponents() {
		_storedLocalizedForComponents.clear();
		_storedLocalizedForBorders.clear();
		// _storedAdditionalStrings.clear();
		_storedLocalizedForTableColumn.clear();
		// localizationListeners.clear();
	}

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

	/**
	 * @param returned
	 * @param object
	 */
	private static String replaceAllParamsInString(String aString, Object... object) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("replaceAllParamsInString() with " + aString + " and " + object);
		}
		// Pattern p = Pattern.compile("*\\($[a-zA-Z]\\)*");
		// Pattern p = Pattern.compile("\\p{Punct}\\bJava(\\w*)\\p{Punct}");
		// Pattern p = Pattern.compile("\\(\\bJava(\\w*)\\)");
		// Pattern p = Pattern.compile("\\(\\$(\\w*)\\)");
		Pattern p = Pattern.compile("\\(\\$([_0-9a-zA-Z[\\.]]+)\\)");
		Matcher m = p.matcher(aString);
		StringBuffer returned = new StringBuffer();
		while (m.find()) {
			int nextIndex = m.start();
			String foundPattern = m.group();
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("Found '" + foundPattern + "' at position " + nextIndex);
			}
			String suffix = m.group(1);
			if (logger.isLoggable(Level.FINE)) {
				logger.finest("Suffix is " + suffix);
			}
			try {
				int index = Integer.parseInt(suffix);
				if (object.length > index) {
					if (object[index] != null) {
						m.appendReplacement(returned, object[index].toString());
					}
					else {
						m.appendReplacement(returned, "");
					}
				}
				else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Argument index " + index + " is greater than number of arguments");
					}
				}
			} catch (NumberFormatException e) {
				try {
					m.appendReplacement(returned, valueForKeyAndObject(suffix, object[0]));
				} catch (IllegalArgumentException e2) {
					logger.warning("Unexpected IllegalArgumentException " + e2.getMessage());
				}
			}
		}
		m.appendTail(returned);
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("Returning " + returned);
		}
		return returned.toString();
	}

	private static String valueForKeyAndObject(String key, Object object) {

		try {
			return BindingEvaluator.evaluateBinding(key, object).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
		/*try {
		
			// Object objectForKey = BindingEvaluator.evaluateBinding(key, object);
			Object objectForKey = KeyValueDecoder.valueForKey(object, key);
			if (objectForKey != null) {
				return objectForKey.toString();
			} else {
				return "";
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning(e.getMessage());
			return key;
		}*/
	}

}
