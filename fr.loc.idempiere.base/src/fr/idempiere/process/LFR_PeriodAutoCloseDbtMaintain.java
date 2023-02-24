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

package fr.idempiere.process;

import org.compiere.model.MClient;
import org.compiere.model.MTable;
import org.compiere.util.Util;

import fr.idempiere.model.MLFRPeriodAutoCloseDBT;

/**
 *	Process d'initialisation de la table LFR_PeriodAutoCloseDBT
 *  @author Nicolas Micoud - TGI
 */

public class LFR_PeriodAutoCloseDbtMaintain extends LfrProcess {

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
	}	//	prepare

	/**
	 * 	Execute
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		if (getAD_Client_ID() > 0)
			return "@ProcessOK@ " + process(getAD_Client_ID());
		else {

			StringBuilder retValue = new StringBuilder();
			for (MClient client : MClient.getAll(getCtx(), "AD_Client_ID")) {
				if (client.getAD_Client_ID() > MTable.MAX_OFFICIAL_ID)
					retValue.append(" ").append(client.getName()).append(": ").append(process(client.getAD_Client_ID()));
			}

			return "@ProcessOK@ " + retValue;
		}
	}	//	doIt

	private String process(int clientID) {

		String err = MLFRPeriodAutoCloseDBT.initData(getCtx(), clientID, get_TrxName());
		if (Util.isEmpty(err))
			return "OK";
		else
			return err;
	}


}	//	LFR_PeriodAutoCloseDbtMaintain