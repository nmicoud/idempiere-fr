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

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MInvoice;
import org.compiere.model.MTax;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * OD Situation Line model
 * @author Nicolas Micoud - TGI
 */

public class MLFRODSituationPrepaLine extends X_LFR_ODSituationPrepaLine {

	private static final long serialVersionUID = -1820891813412777158L;

	public MLFRODSituationPrepaLine (Properties ctx, int LFR_PeriodAutoCloseDBT_ID, String trxName) {
		super (ctx, LFR_PeriodAutoCloseDBT_ID, trxName);
	}	//	MLFRODSituationPrepaLine

	public MLFRODSituationPrepaLine (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}	//	MLFRODSituationPrepaLine

	protected boolean beforeSave (boolean newRecord)
	{
		if (getLine() == 0)
			setLine(DB.getSQLValueEx(get_TrxName(), "SELECT COALESCE(MAX(Line), 0) + 10 FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = ?", getLFR_ODSituationPrepa_ID()));

		if (getAD_OrgDoc_ID() <= 0 ) {
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@FillMandatory@: @AD_OrgDoc_ID@"));
			return false;
		}

		if (getAccount_ID() <= 0 ) {
			log.saveError("Error", Msg.parseTranslation(getCtx(), "@FillMandatory@: @Account_ID@"));
			return false;
		}

		if (getType().equals(TYPE_CAP) && !newRecord && is_ValueChanged(COLUMNNAME_Amt))
			setTaxAmtForCAP(new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName()));

		// Si différence, alors IsManual devient true
		if (getAmtAcct().compareTo(getAmt()) != 0)
			setIsManual(true);
		if (getLFR_FactAcct_Org_ID() != getAD_OrgDoc_ID())
			setIsManual(true);
		if (getLFR_FactAcct_Account_ID() != getAccount_ID())
			setIsManual(true);

		// Si tout pareil, IsManual devient false
		if ((getAmt().compareTo(getAmtAcct()) == 0) && (getLFR_FactAcct_Org_ID() == getAD_OrgDoc_ID()) && (getLFR_FactAcct_Account_ID() == getAccount_ID()))
			setIsManual(false);

		return true;
	}	//	beforeSave

	protected boolean afterSave (boolean newRecord, boolean success) {
		setHeader();
		return success;
	}	//	afterSave

	protected boolean afterDelete (boolean success) {
		setHeader();
		return success;
	}	//	afterDelete	

	private void setHeader() {
		DB.executeUpdateEx("UPDATE LFR_ODSituationPrepa sp"
				+ " SET TotalAmt = (SELECT COALESCE(SUM(AmtAcct), 0)"
				+ " FROM LFR_ODSituationPrepaLine spl"
				+ " WHERE sp.LFR_ODSituationPrepa_ID = LFR_ODSituationPrepa_ID AND IsActive='Y')"
				+ " WHERE sp.LFR_ODSituationPrepa_ID = ?", new Object[] {getLFR_ODSituationPrepa_ID()}, get_TrxName());
	}	//	setHeader

	public void setTaxAmtForCAP(MInvoice i) {
		setTaxAmt(MTax.get(getCtx(), getC_Tax_ID()).calculateTax(getAmtAcct(), false, i.getPrecision()));
	}
}
