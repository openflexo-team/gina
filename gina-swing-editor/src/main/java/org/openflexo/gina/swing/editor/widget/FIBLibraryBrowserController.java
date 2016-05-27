/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.gina.swing.editor.widget;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.gina.ApplicationFIBLibrary;
import org.openflexo.gina.FIBFolder;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.editor.controller.FIBEditorIconLibrary;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.gina.utils.FIBIconLibrary;
import org.openflexo.rm.Resource;

public class FIBLibraryBrowserController extends FIBController /*implements Observer*/ {

	private static final Logger logger = Logger.getLogger(FIBLibraryBrowserController.class.getPackage().getName());

	private FIBLibraryBrowser fibLibraryBrowser;

	public FIBLibraryBrowserController(FIBComponent rootComponent) {
		super(rootComponent, SwingViewFactory.INSTANCE);
	}

	public Resource getSelectedComponentResource() {
		if (fibLibraryBrowser != null) {
			return fibLibraryBrowser.getSelectedComponentResource();
		}
		return null;
	}

	public void setSelectedComponentResource(Resource selectedComponentResource) {
		if (fibLibraryBrowser != null) {
			fibLibraryBrowser.setSelectedComponentResource(selectedComponentResource);
		}
	}

	public void doubleClickOnComponentResource(Resource selectedComponentResource) {
		if (fibLibraryBrowser != null) {
			fibLibraryBrowser.doubleClickOnComponentResource(selectedComponentResource);
		}
	}

	public ImageIcon iconFor(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof FIBFolder) {
			return FIBIconLibrary.FOLDER_ICON;
		}
		else if (object instanceof FIBLibrary) {
			return FIBIconLibrary.FOLDER_ICON;
		}
		else if (object instanceof Resource) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		}
		return null;

	}

	public String textFor(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof Resource) {
			String relativePath = ((Resource) object).getRelativePath();
			if (relativePath.lastIndexOf("/") > -1) {
				return relativePath.substring(relativePath.lastIndexOf("/") + 1);
			}
			return relativePath;
		}
		else if (object instanceof ApplicationFIBLibrary) {
			return "ApplicationFIBLibrary";
		}
		else if (object instanceof FIBLibrary) {
			return "FIBLibrary";
		}
		return object.toString();

	}

	public void rightClick(FIBComponent component, MouseEvent event) {
		// editorController.getContextualMenu().displayPopupMenu(component, ((JFIBView<?, ?>) getRootView()).getJComponent(), event);
	}

	public FIBLibraryBrowser getFIBLibraryBrowser() {
		return fibLibraryBrowser;
	}

	public void setFIBLibraryBrowser(FIBLibraryBrowser fibLibraryBrowser) {
		if ((fibLibraryBrowser == null && this.fibLibraryBrowser != null)
				|| (fibLibraryBrowser != null && !fibLibraryBrowser.equals(this.fibLibraryBrowser))) {
			FIBLibraryBrowser oldValue = this.fibLibraryBrowser;
			this.fibLibraryBrowser = fibLibraryBrowser;
			getPropertyChangeSupport().firePropertyChange("fibLibraryBrowser", oldValue, fibLibraryBrowser);
		}
	}

}
