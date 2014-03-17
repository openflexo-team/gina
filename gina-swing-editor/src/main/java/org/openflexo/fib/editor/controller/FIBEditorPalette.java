/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2013-2014 Openflexo
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
package org.openflexo.fib.editor.controller;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.editor.FIBEditor;
import org.openflexo.fib.editor.FIBPreferences;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.Resource;
import org.openflexo.rm.CompositeResourceLocatorImpl;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.toolbox.ToolBox;

public class FIBEditorPalette extends JDialog {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	private static final CompositeResourceLocatorImpl rl = CompositeResourceLocatorImpl.getResourceLocator();

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;

			public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
					DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

					private final JPanel paletteContent;

					private FIBEditorController editorController;

					public FIBEditorPalette(JFrame frame) {
						super(frame, "Palette", false);

						paletteContent = new JPanel(null);

						Resource dir = rl.locateResource("FIBEditorPalette");

						for (Resource modelFIBFile : rl.listResources(dir, Pattern.compile(".*[.]fib"))) {
							String paletteURL = modelFIBFile.getURI().replace(".fib", ".palette");
							
							FIBComponent modelComponent = FIBLibrary.instance().retrieveFIBComponent(modelFIBFile);

							int ind = paletteURL.indexOf("FIBEditorPalette");
							if (ind > 0){
								paletteURL = paletteURL.substring(ind);
							}
							Resource representationFIBFile = rl.locateResource(paletteURL);

							FIBComponent representationComponent = null;
							if (representationFIBFile != null){
								representationComponent = FIBLibrary.instance().retrieveFIBComponent(representationFIBFile);

							}else{
								representationComponent = FIBLibrary.instance().retrieveFIBComponent(modelFIBFile);
							}
							addPaletteElement(modelComponent, representationComponent);

						}

						getContentPane().add(paletteContent);
						setBounds(FIBPreferences.getPaletteBounds());
						new ComponentBoundSaver(this) {

							@Override
							public void saveBounds(Rectangle bounds) {
								FIBPreferences.setPaletteBounds(bounds);
							}
						};

					}

					private PaletteElement addPaletteElement(FIBComponent modelComponent, FIBComponent representationComponent) {
						PaletteElement el = new PaletteElement(modelComponent, representationComponent, this);
						paletteContent.add(el.getView().getResultingJComponent());
						return el;
					}

					public FIBEditorController getEditorController() {
						return editorController;
					}

					public void setEditorController(FIBEditorController editorController) {
						this.editorController = editorController;
					}

}
