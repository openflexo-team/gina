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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.JDOMException;
import org.openflexo.connie.type.CustomTypeManager;
import org.openflexo.gina.FIBFolder.FIBFolderImpl;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.bindings.FIBBindingFactory;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * A {@link FIBLibrary} has the responsability of managing a collection of {@link FIBComponent} encoded as {@link Resource}
 * 
 * Those {@link FIBComponent} are organized relatively to their resource organization and are stored in {@link FIBFolder}s.
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(FIBLibrary.FIBLibraryImpl.class)
public interface FIBLibrary extends FIBLibraryContainer {

	static final Logger LOGGER = Logger.getLogger(FIBLibrary.class.getPackage().getName());

	public FIBModelFactory getFIBModelFactory();

	public FIBBindingFactory getBindingFactory();

	public boolean componentIsLoaded(Resource fibResourcePath);

	public FIBComponent retrieveFIBComponent(Resource fibFile, boolean useCache, FIBModelFactory factory);

	public void removeFIBComponentFromCache(Resource fibFile);

	public FIBComponent retrieveFIBComponent(Resource fibResourceLocation);

	public FIBComponent retrieveFIBComponent(Resource fibResourceLocation, boolean useCache);

	public boolean save(FIBComponent component, Resource resourceToSave);

	public void saveComponentToStream(FIBComponent component, Resource resourceToSave, OutputStream stream);

	public String stringRepresentation(FIBComponent object);

	public CustomTypeManager getCustomTypeManager();

	public static abstract class FIBLibraryImpl extends FIBLibraryContainerImpl implements FIBLibrary {

		private static final Logger LOGGER = Logger.getLogger(FIBLibrary.class.getPackage().getName());

