/*
 *    ALMA - Atacama Large Millimiter Array
 *    (c) European Southern Observatory, 2002
 *    Copyright by ESO (in the framework of the ALMA collaboration)
 *    and Cosylab 2002, All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 *    MA 02111-1307  USA
 */
package alma.acs.gui.loglevel.tree;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import si.ijs.maci.Container;
import si.ijs.maci.ContainerInfo;
import si.ijs.maci.LoggingConfigurableHelper;
import si.ijs.maci.LoggingConfigurableOperations;
import si.ijs.maci.Manager;
import alma.acs.gui.loglevel.LogPaneNotFoundException;
import alma.acs.gui.loglevel.leveldlg.LogLevelSelectorPanel;
import alma.acs.gui.loglevel.tree.node.TreeContainerInfo;

/**
 * The class to receive mouse events generated by the tree
 * 
 * @author acaproni
 *
 */
public class TreeMouseListener extends MouseAdapter {
	
	// The tree generating events
	private LogLvlTree tree;
	
	// The node of the managers
	private DefaultMutableTreeNode managersNode;
	
	// The node of the containers
	private DefaultMutableTreeNode containersNode;
	
	// The model of the tree
	private LogLvlTreeModel model;
	
	// The popup menu
	private TreePopupMenu popupMenu;
	
	/**
	 * Constructor 
	 * 
	 * @param logLevelTree The tree generating events
	 */
	public TreeMouseListener(LogLvlTree logLevelTree) {
		if (logLevelTree==null) {
			throw new IllegalArgumentException("Invalid null LogLvlTree in constructor");
		}
		tree=logLevelTree;
		model = (LogLvlTreeModel)tree.getModel();
		popupMenu= new TreePopupMenu(model);
		managersNode = model.findNode(null, "Managers", 0);
		containersNode = model.findNode(null, "Containers", 0);
		if (containersNode==null) {
			throw new IllegalStateException("Containers node not found in the tree");
		}
	}
	
	/**
	 * @see java.awt.event.MouseListener
	 */
	public void mousePressed(MouseEvent e) {
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if (selPath == null) {
			// No object selected but the user expand/compress a node
			return;
		}
		
		if (e.getClickCount()==2) {
			tree.setSelectionPath(selPath);
			/*class TabShower implements Runnable {
				TreePath selectedPath;
				public TabShower(TreePath path) {
					selectedPath=path;
				}
				public void run() {
					showLoggingConfigTab(selectedPath);
				}
			};
			TabShower shower = new TabShower(selPath);
			Thread t = new Thread(shower);
			t.run();*/
			showLoggingConfigTab(selPath);
			return;
		}
		if (e.isPopupTrigger()) {
			//Toolkit tk = Toolkit.getDefaultToolkit();
			PointerInfo pi = MouseInfo.getPointerInfo();
			Point mouseLocation = pi.getLocation();
			SwingUtilities.convertPointFromScreen(mouseLocation, tree);
			popupMenu.show(tree,mouseLocation.x,mouseLocation.y);
		}
		
	}
	
	/**
	 * Show the dialog to read and configure the log level
	 * 
	 * @param path The path of the selected node
	 *
	 */
	private void showLoggingConfigTab(TreePath path) {
		if (path==null) {
			throw new IllegalArgumentException("Invalid null path");
		}
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)path.getLastPathComponent();
		DefaultMutableTreeNode targetNode = selNode;
		boolean implementsLogging=implementsLoggingConfigurable(selNode);
		if (!implementsLogging) {
			// yatagai: ad hoc implementation
			targetNode = (DefaultMutableTreeNode)selNode.getParent();
			if (targetNode == null || !implementsLoggingConfigurable(targetNode))
				return;
		}
		// Get a reference to the LoggingConfigurable for the 
		// selected item
		LoggingConfigurableOperations logConf = null;

