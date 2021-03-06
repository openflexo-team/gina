/**
 * HIM Recording example of a simple gina interface
 * 
 * @author Alexandre
 *
 */

package org.openflexo.replay.cases;

import java.awt.Dimension;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBBrowserElementChildren;
import org.openflexo.gina.sampleData.Family;
import org.openflexo.gina.sampleData.Person;
import org.openflexo.replay.test.Case;
import org.openflexo.replay.test.GraphicalContextDelegate;
import org.openflexo.replay.test.Window;

public class BrowserCase extends Case {

	private static FIBBrowser browser;
	private static Family family;

	public static void main(String[] args) {
		// initExecutor(1);
		initCase(new BrowserCase());
	}

	@Override
	public void start() {
		new Window(getManager(), 'A', Family.class, FIBController.class, getFamily());
		new Window(getManager(), 'B', Family.class, FIBController.class, getFamily());
	}

	public static Family getFamily() {
		if (family == null)
			family = new Family();
		return family;
	}

	@Override
	public Dimension getWindowSize() {
		return new Dimension(480, 320);
	}

	@Override
	public void initWindow(Window w) {
		browser = GraphicalContextDelegate.getFactory().newInstance(FIBBrowser.class);
		browser.setRoot(new DataBinding<>("data", browser, Object.class, BindingDefinitionType.GET));
		browser.setBoundToSelectionManager(true);
		browser.setIteratorType(Person.class);
		browser.setName("browser");

		FIBBrowserElement rootElement = GraphicalContextDelegate.getFactory().newInstance(FIBBrowserElement.class);
		rootElement.setName("family");
		rootElement.setDataType(Family.class);
		rootElement.setLabel(new DataBinding<>("\"My Family\"", browser, String.class, BindingDefinitionType.GET));
		FIBBrowserElementChildren parents = GraphicalContextDelegate.getFactory().newInstance(FIBBrowserElementChildren.class);
		parents.setData(new DataBinding<>("family.parents", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(parents);
		FIBBrowserElementChildren children = GraphicalContextDelegate.getFactory().newInstance(FIBBrowserElementChildren.class);
		parents.setData(new DataBinding<>("family.children", browser, Object.class, BindingDefinitionType.GET));
		rootElement.addToChildren(children);

		browser.addToElements(rootElement);

		FIBBrowserElement personElement = GraphicalContextDelegate.getFactory().newInstance(FIBBrowserElement.class);
		personElement.setName("person");
		personElement.setDataType(Person.class);
		personElement
				.setLabel(new DataBinding<String>("\"My relative: \"+person.toString", browser, String.class, BindingDefinitionType.GET));

		browser.addToElements(personElement);
		w.getComponent().addToSubComponents(browser, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));
	}

}
