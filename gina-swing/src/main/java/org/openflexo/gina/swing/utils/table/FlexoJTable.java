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

package org.openflexo.gina.swing.utils.table;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class FlexoJTable extends JTable {

	protected static final Logger LOGGER = Logger.getLogger(FlexoJTable.class.getPackage().getName());

	protected AbstractModel _model;

	public FlexoJTable(AbstractModel dm) {
		super(dm);
		_model = dm;
		// Following line is very important and must be kept in order
		// to prevent unexpected column rebuilt !!!!!!!!!!!!
		setAutoCreateColumnsFromModel(false);
	}

	/**
	 * Overrides createDefaultTableHeader
	 * 
	 * @see javax.swing.JTable#createDefaultTableHeader()
	 */
	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {
			@Override
			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int index = columnModel.getColumnIndexAtX(p.x);
				int realIndex = columnModel.getColumn(index).getModelIndex();
				return _model.getColumnTooltip(realIndex);
			}
		};
	}

	/* public void doLayout() 
	 {
	     logger.info("doLayout() for  "+_model);
	     logger.info("getResizingColumn() =  "+getResizingColumn());
	     logger.info("getSize() =  "+getSize());
	     logger.info("getPreferredSize() =  "+getPreferredSize());
	     logger.info("model.getTotalPreferredWidth() =  "+_model.getTotalPreferredWidth());
	     if (getResizingColumn() == null) {
	         setWidthsFromPreferredWidths(false);
	     } else {
	         super.doLayout();
	     }
	 }
	 
	 public void columnMarginChanged(ChangeEvent e) 
	 {
	     logger.info("columnMarginChanged() with "+e+" for  "+_model);
	     super.columnMarginChanged(e);
	 }
	 
	 private TableColumn getResizingColumn()
	 {
	     return (tableHeader == null) ? null
	             : tableHeader.getResizingColumn();
	 }
	 
	 private void setWidthsFromPreferredWidths(final boolean inverse) 
	 {
	     logger.info("setWidthsFromPreferredWidths() with "+inverse+" for  "+_model);
	     int totalWidth     = getWidth();
	     int totalPreferred = getPreferredSize().width;
	     int target = !inverse ? totalWidth : totalPreferred;
	     
	     final TableColumnModel cm = columnModel;
	     Resizable3 r = new Resizable3() {
	         public int  getElementCount()      { return cm.getColumnCount(); }
	         public int  getLowerBoundAt(int i) { return cm.getColumn(i).getMinWidth(); }
	         public int  getUpperBoundAt(int i) { return cm.getColumn(i).getMaxWidth(); }
	         public int  getMidPointAt(int i)  {
	             if (!inverse) {
	                 logger.info("hophophop");
	                 return cm.getColumn(i).getPreferredWidth();
	             }
	             else {
	                 logger.info("oucacacacca");
	                 return cm.getColumn(i).getWidth();
	             }
	         }
	         public void setSizeAt(int s, int i) {
	             if (!inverse) {
	                 cm.getColumn(i).setWidth(s);
	             }
	             else {
	                 cm.getColumn(i).setPreferredWidth(s);
	             }
	         }
	     };
	     
	     adjustSizes(target, r, inverse);
	 }
	 
	 
	 private interface Resizable2
	 {
	     public int  getElementCount();
	     public int  getLowerBoundAt(int i);
	     public int  getUpperBoundAt(int i);
	     public void setSizeAt(int newSize, int i);
	 }
	 
	 private interface Resizable3 extends Resizable2 
	 {
	     public int  getMidPointAt(int i);
	 }
	 
	 
	 private void adjustSizes(long target, final Resizable3 r, boolean inverse) 
	 {
	     logger.info("adjustSizes1() with "+target+","+r+","+inverse+" for  "+_model);
	     int N = r.getElementCount();
	     long totalPreferred = 0;
	     String st = "0";
	     for(int i = 0; i < N; i++) {
	         int w = r.getMidPointAt(i);
	         totalPreferred += w;
	         st = st + " + "+w;
	     }
	     logger.info("totalPreferred = "+st+" = "+totalPreferred);
	     Resizable2 s;
	     if ((target < totalPreferred) == !inverse) {
	         logger.info("On passe ici");
	         s = new Resizable2() {
	             public int  getElementCount()      { return r.getElementCount(); }
	             public int  getLowerBoundAt(int i) { return r.getLowerBoundAt(i); }
	             public int  getUpperBoundAt(int i) { return r.getMidPointAt(i); }
	             public void setSizeAt(int newSize, int i) { r.setSizeAt(newSize, i); }
	             
	         };
	     }
	     else {
	         logger.info("On passe la");
	        s = new Resizable2() {
	             public int  getElementCount()      { return r.getElementCount(); }
	             public int  getLowerBoundAt(int i) { return r.getMidPointAt(i); }
	             public int  getUpperBoundAt(int i) { return r.getUpperBoundAt(i); }
	             public void setSizeAt(int newSize, int i) { r.setSizeAt(newSize, i); }
	             
	         };
	     }
	     adjustSizes(target, s, !inverse);
	 }
	 
	 private void adjustSizes(long target, Resizable2 r, boolean limitToRange)
	 {
	     logger.info("adjustSizes2() with "+target+","+r+","+limitToRange+" for  "+_model);
	     long totalLowerBound = 0;
	     long totalUpperBound = 0;
	     for(int i = 0; i < r.getElementCount(); i++) {
	         totalLowerBound += r.getLowerBoundAt(i);
	         totalUpperBound += r.getUpperBoundAt(i);
	     }
	     
	     if (limitToRange) {
	         target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
	     }
	     
	     for(int i = 0; i < r.getElementCount(); i++) {
	         int lowerBound = r.getLowerBoundAt(i);
	         int upperBound = r.getUpperBoundAt(i);
	         // Check for zero. This happens when the distribution of the delta
	         // finishes early due to a series of "fixed" entries at the end.
	         // In this case, lowerBound == upperBound, for all subsequent terms.
	         int newSize;
	         if (totalLowerBound == totalUpperBound) {
	             newSize = lowerBound;
	         }
	         else {
	             double f = (double)(target - totalLowerBound)/(totalUpperBound - totalLowerBound);
	             newSize = (int)Math.round(lowerBound+f*(upperBound - lowerBound));
	             // We'd need to round manually in an all integer version.
	             // size[i] = (int)(((totalUpperBound - target) * lowerBound +
	             //     (target - totalLowerBound) * upperBound)/(totalUpperBound-totalLowerBound));
	         }
	         logger.info("setSizeAt newSize="+newSize+" i="+i );
	         r.setSizeAt(newSize, i);
	         target -= newSize;
	         totalLowerBound -= lowerBound;
	         totalUpperBound -= upperBound;
	     }
	 }
	 
	 public void createDefaultColumnsFromModel()
	 {
	     logger.info("########### createDefaultColumnsFromModel()");
	     super.createDefaultColumnsFromModel();
	 }*/
}
