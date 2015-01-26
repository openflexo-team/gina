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

package org.openflexo.fib.swing.logging;

import java.awt.Color;
import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.logging.LogRecord;
import org.openflexo.logging.LogRecords;
import org.openflexo.logging.LoggingFilter;
import org.openflexo.logging.LoggingFilter.FilterType;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.StringUtils;

public class FlexoLoggingViewer implements HasPropertyChangeSupport {

	public static ResourceLocator rl = ResourceLocator.getResourceLocator();

	static final Logger LOGGER = Logger.getLogger(FlexoLoggingViewer.class.getPackage().getName());

	public static final Resource LOGGING_VIEWER_FIB_NAME = ResourceLocator.locateResource("Fib/LoggingViewer.fib");

	public static final ImageIcon FILTER_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Utils/Search.png"));

	private final FlexoLoggingManager loggingManager;

	private final PropertyChangeSupport _pcSupport;

	public Vector<LoggingFilter> filters = new Vector<LoggingFilter>();
	public String searchedText;

	public boolean displayLogLevel = false;
	public boolean displayPackage = true;
	public boolean displayClass = true;
	public boolean displayMethod = true;
	public boolean displaySequence = true;
	public boolean displayDate = true;
	public boolean displayMillis = true;
	public boolean displayThread = true;

	private static FlexoLoggingViewer instance;
	private static FIBDialog<FlexoLoggingViewer> dialog;

	public static void showLoggingViewer(FlexoLoggingManager loggingManager, Window parent) {
		System.out.println("showLoggingViewer with " + loggingManager);
		FIBComponent loggingViewerComponent = FIBLibrary.instance().retrieveFIBComponent(LOGGING_VIEWER_FIB_NAME, true);
		if (instance == null || dialog == null) {
			instance = new FlexoLoggingViewer(loggingManager);
			dialog = FIBDialog.instanciateAndShowDialog(loggingViewerComponent, instance, parent, false,
					FlexoLocalization.getMainLocalizer());
		} else {
			dialog.showDialog();
		}
	}

	public FlexoLoggingViewer(FlexoLoggingManager loggingManager) {
		_pcSupport = new PropertyChangeSupport(this);
		this.loggingManager = loggingManager;
	}

	public LogRecords getRecords() {
		return loggingManager.logRecords;
	}

	public Icon getIconForFilter(LoggingFilter filter) {
		return FILTER_ICON;
	}

	public Icon getIconForLogRecord(LogRecord record) {
		if (record.level == Level.WARNING) {
			return UtilsIconLibrary.WARNING_ICON;
		}
		if (record.level == Level.SEVERE) {
			return UtilsIconLibrary.ERROR_ICON;
		}
		return null;
	}

	public Color getColorForLogRecord(LogRecord record) {
		if (record.level == Level.INFO) {
			return Color.BLACK;
		} else if (record.level == Level.WARNING) {
			return Color.RED;
		} else if (record.level == Level.SEVERE) {
			return Color.PINK;
		}
		return Color.GRAY;
	}

	public Color getBgColorForLogRecord(LogRecord record) {
		if (getRecords().filtersApplied()) {
			for (LoggingFilter f : filters) {
				if (f.type == FilterType.Highlight && f.filterDoesApply(record)) {
					return Color.YELLOW;
				}
			}
		}
		return null;
	}

	private static final Level[] LEVELS = { Level.SEVERE, Level.WARNING, Level.INFO, Level.FINE, Level.FINER, Level.FINEST };

	public Level[] getAvailableLevels() {
		return LEVELS;
	}

	public int getNumberOfLogsToKeep() {
		return loggingManager.getMaxLogCount();
	}

	public void setNumberOfLogsToKeep(int numberOfLogsToKeep) {
		loggingManager.setMaxLogCount(numberOfLogsToKeep);
	}

	public boolean getIsInfiniteNumberOfLogs() {
		return getNumberOfLogsToKeep() == -1;
	}

	public void setIsInfiniteNumberOfLogs(boolean isInfinite) {
		if (isInfinite) {
			setNumberOfLogsToKeep(-1);
		} else {
			setNumberOfLogsToKeep(500);
		}
	}

	public boolean isKeepLogTraceInMemory() {
		return loggingManager.getKeepLogTrace();
	}

	public void setKeepLogTraceInMemory(boolean keepLogTraceInMemory) {
		loggingManager.setKeepLogTrace(keepLogTraceInMemory);
	}

	private Resource configurationFile;

	public File getConfigurationFile() {
		if (configurationFile == null) {
			String loggingFileName = loggingManager.getConfigurationFileName();
			configurationFile = ResourceLocator.locateResource(loggingFileName);
			/*
			if (loggingFileName != null && new File(loggingFileName).exists()) {
				configurationFile = new File(loggingFileName);
			}
			*/
		}
		if (configurationFile != null) {
			return rl.retrieveResourceAsFile(configurationFile);
		}
		return null;
	}

	public void setConfigurationFile(Resource configurationFile) {
		this.configurationFile = configurationFile;
		loggingManager.setConfigurationFileLocation(configurationFile);
		_pcSupport.firePropertyChange("configurationFile", null, configurationFile);
	}

	public Level getLogLevel() {
		return loggingManager.getDefaultLoggingLevel();
	}

	public void setLogLevel(Level lev) {
		loggingManager.setDefaultLoggingLevel(lev);
		configurationFile = null;
		_pcSupport.firePropertyChange("configurationFile", null, configurationFile);
	}

	public void refresh() {
		_pcSupport.firePropertyChange("records", null, getRecords());
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return _pcSupport;
	}

	public void printStackTrace(LogRecord record) {
		if (record == null) {
			return;
		}
		System.err.println("Stack trace for '" + record.message + "':");
		StringTokenizer st = new StringTokenizer(record.getStackTraceAsString(), StringUtils.LINE_SEPARATOR);
		while (st.hasMoreTokens()) {
			System.err.println("\t" + st.nextToken());
		}
	}

	public LoggingFilter createFilter() {
		LoggingFilter newFilter = new LoggingFilter("New filter");
		filters.add(newFilter);
		return newFilter;
	}

	public void deleteFilter(LoggingFilter filter) {
		filters.remove(filter);
	}

	public void applyFilters() {
		getRecords().applyFilters(filters);
	}

	public void dismissFilters() {
		getRecords().dismissFilters();
	}

	public void searchText() {
		if (StringUtils.isNotEmpty(searchedText)) {
			getRecords().searchText(searchedText);
		}
	}

	public void dismissSearchText() {
		getRecords().dismissSearchText();
	}

	public void loadLogsFile(File logsFile) {
		System.out.println("Open logs " + logsFile);
		System.out.println("logs=" + FlexoLoggingManager.loadLogFile(logsFile));
	}
}
