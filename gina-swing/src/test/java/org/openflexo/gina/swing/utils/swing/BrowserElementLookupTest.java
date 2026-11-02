/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.gina.swing.utils.swing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openflexo.gina.model.widget.FIBBrowser;
import org.openflexo.gina.model.widget.FIBBrowserElement;
import org.openflexo.gina.test.FIBTestCase;

/**
 * The element of a browser chosen for an object is the one of its most specialized declared type, whatever the lookups done before
 * (GINA-D-5).<br>
 * The classes mimic the PAMELA ones: interfaces of the model, a chain of implementation classes, and a proxy class. Looking up a
 * picture first used to cache its implementation classes in the table of the declared types, where they then competed with the
 * declared interfaces: a text box got the element of a shape or of a text shape depending on the order of a hash table. Six
 * independent hierarchies make the former behaviour fail almost always.
 * 
 * @author sylvain
 *
 */
public class BrowserElementLookupTest extends FIBTestCase {

	public interface Shape0 {
	}

	public interface TextShape0 extends Shape0 {
	}

	public interface TextBox0 extends TextShape0 {
	}

	public interface Picture0 extends Shape0 {
	}

	public static abstract class ShapeImpl0 implements Shape0 {
	}

	public static abstract class SimpleShapeImpl0 extends ShapeImpl0 {
	}

	public static abstract class TextShapeImpl0 extends SimpleShapeImpl0 implements TextShape0 {
	}

	public static abstract class TextBoxImpl0 extends TextShapeImpl0 implements TextBox0 {
	}

	public static class TextBoxProxy0 extends TextBoxImpl0 implements TextBox0 {
	}

	public static abstract class PictureImpl0 extends SimpleShapeImpl0 implements Picture0 {
	}

	public static class PictureProxy0 extends PictureImpl0 implements Picture0 {
	}

	public interface Shape1 {
	}

	public interface TextShape1 extends Shape1 {
	}

	public interface TextBox1 extends TextShape1 {
	}

	public interface Picture1 extends Shape1 {
	}

	public static abstract class ShapeImpl1 implements Shape1 {
	}

	public static abstract class SimpleShapeImpl1 extends ShapeImpl1 {
	}

	public static abstract class TextShapeImpl1 extends SimpleShapeImpl1 implements TextShape1 {
	}

	public static abstract class TextBoxImpl1 extends TextShapeImpl1 implements TextBox1 {
	}

	public static class TextBoxProxy1 extends TextBoxImpl1 implements TextBox1 {
	}

	public static abstract class PictureImpl1 extends SimpleShapeImpl1 implements Picture1 {
	}

	public static class PictureProxy1 extends PictureImpl1 implements Picture1 {
	}

	public interface Shape2 {
	}

	public interface TextShape2 extends Shape2 {
	}

	public interface TextBox2 extends TextShape2 {
	}

	public interface Picture2 extends Shape2 {
	}

	public static abstract class ShapeImpl2 implements Shape2 {
	}

	public static abstract class SimpleShapeImpl2 extends ShapeImpl2 {
	}

	public static abstract class TextShapeImpl2 extends SimpleShapeImpl2 implements TextShape2 {
	}

	public static abstract class TextBoxImpl2 extends TextShapeImpl2 implements TextBox2 {
	}

	public static class TextBoxProxy2 extends TextBoxImpl2 implements TextBox2 {
	}

	public static abstract class PictureImpl2 extends SimpleShapeImpl2 implements Picture2 {
	}

	public static class PictureProxy2 extends PictureImpl2 implements Picture2 {
	}

	public interface Shape3 {
	}

	public interface TextShape3 extends Shape3 {
	}

	public interface TextBox3 extends TextShape3 {
	}

	public interface Picture3 extends Shape3 {
	}

	public static abstract class ShapeImpl3 implements Shape3 {
	}

	public static abstract class SimpleShapeImpl3 extends ShapeImpl3 {
	}

	public static abstract class TextShapeImpl3 extends SimpleShapeImpl3 implements TextShape3 {
	}

	public static abstract class TextBoxImpl3 extends TextShapeImpl3 implements TextBox3 {
	}

	public static class TextBoxProxy3 extends TextBoxImpl3 implements TextBox3 {
	}

