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

import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;

/**
 * Represents a language in Flexo Application
 * 
 * @author sguerin
 * 
 */
public abstract class Language implements ChoiceList {

	private static final Logger logger = Logger.getLogger(Language.class.getPackage().getName());

	/**
	 * Those are available langages, represented as a
	 * 
	 * <pre>
	 * Vector
	 * </pre>
	 * 
	 * of
	 * 
	 * <pre>
	 * Language
	 * </pre>
	 * 
	 * objects
	 */
	private static Vector<Language> availableLanguages = null;

	public static final Language ENGLISH = new EnglishLanguage();

	public static final Language FRENCH = new FrenchLanguage();

	public static final Language DUTCH = new DutchLanguage();

	private static final Language[] knownsLanguages = { ENGLISH, FRENCH, DUTCH };

	private static class EnglishLanguage extends Language {
		EnglishLanguage() {
			super();
		}

		@Override
		public String getName() {
			return "English";
		}

		@Override
		public String getIdentifier() {
			return "ENGLISH";
		}

		@Override
		public String getTag() {
			return "EN";
		}
	}

	private static class FrenchLanguage extends Language {
		FrenchLanguage() {
			super();
		}

		@Override
		public String getName() {
			return "French";
		}

		@Override
		public String getIdentifier() {
			return "FRENCH";
		}

		@Override
		public String getTag() {
			return "FR";
		}
	}

	private static class DutchLanguage extends Language {
		DutchLanguage() {
			super();
		}

		@Override
		public String getName() {
			return "Dutch";
		}

		@Override
		public String getIdentifier() {
			return "DUTCH";
		}

		@Override
		public String getTag() {
			return "NL";
		}
	}

	/**
	 * Returns a vector containing all available languages as a
	 * 
	 * <pre>
	 * Vector
	 * </pre>
	 * 
	 * of
	 * 
	 * <pre>
	 * Language
	 * </pre>
	 * 
	 * objects
	 * 
	 * @return Vector of
	 * 
	 *         <pre>
	 * Language
	 * </pre>
	 * 
	 *         objects
	 */
	public static Vector<Language> getAvailableLanguages() {
		if (availableLanguages == null) {
			availableLanguages = new Vector<Language>();
			for (int i = 0; i < knownsLanguages.length; i++) {
				availableLanguages.add(knownsLanguages[i]);
			}
		}
		return availableLanguages;
	}

	public Language[] getKnownsLanguages() {
		return knownsLanguages;
	}

	/**
	 * Return a Vector of possible values (which must be of the same type as the one declared as class implemented this interface)
	 * 
	 * @return a Vector of ChoiceList
	 */
	@Override
	public Vector<Language> getAvailableValues() {
		return getAvailableLanguages();
	}

	public static Language get(Locale locale) {
		if (locale == Locale.ENGLISH || locale == Locale.UK || locale == Locale.US) {
			return ENGLISH;
		} else if (locale == Locale.FRANCE || locale == Locale.FRENCH) {
			return FRENCH;
		}
		return ENGLISH;
	}

	public static Language get(String languageAsString) {
		if (languageAsString == null) {
			return ENGLISH;
		}
		for (Language next : getAvailableLanguages()) {
			if (next.getName().equalsIgnoreCase(languageAsString)) {
				return next;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Cannot find language " + languageAsString);
		}
		if (getAvailableLanguages().size() > 0) {
			return getAvailableLanguages().firstElement();
		}
		return ENGLISH;
	}

	public static Language retrieveLanguage(String languageAsString) {
		if (languageAsString == null) {
			return ENGLISH;
		}
		for (Language next : getAvailableLanguages()) {
			if (next.getName().equalsIgnoreCase(languageAsString)) {
				return next;
			}
			if (next.getTag().equalsIgnoreCase(languageAsString)) {
				return next;
			}
		}
		return ENGLISH;
	}

	public abstract String getName();

	public abstract String getIdentifier();

	public abstract String getTag();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public static Vector<Language> availableValues() {
		return getAvailableLanguages();
	}

	@Override
	public String toString() {
		return getName();
	}
}
