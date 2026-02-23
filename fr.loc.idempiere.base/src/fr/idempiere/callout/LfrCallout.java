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

package fr.idempiere.callout;

import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEDEB;
import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEFIN;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBankAccount;
import org.compiere.model.MConversionRate;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPayment;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

import fr.idempiere.model.MLFRPaySelectionPrepayment;
import fr.idempiere.util.LfrUtil;


public class LfrCallout implements IColumnCallout {

	private static CLogger s_log = CLogger.getCLogger (LfrCallout.class);

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		String tableName = mTab.getTableName();
		String columnName = mField.getColumnName();

		if (tableName.equals(MBPBankAccount.Table_Name)) {
			if (columnName.equals(MBPBankAccount.COLUMNNAME_C_BPartner_ID) && mTab.getParentTab() == null)
				onBPBankAccountBPartnerID(mTab, value);
		}
		else if (tableName.equals(MLFRPaySelectionPrepayment.Table_Name)) {
			if (columnName.equals(MLFRPaySelectionPrepayment.COLUMNNAME_C_Payment_ID))
				onPaySelectionPrepaymentPaymentID(ctx, mTab, value);
		}
		else if (tableName.equals(MInvoiceLine.Table_Name)) {
			if (columnName.equals(C_INVOICELINE_LFR_IMPUTATIONDATEDEB))
				onInvoiceLineImputationDateDeb(ctx, mTab, value);
			else if (columnName.equals(C_INVOICELINE_LFR_IMPUTATIONDATEFIN))
				onInvoiceLineImputationDateFin(ctx, mTab, value);
		}

		return "";
	}

	private void onInvoiceLineImputationDateDeb(Properties ctx, GridTab mTab, Object value) {

		if (value != null && mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEFIN) != null)
			checkInvoiceLineImputationDates(ctx, mTab);
	}

	private void onInvoiceLineImputationDateFin(Properties ctx, GridTab mTab, Object value) {

		if (value != null && mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEDEB) != null)
			checkInvoiceLineImputationDates(ctx, mTab);
	}

	private void checkInvoiceLineImputationDates(Properties ctx, GridTab mTab) {

		Timestamp deb = (Timestamp) mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEDEB);
		Timestamp fin = (Timestamp) mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEFIN);

		String err = LfrUtil.checkInvoiceLineImputation(ctx, deb, fin);

		if (Util.isEmpty(err)) { // on s'assure qu'il n'y a pas trop d'écart entre les dates saisies
			int nbDays = TimeUtil.getDaysBetween(deb, fin);
			if (nbDays > 3650)
				mTab.fireDataStatusEEvent("Warning", "Plus de 10 ans entre les dates d'imputation", false);
		}
		else
			mTab.fireDataStatusEEvent("Warning", err, false);
	}

	private void onBPBankAccountBPartnerID(GridTab mTab, Object value) {
		mTab.setValue(MBPBankAccount.COLUMNNAME_A_Name, value == null ? "" : DB.getSQLValueStringEx(null, "SELECT Name FROM C_BPartner WHERE C_BPartner_ID = ?", value));
	}

	private void onPaySelectionPrepaymentPaymentID(Properties ctx, GridTab mTab, Object value) {
		if (value != null) {
			MPayment p = new MPayment(ctx, (Integer) value, null);
			int currentyToID = MBankAccount.get(ctx, (Integer) mTab.getParentTab().getValue("C_BankAccount_ID")).getC_Currency_ID();
			BigDecimal amt = MConversionRate.convert (ctx, p.getPayAmt(), p.getC_Currency_ID(), currentyToID, p.getDateAcct(), 0, p.getAD_Client_ID(), 0);
			mTab.setValue(MLFRPaySelectionPrepayment.COLUMNNAME_C_BPartner_ID, p.getC_BPartner_ID());
			mTab.setValue(MLFRPaySelectionPrepayment.COLUMNNAME_PayAmt, amt);
			mTab.setValue(MLFRPaySelectionPrepayment.COLUMNNAME_Description, p.getDescription());
		}
	}
}
