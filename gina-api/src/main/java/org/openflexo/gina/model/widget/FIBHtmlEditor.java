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

package org.openflexo.gina.model.widget;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.gina.model.FIBWidget;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

import com.metaphaseeditor.MetaphaseEditorPanel;

@ModelEntity
@ImplementationClass(FIBHtmlEditor.FIBHtmlEditorImpl.class)
@XMLElement(xmlTag = "HTMLEditor")
public interface FIBHtmlEditor extends FIBWidget {

	@PropertyIdentifier(type = Vector.class)
	public static final String OPTIONS_IN_LINE1_KEY = "optionsInLine1";
	@PropertyIdentifier(type = Vector.class)
	public static final String OPTIONS_IN_LINE2_KEY = "optionsInLine2";
	@PropertyIdentifier(type = Vector.class)
	public static final String OPTIONS_IN_LINE3_KEY = "optionsInLine3";
	@PropertyIdentifier(type = Vector.class)
	public static final String VISIBLE_AND_UNUSED_OPTIONS_KEY = "visibleAndUnusedOptions";

	@Getter(value = OPTIONS_IN_LINE1_KEY, cardinality = Cardinality.LIST, inverse = FIBHtmlEditorOption.EDITOR_KEY)
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBHtmlEditorOption> getOptionsInLine1();

	@Setter(OPTIONS_IN_LINE1_KEY)
	public void setOptionsInLine1(List<FIBHtmlEditorOption> optionsInLine1);

	@Adder(OPTIONS_IN_LINE1_KEY)
	public void addToOptionsInLine1(FIBHtmlEditorOption aOptionsInLine1);

	@Remover(OPTIONS_IN_LINE1_KEY)
	public void removeFromOptionsInLine1(FIBHtmlEditorOption aOptionsInLine1);

	@Getter(value = OPTIONS_IN_LINE2_KEY, cardinality = Cardinality.LIST, inverse = FIBHtmlEditorOption.EDITOR_KEY)
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBHtmlEditorOption> getOptionsInLine2();

	@Setter(OPTIONS_IN_LINE2_KEY)
	public void setOptionsInLine2(List<FIBHtmlEditorOption> optionsInLine2);

	@Adder(OPTIONS_IN_LINE2_KEY)
	public void addToOptionsInLine2(FIBHtmlEditorOption aOptionsInLine2);

	@Remover(OPTIONS_IN_LINE2_KEY)
	public void removeFromOptionsInLine2(FIBHtmlEditorOption aOptionsInLine2);

	@Getter(value = OPTIONS_IN_LINE3_KEY, cardinality = Cardinality.LIST, inverse = FIBHtmlEditorOption.EDITOR_KEY)
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBHtmlEditorOption> getOptionsInLine3();

	@Setter(OPTIONS_IN_LINE3_KEY)
	public void setOptionsInLine3(List<FIBHtmlEditorOption> optionsInLine3);

	@Adder(OPTIONS_IN_LINE3_KEY)
	public void addToOptionsInLine3(FIBHtmlEditorOption aOptionsInLine3);

	@Remover(OPTIONS_IN_LINE3_KEY)
	public void removeFromOptionsInLine3(FIBHtmlEditorOption aOptionsInLine3);

	public FIBHtmlEditorOption getOption(String key);

	public boolean anyLineContains(FIBHtmlEditorOption option);

	public List<FIBHtmlEditorOption> getAvailableOptions();

	@Getter(value = VISIBLE_AND_UNUSED_OPTIONS_KEY, cardinality = Cardinality.LIST, inverse = FIBHtmlEditorOption.EDITOR_KEY)
	@CloningStrategy(StrategyType.CLONE)
	public List<FIBHtmlEditorOption> getVisibleAndUnusedOptions();

	@Adder(VISIBLE_AND_UNUSED_OPTIONS_KEY)
	public void addToVisibleAndUnusedOptions(FIBHtmlEditorOption anOption);

	@Remover(VISIBLE_AND_UNUSED_OPTIONS_KEY)
	public void removeFromVisibleAndUnusedOptions(FIBHtmlEditorOption anOption);

