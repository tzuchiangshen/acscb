/*******************************************************************************
 * ALMA - Atacama Large Millimeter Array
 * Copyright (c) ESO - European Southern Observatory, 2011
 * (in the framework of the ALMA collaboration).
 * All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *******************************************************************************/
package alma.alarmsystemdemo.PSImpl;

import alma.acs.component.ComponentImplBase;
import alma.alarmsystemdemo.PSOperations;
import alma.alarmsystemdemo.Mount;
import alma.alarmsystemdemo.MountOperations;
import alma.alarmsystemdemo.MountHelper;

import alma.alarmsystem.source.ACSFaultState;
import alma.alarmsystem.source.ACSAlarmSystemInterface;
import alma.alarmsystem.source.ACSAlarmSystemInterfaceFactory;


import java.sql.Timestamp;
import java.util.Properties;


class PSImpl extends ComponentImplBase implements PSOperations {
	Mount mount = null;
	
	public void faultPS() {
		send_alarm("PS","ALARM_SOURCE_PS",1,ACSFaultState.ACTIVE);
		try { 
			Thread.sleep(5000);
		} catch (Exception e) {}
		Mount mnt = getMount();
		if (mnt!=null) {
			mnt.faultMount();
		}
	}
	public void terminate_faultPS() {
		send_alarm("PS","ALARM_SOURCE_PS",1,ACSFaultState.TERMINATE);
		try { 
			Thread.sleep(5000);
		} catch (Exception e) {}
		Mount mnt = getMount();
		if (mnt!=null) {
			mnt.terminate_faultMount();
		}
	}
	
	public void send_alarm(String faultFamily, String faultMember, int faultCode, String faultState) {
		ACSAlarmSystemInterface alarmSource;
		try {
			alarmSource = ACSAlarmSystemInterfaceFactory.createSource(this.name());
			ACSFaultState fs = ACSAlarmSystemInterfaceFactory.createFaultState(
					faultFamily, faultMember, faultCode);
			fs.setDescriptor(faultState);
			fs.setUserTimestamp(new Timestamp(System.currentTimeMillis()));

			Properties props = new Properties();
			props.setProperty(ACSFaultState.ASI_PREFIX_PROPERTY, "prefix");
			props.setProperty(ACSFaultState.ASI_SUFFIX_PROPERTY, "suffix");
			props.setProperty("TEST_PROPERTY", "TEST_VALUE");
			fs.setUserProperties(props);

			alarmSource.push(fs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Mount getMount() {
		if (this.mount==null) {
			org.omg.CORBA.Object cmp = null;
			try
			{
				cmp = m_containerServices.getComponent("ALARM_SOURCE_MOUNT");
				mount = MountHelper.narrow(cmp);
			}
			catch (Exception ex)
			{}
		}
		return mount;
	}
}