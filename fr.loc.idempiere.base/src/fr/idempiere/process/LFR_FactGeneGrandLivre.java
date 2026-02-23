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

import static fr.idempiere.model.SystemIDs_LFR.C_ELEMENT_VALUE_LFR_ISCUMULONGRANDLIVRE;

import java.sql.Timestamp;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.TimeUtil;

import fr.idempiere.model.MTLFRReport;

/**
 *	Process de préparation du grand livre général
 *  @author Nicolas Micoud - TGI
 */
public class LFR_FactGeneGrandLivre extends LfrProcessFact
{
	private int 		p_accountFromID = 0;
	private int 		p_accountToID = 0;
	private Timestamp	p_dateAcctFrom = null;
	private Timestamp	p_dateAcctTo = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		super.prepare();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("LFR_ProcessParaDateAcct")) {
				p_dateAcctFrom = para[i].getParameterAsTimestamp();
				p_dateAcctTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("Account_ID")) {
				p_accountFromID = para[i].getParameterAsInt();
				p_accountToID = para[i].getParameter_ToAsInt();
			}
		}
	}	//	prepare

	/**
	 * 	Execute
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception {
		Timestamp exec_debut = new Timestamp (System.currentTimeMillis());
		int sequenceID = MTLFRReport.getSequenceID(get_TrxName());
		int lines = 0;
		String language = Env.getAD_Language(Env.getCtx());
		boolean	baseLanguage = language.equals(Language.getBaseAD_Language());

		StringBuilder sqlWhere = new StringBuilder();
		sqlWhere.append(getSqlWhereOrg("fa"));
		sqlWhere.append(getSqlWhereGLCatToExclude("fa"));

		String criteresDate = getDateCriteres(getAD_Client_ID(), p_dateAcctFrom, p_dateAcctTo);
		setFooterCenter(getGLCatToExclude());

		//Sélection des comptes concernés par l'édition
		StringBuilder sqlAccounts = new StringBuilder("SELECT DISTINCT Account_ID FROM RV_Fact_Acct fa") 
				.append(" WHERE C_AcctSchema_ID = ").append(p_acctSchema_ID)
				.append(" AND PostingType = ").append(DB.TO_STRING(p_postingType)).append(sqlWhere);
		if (p_dateAcctFrom != null)
			sqlAccounts.append(" AND DateAcct >= ").append(DB.TO_DATE(p_dateAcctFrom, true));
		if (p_dateAcctTo != null)
			sqlAccounts.append(" AND DateAcct <= ").append(DB.TO_DATE(p_dateAcctTo, true));
		if (p_accountFromID > 0)
			sqlAccounts.append(" AND AccountValue >= (SELECT Value FROM C_ElementValue WHERE C_ElementValue_ID = ").append(p_accountFromID).append(")");
		if (p_accountToID > 0)
			sqlAccounts.append(" AND AccountValue <= (SELECT Value FROM C_ElementValue WHERE C_ElementValue_ID = ").append(p_accountToID).append(")");

		String s_insert = "INSERT INTO T_LFR_Report "
				+ "(T_LFR_Report_ID, AD_PInstance_ID, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, "
				+ " Fact_Acct_ID, C_ElementValue_ID, GL_Category_ID, AmtAcctDr, AmtAcctCr, DateAcct, LFR_FactAcctDescription, "
				+ " LFR_FactAcctOrg, LFR_GLCategoryPrintName, LFR_SoldeProgressif, "
				+ " ClientName, OrgName, AccountValue, Account_Name, FooterCenter, HeaderCenter, Title, LFR_DateAsString, C_AcctSchema_ID, PostingType, LFR_NumEcriture)"; 

		String s_select = " SELECT nextidfunc(" + sequenceID + ",'N'), " + getAD_PInstance_ID() + ", " + getAD_Client_ID() + ", 0, SysDate, " + getAD_User_ID() +", SysDate, " + getAD_User_ID() + ","
				+ " RESULT.Fact_Acct_ID, RESULT.Account_ID, RESULT.GL_Category_ID, RESULT.AmtAcctDr, RESULT.AmtAcctCr, RESULT.DateAcct DateAcct, RESULT.Description,"
				+ " RESULT.OrgName, RESULT.LFR_GLCategoryPrintName, RESULT.AmtAcct, "
				+ DB.TO_STRING(getClientName()) + ", " + DB.TO_STRING(getOrgName()) + ", RESULT.AccountValue, RESULT.AccountName, "
				+ DB.TO_STRING(getFooterCenter()) + ", RESULT.HeaderCenter, " + DB.TO_STRING(getReportTitle()) + ", " + DB.TO_STRING(criteresDate)
				+ ", " + p_acctSchema_ID + ", " + DB.TO_STRING(p_postingType)
				+ " , RESULT.LFR_NumEcriture " 
				+ " FROM ("
				// détail écritures
				+ "SELECT fa.Fact_Acct_ID Fact_Acct_ID, fa.Account_ID Account_ID, fa.GL_Category_ID GL_Category_ID, fa.AmtAcctDr AmtAcctDr, fa.AmtAcctCr AmtAcctCr, fa.DateAcct DateAcct,"
				+" fa.Description Description, o.Name OrgName, gl.Code LFR_GLCategoryPrintName,"
				+ " fa.AmtAcctDr - fa.AmtAcctCr AmtAcct, ev"+(baseLanguage ? "":"t")+".Name AccountName, ev.Value AccountValue,"
				+ " ev.Value || ' - ' || ev"+(baseLanguage ? "":"t")+".Name || ' : Détail des écritures' HeaderCenter"
				+ ", COALESCE(pvf.LFR_NumEcriture, 0) LFR_NumEcriture" 
				+ ", fa.AD_Table_ID, fa.Record_ID"
				+ " FROM Fact_Acct fa"
				+ " LEFT OUTER JOIN LFR_PeriodValidationFact pvf ON (fa.Fact_Acct_ID = pvf.Fact_Acct_ID)"
				+ ", AD_Client c, GL_Category gl, C_ElementValue ev" + (baseLanguage ? "" : ", C_ElementValue_Trl evt"  + " , AD_Org o")
				+ " WHERE fa.AD_Client_ID = c.AD_Client_ID"
				+ " AND fa.GL_Category_ID = gl.GL_Category_ID" 
				+ " AND fa.C_AcctSchema_ID = " + p_acctSchema_ID  
				+ " AND fa.PostingType = " + DB.TO_STRING(p_postingType) + sqlWhere
				+ " AND fa.Account_ID = ev.C_ElementValue_ID "
				+ " AND fa.AD_Org_ID = o.AD_Org_ID"
				+ (baseLanguage ? "" : " AND fa.Account_ID = evt.C_ElementValue_ID AND evt.AD_Language=" + DB.TO_STRING(language))
				+ " AND fa.Account_ID IN (" + sqlAccounts + ")"
				+ " AND ev." + C_ELEMENT_VALUE_LFR_ISCUMULONGRANDLIVRE + " = 'N'";
		if (p_dateAcctFrom != null)
			s_select += " AND fa.DateAcct >= " + DB.TO_DATE(p_dateAcctFrom, true);
		if (p_dateAcctTo != null)
			s_select += " AND fa.DateAcct <= " + DB.TO_DATE(p_dateAcctTo, true);

		//cumul
		s_select += " UNION "
				+ "SELECT NULL, fa.Account_ID Account_ID, fa.GL_Category_ID GL_Category_ID, SUM(fa.AmtAcctDr) AmtAcctDr, SUM(fa.AmtAcctCr) AmtAcctCr," 
				+" p.EndDate DateAcct, p.Name, o.Name OrgName, gl.Code LFR_GLCategoryPrintName,"
				+ " SUM(fa.AmtAcctDr)-SUM(fa.AmtAcctCr) AmtAcct, ev"+(baseLanguage ? "":"t")+".Name AccountName, ev.Value AccountValue,"
				+ " ev.Value || ' - ' || ev"+(baseLanguage ? "":"t")+".Name || ' : Compte cumulé' HeaderCenter, NULL AS LFR_NumEcriture"
				+ ", NULL, NULL"
				+ " FROM Fact_Acct fa, AD_Client c, GL_Category gl, C_Period p, "
				+ " C_ElementValue ev" + (baseLanguage ? "" : ", C_ElementValue_Trl evt, AD_Org o")
				+ " WHERE fa.AD_Client_ID = c.AD_Client_ID"
				+ " AND fa.GL_Category_ID = gl.GL_Category_ID" 
				+ " AND fa.C_AcctSchema_ID = " + p_acctSchema_ID  
				+ " AND fa.PostingType = " + DB.TO_STRING(p_postingType) + sqlWhere
				+ " AND fa.Account_ID = ev.C_ElementValue_ID"
				+ " AND fa.C_Period_ID = p.C_Period_ID"
				+ " AND fa.AD_Org_ID = o.AD_Org_ID"
				+ (baseLanguage ? "" : " AND fa.Account_ID = evt.C_ElementValue_ID AND evt.AD_Language=" + DB.TO_STRING(language))
				+ " AND fa.Account_ID IN (" + sqlAccounts + ")"
				+ " AND ev." + C_ELEMENT_VALUE_LFR_ISCUMULONGRANDLIVRE + " = 'Y'";
		if (p_dateAcctFrom != null)
			s_select += " AND fa.DateAcct >= " + DB.TO_DATE(p_dateAcctFrom, true);
		if (p_dateAcctTo != null)
			s_select += " AND fa.DateAcct <= " + DB.TO_DATE(p_dateAcctTo, true);
		s_select +=" GROUP BY o.Name, fa.Account_ID, gl.Code, p.EndDate, p.Name, fa.GL_Category_ID, evt.Name, ev.Value, ev." + C_ELEMENT_VALUE_LFR_ISCUMULONGRANDLIVRE;

		s_select += " ORDER BY AccountValue, DateAcct, OrgName, GL_Category_ID, AD_Table_ID, Record_ID"
				+ ") RESULT";

		lines = lines + DB.executeUpdateEx(s_insert+s_select, get_TrxName());

		String duree =  TimeUtil.formatElapsed(exec_debut);
		return "# Fin du traitement ; " + lines + " lignes générées en " + duree;
	}	//	doIt

	protected String getTitleCenter() {
		return "Grand Livre Général";
	}
}	//	LFR_FactGeneGrandLivre