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
package org.openflexo.fib.model;

import org.openflexo.model.annotations.DefineValidationRule;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.validation.FixProposal;
import org.openflexo.model.validation.ValidationIssue;
import org.openflexo.model.validation.ValidationRule;
import org.openflexo.model.validation.ValidationWarning;

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

	@Getter(value = DICTIONARY_KEY /*, inverse = FIBLocalizedDictionary.ENTRIES_KEY*/)
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
			return getLocalizedDictionary().getComponent();
		}

	}

	@DefineValidationRule
	public static class LocalizedEntryShouldNotBeRegisteredTwice extends
			ValidationRule<LocalizedEntryShouldNotBeRegisteredTwice, FIBLocalizedEntry> {
		public LocalizedEntryShouldNotBeRegisteredTwice() {
			super(FIBLocalizedEntry.class, "localized_entry_should_not_be_registered_twice");
		}

		@Override
		public ValidationIssue<LocalizedEntryShouldNotBeRegisteredTwice, FIBLocalizedEntry> applyValidation(FIBLocalizedEntry entry) {

			if (entry.getLocalizedDictionary() != null) {
				if (entry.getLocalizedDictionary().getEntries().indexOf(entry) != entry.getLocalizedDictionary().getEntries()
						.lastIndexOf(entry)) {
					RemoveExtraReferences fixProposal = new RemoveExtraReferences(entry);
					return new ValidationWarning<LocalizedEntryShouldNotBeRegisteredTwice, FIBLocalizedEntry>(this, entry,
							"localized_entry_is_registered_twice", fixProposal);
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
					while (dict.getEntries().contains(entry)) {
						dict.removeFromEntries(entry);
					}
				}
			}

		}

	}

}
