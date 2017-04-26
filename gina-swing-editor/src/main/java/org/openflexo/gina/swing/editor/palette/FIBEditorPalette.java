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

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.FIBLibrary.FIBLibraryImpl;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.editor.FIBEditor;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ToolBox;

/**
 * Represent a palette for the FIBEditor<br>
 * A palette present some elements from a folder
 * 
 * 
 * @author sylvain
 *
 */
public class FIBEditorPalette extends JPanel {

	static final Logger logger = FlexoLogger.getLogger(FIBEditor.class.getPackage().getName());

	private static final Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static final Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;

	public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS
			? Toolkit.getDefaultToolkit().createCustomCursor(DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private static FIBLibrary PALETTE_FIB_LIBRARY = FIBLibraryImpl.createInstance(null);

	private final Resource dir;
	private final FIBEditorPalettes palettes;

	public FIBEditorPalette(Resource dir, FIBEditorPalettes palettes, Object dataObject) {
		super(null);

		this.dir = dir;
		this.palettes = palettes;

		for (Resource modelFIBFile : dir.getContents(Pattern.compile(".*[.]fib"), true)) {
			String paletteURL = modelFIBFile.getURI().replace(".fib", ".palette");

			FIBComponent modelComponent = PALETTE_FIB_LIBRARY.retrieveFIBComponent(modelFIBFile);

			int ind = paletteURL.indexOf("FIBEditorPalette");
			if (ind > 0) {
				paletteURL = paletteURL.substring(ind);
			}
			Resource representationFIBFile = ResourceLocator.locateResource(paletteURL);

			FIBComponent representationComponent = null;
			if (representationFIBFile != null) {
				representationComponent = PALETTE_FIB_LIBRARY.retrieveFIBComponent(representationFIBFile);

			}
			else {
				representationComponent = PALETTE_FIB_LIBRARY.retrieveFIBComponent(modelFIBFile);
			}
			addPaletteElement(modelComponent, representationComponent, dataObject);

			/*System.out.println("********* FOUND palette element");
			System.out.println("modelFIBFile=" + modelFIBFile);
			System.out.println("representationFIBFile=" + representationFIBFile);
			System.out.println(modelComponent.getFactory().stringRepresentation(modelComponent));*/
		}

	}

	@Override
	public String getName() {
		if (dir instanceof FileResourceImpl) {
			return ((FileResourceImpl) dir).getFile().getName();
		}
		if (dir != null) {
			return dir.getRelativePath();
		}
		return super.getName();
	}

	private PaletteElement addPaletteElement(FIBComponent modelComponent, FIBComponent representationComponent, Object dataObject) {
		PaletteElement el = new PaletteElement(modelComponent, representationComponent, this, dataObject);
		JComponent resultingJComponent = ((JFIBView<?, ?>) el.getView()).getResultingJComponent();
		add(resultingJComponent);
		return el;
	}

	public FIBEditorPalettes getPalettes() {
		return palettes;
	}

}
