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

package org.openflexo.gina.model.container.layout;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.container.FIBPanel.Layout;

public class BorderLayoutConstraints extends ComponentConstraints {

	private static final Logger logger = Logger.getLogger(FIBComponent.class.getPackage().getName());

	private static final String LOCATION = "location";

	public BorderLayoutLocation getLocation() {
		return getEnumValue(LOCATION, BorderLayoutLocation.class, BorderLayoutLocation.center);
	}

	public void setLocation(BorderLayoutLocation location) {
		setEnumValue(LOCATION, location);
	}

	public static enum BorderLayoutLocation {
		north {
			@Override
			public String getConstraint() {
				return BorderLayout.NORTH;
			}
		},
		south {
			@Override
			public String getConstraint() {
				return BorderLayout.SOUTH;
			}
		},
		east {
			@Override
			public String getConstraint() {
				return BorderLayout.EAST;
			}
		},
		west {
			@Override
			public String getConstraint() {
				return BorderLayout.WEST;
			}
		},
		center {
			@Override
			public String getConstraint() {
				return BorderLayout.CENTER;
			}
		},
		beforeFirstLine {
			@Override
			public String getConstraint() {
				return BorderLayout.BEFORE_FIRST_LINE;
			}
		},
		afterLastLine {
			@Override
			public String getConstraint() {
				return BorderLayout.AFTER_LAST_LINE;
			}
		},
		beforeLineBegins {
			@Override
			public String getConstraint() {
				return BorderLayout.BEFORE_LINE_BEGINS;
			}
		},
		afterLineEnds {
			@Override
			public String getConstraint() {
				return BorderLayout.AFTER_LINE_ENDS;
			}
		},
		pageStart {
			@Override
			public String getConstraint() {
				return BorderLayout.PAGE_START;
			}
		},
		pageEnd {
			@Override
			public String getConstraint() {
				return BorderLayout.PAGE_END;
			}
		},
		lineStart {
			@Override
			public String getConstraint() {
				return BorderLayout.LINE_START;
			}
		},
		lineEnd {
			@Override
			public String getConstraint() {
				return BorderLayout.LINE_END;
			}
		};

		public abstract String getConstraint();
	}

	public BorderLayoutConstraints() {
		super();
	}

	public BorderLayoutConstraints(BorderLayoutLocation location) {
		super();
		setLocation(location);
	}

	public BorderLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	public BorderLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	@Override
	protected Layout getType() {
		return Layout.border;
	}

}
