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
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MElementValue;
import org.compiere.model.MOrg;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.Util;

import fr.idempiere.model.MTLFRReport;

/**
 *	Process de préparation de la balance générale
 *  @author Nicolas Micoud - TGI
 */

public class LFR_FactGeneBalance extends LfrProcess {

	private int			p_acctSchema_ID = 0;
	private int			p_orgID = 0;
	private String		p_postingType = "";
	private int 		p_accountFromID = 0;
	private int 		p_accountToID = 0;
	private Timestamp	p_dateAcctFrom = null;
	private Timestamp	p_dateAcctTo = null;
	private Timestamp	p_dateAcctPrecFrom = null;
	private Timestamp	p_dateAcctPrecTo = null;
	private boolean		p_isSummary = false;
	private String		p_balanceGeneRegrLevel = "";

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("C_AcctSchema_ID"))
				p_acctSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_orgID = para[i].getParameterAsInt();
			else if (name.equals("PostingType"))
				p_postingType = para[i].getParameterAsString();
			else if (name.equals("DateAcct")) {
				p_dateAcctFrom = para[i].getParameterAsTimestamp();
				p_dateAcctTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("LFR_DateAcctPrec")) {
				p_dateAcctPrecFrom = para[i].getParameterAsTimestamp();
				p_dateAcctPrecTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("Account_ID")) {
				p_accountFromID = para[i].getParameterAsInt();
				p_accountToID = para[i].getParameter_ToAsInt();
			}
			else if (name.equals("IsSummary"))
				p_isSummary = para[i].getParameterAsBoolean();
			else if (name.equals("LFR_BalanceGeneRegrLevel"))
				p_balanceGeneRegrLevel = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Execute
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		String orgName = "";
		String reportTitle = "Balance Générale";
		String footerCenter = "";
		int lines = 0;

		// on ajoute un tiret pour séparer les plages de date ; ce tiret est utilisé dans l'état Jasper
		String criteresDate = MTLFRReport.getDateCriteres(getCtx(), getAD_Client_ID(), p_dateAcctPrecFrom, p_dateAcctPrecTo) + "-" + MTLFRReport.getDateCriteres(getCtx(), getAD_Client_ID(), p_dateAcctFrom, p_dateAcctTo);

		StringBuilder sqlOrg = new StringBuilder("");
		if (p_orgID > 0) {
			orgName = MOrg.get(getCtx(), p_orgID).getName();	
			sqlOrg = new StringBuilder(" AND fa.AD_Org_ID = ?");
		}

		// Sélection des comptes concernés par l'édition
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> params2 = new ArrayList<Object>();

		StringBuilder sql1 = new StringBuilder("SELECT DISTINCT fa.Account_ID, fa.AccountValue FROM RV_Fact_Acct fa")
				.append(" WHERE fa.C_AcctSchema_ID = ?")
				.append(" AND fa.PostingType = ?")
				.append(" AND fa.DateAcct >= ?")
				.append(" AND fa.DateAcct <= ?");

		params.add(p_acctSchema_ID);
		params.add(p_postingType);
		params.add(p_dateAcctPrecFrom);
		params.add(p_dateAcctTo);

		if (p_orgID > 0) {
			sql1.append(sqlOrg);
			params2.add(p_orgID);
		}

		if (p_accountFromID > 0) {
			sql1.append(" AND fa.AccountValue >= (SELECT Value FROM C_ElementValue WHERE C_ElementValue_ID = ?)");
			params2.add(p_accountFromID);
		}

		if (p_accountToID > 0) {
			sql1.append(" AND fa.AccountValue <= (SELECT Value FROM C_ElementValue WHERE C_ElementValue_ID = ?)");
			params2.add(p_accountToID);
		}

		sql1.append(" ORDER BY fa.AccountValue");

		for (int accountID : DB.getIDsEx(get_TrxName(), sql1.toString(), joinParams(params, params2))) {
			MElementValue ev = new MElementValue (getCtx(), accountID, get_TrxName());
			String accountValue = ev.getValue();

			statusUpdate(accountValue + " - " + ev.getName());

			//Insertion de la ligne avec n° compte + CL1, CL2, CL3
			MTLFRReport taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
			taf.setLine(lines++);
			taf.setC_ElementValue_ID(accountID);
			if (p_orgID > 0)
				taf.setAD_Org_ID(p_orgID);
			taf.setClientName(MClient.get(getCtx(), getAD_Client_ID()).getName());
			taf.setOrgName(orgName);
			taf.setAccountValue(accountValue);
			taf.setAccount_Name(ev.getName());
			taf.setLFR_CL1(accountValue.substring(0,1));
			taf.setLFR_CL2(accountValue.substring(0,2));
			taf.setLFR_CL3(accountValue.substring(0,3));
			taf.setIsSummary(p_isSummary);
			if (!Util.isEmpty(p_balanceGeneRegrLevel) && !p_balanceGeneRegrLevel.equals("0"))
				taf.setLFR_BalanceGeneRegrLevel(p_balanceGeneRegrLevel);

			// Montants
			StringBuilder sql2 = new StringBuilder("SELECT SUM(fa.AmtAcctDr), SUM(fa.AmtAcctCr), SUM(fa.AmtAcctDr)-SUM(fa.AmtAcctCr)")
					.append(" FROM RV_Fact_Acct fa")
					.append(" WHERE fa.C_AcctSchema_ID = ?")
					.append(" AND fa.PostingType = ?")
					.append(" AND fa.Account_ID = ?");

			params.clear();
			params2.clear();
			params.add(p_acctSchema_ID);
			params.add(p_postingType);
			params.add(accountID);

			if (p_orgID > 0) {
				sql2.append(sqlOrg);
				params.add(p_orgID);
			}

			sql2.append(" AND fa.DateAcct >= ?")
			.append(" AND fa.DateAcct <= ?")
			.append(" GROUP BY fa.Account_ID");

			params2.add(p_dateAcctPrecFrom);
			params2.add(p_dateAcctPrecTo);

			// Calcul D/C/S N-1
			List<List<Object>> rows = DB.getSQLArrayObjectsEx(get_TrxName(), sql2.toString(), joinParams(params, params2));
			if (rows != null && rows.size() > 0) {
				for (List<Object> row : rows) {
					taf.setLFR_AmtAcctPrecDr((BigDecimal) row.get(0));
					taf.setLFR_AmtAcctPrecCr((BigDecimal) row.get(1));
					taf.setLFR_AmtAcctPrec((BigDecimal) row.get(2));
				}
			}

			params2.clear();
			params2.add(p_dateAcctFrom);
			params2.add(p_dateAcctTo);

			// Calcul D/C/S N
			rows = DB.getSQLArrayObjectsEx(get_TrxName(), sql2.toString(), joinParams(params, params2));
			if (rows != null && rows.size() > 0) {
				for (List<Object> row : rows) {
					taf.setAmtAcctDr((BigDecimal) row.get(0));
					taf.setAmtAcctCr((BigDecimal) row.get(1));
					taf.setAmtAcct((BigDecimal) row.get(2));
				}
			}

			taf.setFooterCenter(footerCenter);
			taf.setTitle(reportTitle);
			taf.setLFR_DateAsString(criteresDate);
			taf.saveEx();
		}

		return "@ProcessOK@";
	}	//	doIt

	private Object[] joinParams(ArrayList<Object> params, ArrayList<Object> params2) {
		ArrayList<Object> retValue = new ArrayList<Object>();
		retValue.addAll(params);
		retValue.addAll(params2);
		return retValue.toArray();
	}

}	//	LFR_FactGeneBalance