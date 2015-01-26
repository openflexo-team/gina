/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexographicutils, a component of the software infrastructure 
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

package org.openflexo.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.openflexo.toolbox.FileUtils;
import org.w3c.dom.DOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;

public class ImageUtils {

	public enum ImageType {
		JPG, PNG, GIF, SVG;

		public String getExtension() {
			return name().toLowerCase();
		}
	}

	public static BufferedImage createImageFromComponent(Component componentToPrint) {
		BufferedImage bi = new BufferedImage(componentToPrint.getWidth(), componentToPrint.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bi.createGraphics();
		componentToPrint.print(graphics);
		graphics.dispose();
		return bi;
	}

	public static void saveImageToFile(RenderedImage image, File dest, ImageType type) throws IOException {
		if (type == null) {
			type = ImageType.PNG;
		}
		if (!dest.exists()) {
			FileUtils.createNewFile(dest);
		}
		if(type==ImageType.SVG){
		    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		    String svgNS = "http://www.w3.org/2000/svg";
		    Document document = domImpl.createDocument(svgNS, "svg", null);
		    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		    svgGenerator.drawRenderedImage(image, null);
		    svgGenerator.stream(new FileWriter(dest));
		}
		ImageIO.write(image, type.getExtension(), dest);
	}

	public static BufferedImage loadImageFromFile(File source) {
		try {
			return ImageIO.read(source);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ImageIcon getThumbnail(ImageIcon src, int maxWidth) {
		if (src != null) {
			if (src.getIconWidth() > maxWidth) {
				return new ImageIcon(src.getImage().getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH));
			} else { // no need to miniaturize
				return src;
			}
		}
		return null;
	}

	public static ImageIcon getThumbnail(ImageIcon src, int maxWidth, int maxHeight) {
		if (src != null) {
			double ratio = (double) maxWidth / maxHeight;
			double imageRatio = (double) src.getIconWidth() / src.getIconHeight();
			if (ratio < imageRatio) {
				return new ImageIcon(src.getImage().getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH));
			} else {
				return new ImageIcon(src.getImage().getScaledInstance(-1, maxHeight, Image.SCALE_SMOOTH));
			}

		}
		return null;
	}

	public static ImageIcon resize(ImageIcon src, Dimension size) {
		if (src != null) {
			return new ImageIcon(src.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
		}
		return null;
	}

	public static BufferedImage scaleImage(BufferedImage img, int width, int height) {
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		if (imgWidth * height < imgHeight * width) {
			width = imgWidth * height / imgHeight;
		} else {
			height = imgHeight * width / imgWidth;
		}
		BufferedImage newImage = new BufferedImage(width, height, img.getType());
		Graphics2D g = newImage.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}
		return newImage;
	}
}
