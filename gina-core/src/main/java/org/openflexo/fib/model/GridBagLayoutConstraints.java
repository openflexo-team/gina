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

package org.openflexo.fib.model;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;

public class GridBagLayoutConstraints extends ComponentConstraints {

	private static final String ANCHOR = "anchor";
	private static final String FILL = "fill";

	public static enum AnchorType {
		north {
			@Override
			public int getAnchor() {
				return GridBagConstraints.NORTH;
			}
		},
		south {
			@Override
			public int getAnchor() {
				return GridBagConstraints.SOUTH;
			}
		},
		east {
			@Override
			public int getAnchor() {
				return GridBagConstraints.EAST;
			}
		},
		west {
			@Override
			public int getAnchor() {
				return GridBagConstraints.WEST;
			}
		},
		center {
			@Override
			public int getAnchor() {
				return GridBagConstraints.CENTER;
			}
		},
		north_east {
			@Override
			public int getAnchor() {
				return GridBagConstraints.NORTHEAST;
			}
		},
		north_west {
			@Override
			public int getAnchor() {
				return GridBagConstraints.NORTHWEST;
			}
		},
		south_east {
			@Override
			public int getAnchor() {
				return GridBagConstraints.SOUTHEAST;
			}
		},
		south_west {
			@Override
			public int getAnchor() {
				return GridBagConstraints.SOUTHWEST;
			}
		};
		public abstract int getAnchor();
	}

	public static enum FillType {
		none {
			@Override
			public int getFill() {
				return GridBagConstraints.NONE;
			}
		},
		horizontal {
			@Override
			public int getFill() {
				return GridBagConstraints.HORIZONTAL;
			}
		},
		vertical {
			@Override
			public int getFill() {
				return GridBagConstraints.VERTICAL;
			}
		},
		both {
			@Override
			public int getFill() {
				return GridBagConstraints.BOTH;
			}
		};
		public abstract int getFill();
	}

	public GridBagLayoutConstraints() {
		super();
	}

	public GridBagLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	public GridBagLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	public GridBagLayoutConstraints(int gridX, int gridY, int gridWidth, int gridHeight, double weightX, double weightY,
			AnchorType anchorType, FillType fillType, int insetsTop, int insetsBottom, int insetsLeft, int insetsRight, int padX, int padY) {
		super();
		setGridX(gridX);
		setGridY(gridY);
		setGridWidth(gridWidth);
		setGridHeight(gridHeight);
		setWeightX(weightX);
		setWeightY(weightY);
		setAnchor(anchorType);
		setFill(fillType);
		setInsetsTop(insetsTop);
		setInsetsBottom(insetsBottom);
		setInsetsLeft(insetsLeft);
		setInsetsRight(insetsRight);
		setPadX(padX);
		setPadY(padY);
	}

	@Override
	protected Layout getType() {
		return Layout.gridbag;
	}

	@Override
	public void performConstrainedAddition(JComponent container, JComponent contained) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = getGridX();
		c.gridy = getGridY();
		c.gridwidth = getGridWidth();
		c.gridheight = getGridHeight();

		c.weightx = getWeightX();
		c.weighty = getWeightY();
		c.anchor = getAnchor().getAnchor();
		c.fill = getFill().getFill();

		c.insets = new Insets(getInsetsTop(), getInsetsLeft(), getInsetsBottom(), getInsetsRight());
		c.ipadx = getPadX();
		c.ipady = getPadY();

