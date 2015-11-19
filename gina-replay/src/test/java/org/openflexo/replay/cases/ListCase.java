/**
 * HIM Recording example of a simple gina interface
 * 
 * @author Alexandre
 *
 */

package org.openflexo.replay.cases;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBList;
import org.openflexo.gina.model.widget.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.replay.sampleData.Family;
import org.openflexo.replay.sampleData.Person;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.GraphicalContextDelegate;
import org.openflexo.replay.utils.Window;

public class ListCase extends Case {

	private static FIBLabel listLabel;
	private static FIBList list;
	private static Family family;
	
	public static void main(String[] args) {
		//initExecutor(1);
		initCase(new ListCase());
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
		listLabel = GraphicalContextDelegate.getFactory().newInstance(FIBLabel.class);
		listLabel.setLabel("List data");
		w.getComponent().addToSubComponents(listLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
		list = GraphicalContextDelegate.getFactory().newInstance(FIBList.class);
		list.setData(new DataBinding<Person>("data.biggestChild", list, Person.class, BindingDefinitionType.GET_SET));
		list.setList(new DataBinding<List<?>>("data.children", list, List.class, BindingDefinitionType.GET));
		list.setAutoSelectFirstRow(true);
		list.setName("list");
		w.getComponent().addToSubComponents(list, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, false, false));
	}

}
