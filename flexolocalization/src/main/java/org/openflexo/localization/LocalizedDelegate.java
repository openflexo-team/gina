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

import java.io.File;
import java.util.List;

import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This interface is implemented by all classes implementing localization features<br>
 * A localized delegate is responsible for the localization of a set of keys<br>
 * If no support is assumed for a given key, forward the responsability of translation to parent delegate.
 * 
 * @author sylvain
 * 
 */
public interface LocalizedDelegate {

	/**
	 * Return String matching specified key and language<br>
	 * If this key is not localized, this method MUST return null, in order to forward request to parent delegate
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public String getLocalizedForKeyAndLanguage(String key, Language language);

	/**
	 * Return String matching specified key and language<br>
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public String getLocalizedForKeyAndLanguage(String key, Language language, boolean createsNewEntriesIfNonExistant);

	/**
	 * Return a boolean indicating if this delegate handle creation of new entries
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public boolean handleNewEntry(String key, Language language);

	/**
	 * Register new localization entry with supplied value for specified key and language
	 * 
	 * @param key
	 * @param language
	 * @param value
	 * @return boolean indicating if registration was successfully performed
	 */
	public boolean registerNewEntry(String key, Language language, String value);

	/**
	 * Return the parent delegate to which the localization request should be forwarded if no value is defined for a given localization key
	 * 
	 * @return
	 */
	public LocalizedDelegate getParent();

	/**
	 * Return all entries declared for this {@link LocalizedDelegate}
	 * 
	 * @return
	 */
	public List<? extends LocalizedEntry> getEntries();

	/**
	 * Return directory where this localized delegate is stored, when available (might be null if located in a JAR for example)
	 * 
	 * @return
	 */
	public File getLocalizedDirectory();

	/**
	 * Search automatic translation for supplied entry
	 * 
	 * @param entry
	 */
	public void searchTranslation(LocalizedEntry entry);

	/**
	 * Represents a localized entry<br>
	 * A {@link LocalizedEntry} is explicitely identified by its key
	 * 
	 * @author sylvain
	 *
	 */
	public interface LocalizedEntry extends HasPropertyChangeSupport {

		public String getKey();

		public String getEnglish();

		public void setEnglish(String value);

		public String getFrench();

		public void setFrench(String value);

		public String getDutch();

		public void setDutch(String value);

		public void delete();

		public boolean getIsHTML();

		public void setIsHTML(boolean flag);

		public boolean hasInvalidValue();

		public boolean isFrenchValueValid();

		public boolean isEnglishValueValid();

		public boolean isDutchValueValid();

	}
}
