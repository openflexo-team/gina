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

package org.openflexo.gina.model.container;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.swing.BoxLayout;

import org.openflexo.connie.DataBinding;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBPropertyNotification;
import org.openflexo.gina.model.widget.FIBImage.Align;
import org.openflexo.gina.model.widget.FIBImage.SizeAdjustment;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;

/**
 * Represents a basic panel, as a container of some children component, with a given layout, and a border
 *
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(FIBPanel.FIBPanelImpl.class)
@XMLElement(xmlTag = "Panel")
public interface FIBPanel extends FIBContainer {

	public static enum Layout {
		none, flow, border, grid, box, twocols, gridbag, buttons, split;
	}

	public static enum FlowLayoutAlignment {
		LEFT {
			@Override
			public int getAlign() {
				return FlowLayout.LEFT;
			}
		},
		RIGHT {
			@Override
			public int getAlign() {
				return FlowLayout.RIGHT;
			}
		},
		CENTER {
			@Override
			public int getAlign() {
				return FlowLayout.CENTER;
			}
		},
		LEADING {
			@Override
			public int getAlign() {
				return FlowLayout.LEADING;
			}
		},
		TRAILING {
			@Override
			public int getAlign() {
				return FlowLayout.TRAILING;
			}
		};

		public abstract int getAlign();
	}

	public static enum BoxLayoutAxis {
		X_AXIS {
			@Override
			public int getAxis() {
				return BoxLayout.X_AXIS;
			}
		},
		Y_AXIS {
			@Override
			public int getAxis() {
				return BoxLayout.Y_AXIS;
			}
		};

		public abstract int getAxis();
	}

	public static enum Border {
		empty, line, etched, raised, lowered, titled, rounded3d
	}

	@PropertyIdentifier(type = Layout.class)
	public static final String LAYOUT_KEY = "layout";
	@PropertyIdentifier(type = Integer.class)
	public static final String H_GAP_KEY = "hGap";
	@PropertyIdentifier(type = Integer.class)
	public static final String V_GAP_KEY = "vGap";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLS_KEY = "cols";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROWS_KEY = "rows";
	@PropertyIdentifier(type = FlowLayoutAlignment.class)
	public static final String FLOW_ALIGNMENT_KEY = "flowAlignment";
	@PropertyIdentifier(type = BoxLayoutAxis.class)
	public static final String BOX_LAYOUT_AXIS_KEY = "boxLayoutAxis";
	@PropertyIdentifier(type = Border.class)
	public static final String BORDER_KEY = "border";
	@PropertyIdentifier(type = Color.class)
	public static final String BORDER_COLOR_KEY = "borderColor";
	@PropertyIdentifier(type = String.class)
	public static final String BORDER_TITLE_KEY = "borderTitle";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_TOP_KEY = "borderTop";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_BOTTOM_KEY = "borderBottom";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_LEFT_KEY = "borderLeft";
	@PropertyIdentifier(type = Integer.class)
	public static final String BORDER_RIGHT_KEY = "borderRight";
	@PropertyIdentifier(type = Font.class)
	public static final String TITLE_FONT_KEY = "titleFont";
	@PropertyIdentifier(type = int.class)
	public static final String DARK_LEVEL_KEY = "darkLevel";
	@PropertyIdentifier(type = boolean.class)
	public static final String PROTECT_CONTENT_KEY = "protectContent";
	@PropertyIdentifier(type = boolean.class)
	public static final String TRACK_VIEW_PORT_WIDTH_KEY = "trackViewPortWidth";
	@PropertyIdentifier(type = boolean.class)
	public static final String TRACK_VIEW_PORT_HEIGHT_KEY = "trackViewPortHeight";

	// Background image
	@PropertyIdentifier(type = Resource.class)
	public static final String IMAGE_FILE_KEY = "imageFile";
	@PropertyIdentifier(type = SizeAdjustment.class)
	public static final String SIZE_ADJUSTMENT_KEY = "sizeAdjustment";
	@PropertyIdentifier(type = Align.class)
	public static final String ALIGN_KEY = "align";
	@PropertyIdentifier(type = Integer.class)
	public static final String IMAGE_WIDTH_KEY = "imageWidth";
	@PropertyIdentifier(type = Integer.class)
	public static final String IMAGE_HEIGHT_KEY = "imageHeight";
	@PropertyIdentifier(type = Image.class)
	public static final String DYNAMIC_BACKGROUND_IMAGE_KEY = "dynamicBackgroundImage";

	@Override
	@Getter(value = LAYOUT_KEY)
	@XMLAttribute
	public Layout getLayout();

	@Setter(LAYOUT_KEY)
	public void setLayout(Layout layout);

	@Getter(value = H_GAP_KEY)
	@XMLAttribute
	public Integer getHGap();

	@Setter(H_GAP_KEY)
	public void setHGap(Integer hGap);

	@Getter(value = V_GAP_KEY)
	@XMLAttribute
	public Integer getVGap();

	@Setter(V_GAP_KEY)
	public void setVGap(Integer vGap);

	@Getter(value = COLS_KEY)
	@XMLAttribute
	public Integer getCols();

	@Setter(COLS_KEY)
	public void setCols(Integer cols);

	@Getter(value = ROWS_KEY)
	@XMLAttribute
	public Integer getRows();

	@Setter(ROWS_KEY)
	public void setRows(Integer rows);

	@Getter(value = FLOW_ALIGNMENT_KEY)
	@XMLAttribute
	public FlowLayoutAlignment getFlowAlignment();

	@Setter(FLOW_ALIGNMENT_KEY)
	public void setFlowAlignment(FlowLayoutAlignment flowAlignment);

	@Getter(value = BOX_LAYOUT_AXIS_KEY)
	@XMLAttribute
	public BoxLayoutAxis getBoxLayoutAxis();

	@Setter(BOX_LAYOUT_AXIS_KEY)
	public void setBoxLayoutAxis(BoxLayoutAxis boxLayoutAxis);

	@Getter(value = BORDER_KEY)
	@XMLAttribute
	public Border getBorder();

	@Setter(BORDER_KEY)
	public void setBorder(Border border);

	@Getter(value = BORDER_COLOR_KEY)
	@XMLAttribute
	public Color getBorderColor();

	@Setter(BORDER_COLOR_KEY)
	public void setBorderColor(Color borderColor);

	@Getter(value = BORDER_TITLE_KEY)
	@XMLAttribute
	public String getBorderTitle();

	@Setter(BORDER_TITLE_KEY)
	public void setBorderTitle(String borderTitle);

	@Getter(value = BORDER_TOP_KEY)
	@XMLAttribute
	public Integer getBorderTop();

	@Setter(BORDER_TOP_KEY)
	public void setBorderTop(Integer borderTop);

	@Getter(value = BORDER_BOTTOM_KEY)
	@XMLAttribute
	public Integer getBorderBottom();

	@Setter(BORDER_BOTTOM_KEY)
	public void setBorderBottom(Integer borderBottom);

	@Getter(value = BORDER_LEFT_KEY)
	@XMLAttribute
	public Integer getBorderLeft();

	@Setter(BORDER_LEFT_KEY)
	public void setBorderLeft(Integer borderLeft);

	@Getter(value = BORDER_RIGHT_KEY)
	@XMLAttribute
	public Integer getBorderRight();

	@Setter(BORDER_RIGHT_KEY)
	public void setBorderRight(Integer borderRight);

	@Getter(value = TITLE_FONT_KEY)
	@XMLAttribute
	public Font getTitleFont();

	@Setter(TITLE_FONT_KEY)
	public void setTitleFont(Font titleFont);

	@Getter(value = DARK_LEVEL_KEY, defaultValue = "0")
	@XMLAttribute
	public int getDarkLevel();

	@Setter(DARK_LEVEL_KEY)
	public void setDarkLevel(int darkLevel);

	@Getter(value = PROTECT_CONTENT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getProtectContent();

	@Setter(PROTECT_CONTENT_KEY)
	public void setProtectContent(boolean protectContent);

	@Getter(value = TRACK_VIEW_PORT_WIDTH_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isTrackViewPortWidth();

	@Setter(TRACK_VIEW_PORT_WIDTH_KEY)
	public void setTrackViewPortWidth(boolean trackViewPortWidth);

	@Getter(value = TRACK_VIEW_PORT_HEIGHT_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isTrackViewPortHeight();

	@Setter(TRACK_VIEW_PORT_HEIGHT_KEY)
	public void setTrackViewPortHeight(boolean trackViewPortHeight);

	// Background image

	@Getter(value = DYNAMIC_BACKGROUND_IMAGE_KEY)
	@XMLAttribute
	public DataBinding<Image> getDynamicBackgroundImage();

	@Setter(DYNAMIC_BACKGROUND_IMAGE_KEY)
	public void setDynamicBackgroundImage(DataBinding<Image> dynamicImage);

	@Getter(value = IMAGE_FILE_KEY, isStringConvertable = true)
	@XMLAttribute
	public Resource getImageFile();

	@Setter(IMAGE_FILE_KEY)
	public void setImageFile(Resource imageFile);

	// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
	public File getImageActualFile();

	// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
	public void setImageActualFile(File file) throws MalformedURLException, LocatorNotFoundException;

	@Getter(value = SIZE_ADJUSTMENT_KEY)
	@XMLAttribute
	public SizeAdjustment getSizeAdjustment();

	@Setter(SIZE_ADJUSTMENT_KEY)
	public void setSizeAdjustment(SizeAdjustment sizeAdjustment);

	@Getter(value = ALIGN_KEY)
	@XMLAttribute
	public Align getAlign();

	@Setter(ALIGN_KEY)
	public void setAlign(Align align);

	@Getter(value = IMAGE_WIDTH_KEY)
	@XMLAttribute
	public Integer getImageWidth();

	@Setter(IMAGE_WIDTH_KEY)
	public void setImageWidth(Integer imageWidth);

	@Getter(value = IMAGE_HEIGHT_KEY)
	@XMLAttribute
	public Integer getImageHeight();

	@Setter(IMAGE_HEIGHT_KEY)
	public void setImageHeight(Integer imageHeight);

	public static abstract class FIBPanelImpl extends FIBContainerImpl implements FIBPanel {

		private static final Logger logger = Logger.getLogger(FIBPanel.class.getPackage().getName());

		private Layout layout;

		private FlowLayoutAlignment flowAlignment = null;
		private BoxLayoutAxis boxLayoutAxis = null;

		private Integer hGap = null;
		private Integer vGap = null;

		private Integer cols = null;
		private Integer rows = null;

		private Border border = Border.empty;
		private Color borderColor = null;
		private String borderTitle = null;
		private Integer borderTop = 0;
		private Integer borderBottom = 0;
		private Integer borderLeft = 0;
		private Integer borderRight = 0;

		private Font titleFont = null;
		private int darkLevel = 0;

		private boolean trackViewPortWidth = true;
		private boolean trackViewPortHeight = true;

		private boolean protectContent = false;

		private Resource imageFile;
		private Align align = Align.left;
		private Integer imageWidth;
		private Integer imageHeight;
		private SizeAdjustment sizeAdjustment = SizeAdjustment.OriginalSize;
		private DataBinding<Image> dynamicBackgroundImage;

		public FIBPanelImpl() {
			super();
			layout = Layout.none;
		}

		/*@Override
		public String getIdentifier() {
			return null;
		}*/

		@Override
		public Layout getLayout() {
			return layout;
		}

		@Override
		public void setLayout(Layout layout) {

			FIBPropertyNotification<Layout> notification = requireChange(LAYOUT_KEY, layout);
			if (notification != null && layout != null) {
				this.layout = layout;
				switch (layout) {
					case none:
						break;
					case flow:
						if (flowAlignment == null) {
							flowAlignment = FlowLayoutAlignment.LEADING;
						}
						if (hGap == null) {
							hGap = 5;
						}
						if (vGap == null) {
							vGap = 5;
						}
						break;
					case grid:
						if (hGap == null) {
							hGap = 5;
						}
						if (vGap == null) {
							vGap = 5;
						}
						if (rows == null) {
							rows = 2;
						}
						if (cols == null) {
							cols = 2;
						}
						break;
					case buttons:
						if (hGap == null) {
							hGap = 5;
						}
						if (vGap == null) {
							vGap = 5;
						}
						break;
					case box:
						if (boxLayoutAxis == null) {
							boxLayoutAxis = BoxLayoutAxis.X_AXIS;
						}
						break;
					case border:
						break;
					case twocols:
						break;
					case gridbag:
						break;

					default:
						break;
				}

				// Here we MUST mutate layout constraints for all children, otherwise ClassCastException will arise
				for (FIBComponent child : getSubComponents()) {
					// System.out.println("PANEL: child with constraints " + child.getConstraints());
					child.normalizeConstraintsWhenRequired();
					// System.out.println("PANEL: child with constraints " + child.getConstraints());
				}

				hasChanged(notification);
			}
		}

		@Override
		public Border getBorder() {
			return border;
		}

		@Override
		public void setBorder(Border border) {
			FIBPropertyNotification<Border> notification = requireChange(BORDER_KEY, border);
			if (notification != null && border != null) {
				this.border = border;
				switch (border) {
					case line:
						if (borderColor == null) {
							borderColor = Color.BLACK;
						}
						break;
					case titled:
						if (borderTitle == null) {
							borderTitle = "Panel";
						}
						break;
					case rounded3d:
						if (borderTop == null) {
							borderTop = 2;
						}
						if (borderBottom == null) {
							borderBottom = 2;
						}
						if (borderLeft == null) {
							borderRight = 2;
						}
						if (borderRight == null) {
							borderRight = 2;
						}
						break;

					default:
						break;
				}
				hasChanged(notification);
			}
		}

		@Override
		public Integer getHGap() {
			return hGap;
		}

		@Override
		public void setHGap(Integer hGap) {
			FIBPropertyNotification<Integer> notification = requireChange(H_GAP_KEY, hGap);
			if (notification != null) {
				this.hGap = hGap;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getVGap() {
			return vGap;
		}

		@Override
		public void setVGap(Integer vGap) {
			FIBPropertyNotification<Integer> notification = requireChange(V_GAP_KEY, vGap);
			if (notification != null) {
				this.vGap = vGap;
				hasChanged(notification);
			}
		}

		@Override
		public FlowLayoutAlignment getFlowAlignment() {
			return flowAlignment;
		}

		@Override
		public void setFlowAlignment(FlowLayoutAlignment flowAlignment) {
			FIBPropertyNotification<FlowLayoutAlignment> notification = requireChange(FLOW_ALIGNMENT_KEY, flowAlignment);
			if (notification != null) {
				this.flowAlignment = flowAlignment;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getCols() {
			return cols;
		}

		@Override
		public void setCols(Integer cols) {
			// logger.info("setCols with "+cols);
			FIBPropertyNotification<Integer> notification = requireChange(COLS_KEY, cols);
			if (notification != null) {
				this.cols = cols;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getRows() {
			return rows;
		}

		@Override
		public void setRows(Integer rows) {
			// logger.info("setRows with "+rows);
			FIBPropertyNotification<Integer> notification = requireChange(ROWS_KEY, rows);
			if (notification != null) {
				this.rows = rows;
				hasChanged(notification);
			}
		}

		@Override
		public Color getBorderColor() {
			return borderColor;
		}

		@Override
		public void setBorderColor(Color borderColor) {
			FIBPropertyNotification<Color> notification = requireChange(BORDER_COLOR_KEY, borderColor);
			if (notification != null) {
				this.borderColor = borderColor;
				hasChanged(notification);
			}
		}

		@Override
		public String getBorderTitle() {
			return borderTitle;
		}

		@Override
		public void setBorderTitle(String borderTitle) {
			FIBPropertyNotification<String> notification = requireChange(BORDER_TITLE_KEY, borderTitle);
			if (notification != null) {
				this.borderTitle = borderTitle;
				hasChanged(notification);
			}
		}

		@Override
		public BoxLayoutAxis getBoxLayoutAxis() {
			return boxLayoutAxis;
		}

		@Override
		public void setBoxLayoutAxis(BoxLayoutAxis boxLayoutAxis) {
			FIBPropertyNotification<BoxLayoutAxis> notification = requireChange(BOX_LAYOUT_AXIS_KEY, boxLayoutAxis);
			if (notification != null) {
				this.boxLayoutAxis = boxLayoutAxis;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getProtectContent() {
			return protectContent;
		}

		@Override
		public void setProtectContent(boolean protectContent) {
			FIBPropertyNotification<Boolean> notification = requireChange(PROTECT_CONTENT_KEY, protectContent);
			if (notification != null) {
				this.protectContent = protectContent;
				hasChanged(notification);
			}

		}

		@Override
		public Integer getBorderTop() {
			return borderTop;
		}

		@Override
		public void setBorderTop(Integer borderTop) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_TOP_KEY, borderTop);
			if (notification != null) {
				this.borderTop = borderTop;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getBorderBottom() {
			return borderBottom;
		}

		@Override
		public void setBorderBottom(Integer borderBottom) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_BOTTOM_KEY, borderBottom);
			if (notification != null) {
				this.borderBottom = borderBottom;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getBorderLeft() {
			return borderLeft;
		}

		@Override
		public void setBorderLeft(Integer borderLeft) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_LEFT_KEY, borderLeft);
			if (notification != null) {
				this.borderLeft = borderLeft;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getBorderRight() {
			return borderRight;
		}

		@Override
		public void setBorderRight(Integer borderRight) {
			FIBPropertyNotification<Integer> notification = requireChange(BORDER_RIGHT_KEY, borderRight);
			if (notification != null) {
				this.borderRight = borderRight;
				hasChanged(notification);
			}
		}

		@Override
		public Font getTitleFont() {
			if (titleFont == null) {
				return retrieveValidFont();
			}
			return titleFont;
		}

		@Override
		public void setTitleFont(Font titleFont) {
			FIBPropertyNotification<Font> notification = requireChange(TITLE_FONT_KEY, titleFont);
			if (notification != null) {
				this.titleFont = titleFont;
				hasChanged(notification);
			}
		}

		@Override
		public int getDarkLevel() {
			return darkLevel;
		}

		@Override
		public void setDarkLevel(int darkLevel) {
			FIBPropertyNotification<Integer> notification = requireChange(DARK_LEVEL_KEY, darkLevel);
			if (notification != null) {
				this.darkLevel = darkLevel;
				hasChanged(notification);
			}
		}

		@Override
		public boolean isTrackViewPortWidth() {
			return trackViewPortWidth;
		}

		@Override
		public void setTrackViewPortWidth(boolean trackViewPortWidth) {
			FIBPropertyNotification<Boolean> notification = requireChange(TRACK_VIEW_PORT_WIDTH_KEY, trackViewPortWidth);
			if (notification != null) {
				this.trackViewPortWidth = trackViewPortWidth;
				hasChanged(notification);
			}
		}

		@Override
		public boolean isTrackViewPortHeight() {
			return trackViewPortHeight;
		}

		@Override
		public void setTrackViewPortHeight(boolean trackViewPortHeight) {
			FIBPropertyNotification<Boolean> notification = requireChange(TRACK_VIEW_PORT_HEIGHT_KEY, trackViewPortHeight);
			if (notification != null) {
				this.trackViewPortHeight = trackViewPortHeight;
				hasChanged(notification);
			}
		}

		@Override
		public void searchLocalized(LocalizationEntryRetriever retriever) {
			super.searchLocalized(retriever);
			if (getBorder() == Border.titled) {
				retriever.foundLocalized(getBorderTitle());
			}
		}

		@Override
		public DataBinding<Image> getDynamicBackgroundImage() {

			if (dynamicBackgroundImage == null) {
				dynamicBackgroundImage = new DataBinding<>(this, Image.class, DataBinding.BindingDefinitionType.GET);
				dynamicBackgroundImage.setBindingName("dynamicBackgroundImage");
			}
			return dynamicBackgroundImage;
		}

		@Override
		public void setDynamicBackgroundImage(DataBinding<Image> dynamicBackgroundImage) {

			FIBPropertyNotification<DataBinding<Image>> notification = requireChange(DYNAMIC_BACKGROUND_IMAGE_KEY, dynamicBackgroundImage);
			if (notification != null) {
				if (dynamicBackgroundImage != null) {
					dynamicBackgroundImage.setOwner(this);
					dynamicBackgroundImage.setDeclaredType(Image.class);
					dynamicBackgroundImage.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
					dynamicBackgroundImage.setBindingName("dynamicBackgroundImage");
				}
				this.dynamicBackgroundImage = dynamicBackgroundImage;
				notify(notification);
			}

		}

		@Override
		public Resource getImageFile() {
			return imageFile;
		}

		@Override
		public void setImageFile(Resource imageFile) {
			FIBPropertyNotification<Resource> notification = requireChange(IMAGE_FILE_KEY, imageFile);
			if (notification != null) {
				this.imageFile = imageFile;
				hasChanged(notification);
			}
		}

		// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
		@Override
		public File getImageActualFile() {
			if (imageFile instanceof FileResourceImpl) {
				return ((FileResourceImpl) imageFile).getFile();
			}
			else
				return null;
		}

		// TODO : this is a Workaround for Fib File selector...It has to be fixed in a more efficient way
		@Override
		public void setImageActualFile(File file) throws MalformedURLException, LocatorNotFoundException {

			this.setImageFile(new FileResourceImpl(file));
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
		public SizeAdjustment getSizeAdjustment() {
			return sizeAdjustment;
		}

		@Override
		public void setSizeAdjustment(SizeAdjustment sizeAdjustment) {
			FIBPropertyNotification<SizeAdjustment> notification = requireChange(SIZE_ADJUSTMENT_KEY, sizeAdjustment);
			if (notification != null) {
				this.sizeAdjustment = sizeAdjustment;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getImageWidth() {
			return imageWidth;
		}

		@Override
		public void setImageWidth(Integer imageWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(IMAGE_WIDTH_KEY, imageWidth);
			if (notification != null) {
				this.imageWidth = imageWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getImageHeight() {
			return imageHeight;
		}

		@Override
		public void setImageHeight(Integer imageHeight) {
			FIBPropertyNotification<Integer> notification = requireChange(IMAGE_HEIGHT_KEY, imageHeight);
			if (notification != null) {
				this.imageHeight = imageHeight;
				hasChanged(notification);
			}
		}

	}
}
