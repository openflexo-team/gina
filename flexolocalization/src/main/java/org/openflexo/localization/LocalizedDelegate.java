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
public interface LocalizedDelegate {

	/**
	 * Return String matching specified key and language defined as default in {@link FlexoLocalization}<br>
	 * If this key is not localized, this method MUST return null, in order to forward request to parent delegate
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public String localizedForKey(String key);

	public String localizedForKeyWithParams(String key, Object... object);

	/**
	 * Return String matching specified key and language<br>
	 * If this key is not localized, this method MUST return null, in order to forward request to parent delegate
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public String localizedForKeyAndLanguage(String key, Language language);

	/**
	 * Return String matching specified key and language<br>
	 * 
	 * @param key
	 * @param language
	 * @return
	 */
	public String localizedForKeyAndLanguage(String key, Language language, boolean createsNewEntriesIfNonExistant);

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

	public void searchLocalized();

	public String localizedForKey(String key, Component component);

	public String localizedTooltipForKey(String key, JComponent component);

	public String localizedForKey(String key, TitledBorder border);

	public String localizedForKey(String key, TableColumn column);

	public void clearStoredLocalizedForComponents();

	public void updateGUILocalized();

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
