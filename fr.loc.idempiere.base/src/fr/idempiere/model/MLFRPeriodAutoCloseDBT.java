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

package fr.idempiere.model;

import static fr.idempiere.model.SystemIDs_LFR.LFR_PERIOD_AUTO_CLOSE_DOCBASETYPE_DAYS;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MColumn;
import org.compiere.model.MDocType;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Util;

/**
 * Period Automatic Close / configuration per DocBaseType
 * @author Nicolas Micoud - TGI
 */

public class MLFRPeriodAutoCloseDBT extends X_LFR_PeriodAutoCloseDBT {

	private static final long serialVersionUID = -3803358782337979426L;
	/**	Logger							*/
	private static CLogger		s_log = CLogger.getCLogger (MLFRPeriodAutoCloseDBT.class);

	public MLFRPeriodAutoCloseDBT (Properties ctx, int LFR_PeriodAutoCloseDBT_ID, String trxName) {
		super (ctx, LFR_PeriodAutoCloseDBT_ID, trxName);
	}	//	MLFRPeriodAutoCloseDBT

	public MLFRPeriodAutoCloseDBT (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}	//	MLFRPeriodAutoCloseDBT

	public static String initData(Properties ctx, int clientID, String trxName) {

		StringBuilder sql = new StringBuilder("SELECT rl.Value FROM AD_Ref_List rl")
				.append(" WHERE AD_Reference_ID = ?")
				.append(" AND rl.Value NOT IN (SELECT DocBaseType FROM LFR_PeriodAutoCloseDBT WHERE AD_Client_ID = ?)")
				.append(" ORDER BY 1");

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, MColumn.get(ctx, MDocType.Table_Name, MDocType.COLUMNNAME_DocBaseType).getAD_Reference_Value_ID());
			pstmt.setInt(2, clientID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				MLFRPeriodAutoCloseDBT pac = new MLFRPeriodAutoCloseDBT(ctx, 0, trxName);
				pac.setDocBaseType(rs.getString("Value"));
				pac.setLFR_CloseAfterPeriodEndDays(getDefaultValue(clientID, pac.getDocBaseType()));
				pac.saveEx();
			}
		}
		catch(Exception e) {
			return "Error while initializing data " + e;
		}
		finally {
			DB.close(rs, pstmt);
		}
		
		return "";
	}

	private static int getDefaultValue(int clientID, String dbt) {

		String defaultValues = MSysConfig.getValue(LFR_PERIOD_AUTO_CLOSE_DOCBASETYPE_DAYS, "", clientID);
		
		if (!Util.isEmpty(defaultValues)) {
			try {
				for (String s : defaultValues.split(",")) {
					if (!Util.isEmpty(s) && s.toUpperCase().startsWith(dbt.toUpperCase() + "=")) {
						String value = s.substring(s.indexOf("=") + 1);
						return Integer.valueOf(value);
					}
				}
			}
			catch(Exception e) {
				s_log.log(Level.SEVERE, "Error while extracting defautl value for days " + defaultValues, e);			
			}
		}
		return 0;
	}
}
