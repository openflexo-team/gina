/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
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

package org.openflexo.icon;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ImageIcon;

public class IconFactory {

	// icons with marker

	private static ImageIcon mergeImageIcon(ImageIcon background, IconMarker marker) {
		ImageIcon foreground = marker.getImage();
		int posX = marker.getPX();
		int posY = marker.getPY();
		int newWidth = Math.max(background.getIconWidth(), posX + foreground.getIconWidth());
		int newHeight = Math.max(background.getIconHeight(), posY + foreground.getIconHeight());
		BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		Image img1 = background.getImage();
		Image img2 = foreground.getImage();
		/*	    g.setColor(Color.WHITE);
			    g.fillRect(0,0,newWidth,newHeight);*/
		g.drawImage(img1, 0, 0, null, null);
		g.drawImage(img2, posX, posY, null, null);
		g.dispose();
		return new ImageIcon(result);
	}

	private static final Hashtable<ImageIcon, Hashtable<Long, ImageIcon>> _storedIcons = new Hashtable<>();

	public static ImageIcon getImageIcon(ImageIcon icon, IconMarker... iconMarkers) {
		if (icon == null) {
			return null;
		}
		Hashtable<Long, ImageIcon> knownIcons = _storedIcons.get(icon);
		if (knownIcons == null) {
			knownIcons = new Hashtable<>();
			_storedIcons.put(icon, knownIcons);
		}
		long code = 0;
		for (int i = 0; i < iconMarkers.length; i++) {
			code += iconMarkers[i].getID();
		}
		ImageIcon returned = knownIcons.get(code);
		if (returned == null) {
			// logger.info("Compute icon " + code);
			ImageIcon resultImage = icon;
			for (int i = 0; i < iconMarkers.length; i++) {
				resultImage = mergeImageIcon(resultImage, iconMarkers[i]);
			}
			knownIcons.put(code, resultImage);
			returned = resultImage;
		}
		else {
			// logger.info("Retrieve icon "+code);
		}
		return returned;
	}

	// disabled icons

	private static Hashtable<ImageIcon, ImageIcon> _disabledIcons;

	public static ImageIcon getDisabledIcon(ImageIcon imageIcon) {
		if (_disabledIcons == null) {
			_disabledIcons = new Hashtable<>();
		}
		ImageIcon returned = _disabledIcons.get(imageIcon);
		if (returned == null) {
			returned = buildDisabledIcon(imageIcon);
			_disabledIcons.put(imageIcon, returned);
		}
		return returned;
	}

	private static ImageIcon buildDisabledIcon(ImageIcon imageIcon) {
		BufferedImage result = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = result.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.setPaint(Color.WHITE);
		g.drawImage(imageIcon.getImage(), 0, 0, Color.WHITE, null);
		g.dispose();
		return new ImageIcon(result);
	}
}
