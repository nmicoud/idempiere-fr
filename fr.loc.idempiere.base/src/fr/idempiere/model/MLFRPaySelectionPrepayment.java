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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * Preparation virement pour acomptes
 * @author Nicolas Micoud - TGI
 */

public class MLFRPaySelectionPrepayment extends X_LFR_PaySelectionPrepayment {

	private static final long serialVersionUID = -9215335968993824876L;
	static private CLogger	s_log = CLogger.getCLogger (MLFRPaySelectionPrepayment.class);

	public MLFRPaySelectionPrepayment (Properties ctx, int LFR_PeriodAutoCloseDBT_ID, String trxName) {
		super (ctx, LFR_PeriodAutoCloseDBT_ID, trxName);
	}	//	MLFRPaySelectionPrepayment

	public MLFRPaySelectionPrepayment (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}	//	MLFRPaySelectionPrepayment

	protected boolean beforeSave (boolean newRecord) {

		if (getLine() == 0)
			setLine (DB.getSQLValueEx(get_TrxName(), "SELECT COALESCE(MAX(Line), 0) + 10 FROM LFR_PaySelectionPrepayment WHERE C_PaySelection_ID = ?", getC_PaySelection_ID()));

		return true;
	}

	@Override
	protected boolean afterSave (boolean newRecord, boolean success) {
		if (!success)
			return success;
		updatePaySelectionTotalAmt(getC_PaySelection_ID(), get_TrxName());
		return success;
	}	//	afterSave

	@Override
	protected boolean afterDelete (boolean success) {
		if (!success)
			return success;
		updatePaySelectionTotalAmt(getC_PaySelection_ID(), get_TrxName());
		return success;
	}	//	afterDelete

	public static void updatePaySelectionTotalAmt(int paySelectionID, String trxName) {

		StringBuilder sql = new StringBuilder("UPDATE C_PaySelection ps")
				.append(" SET TotalAmt = (SELECT COALESCE(SUM(psl.PayAmt), 0)")
				.append(" FROM C_PaySelectionLine psl")
				.append(" WHERE ps.C_PaySelection_ID=psl.C_PaySelection_ID AND psl.IsActive='Y')")
				.append(" +")
				.append(" (SELECT COALESCE(SUM(p.PayAmt), 0)")
				.append(" FROM LFR_PaySelectionPrepayment psp, C_Payment p")
				.append(" WHERE ps.C_PaySelection_ID=psp.C_PaySelection_ID AND psp.IsActive='Y' AND psp.C_Payment_ID = p.C_Payment_ID)")
				.append(" WHERE C_PaySelection_ID = ?");

		DB.executeUpdateEx(sql.toString(), new Object[] {paySelectionID}, trxName);
	}
	
	
	public static MLFRPaySelectionPrepayment[] get (int paySelectionID, String trxName)
	{
		ArrayList<MLFRPaySelectionPrepayment> list = new ArrayList<MLFRPaySelectionPrepayment>();

		String sql = "SELECT * FROM LFR_PaySelectionPrepayment WHERE C_PaySelection_ID = ? ORDER BY Line";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, paySelectionID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MLFRPaySelectionPrepayment (Env.getCtx(), rs, trxName));
		}
		catch (SQLException e) {
			s_log.log(Level.SEVERE, sql, e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		//  convert to Array
		MLFRPaySelectionPrepayment[] retValue = new MLFRPaySelectionPrepayment[list.size()];
		list.toArray(retValue);
		return retValue;
	}   //  get
}