	public void indexChanged();

	public void makeFullHtmlEditor();

	public void makeEmbeddedHtmlEditor();

	public void makeLightHtmlEditor();

	public void makeUltraLightHtmlEditor();

	public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine1();

	public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine2();

	public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine3();

	public static abstract class FIBHtmlEditorImpl extends FIBWidgetImpl implements FIBHtmlEditor {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(FIBHtmlEditor.class.getPackage().getName());

		public static String[] option_keys = { MetaphaseEditorPanel.SOURCE_PANEL_KEY, MetaphaseEditorPanel.SOURCE_BUTTON_KEY,

				MetaphaseEditorPanel.PAGE_PANEL_KEY, MetaphaseEditorPanel.OPEN_BUTTON_KEY, MetaphaseEditorPanel.SAVE_BUTTON_KEY,
				MetaphaseEditorPanel.NEW_BUTTON_KEY, MetaphaseEditorPanel.PREVIEW_BUTTON_KEY,

				MetaphaseEditorPanel.EDIT_PANEL_KEY, MetaphaseEditorPanel.CUT_BUTTON_KEY, MetaphaseEditorPanel.COPY_BUTTON_KEY,
				MetaphaseEditorPanel.PASTE_BUTTON_KEY, MetaphaseEditorPanel.PASTE_AS_TEXT_BUTTON_KEY,

				MetaphaseEditorPanel.TOOLS_PANEL_KEY, MetaphaseEditorPanel.PRINT_BUTTON_KEY, MetaphaseEditorPanel.SPELL_CHECK_BUTTON_KEY,

				MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY, MetaphaseEditorPanel.UNDO_BUTTON_KEY, MetaphaseEditorPanel.REDO_BUTTON_KEY,

				MetaphaseEditorPanel.SEARCH_PANEL_KEY, MetaphaseEditorPanel.FIND_BUTTON_KEY, MetaphaseEditorPanel.REPLACE_BUTTON_KEY,

				MetaphaseEditorPanel.FORMAT_PANEL_KEY, MetaphaseEditorPanel.SELECT_ALL_BUTTON_KEY,
				MetaphaseEditorPanel.CLEAR_FORMATTING_BUTTON_KEY,

				MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY, MetaphaseEditorPanel.BOLD_BUTTON_KEY, MetaphaseEditorPanel.ITALIC_BUTTON_KEY,
				MetaphaseEditorPanel.UNDERLINE_BUTTON_KEY, MetaphaseEditorPanel.STRIKE_BUTTON_KEY,

				MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY, MetaphaseEditorPanel.SUB_SCRIPT_BUTTON_KEY,
				MetaphaseEditorPanel.SUPER_SCRIPT_BUTTON_KEY,

				MetaphaseEditorPanel.LIST_PANEL_KEY, MetaphaseEditorPanel.NUMBERED_LIST_BUTTON_KEY,
				MetaphaseEditorPanel.BULLETED_BUTTON_KEY,

				MetaphaseEditorPanel.BLOCK_PANEL_KEY, MetaphaseEditorPanel.DECREASE_INDENT_BUTTON_KEY,
				MetaphaseEditorPanel.INCREASE_INDENT_BUTTON_KEY, MetaphaseEditorPanel.BLOCK_QUOTE_BUTTON_KEY,
				MetaphaseEditorPanel.DIV_BUTTON_KEY, MetaphaseEditorPanel.PARAGRAPH_BUTTON_KEY,

				MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY, MetaphaseEditorPanel.LEFT_JUSTIFY_BUTTON_KEY,
				MetaphaseEditorPanel.CENTER_JUSTIFY_BUTTON_KEY, MetaphaseEditorPanel.RIGHT_JUSTIFY_BUTTON_KEY,
				MetaphaseEditorPanel.BLOCK_JUSTIFY_BUTTON_KEY,

				MetaphaseEditorPanel.LINK_PANEL_KEY, MetaphaseEditorPanel.LINK_BUTTON_KEY, MetaphaseEditorPanel.UNLINK_BUTTON_KEY,
				MetaphaseEditorPanel.ANCHOR_BUTTON_KEY,

				MetaphaseEditorPanel.MISC_PANEL_KEY, MetaphaseEditorPanel.IMAGE_BUTTON_KEY, MetaphaseEditorPanel.TABLE_BUTTON_KEY,
				MetaphaseEditorPanel.HORIZONTAL_LINE_BUTTON_KEY, MetaphaseEditorPanel.SPECIAL_CHAR_BUTTON_KEY,

				MetaphaseEditorPanel.FONT_PANEL_KEY, MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY,
				MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY,

				MetaphaseEditorPanel.COLOR_PANEL_KEY, MetaphaseEditorPanel.TEXT_COLOR_BUTTON_KEY,
				MetaphaseEditorPanel.BACKGROUND_COLOR_BUTTON_KEY,

				MetaphaseEditorPanel.ABOUT_PANEL_KEY, MetaphaseEditorPanel.ABOUT_BUTTON_KEY };

