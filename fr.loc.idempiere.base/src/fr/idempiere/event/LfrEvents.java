/**********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Nicolas Micoud - TGI                                              *
 **********************************************************************/

package fr.idempiere.event;

import static fr.idempiere.model.SystemIDs_LFR.LFR_IN_USE;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;
import org.osgi.service.event.Event;


public class LfrEvents extends AbstractEventHandler {

	@Override
	protected void initialize() {
		registerEvent(IEventTopics.AFTER_LOGIN);
	}

	@Override
	protected void doHandleEvent(Event event) {

		String topic = event.getTopic();

		if (topic.equals(IEventTopics.AFTER_LOGIN)) {
			LoginEventData loginData = (LoginEventData) event.getProperty(IEventManager.EVENT_DATA);
			boolean useLfr = MSysConfig.getBooleanValue(LFR_IN_USE, false, loginData.getAD_Client_ID());
			Env.setContext(Env.getCtx(), "#LFR", useLfr);
		}
	}
}
