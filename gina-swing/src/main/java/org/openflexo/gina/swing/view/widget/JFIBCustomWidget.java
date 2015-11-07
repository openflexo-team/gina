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

package org.openflexo.gina.swing.view.widget;

import java.awt.Image;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.controller.FIBSelectable;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomAssignment;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.swing.view.SwingRenderingAdapter;
import org.openflexo.gina.swing.view.widget.JFIBImageWidget.SwingImageRenderingAdapter;
import org.openflexo.gina.view.FIBView.RenderingAdapter;
import org.openflexo.gina.view.impl.FIBWidgetViewImpl;
import org.openflexo.gina.view.widget.FIBImageWidget;
import org.openflexo.gina.view.widget.FIBImageWidget.ImageRenderingAdapter;
import org.openflexo.gina.view.widget.impl.FIBCustomWidgetImpl;
import org.openflexo.kvc.InvalidKeyValuePropertyException;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;

/**
 * Defines an abstract custom widget
 * 
 * @author sguerin
 * 
 */
public class JFIBCustomWidget<J extends JComponent & FIBCustomComponent<T>, T> extends FIBCustomWidgetImpl<J, T> {

	private static final Logger LOGGER = Logger.getLogger(JFIBCustomWidget.class.getPackage().getName());

	/**
	 * A {@link RenderingAdapter} implementation dedicated for Swing custom component<br>
	 * 
	 * @author sylvain
	 * 
	 */
	public static class SwingCustomComponentRenderingAdapter<J extends JComponent,T> extends SwingRenderingAdapter<J>
			implements CustomComponentRenderingAdapter<J,T> {

	}
	
	private final JLabel ERROR_LABEL = new JLabel("<Cannot instanciate component>");

	public JFIBCustomWidget(FIBCustom model, FIBController controller) {
		super(model, controller, new SwingCustomComponentRenderingAdapter<J,T>());
	}




	/*protected class NotFoundComponent implements FIBCustomComponent {

		private JLabel label;

		public NotFoundComponent() {
			label = new JLabel("< Custom component >");
		}

		@Override
		public void init(FIBCustom component, FIBController controller) {
		}

		@Override
		public void addApplyCancelListener(ApplyCancelListener l) {
		}

		@Override
		public Object getEditedObject() {
			return null;
		}

		@Override
		public Class<T> getRepresentedType() {
			return null;
		}

		@Override
		public T getRevertValue() {
			return null;
		}

		@Override
		public void removeApplyCancelListener(ApplyCancelListener l) {
		}

		@Override
		public void setEditedObject(Object object) {
		}

		@Override
		public void setRevertValue(Object object) {
		}

		@Override
		public void delete() {
			label = null;
		}
	}*/




	@Override
	public JComponent getJComponent() {
		return getTechnologyComponent();
	}

}