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

import java.awt.Image;
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
import org.openflexo.rm.Resource;

@ModelEntity
@ImplementationClass(FIBImage.FIBImageImpl.class)
@XMLElement(xmlTag = "Image")
public interface FIBImage extends FIBWidget {

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

	public static enum SizeAdjustment {
		OriginalSize, FitToAvailableSize, FitToAvailableSizeRespectRatio, AdjustWidth, AdjustHeight, AdjustDimensions
	}

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

	@Getter(value = IMAGE_FILE_KEY)
	@XMLAttribute
	public Resource getImageFile();

	@Setter(IMAGE_FILE_KEY)
	public void setImageFile(Resource imageFile);

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

	public static abstract class FIBImageImpl extends FIBWidgetImpl implements FIBImage {

		private Resource imageFile;
		private Align align = Align.left;
		private Integer imageWidth;
		private Integer imageHeight;
		private SizeAdjustment sizeAdjustment = SizeAdjustment.OriginalSize;

		@Override
		public String getBaseName() {
			return "Image";
		}

		@Override
		public Type getDefaultDataType() {
			return Image.class;
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
