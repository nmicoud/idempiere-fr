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

import static fr.idempiere.model.SystemIDs_LFR.C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT;
import static fr.idempiere.model.SystemIDs_LFR.C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaGL;
import org.compiere.model.MConversionType;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalBatch;
import org.compiere.model.MJournalLine;
import org.compiere.model.MOrg;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Msg;

/**
 *	Process de préparation des reports à nouveau
 *  @author Nicolas Micoud - TGI
 */

public class LFR_RanPrepa extends LfrProcess {

	private String		p_type = "";
	private int			p_journalBatchID = 0;
	private int			p_acctSchemaID = 0;
	private Timestamp	p_dateAcctFrom = null;
	private Timestamp	p_dateAcctTo = null;
	private Timestamp	p_dateAcct = null;
	private int			p_docTypeID = 0;
	private int			p_orgID = -1;
	private boolean		p_IsJournalPerOrg = false;
	private int			p_orgForJournalID = 0;
	private int			p_orgForBatchID = 0;

	private final static String TYPE_BATCH = "B";
	private final static String TYPE_JOURNAL = "J";

	private String 		m_description = "";

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
				;
			if (name.equals("C_AcctSchema_ID"))
				p_acctSchemaID = para[i].getParameterAsInt();
			else if (name.equals("LFR_DateAcctParaRange")) {
				p_dateAcctFrom = para[i].getParameterAsTimestamp(); 
				p_dateAcctTo = para[i].getParameter_ToAsTimestamp();
			}
			else if (name.equals("DateAcct"))
				p_dateAcct = para[i].getParameterAsTimestamp();
			else if (name.equals("C_DocType_ID"))
				p_docTypeID = para[i].getParameterAsInt();

