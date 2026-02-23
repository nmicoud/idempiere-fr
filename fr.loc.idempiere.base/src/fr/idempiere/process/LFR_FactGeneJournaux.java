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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.compiere.model.MGLCategory;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import fr.idempiere.model.MTLFRReport;


/**
 *	Process de préparation des journaux de compta géné
 *	Les états jrxml lisent ensuite la table pour l'affichage des infos
 *  @author Nicolas Micoud - TGI
 */

public class LFR_FactGeneJournaux extends LfrProcessFact {

	private int			lines = 0;
	private int			p_GL_Category_ID = 0;
	private Timestamp	p_DateAcct_From = null;
	private Timestamp	p_DateAcct_To = null;
	private boolean		p_isJournalCent = false;
	private boolean		p_isGroupByRecord = false; // Regrouper les écritures par document (journal) - détermine le sql utilisé
	private boolean		p_isAccountDetail = false; // Afficher par compte (journal centralisateur) - met à jour IsSummary dans T_LFR_Report
	private String		language = "";
	private boolean		baseLanguage = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		super.prepare();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("LFR_IsJournalCentralisateur"))
				p_isJournalCent = para[i].getParameterAsBoolean();
			else if (name.equals("LFR_ProcessParaDateAcct")) {
				p_DateAcct_From = para[i].getParameterAsTimestamp();
				p_DateAcct_To = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("GL_Category_ID"))
				p_GL_Category_ID = para[i].getParameterAsInt();
			else if (name.equals("LFR_IsGroupByRecord"))
				p_isGroupByRecord = para[i].getParameterAsBoolean();
			else if (name.equals("LFR_IsAccountDetail"))
				p_isAccountDetail = para[i].getParameterAsBoolean();

			language = Env.getAD_Language(Env.getCtx());
		}
	}	//	prepare

	/**
	 * 	Execute
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		int sequenceID = MTLFRReport.getSequenceID(get_TrxName());

		if (language.equals(Language.getBaseAD_Language()))
			baseLanguage=true;

		if (p_isGroupByRecord)
			setFooterCenter(Msg.getMsg(getCtx(), "LFR_EcrituresGroupeesParCompte"));

		// les paramètres optionnels
		StringBuilder sqlWhere = new StringBuilder("");
		sqlWhere.append(getSqlWhereOrg("fa"));

		//Sélection des GL_Cat concernés par l'édition
		StringBuilder sql1 = new StringBuilder("SELECT DISTINCT fa.GL_Category_ID, gl.Value")
				.append(" FROM Fact_Acct fa")
				.append(" INNER JOIN GL_Category gl ON (fa.GL_Category_ID = gl.GL_Category_ID)")
				.append(" WHERE fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
				.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType))
				.append(sqlWhere);

		if (p_DateAcct_From != null)
			sql1.append(" AND fa.DateAcct >= ").append(DB.TO_DATE(p_DateAcct_From, true));
		if (p_DateAcct_To != null)
			sql1.append(" AND fa.DateAcct <= ").append(DB.TO_DATE(p_DateAcct_To, true));
		if (p_GL_Category_ID > 0) // si sélection d'un seul journal
			sql1.append(" AND fa.GL_Category_ID = ").append(p_GL_Category_ID);
		sql1.append(" ORDER BY gl.Value");

		int[] glCatIDs = DB.getIDsEx(get_TrxName(), sql1.toString());

		for (int glCatID : glCatIDs) {
			MGLCategory gl = MGLCategory.get(getCtx(), glCatID);
			statusUpdate(gl.getName());
			String affich = Util.isEmpty(gl.getPrintName()) ? gl.getName() : gl.getPrintName();
			String code = gl.getValue();

			MTLFRReport taf = null;
			String glCatDisplayName = "";
			if (!Util.isEmpty(code))
				glCatDisplayName = "(" + code + ") ";
			glCatDisplayName += affich;

			String criteresDate = getDateCriteres(getAD_Client_ID(), p_DateAcct_From, p_DateAcct_To);
			resetReportTitle();

			StringBuilder sql2 = new StringBuilder("");
			if (!p_isJournalCent) {
				forceTitleCenter(Msg.getElement(getCtx(), "GL_Category_ID") + " " + affich + (Util.isEmpty(code) ? "" : " (" + code + ")"));
				String reportTitle = getReportTitle();

				StringBuilder s_insert = new StringBuilder("INSERT INTO T_LFR_Report ")
						.append("(T_LFR_Report_ID, AD_PInstance_ID, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, ")
						.append(" Fact_Acct_ID, BPName, AccountValue, GL_Category_ID, AmtAcctDr, AmtAcctCr, DateAcct, LFR_FactAcctDescription,")
						.append(" LFR_FactAcctOrg, Account_Name, LFR_GLCategoryPrintName, ClientName, OrgName, FooterCenter, Title, LFR_DateAsString, LFR_NumEcriture, C_AcctSchema_ID, PostingType)");

				if (p_isGroupByRecord) {
					int dt_bs = getDocTypeForBankStatement();
					int dt_a = getDocTypeForAllocation();

					sql2.append(s_insert).append(" SELECT nextidfunc(" + sequenceID + ",'N'), ").append(getAD_PInstance_ID()).append(", ").append(getAD_Client_ID()).append(", ")
					.append("0, SysDate, 0, SysDate, ").append(getAD_User_ID()).append(", ")
							.append(" NULL, RESULT.BPName, RESULT.AccountValue,")
							.append(" RESULT.GL_Category_ID, RESULT.AmtAcctDr, RESULT.AmtAcctCr, RESULT.DateAcct, RESULT.Description,")
							.append(" NULL, RESULT.AccountName, ")	// pas d'organisation sur la ligne
							.append(DB.TO_STRING(glCatDisplayName)).append(", ").append(DB.TO_STRING(getClientName())).append(", ").append(DB.TO_STRING(getOrgName())).append(", ")
							.append(DB.TO_STRING(getFooterCenter())).append(", ").append(DB.TO_STRING(reportTitle)).append(", ").append(DB.TO_STRING(criteresDate))
							.append(", RESULT.LFR_NumEcriture")
							.append(", ").append(p_acctSchema_ID).append(", ").append(DB.TO_STRING(p_postingType))

							.append(" FROM (SELECT bp.Name BPName, ev.Value AccountValue,")
							.append(" fa.GL_Category_ID, SUM(fa.AmtAcctDr) AmtAcctDr, SUM(fa.AmtAcctCr) AmtAcctCr, fa.DateAcct, ")
							// DocTypeName et N° doc
							.append(" CASE WHEN fa.AD_Table_ID = 318 THEN dt.PrintName || ' ' || i.DocumentNo")
							.append(" WHEN fa.AD_Table_ID = 335 THEN dt.PrintName || ' ' || p.DocumentNo")
							.append(" WHEN fa.AD_Table_ID = 224 THEN dt.PrintName || ' ' || j.DocumentNo")
							.append(" WHEN fa.AD_Table_ID = 392 THEN dt.PrintName || ' ' || bs.Name")
							.append(" WHEN fa.AD_Table_ID = 735 THEN dt.PrintName || ' ' || a.DocumentNo")
							.append(" END Description,")
							.append(" ev").append(baseLanguage ? "" : "t").append(".Name AccountName,")	// on vient lire le nom du compte sur la table principale ou la table trl
							.append(" COALESCE(pvf.LFR_NumEcriture, 0) LFR_NumEcriture")

							.append(" FROM Fact_Acct fa")
							.append(" LEFT OUTER JOIN C_Invoice i ON (fa.Record_ID = i.C_Invoice_ID AND fa.AD_Table_ID = 318)")
							.append(" LEFT OUTER JOIN C_Payment p ON (fa.Record_ID = p.C_Payment_ID AND fa.AD_Table_ID = 335)")
							.append(" LEFT OUTER JOIN GL_Journal j ON (fa.Record_ID = j.GL_Journal_ID AND fa.AD_Table_ID = 224)")
							.append(" LEFT OUTER JOIN C_BankStatement bs ON (fa.Record_ID = bs.C_BankStatement_ID AND fa.AD_Table_ID = 392)")
							.append(" LEFT OUTER JOIN C_AllocationHdr a ON (fa.Record_ID = a.C_AllocationHdr_ID AND fa.AD_Table_ID = 735)")
							.append(" LEFT OUTER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID AND fa.AD_Table_ID <> 392)") // on ne prend pas en compte le tiers figurant sur le rapprochement bancaire (empêche le group by)
							.append(" LEFT OUTER JOIN LFR_PeriodValidationFact pvf ON (fa.Fact_Acct_ID = pvf.Fact_Acct_ID),")
							.append(" AD_Client c, AD_Org o, GL_Category gl, C_ElementValue ev, " + (baseLanguage ? "" : "C_ElementValue_Trl evt, "))
							.append(" C_DocType").append((baseLanguage ? "" : "_Trl ") + " dt")	// soit C_DocType soit C_DocType_Trl

							.append(" WHERE fa.AD_Client_ID = c.AD_Client_ID")
							.append(" AND fa.AD_Org_ID = o.AD_Org_ID")
							.append(" AND fa.GL_Category_ID = gl.GL_Category_ID")
							.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
							.append(" AND fa.Account_ID = ev.C_ElementValue_ID ")
							.append(baseLanguage ? "" : " AND fa.Account_ID = evt.C_ElementValue_ID AND evt.AD_Language= " + DB.TO_STRING(language))
							// DocTypeName et N° doc
							.append(" AND (")
							.append("    (fa.AD_Table_ID = 318 AND dt.C_DocType_ID = i.C_DocType_ID)")
							.append(" OR (fa.AD_Table_ID = 335 AND dt.C_DocType_ID = p.C_DocType_ID)")
							.append(" OR (fa.AD_Table_ID = 224 AND dt.C_DocType_ID = j.C_DocType_ID)")
							.append(" OR (fa.AD_Table_ID = 392 AND dt.C_DocType_ID = " + dt_bs + ")")
							.append(" OR (fa.AD_Table_ID = 735 AND dt.C_DocType_ID = " + dt_a + "))")
							.append((baseLanguage ? "" : " AND dt.AD_Language=" + DB.TO_STRING(language)))

							.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType)).append(sqlWhere);
					if (p_DateAcct_From != null)
						sql2.append(" AND fa.DateAcct >= ").append(DB.TO_DATE(p_DateAcct_From, true));
					if (p_DateAcct_To != null)
						sql2.append(" AND fa.DateAcct <= ").append(DB.TO_DATE(p_DateAcct_To, true));
					sql2.append(" AND fa.GL_Category_ID = " + glCatID)
					.append(" GROUP BY fa.GL_Category_ID, fa.DateAcct, ev.Value, fa.Account_ID, fa.AD_Table_ID, fa.Record_ID, bp.Name, ev").append((baseLanguage ? "" : "t")) .append(".Name, ")
					.append("dt.printname, i.DocumentNo, p.DocumentNo, j.DocumentNo, bs.Name, a.DocumentNo, pvf.LFR_NumEcriture")
					.append(" ORDER BY fa.DateAcct, fa.Record_ID, ev.Value")
					.append(") RESULT");
				} else { // pas de regroupement
					sql2.append(s_insert).append(" SELECT nextidfunc(" + sequenceID + ",'N'), ").append(getAD_PInstance_ID()).append(", ").append(getAD_Client_ID()).append(", ")
					.append("0, SysDate, 0, SysDate, ").append(getAD_User_ID()).append(", ")
					.append(" RESULT.Fact_Acct_ID, RESULT.BPName, RESULT.AccountValue,")
					.append(" RESULT.GL_Category_ID, RESULT.AmtAcctDr, RESULT.AmtAcctCr, RESULT.DateAcct, RESULT.Description,")
					.append(" RESULT.OrgName, RESULT.AccountName, ")
					.append(DB.TO_STRING(glCatDisplayName)).append(", ").append(DB.TO_STRING(getClientName())).append(", ").append(DB.TO_STRING(getOrgName())).append(", ")
					.append(DB.TO_STRING(getFooterCenter())).append(", ").append(DB.TO_STRING(reportTitle)).append(", ").append(DB.TO_STRING(criteresDate))
					.append(", RESULT.LFR_NumEcriture")
					.append(", ").append(p_acctSchema_ID).append(", ").append(DB.TO_STRING(p_postingType))

					.append(" FROM (SELECT fa.Fact_Acct_ID, bp.Name BPName, ev.Value AccountValue,")
					.append(" fa.GL_Category_ID, fa.AmtAcctDr, fa.AmtAcctCr, fa.DateAcct, fa.Description,")
					.append(" o.Name OrgName, ev").append((baseLanguage ? "" : "t")).append(".Name AccountName")
					.append(", COALESCE(pvf.LFR_NumEcriture, 0) LFR_NumEcriture")

					.append(" FROM Fact_Acct fa")
					.append(" LEFT OUTER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID)")
					.append(" LEFT OUTER JOIN LFR_PeriodValidationFact pvf ON (fa.Fact_Acct_ID = pvf.Fact_Acct_ID)")
					.append(", AD_Client c, AD_Org o, GL_Category gl, C_ElementValue ev").append(baseLanguage ? "" : ", C_ElementValue_Trl evt")
					.append(" WHERE fa.AD_Client_ID = c.AD_Client_ID")
					.append(" AND fa.AD_Org_ID = o.AD_Org_ID")
					.append(" AND fa.GL_Category_ID = gl.GL_Category_ID")
					.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
					.append(" AND fa.Account_ID = ev.C_ElementValue_ID ")
					.append((baseLanguage ? "" : " AND fa.Account_ID = evt.C_ElementValue_ID AND evt.AD_Language=" + DB.TO_STRING(language)))
					.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType)).append(sqlWhere);
					if (p_DateAcct_From != null)
						sql2.append(" AND fa.DateAcct >= " + DB.TO_DATE(p_DateAcct_From, true));
					if (p_DateAcct_To != null)
						sql2.append(" AND fa.DateAcct <= " + DB.TO_DATE(p_DateAcct_To, true));
					sql2.append(" AND fa.GL_Category_ID = " + glCatID)
					.append(" ORDER BY gl.PrintName, fa.DateAcct NULLS FIRST, fa.Record_ID, ev.Value, fa.Line_ID")
							.append(") RESULT");
				}
			} else { // journal centralisateur
				
				forceTitleCenter("Journal Centralisateur");
				
				StringBuilder groupByClause = new StringBuilder(" GROUP BY fa.GL_Category_ID, gl.PrintName");
				if (p_isAccountDetail)
					groupByClause.append(", fa.Account_ID, fa.AccountValue, fa.Name");

				StringBuilder orderByClause = new StringBuilder(" ORDER BY gl.PrintName");
				if (p_isAccountDetail)
					orderByClause.append(", fa.AccountValue");

				sql2 = new StringBuilder("SELECT fa.GL_Category_ID, COALESCE(SUM(fa.AmtAcctDr),0), COALESCE(SUM(fa.AmtAcctCr),0)");
				if (p_isAccountDetail)
					sql2.append(", fa.AccountValue, fa.Name");

				sql2.append(" FROM RV_Fact_Acct fa, GL_Category gl")
				.append(" WHERE fa.GL_Category_ID = gl.GL_Category_ID")
				.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID) 
				.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType)).append(sqlWhere);
				if (p_DateAcct_From != null)
					sql2.append(" AND fa.DateAcct >= ").append(DB.TO_DATE(p_DateAcct_From, true));
				if (p_DateAcct_To != null)
					sql2.append(" AND fa.DateAcct <= ").append(DB.TO_DATE(p_DateAcct_To, true));
				sql2.append(" AND fa.GL_Category_ID = ").append(glCatID)
				.append(groupByClause).append(orderByClause);
			}

			//Spé détail journaux
			if (!p_isJournalCent) // on remplit la table par du sql
				lines = lines + DB.executeUpdate(sql2.toString(), get_TrxName());
			else { // journal cent ; la table se remplit en java

				PreparedStatement pstmt2 = null;
				ResultSet rs2 = null;

				try {
					pstmt2 = DB.prepareStatement(sql2.toString(), get_TrxName());
					rs2 = pstmt2.executeQuery ();

					while (rs2.next()) {
						taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
						taf.setClientName(getClientName());
						taf.setOrgName(getOrgName());
						taf.setC_AcctSchema_ID(p_acctSchema_ID);
						taf.setPostingType(p_postingType);
						taf.setLine(lines++);
						taf.setLFR_GLCategoryPrintName(glCatDisplayName);
						taf.setLFR_DateAsString(criteresDate);
						taf.setTitle(getReportTitle());
						taf.setFooterCenter(getFooterCenter());
						taf.setGL_Category_ID(rs2.getInt(1));
						taf.setAmtAcctDr(rs2.getBigDecimal(2));
						taf.setAmtAcctCr(rs2.getBigDecimal(3));
						taf.setIsSummary(p_isAccountDetail);
						if (p_isAccountDetail){
							taf.setAccountValue(rs2.getString(4));
							taf.setAccount_Name(rs2.getString(5));
						} 
						taf.saveEx();
					}
				}
				catch(Exception e) {
					log.severe("Error while inserting data " + e);
				}
				finally {
					DB.close(rs2, pstmt2);
				}
			}
		}

		return "@ProcessOK@";
	}	//	doIt

}	//	LFR_FactGeneJournaux
