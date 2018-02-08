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

package org.openflexo.gina.swing.editor.view.container;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.gina.model.FIBModelObject;
import org.openflexo.gina.model.bindings.RuntimeContext;
import org.openflexo.gina.model.operator.FIBIteration;
import org.openflexo.gina.swing.editor.controller.FIBEditorController;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerView;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableContainerViewDelegate;
import org.openflexo.gina.swing.editor.view.FIBSwingEditableView;
import org.openflexo.gina.swing.editor.view.OperatorDecorator;
import org.openflexo.gina.swing.editor.view.PlaceHolder;
import org.openflexo.gina.swing.view.container.JFIBIterationView;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public class JFIBEditableIterationView extends JFIBIterationView implements FIBSwingEditableContainerView<FIBIteration, JPanel> {

	private static final Logger logger = FlexoLogger.getLogger(JFIBEditableIterationView.class.getPackage().getName());

	private final FIBSwingEditableContainerViewDelegate<FIBIteration, JPanel> delegate;

	private final FIBEditorController editorController;

	@Override
	public JComponent getDraggableComponent() {
		return getTechnologyComponent();
	}

	@Override
	public FIBEditorController getEditorController() {
		return editorController;
	}

	public JFIBEditableIterationView(final FIBIteration model, FIBEditorController editorController, RuntimeContext context) {
		super(model, editorController.getController(), context);
		this.editorController = editorController;
		delegate = new FIBSwingEditableContainerViewDelegate<>(this);
	}

	@Override
	public boolean handleIteration() {
		// In edit mode, we don't want to have to iterate
		return false;
	}

	private JLabel iterationInfoLabel;

	@Override
	protected JPanel makeTechnologyComponent() {
		if (getComponent().getSubComponents().size() == 0) {
			JPanel returned = new JPanel(new BorderLayout());
			returned.add(iterationInfoLabel = new JLabel(
					(StringUtils.isNotEmpty(getComponent().getName()) ? getComponent().getName() : "Iteration")));
			returned.add(iterationInfoLabel, BorderLayout.CENTER);
			returned.setBorder(BorderFactory.createEmptyBorder(0, FIBSwingEditableView.OPERATOR_ICON_SPACE, 0, 0));
			return returned;
		}
		else {
			return null;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FIBModelObject.NAME_KEY)) {
			iterationInfoLabel.setText(StringUtils.isNotEmpty(getComponent().getName()) ? getComponent().getName() : "Iteration");
		}
		super.propertyChange(evt);
	}

	@Override
	public JComponent getJComponent() {
		return getTechnologyComponent();
	}

	@Override
	public JComponent getResultingJComponent() {
		return getTechnologyComponent();
	}

	@Override
	public void delete() {
		delegate.delete();
		super.delete();
	}

	@Override
	public FIBSwingEditableContainerViewDelegate<FIBIteration, JPanel> getDelegate() {
		return delegate;
	}

	@Override
	protected void paintAdditionalInfo(Graphics g) {
		if (getDelegate().getPlaceholders() != null) {
			for (PlaceHolder ph : getDelegate().getPlaceholders()) {
				ph.paint(g);
			}
		}
	}

	/*@Override
	public FIBLayoutManager<JPanel, JComponent, ?> makeFIBLayoutManager(Layout layoutType) {
		if (layoutType == null) {
			return super.makeFIBLayoutManager(layoutType);
		}
		switch (layoutType) {
			case none:
				return new JAbsolutePositionningLayout(this);
			case border:
				return new JEditableBorderLayout(this);
			case box:
				return new JEditableBoxLayout(this);
			case flow:
				return new JEditableFlowLayout(this);
			case buttons:
				return new JButtonLayout(this);
			case twocols:
				return new JEditableTwoColsLayout(this);
			case grid:
				return new JEditableGridLayout(this);
			case gridbag:
				return new JEditableGridBagLayout(this);
			default:
				return super.makeFIBLayoutManager(layoutType);
		}
	
	}*/

	/*@Override
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize) {
	
		List<PlaceHolder> returned = new ArrayList<>();
		returned.add(new PlaceHolder(this, getComponent().getName() != null ? getComponent().getName() : "Iteration") {
	
			@Override
			public void insertComponent(FIBComponent newComponent, int originalIndex) {
				System.out.println("Adding inside iteration");
				getComponent().addToSubComponents(newComponent);
			}
	
		});
	
		return returned;
		// return Collections.emptyList();
	}*/

	@Override
	public List<PlaceHolder> makePlaceHolders(Dimension preferredSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OperatorDecorator> makeOperatorDecorators() {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean operatorContentsStart = false;

	// TODO: avoid code duplication in FIBSwingEditableView
	@Override
	public boolean isOperatorContentsStart() {
		return operatorContentsStart;
	}

	// TODO: avoid code duplication in FIBSwingEditableView
	@Override
	public void setOperatorContentsStart(boolean operatorContentsStart) {
		if (operatorContentsStart != this.operatorContentsStart) {
			this.operatorContentsStart = operatorContentsStart;
			FIBSwingEditableView.updateOperatorContentsStart(this, operatorContentsStart);
			getPropertyChangeSupport().firePropertyChange("operatorContentsStart", !operatorContentsStart, operatorContentsStart);
		}
	}

}