		if (model.isManagerNode(targetNode)) {
			try {
				logConf= getLogConfFromManager();
			} catch (Throwable t) {
				JOptionPane.showInternalMessageDialog(tree, "Error getting the LoggingConfigurable:\n"+t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			ContainerInfo cInfo;
			try {
				cInfo = ((TreeContainerInfo)targetNode.getUserObject()).getClientInfo();
			} catch (Throwable t) {
				JOptionPane.showInternalMessageDialog(tree, "Error retrieving ContainerInfo from the node","Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				logConf=getLogConfFromContainer(cInfo);
			} catch (Throwable t) {
				JOptionPane.showInternalMessageDialog(tree, "Error getting the LoggingConfigurable:\n"+t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if (logConf==null) {
			JOptionPane.showMessageDialog(tree, "LoggingConfigurable is null", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			tree.getTabPanel().showTab(targetNode.getUserObject().toString());
		} catch (LogPaneNotFoundException e) {
			// The tab with this name does not exist: create and add a new one
			LogLevelSelectorPanel pnl;
			try {
				pnl = new LogLevelSelectorPanel(logConf,targetNode.getUserObject().toString());
			//} catch (LogLvlSelNotSupportedException ex) {
			} catch (Exception t) {
				JOptionPane.showMessageDialog(
						tree,
						//2010-02-17 panta@naoj
						//"<HTML>"+targetNode.getUserObject().toString()+" does not support selection of log levels:<BR>"+ex.getMessage(),
						"<HTML>"+targetNode.getUserObject().toString()+" Error selecting log level panel:<BR>"+t.getMessage(),
						"Error", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				tree.getTabPanel().addLogSelectorTab(pnl);
			} catch (Throwable t) {
				JOptionPane.showMessageDialog(
						tree,
						"<HTML>Error creating the panel for "+targetNode.getUserObject().toString()+":<BR>"+t.getMessage(),
						"Error", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// yatagai : Although the mousePressed action for a LoggingConfigurable 
		// folder node, expanding or collapsing, which is set by the JTree UI is not desirable,
		// I could not find a good way to disable it. 
		//
		// Reaching here, a LoggingConfigTab is displayed.
		// If this is a folder node, the following code cancels expanding or collapsing
		// action.
		TreeNode node = (TreeNode)path.getLastPathComponent();
		if (node.isLeaf())
			return;
		else {
			if (tree.isExpanded(path))
				tree.collapsePath(path);
			else
				tree.expandPath(path);
		}
	}
	
	/**
	 * Get the LoggingConfigurable out of the manager
	 * 
	 * @return The LoggingConfigurable for the manager
	 */
	private LoggingConfigurableOperations getLogConfFromManager() throws Exception {

		System.out.println("Getting LoggingConfigurable out of Manager");

		// Manager
		Manager mgr = model.getManagerRef();
		if (mgr==null) {
			throw new Exception("Invalid manager reference");
		}

		SwingWorker<LoggingConfigurableOperations, Void> worker = new SwingWorker<LoggingConfigurableOperations, Void>() {
			protected LoggingConfigurableOperations doInBackground()
					throws Exception {
				LoggingConfigurableOperations logConf;
				try {
					logConf = LoggingConfigurableHelper.narrow(model.getManagerRef());
				} catch (Throwable t) {
					throw new Exception("Error getting the LoggingConfigurable out of Manager:\n"+t.getMessage(),t);
				}
				return logConf;
			}
		};
		worker.execute();

		return worker.get();
	}
	
	/**
	 * Get the LoggingConfigurable out of the container
	 * 
	 * @return The LoggingConfigurable for the container
	 */
	private LoggingConfigurableOperations getLogConfFromContainer(ContainerInfo info) throws Exception {
		
		if (info==null) {
			throw new IllegalArgumentException("Invalid null ContainerInfo");
		}

		final Container cnt = info.reference;
		if (cnt==null) {
			throw new Exception("The reference to the container is null in ContainerInfo");
		}

		SwingWorker<LoggingConfigurableOperations, Void> worker = new SwingWorker<LoggingConfigurableOperations, Void>() {
			protected LoggingConfigurableOperations doInBackground()
					throws Exception {
				LoggingConfigurableOperations logConf;
				try {
					logConf = LoggingConfigurableHelper.narrow(cnt);
				} catch (Throwable t) {
					throw new Exception("Error getting the LoggingConfigurable out of Manager:\n"+t.getMessage(),t);
				}
				return logConf;
			}
		};
		worker.execute();

		return worker.get();
	}
	
	/**
	 * Check if the object represented by the node implements
	 * the LoggingConfigurable IDL interface.
	 * Actually the only ACS items implementing such an interface are
	 *   - the manager
	 *   - the containers
	 *   
	 * @param node The node to query
	 * @return true if the object implements LoggingConfigurable
	 */
	private boolean implementsLoggingConfigurable(DefaultMutableTreeNode node) {
		if (node==null) {
			throw new IllegalArgumentException("Invalid null node to query");
		}
		if (node.isRoot()) {
			return false;
		}
		return containersNode.isNodeChild(node) || 
			managersNode.isNodeChild(node);
	}
}