			else if (name.equals("p_type"))
				p_type = para[i].getParameterAsString();
			else if (name.equals("GL_JournalBatch_ID"))
				p_journalBatchID = para[i].getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_orgID = para[i].getParameterAsInt();
			else if (name.equals("SeparateOrg"))
				p_IsJournalPerOrg = para[i].getParameterAsBoolean();
			else if (name.equals("OrgForJournal_ID"))
				p_orgForJournalID = para[i].getParameterAsInt();
			else if (name.equals("OrgForBatch_ID"))
				p_orgForBatchID = para[i].getParameterAsInt();
		}
	}	//	prepare

	protected String doIt() throws Exception
	{
		MAcctSchema as = MAcctSchema.get(getCtx(), p_acctSchemaID);

		if (MAcctSchemaGL.get(getCtx(), p_acctSchemaID).get_ValueAsInt(C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT) <= 0 
				|| MAcctSchemaGL.get(getCtx(), p_acctSchemaID).get_ValueAsInt(C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT) <= 0)
			return "@Error@ @FillMandatory@ @C_AcctSchema_ID@ @LFR_RanBenef_Acct@ / @LFR_RanPerte_Acct@";

		String returnMsg = "";
		m_description = Msg.getMsg(getCtx(), "LFR_RAN") + " " + DisplayType.getDateFormat(DisplayType.Date).format(p_dateAcct);

		MJournalBatch batch = null;
		if (p_type.equals(TYPE_BATCH)) {
			if (p_journalBatchID==0) { // Création du GL_JournalBatch
				batch = new MJournalBatch(getCtx(), 0, get_TrxName());
				batch.setC_DocType_ID(p_docTypeID);
				batch.setGL_Category_ID(MDocType.get(getCtx(), p_docTypeID).getGL_Category_ID());
				batch.setPostingType(MJournalBatch.POSTINGTYPE_Actual);
				batch.setDescription(m_description);
				batch.setDateAcct(p_dateAcct);
				batch.setDateDoc(p_dateAcct);
				batch.setC_Currency_ID(as.getC_Currency_ID());

				// Si l'utilisateur s'est connecté avec l'organisation 0, c'est celle-ci qui sera automatiquement attribuée au batch. Or il faut obligatoirement une organisation de transaction ; on va donc prendre la première
				if (p_orgID==0)
					batch.setAD_Org_ID(p_orgForBatchID);
				else
					batch.setAD_Org_ID(p_orgID);

				batch.saveEx();
			}
			else
				batch = new MJournalBatch(getCtx(), p_journalBatchID, get_TrxName());

			returnMsg = "@GL_JournalBatch_ID@ " + batch.getDocumentNo();
		}
		// p_AD_Org_ID
		//	Si p_AD_Org_ID = 0 ; faut faire 1 GL_Journal pour toutes les organisations du client
		//	Si p_AD_Org_ID > 0 ; on fait uniquement pour l'organisation sélectionnée
		if (p_orgID <= 0 && p_IsJournalPerOrg)	{ // toutes les org, un journal par org
			// Sélection des org concernées
			String sql = getSQL("DISTINCT fa.AD_Org_ID", 0, "fa.AD_Org_ID", "fa.AD_Org_ID");

			for (int orgID : DB.getIDsEx(get_TrxName(), sql)) {
				MOrg o = new MOrg(getCtx(), orgID, get_TrxName());
				MJournal journal = null;

				if (p_type.equals(TYPE_BATCH))
					journal = createJournal(batch, o);
				else if (p_type.equals(TYPE_JOURNAL)) {
					journal = createJournal(o.getAD_Org_ID());
					journal.saveEx();
					returnMsg += journal.getDocumentNo() + " / ";
				}
				insertJournalLines(journal, o.getAD_Org_ID());
			}	//	for all orgs
		} else {	// une seule org ou les org regroupées dans un seul journal
			if (!p_IsJournalPerOrg)
				p_orgID = p_orgForJournalID;	// on utilise l'org définie en paramètre

			MOrg org = new MOrg(getCtx(), p_orgID, get_TrxName());

			MJournal journal = null;
			if (p_type.equals(TYPE_BATCH))
				journal = createJournal(batch, org);
			else if (p_type.equals(TYPE_JOURNAL)) {
				journal = createJournal(p_orgID);
				journal.saveEx();
				returnMsg += journal.getDocumentNo() + " / ";
			}
			insertJournalLines(journal, p_orgID);			
		}

		if (p_type.equals(TYPE_JOURNAL))
			returnMsg = Msg.translate(getCtx(), "GL_Journal_ID") + " " + returnMsg.substring(0, returnMsg.length()-3);

		return Msg.translate(getCtx(), "LFR_RanPrepaOK") + returnMsg;
	}	//	doIt

	private MJournal createJournal(MJournalBatch batch, MOrg org)
	{
		MJournal journal = new MJournal(batch);
		journal.setAD_Org_ID(org.getAD_Org_ID());
		journal.setC_AcctSchema_ID(p_acctSchemaID);
		journal.setDescription (m_description + " " + org.getName());
		journal.setGL_Category_ID(batch.getGL_Category_ID());
		journal.setC_ConversionType_ID(MConversionType.getDefault(getAD_Client_ID()));
		journal.saveEx();

		return journal;
	}	// createJournal

	private void insertJournalLines(MJournal journal, int orgID) {
		String sql = getSQL("fa.Account_ID, SUM(fa.AmtAcctDR) - SUM(fa.AmtAcctCR)", (p_IsJournalPerOrg ? orgID : 0), "fa.Account_ID, ev.Value ", "ev.Value");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();

			BigDecimal total = new BigDecimal(0);
			int LineNo = 10;
			while (rs.next()) {
				MJournalLine line = new MJournalLine(journal);
				line.setAD_Org_ID(orgID);
				line.setAccount_ID(rs.getInt(1));
				line.setDescription(m_description);

				// Solde du compte
				BigDecimal solde = new BigDecimal (rs.getDouble(2)).setScale(2, RoundingMode.HALF_UP);
				if (solde.compareTo(new BigDecimal(0)) > 0)
					line.setAmtSourceDr(solde);
				if (solde.compareTo(new BigDecimal(0)) < 0)
					line.setAmtSourceCr(solde.negate());
				total=total.add(solde);

				line.setLine(LineNo);
				LineNo = LineNo + 10;

				line.saveEx();
			}

			// Insertion de la ligne qui équilibre
			if (total.compareTo(new BigDecimal(0)) !=0) {
				MJournalLine line = new MJournalLine(journal);

				// Compte et montant
				MAcctSchemaGL asGL = MAcctSchemaGL.get(getCtx(), p_acctSchemaID);

				int combi_id = 0;
				if (total.compareTo(new BigDecimal(0)) < 0) {
					line.setAmtSourceDr(total.negate());
					combi_id = asGL.get_ValueAsInt(C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT);
				}
				if (total.compareTo(new BigDecimal(0)) > 0) {
					line.setAmtSourceCr(total);
					combi_id = asGL.get_ValueAsInt(C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT);
				}

				MAccount combination = new MAccount(getCtx(), combi_id, get_TrxName());
				int account_id = combination.getAccount_ID();
				if (account_id > 0)
					line.setAccount_ID(account_id);
				line.setDescription(m_description);
				line.setLine(LineNo);
				line.saveEx();
			}
		}
		catch (SQLException e) {
			log.log(Level.SEVERE, "", e);
			throw new IllegalStateException(Msg.translate(getCtx(), "@Error@ @SQL@"));
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	}	// insertJournalLines

	private MJournal createJournal(int orgID)
	{
		MJournal journal = new MJournal(getCtx(), 0, get_TrxName());
		journal.setClientOrg(getAD_Client_ID(), orgID);
		journal.setC_DocType_ID(p_docTypeID);
		journal.setGL_Category_ID(MDocType.get(getCtx(), p_docTypeID).getGL_Category_ID());
		journal.setPostingType(MJournal.POSTINGTYPE_Actual);
		journal.setDateDoc(p_dateAcct);
		journal.setDateAcct(p_dateAcct);
		journal.setDescription(m_description);
		journal.setC_AcctSchema_ID(p_acctSchemaID);
		journal.setC_Currency_ID(getAcctSchema().getC_Currency_ID());
		journal.setC_ConversionType_ID(MConversionType.getDefault(getAD_Client_ID()));
		return journal;
	}

	private String getSQL(String select, int orgID, String groupBy, String order) {

		StringBuilder sql = new StringBuilder("SELECT ").append(select)
				.append(" FROM Fact_Acct fa, C_ElementValue ev")
				.append(" WHERE fa.C_AcctSchema_ID = ").append(p_acctSchemaID)
				.append(" AND fa.PostingType = ").append(DB.TO_STRING(MFactAcct.POSTINGTYPE_Actual))
				.append(" AND TRUNC(fa.DateAcct, 'DD') >= ").append(DB.TO_DATE(p_dateAcctFrom))
				.append(" AND TRUNC(fa.DateAcct, 'DD') <= ").append(DB.TO_DATE(p_dateAcctTo))
				.append(" AND SUBSTR(ev.Value, 1, 1) >= '1'")
				.append(" AND SUBSTR(ev.Value, 1, 1) <= '5'")
				.append(" AND ev.IsSummary='N' ")
				.append(" AND Account_ID = ev.C_ElementValue_ID");

		if (orgID > 0)
			sql.append(" AND fa.AD_Org_ID = ").append(orgID);

		sql.append(" GROUP BY ").append(groupBy)
		.append(" HAVING (sum(fa.AmtAcctDR) - sum(fa.AmtAcctCR)) <> 0")
		.append(" ORDER BY ").append(order);

		return sql.toString();
	}

	private MAcctSchema getAcctSchema() {
		return MAcctSchema.get(getCtx(), p_acctSchemaID);
	}

}	//	LFR_RanPrepa