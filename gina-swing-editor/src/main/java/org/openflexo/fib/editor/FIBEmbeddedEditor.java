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

package org.openflexo.fib.editor;

import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * This class is used to generate a live FIB editor
 * 
 * @author sylvain
 * 
 */
public class FIBEmbeddedEditor {

	private FileResourceImpl fibResource;
	private Object[] data;

	private class Editor extends FIBAbstractEditor {

		@Override
		public Object[] getData() {
			return FIBEmbeddedEditor.this.getData();
		}

		@Override
		public FileResourceImpl getFIBResource() {
			return FIBEmbeddedEditor.this.getFIBResource();
		}

		@Override
		public boolean exitOnDispose() {
			return false;
		}

	}

	public FIBEmbeddedEditor(Resource fibResource, Object object) {
		super();
		Resource sourceCodeResource = ResourceLocator.locateSourceCodeResource(fibResource);
		if (sourceCodeResource instanceof FileResourceImpl) {
			this.fibResource = (FileResourceImpl) sourceCodeResource;
			this.data = new Object[1];
			this.data[0] = object;
			FIBAbstractEditor.init(new Editor());
		} else {
			throw new IllegalArgumentException("Resource " + fibResource + " is not a file");
		}
	}

	/*public FIBEmbeddedEditor(File aFile, Object object) {
		super();
		this.fibFile = aFile;
		this.data = new Object[1];
		this.data[0] = object;
		FIBAbstractEditor.init(new Editor());
	}

	public FIBEmbeddedEditor(String aFileName, Object object) {
		super();
		this.fibFile = ResourceLocator.retrieveResourceAsFile(ResourceLocator.locateResource(aFileName));
		this.data = new Object[1];
		this.data[0] = object;
		FIBAbstractEditor.init(new Editor());
	}*/

	public Object[] getData() {
		return data;
	}

	public FileResourceImpl getFIBResource() {
		return fibResource;
	}
}
