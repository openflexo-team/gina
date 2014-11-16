/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.fib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.Resource;

final public class FIBLibrary {

	static final Logger LOGGER = Logger.getLogger(FIBLibrary.class.getPackage().getName());

	private static FIBLibrary _current;

	private final Map<Resource, FIBComponent> _fibDefinitions;

	private final BindingFactory bindingFactory = new FIBBindingFactory();

	private FIBModelFactory fibModelFactory;

	private FIBLibrary() {
		super();
		_fibDefinitions = new Hashtable<Resource, FIBComponent>();
		try {
			fibModelFactory = new FIBModelFactory();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static FIBLibrary createInstance() {
		_current = new FIBLibrary();
		return _current;
	}

	public static FIBLibrary instance() {
		if (_current == null) {
			createInstance();
		}
		return _current;
	}

	public static boolean hasInstance() {
		return _current != null;
	}

	public FIBModelFactory getFIBModelFactory() {
		return fibModelFactory;
	}

	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}

	public boolean componentIsLoaded(Resource fibResourcePath) {
		return _fibDefinitions.get(fibResourcePath) != null;
	}

	/*public FIBComponent retrieveFIBComponent(File fibFile) {
		try {
			return retrieveFIBComponent(fibFile, true, new FIBModelFactory(fibFile.getParentFile()));
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public FIBComponent retrieveFIBComponent(File fibFile, boolean useCache, FIBModelFactory factory) {
		if (!fibFile.exists()) {
			logger.warning("FIB file does not exists: " + fibFile);
			return null;
		}
		FileResourceImpl fibLocation = null;
		try {
			fibLocation = new FileResourceImpl(fibFile.getCanonicalPath(), fibFile.toURI().toURL(), fibFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return retrieveFIBComponent(fibLocation, useCache, factory);
	}*/

	public FIBComponent retrieveFIBComponent(Resource fibFile, boolean useCache, FIBModelFactory factory) {
		FIBComponent fibComponent = _fibDefinitions.get(fibFile);
		if (!useCache || fibComponent == null || fibComponent.getLastModified().before(fibFile.getLastUpdate())) {

			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Load " + fibFile.getURI());
			}

			InputStream fis = null;

			try {
				fis = fibFile.openInputStream();
				FIBComponent component = (FIBComponent) factory.deserialize(fis);
				component.setLastModified(fibFile.getLastUpdate());
				component.setResource(fibFile);
				_fibDefinitions.put(fibFile, component);
				return component;
			} catch (ModelDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.warning("Unhandled Exception");
			} finally {
				if (fis != null) {
					IOUtils.closeQuietly(fis);
				}
			}
		}
		return fibComponent;
	}

	public void removeFIBComponentFromCache(Resource fibFile) {
		_fibDefinitions.remove(fibFile);
	}

	/*
	public void removeFIBComponentFromCache(String fibFileName) {
		_fibDefinitions.remove(fibFileName);
	}
	 */

	public FIBComponent retrieveFIBComponent(Resource fibResourceLocation) {
		return retrieveFIBComponent(fibResourceLocation, true);
	}

	public FIBComponent retrieveFIBComponent(Resource fibResourceLocation, boolean useCache) {
		InputStream inputStream = fibResourceLocation.openInputStream();
		try {
			return retrieveFIBComponent(fibResourceLocation, inputStream, useCache);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private FIBComponent retrieveFIBComponent(Resource fibIdentifier, InputStream inputStream, boolean useCache) {
		if (!useCache || _fibDefinitions.get(fibIdentifier) == null) {

			try {
				FIBModelFactory factory = new FIBModelFactory();

				FIBComponent component = (FIBComponent) factory.deserialize(inputStream);
				component.setLastModified(new Date());
				component.setResource(fibIdentifier);
				_fibDefinitions.put(fibIdentifier, component);
				return component;
			} catch (ModelDefinitionException e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
			} catch (IOException e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
			} catch (JDOMException e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
			} catch (InvalidDataException e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
			} catch (Exception e) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Exception raised during Fib import '" + fibIdentifier + "': " + e);
				}
				e.printStackTrace();
				LOGGER.warning("Unhandled Exception");
			} finally {
				if (inputStream != null) {
					IOUtils.closeQuietly(inputStream);
				}
			}
		}
		return _fibDefinitions.get(fibIdentifier);
	}

	public static boolean save(FIBComponent component, File file) {
		LOGGER.info("Save to file " + file.getAbsolutePath());

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			saveComponentToStream(component, file, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
		return false;
	}

	public static void saveComponentToStream(FIBComponent component, File fibFile, OutputStream stream) {

		try {
			FIBModelFactory factory = new FIBModelFactory(fibFile.getParentFile());

			factory.serialize(component, stream);
			LOGGER.info("Succeeded to save: " + fibFile);
		} catch (Exception e) {
			LOGGER.warning("Failed to save: " + fibFile + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

}
