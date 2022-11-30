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

//import static org.tgi.model.SystemIDs_Tgi.PROCESS_XXA_FACTGENEJOURNALCENT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MGLCategory;
import org.compiere.model.MOrg;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import fr.idempiere.model.MTLFRReport;
//import org.tgi.util.Util_Tgi;


/**
 *	Process de préparation des journaux de compta géné
 *	Les états jrxml lisent ensuite la table pour l'affichage des infos
 *  @author Nico
 */


// TODO : remplacer les String par des StringBuilder
// TODO : ajouter un param IsJournalCentralisateur

public class LFR_FactGeneJournaux extends LfrProcess {

	private int			p_C_AcctSchema_ID = 0;
	private int			p_AD_Org_ID = 0;
	private String		p_PostingType = "";
	private int			lines = 0;
	private String 		orgName = ""; // l'organisation pour laquelle on a demandé l'édition ; différente de OrgTrxName, organisation de la ligne de compta
	private int			p_GL_Category_ID = 0;
	private Timestamp	p_DateAcct_From = null;
	private Timestamp	p_DateAcct_To = null;
	private boolean		isJournalCent = false;
	private boolean		p_isGroupByRecord = false; // Regrouper les écritures par document (journal) - détermine le sql utilisé
	private boolean		p_isAccountDetail = false; // Afficher par compte (journal centralisateur) - met à jour IsSummary dans T_XXA_Report
	private String		language = "";
	private boolean		baseLanguage = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("C_AcctSchema_ID"))
				p_C_AcctSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("PostingType"))
				p_PostingType = (String)para[i].getParameter();
			else if (name.equals("DateAcct")) {
				p_DateAcct_From = para[i].getParameterAsTimestamp();
				p_DateAcct_To = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("GL_Category_ID"))
				p_GL_Category_ID = para[i].getParameterAsInt();
			else if (name.equals("LFR_IsGroupByRecord"))
				p_isGroupByRecord = para[i].getParameterAsBoolean();
			else if (name.equals("LFR_IsAccountDetail"))
				p_isAccountDetail = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);

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
		String clientName = MClient.get(getCtx(), getAD_Client_ID()).getName();
		String reportTitle = ""; // le titre de l'état (repris dans le Page Header)
		int sequenceID = MTLFRReport.getSequenceID(get_TrxName()); // TODO à faire dans le SQL directement
		String footerCenter = "";

		if (language.equals(Language.getBaseAD_Language()))
			baseLanguage=true;

		// FIXME : remplacer par un nouveau paramètre IsJournalCentralisateur
		//			if (Util_Tgi.getProcessID(PROCESS_XXA_FACTGENEJOURNALCENT, get_TrxName()) == getProcessInfo().getAD_Process_ID()) {
		//				isJournalCent = true;
		//				reportTitle = "Journal Centralisateur"; // translate	
		//			}

		if (p_isGroupByRecord)
			footerCenter = Msg.getMsg(getCtx(), "LFR_EcrituresGroupeesParCompte");

		// les paramètres optionnels
		String sqlWhere = "";
		if (p_AD_Org_ID > 0) {
			orgName = MOrg.get(getCtx(), p_AD_Org_ID).getName();
			sqlWhere += " AND fa.AD_Org_ID = " + p_AD_Org_ID + " ";
		}

		//Sélection des GL_Cat concernés par l'édition
		StringBuilder sql1 = new StringBuilder("SELECT DISTINCT fa.GL_Category_ID, gl.Code")
				.append(" FROM Fact_Acct fa")
				.append(" INNER JOIN GL_Category gl ON (fa.GL_Category_ID = gl.GL_Category_ID)")
				.append(" WHERE fa.C_AcctSchema_ID = ").append(p_C_AcctSchema_ID)
				.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_PostingType))
				.append(sqlWhere);

		if (p_DateAcct_From != null)
			sql1.append(" AND fa.DateAcct >= ").append(DB.TO_DATE(p_DateAcct_From, true));
		if (p_DateAcct_To != null)
			sql1.append(" AND fa.DateAcct <= ").append(DB.TO_DATE(p_DateAcct_To, true));
		if (p_GL_Category_ID > 0) // si sélection d'un seul journal
			sql1.append(" AND fa.GL_Category_ID = ").append(p_GL_Category_ID);
		sql1.append(" ORDER BY gl.Code");

		int[] glCatIDs = DB.getIDsEx(get_TrxName(), sql1.toString());

		for (int glCatID : glCatIDs) {
			MGLCategory gl = MGLCategory.get(getCtx(), glCatID);
			statusUpdate(gl.getName());
			String affich = Util.isEmpty(gl.get_ValueAsString("PrintName")) ? gl.getName() : gl.get_ValueAsString("PrintName");
			String code = gl.get_ValueAsString("Code");

			MTLFRReport taf = null;
			String printName = "";
			if (!Util.isEmpty(code))
				printName = "(" + code + ") ";
			printName += affich;

			String criteresDate = MTLFRReport.getDateCriteres(getCtx(), getAD_Client_ID(), p_DateAcct_From, p_DateAcct_To);

			String sql2 = "";
			if (!isJournalCent) {
				reportTitle = Msg.getElement(getCtx(), "GL_Category_ID") + " " + affich + (Util.isEmpty(code) ? "" : " (" + code + ")");

				String s_insert = "INSERT INTO T_LFR_Report "
						+ "(T_LFR_Report_ID, AD_PInstance_ID, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, "
						+ " Fact_Acct_ID, BPName, AccountValue, GL_Category_ID, AmtAcctDr, AmtAcctCr, DateAcct, Description,"
						+ " LFR_FactAcctOrg, Account_Name, PrintName, ClientName, OrgName, FooterCenter, Title, LFR_DateAsString/*, XXA_NumEcriture*/)"; // FEC

				if (p_isGroupByRecord) {
					int dt_bs = MTLFRReport.getDocTypeForBankStatement(getCtx());
					int dt_a = MTLFRReport.getDocTypeForAllocation(getCtx());

					sql2 = s_insert + " SELECT nextidfunc(" + sequenceID + ",'N'), " + getAD_PInstance_ID() + ", " + getAD_Client_ID() + ", " + (p_AD_Org_ID > 0 ? p_AD_Org_ID : 0) + ", SysDate, 0, SysDate, " + getAD_User_ID() + ", "
							+ " NULL, RESULT.BPName, RESULT.AccountValue,"
							+ " RESULT.GL_Category_ID, RESULT.AmtAcctDr, RESULT.AmtAcctCr, RESULT.DateAcct, RESULT.Description,"
							+ " NULL, RESULT.AccountName, "	// pas d'organisation sur la ligne
							+ DB.TO_STRING(printName) + ", " + DB.TO_STRING(clientName) + ", " + DB.TO_STRING(orgName) + ", "
							+ DB.TO_STRING(footerCenter) + ", " + DB.TO_STRING(reportTitle) + ", " + DB.TO_STRING(criteresDate)
							//+ ", RESULT.XXA_NumEcriture" FEC

							+ " FROM (SELECT bp.Name BPName, ev.Value AccountValue,"
							+ " fa.GL_Category_ID, SUM(fa.AmtAcctDr) AmtAcctDr, SUM(fa.AmtAcctCr) AmtAcctCr, fa.DateAcct, "
							// DocTypeName et N° doc
							+ " CASE WHEN fa.AD_Table_ID = 318 THEN dt.PrintName || ' ' || i.DocumentNo"
							+ " WHEN fa.AD_Table_ID = 335 THEN dt.PrintName || ' ' || p.DocumentNo"
							+ " WHEN fa.AD_Table_ID = 224 THEN dt.PrintName || ' ' || j.DocumentNo"
							+ " WHEN fa.AD_Table_ID = 392 THEN dt.PrintName || ' ' || bs.Name"
							+ " WHEN fa.AD_Table_ID = 735 THEN dt.PrintName || ' ' || a.DocumentNo"
							+ " END Description,"
							+ " ev" + (baseLanguage ? "":"t") + ".Name AccountName"	// on vient lire le nom du compte sur la table principale ou la table trl
							//+ " COALESCE(fa.XXA_NumEcriture, 0) XXA_NumEcriture" FEC

							+ " FROM Fact_Acct fa"
							+ " LEFT OUTER JOIN C_Invoice i ON (fa.Record_ID = i.C_Invoice_ID AND fa.AD_Table_ID = 318)"
							+ " LEFT OUTER JOIN C_Payment p ON (fa.Record_ID = p.C_Payment_ID AND fa.AD_Table_ID = 335)"
							+ " LEFT OUTER JOIN GL_Journal j ON (fa.Record_ID = j.GL_Journal_ID AND fa.AD_Table_ID = 224)"
							+ " LEFT OUTER JOIN C_BankStatement bs ON (fa.Record_ID = bs.C_BankStatement_ID AND fa.AD_Table_ID = 392)"
							+ " LEFT OUTER JOIN C_AllocationHdr a ON (fa.Record_ID = a.C_AllocationHdr_ID AND fa.AD_Table_ID = 735)"
							+ " LEFT OUTER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID AND fa.AD_Table_ID <> 392)," // on ne prend pas en compte le tiers figurant sur le rapprochement bancaire (empêche le group by)
							+ " AD_Client c, AD_Org o, GL_Category gl, C_ElementValue ev, " + (baseLanguage ? "" : "C_ElementValue_Trl evt, ")
							+ " C_DocType" + (baseLanguage ? "" : "_Trl ") + " dt"	// soit C_DocType soit C_DocType_Trl

							+ " WHERE fa.AD_Client_ID = c.AD_Client_ID"
							+ " AND fa.AD_Org_ID = o.AD_Org_ID"
							+ " AND fa.GL_Category_ID = gl.GL_Category_ID"
							+ " AND fa.C_AcctSchema_ID = " + p_C_AcctSchema_ID
							+ " AND fa.Account_ID = ev.C_ElementValue_ID "
							+ (baseLanguage ? "" : " AND fa.Account_ID = evt.C_ElementValue_ID AND evt.AD_Language=" + DB.TO_STRING(language))
							// DocTypeName et N° doc
							+ " AND ("
							+ "    (fa.AD_Table_ID = 318 AND dt.C_DocType_ID = i.C_DocType_ID)"
							+ " OR (fa.AD_Table_ID = 335 AND dt.C_DocType_ID = p.C_DocType_ID)"
							+ " OR (fa.AD_Table_ID = 224 AND dt.C_DocType_ID = j.C_DocType_ID)"
							+ " OR (fa.AD_Table_ID = 392 AND dt.C_DocType_ID = " + dt_bs + ")"
							+ " OR (fa.AD_Table_ID = 735 AND dt.C_DocType_ID = " + dt_a + "))"
							+ (baseLanguage ? "" : " AND dt.AD_Language=" + DB.TO_STRING(language))

							+ " AND fa.PostingType = " + DB.TO_STRING(p_PostingType) + " " + sqlWhere;
					if (p_DateAcct_From != null)
						sql2 = sql2 + " AND fa.DateAcct >= " + DB.TO_DATE(p_DateAcct_From, true);
					if (p_DateAcct_To != null)
						sql2 = sql2 + " AND fa.DateAcct <= " + DB.TO_DATE(p_DateAcct_To, true);
					sql2 = sql2 + " AND fa.GL_Category_ID = " + glCatID
							+ " GROUP BY fa.GL_Category_ID, fa.DateAcct, ev.Value, fa.Account_ID, fa.AD_Table_ID, fa.Record_ID, bp.Name, ev"+(baseLanguage ? "":"t")+".Name, "
							+ "dt.printname, i.DocumentNo, p.DocumentNo, j.DocumentNo, bs.Name, a.DocumentNo/*, fa.XXA_NumEcriture*/" // FEC
							+ " ORDER BY fa.DateAcct, fa.Record_ID, ev.Value"
							+ ") RESULT";
				} else { // pas de regroupement
					sql2 = s_insert + " SELECT nextidfunc(" + sequenceID + ",'N'), " + getAD_PInstance_ID() + ", " + getAD_Client_ID() + ", " + (p_AD_Org_ID > 0 ? p_AD_Org_ID : 0) + ", SysDate, 0, SysDate, " + getAD_User_ID() + ", "
							+ " RESULT.Fact_Acct_ID, RESULT.BPName, RESULT.AccountValue,"
							+ " RESULT.GL_Category_ID, RESULT.AmtAcctDr, RESULT.AmtAcctCr, RESULT.DateAcct, RESULT.Description,"
							+ " RESULT.OrgName, RESULT.AccountName, "
							+ DB.TO_STRING(printName) + ", " + DB.TO_STRING(clientName) + ", " + DB.TO_STRING(orgName) + ", "
							+ DB.TO_STRING(footerCenter) + ", " + DB.TO_STRING(reportTitle) + ", " + DB.TO_STRING(criteresDate)
							//+ ", RESULT.XXA_NumEcriture " FEC

							+ " FROM (SELECT fa.Fact_Acct_ID, bp.Name BPName, ev.Value AccountValue,"
							+ " fa.GL_Category_ID, fa.AmtAcctDr, fa.AmtAcctCr, fa.DateAcct, fa.Description,"
							+ " o.Name OrgName, ev"+(baseLanguage ? "":"t")+".Name AccountName"
							//+ ", COALESCE(fa.XXA_NumEcriture, 0) XXA_NumEcriture" FEC

							+ " FROM Fact_Acct fa"
							+ " LEFT OUTER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID)"
							+ ", AD_Client c, AD_Org o, GL_Category gl, C_ElementValue ev" + (baseLanguage ? "" : ", C_ElementValue_Trl evt")
							+ " WHERE fa.AD_Client_ID = c.AD_Client_ID"
							+ " AND fa.AD_Org_ID = o.AD_Org_ID"
							+ " AND fa.GL_Category_ID = gl.GL_Category_ID"
							+ " AND fa.C_AcctSchema_ID = " + p_C_AcctSchema_ID
							+ " AND fa.Account_ID = ev.C_ElementValue_ID "
							+ (baseLanguage ? "" : " AND fa.Account_ID = evt.C_ElementValue_ID AND evt.AD_Language=" + DB.TO_STRING(language))
							+ " AND fa.PostingType = '" + p_PostingType + "' " + sqlWhere;
					if (p_DateAcct_From != null)
						sql2 = sql2 + " AND fa.DateAcct >= " + DB.TO_DATE(p_DateAcct_From, true);
					if (p_DateAcct_To != null)
						sql2 = sql2 + " AND fa.DateAcct <= " + DB.TO_DATE(p_DateAcct_To, true);
					sql2 = sql2 + " AND fa.GL_Category_ID = " + glCatID
							+ " ORDER BY gl.PrintName, fa.DateAcct NULLS FIRST, fa.Record_ID, ev.Value, fa.Line_ID"
							+ ") RESULT";
				}
			} else { // journal centralisateur
				String groupByClause = " GROUP BY fa.GL_Category_ID, gl.PrintName";
				if (p_isAccountDetail)
					groupByClause += ", fa.Account_ID, fa.AccountValue, fa.Name";

				String orderByClause = " ORDER BY gl.PrintName";
				if (p_isAccountDetail)
					orderByClause += ", fa.AccountValue";

				sql2 = "SELECT fa.GL_Category_ID, COALESCE(SUM(fa.AmtAcctDr),0), COALESCE(SUM(fa.AmtAcctCr),0)";
				if (p_isAccountDetail)
					sql2 += ", fa.AccountValue, fa.Name";

				sql2 += " FROM RV_Fact_Acct fa, GL_Category gl"
						+ " WHERE fa.GL_Category_ID = gl.GL_Category_ID"
						+ " AND fa.C_AcctSchema_ID = " + p_C_AcctSchema_ID 
						+ " AND fa.PostingType = " + DB.TO_STRING(p_PostingType) + " " + sqlWhere;
				if (p_DateAcct_From != null)
					sql2 = sql2 + " AND fa.DateAcct >= " + DB.TO_DATE(p_DateAcct_From, true);
				if (p_DateAcct_To != null)
					sql2 = sql2 + " AND fa.DateAcct <= " + DB.TO_DATE(p_DateAcct_To, true);
				sql2 = sql2 + " AND fa.GL_Category_ID = " + glCatID
						+ groupByClause + orderByClause;
			}

			//Spé détail journaux
			if (!isJournalCent) // on remplit la table par du sql
				lines = lines + DB.executeUpdate(sql2, get_TrxName());
			else { // journal cent ; la table se remplit en java
				PreparedStatement pstmt2 = DB.prepareStatement(sql2.toString(), get_TrxName());
				ResultSet rs2 = pstmt2.executeQuery ();

				while (rs2.next()) {
					taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
					taf.setClientName(clientName);
					taf.setOrgName(orgName);
					taf.setLine(lines++);
					if (p_AD_Org_ID > 0)
						taf.setAD_Org_ID(p_AD_Org_ID);
					taf.setPrintName(printName);
					taf.setLFR_DateAsString(criteresDate);
					taf.setTitle(reportTitle);
					taf.setFooterCenter(footerCenter);
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
				DB.close(rs2, pstmt2);
			}
		}

		return "@ProcessOK@";
	}	//	doIt

}	//	LFR_FactGeneJournaux
