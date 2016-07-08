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

package org.openflexo.gina.model.widget;

import java.lang.reflect.Type;

import javax.swing.SwingConstants;

import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(FIBLabel.FIBLabelImpl.class)
@XMLElement(xmlTag = "Label")
public interface FIBLabel extends FIBWidget {

	public static enum Align {
		left {
			@Override
			public int getAlign() {
				return SwingConstants.LEFT;
			}
		},
		right {
			@Override
			public int getAlign() {
				return SwingConstants.RIGHT;
			}
		},
		center {
			@Override
			public int getAlign() {
				return SwingConstants.CENTER;
			}
		};
		public abstract int getAlign();
	}

	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = Align.class)
	public static final String ALIGN_KEY = "align";
	@PropertyIdentifier(type = boolean.class)
	public static final String TRIM_TEXT_KEY = "trimText";

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Getter(value = ALIGN_KEY)
	@XMLAttribute
	public Align getAlign();

	@Setter(ALIGN_KEY)
	public void setAlign(Align align);

	@Getter(value = TRIM_TEXT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getTrimText();

	@Setter(TRIM_TEXT_KEY)
	public void setTrimText(boolean trimText);

	public static abstract class FIBLabelImpl extends FIBWidgetImpl implements FIBLabel {

		private String label;
		private Align align = Align.left;

		public FIBLabelImpl() {
			super();
		}

		public FIBLabelImpl(String label) {
			this();
			this.label = label;
		}

		@Override
		public String getBaseName() {
			if (StringUtils.isNotEmpty(getLabel())) {
				return JavaUtils.getClassName(getLabel()) + (JavaUtils.getClassName(getLabel()).contains("Label") ? "" : "Label");
			}
			return "Label";
		}

		/*@Override
		public String getIdentifier() {
			return getLabel();
		}*/

		@Override
		public Type getDefaultDataType() {
			return String.class;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void setLabel(String label) {
			FIBPropertyNotification<String> notification = requireChange(LABEL_KEY, label);
			if (notification != null) {
				this.label = label;
				hasChanged(notification);
			}
		}

		@Override
		public Align getAlign() {
			return align;
		}

		@Override
		public void setAlign(Align align) {
			FIBPropertyNotification<Align> notification = requireChange(ALIGN_KEY, align);
			if (notification != null) {
				this.align = align;
				hasChanged(notification);
			}
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			super.searchLocalized(retriever);
			retriever.foundLocalized(getLabel());
		}

	}
}
