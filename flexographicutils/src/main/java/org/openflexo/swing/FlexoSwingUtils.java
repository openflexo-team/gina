/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

public class FlexoSwingUtils {

	private static class RunnableCallable<V> implements Runnable {
		private Callable<V> callable;

		private V result;

		private Exception exception;

		protected RunnableCallable(Callable<V> callable) {
			super();
			this.callable = callable;
		}

		@Override
		public void run() {
			try {
				result = callable.call();
			} catch (Exception e) {
				exception = e;
			}
		}

		public V getResult() {
			return result;
		}

		public Exception getException() {
			return exception;
		}
	}

	public static void syncRunInEDT(Runnable runnable) throws Exception {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeAndWait(runnable);
		}
		else {
			runnable.run();
		}
	}

	public static <V> V syncRunInEDT(Callable<V> callable) throws Exception {
		RunnableCallable<V> runnable = new RunnableCallable<>(callable);
		syncRunInEDT(runnable);
		if (runnable.getException() != null) {
			throw new InvocationTargetException(runnable.getException(), "Error while running callable");
		}
		return runnable.getResult();
	}

}
