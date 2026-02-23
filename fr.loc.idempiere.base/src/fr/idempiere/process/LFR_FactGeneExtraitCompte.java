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

import static fr.idempiere.model.SystemIDs_LFR.REFERENCE_LFR_LETTRAGEFILTRE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.compiere.model.MActivity;
import org.compiere.model.MRefList;
import org.compiere.model.MReference;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

import fr.idempiere.model.MTLFRReport;

/**
 *	Process de préparation des extraits de compte géné
 *	Les états jrxml lisent ensuite la table pour l'affichage des infos
 *	Sélection des comptes à afficher et on affiche TOUTES les écritures comprises entre les dates
 *	Les reports sont faits par le biais d'une OD au 0101N
 *  @author Nico
 */

public class LFR_FactGeneExtraitCompte extends LfrProcessFact {

	private int			lines = 0;
	private int 		p_accountFromID = 0; // compte
	private int 		p_accountToID = 0; // ... au compte xxx
	private String 		p_accountIDs = "";
	private int[]		p_accountList = null;
	private int			p_activityID = 0;
//	private int			p_XXA_Employee_ID = 0; TODO à faire via C_Employee_ID

	private Timestamp	m_dateAcctFrom = null;
	private Timestamp	m_dateAcctTo = null;
	private boolean		p_isSoldeInitial = false; // Affichage d'un solde initial (si édition demandée à une date différente du 0101N)
	private boolean		p_isGroupByRecord = false;
	private String 		p_lettrageFiltre = "";
	private Timestamp	p_lettrageDate = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare() {

		super.prepare();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("LFR_ProcessParaDateAcct")) {
				m_dateAcctFrom = para[i].getParameterAsTimestamp();
				m_dateAcctTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("LFR_AccountSelection")) {
				p_accountIDs = para[i].getParameterAsCSVInt();
				p_accountList= para[i].getParameterAsIntArray();
			}
			else if (name.equals("Account_ID")) {
				p_accountFromID = para[i].getParameterAsInt();
				p_accountToID = para[i].getParameter_ToAsInt();
			}
			else if (name.equals("LFR_IsAffichSoldeInitial"))
				p_isSoldeInitial = para[i].getParameterAsBoolean();
			else if (name.equals("LFR_IsGroupByRecord"))
				p_isGroupByRecord = para[i].getParameterAsBoolean();
			else if (name.equals("LFR_LettrageFiltre"))
				p_lettrageFiltre = para[i].getParameterAsString();
			else if (name.equals("LFR_LettrageDateParam"))
				p_lettrageDate = para[i].getParameterAsTimestamp();
			else if (name.equals("C_Activity_ID"))
				p_activityID = para[i].getParameterAsInt();
//			else if (name.equals("XXA_Employee_ID"))
//				p_XXA_Employee_ID = para[i].getParameterAsInt();
		}
	}	//	prepare

	/**
	 * 	Execute
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		if (p_pinstanceSourceID > 0)
			return "";

		if (m_dateAcctFrom == null && p_isSoldeInitial)
			return "@Error@ impossible d'avoir un solde initial sans date de début";

		if (p_accountFromID <= 0 && p_accountToID <= 0 && Util.isEmpty(p_accountIDs) && p_accountList.length == 0)
			return "@Error@ aucun compte sélectionné pour l'édition";

		String reportTitle = getReportTitle();

		String language = Env.getAD_Language(Env.getCtx());
		Timestamp exec_debut = new Timestamp (System.currentTimeMillis());
		int sequenceID = MTLFRReport.getSequenceID(get_TrxName());

		String sqlWhere = "";
		sqlWhere += getSqlWhereOrg("fa");

		if (p_activityID > 0)
			sqlWhere += " AND fa.C_Activity_ID = " + p_activityID;
//		if (p_XXA_Employee_ID > 0)
//			sqlWhere += " AND fa.XXA_Employee_ID = " + p_XXA_Employee_ID;

		if (p_lettrageFiltre.equals("M") || p_lettrageFiltre.equals("N")) {
			SimpleDateFormat df = DisplayType.getDateFormat(DisplayType.Date);
			setFooterCenter(MRefList.getListName(getCtx(), MReference.get(getCtx(), REFERENCE_LFR_LETTRAGEFILTRE).getAD_Reference_ID(), p_lettrageFiltre)
					+ " " + Msg.translate(getCtx(), "LFR_LettrageDateParam") + " "
					+	(((p_lettrageFiltre.equals("M") || p_lettrageFiltre.equals("N")) ? df.format(p_lettrageDate) : "")));
		}

		// Clause Where utilisées pour les mouvements
		String whereClauseLettrage = "";
		if (p_lettrageFiltre.equals("M"))	// Ecritures lettrées : écritures avec un code et une date lettrage inférieure à la date passée en paramètre
			whereClauseLettrage =" AND l.MatchCode IS NOT NULL AND TRUNC(l.LFR_ReconciliationDate, 'DD') <= " + DB.TO_DATE(p_lettrageDate, true);
		else if (p_lettrageFiltre.equals("N"))	// Ecritures non lettrées : sans code lettrage ou avec une date lettrage supérieure à la date passée en paramètre
			whereClauseLettrage =" AND (l.MatchCode IS NULL OR TRUNC(l.LFR_ReconciliationDate, 'DD') > " + DB.TO_DATE(p_lettrageDate, true) + ")";

		String criteresDate = getDateCriteres(getAD_Client_ID(), m_dateAcctFrom, m_dateAcctTo);

		sqlWhere += getSqlWhereGLCatToExclude("fa");
		setFooterCenter(getGLCatToExclude());

		if (p_isGroupByRecord)
			setFooterCenter(Msg.getMsg(getCtx(), "LFR_EcrituresGroupeesParCompte"));

		if (p_activityID > 0)
			setHeaderCenter(MActivity.get(getCtx(), p_activityID).getName());

//		if (p_XXA_Employee_ID > 0) {
//			if (headerCenter.length() > 0)
//				headerCenter.append("\n");
//			headerCenter.append(MBPartner.get(getCtx(), p_XXA_Employee_ID).getName());
//		}

		String listAccount = ""; // Comptes demandés au travers des paramètres (sélection ou plage de comptes)

		if (!Util.isEmpty(p_accountIDs))
			listAccount = p_accountIDs;
		else if (p_accountToID > 0) {

			StringBuilder sql = new StringBuilder("SELECT C_ElementValue_ID FROM C_ElementValue")
					.append(" WHERE C_Element_ID IN (SELECT C_Element_ID FROM C_Element e WHERE e.IsNaturalAccount='Y' AND e.AD_Client_ID = ").append(getAD_Client_ID()).append(")")
					.append(" AND Value <= (SELECT Value FROM C_ElementValue WHERE C_ElementValue_ID = ").append(p_accountToID).append(")");
			if (p_accountFromID > 0)
				sql.append(" AND Value >= (SELECT Value FROM C_ElementValue WHERE C_ElementValue_ID = ").append(p_accountFromID).append(")");

			listAccount = sql.toString();

		} else // sinon, un seul compte
			listAccount = Integer.toString(p_accountFromID);

		//Sélection des comptes concernés par l'édition
		StringBuilder sql = new StringBuilder("SELECT DISTINCT fa.Account_ID, ev.Value, ev.Name FROM Fact_Acct fa")
		.append(" INNER JOIN C_ElementValue ev ON (fa.Account_ID = ev.C_ElementValue_ID)")
		.append(" LEFT OUTER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID)")
		.append(" WHERE fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
		.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType))
		.append(" AND fa.Account_ID IN (").append(listAccount).append(")").append(sqlWhere).append(whereClauseLettrage);

		if (m_dateAcctFrom != null)
			sql.append(" AND fa.DateAcct >= ").append(DB.TO_DATE(m_dateAcctFrom, true));
		if (m_dateAcctTo != null)
			sql.append(" AND fa.DateAcct <= ").append(DB.TO_DATE(m_dateAcctTo, true));

		sql.append(" ORDER BY ev.Value");

		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery ();

		while (rs.next()) {

			int accountID = rs.getInt("Account_ID");
			String accountValue = rs.getString("Value");
			String accountName = rs.getString("Name");

			MTLFRReport taf = null;
			BigDecimal soldeProgressif = Env.ZERO;
			int siTLFReportID = -1;

			if (p_isSoldeInitial) { // Affichage du solde initial si la case est cochée (solde calculé entre le 01/01 et la date de début d'édition (mais on insère débit/crédit) pour le calcul du solde progressif
				StringBuilder sqlSoldeInitial = new StringBuilder("SELECT COALESCE(SUM(fa.AmtAcctDr),0) AS Debit, COALESCE(SUM(fa.AmtAcctCr),0) AS Credit FROM Fact_Acct fa")
						.append(" WHERE fa.DateAcct < ").append(DB.TO_DATE(m_dateAcctFrom, true)).append(" AND fa.DateAcct >= firstOf(").append(DB.TO_DATE(m_dateAcctFrom, true)).append(",'YY')")
						.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
						.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType))
						.append(" AND fa.Account_ID = ").append(accountID).append(sqlWhere);

				PreparedStatement pstmtSi = DB.prepareStatement(sqlSoldeInitial.toString(), get_TrxName());
				ResultSet rsSi = pstmtSi.executeQuery ();

				if (rsSi.next()) {

					BigDecimal debit = rsSi.getBigDecimal("Debit");
					BigDecimal credit = rsSi.getBigDecimal("Credit");

					if (debit.compareTo(credit) != 0) { // affichage de la ligne si D-C <> 0
						lines++;
						taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName()); 
						taf.setLine(lines);
						taf.setAmtAcctDr(debit);
						taf.setAmtAcctCr(credit);
						taf.setC_ElementValue_ID(accountID);
						soldeProgressif = (debit.subtract(credit)).setScale(2, RoundingMode.HALF_UP);
						taf.setLFR_SoldeProgressif(soldeProgressif);
						taf.setLFR_FactAcctDescription("Solde depuis le " + DisplayType.getDateFormat(DisplayType.Date).format(TimeUtil.trunc(m_dateAcctFrom, "YYYY")));
						taf.setClientName(getClientName());
						taf.setOrgName(getOrgName());
						taf.setLFR_FactAcctOrg(getOrgNameIfSingle());
						taf.setAccountValue(accountValue);
						taf.setAccount_Name(accountName);
						taf.setFooterCenter(getFooterCenter());
						taf.setHeaderCenter(getHeaderCenter());
						taf.setTitle(reportTitle);
						taf.setLFR_DateAsString(criteresDate);
						taf.setC_AcctSchema_ID(p_acctSchema_ID);
						taf.setPostingType(p_postingType);
						taf.saveEx();
						siTLFReportID = taf.getT_LFR_Report_ID();
					}
				}
				DB.close(rsSi, pstmtSi);
			} // fin solde initial

			String fromWhere = " FROM Fact_Acct fa"
					+ " INNER JOIN C_ElementValue ev ON (fa.Account_ID = ev.C_ElementValue_ID)"
					+ " INNER JOIN AD_Org o ON (fa.AD_Org_ID = o.AD_Org_ID)"
					+ " LEFT OUTER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID),"
					+ " GL_Category gl" 
					+ " WHERE fa.GL_Category_ID = gl.GL_Category_ID" 
					+ " AND fa.C_AcctSchema_ID = " + p_acctSchema_ID 
					+ " AND fa.PostingType = " + DB.TO_STRING(p_postingType)
					+ " AND fa.Account_ID = " + accountID						
					+ sqlWhere + whereClauseLettrage;

			if (m_dateAcctFrom != null)
				fromWhere += " AND fa.DateAcct >= " + DB.TO_DATE(m_dateAcctFrom, true);
			if (m_dateAcctTo != null)
				fromWhere += " AND fa.DateAcct <= " + DB.TO_DATE(m_dateAcctTo, true);;

				String orderByClause   = " ORDER BY ev.Value, fa.DateAcct NULLS FIRST, fa.GL_Category_ID, fa.AD_Table_ID, fa.Record_ID";
				String orderByClauseSP = " ORDER BY AccountValue, DateAcct NULLS FIRST, GL_Category_ID , AD_Table_ID, Record_ID";

				String s_select = "";
				if (p_isGroupByRecord) {
					int dt_bs = getDocTypeForBankStatement();
					int dt_a = getDocTypeForAllocation();

					boolean baseLanguage=false;
					if (language.equals(Language.getBaseAD_Language()))
						baseLanguage=true;

					String fromDescription = " LEFT OUTER JOIN C_Invoice i ON (fa.Record_ID = i.C_Invoice_ID AND fa.AD_Table_ID = 318)"
							+ " LEFT OUTER JOIN C_Payment p ON (fa.Record_ID = p.C_Payment_ID AND fa.AD_Table_ID = 335)"
							+ " LEFT OUTER JOIN GL_Journal j ON (fa.Record_ID = j.GL_Journal_ID AND fa.AD_Table_ID = 224)"
							+ " LEFT OUTER JOIN C_BankStatement bs ON (fa.Record_ID = bs.C_BankStatement_ID AND fa.AD_Table_ID = 392)"
							+ " LEFT OUTER JOIN C_AllocationHdr a ON (fa.Record_ID = a.C_AllocationHdr_ID AND fa.AD_Table_ID = 735)"
							+ " LEFT OUTER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID AND fa.AD_Table_ID <> 392)"; // on ne prend pas en compte le tiers figurant sur le rapprochement bancaire (empêche le group by)

					fromWhere = fromWhere.replace("Fact_Acct fa", "Fact_Acct fa" + fromDescription); // on ajoute ds la clause FROM les tables documents
					fromWhere = fromWhere.replace("GL_Category gl", "GL_Category gl, C_DocType" + (baseLanguage ? "" : "_Trl ") + " dt");	// on ajoute la table C_DocType ou C_DocType_Trl

					s_select = " SELECT nextidfunc(" + sequenceID + ",'N'), " + getAD_PInstance_ID() + ", " + getAD_Client_ID() + ", 0, SysDate, " + getAD_User_ID() + ", SysDate, " + getAD_User_ID() + ","
							+ " NULL, RESULT.Account_ID, NULL, RESULT.AmtAcctDr, RESULT.AmtAcctCr,"
							+ " RESULT.DateAcct, RESULT.description, NULL, RESULT.LFR_GLCategoryPrintName, NULL, NULL," // on regroupe par document, on ne tient donc pas compte du FactAcct.AD_Org_ID
							+ " SUM(COALESCE(AmtAcctDr, 0) - COALESCE(AmtAcctCr, 0))"
							+ " OVER ("
							+ " PARTITION BY Account_ID"
							+ orderByClauseSP
							+ " ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW"
							+ " ) AS LFR_SoldeProgressif, "
							
							+ DB.TO_STRING(getClientName()) + ", " + DB.TO_STRING(getOrgName()) + ", " + DB.TO_STRING(accountValue) + ", " + DB.TO_STRING(accountName) + ", "
							+ DB.TO_STRING(getFooterCenter()) + ", " + DB.TO_STRING(getHeaderCenter()) + ", " + DB.TO_STRING(reportTitle) + ", " + DB.TO_STRING(criteresDate)
							+ ", " + p_acctSchema_ID + ", " + DB.TO_STRING(p_postingType)
							+ " FROM (SELECT fa.Account_ID, SUM(fa.AmtAcctDr) AmtAcctDr, SUM(AmtAcctCr) AmtAcctCr, "
							+ " fa.DateAcct, "
							// DocTypeName et N° doc
							+ " CASE WHEN fa.AD_Table_ID = 318 THEN dt.PrintName || ' ' || i.DocumentNo"
							+ " WHEN fa.AD_Table_ID = 335 THEN dt.PrintName || ' ' || p.DocumentNo"
							+ " WHEN fa.AD_Table_ID = 224 THEN dt.PrintName || ' ' || j.DocumentNo"
							+ " WHEN fa.AD_Table_ID = 392 THEN dt.PrintName || ' ' || bs.Name"
							+ " WHEN fa.AD_Table_ID = 735 THEN dt.PrintName || ' ' || a.DocumentNo"
							+ " END Description,"
							+ " gl.PrintName AS LFR_GLCategoryPrintName, fa.GL_Category_ID, SUM(fa.AmtAcctDr - fa.AmtAcctCr) AmtAcct"
							+ ", Record_ID, AD_Table_ID, ev.Value AS AccountValue"
							+ fromWhere

							// DocTypeName et N° doc
							+ " AND ("
							+ "    (fa.AD_Table_ID = 318 AND dt.C_DocType_ID = i.C_DocType_ID)"
							+ " OR (fa.AD_Table_ID = 335 AND dt.C_DocType_ID = p.C_DocType_ID)"
							+ " OR (fa.AD_Table_ID = 224 AND dt.C_DocType_ID = j.C_DocType_ID)"
							+ " OR (fa.AD_Table_ID = 392 AND dt.C_DocType_ID = " + dt_bs + ")"
							+ " OR (fa.AD_Table_ID = 735 AND dt.C_DocType_ID = " + dt_a + "))"
							+ (baseLanguage ? "" : " AND dt.AD_Language=" + DB.TO_STRING(language))

							+ " GROUP BY fa.Account_ID, fa.DateAcct, fa.AD_Table_ID, fa.Record_ID, gl.PrintName, fa.GL_Category_ID, ev.Value,"
							+ " dt.printname, i.DocumentNo, p.DocumentNo, j.DocumentNo, bs.Name, a.DocumentNo"
							+ orderByClause
							+ ") RESULT";
				} else {	// EC avec toutes les lignes
					s_select = " SELECT nextidfunc(" + sequenceID + ",'N'), " + getAD_PInstance_ID() + ", RESULT.AD_Client_ID, 0, SysDate, " + getAD_User_ID() +", SysDate, " + getAD_User_ID() + ","
							+ " RESULT.Fact_Acct_ID, RESULT.Account_ID, RESULT.GL_Category_ID, RESULT.AmtAcctDr, RESULT.AmtAcctCr, RESULT.DateAcct DateAcct, RESULT.Description,"
							+ " RESULT.LFR_FactAcctOrg, RESULT.LFR_GLCategoryPrintName, RESULT.MatchCode, RESULT.LFR_ReconciliationDate, /* RESULT.AmtAcct, */"
							
							+ " SUM(COALESCE(AmtAcctDr, 0) - COALESCE(AmtAcctCr, 0))" 
					        + " OVER ("
					        + " PARTITION BY Account_ID"
					        + orderByClauseSP
					        + " ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW"
					        + " ) AS LFR_SoldeProgressif, "
							
							+ DB.TO_STRING(getClientName()) + ", " + DB.TO_STRING(getOrgName()) + ", " + DB.TO_STRING(accountValue) + ", " + DB.TO_STRING(accountName) + ","
							+ DB.TO_STRING(getFooterCenter()) + ", " + DB.TO_STRING(getHeaderCenter()) + ", " + DB.TO_STRING(reportTitle) + ", " + DB.TO_STRING(criteresDate)
							+ ", " + p_acctSchema_ID + ", " + DB.TO_STRING(p_postingType)
							+ " FROM (SELECT fa.AD_Client_ID AD_Client_ID, fa.Fact_Acct_ID Fact_Acct_ID," 
							+" fa.Account_ID Account_ID,"
							+" fa.GL_Category_ID GL_Category_ID," 
							+" fa.AmtAcctDr AmtAcctDr,"
							+" fa.AmtAcctCr AmtAcctCr," 
							+" fa.DateAcct DateAcct,"
							+" fa.Description Description," 
							+ " o." + getOrgDisplay() + " AS LFR_FactAcctOrg, "
							+" gl.PrintName LFR_GLCategoryPrintName,"
							+" l.MatchCode MatchCode," 
							+" l.LFR_ReconciliationDate LFR_ReconciliationDate,"
							+" fa.AmtAcctDr - fa.AmtAcctCr AmtAcct"
							+" ,fa.Record_ID, fa.AD_Table_ID, ev.Value AS AccountValue " // pour SoldeProgressif
							+ fromWhere + orderByClause + ", fa.Line_ID"
							+ ") RESULT";
				}

				String s_insert = "INSERT INTO T_LFR_Report "
						+ "(T_LFR_Report_ID, AD_PInstance_ID, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy,"
						+ " Fact_Acct_ID, C_ElementValue_ID, GL_Category_ID, AmtAcctDr, AmtAcctCr, DateAcct, LFR_FactAcctDescription,"
						+ " LFR_FactAcctOrg, LFR_GLCategoryPrintName, LFR_MatchCode, LFR_ReconciliationDate, LFR_SoldeProgressif,"
						+ " ClientName, OrgName, AccountValue, Account_Name, FooterCenter, HeaderCenter, Title, LFR_DateAsString, C_AcctSchema_ID, PostingType)";

				lines = lines + DB.executeUpdateEx(s_insert + s_select, get_TrxName());

				if (p_isSoldeInitial) { // calcul du solde progressif qui ne peut pas se faire dans l'insert/select car la ligne SI est insérée en java
					String updateSP = "UPDATE T_LFR_Report r SET r.LFR_SoldeProgressif = "
							+ " (SELECT SUM(COALESCE(r1.AmtAcctDr, 0) - COALESCE(r1.AmtAcctCr, 0)) FROM T_LFR_Report r1 WHERE r1.AD_PInstance_ID = r.AD_PInstance_ID"
							+ " AND r1.T_LFR_Report_ID <= r.T_LFR_Report_ID) WHERE r.AD_PInstance_ID = " + getAD_PInstance_ID() + " AND r.T_LFR_Report_ID > " + siTLFReportID;
					DB.executeUpdateEx(updateSP, get_TrxName());

					// On vide les champs Debit/Credit de la ligne de SI
					DB.executeUpdateEx("UPDATE T_LFR_Report SET AmtAcctDr = NULL, AmtAcctCr = NULL WHERE AD_PInstance_ID = ? AND T_LFR_Report_ID = ?", new Object[] {getAD_PInstance_ID(), siTLFReportID}, get_TrxName());
				}
		}

		DB.close(rs, pstmt);

		String duree =  TimeUtil.formatElapsed(exec_debut);
		return "# Fin du traitement ; " + lines + " lignes générées : " + lines + " en " + duree;
	}	//	doIt

	protected String getTitleCenter() {
		return "Extrait de compte";
	}
}	//	LFR_FactGeneExtraitCompte
