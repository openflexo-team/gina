/**
 *
 * Copyright (c) 2013-2014, Openflexo
 *
 * This file is part of Flexolocalization, a component of the software infrastructure
 * developed at Openflexo.
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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.localization;

import static org.junit.Assert.assertEquals;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openflexo.rm.ResourceLocator;

/**
 * Tests that the components a localizer was asked a text for follow the language and the edition of the dictionaries.
 */
public class TestLocalizationHotUpdate {

	private LocalizedDelegateImpl localizer;

	@Before
	public void setup() {
		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
		// Read-only: the edition below changes the dictionaries in memory only
		localizer = new LocalizedDelegateImpl(ResourceLocator.locateResource("TestLocales"), null, false, false);
	}

	@After
	public void tearDown() {
		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
	}

	private LocalizedDelegate.LocalizedEntry entry(String key) {
		return localizer.getEntries().stream().filter(e -> key.equals(e.getKey())).findFirst().get();
	}

	/** Wait for what was posted on the event dispatch thread so far. */
	private static void flushEventQueue() throws Exception {
		SwingUtilities.invokeAndWait(() -> {
		});
	}

	@Test
	public void testLanguageChangeUpdatesComponents() throws Exception {
		JLabel label = new JLabel("unchanged");
		JLabel other = new JLabel();
		TitledBorder border = new TitledBorder("");
		border.setTitle(localizer.localizedForKey("english_word", border));
		TableColumn column = new TableColumn();
		column.setHeaderValue(localizer.localizedForKey("english_word", column));
		other.setToolTipText(localizer.localizedTooltipForKey("english_word", other));
		assertEquals("English word", border.getTitle());

		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		flushEventQueue();

		assertEquals("Mot anglais", border.getTitle());
		assertEquals("Mot anglais", column.getHeaderValue());
		assertEquals("Mot anglais", other.getToolTipText());
		assertEquals("unchanged", label.getText());
	}

	@Test
	public void testRegisteredLabelFollowsLanguage() throws Exception {
		JLabel label = new JLabel();
		label.setText(localizer.localizedForKey("english_word", label));
		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		flushEventQueue();
		assertEquals("Mot anglais", label.getText());
		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
		flushEventQueue();
		assertEquals("English word", label.getText());
	}

	@Test
	public void testEditingATranslationUpdatesComponents() throws Exception {
		JLabel label = new JLabel();
		label.setText(localizer.localizedForKey("english_word", label));
		assertEquals("English word", label.getText());

		entry("english_word").setEnglish("Changed word");
		flushEventQueue();

		assertEquals("Changed word", label.getText());
	}

	@Test
	public void testSeveralEditsGiveOneUpdate() throws Exception {
		int[] count = { 0 };
		LocalizationListener listener = language -> count[0]++;
		FlexoLocalization.addToLocalizationListeners(listener);
		try {
			flushEventQueue();
			count[0] = 0;
			// On the event dispatch thread, so that the notification cannot run between two edits
			SwingUtilities.invokeAndWait(() -> {
				entry("english_word").setEnglish("A");
				entry("english_word").setEnglish("B");
				entry("english_word").setEnglish("C");
			});
			flushEventQueue();
			assertEquals(1, count[0]);
		} finally {
			FlexoLocalization.removeFromLocalizationListeners(listener);
		}
	}

	@Test
	public void testListenerRegisteredOnceOnly() {
		int[] count = { 0 };
		LocalizationListener listener = language -> count[0]++;
		FlexoLocalization.addToLocalizationListeners(listener);
		FlexoLocalization.addToLocalizationListeners(listener);
		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		FlexoLocalization.removeFromLocalizationListeners(listener);
		assertEquals(1, count[0]);
	}
}
