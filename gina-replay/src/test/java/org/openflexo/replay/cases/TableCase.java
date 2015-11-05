/**
 * HIM Recording example of a simple gina interface
 * 
 * @author Alexandre
 *
 */

package org.openflexo.replay.cases;

import java.awt.Dimension;
import java.util.List;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints;
import org.openflexo.fib.model.container.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.fib.model.widget.FIBDropDownColumn;
import org.openflexo.fib.model.widget.FIBLabelColumn;
import org.openflexo.fib.model.widget.FIBNumberColumn;
import org.openflexo.fib.model.widget.FIBTable;
import org.openflexo.fib.model.widget.FIBTextFieldColumn;
import org.openflexo.fib.model.widget.FIBNumber.NumberType;
import org.openflexo.replay.sampleData.Family;
import org.openflexo.replay.sampleData.Gender;
//import org.openflexo.replay.sampleData.Family.Gender;
import org.openflexo.replay.sampleData.Person;
import org.openflexo.replay.utils.Case;
import org.openflexo.replay.utils.GraphicalContextDelegate;
import org.openflexo.replay.utils.Window;

public class TableCase extends Case {

	private FIBTable table;
	private static Family family;
	
	public static void main(String[] args) {
		//initExecutor(1);
		initCase(new TableCase());
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
		table = GraphicalContextDelegate.getFactory().newInstance(FIBTable.class);
		table.setData(new DataBinding<List<?>>("data.children", table, List.class, BindingDefinitionType.GET));
		table.setAutoSelectFirstRow(true);
		table.setIteratorClass(Person.class);
		table.setBoundToSelectionManager(true);
		table.setName("table");

		FIBTextFieldColumn c1 = GraphicalContextDelegate.getFactory().newInstance(FIBTextFieldColumn.class);
		c1.setData(new DataBinding<String>("iterator.firstName", c1, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c1);
		FIBTextFieldColumn c2 = GraphicalContextDelegate.getFactory().newInstance(FIBTextFieldColumn.class);
		c2.setData(new DataBinding<String>("iterator.lastName", c2, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c2);
		FIBNumberColumn c3 = GraphicalContextDelegate.getFactory().newInstance(FIBNumberColumn.class);
		c3.setNumberType(NumberType.IntegerType);
		c3.setData(new DataBinding<Integer>("iterator.age", c3, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c3);
		FIBDropDownColumn c4 = GraphicalContextDelegate.getFactory().newInstance(FIBDropDownColumn.class);
		c4.setData(new DataBinding<Gender>("iterator.gender", c4, String.class, BindingDefinitionType.GET_SET));
		table.addToColumns(c4);
		FIBLabelColumn c5 = GraphicalContextDelegate.getFactory().newInstance(FIBLabelColumn.class);
		c5.setData(new DataBinding<String>("iterator.toString", c5, String.class, BindingDefinitionType.GET));
		table.addToColumns(c5);

		w.getComponent().addToSubComponents(table, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, true));
	}

}
