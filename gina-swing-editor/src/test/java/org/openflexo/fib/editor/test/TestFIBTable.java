/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing-editor, a component of the software infrastructure 
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

package org.openflexo.fib.editor.test;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public class TestFIBTable {

	private static final Logger logger = Logger.getLogger(TestFIBTable.class.getPackage().getName());

	private static final ResourceLocator rl = ResourceLocator.getResourceLocator();

	public static Resource FIB_FILE = ResourceLocator.locateSourceCodeResource("TestFIB/TestTable2.fib");

	public static void main(String[] args) {
		final User user1 = new User("John", "Doe", "john.doe@yahoo.com");
		final User user2 = new User("Thomas", "Smith", "thomas.smith@google.com");
		final User user3 = new User("Sarah", "Martins", "smartin@free.com");
		final User user4 = new User("Leonce", "Clercks", "leonceclecks@deef.org");
		final UserList userList = new UserList(user1, user2, user3);

		FIBAbstractEditor editor = new FIBAbstractEditor() {
			@Override
			public Object[] getData() {
				return FIBAbstractEditor.makeArray(userList);
			}

			@Override
			public Resource getFIBResource() {
				return FIB_FILE;
			}
		};
		editor.addAction("change_name", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				user1.setFirstName("Johnny");
			}
		});
		editor.addAction("add_user", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userList.addUser(user4);
			}
		});
		editor.addAction("remove_user", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userList.removeUser(user4);
			}
		});
		editor.launch();
	}

	public static class UserList implements HasPropertyChangeSupport {
		private final PropertyChangeSupport pcSupport;

		public Vector<User> users;

		public UserList(User... someUsers) {
			super();
			pcSupport = new PropertyChangeSupport(this);
			users = new Vector<User>();
			for (User u : someUsers) {
				addUser(u);
			}
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		public void addUser(User u) {
			users.add(u);
			pcSupport.firePropertyChange("users", null, null);
		}

		public void removeUser(User u) {
			users.remove(u);
			pcSupport.firePropertyChange("users", null, null);
		}

		@Override
		public String toString() {
			return "UserList[]";
		}

		public User createNewUser() {
			User returned = new User("firstName", "lastName", "email");
			addUser(returned);
			return returned;
		}

		public User deleteUser(User u) {
			removeUser(u);
			return u;
		}
	}

	public static class User implements HasPropertyChangeSupport {
		private final PropertyChangeSupport pcSupport;
		private String firstName;
		private String lastName;
		private String email;

		public User(String firstName, String lastName, String email) {
			super();
			pcSupport = new PropertyChangeSupport(this);
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			String oldFirstName = this.firstName;
			this.firstName = firstName;
			pcSupport.firePropertyChange("firstName", oldFirstName, firstName);
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			String oldLastName = this.lastName;
			this.lastName = lastName;
			pcSupport.firePropertyChange("lastName", oldLastName, lastName);
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			String oldEmail = this.email;
			this.email = email;
			pcSupport.firePropertyChange("email", oldEmail, email);
		}

	}

}