	public static abstract class PictureImpl3 extends SimpleShapeImpl3 implements Picture3 {
	}

	public static class PictureProxy3 extends PictureImpl3 implements Picture3 {
	}

	public interface Shape4 {
	}

	public interface TextShape4 extends Shape4 {
	}

	public interface TextBox4 extends TextShape4 {
	}

	public interface Picture4 extends Shape4 {
	}

	public static abstract class ShapeImpl4 implements Shape4 {
	}

	public static abstract class SimpleShapeImpl4 extends ShapeImpl4 {
	}

	public static abstract class TextShapeImpl4 extends SimpleShapeImpl4 implements TextShape4 {
	}

	public static abstract class TextBoxImpl4 extends TextShapeImpl4 implements TextBox4 {
	}

	public static class TextBoxProxy4 extends TextBoxImpl4 implements TextBox4 {
	}

	public static abstract class PictureImpl4 extends SimpleShapeImpl4 implements Picture4 {
	}

	public static class PictureProxy4 extends PictureImpl4 implements Picture4 {
	}

	public interface Shape5 {
	}

	public interface TextShape5 extends Shape5 {
	}

	public interface TextBox5 extends TextShape5 {
	}

	public interface Picture5 extends Shape5 {
	}

	public static abstract class ShapeImpl5 implements Shape5 {
	}

	public static abstract class SimpleShapeImpl5 extends ShapeImpl5 {
	}

	public static abstract class TextShapeImpl5 extends SimpleShapeImpl5 implements TextShape5 {
	}

	public static abstract class TextBoxImpl5 extends TextShapeImpl5 implements TextBox5 {
	}

	public static class TextBoxProxy5 extends TextBoxImpl5 implements TextBox5 {
	}

	public static abstract class PictureImpl5 extends SimpleShapeImpl5 implements Picture5 {
	}

	public static class PictureProxy5 extends PictureImpl5 implements Picture5 {
	}

	private FIBBrowserElement element(String name, Class<?> type) {
		FIBBrowserElement element = newFIBBrowserElement();
		element.setName(name);
		element.setDataType(type);
		return element;
	}

	@Test
	public void testMostSpecializedElementWhateverThePreviousLookups() {
		FIBBrowser browser = newFIBBrowser();
		browser.addToElements(element("shape0", Shape0.class));
		browser.addToElements(element("textShape0", TextShape0.class));
		browser.addToElements(element("shape1", Shape1.class));
		browser.addToElements(element("textShape1", TextShape1.class));
		browser.addToElements(element("shape2", Shape2.class));
		browser.addToElements(element("textShape2", TextShape2.class));
		browser.addToElements(element("shape3", Shape3.class));
		browser.addToElements(element("textShape3", TextShape3.class));
		browser.addToElements(element("shape4", Shape4.class));
		browser.addToElements(element("textShape4", TextShape4.class));
		browser.addToElements(element("shape5", Shape5.class));
		browser.addToElements(element("textShape5", TextShape5.class));

		// Pictures first: their implementation classes are shared with the text boxes
		assertEquals("shape0", browser.elementForObject(new PictureProxy0()).getName());
		assertEquals("shape1", browser.elementForObject(new PictureProxy1()).getName());
		assertEquals("shape2", browser.elementForObject(new PictureProxy2()).getName());
		assertEquals("shape3", browser.elementForObject(new PictureProxy3()).getName());
		assertEquals("shape4", browser.elementForObject(new PictureProxy4()).getName());
		assertEquals("shape5", browser.elementForObject(new PictureProxy5()).getName());

		assertEquals("textShape0", browser.elementForObject(new TextBoxProxy0()).getName());
		assertEquals("textShape1", browser.elementForObject(new TextBoxProxy1()).getName());
		assertEquals("textShape2", browser.elementForObject(new TextBoxProxy2()).getName());
		assertEquals("textShape3", browser.elementForObject(new TextBoxProxy3()).getName());
		assertEquals("textShape4", browser.elementForObject(new TextBoxProxy4()).getName());
		assertEquals("textShape5", browser.elementForObject(new TextBoxProxy5()).getName());

		// The interfaces themselves
		assertEquals("textShape0", browser.elementForType(TextBox0.class).getName());
		assertEquals("shape0", browser.elementForType(Picture0.class).getName());
	}
}
