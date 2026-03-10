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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MDocType;
import org.compiere.model.MElementValue;
import org.compiere.model.MRefList;
import org.compiere.model.MReference;
import org.compiere.model.MTable;
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
 *	Process de préparation des états auxilliaires (balance et GL/extrait compte)
 *	Les états jrxml lisent ensuite la table pour l'affichage des infos
 *  @author Nicolas Micoud - TGI
 */
public class LFR_FactAux extends LfrProcessFact
{
	public String p_type = "";
	private Timestamp	m_DateAcct_From = null;
	private Timestamp	m_DateAcct_To = null;
	private boolean		p_isCustomer = false;
	private boolean		p_isVendor = false;
	private boolean		p_isEmployee = false;
	private int			lines = 0;
	//specs GL
	private int			p_C_ElementValue_ID = 0;
	private int			p_C_BPartner_From_ID = -1;
	private int			p_C_BPartner_To_ID = -1;
	private String 		p_bpartnerIDs = "";
	private String 		m_accountName = "";
	private String		m_accountValue = "";
	private BigDecimal 	m_amtPeriodeTempDebit = Env.ZERO;
	private BigDecimal 	m_amtPeriodeTempCredit = Env.ZERO;
	private BigDecimal 	m_amtPeriodeTempSolde = Env.ZERO;
	private BigDecimal 	m_amtPeriodeDefDebit = Env.ZERO;
	private BigDecimal	m_amtPeriodeDefCredit= Env.ZERO;
	private BigDecimal	m_amtPeriodeDefSolde = Env.ZERO;
	private String 		p_lettrageFiltre = "";
	private Timestamp	p_lettrageDate = null;
	private boolean		p_isRANDetail = false;	// pour l'édition, l'utilisateur a-t-il demandé le détail du RAN
	private boolean		p_isRANDetailBP = false;	// pour le tiers courant ; le tiers a-t-il un détail de RAN à afficher ?
	private String 		m_criteresDate = "";
	private String		reportTitle = "";

	private String		language = "";
	private String		m_orderBy = "";
	
	public static final String TYPE_EXTRAIT_COMPTE_CLIENT = "1";
	public static final String TYPE_EXTRAIT_COMPTE_FOURNISSEUR = "2";
	public static final String TYPE_BALANCE = "3";
	public static final String TYPE_GRAND_LIVRE = "4";