		private List<FIBHtmlEditorOption> availableOptions;
		private List<FIBHtmlEditorOption> visibleAndUnusedOptions;

		private Vector<FIBHtmlEditorOption> optionsInLine1;
		private Vector<FIBHtmlEditorOption> optionsInLine2;
		private Vector<FIBHtmlEditorOption> optionsInLine3;
		private final Vector<FIBHtmlEditorOption> firstLevelOptionsInLine1;
		private final Vector<FIBHtmlEditorOption> firstLevelOptionsInLine2;
		private final Vector<FIBHtmlEditorOption> firstLevelOptionsInLine3;

		public FIBHtmlEditorImpl() {
			super();
			optionsInLine1 = new Vector<FIBHtmlEditorOption>();
			optionsInLine2 = new Vector<FIBHtmlEditorOption>();
			optionsInLine3 = new Vector<FIBHtmlEditorOption>();
			firstLevelOptionsInLine1 = new Vector<FIBHtmlEditorOption>();
			firstLevelOptionsInLine2 = new Vector<FIBHtmlEditorOption>();
			firstLevelOptionsInLine3 = new Vector<FIBHtmlEditorOption>();

			/*FIBHtmlEditorOption o1 =  getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY);
			o1.setIsVisible(true);
			addToOptionsInLine1(o1);*/

		}

		@Override
		public String getBaseName() {
			return "HTMLEditor";
		}

		@Override
		public void makeFullHtmlEditor() {
			for (String s : option_keys) {
				FIBHtmlEditorOption option = getOption(s);
				option.setIsVisible(false);
				option.setIsVisible(true);
			}

			addToOptionsInLine1(getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY));

			addToOptionsInLine2(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.LIST_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));

