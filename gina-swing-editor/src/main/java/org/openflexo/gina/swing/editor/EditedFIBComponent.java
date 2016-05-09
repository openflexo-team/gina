package org.openflexo.gina.swing.editor;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.FIBLibrary;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;

/**
 * This represent the edition of a {@link FIBComponent}, in a given context<br>
 * 
 * @author sylvain
 *
 */
public class EditedFIBComponent extends PropertyChangedSupportDefaultImplementation {

	static final Logger logger = FlexoLogger.getLogger(EditedFIBComponent.class.getPackage().getName());

	private String name;
	private Resource sourceResource;
	private Resource productionResource;
	private FIBComponent fibComponent;
	private final FIBLibrary fibLibrary;
	private FIBModelFactory fibFactory;
	private Object dataObject;

	public EditedFIBComponent(String name, FIBComponent fibComponent, FIBLibrary fibLibrary) {
		super();
		this.fibLibrary = fibLibrary;
		this.name = name;
		this.fibComponent = fibComponent;

	}

	public EditedFIBComponent(Resource sourceResource, FIBLibrary fibLibrary) {
		super();
		this.fibLibrary = fibLibrary;
		load(sourceResource);
	}

	public EditedFIBComponent(Resource sourceResource, Resource productionResource, FIBLibrary fibLibrary) {
		this(sourceResource, fibLibrary);
		this.productionResource = productionResource;
	}

	public String getName() {
		if (name != null) {
			return name;
		}
		if (getSourceResource() instanceof FileResourceImpl) {
			return ((FileResourceImpl) getSourceResource()).getFile().getName();
		}
		return "untitled.fib";
	}

	public FIBComponent getFIBComponent() {
		return fibComponent;
	}

	public FIBLibrary getFIBLibrary() {
		return fibLibrary;
	}

	public Object getDataObject() {
		return dataObject;
	}

	public void setDataObject(Object dataObject) {
		if ((dataObject == null && this.dataObject != null) || (dataObject != null && !dataObject.equals(this.dataObject))) {
			Object oldValue = this.dataObject;
			this.dataObject = dataObject;
			getPropertyChangeSupport().firePropertyChange("dataObject", oldValue, dataObject);
		}
	}

	public FIBModelFactory getFactory() {
		if (fibFactory != null) {
			return fibFactory;
		}
		if (getFIBLibrary() != null) {
			return getFIBLibrary().getFIBModelFactory();
		}
		return ApplicationFIBLibraryImpl.instance().getFIBModelFactory();
	}

	public Resource getSourceResource() {
		return sourceResource;
	}

	public void setSourceResource(Resource sourceResource) {
		if ((sourceResource == null && this.sourceResource != null)
				|| (sourceResource != null && !sourceResource.equals(this.sourceResource))) {
			Resource oldValue = this.sourceResource;
			this.sourceResource = sourceResource;
			getPropertyChangeSupport().firePropertyChange("sourceResource", oldValue, sourceResource);
		}
	}

	public Resource getProductionResource() {
		if (productionResource == null && getSourceResource() != null) {
			return getSourceResource();
		}
		return productionResource;
	}

	public void setProductionResource(Resource productionResource) {
		if ((productionResource == null && this.productionResource != null)
				|| (productionResource != null && !productionResource.equals(this.productionResource))) {
			Resource oldValue = this.productionResource;
			this.productionResource = productionResource;
			getPropertyChangeSupport().firePropertyChange("productionResource", oldValue, productionResource);
		}
	}

	public File getSourceFile() {
		if (sourceResource instanceof FileResourceImpl) {
			return ((FileResourceImpl) sourceResource).getFile();
		}
		return null;
	}

	private void load(Resource sourceResource) {
		setSourceResource(sourceResource);

		if (getSourceFile() != null) {
			try {
				fibFactory = new FIBModelFactory(getSourceFile().getParentFile());
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
				return;
			}
		}
		System.out.println("Loading " + sourceResource);
		this.fibComponent = fibLibrary.retrieveFIBComponent(sourceResource, true, getFactory());
		System.out.println("Done: fibComponent=" + this.fibComponent);
	}

	public void save() {
		if (getSourceResource() != null) {
			if (getSourceFile() != null) {
				logger.info("Save to file " + getSourceFile());
				getFIBLibrary().save(fibComponent, getSourceFile());
			}
			else {
				logger.warning("Cannot save FIBComponent: source resource is not a file");
			}
		}
		else {
			logger.warning("Cannot save FIBComponent: source resource is null");
		}
	}

	public void saveAs(FileResourceImpl sourceResource) {
		String oldName = getName();
		name = null;
		setSourceResource(sourceResource);
		save();
		getPropertyChangeSupport().firePropertyChange("name", oldName, getName());
	}

}