	public static final String ORDER_BY_NAME = "Name";
	public static final String ORDER_BY_VALUE = "Value";

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		super.prepare();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("LFR_FactAuxType"))
				p_type = para[i].getParameterAsString();
			else if (name.equals("LFR_ProcessParaDateAcct")) {
				m_DateAcct_From = para[i].getParameterAsTimestamp();
				m_DateAcct_To = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("IsCustomer"))
				p_isCustomer = para[i].getParameterAsBoolean();
			else if (name.equals("IsVendor"))
				p_isVendor = para[i].getParameterAsBoolean();
			else if (name.equals("IsEmployee"))
				p_isEmployee = para[i].getParameterAsBoolean();
			// specs GL
			else if ((name.equals("LFR_Customer_Acct") || name.equals("LFR_Vendor_Acct")) && p_C_ElementValue_ID == 0)
				p_C_ElementValue_ID = para[i].getParameterAsInt();
			else if (name.equals("C_BPartner_ID") || name.equals("LFR_Customer_ID") || name.equals("LFR_Vendor_ID") || name.equals("LFR_Employee_ID")) { // défini uniquement pour EC
				p_C_BPartner_From_ID = para[i].getParameterAsInt();
				p_C_BPartner_To_ID = para[i].getParameter_ToAsInt();
			}
			else if (name.equals("LFR_BPartnerCustomerSelection") || name.equals("LFR_BPartnerVendorSelection"))
				p_bpartnerIDs = para[i].getParameterAsCSVInt();
			else if (name.equals("LFR_LettrageFiltre"))
				p_lettrageFiltre = para[i].getParameterAsString();
			else if (name.equals("LFR_LettrageDateParam"))
				p_lettrageDate = para[i].getParameterAsTimestamp();
			else if (name.equals("LFR_IsRANDetail"))
				p_isRANDetail = para[i].getParameterAsBoolean();
		}
		language = Env.getAD_Language(Env.getCtx());
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
		
		Timestamp exec_debut = new Timestamp (System.currentTimeMillis());

		//Détermination du type (client, fournisseur ou salarié)
		if (p_type.equals(TYPE_EXTRAIT_COMPTE_CLIENT))
			p_isCustomer = true;
		else if (p_type.equals(TYPE_EXTRAIT_COMPTE_FOURNISSEUR))
			p_isVendor = true;

		if ((!p_isCustomer && !p_isVendor) || (p_isCustomer) && (p_isVendor) || (p_isCustomer) && (p_isEmployee))
			return "@Error@ Impossible de déterminer le type d'état auxilliaire à réaliser";

		reportTitle = getReportTitle();
		m_criteresDate = getDateCriteres(getAD_Client_ID(), m_DateAcct_From, m_DateAcct_To);

		String compteAux = "";
		if (p_isCustomer)
			compteAux = MElementValue.BPARTNERTYPE_Customer;
		if (p_isVendor)
			compteAux = MElementValue.BPARTNERTYPE_Vendor;

		m_orderBy = getOrderBy();

		if (p_C_ElementValue_ID > 0) { // Specs GL/EC
			// Récup de la value et du nom du compte
			MElementValue ev = new MElementValue (getCtx(), p_C_ElementValue_ID, get_TrxName());
			m_accountValue = ev.getValue();
			m_accountName = ev.getName();
		}

		if (p_lettrageFiltre.equals("M") || p_lettrageFiltre.equals("N")) { // TODO utiliser des constantes statiques dans LfrProcessFact
			SimpleDateFormat df = DisplayType.getDateFormat(DisplayType.Date);
			setFooterCenter(MRefList.getListName(getCtx(), MReference.get(getCtx(), REFERENCE_LFR_LETTRAGEFILTRE).getAD_Reference_ID(), p_lettrageFiltre)
					+ " " + Msg.getElement(getCtx(), "LFR_LettrageDateParam") + " ");

			if (p_lettrageFiltre.equals("M") || p_lettrageFiltre.equals("N"))
				setFooterCenter(" " + df.format(p_lettrageDate));

			if (p_lettrageFiltre.equals("N"))
				p_isRANDetail=true; // on force l'affichage du détail
		}

		// Clauses Where utilisées pour le détail RAN et déterminer si le tiers a des écritures sur la période
		String whereClauseLettrageRAN = "";	// le RAN
		if (p_isRANDetail)
		{
			if (p_lettrageFiltre.equals("A"))	// si toutes les écritures, on affiche les non lettrées et celle lettrées après la date de début
				whereClauseLettrageRAN = "AND (l.MatchCode IS NULL OR TRUNC(l.LFR_ReconciliationDate, 'DD') >= " + DB.TO_DATE(m_DateAcct_From, true)+ ")";
			else if (p_lettrageFiltre.equals("M"))	// si uniquement les lettrées, on affiche celles lettrées entre la date de début et la date de lettrage
				whereClauseLettrageRAN = "AND (TRUNC(l.LFR_ReconciliationDate, 'DD') >= " + DB.TO_DATE(m_DateAcct_From, true)+ ")"
						+	 "AND (TRUNC(l.LFR_ReconciliationDate, 'DD') <= " + DB.TO_DATE(p_lettrageDate, true)+ ")";
			else if (p_lettrageFiltre.equals("N"))	// si uniquement les non lettrées, on affiche les non lettrées et celles lettrées après la date de lettrage
				whereClauseLettrageRAN = "AND (l.MatchCode IS NULL OR TRUNC(l.LFR_ReconciliationDate, 'DD') >= " + DB.TO_DATE(p_lettrageDate, true)+ ")";
		}

		// Clause Where utilisées pour les mouvements
		String whereClauseLettrage = "";
		if (p_lettrageFiltre.equals("M"))	// Ecritures lettrées : écritures avec un code et une date lettrage inférieure à la date passée en paramètre
			whereClauseLettrage =" AND l.MatchCode IS NOT NULL AND TRUNC(l.LFR_ReconciliationDate, 'DD') <= " + DB.TO_DATE(p_lettrageDate, true);
		else if (p_lettrageFiltre.equals("N"))	// Ecritures non lettrées : sans code lettrage ou avec une date lettrage supérieure à la date passée en paramètre
			whereClauseLettrage =" AND (l.MatchCode IS NULL OR TRUNC(l.LFR_ReconciliationDate, 'DD') > " + DB.TO_DATE(p_lettrageDate, true) + ")";

		String ListComptesAux="";
		if (p_type.equals(TYPE_BALANCE))
			ListComptesAux = getComptesAux(compteAux);

		String sqlBPartner = "";
		if ((p_type.equals(TYPE_EXTRAIT_COMPTE_CLIENT) || p_type.equals(TYPE_EXTRAIT_COMPTE_FOURNISSEUR)) && !Util.isEmpty(p_bpartnerIDs))
			sqlBPartner = p_bpartnerIDs;
		else if ((p_type.equals(TYPE_EXTRAIT_COMPTE_CLIENT) || p_type.equals(TYPE_EXTRAIT_COMPTE_FOURNISSEUR)) && p_C_BPartner_To_ID==0)
			sqlBPartner = Integer.toString(p_C_BPartner_From_ID);
		else {
			// Sélection des tiers concernés par l'édition
			StringBuilder bpartnerWhere = new StringBuilder("");
			if (p_isCustomer)
				bpartnerWhere.append(" AND bp.IsCustomer = 'Y'");
			if (p_isVendor)
				bpartnerWhere.append(" AND bp.IsVendor = 'Y' AND bp.IsEmployee = ").append(p_isEmployee ? "'Y'" : "'N'");
			bpartnerWhere.append(getSqlWhereOrg("fa"));

			if (p_C_ElementValue_ID > 0) // specs GL
				bpartnerWhere.append(" AND fa.Account_ID = ").append(p_C_ElementValue_ID);
			else // balance
				bpartnerWhere.append(" AND fa.Account_ID IN (").append(ListComptesAux).append(")");

			bpartnerWhere.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID).append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType));

			// tiers avec solde non nul à la date de début (pour Balance, GL et EC multiples)
			// pour les ec multiples, on ajoute ce filtre qui permet de ne conserver que les tiers dont le nom est compris entre p_C_BPartner_From_ID et p_C_BPartner_To_ID
			if (p_C_BPartner_To_ID > 0)
				bpartnerWhere.append(" AND bp.Name >= (SELECT Name FROM C_BPartner WHERE C_BPartner_ID = ").append(p_C_BPartner_From_ID)
				.append(") AND bp.Name <= (SELECT Name FROM C_BPartner WHERE C_BPartner_ID = ").append(p_C_BPartner_To_ID).append(")");

			StringBuilder sql = new StringBuilder("SELECT fa.C_BPartner_ID")
			.append(" FROM Fact_Acct fa INNER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID)");
			if (p_isRANDetail)
				sql.append(" LEFT OUTER JOIN Fact_Reconciliation l ON (l.Fact_Acct_ID = fa.Fact_Acct_ID)");
			sql.append(" WHERE fa.DateAcct < ").append(DB.TO_DATE(m_DateAcct_From, true)).append(bpartnerWhere).append(whereClauseLettrageRAN)	
			.append(" HAVING (SUM(fa.AmtAcctDR) - SUM(fa.AmtAcctCR)<>0)") 
			.append(" GROUP BY fa.C_BPartner_ID");

			sql.append(" UNION ");

			// tiers avec mouvements sur la période
			sql.append("SELECT fa.C_BPartner_ID")
			.append(" FROM Fact_Acct fa")
			.append(" INNER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID)")
			.append(" LEFT OUTER JOIN Fact_Reconciliation l ON (l.Fact_Acct_ID = fa.Fact_Acct_ID)")
			.append(" WHERE fa.DateAcct >= ").append(DB.TO_DATE(m_DateAcct_From, true))
			.append(" AND fa.DateAcct <= ").append(DB.TO_DATE(m_DateAcct_To, true)) 
			.append(bpartnerWhere).append(whereClauseLettrage);

			sqlBPartner = sql.toString();
		}

		if (p_type.equals(TYPE_GRAND_LIVRE))
			lines = grandLivre(whereClauseLettrage, whereClauseLettrageRAN, sqlBPartner);
		else if (p_type.equals(TYPE_BALANCE))
			lines = balance(ListComptesAux, sqlBPartner);
		else if (p_type.equals(TYPE_EXTRAIT_COMPTE_CLIENT) || p_type.equals(TYPE_EXTRAIT_COMPTE_FOURNISSEUR))
			lines = extraitCompte(whereClauseLettrage, whereClauseLettrageRAN, compteAux, sqlBPartner);

		return "# Fin du traitement ; lignes générées : " + lines + " en " + TimeUtil.formatElapsed(exec_debut);
	}	//	doIt

	private String getBPartnerDisplayName() {
		if (m_orderBy.equals(ORDER_BY_NAME))
			return " bp.Name || ' (' || bp.Value || ')'";
		else if (m_orderBy.equals(ORDER_BY_VALUE))
			return " bp.Value || ' - ' || bp.Name";

		return "";
	}

	private int grandLivre(String whereClauseLettrage, String whereClauseLettrageRAN, String sqlBPartner) {
		boolean baseLanguage=false;
		if (language.equals(Language.getBaseAD_Language()))
			baseLanguage=true;

		int dt_bs = getDocTypeForBankStatement();
		int dt_a = getDocTypeForAllocation();

		String s_insert = "INSERT INTO T_LFR_Report"
				+ " (T_LFR_Report_ID, AD_PInstance_ID, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy,"
				+ " Fact_Acct_ID, C_ElementValue_ID, AmtAcctDr, AmtAcctCr, DateAcct, LFR_FactAcctDescription,"
				+ " LFR_FactAcctOrg, LFR_BPartnerDisplayName, LFR_MatchCode, LFR_ReconciliationDate, LFR_SoldeProgressif,"
				+ " ClientName, OrgName, AccountValue, Account_Name, FooterCenter, Title, LFR_DateAsString,"
				+ " C_BPartner_ID, DocTypeName, C_AcctSchema_ID, PostingType)";

		String s_select = "";
		int AD_Sequence_ID = MTLFRReport.getSequenceID(get_TrxName());

		s_select = " SELECT nextidfunc(" + AD_Sequence_ID + ",'N'), " + getAD_PInstance_ID() + ", " + getAD_Client_ID() + ", 0, SysDate, " + getAD_User_ID() +", SysDate, " + getAD_User_ID() + ","
				+ " NULL, " + p_C_ElementValue_ID + ", AmtAcctDr, AmtAcctCr, DateAcct, RESULT.Description,"
				+ " RESULT.OrgName, RESULT.LFR_BPartnerDisplayName, RESULT.LFR_MatchCode, RESULT.LFR_ReconciliationDate, RESULT.AmtAcct,"
				+ DB.TO_STRING(getClientName()) + ", " + DB.TO_STRING(getOrgName()) + ", " + DB.TO_STRING(m_accountValue) + ", " + DB.TO_STRING(m_accountName) + ","
				+ DB.TO_STRING(getFooterCenter()) + ", " + DB.TO_STRING(reportTitle) + ", " + DB.TO_STRING(m_criteresDate) + ", "
				+ " RESULT.C_BPartner_ID, RESULT.DocTypeName, " + p_acctSchema_ID + ", " + DB.TO_STRING(p_postingType);

		// Soit détail RAN (basé sur lettrage), soit une ligne qui totalise les écritures précédentes
		if (p_isRANDetail)
		{
			s_select += " FROM (SELECT fa.Fact_Acct_ID Fact_Acct_ID, COALESCE(fa.AmtAcctDr,0) AmtAcctDr, COALESCE(fa.AmtAcctCr,0) AmtAcctCr,"
					+ " CASE WHEN fa.Fact_Acct_ID IS NULL THEN " + DB.TO_DATE(m_DateAcct_From, true) + " ELSE fa.DateAcct END DateAcct,"
					+ " CASE WHEN fa.AD_Table_ID = " + MAllocationHdr.Table_ID + " THEN ("	// pour les affectations, on va afficher le montant du paiment 
					+ " SELECT TO_CHAR(aah.DocumentNo || ' (' ||  ap.DocumentNo || ' - ' || ap.PayAmt || ' ' || ac.ISO_Code || ' > ' || ai.DocumentNo || ')')"
					+ " FROM C_AllocationLine aal"
					+ " INNER JOIN C_AllocationHdr aah ON (aah.C_AllocationHdr_ID=aal.C_AllocationHdr_ID)"
					+ " INNER JOIN C_Currency ac ON (aah.C_Currency_ID=ac.C_Currency_ID)"
					+ " LEFT OUTER JOIN C_Payment ap ON (aal.C_Payment_ID = ap.C_Payment_ID)"
					+ " LEFT OUTER JOIN C_Invoice ai ON (aal.C_Invoice_ID = ai.C_Invoice_ID)"
					+ " WHERE aal.C_AllocationLine_ID = fa.Line_ID)"
					+ " WHEN fa.AD_Table_ID <> " + MAllocationHdr.Table_ID + " THEN TO_CHAR(fa.Description)"
					+ " ELSE 'RAN' END Description,"
					+ " o.Name OrgName," + getBPartnerDisplayName() + " LFR_BPartnerDisplayName, "
					+ " fa.LFR_MatchCode AS LFR_MatchCode, fa.LFR_ReconciliationDate, COALESCE(fa.AmtAcct,0) AmtAcct, bp.C_BPartner_ID,"
					+ " CASE WHEN fa.Fact_Acct_ID IS NULL THEN 'RAN' ELSE TO_CHAR(dt.PrintName) END DocTypeName"
					+ " FROM C_BPartner bp"
					+ " INNER JOIN LFR_FactReconciliation_v fa ON (fa.C_BPartner_ID = bp.C_BPartner_ID"
					+ " AND fa.Account_ID = " + p_C_ElementValue_ID
					+ " AND fa.DateAcct < " + DB.TO_DATE(m_DateAcct_From, true)
					+ " AND fa.C_AcctSchema_ID = "+ p_acctSchema_ID 
					+ " AND fa.PostingType = " + DB.TO_STRING(p_postingType) + " ";

					// il faut bidouiller le whereClauseLettrageRAN pour remplacer les l. par des fa. et MatchCode par LFR_MatchCode
					whereClauseLettrageRAN = whereClauseLettrageRAN.replace("l.MatchCode", "l.LFR_MatchCode");
					s_select += whereClauseLettrageRAN.replace("l.", "fa.") + ")"; 

			s_select += " LEFT OUTER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID)"
					+ " LEFT OUTER JOIN AD_Org o ON (fa.AD_Org_ID=o.AD_Org_ID)"
					// DocTypeName
					+ " LEFT OUTER JOIN C_Invoice i ON (fa.Record_ID = i.C_Invoice_ID AND fa.AD_Table_ID = 318)"
					+ " LEFT OUTER JOIN C_Payment p ON (fa.Record_ID = p.C_Payment_ID AND fa.AD_Table_ID = 335)"
					+ " LEFT OUTER JOIN GL_Journal j ON (fa.Record_ID = j.GL_Journal_ID AND fa.AD_Table_ID = 224)"
					+ " LEFT OUTER JOIN C_DocType" + (baseLanguage ? "" : "_Trl ") + " dt ON "
					+ "(fa.fact_acct_id is null and dt.c_doctype_id=0 or ("
					+ " (fa.AD_Table_ID = 318 AND dt.C_DocType_ID = i.C_DocType_ID)"
					+ " OR (fa.AD_Table_ID = 335 AND dt.C_DocType_ID = p.C_DocType_ID)"
					+ " OR (fa.AD_Table_ID = 224 AND dt.C_DocType_ID = j.C_DocType_ID)"
					+ " OR (fa.AD_Table_ID = 392 AND dt.C_DocType_ID = " + dt_bs + ")"
					+ " OR (fa.AD_Table_ID = 735 AND dt.C_DocType_ID = " + dt_a + "))"
					+ (baseLanguage ? "" : " AND dt.AD_Language=" + DB.TO_STRING(language))
					+ ")"
					+ " WHERE bp.AD_Client_ID = " + getAD_Client_ID()
					+ " AND bp.C_BPartner_ID IN ( " + sqlBPartner + ")";
			s_select += getSqlWhereOrg("fa");
		}
		else
		{
			s_select	+= " FROM (SELECT NULL Fact_Acct_ID, 0 AmtAcctDr, 0 AmtAcctCr, NULL DateAcct, 'RAN' Description, NULL OrgName," + getBPartnerDisplayName()
					+ " NULL LFR_MatchCode, NULL LFR_ReconciliationDate, COALESCE((SUM(AmtAcctDr) - SUM(AmtAcctCr)),0) AmtAcct, bp.C_BPartner_ID, 'RAN' DocTypeName"
					//	+ " UPPER(bp."+orderByName+")"// pour utilisation dans orderby
					+ " FROM C_BPartner bp"
					+ " LEFT OUTER JOIN Fact_Acct fa ON (fa.C_BPartner_ID = bp.C_BPartner_ID AND fa.Account_ID = " + p_C_ElementValue_ID
					+ " AND fa.DateAcct < " + DB.TO_DATE(m_DateAcct_From, true) ;
			s_select += getSqlWhereOrg("fa");
			s_select += ")"
					+ " WHERE bp.C_BPartner_ID IN ( " + sqlBPartner + ")";
			s_select += " GROUP BY bp.C_BPartner_ID, bp.Name, bp.Value";
		}

		s_select += " UNION SELECT fa.Fact_Acct_ID Fact_Acct_ID," 
				+ " fa.AmtAcctDr AmtAcctDr, fa.AmtAcctCr AmtAcctCr, fa.DateAcct DateAcct,"
				+ " CASE WHEN fa.AD_Table_ID= 735 THEN ("	// pour les affectations, on va afficher le montant du paiment
				+ " SELECT TO_CHAR(aah.DocumentNo || ' (' ||  ap.DocumentNo || ' - ' || ap.PayAmt || ' ' || ac.ISO_Code || ' > ' || ai.DocumentNo || ')')"
				+ " FROM C_AllocationLine aal"
				+ " INNER JOIN C_AllocationHdr aah ON (aah.C_AllocationHdr_ID=aal.C_AllocationHdr_ID)"
				+ " INNER JOIN C_Currency ac ON (aah.C_Currency_ID=ac.C_Currency_ID)"
				+ " LEFT OUTER JOIN C_Payment ap ON (aal.C_Payment_ID = ap.C_Payment_ID)"
				+ " LEFT OUTER JOIN C_Invoice ai ON (aal.C_Invoice_ID = ai.C_Invoice_ID)"
				+ " WHERE aal.C_AllocationLine_ID = fa.Line_ID)"
				+ " ELSE TO_CHAR(fa.Description) END Description," // car pour RAN on met 'RAN'
				+ " fa.OrgName OrgName," + getBPartnerDisplayName() + " LFR_BPartnerDisplayName, "
				+ " l.MatchCode LFR_MatchCode, l.LFR_ReconciliationDate, fa.AmtAcct AmtAcct, fa.C_BPartner_ID C_BPartner_ID,"
				+ " TO_CHAR(dt.PrintName) DocTypeName"
				+ " FROM RV_Fact_Acct fa"
				+ " LEFT OUTER JOIN Fact_Reconciliation l ON (l.Fact_Acct_ID = fa.Fact_Acct_ID)"
				+ " INNER JOIN C_BPartner bp ON (bp.C_BPartner_ID = fa.C_BPartner_ID)"
				// DocTypeName
				+ " LEFT OUTER JOIN C_Invoice i ON (fa.Record_ID = i.C_Invoice_ID AND fa.AD_Table_ID = 318)"
				+ " LEFT OUTER JOIN C_Payment p ON (fa.Record_ID = p.C_Payment_ID AND fa.AD_Table_ID = 335)"
				+ " LEFT OUTER JOIN GL_Journal j ON (fa.Record_ID = j.GL_Journal_ID AND fa.AD_Table_ID = 224)"
				+ " LEFT OUTER JOIN C_BankStatement bs ON (fa.Record_ID = bs.C_BankStatement_ID AND fa.AD_Table_ID = 392)"
				+ " LEFT OUTER JOIN C_AllocationHdr a ON (fa.Record_ID = a.C_AllocationHdr_ID AND fa.AD_Table_ID = 735)"
				+ " ,C_DocType" + (baseLanguage ? "" : "_Trl ") + " dt"	// on ajoute la table C_DocType ou C_DocType_Trl

				+ " WHERE fa.C_BPartner_ID IN ( " + sqlBPartner + ")"
				+ " AND fa.Account_ID = " + p_C_ElementValue_ID
				+ " AND fa.AD_Client_ID = " + getAD_Client_ID()
				+ " AND TRUNC(fa.DateAcct, 'DD') >= " + DB.TO_DATE(m_DateAcct_From, true) 
				+ " AND TRUNC(fa.DateAcct, 'DD') <= " + DB.TO_DATE(m_DateAcct_To, true)
				+ " AND fa.C_AcctSchema_ID = " + p_acctSchema_ID
				+ " AND fa.PostingType = " + DB.TO_STRING(p_postingType)

				// DocTypeName et N° doc
				+ " AND ("
				+ "    (fa.AD_Table_ID = 318 AND dt.C_DocType_ID = i.C_DocType_ID)"
				+ " OR (fa.AD_Table_ID = 335 AND dt.C_DocType_ID = p.C_DocType_ID)"
				+ " OR (fa.AD_Table_ID = 224 AND dt.C_DocType_ID = j.C_DocType_ID)"
				+ " OR (fa.AD_Table_ID = 392 AND dt.C_DocType_ID = " + dt_bs + ")"
				+ " OR (fa.AD_Table_ID = 735 AND dt.C_DocType_ID = " + dt_a + "))"
				+ (baseLanguage ? "" : " AND dt.AD_Language=" + DB.TO_STRING(language));

		s_select += getSqlWhereOrg("fa") + " ";
		
		s_select += whereClauseLettrage;
		s_select += " ORDER BY 7, DateAcct NULLS FIRST, Fact_Acct_ID NULLS FIRST"; // on ajoute le tri avec Fact_Acct_ID pour que les écritures d'une même date soient toujours affichées dans le même ordre
		s_select += ") RESULT";

		log.fine(s_insert+s_select);
		lines = lines + DB.executeUpdateEx(s_insert+s_select, get_TrxName());

		return lines;
	}

	private int balance(String listComptesAux, String sqlBPartner)	{

		List<List<Object>> bpartnerRows = DB.getSQLArrayObjectsEx(get_TrxName(), sqlListBPartnerAvecOrderBy(sqlBPartner));
		if (bpartnerRows != null && bpartnerRows.size() > 0) {
			for (List<Object> bpartnerRow : bpartnerRows) {

				lines++;

				int bpartnerID = ((BigDecimal) bpartnerRow.get(0)).intValue();
				String bpartnerDisplayName = (String) bpartnerRow.get(1);
				statusUpdate(bpartnerDisplayName);

				MTLFRReport taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
				setAmountEqualZero(taf);
				taf.setLine(lines);
				taf.setC_BPartner_ID(bpartnerID);
				taf.setLFR_BPartnerDisplayName(bpartnerDisplayName);
				taf.setClientName(getClientName());
				updateCommun(taf);

				BigDecimal debut_solde = Env.ZERO;

				// Calcul du Solde début
				StringBuilder sql = new StringBuilder("SELECT COALESCE((SUM(AmtAcctDr) - SUM(AmtAcctCr)), 0) AS SoldeDebut FROM Fact_Acct")
						.append(" WHERE C_BPartner_ID = ").append(bpartnerID)
						.append(" AND DateAcct < ").append(DB.TO_DATE(m_DateAcct_From, true))
						.append(" AND C_AcctSchema_ID = ").append(p_acctSchema_ID)
						.append(" AND PostingType = ").append(DB.TO_STRING(p_postingType))
						.append(" AND Account_ID IN (").append(listComptesAux).append(")");

				sql.append(getSqlWhereOrg(""));

				debut_solde = DB.getSQLValueBD(get_TrxName(), sql.toString());
				if (debut_solde.compareTo(new BigDecimal(0)) > 0)
					taf.setLFR_AmtDebutDr(debut_solde);
				if (debut_solde.compareTo(new BigDecimal(0)) < 0)
					taf.setLFR_AmtDebutCr(debut_solde.negate());

				// Mouvements
				sql = new StringBuilder("SELECT COALESCE(SUM(AmtAcctDR), 0), COALESCE(SUM(AmtAcctCR), 0) FROM Fact_Acct")
						.append(" WHERE C_BPartner_ID = ").append(bpartnerID)
						.append(" AND Account_ID IN (").append(listComptesAux).append(") ")
						.append(" AND DateAcct >= ").append(DB.TO_DATE(m_DateAcct_From, true)).append(" AND DateAcct <= ").append(DB.TO_DATE(m_DateAcct_To, true))
						.append(" AND C_AcctSchema_ID = ").append(p_acctSchema_ID)
						.append(" AND PostingType = ").append(DB.TO_STRING(p_postingType));

				sql.append(getSqlWhereOrg(""));
				
				BigDecimal mvts_debit = new BigDecimal (0);
				BigDecimal mvts_credit = new BigDecimal (0);
				BigDecimal mvts_solde = new BigDecimal (0);

				List<List<Object>> rows = DB.getSQLArrayObjectsEx(get_TrxName(), sql.toString());
				if (rows != null && rows.size() > 0) {
					for (List<Object> row : rows) {
						mvts_debit = (BigDecimal) row.get(0);
						mvts_credit = (BigDecimal) row.get(1);
						taf.setAmtAcctDr(mvts_debit);
						taf.setAmtAcctCr(mvts_credit);
						mvts_solde = mvts_debit.add(mvts_credit.negate());
						taf.setAmtAcct(mvts_solde);
					}
				}

				// Calcul du solde final
				BigDecimal final_solde = debut_solde.add(mvts_solde);
				if (final_solde.compareTo(new BigDecimal(0)) > 0)
					taf.setLFR_AmtFinalDr(final_solde);
				if (final_solde.compareTo(new BigDecimal(0)) < 0)
					taf.setLFR_AmtFinalCr(final_solde.negate());
				taf.saveEx();
			}
		}
		return lines;
	}

	private int extraitCompte(String whereClauseLettrage, String whereClauseLettrageRAN, String compteAux, String sqlBPartner) throws SQLException {

		PreparedStatement ps = DB.prepareStatement(sqlListBPartnerAvecOrderBy(sqlBPartner), get_TrxName());
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {

			lines++;
			int bpartnerID = rs.getInt("C_BPartner_ID");
			String bpAffich = rs.getString("BPartnerDisplayName");
			statusUpdate(bpAffich);
			
			// Pour l'extrait de compte UNIQUEMENT, calcul des totaux D et C pour les comptes déf et intermédiaires
			calculDefEtTemp(compteAux, bpartnerID);

			p_isRANDetailBP = p_isRANDetail;	// on réinitialise la variable (en fonction du paramètre du process)

			BigDecimal debut_solde = Env.ZERO;

			if (p_isRANDetailBP) { // on demande le détail des RAN ; mais s'il n'y a pas, on force p_IsRANDetailBP = false pour insérer une ligne RAN
				StringBuilder sqlRAN = new StringBuilder("SELECT fa.Fact_Acct_ID, fa.AD_Table_ID, fa.Record_ID, fa.Line_ID, fa.DateAcct, fa.AmtAcctDR, fa.AmtAcctCR, fa.AmtAcct, fa.OrgName, fa.Description")
						.append(", l.MatchCode, l.LFR_ReconciliationDate")
						.append(" FROM RV_Fact_Acct fa")
						.append(" LEFT OUTER JOIN Fact_Reconciliation l ON (l.Fact_Acct_ID = fa.Fact_Acct_ID)")	
						.append(" WHERE fa.C_BPartner_ID = ").append(bpartnerID)
						.append(" AND fa.Account_ID = ").append(p_C_ElementValue_ID)
						.append(" AND fa.AD_Client_ID = ").append(getAD_Client_ID())
						.append(" AND TRUNC(fa.DateAcct, 'DD') < ").append(DB.TO_DATE(m_DateAcct_From))
						.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
						.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType))
						.append(whereClauseLettrageRAN)
						.append(getSqlWhereOrg("fa"))
						.append(" ORDER BY fa.DateAcct");

				PreparedStatement pstmtRan = DB.prepareStatement(sqlRAN.toString(), get_TrxName());
				ResultSet rsRan = pstmtRan.executeQuery ();
				boolean hasDetailRAN = false;

				while (rsRan.next()) {
					lines++;

					debut_solde = debut_solde.add(rsRan.getBigDecimal("AmtAcct").setScale(2, RoundingMode.HALF_UP));

					MTLFRReport taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
					taf.setLine(lines);
					setAmountEqualZero(taf);

					taf.setFact_Acct_ID(rsRan.getInt("Fact_Acct_ID"));
					taf.setC_BPartner_ID(bpartnerID);
					taf.setLFR_BPartnerDisplayName(bpAffich);
					taf.setLFR_FactAcctOrg(rsRan.getString("OrgName")); // l'organisation est mise systématiquement sur la ligne de mouvement (par opposition à la ligne de RAN)
					taf.setDateAcct(rsRan.getTimestamp("DateAcct"));
					taf.setAmtAcctDr(rsRan.getBigDecimal("AmtAcctDR").setScale(2, RoundingMode.HALF_UP));
					taf.setAmtAcctCr(rsRan.getBigDecimal("AmtAcctCR").setScale(2, RoundingMode.HALF_UP));
					taf.setLFR_SoldeProgressif(debut_solde);
					taf.setAmtAcct(taf.getAmtAcctDr().add(taf.getAmtAcctCr().negate()));
					taf.setLFR_FactAcctDescription(rsRan.getString("Description"));
					taf.setLFR_MatchCode(rsRan.getString("MatchCode"));
					taf.setLFR_ReconciliationDate(rsRan.getTimestamp("LFR_ReconciliationDate"));

					updateCommun(taf);
					updateGLEC(taf);
					setDocTypeName(taf, rsRan.getInt("AD_Table_ID"), rsRan.getInt("Record_ID"), rsRan.getInt("Line_ID"));

					taf.saveEx();
					
					hasDetailRAN = true;
				}
				DB.close(rsRan, pstmtRan);

				if (!hasDetailRAN)
					p_isRANDetailBP = false; // si aucune ligne, on va quand même insérer une ligne RAN
			}	// fin RANDetail
			
			if (!p_isRANDetailBP) // on force une ligne RAN avec un montant
			{
				MTLFRReport taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
				
				// insertion systématique de la 1ère ligne => solde initial à 0 ; pourra être mise à jour par la suite
				setAmountEqualZero(taf);
				taf.setLine(lines++);

				// Contenu du champ Tiers à afficher BPAffich

				taf.setC_BPartner_ID(bpartnerID);
				taf.setLFR_BPartnerDisplayName(bpAffich);
				taf.setClientName(getClientName());
				taf.setLFR_FactAcctOrg(getOrgNameIfSingle());
				updateCommun(taf);

				taf.setDocTypeName("RAN");
				taf.setDescription("RAN");
				taf.setDateAcct(m_DateAcct_From);
				taf.setAccountValue(m_accountValue);
				taf.setAccount_Name(m_accountName);
				updateGLEC(taf);
				updateCommun(taf);

				taf.setLFR_FactAcctOrg(getOrgNameIfSingle());
				
				// Calcul du solde initial lui-même
				StringBuilder sql = new StringBuilder("SELECT COALESCE((SUM(AmtAcctDr) - SUM(AmtAcctCr)), 0) AS SoldeDebut FROM Fact_Acct")
						.append(" WHERE C_BPartner_ID = ").append(bpartnerID)
						.append(" AND TRUNC(DateAcct, 'DD') < ").append(DB.TO_DATE(m_DateAcct_From))
						.append(" AND C_AcctSchema_ID = ").append(p_acctSchema_ID)
						.append(" AND PostingType = ").append(DB.TO_STRING(p_postingType))
						.append(" AND Account_ID = ").append(p_C_ElementValue_ID)
						.append(getSqlWhereOrg(""));
				debut_solde = DB.getSQLValueBDEx(get_TrxName(), sql.toString());
				taf.setLFR_SoldeProgressif(debut_solde);
				taf.setAmtAcct(debut_solde);
				taf.saveEx();

			}	// fin 1ère ligne (si pas détail RAN ou balance)

			// ajout des lignes de mouvements
			StringBuilder sql = new StringBuilder("SELECT fa.Fact_Acct_ID, fa.AD_Table_ID, fa.Record_ID, fa.Line_ID, fa.DateAcct, fa.AmtAcctDR, fa.AmtAcctCR, fa.AmtAcct, fa.OrgName, fa.Description,")
					.append(" l.MatchCode, l.LFR_ReconciliationDate")
					.append(" FROM RV_Fact_Acct fa")
					.append(" LEFT OUTER JOIN Fact_Reconciliation l ON (l.Fact_Acct_ID = fa.Fact_Acct_ID)")
					.append(" WHERE fa.C_BPartner_ID = ").append(bpartnerID)
					.append(" AND fa.Account_ID = ").append(p_C_ElementValue_ID)
					.append(" AND fa.AD_Client_ID = ").append(getAD_Client_ID())
					.append(" AND TRUNC(fa.DateAcct, 'DD') >= ").append(DB.TO_DATE(m_DateAcct_From)) 
					.append(" AND TRUNC(fa.DateAcct, 'DD') <= ").append(DB.TO_DATE(m_DateAcct_To))
					.append(" AND fa.C_AcctSchema_ID = ").append(p_acctSchema_ID)
					.append(" AND fa.PostingType = ").append(DB.TO_STRING(p_postingType))
					.append(getSqlWhereOrg("fa"))
					.append(whereClauseLettrage)
					.append(" ORDER BY fa.DateAcct, fa.AD_Table_ID, fa.Record_ID");

			BigDecimal soldeProgressif = debut_solde; 

			PreparedStatement pstmtDetail = DB.prepareStatement(sql.toString(), get_TrxName());
			ResultSet rsDetail = pstmtDetail.executeQuery ();

			while (rsDetail.next()) {
				lines++;

				soldeProgressif = soldeProgressif.add(rsDetail.getBigDecimal("AmtAcct").setScale(2, RoundingMode.HALF_UP));

				MTLFRReport taf = new MTLFRReport(getCtx(), 0, getAD_PInstance_ID(), get_TrxName());
				taf.setLine(lines);
				setAmountEqualZero(taf);

				taf.setFact_Acct_ID(rsDetail.getInt("Fact_Acct_ID"));
				taf.setC_BPartner_ID(bpartnerID);
				taf.setLFR_BPartnerDisplayName(bpAffich);

				taf.setLFR_FactAcctOrg(rsDetail.getString("OrgName")); // l'organisation est mise systématiquement sur la ligne de mouvement (par opposition à la ligne de RAN)
				taf.setDateAcct(rsDetail.getTimestamp("DateAcct"));
				taf.setAmtAcctDr(rsDetail.getBigDecimal("AmtAcctDR").setScale(2, RoundingMode.HALF_UP));
				taf.setAmtAcctCr(rsDetail.getBigDecimal("AmtAcctCR").setScale(2, RoundingMode.HALF_UP));
				taf.setLFR_SoldeProgressif(soldeProgressif);
				taf.setAmtAcct(taf.getAmtAcctDr().add(taf.getAmtAcctCr()).negate());
				taf.setLFR_FactAcctDescription(rsDetail.getString("Description"));
				taf.setLFR_MatchCode(rsDetail.getString("MatchCode"));
				taf.setLFR_ReconciliationDate(rsDetail.getTimestamp("LFR_ReconciliationDate"));

				updateCommun(taf);
				updateGLEC(taf);
				setDocTypeName(taf, rsDetail.getInt("AD_Table_ID"), rsDetail.getInt("Record_ID"), rsDetail.getInt("Line_ID"));

				taf.saveEx();
			}
			DB.close(rsDetail, pstmtDetail);
		}
		
		DB.close(rs, ps);
		return lines;
	}

	private void calculDefEtTemp(String bpType, int C_BPartner_ID)
	{
		String sql4 =  "SELECT COALESCE(SUM(fa.AmtAcctDr), 0), COALESCE(SUM(fa.AmtAcctCr), 0)" 
				+ " FROM Fact_Acct fa"
				+ " WHERE fa.C_BPartner_ID = " + C_BPartner_ID
				+ " AND fa.DateAcct >= " + DB.TO_DATE(m_DateAcct_From)
				+ " AND fa.DateAcct <= " + DB.TO_DATE(m_DateAcct_To)
				+ " AND fa.C_AcctSchema_ID = " + p_acctSchema_ID
				+ " AND fa.PostingType = " + DB.TO_STRING(p_postingType);
		
		sql4 += getSqlWhereOrg("fa");

		// Choix des comptes
		String sql4t = "";
		String sql4d = "";

		if (bpType.equals(MElementValue.BPARTNERTYPE_Vendor)) {
			sql4t = sql4 + "AND fa.Account_ID IN (SELECT Account_ID FROM C_ValidCombination " +
					"WHERE C_ValidCombination_ID IN " +
					"(SELECT DISTINCT B_PaymentSelect_Acct FROM C_BankAccount_Acct WHERE C_AcctSchema_ID = " + p_acctSchema_ID + "))";

			sql4d = sql4 + "AND fa.Account_ID IN (SELECT Account_ID FROM C_ValidCombination " +
					"WHERE C_ValidCombination_ID IN " +
					"(SELECT DISTINCT V_Liability_Acct FROM C_BP_Vendor_Acct WHERE C_AcctSchema_ID = " + p_acctSchema_ID + "))";
		} else if (bpType.equals(MElementValue.BPARTNERTYPE_Customer)) {
			sql4t = sql4 + "AND fa.Account_ID IN (SELECT Account_ID FROM C_ValidCombination " +
					"WHERE C_ValidCombination_ID IN " +
					"(SELECT DISTINCT B_UnAllocatedCash_Acct FROM C_BankAccount_Acct WHERE C_AcctSchema_ID = " + p_acctSchema_ID + "))";

			sql4d = sql4 + "AND fa.Account_ID IN (SELECT Account_ID FROM C_ValidCombination " +
					"WHERE C_ValidCombination_ID IN " +
					"(SELECT DISTINCT C_Receivable_Acct FROM C_BP_Customer_Acct WHERE C_AcctSchema_ID = " + p_acctSchema_ID + "))";		
		}

		List<List<Object>> rows = DB.getSQLArrayObjectsEx(get_TrxName(), sql4t);
		if (rows != null && rows.size() > 0) {
			for (List<Object> row : rows) {
				m_amtPeriodeTempDebit = (BigDecimal) row.get(0);
				m_amtPeriodeTempCredit = (BigDecimal) row.get(1);
				m_amtPeriodeTempSolde = m_amtPeriodeTempDebit.add(m_amtPeriodeTempCredit.negate());
			}
		}

		rows = DB.getSQLArrayObjectsEx(get_TrxName(), sql4d);
		if (rows != null && rows.size() > 0) {
			for (List<Object> row : rows) {
				m_amtPeriodeDefDebit = (BigDecimal) row.get(0);
				m_amtPeriodeDefCredit = (BigDecimal) row.get(1);
				m_amtPeriodeDefSolde = m_amtPeriodeDefDebit.add(m_amtPeriodeDefCredit.negate());
			}
		}
	}

	private void updateCommun(MTLFRReport taf) {
		taf.setC_ElementValue_ID(p_C_ElementValue_ID);
		taf.setFooterCenter(getFooterCenter());
		taf.setTitle(reportTitle);
		taf.setLFR_DateAsString(m_criteresDate);
		taf.setC_AcctSchema_ID(p_acctSchema_ID);
		taf.setPostingType(p_postingType);
	}	//	updateCommun

	// Update uniquement pour GL et EC
	private void updateGLEC(MTLFRReport taf) {
		taf.setClientName(getClientName());
		taf.setOrgName(getOrgName());
		taf.setAccountValue(m_accountValue);
		taf.setAccount_Name(m_accountName);
		taf.setLFR_AmtPeriodeTempDr(m_amtPeriodeTempDebit);
		taf.setLFR_AmtPeriodeTempCr(m_amtPeriodeTempCredit);
		taf.setLFR_AmtPeriodeTemp(m_amtPeriodeTempSolde);
		taf.setLFR_AmtPeriodeDefDr(m_amtPeriodeDefDebit);
		taf.setLFR_AmtPeriodeDefCr(m_amtPeriodeDefCredit);
		taf.setLFR_AmtPeriodeDef(m_amtPeriodeDefSolde);
	}	//	updateGLEC

	private void setDocTypeName(MTLFRReport taf, int tableID, int recordID, int lineID) {

		int docTypeID = DB.getSQLValue(get_TrxName(), "SELECT C_DocType_ID FROM " + MTable.getTableName(getCtx(), tableID) + " WHERE " + MTable.getTableName(getCtx(), tableID) + "_ID = " + recordID);
		String docTypeName = ""; 
		String descr = "";

		if (tableID == MAllocationHdr.Table_ID) { // C_DocType_ID n'est pas obligatoire et on force une description
			docTypeName = "Affectation";
			descr = getDescriptionForAllocation(docTypeID, recordID, lineID);

			if (Util.isEmpty(descr)) {
				String sqlDescription = "SELECT ah.DocumentNo || ' (' ||  p.DocumentNo || ' - ' || p.PayAmt || ' ' || c.ISO_Code || ' > ' || i.DocumentNo || ')'"
						+ " FROM C_AllocationLine al"
						+ " INNER JOIN C_AllocationHdr ah ON (ah.C_AllocationHdr_ID=al.C_AllocationHdr_ID)"
						+ " INNER JOIN C_Currency c ON (ah.C_Currency_ID=c.C_Currency_ID)"
						+ " LEFT OUTER JOIN C_Payment p ON (al.C_Payment_ID = p.C_Payment_ID)"
						+ " LEFT OUTER JOIN C_Invoice i ON (al.C_Invoice_ID = i.C_Invoice_ID)"
						+ " WHERE al.C_AllocationLine_ID = " + lineID;
				descr = DB.getSQLValueString(get_TrxName(), sqlDescription);	
			}

			taf.setDescription(descr);
		}

		if (docTypeID > 0)
			docTypeName = MDocType.get(getCtx(), docTypeID).get_Translation("Name");

		taf.setDocTypeName(docTypeName);
	}	// getDocTypeName

	protected String getDescriptionForAllocation(int docTypeID, int recordID, int lineID) {
		return "";
	}

	/** Renvoie un SQL qui liste les tiers concernés par l'édition, triés selon le paramétrage défini dans le schéma comptable */
	private String sqlListBPartnerAvecOrderBy(String sqlBPartner) {

		StringBuilder sql = new StringBuilder("SELECT bp.C_BPartner_ID, ").append(getBPartnerDisplayName()).append(" AS BPartnerDisplayName")
		.append(" FROM C_BPartner bp")
		.append(" WHERE bp.C_BPartner_ID IN (").append(sqlBPartner).append(")");

		/* Il faut borner les tiers à ramener dans le cas du grand livre ou de l'extrait de compte
		 * Si p_C_BPartner_From_ID = 0 && p_C_BPartner_To_ID = 0  >> tous les tiers, donc on fait rien (=Grand Livre)
		 * Si p_C_BPartner_From_ID > 0 && p_C_BPartner_To_ID = 0  >> uniquement p_C_BPartner_From_ID
		 * Si p_C_BPartner_From_ID > 0 && p_C_BPartner_To_ID > 0  >> les tiers entre les 2
		 */
		if (p_type.equals(TYPE_EXTRAIT_COMPTE_CLIENT) || p_type.equals(TYPE_EXTRAIT_COMPTE_FOURNISSEUR)) {
			if (p_C_BPartner_From_ID > 0 && p_C_BPartner_To_ID > 0) {
				sql.append("AND UPPER(bp.Name) >= (SELECT UPPER(Name) FROM C_BPartner WHERE C_BPartner_ID = ").append(p_C_BPartner_From_ID)
				.append(") AND UPPER(bp.Name) <= (SELECT UPPER(Name) FROM C_BPartner WHERE C_BPartner_ID = ").append(p_C_BPartner_To_ID).append(") ");
			}
			else if (!Util.isEmpty(p_bpartnerIDs))
				sql.append(" AND bp.C_BPartner_ID IN (").append(p_bpartnerIDs).append(")");
			else
				sql.append(" AND bp.C_BPartner_ID = ").append(p_C_BPartner_From_ID);
		}

		sql.append(" ORDER BY UPPER(bp.").append(m_orderBy).append(")");

		return sql.toString();
	}

	protected String getOrderBy() {
		return ORDER_BY_NAME;
	}

	/** Comptes concernés par les balances (comptes auxilliaires si balance aux ; compte défini dans la charge 'ClientDouteux' si balance douteux) */	
	protected String getComptesAux(String bpType)
	{
		String retValue = "";

		String sql = "SELECT C_ElementValue_ID FROM C_ElementValue WHERE AD_Client_ID = " + getAD_Client_ID()
		+ " AND BPartnerType = " + DB.TO_STRING(bpType)
		+ "AND IsActive='Y' ";

		for (int accountID : DB.getIDsEx(get_TrxName(), sql)) {
			if (!Util.isEmpty(retValue))
				retValue += ", ";
			retValue += accountID;
		}
		return retValue;
	}
	
	private void setAmountEqualZero(MTLFRReport taf) {
		taf.setAmtAcctDr(Env.ZERO);
		taf.setAmtAcctCr(Env.ZERO);
		taf.setLFR_SoldeProgressif(Env.ZERO);
		taf.setAmtAcctDr(Env.ZERO);
		taf.setAmtAcctCr(Env.ZERO);
		taf.setAmtAcct(Env.ZERO);
	}

	protected String getTitleCenter() {
		StringBuilder retValue = new StringBuilder();
		if (p_type.equals(TYPE_BALANCE))
			retValue.append("Balance");
		else if (p_type.equals(TYPE_GRAND_LIVRE))
			retValue.append("Grand Livre");
		else 
			retValue.append("Extrait de compte");

		retValue.append(" ");
		if (p_type.equals(TYPE_EXTRAIT_COMPTE_CLIENT))
			retValue.append("clients");
		else if (p_type.equals(TYPE_EXTRAIT_COMPTE_FOURNISSEUR)) {
			if (p_isEmployee)
				retValue.append("salariés");
			else
				retValue.append("fournisseurs");	
		}
		return retValue.toString();
	}

}	//	LFR_FactAux
