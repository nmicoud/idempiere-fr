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

import org.compiere.model.MBankAccount;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPayment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;

import fr.idempiere.model.MLFRPaySelectionPrepayment;

public class LFR_PaySelectionPrepaymentAdd extends LfrProcess
{
	private int p_bpartnerID = 0;
	private Timestamp p_payDateFrom = null;
	private Timestamp p_payDateTo = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (name.equals("C_BPartner_ID"))
				p_bpartnerID = para[i].getParameterAsInt();
			else if (name.equals("PayDate")) {
				p_payDateFrom = para[i].getParameterAsTimestamp();
				p_payDateTo = para[i].getParameter_ToAsTimestamp();
			}
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws Exception {
		MPaySelection ps = new MPaySelection (getCtx(), getRecord_ID(), get_TrxName());

		ArrayList<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder("SELECT p.C_Payment_ID, p.C_BPartner_ID,") // 1
				.append(" currencyConvertPayment(p.C_Payment_ID, ?, p.PayAmt, null) AS PayAmt,")
				.append(" p.Description")
				.append(" FROM C_Payment p")
				.append(" WHERE p.AD_Client_ID = ? AND p.IsReceipt = 'N' AND p.DocStatus IN ('CO', 'CL') AND p.PayAmt <> 0 AND p.TenderType = ?")
				.append(" AND NOT EXISTS (SELECT 1 FROM LFR_PaySelectionPrepayment psp WHERE p.C_Payment_ID = psp.C_Payment_ID AND psp.IsActive = 'Y')")
				.append(" AND p.C_Invoice_ID IS NULL")
				.append(" AND NOT EXISTS (SELECT 1 FROM C_PaymentAllocate pa WHERE p.C_Payment_ID = pa.C_Payment_ID)")
				.append(" AND NOT EXISTS (SELECT 1 FROM C_AllocationLine al WHERE p.C_Payment_ID = al.C_Payment_ID)");

		params.add(MBankAccount.get(getCtx(), ps.getC_BankAccount_ID()).getC_Currency_ID());
		params.add(getAD_Client_ID());
		params.add(MPayment.TENDERTYPE_DirectDeposit);

		if (p_bpartnerID > 0) {
			sql.append(" AND p.C_BPartner_ID = ?");
			params.add(p_bpartnerID);
		}
		if (p_payDateFrom != null) {
			sql.append(" AND p.DateTrx >= ?");
			params.add(p_payDateFrom);
		}
		if (p_payDateTo != null) {
			sql.append(" AND p.DateTrx <= ?");
			params.add(p_payDateTo);
		}

		int cnt = 0;
		List<List<Object>> rows = DB.getSQLArrayObjectsEx(null, sql.toString(), params.toArray());
		if (rows != null && rows.size() > 0) {
			for (List<Object> row : rows) {

				int paymentID = ((BigDecimal) row.get(0)).intValue();
				int bpartnerID = ((BigDecimal) row.get(1)).intValue();
				BigDecimal payAmt = (BigDecimal) row.get(2);
				String description = (String) row.get(3);

				MLFRPaySelectionPrepayment psp = new MLFRPaySelectionPrepayment(getCtx(), 0, get_TrxName());
				psp.setC_PaySelection_ID(ps.getC_PaySelection_ID());
				psp.setAD_Org_ID(ps.getAD_Org_ID());
				psp.setC_Payment_ID(paymentID);
				psp.setC_BPartner_ID(bpartnerID);
				psp.setPayAmt(payAmt);
				psp.setDescription(description);
				psp.saveEx();
				cnt++;
			}
		}

		return new StringBuilder("@LFR_PaySelectionPrepayment_ID@  - #").append(cnt).toString();
	} // doIt

} // LFR_PaySelectionPrepaymentAdd