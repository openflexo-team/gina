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
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * This interface is implemented by all classes implementing localization features<br>
 * A localized delegate is responsible for the localization of a set of keys<br>
 * If no support is assumed for a given key, forward the responsability of translation to parent delegate.
 * 
 * @author sylvain
 * 
 */
public interface LocalizedDelegate extends HasPropertyChangeSupport {

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
	public String localizedForKey(String key);

	/**
	 * Return String matching specified key and language set as default language, asserting that the locale to translate contains some
	 * parametered references<br>
	 * 
	 * Usage examples:<br>
	 * <ul>
	 * <li><code>localizedForKeyWithParams("hello_($firstParameter)", anObject)</code><br>
	 * Return parametered locale such as "Hello World !", asserting that anObject respond to a getter method named firstParameter() or
	 * getFirstParameter() whise value is "World" in run-time context</li>
	 * <li><code>localizedForKeyWithParams("hello_($0)_($1)_and_($2)", "Pierre", "Paul", "Jacques")</code><br>
	 * Return parametered locales, such as "Bonjour Pierre, Paul et Jacques !"</li>
	 * </ul>
	 *
	 * @param key
	 * @param objects
	 * @return String matching specified key and language defined as default in {@link FlexoLocalization}
	 */
	public String localizedForKeyWithParams(String key, Object... objects);

	/**
	 * Return String matching specified key and language<br>
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public String localizedForKeyAndLanguage(String key, Language language);

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
	public String localizedForKeyAndLanguage(String key, Language language, boolean createsNewEntryInFirstEditableParent);

	/**
	 * Return boolean indicating if this delegate defines a translation for supplied key and language
	 * 
	 * @return
	 */
	public boolean hasKey(String key, Language language, boolean recursive);

	/**
	 * Return a boolean indicating if this delegate handle creation of new entries<br>
	 * When returning true, indicates that this delegate might be edited in this context.
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

	public void searchLocalized();

	public String localizedForKey(String key, Component component);

	public String localizedTooltipForKey(String key, JComponent component);

	public String localizedForKey(String key, TitledBorder border);

	public String localizedForKey(String key, TableColumn column);

	public void clearStoredLocalizedForComponents();

	public void updateGUILocalized();

	public LocalizedEntry addEntry(String key);

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
