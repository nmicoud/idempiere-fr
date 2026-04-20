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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

import fr.idempiere.util.LfrFactReconciliationUtil;
import fr.idempiere.util.LfrUtil;

/**
 *	Process de lettrage des écritures comptables
 *  @author Nicolas Micoud - TGI
 */

public class LFR_Reconcile extends LfrProcess {

	private String p_type = "";
	private int p_acctSchema_ID = 0;
	private Timestamp p_docDateFrom = null;
	private Timestamp p_docDateTo = null;
	private Timestamp p_factAcctUpdatedFrom = null;
	private Timestamp p_factAcctUpdatedTo = null;
	private int p_elementValueID = 0;
	private int p_bpartnerID = 0;

	private final String RECONCILIATION_TYPE_BPARTNER = "1";

	protected void prepare() {

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (name.equals("Type"))
				p_type = para[i].getParameterAsString();
			else if (name.equals("C_AcctSchema_ID"))
				p_acctSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("DateDoc")) {
				p_docDateFrom = para[i].getParameterAsTimestamp();
				p_docDateTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("fact_acct_updated")) {
				p_factAcctUpdatedFrom = para[i].getParameterAsTimestamp();
				p_factAcctUpdatedTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("C_ElementValue_ID"))
				p_elementValueID = para[i].getParameterAsInt();
			else if (name.equals("C_BPartner_ID"))
				p_bpartnerID = para[i].getParameterAsInt();
		}
	}	//	prepare

	protected String doIt() {

		if (p_type.equals(RECONCILIATION_TYPE_BPARTNER)) {
			/*		
			 #1 : prendre les écritures non lettrées des factures/règlements (payée/affectés) qui concernent
			  - un AccountID ou tous les comptes de type ev.BPartnerType IS NOT NULL
			  - fa.DateAcct >= ? AND fa.DateAcct <= ?
			  - Document.Updated >= ? AND Document.Updated <= ?
			  - Posted = Y avec PostingType = A
			 */			  
			StringBuilder whereClause = new StringBuilder("Fact_Acct.PostingType = ? AND Fact_Acct.AD_Client_ID=? AND l.MatchCode IS NULL AND (i.isPaid='Y' OR p.isAllocated='Y' OR (i.isPaid IS NULL AND p.isAllocated IS NULL))")
					.append(" AND Fact_Acct.AD_Table_ID IN (").append(MInvoice.Table_ID).append(", ").append(MPayment.Table_ID).append(", ").append(MAllocationHdr.Table_ID).append(")")
					.append(" AND NOT EXISTS (SELECT fr.Fact_Acct_ID FROM Fact_Reconciliation fr WHERE Fact_Acct.Fact_Acct_ID = fr.Fact_Acct_ID)");
Timestamp start = new Timestamp (System.currentTimeMillis());
			// FIXME ?? Impossible vu que colonnes obligatoires ?
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(MFactAcct.POSTINGTYPE_Actual);
			params.add(getAD_Client_ID()); // TODO ajouter un paramètre clientID pour que le process puisse tourner en System

			if (p_acctSchema_ID > 0) {
				whereClause.append(" AND Fact_Acct.C_AcctSchema_ID=?");
				params.add(p_acctSchema_ID);
			}
			if (p_elementValueID > 0) {
				whereClause.append(" AND Fact_Acct.Account_ID=?");
				params.add(p_elementValueID);
			}
			else {
				whereClause.append(" AND Fact_Acct.Account_ID IN (SELECT C_ElementValue_ID FROM C_ElementValue WHERE BPartnerType IS NOT NULL AND AD_Client_ID=Fact_Acct.AD_Client_ID)");
			}

			if (p_bpartnerID > 0) {
				whereClause.append(" AND Fact_Acct.C_BPartner_ID=?");
				params.add(p_bpartnerID);
			}
			if (p_docDateFrom != null) {
				whereClause.append(" AND (TRUNC(i.DateAcct) >= ? OR TRUNC(p.DateAcct) >= ?)");
				params.add(p_docDateFrom);
				params.add(p_docDateFrom);
			}

			if (p_docDateTo != null) {
				whereClause.append(" AND (TRUNC(i.DateAcct) <= ? OR TRUNC(i.DateAcct) <= ?)");
				params.add(p_docDateTo);
				params.add(p_docDateTo);
			}
			if (p_factAcctUpdatedFrom != null) {
				whereClause.append(" AND TRUNC(Fact_Acct.Updated) >= ?");
				params.add(p_factAcctUpdatedFrom);
			}
			if (p_factAcctUpdatedTo != null) {
				whereClause.append(" AND TRUNC(Fact_Acct.Updated) <= ?");
				params.add(p_factAcctUpdatedTo);
			}

			Query query = new Query(getCtx(), MFactAcct.Table_Name, whereClause.toString(), get_TrxName())
					.addJoinClause("LEFT OUTER JOIN Fact_Reconciliation l ON (Fact_Acct.Fact_Acct_id = l.Fact_Acct_ID)")
					.addJoinClause("LEFT OUTER JOIN C_Invoice i on (Fact_Acct.AD_Table_ID=318 AND Fact_Acct.Record_ID=i.C_Invoice_ID)")
					.addJoinClause("LEFT OUTER JOIN C_Payment p on (Fact_Acct.AD_Table_ID=335 AND Fact_Acct.Record_ID=p.C_Payment_ID)")
					.setParameters(params)
					.setOrderBy("Fact_Acct.Account_ID, Fact_Acct.AD_Table_ID, Fact_Acct.Record_ID");

			System.out.println(query.getSQL());
			for (Object o : params)
				System.out.println(o);

//			int [] ids = query.getIDs();
			List<MFactAcct> listFA = query.list();

			int nbLettreesTotal = 0;

			int idx = 1;
			for (MFactAcct fa : listFA) {
				statusUpdate("Lettrage écritures " + idx++ + "/" + listFA.size() + " : " + MTable.get(getCtx(), fa.getAD_Table_ID()).get_Translation("Name") + " : " + LfrUtil.formatDate(getCtx(), getAD_Client_ID(), fa.getDateAcct()));

				try {
					int NbEcr = LfrFactReconciliationUtil.lettrageFactAcct(getCtx(), fa, true, get_TrxName());

					System.out.println(idx + "/" + listFA.size() + " " + TimeUtil.formatElapsed(start) + " : nbEcr = " + NbEcr);
					nbLettreesTotal = nbLettreesTotal + NbEcr;
					commitEx();
				}
				catch (Exception e) {
					throw new AdempiereUserError("Erreur lors du lettrage de : " + fa.toString() + " : " + e);
				}

			}
			System.out.println("nbLettreesTotal:" + nbLettreesTotal);

			// #2 : les affectations (qui ne produisent pas forcément d'écritures) Posted = Y
			// TODO il faut donc ne tester que celles qui n'ont pas d'écritures
			whereClause = new StringBuilder("C_AllocationHdr.AD_Client_ID = ? AND C_AllocationHdr.Processed = 'Y' AND C_AllocationHdr.Posted = 'Y'")
					.append("AND NOT EXISTS (SELECT 1 FROM Fact_Acct fa WHERE C_AllocationHdr.C_AllocationHdr_ID = fa.Record_ID AND AD_Table_ID = ").append(MAllocationHdr.Table_ID).append(")");
			params = new ArrayList<Object>();
			params.add(getAD_Client_ID());

			if (p_docDateFrom != null) {
				whereClause.append(" AND TRUNC(C_AllocationHdr.DateAcct) >= ?");
				params.add(p_docDateFrom);
			}

			if (p_docDateTo != null) {
				whereClause.append(" AND TRUNC(C_AllocationHdr.DateAcct) <= ?");
				params.add(p_docDateTo);
			}
			if (p_bpartnerID > 0) {
				whereClause.append("AND EXISTS (SELECT 1 FROM C_AllocationLine al WHERE C_AllocationHdr.C_AllocationHdr_ID = al.C_AllocationHdr_ID AND al.C_BPartner_ID = ?)");
				params.add(p_bpartnerID);
			}
			if (p_factAcctUpdatedFrom != null) {
				whereClause.append(" AND TRUNC(C_AllocationHdr.Updated) >= ?");
				params.add(p_factAcctUpdatedFrom);
			}
			if (p_factAcctUpdatedTo != null) {
				whereClause.append(" AND TRUNC(C_AllocationHdr.Updated) <= ?");
				params.add(p_factAcctUpdatedTo);
			}

			query = new Query(getCtx(), MAllocationHdr.Table_Name, whereClause.toString(), get_TrxName())
					.setParameters(params)
					.setOrderBy("C_AllocationHdr.C_AllocationHdr_ID");

			System.out.println(query.getSQL());
			for (Object o : params)
				System.out.println(o);

			List<MAllocationHdr> listAllocs = query.list();

			idx = 1;
			for (MAllocationHdr a : listAllocs) {
				statusUpdate("Lettrage affectation " + idx++ + "/" + listAllocs.size() + " : " + a.getDocumentInfo());
				System.out.println(idx + "/" + listAllocs.size() + " : " + TimeUtil.formatElapsed(start) + " : " + a.getDocumentInfo());
				String msg = LfrFactReconciliationUtil.lettrageAlloc(getCtx(), a, p_acctSchema_ID, p_elementValueID, get_TrxName());
				
				int nbLettrees = 0;
				
				if (!Util.isEmpty(msg) && msg.matches("[0-9]+"))
					nbLettrees = Integer.valueOf(msg);	

				//log.info("listAlloc="+i+"/"+listAlloc.size());
				if (nbLettrees>0)
					nbLettreesTotal += nbLettrees;
				else if (nbLettrees <0)	{ // si erreur, on renvoie -1
					return "@Error@ " + msg;
				}
			}
		}

		return "";
	}	//	doIt

}	//	LFR_Reconcile