		container.add(contained, c);
	}

	public AnchorType getAnchor() {
		return getEnumValue(ANCHOR, AnchorType.class, AnchorType.center);
	}

	public void setAnchor(AnchorType location) {
		setEnumValue(ANCHOR, location);
	}

	public FillType getFill() {
		return getEnumValue(FILL, FillType.class, FillType.none);
	}

	public void setFill(FillType fill) {
		setEnumValue(FILL, fill);
	}

	private static final String GRID_X = "gridX";
	private static final String GRID_Y = "gridY";

	public int getGridX() {
		return getIntValue(GRID_X, GridBagConstraints.RELATIVE);
	}

	public void setGridX(int gridX) {
		setIntValue(GRID_X, gridX);
	}

	public boolean getGridXRelative() {
		return getGridX() == GridBagConstraints.RELATIVE;
	}

	public void setGridXRelative(boolean flag) {
		if (flag) {
			setGridX(GridBagConstraints.RELATIVE);
		} else {
			setGridX(0);
		}
	}

	public int getGridY() {
		return getIntValue(GRID_Y, GridBagConstraints.RELATIVE);
	}

	public void setGridY(int gridY) {
		setIntValue(GRID_Y, gridY);
	}

	public boolean getGridYRelative() {
		return getGridY() == GridBagConstraints.RELATIVE;
	}

	public void setGridYRelative(boolean flag) {
		if (flag) {
			setGridY(GridBagConstraints.RELATIVE);
		} else {
			setGridY(0);
		}
	}

	private static final String GRID_WIDTH = "gridWidth";
	private static final String GRID_HEIGHT = "gridHeight";

	public int getGridWidth() {
		return getIntValue(GRID_WIDTH, 1);
	}

	public void setGridWidth(int gridWidth) {
		setIntValue(GRID_WIDTH, gridWidth);
	}

	public boolean getGridWidthRelative() {
		return getGridWidth() == GridBagConstraints.RELATIVE;
	}

	public void setGridWidthRelative(boolean flag) {
		if (flag) {
			setGridWidth(GridBagConstraints.RELATIVE);
		} else {
			setGridWidth(1);
		}
	}

	public boolean getGridWidthRemainder() {
		return getGridWidth() == GridBagConstraints.REMAINDER;
	}

	public void setGridWidthRemainder(boolean flag) {
		if (flag) {
			setGridWidth(GridBagConstraints.REMAINDER);
		} else {
			setGridWidth(1);
		}
	}

	public int getGridHeight() {
		return getIntValue(GRID_HEIGHT, 1);
	}

	public void setGridHeight(int gridHeight) {
		setIntValue(GRID_HEIGHT, gridHeight);
	}

	public boolean getGridHeightRelative() {
		return getGridHeight() == GridBagConstraints.RELATIVE;
	}

	public void setGridHeightRelative(boolean flag) {
		if (flag) {
			setGridHeight(GridBagConstraints.RELATIVE);
		} else {
			setGridHeight(1);
		}
	}

	public boolean getGridHeightRemainder() {
		return getGridHeight() == GridBagConstraints.REMAINDER;
	}

	public void setGridHeightRemainder(boolean flag) {
		if (flag) {
			setGridHeight(GridBagConstraints.REMAINDER);
		} else {
			setGridHeight(1);
		}
	}

	private static final String WEIGHT_X = "weightX";
	private static final String WEIGHT_Y = "weightY";

	public double getWeightX() {
		return getDoubleValue(WEIGHT_X, 0);
	}

	public void setWeightX(double weightX) {
		setDoubleValue(WEIGHT_X, weightX);
	}

	public double getWeightY() {
		return getDoubleValue(WEIGHT_Y, 0);
	}

	public void setWeightY(double weightY) {
		setDoubleValue(WEIGHT_Y, weightY);
	}

	private static final String PAD_X = "padX";
	private static final String PAD_Y = "padY";

	public int getPadX() {
		return getIntValue(PAD_X, 0);
	}

	public void setPadX(int padX) {
		setIntValue(PAD_X, padX);
	}

	public int getPadY() {
		return getIntValue(PAD_Y, 0);
	}

	public void setPadY(int padY) {
		setIntValue(PAD_Y, padY);
	}

	private static final String INSETS_TOP = "insetsTop";
	private static final String INSETS_BOTTOM = "insetsBottom";
	private static final String INSETS_LEFT = "insetsLeft";
	private static final String INSETS_RIGHT = "insetsRight";

	public int getInsetsTop() {
		return getIntValue(INSETS_TOP, 0);
	}

	public void setInsetsTop(int insetsTop) {
		setIntValue(INSETS_TOP, insetsTop);
	}

	public int getInsetsBottom() {
		return getIntValue(INSETS_BOTTOM, 0);
	}

	public void setInsetsBottom(int insetsBottom) {
		setIntValue(INSETS_BOTTOM, insetsBottom);
	}

	public int getInsetsLeft() {
		return getIntValue(INSETS_LEFT, 0);
	}

	public void setInsetsLeft(int insetsLeft) {
		setIntValue(INSETS_LEFT, insetsLeft);
	}

	public int getInsetsRight() {
		return getIntValue(INSETS_RIGHT, 0);
	}

	public void setInsetsRight(int insetsRight) {
		setIntValue(INSETS_RIGHT, insetsRight);
	}

}