		public static FIBLibrary createInstance(CustomTypeManager customTypeManager) {
			FIBLibraryImpl returned = (FIBLibraryImpl) FOLDER_FACTORY.newInstance(FIBLibrary.class);
			returned.setCustomTypeManager(customTypeManager);
			try {
				returned.fibModelFactory = new FIBModelFactory(customTypeManager);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
			return returned;
		}

		// This map stores FIBComponent related to their source resource
		private final Map<Resource, FIBComponent> fibs;
		private final FIBBindingFactory bindingFactory = new FIBBindingFactory();
		protected FIBModelFactory fibModelFactory;
		private CustomTypeManager customTypeManager;

		public FIBLibraryImpl() {
			super();
			fibs = new Hashtable<>();
		}

		@Override
		public FIBModelFactory getFIBModelFactory() {
			return fibModelFactory;
		}

		@Override
		public FIBBindingFactory getBindingFactory() {
			return bindingFactory;
		}

		@Override
		public CustomTypeManager getCustomTypeManager() {
			return customTypeManager;
		}

		protected void setCustomTypeManager(CustomTypeManager customTypeManager) {
			this.customTypeManager = customTypeManager;
		}

		@Override
		public boolean componentIsLoaded(Resource fibResource) {
			Resource sourceResource = ResourceLocator.locateSourceCodeResource(fibResource);
			if (sourceResource == null) {
				sourceResource = fibResource;
			}
			return fibs.get(fibResource) != null;
		}

		protected FIBFolder notifyLoaded(FIBComponent component, Resource sourceResource) {
			// System.out.println(
			// "In FIBLibrary [" + Integer.toHexString(hashCode()) + "] loading " + resource + " " + resource.getRelativePath());

			FIBFolder returned = ensureFolderedOrganization(sourceResource);
			returned.addToResources(sourceResource);

			return returned;
		}

		private FIBFolder ensureFolderedOrganization(Resource resource) {
			StringTokenizer st = new StringTokenizer(resource.getRelativePath(), "/\\");
			List<String> path = new ArrayList<>();
			while (st.hasMoreTokens()) {
				path.add(st.nextToken());
			}
			FIBFolder returned = null;
			for (String pathElement : path) {
				if (path.indexOf(pathElement) < path.size() - 1) {
					returned = retrieveFolder(returned, pathElement);
				}
			}
			return returned;
		}

		private FIBFolder retrieveFolder(FIBFolder parent, String pathElement) {
			if (pathElement == null) {
				return null;
			}
			FIBFolder returned = null;
			if (parent != null) {
				for (FIBFolder f : parent.getFolders()) {
					if (pathElement.equals(f.getName())) {
						returned = f;
						break;
					}
				}
			}
			else {
				for (FIBFolder f : getFolders()) {
					if (pathElement.equals(f.getName())) {
						returned = f;
						break;
					}
				}
			}
			if (returned == null) {
				// Folder was not found
				// System.out.println("new folder " + pathElement + " in " + (parent != null ? parent.getName() : "FIBLibrary"));
				returned = FIBFolderImpl.FOLDER_FACTORY.newInstance(FIBFolder.class);
				returned.setName(pathElement);
				if (parent == null) {
					addToFolders(returned);
				}
				else {
					parent.addToFolders(returned);
				}
			}
			else {
				// Folder already exist, return it
			}
			return returned;
		}

		@Override
		public FIBComponent retrieveFIBComponent(Resource fibResource, boolean useCache, FIBModelFactory factory) {
			Resource sourceResource = ResourceLocator.locateSourceCodeResource(fibResource);
			if (sourceResource == null) {
				sourceResource = fibResource;
			}
			FIBComponent fibComponent = fibs.get(sourceResource);
			if (!useCache || fibComponent == null || fibComponent.getLastModified().before(fibResource.getLastUpdate())) {

				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("Load " + fibResource.getURI());
				}

				try (InputStream fis = fibResource.openInputStream()) {
					FIBComponent component = (FIBComponent) factory.deserialize(fis);
					component.setLastModified(fibResource.getLastUpdate());
					component.setResource(fibResource);
					component.setFIBLibrary(this);
					fibs.put(sourceResource, component);
					notifyLoaded(component, sourceResource);
					return component;
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (InvalidDataException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.warning("Unhandled Exception");
				}
			}
			return fibComponent;
		}

		@Override
		public void removeFIBComponentFromCache(Resource fibResource) {
			Resource sourceResource = ResourceLocator.locateSourceCodeResource(fibResource);
			if (sourceResource == null) {
				sourceResource = fibResource;
			}
			fibs.remove(sourceResource);
		}

		/*
		public void removeFIBComponentFromCache(String fibFileName) {
		_fibDefinitions.remove(fibFileName);
		}
		 */

		@Override
		public FIBComponent retrieveFIBComponent(Resource fibResourceLocation) {
			return retrieveFIBComponent(fibResourceLocation, true);
		}

		@Override
		public FIBComponent retrieveFIBComponent(Resource fibResourceLocation, boolean useCache) {
			try (InputStream inputStream = fibResourceLocation.openInputStream()) {
				return retrieveFIBComponent(fibResourceLocation, inputStream, useCache);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private FIBComponent retrieveFIBComponent(Resource fibResource, InputStream inputStream, boolean useCache) {
			Resource sourceResource = ResourceLocator.locateSourceCodeResource(fibResource);
			if (sourceResource == null) {
				sourceResource = fibResource;
			}
			if (!useCache || fibs.get(sourceResource) == null) {

				try {
					FIBModelFactory factory = new FIBModelFactory(fibResource.getContainer(), getCustomTypeManager());
					FIBComponent component = (FIBComponent) factory.deserialize(inputStream);
					component.setLastModified(new Date());
					component.setResource(fibResource);
					component.setFIBLibrary(this);
					fibs.put(sourceResource, component);
					notifyLoaded(component, sourceResource);
					return component;
				} catch (ModelDefinitionException e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.warning("Exception raised during Fib import '" + fibResource + "': " + e);
					}
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.warning("Exception raised during Fib import '" + fibResource + "': " + e);
					}
					e.printStackTrace();
				} catch (IOException e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.warning("Exception raised during Fib import '" + fibResource + "': " + e);
					}
					e.printStackTrace();
				} catch (JDOMException e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.warning("Exception raised during Fib import '" + fibResource + "': " + e);
					}
					e.printStackTrace();
				} catch (InvalidDataException e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.warning("Exception raised during Fib import '" + fibResource + "': " + e);
					}
					e.printStackTrace();
				} catch (Exception e) {
					if (LOGGER.isLoggable(Level.WARNING)) {
						LOGGER.warning("Exception raised during Fib import '" + fibResource + "': " + e);
					}
					e.printStackTrace();
					LOGGER.warning("Unhandled Exception");
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {}
					}
				}
			}
			return fibs.get(sourceResource);
		}

		@Override
		public boolean save(FIBComponent component, Resource resourceToSave) {
			LOGGER.info("Save to resourceToSave " + resourceToSave);
			try (OutputStream out = resourceToSave.openOutputStream()) {
				if (out != null) {
					saveComponentToStream(component, resourceToSave, out);
				}
				else {
					LOGGER.warning("Could not openOutStream for resource " + resourceToSave);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public void saveComponentToStream(FIBComponent component, Resource resourceToSave, OutputStream stream) {

			try {
				FIBModelFactory factory = new FIBModelFactory(resourceToSave.getContainer(), component.getCustomTypeManager());

				factory.serialize(component, stream);
				LOGGER.info("Succeeded to save: " + resourceToSave);
			} catch (Exception e) {
				LOGGER.warning("Failed to save: " + resourceToSave + " unexpected exception: " + e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					stream.close();
				} catch (IOException e) {}
			}
		}

		@Override
		public String stringRepresentation(FIBComponent object) {
			if (getFIBModelFactory() != null) {
				return getFIBModelFactory().stringRepresentation(object);
			}
			return "!NoFactory";
		}
	}

}
