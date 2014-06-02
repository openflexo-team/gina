package org.openflexo.fib;

import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.fib.model.FIBTable;

final class FIBBindingFactory extends JavaBindingFactory {
	@Override
	public SimplePathElement makeSimplePathElement(BindingPathElement father, String propertyName) {
		// TODO Auto-generated method stub
		SimplePathElement returned = super.makeSimplePathElement(father, propertyName);
		if (propertyName.equals(FIBTable.ITERATOR_NAME)) {
			System.out.println("Tiens, je choppe " + FIBTable.ITERATOR_NAME + " pour " + father);
		}
		return returned;
	}
}