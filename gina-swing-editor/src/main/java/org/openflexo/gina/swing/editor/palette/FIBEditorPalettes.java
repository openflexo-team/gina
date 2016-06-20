/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.gina.swing.editor.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ImageIconResource;

public class FIBEditorPalettes extends JTabbedPane {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	private FIBEditorController editorController;

	private List<FIBEditorPalette> palettes;

	public FIBEditorPalettes() {
		super();

		palettes = new ArrayList<>();

		Resource[] paletteDirs = new Resource[3];
		Object[] paletteData = new Object[3];

		paletteDirs[0] = ResourceLocator.locateResource("FIBEditorPalettes/Panels");
		paletteData[0] = new WidgetsPaletteData();

		paletteDirs[1] = ResourceLocator.locateResource("FIBEditorPalettes/Widgets");
		paletteData[1] = new WidgetsPaletteData();

		paletteDirs[2] = ResourceLocator.locateResource("FIBEditorPalettes/Advanced");
		paletteData[2] = new AdvancedPaletteData();

		for (int i = 0; i < 3; i++) {
			Resource paletteDir = paletteDirs[i];
			Object data = paletteData[i];
			FIBEditorPalette palette = new FIBEditorPalette(paletteDir, this, data);
			palettes.add(palette);
			add(palette.getName(), palette);
		}

	}

	public FIBEditorController getEditorController() {
		return editorController;
	}

	public void setEditorController(FIBEditorController editorController) {
		this.editorController = editorController;
	}

	public static class PanelsPaletteData {
	}

	public static class WidgetsPaletteData {
		public String textFieldString = "TextField";
		public Double doubleValue = 3.14159;
	}

	public static class AdvancedPaletteData {

		public static final ImageIcon ITEM1_ICON = new ImageIconResource(
				ResourceLocator.locateResource("FIBEditorPalettes/Advanced/Item1.png"));
		public static final ImageIcon ITEM2_ICON = new ImageIconResource(
				ResourceLocator.locateResource("FIBEditorPalettes/Advanced/Item2.png"));
		public static final ImageIcon ITEM3_ICON = new ImageIconResource(
				ResourceLocator.locateResource("FIBEditorPalettes/Advanced/Item3.png"));
		public static final ImageIcon FOLDER_ICON = new ImageIconResource(
				ResourceLocator.locateResource("FIBEditorPalettes/Advanced/Folder.gif"));

		public List<TableItem> tableItems;
		public List<BrowserFolder> browserFolders;

		public String htmlText = "<html><b>Formatted text editor</b><br>Used to edit some <i>formatted</i> text</html>";

		public AdvancedPaletteData() {
			tableItems = new ArrayList<TableItem>();
			tableItems.add(new TableItem(ITEM3_ICON, "Item1", "data1"));
			tableItems.add(new TableItem(ITEM3_ICON, "Item2", "data2"));
			tableItems.add(new TableItem(ITEM3_ICON, "Item3", "data3"));
			browserFolders = new ArrayList<BrowserFolder>();
			List<BrowserItem> itemsInFolder1 = new ArrayList<>();
			itemsInFolder1.add(new BrowserItem(ITEM2_ICON, "Item1"));
			itemsInFolder1.add(new BrowserItem(ITEM2_ICON, "Item2"));
			List<BrowserItem> itemsInFolder2 = new ArrayList<>();
			itemsInFolder2.add(new BrowserItem(ITEM2_ICON, "Item3"));
			browserFolders.add(new BrowserFolder(FOLDER_ICON, "Folder1", itemsInFolder1));
			browserFolders.add(new BrowserFolder(FOLDER_ICON, "Folder2", itemsInFolder2));
		}

		public static class TableItem {
			public Icon icon;
			public String data1;
			public String data2;

			public TableItem(Icon icon, String data1, String data2) {
				super();
				this.icon = icon;
				this.data1 = data1;
				this.data2 = data2;
			}
		}

		public static class BrowserFolder {
			public Icon icon;
			public String folderName;
			public List<BrowserItem> items;

			public BrowserFolder(Icon icon, String folderName, List<BrowserItem> items) {
				super();
				this.icon = icon;
				this.folderName = folderName;
				this.items = items;
			}
		}

		public static class BrowserItem {
			public Icon icon;
			public String itemName;

			public BrowserItem(Icon icon, String itemName) {
				super();
				this.icon = icon;
				this.itemName = itemName;
			}
		}

	}

}
