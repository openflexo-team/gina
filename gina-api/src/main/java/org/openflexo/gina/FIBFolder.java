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

package org.openflexo.gina;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.rm.Resource;

/**
 * A {@link FIBFolder} stores some {@link FIBComponent} related to a {@link Resource} folder
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(FIBFolder.FIBFolderImpl.class)
public abstract interface FIBFolder extends FIBLibraryContainer {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = Resource.class, cardinality = Cardinality.LIST)
	public static final String RESOURCES_KEY = "resources";
	@PropertyIdentifier(type = FIBLibraryContainer.class)
	public static final String PARENT_KEY = "parent";

	@Getter(NAME_KEY)
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String aName);

	@Getter(PARENT_KEY)
	public FIBLibraryContainer getParent();

	@Setter(PARENT_KEY)
	public void setParent(FIBLibraryContainer aParent);

	@Getter(value = RESOURCES_KEY, cardinality = Cardinality.LIST, ignoreType = true)
	public List<Resource> getResources();

	@Setter(RESOURCES_KEY)
	public void setResources(List<Resource> resources);

	@Adder(RESOURCES_KEY)
	public void addToResources(Resource aResource);

	@Remover(RESOURCES_KEY)
	public void removeFromResources(Resource aResource);

	public String getFlattenedName();

	public boolean isFlattened();

	public FIBFolder getLastFlattenedFolder();

	public List<FIBFolder> getFlattenedFolders();

	public List<Resource> getFlattenedResources();

	public static abstract class FIBFolderImpl extends FIBLibraryContainerImpl implements FIBFolder {

		private static final Logger LOGGER = Logger.getLogger(FIBFolder.class.getPackage().getName());

		@Override
		public String getFlattenedName() {
			String returned = getName();
			FIBFolder current = this;
			while (current.isFlattened()) {
				current = current.getFolders().get(0);
				returned = returned + "/" + current.getName();
			}
			return returned;
		}

		@Override
		public boolean isFlattened() {
			if (getFolders().size() == 1 && getResources().size() == 0) {
				return true;
			}
			return false;
		}

		@Override
		public FIBFolder getLastFlattenedFolder() {
			if (isFlattened()) {
				FIBFolder nextFolder = getFolders().get(0);
				return nextFolder.getLastFlattenedFolder();
			}
			return this;
		}

		@Override
		public List<FIBFolder> getFlattenedFolders() {
			if (isFlattened()) {
				return getLastFlattenedFolder().getFolders();
			}
			return getFolders();
		}

		@Override
		public List<Resource> getFlattenedResources() {
			if (isFlattened()) {
				return getLastFlattenedFolder().getResources();
			}
			return getResources();
		}

		private class FIBFolderNotification {
			FIBFolder initialFlattenFolder;
			String oldFlattenedName;
			List<FIBFolder> oldFlattenedFolders;
			List<Resource> oldFlattenedResources;

			public FIBFolderNotification() {
				initialFlattenFolder = getInitialFlattenFolder();
				oldFlattenedName = initialFlattenFolder.getFlattenedName();
				oldFlattenedFolders = new ArrayList<>(initialFlattenFolder.getFlattenedFolders());
				oldFlattenedResources = new ArrayList<>(initialFlattenFolder.getFlattenedResources());
			}

			public void fireChange() {
				initialFlattenFolder.getPropertyChangeSupport().firePropertyChange("flattenedName", oldFlattenedName,
						initialFlattenFolder.getFlattenedName());
				initialFlattenFolder.getPropertyChangeSupport().firePropertyChange("flattenedFolders", oldFlattenedFolders,
						initialFlattenFolder.getFlattenedFolders());
				initialFlattenFolder.getPropertyChangeSupport().firePropertyChange("flattenedResources", oldFlattenedResources,
						initialFlattenFolder.getFlattenedResources());
			}
		}

		@Override
		public void addToFolders(FIBFolder aFolder) {

			FIBFolderNotification notif = new FIBFolderNotification();
			performSuperAdder(FOLDERS_KEY, aFolder);
			notif.fireChange();
		}

		@Override
		public void addToResources(Resource aResource) {
			FIBFolderNotification notif = new FIBFolderNotification();
			performSuperAdder(RESOURCES_KEY, aResource);
			notif.fireChange();
		}

		private FIBFolder getInitialFlattenFolder() {
			if (getParent() instanceof FIBFolderImpl && ((FIBFolder) getParent()).isFlattened()) {
				return ((FIBFolderImpl) getParent()).getInitialFlattenFolder();
			}
			return this;
		}

	}

}
