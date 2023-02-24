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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.compiere.model.MClient;
import org.compiere.model.MPeriod;
import org.compiere.model.MPeriodControl;
import org.compiere.model.MTable;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.CacheMgt;
import org.compiere.util.DB;
import org.compiere.util.TimeUtil;

/**
 *	Automatic closing of previous periods
 *  @author Nicolas Micoud - TGI
 */

public class LFR_PeriodAutoClose extends LfrProcess {

	private Timestamp p_date = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
				;
			else if (name.equals("Date"))
				p_date = para[i].getParameterAsTimestamp();
		}
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

		StringBuilder retValue = new StringBuilder("");
		// 
		StringBuilder sql = new StringBuilder("SELECT C_PeriodControl_ID, p.EndDate + pac.LFR_CloseAfterPeriodEndDays")
				.append(" FROM C_PeriodControl pc, C_Period p, LFR_PeriodAutoCloseDBT pac")
				.append(" WHERE p.C_Period_ID = pc.C_Period_ID")
				.append(" AND pac.AD_Client_ID = pc.AD_Client_ID AND pc.DocBaseType = pac.DocBaseType AND pac.IsActive = 'Y'")
				.append(" AND p.AD_Client_ID = ?")
				.append(" AND p.EndDate <= ?")
				.append(" AND pc.PeriodStatus = ?")
				.append(" ORDER BY p.EndDate, pc.DocBaseType");

		ArrayList<Integer> listPeriodIDs = new ArrayList<Integer>();

		List<List<Object>> rows = DB.getSQLArrayObjectsEx(get_TrxName(), sql.toString(), clientID, p_date, MPeriodControl.PERIODSTATUS_Open);
		if (rows != null && rows.size() > 0) {
			for (List<Object> row : rows) {
				int periodControlID = ((BigDecimal) row.get(0)).intValue();
				Timestamp limit = ((Timestamp) row.get(1));

				if (TimeUtil.max(p_date, limit) == p_date || TimeUtil.isSameDay(limit, p_date)) {
					MPeriodControl pc = new MPeriodControl(getCtx(), periodControlID, get_TrxName());
					if (pc != null && pc.getPeriodStatus().equals(MPeriodControl.PERIODSTATUS_Open)) {
						pc.setPeriodStatus(MPeriodControl.PERIODSTATUS_Closed);
						pc.setPeriodAction(MPeriodControl.PERIODACTION_NoAction);
						pc.saveEx();

						if (!listPeriodIDs.contains(pc.getC_Period_ID()))
							listPeriodIDs.add(pc.getC_Period_ID());
					}	
				}
			}
		}

		if (listPeriodIDs.size() > 0) {
			retValue.append("Closing");

			for (int periodID : listPeriodIDs) {
				retValue.append(" ").append(MPeriod.get(periodID).getName());
				CacheMgt.get().reset("C_Period", periodID);
			}
		}
		else
			retValue.append("Nothing to close");

		return retValue.toString();
	}

}	//	LFR_PeriodAutoCloseDbtMaintain