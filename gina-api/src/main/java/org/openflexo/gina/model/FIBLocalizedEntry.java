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

import org.openflexo.localization.Language;
import org.openflexo.pamela.annotations.DefineValidationRule;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.validation.FixProposal;
import org.openflexo.pamela.validation.ValidationIssue;
import org.openflexo.pamela.validation.ValidationRule;
import org.openflexo.pamela.validation.ValidationWarning;

@ModelEntity
@ImplementationClass(FIBLocalizedEntry.FIBLocalizedEntryImpl.class)
@XMLElement(xmlTag = "Localized")
public interface FIBLocalizedEntry extends FIBModelObject {

	@PropertyIdentifier(type = FIBLocalizedDictionary.class)
	public static final String DICTIONARY_KEY = "dictionary";
	@PropertyIdentifier(type = String.class)
	public static final String KEY_KEY = "key";
	@PropertyIdentifier(type = String.class)
	public static final String LANGUAGE_KEY = "language";
	@PropertyIdentifier(type = String.class)
	public static final String VALUE_KEY = "value";

	@Getter(value = DICTIONARY_KEY /*, inverse = FIBLocalizedDictionary.ENTRIES_KEY*/, ignoreForEquality = true)
	public FIBLocalizedDictionary getLocalizedDictionary();

	@Setter(DICTIONARY_KEY)
	public void setLocalizedDictionary(FIBLocalizedDictionary dict);

	@Getter(value = KEY_KEY)
	@XMLAttribute
	public String getKey();

	@Setter(KEY_KEY)
	public void setKey(String key);

	@Getter(value = LANGUAGE_KEY)
	@XMLAttribute(xmlTag = "lang")
	public String getLanguage();

	@Setter(LANGUAGE_KEY)
	public void setLanguage(String language);

	@Getter(value = VALUE_KEY)
	@XMLAttribute(xmlTag = "value")
	public String getValue();

	@Setter(VALUE_KEY)
	public void setValue(String value);

	public static abstract class FIBLocalizedEntryImpl extends FIBModelObjectImpl implements FIBLocalizedEntry {

		@Override
		public FIBComponent getComponent() {
			FIBLocalizedDictionary dic = getLocalizedDictionary();
			if (dic != null) {
				return dic.getComponent();
			}
			return null;
		}

		@Override
		public String toString() {
			return getKey() + "/" + getLanguage() + ":" + getValue();
		}

	}

	@DefineValidationRule
	public static class LocalizedEntryShouldNotBeRegisteredTwice
			extends ValidationRule<LocalizedEntryShouldNotBeRegisteredTwice, FIBLocalizedEntry> {
		public LocalizedEntryShouldNotBeRegisteredTwice() {
			super(FIBLocalizedEntry.class, "localized_entry_should_not_be_registered_twice");
		}

		@Override
		public ValidationIssue<LocalizedEntryShouldNotBeRegisteredTwice, FIBLocalizedEntry> applyValidation(FIBLocalizedEntry entry) {

			if (entry.getLocalizedDictionary() != null) {
				if (entry.getLocalizedDictionary().getLocalizedEntries().indexOf(entry) != entry.getLocalizedDictionary()
						.getLocalizedEntries().lastIndexOf(entry)) {
					RemoveExtraReferences fixProposal = new RemoveExtraReferences(entry);
					return new ValidationWarning<>(this, entry, "localized_entry_is_registered_twice", fixProposal);
				}
			}
			return null;
		}

		protected static class RemoveExtraReferences extends FixProposal<LocalizedEntryShouldNotBeRegisteredTwice, FIBLocalizedEntry> {

			private final FIBLocalizedEntry entry;

			public RemoveExtraReferences(FIBLocalizedEntry entry) {
				super("remove_duplicated_references");
				this.entry = entry;
			}

			@Override
			protected void fixAction() {
				FIBLocalizedDictionary dict = entry.getLocalizedDictionary();
				if (dict != null) {
					if (dict.getLocalizedEntries().indexOf(entry) != dict.getLocalizedEntries().lastIndexOf(entry)) {
						dict.removeFromLocalizedEntries(entry);
					}
				}
			}

		}

	}

	@DefineValidationRule
	public static class LocalizedEntryShouldNotRedefineParentTranslation
			extends ValidationRule<LocalizedEntryShouldNotRedefineParentTranslation, FIBLocalizedEntry> {
		public LocalizedEntryShouldNotRedefineParentTranslation() {
			super(FIBLocalizedEntry.class, "localized_entry_should_not_redefine_parent_translation");
		}

		@Override
		public ValidationIssue<LocalizedEntryShouldNotRedefineParentTranslation, FIBLocalizedEntry> applyValidation(
				FIBLocalizedEntry entry) {

			// System.out.println("looking up " + entry);
			// System.out.println("parent=" + entry.getLocalizedDictionary().getParent());
			if (entry.getLocalizedDictionary() != null && entry.getLocalizedDictionary().getParent() != null) {
				String parentTranslation = entry.getLocalizedDictionary().getParent().localizedForKeyAndLanguage(entry.getKey(),
						Language.retrieveLanguage(entry.getLanguage()));
				// System.out.println("parentTranslation=" + parentTranslation);
				if (parentTranslation != null && parentTranslation.equals(entry.getValue())) {
					DeleteUnnecessaryTranslation fixProposal = new DeleteUnnecessaryTranslation(entry);
					return new ValidationWarning<>(this, entry, "($validable):_unnecessary_parent_locale_redefinition", fixProposal);
				}
			}
			return null;
		}

		protected static class DeleteUnnecessaryTranslation
				extends FixProposal<LocalizedEntryShouldNotRedefineParentTranslation, FIBLocalizedEntry> {

			private final FIBLocalizedEntry entry;

			public DeleteUnnecessaryTranslation(FIBLocalizedEntry entry) {
				super("remove_unnecessary_translation");
				this.entry = entry;
			}

			@Override
			protected void fixAction() {
				if (entry != null && !entry.isDeleted()) {
					if (entry.getLocalizedDictionary() != null) {
						entry.getLocalizedDictionary().removeFromLocalizedEntries(entry);
					}
					entry.delete();
				}
			}

		}

	}

}
