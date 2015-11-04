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
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.swing.utils.JFIBPreferences;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.ComponentBoundSaver;
import org.openflexo.toolbox.ToolBox;

public class FIBEditorPalette extends JDialog {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

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

						Resource dir = ResourceLocator.locateResource("FIBEditorPalette");

						for (Resource modelFIBFile : dir.getContents(Pattern.compile(".*[.]fib"))) {
							String paletteURL = modelFIBFile.getURI().replace(".fib", ".palette");
							
							FIBComponent modelComponent = FIBLibrary.instance().retrieveFIBComponent(modelFIBFile);

							int ind = paletteURL.indexOf("FIBEditorPalette");
							if (ind > 0){
								paletteURL = paletteURL.substring(ind);
							}
							Resource representationFIBFile = ResourceLocator.locateResource(paletteURL);

							FIBComponent representationComponent = null;
							if (representationFIBFile != null){
								representationComponent = FIBLibrary.instance().retrieveFIBComponent(representationFIBFile);

							}else{
								representationComponent = FIBLibrary.instance().retrieveFIBComponent(modelFIBFile);
							}
							addPaletteElement(modelComponent, representationComponent);

						}

						getContentPane().add(paletteContent);
						setBounds(JFIBPreferences.getPaletteBounds());
						new ComponentBoundSaver(this) {

							@Override
							public void saveBounds(Rectangle bounds) {
								JFIBPreferences.setPaletteBounds(bounds);
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