			addToOptionsInLine3(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.MISC_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY));

		}

		@Override
		public void makeEmbeddedHtmlEditor() {
			for (String s : option_keys) {
				FIBHtmlEditorOption option = getOption(s);
				option.setIsVisible(false);
				option.setIsVisible(true);
			}

			getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY).setIsVisible(false);

			addToOptionsInLine1(getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY));

			addToOptionsInLine2(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.LIST_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));

			addToOptionsInLine3(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
			addToOptionsInLine3(getOption(MetaphaseEditorPanel.MISC_PANEL_KEY));

		}

		@Override
		public void makeLightHtmlEditor() {
			for (String s : option_keys) {
				FIBHtmlEditorOption option = getOption(s);
				option.setIsVisible(false);
				option.setIsVisible(true);
			}

			getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.MISC_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY).setIsVisible(false);

			getOption(MetaphaseEditorPanel.HORIZONTAL_LINE_BUTTON_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.SPECIAL_CHAR_BUTTON_KEY).setIsVisible(false);

			addToOptionsInLine1(getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.LIST_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));

			addToOptionsInLine2(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.FONT_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY));
			addToOptionsInLine2(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
		}

		@Override
		public void makeUltraLightHtmlEditor() {
			for (String s : option_keys) {
				FIBHtmlEditorOption option = getOption(s);
				option.setIsVisible(false);
				option.setIsVisible(true);
			}

			getOption(MetaphaseEditorPanel.SOURCE_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.PAGE_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.EDIT_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.TOOLS_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.UNDO_REDO_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.SEARCH_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.FORMAT_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.SUB_SUPER_SCRIPT_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.LIST_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.BLOCK_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.FONT_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.FONT_SIZE_PANEL_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.ABOUT_PANEL_KEY).setIsVisible(false);

			getOption(MetaphaseEditorPanel.ANCHOR_BUTTON_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.TABLE_BUTTON_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.HORIZONTAL_LINE_BUTTON_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.SPECIAL_CHAR_BUTTON_KEY).setIsVisible(false);
			getOption(MetaphaseEditorPanel.STRIKE_BUTTON_KEY).setIsVisible(false);

			addToOptionsInLine1(getOption(MetaphaseEditorPanel.PARAGRAPH_FORMAT_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.TEXT_EFFECT_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.COLOR_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.JUSTIFICATION_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.LINK_PANEL_KEY));
			addToOptionsInLine1(getOption(MetaphaseEditorPanel.MISC_PANEL_KEY));
		}

		@Override
		public List<FIBHtmlEditorOption> getAvailableOptions() {
			if (availableOptions == null && getModelFactory() != null) {
				availableOptions = new ArrayList<FIBHtmlEditorOption>();
				for (String s : option_keys) {
					FIBHtmlEditorOption newOption = getModelFactory().newInstance(FIBHtmlEditorOption.class);
					newOption.setName(s);
					newOption.setEditor(this);
					availableOptions.add(newOption);
				}
				visibleAndUnusedOptions = new ArrayList<FIBHtmlEditorOption>();
			}
			return availableOptions;
		}

		@Override
		public FIBHtmlEditorOption getOption(String key) {
			if (getAvailableOptions() != null) {
				for (FIBHtmlEditorOption option : getAvailableOptions()) {
					if (option.getName().equals(key)) {
						return option;
					}
				}
			}
			return null;
		}

		private void ensureOptionRegistering(FIBHtmlEditorOption option) {
			if (getAvailableOptions() != null) {
				if (getOption(option.getName()) == null) {
					getAvailableOptions().add(option);
				}
				else {
					if (getOption(option.getName()) != option) {
						int index = getAvailableOptions().indexOf(getOption(option.getName()));
						getAvailableOptions().set(index, option);
					}
				}
			}
		}

		@Override
		public Type getDefaultDataType() {
			return String.class;
		}

		@Override
		public boolean anyLineContains(FIBHtmlEditorOption option) {
			return optionsInLine1.contains(option) || optionsInLine2.contains(option) || optionsInLine3.contains(option);
		}

		@Override
		public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine1() {
			return firstLevelOptionsInLine1;
		}

		@Override
		public Vector<FIBHtmlEditorOption> getOptionsInLine1() {
			return optionsInLine1;
		}

		public void setOptionsInLine1(Vector<FIBHtmlEditorOption> optionsInLine1) {
			this.optionsInLine1 = optionsInLine1;
		}

		@Override
		public void addToOptionsInLine1(FIBHtmlEditorOption anOption) {
			ensureOptionRegistering(anOption);
			anOption.setIsVisible(true);
			anOption.setEditor(this);
			optionsInLine1.add(anOption);
			anOption.setIndexNoEditorNotification(optionsInLine1.indexOf(anOption));
			getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE1_KEY, null, optionsInLine1);
			if (visibleAndUnusedOptions.contains(anOption)) {
				removeFromVisibleAndUnusedOptions(anOption);
			}
			for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
				if (subOption.getIsVisible() && !optionsInLine1.contains(subOption)) {
					addToOptionsInLine1(subOption);
				}
			}
			if (anOption.getLevel() == 0) {
				firstLevelOptionsInLine1.add(anOption);
				anOption.setIndexNoEditorNotification(firstLevelOptionsInLine1.indexOf(anOption));
				getPropertyChangeSupport().firePropertyChange("firstLevelOptionsInLine1", null, firstLevelOptionsInLine1);
			}
		}

		public void addToOptionsInLine1(List<FIBHtmlEditorOption> options) {
			Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
			theOptions.addAll(options);
			for (FIBHtmlEditorOption o : theOptions) {
				addToOptionsInLine1(o);
			}
		}

		@Override
		public void removeFromOptionsInLine1(FIBHtmlEditorOption anOption) {
			optionsInLine1.remove(anOption);
			getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE1_KEY, null, optionsInLine1);
			if (!visibleAndUnusedOptions.contains(anOption)) {
				addToVisibleAndUnusedOptions(anOption);
			}
			for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
				if (optionsInLine1.contains(subOption)) {
					removeFromOptionsInLine1(subOption);
				}
			}
			if (anOption.getLevel() == 0) {
				firstLevelOptionsInLine1.remove(anOption);
				getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE1_KEY, null, firstLevelOptionsInLine1);
			}
		}

		public void removeFromOptionsInLine1(List<FIBHtmlEditorOption> options) {
			Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
			theOptions.addAll(options);
			for (FIBHtmlEditorOption o : theOptions) {
				removeFromOptionsInLine1(o);
			}
		}

		@Override
		public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine2() {
			return firstLevelOptionsInLine2;
		}

		@Override
		public Vector<FIBHtmlEditorOption> getOptionsInLine2() {
			return optionsInLine2;
		}

		public void setOptionsInLine2(Vector<FIBHtmlEditorOption> optionsInLine2) {
			this.optionsInLine2 = optionsInLine2;
		}

		@Override
		public void addToOptionsInLine2(FIBHtmlEditorOption anOption) {
			ensureOptionRegistering(anOption);
			anOption.setIsVisible(true);
			anOption.setEditor(this);
			optionsInLine2.add(anOption);
			anOption.setIndexNoEditorNotification(optionsInLine2.indexOf(anOption));
			getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE2_KEY, null, optionsInLine2);
			if (visibleAndUnusedOptions.contains(anOption)) {
				removeFromVisibleAndUnusedOptions(anOption);
			}
			for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
				if (subOption.getIsVisible() && !optionsInLine2.contains(subOption)) {
					addToOptionsInLine2(subOption);
				}
			}
			if (anOption.getLevel() == 0) {
				firstLevelOptionsInLine2.add(anOption);
				anOption.setIndexNoEditorNotification(firstLevelOptionsInLine2.indexOf(anOption));
				getPropertyChangeSupport().firePropertyChange("firstLevelOptionsInLine2", null, firstLevelOptionsInLine2);
			}
		}

		public void addToOptionsInLine2(List<FIBHtmlEditorOption> options) {
			Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
			theOptions.addAll(options);
			for (FIBHtmlEditorOption o : theOptions) {
				addToOptionsInLine2(o);
			}
		}

		@Override
		public void removeFromOptionsInLine2(FIBHtmlEditorOption anOption) {
			optionsInLine2.remove(anOption);
			getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE2_KEY, null, optionsInLine2);
			if (!visibleAndUnusedOptions.contains(anOption)) {
				addToVisibleAndUnusedOptions(anOption);
			}
			for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
				if (optionsInLine2.contains(subOption)) {
					removeFromOptionsInLine2(subOption);
				}
			}
			if (anOption.getLevel() == 0) {
				firstLevelOptionsInLine2.remove(anOption);
				getPropertyChangeSupport().firePropertyChange("firstLevelOptionsInLine2", null, firstLevelOptionsInLine2);
			}
		}

		public void removeFromOptionsInLine2(List<FIBHtmlEditorOption> options) {
			Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
			theOptions.addAll(options);
			for (FIBHtmlEditorOption o : theOptions) {
				removeFromOptionsInLine2(o);
			}
		}

		@Override
		public Vector<FIBHtmlEditorOption> getFirstLevelOptionsInLine3() {
			return firstLevelOptionsInLine3;
		}

		@Override
		public Vector<FIBHtmlEditorOption> getOptionsInLine3() {
			return optionsInLine3;
		}

		public void setOptionsInLine3(Vector<FIBHtmlEditorOption> optionsInLine3) {
			this.optionsInLine3 = optionsInLine3;
		}

		@Override
		public void addToOptionsInLine3(FIBHtmlEditorOption anOption) {
			ensureOptionRegistering(anOption);
			anOption.setIsVisible(true);
			anOption.setEditor(this);
			optionsInLine3.add(anOption);
			anOption.setIndexNoEditorNotification(optionsInLine3.indexOf(anOption));
			getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE3_KEY, null, optionsInLine3);
			if (visibleAndUnusedOptions.contains(anOption)) {
				removeFromVisibleAndUnusedOptions(anOption);
			}
			for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
				if (subOption.getIsVisible() && !optionsInLine3.contains(subOption)) {
					addToOptionsInLine3(subOption);
				}
			}
			if (anOption.getLevel() == 0) {
				firstLevelOptionsInLine3.add(anOption);
				anOption.setIndexNoEditorNotification(firstLevelOptionsInLine3.indexOf(anOption));
				getPropertyChangeSupport().firePropertyChange("firstLevelOptionsInLine3", null, firstLevelOptionsInLine3);
			}
		}

		public void addToOptionsInLine3(List<FIBHtmlEditorOption> options) {
			Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
			theOptions.addAll(options);
			for (FIBHtmlEditorOption o : theOptions) {
				addToOptionsInLine3(o);
			}
		}

		@Override
		public void removeFromOptionsInLine3(FIBHtmlEditorOption anOption) {
			optionsInLine3.remove(anOption);
			getPropertyChangeSupport().firePropertyChange(OPTIONS_IN_LINE3_KEY, null, optionsInLine3);
			if (!visibleAndUnusedOptions.contains(anOption)) {
				addToVisibleAndUnusedOptions(anOption);
			}
			for (FIBHtmlEditorOption subOption : anOption.getSubOptions()) {
				if (optionsInLine3.contains(subOption)) {
					removeFromOptionsInLine3(subOption);
				}
			}
			if (anOption.getLevel() == 0) {
				firstLevelOptionsInLine3.remove(anOption);
				getPropertyChangeSupport().firePropertyChange("firstLevelOptionsInLine3", null, firstLevelOptionsInLine3);
			}
		}

		public void removeFromOptionsInLine3(List<FIBHtmlEditorOption> options) {
			Vector<FIBHtmlEditorOption> theOptions = new Vector<FIBHtmlEditorOption>();
			theOptions.addAll(options);
			for (FIBHtmlEditorOption o : theOptions) {
				removeFromOptionsInLine3(o);
			}
		}

		@Override
		public List<FIBHtmlEditorOption> getVisibleAndUnusedOptions() {
			return visibleAndUnusedOptions;
		}

		@Override
		public void addToVisibleAndUnusedOptions(FIBHtmlEditorOption anOption) {
			if (anOption.getLevel() == 0) {
				// logger.info(">> addToVisibleAndUnusedOptions "+anOption);
				visibleAndUnusedOptions.add(anOption);
				getPropertyChangeSupport().firePropertyChange(VISIBLE_AND_UNUSED_OPTIONS_KEY, null, visibleAndUnusedOptions);
			}
		}

		@Override
		public void removeFromVisibleAndUnusedOptions(FIBHtmlEditorOption anOption) {
			if (anOption.getLevel() == 0) {
				// logger.info(">> removeFromVisibleAndUnusedOptions "+anOption);
				visibleAndUnusedOptions.remove(anOption);
				getPropertyChangeSupport().firePropertyChange(VISIBLE_AND_UNUSED_OPTIONS_KEY, null, visibleAndUnusedOptions);
			}
		}

		@Override
		public void indexChanged() {
			// setChanged();
			// notifyObservers();
		}

	}
}
