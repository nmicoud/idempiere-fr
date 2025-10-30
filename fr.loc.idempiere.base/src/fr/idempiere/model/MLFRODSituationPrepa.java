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

package fr.idempiere.model;

import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEDEB;
import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEFIN;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaGL;
import org.compiere.model.MColumn;
import org.compiere.model.MConversionType;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalBatch;
import org.compiere.model.MJournalLine;
import org.compiere.model.MOrg;
import org.compiere.model.MPeriod;
import org.compiere.model.MRefList;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

/**
 * OD Situation model
 * @author Nicolas Micoud - TGI
 */

public class MLFRODSituationPrepa extends X_LFR_ODSituationPrepa implements DocAction, DocOptions {

	private static final long serialVersionUID = -7095281994019969293L;
	private boolean	m_justPrepared = false;
	private String m_processMsg = null;

	public MLFRODSituationPrepa (Properties ctx, int LFR_ODSituationPrepa_ID, String trxName) {
		super (ctx, LFR_ODSituationPrepa_ID, trxName);
	}	//	MLFRODSituationPrepa

	public MLFRODSituationPrepa (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}	//	MLFRODSituationPrepa

	protected boolean afterSave(boolean newRecord, boolean success) {
		if (!success)
			return false;

		if (newRecord) {
			String err = syncLines(true);
			if (!Util.isEmpty(err)) {
				log.saveError("SaveError", Msg.parseTranslation(getCtx(), err));
				return false;
			}
		}
		
		return true;
	}
	
	public MLFRODSituationPrepaLine[] getActiveLines(String whereClause) {
		if (!Util.isEmpty(whereClause))
			whereClause = " AND " + whereClause;

		List<MLFRODSituationPrepaLine> list = new Query(getCtx(), MLFRODSituationPrepaLine.Table_Name, "LFR_ODSituationPrepa_ID = ?" + whereClause, get_TrxName())
				.setParameters(getLFR_ODSituationPrepa_ID())
				.setOrderBy(MLFRODSituationPrepaLine.COLUMNNAME_Line)
				.setOnlyActiveRecords(true)
				.list();
		return list.toArray(new MLFRODSituationPrepaLine[list.size()]);
	}	//	getActiveLines

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx, int AD_Table_ID, String[] docAction, String[] options, int index) {
		if (docStatus.equals(DocumentEngine.STATUS_Completed))
			options[index++] = DocumentEngine.ACTION_ReActivate;

		return index;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (action, getDocAction());
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public String prepareIt() {

		if (getActiveLines("").length == 0) {
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public String completeIt() {
		//		Re-Check
		if (!m_justPrepared) {
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		String err = generate();
		if (!Util.isEmpty(err)) {
			m_processMsg = err;
			return DocAction.STATUS_Invalid;
		}

		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean voidIt() {
		return false;
	}

	@Override
	public boolean closeIt() {
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {

		// TODO réactiver jbatch et/ou journaux ?

		setDocAction(DOCACTION_Complete);
		setDocStatus(DOCSTATUS_InProgress);
		setProcessed(false);
		return true;
	}

	@Override
	public String getSummary() {
		return "";
	}

	@Override
	public String getDocumentNo() {
		return getName();
	}

	@Override
	public String getDocumentInfo() {
		return getName();
	}

	@Override
	public File createPDF() {
		return null;
	}

	@Override
	public String getProcessMsg() {
		return m_processMsg;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public int getC_Currency_ID() {
		return MAcctSchema.get(getCtx(), getC_AcctSchema_ID()).getC_Currency_ID();
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}

	public String syncLines(boolean returnEmptyStringIfNoError) {
		String type = getLFR_ODSituationType();

		if (Util.isEmpty(type))
			return "@Error@ @FillMandatory@ @LFR_ODSituationType@";

		int nbPrepaLines = 0;
		
		if (type.contains(MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CCA)) {
			int nbLines = insertLines(MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CCA);
			if (nbLines < 0)
				return "@Error@ lors de l'insertion des lignes CCA";
			nbPrepaLines = nbPrepaLines + nbLines;
		}

		if (type.contains(MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CAP)) {
			int nbLines = insertLines(MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CAP);
			if (nbLines < 0)
				return "@Error@ lors de l'insertion des lignes CCA";
			nbPrepaLines = nbPrepaLines + nbLines;
		}

		// Contrôle différences entre lignes de factures et lignes de préparation d'OD
		int countdiff = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement ("SELECT * FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = ?", get_TrxName());
			rs = pstmt.executeQuery ();
			pstmt.setInt(1, getLFR_ODSituationPrepa_ID());
			while (rs.next ()) {
				MLFRODSituationPrepaLine spl = new MLFRODSituationPrepaLine (getCtx(), rs, get_TrxName());
				MInvoiceLine il = new MInvoiceLine(getCtx(), spl.getC_InvoiceLine_ID(), get_TrxName());
				MFactAcct fa = new MFactAcct(getCtx(), spl.getFact_Acct_ID(), get_TrxName());

				Timestamp ilImputationDateDeb = il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB) == null ? null : (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB);
				Timestamp ilImputationDateFin = il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN) == null ? null : (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN);
				
				if ((fa.getAmtAcctDr().subtract(fa.getAmtAcctCr())).compareTo(spl.getLFR_FactAcct_AmtAcct()) != 0
						|| fa.getAD_Org_ID() != spl.getLFR_FactAcct_Org_ID()
						|| fa.getAccount_ID() != spl.getLFR_FactAcct_Account_ID()
						|| !TimeUtil.isSameDay(ilImputationDateDeb, spl.getLFR_ImputationDateDeb())
						|| !TimeUtil.isSameDay(ilImputationDateFin, spl.getLFR_ImputationDateFin())) {
						
					countdiff++;
					spl.setLFR_IsDiffBetweenFactAcctAndSPL(true);
				}
				else
					spl.setLFR_IsDiffBetweenFactAcctAndSPL(false);

				spl.saveEx();
			}
		}
		catch (Exception e) {
			log.severe("Error while inserting lines " + type + " : " + e);
			return "@Error@ while inserting lines " + type + " : " + e;
		}
		finally {
			DB.close(rs, pstmt);
		}

		return returnEmptyStringIfNoError ? "" : // TODO faire un seul message et l'appeler avec les arguments
			Msg.translate(getCtx(), "XXA_InvoiceLinesSearchOK") + " : " + nbPrepaLines + " " + Msg.translate(getCtx(), "XXA_TransferedLines") + " / " + countdiff + " " + Msg.translate(getCtx(), "XXA_DifferentLines");
	}
	
	private int insertLines(String type) {

		Timestamp dateSituation = getDateAcct();
		int count = 0;

		//	Sélection des lignes de factures avec dates imputation qui encadrent la date de la situation, quelque soit la date de la facture
		String sql = "SELECT i.C_Invoice_ID, il.C_InvoiceLine_ID, dt.DocBaseType, fa.Fact_Acct_ID, ev.Value AS AccountValue, bp.Name AS BPartnerName" // 1, 2, 3
				+ " FROM C_Invoice i, C_InvoiceLine il, C_DocType dt, Fact_Acct fa, C_ElementValue ev, C_BPartner bp"
				+ " WHERE i.C_Invoice_ID = il.C_Invoice_ID"
				+ " AND i.C_DocType_ID = dt.C_DocType_ID"
				+ " AND i.IsSOTrx='N'"
				+ " AND i.DocStatus IN ('CO', 'CL')"
				+ " AND i.AD_Client_ID = " + getAD_Client_ID()
				+ " AND fa.C_AcctSchema_ID = " + getC_AcctSchema_ID()
				+ " AND fa.AD_Table_ID = " + MInvoice.Table_ID
				+ " AND fa.Record_ID = i.C_Invoice_ID AND fa.Line_ID = il.C_InvoiceLine_ID"
				+ " AND fa.Account_ID = ev.C_ElementValue_ID"
				+ " AND fa.C_BPartner_ID = bp.C_BPartner_ID"
				;

		if (!isLFR_IsAllOrgs() && getAD_Org_ID() > 0)
			sql += " AND fa.AD_Org_ID = " + getAD_Org_ID();

		if (type.equals(MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CCA)) { // factures achat avec date facture <= date situation et date fin imputation > date situation 
			sql += " AND TRUNC(i.DateAcct, 'DD') <= " + DB.TO_DATE(dateSituation)
			+ " AND TRUNC(il." + C_INVOICELINE_LFR_IMPUTATIONDATEFIN + ", 'DD') > " + DB.TO_DATE(dateSituation);// FIXME nom colonne sur InvoiceLine -> utiliser des constantes
		}
		else if (type.equals(MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CAP)) { // factures achat avec date facture > date situation et date début imputation < date situation
			sql += " AND TRUNC(i.DateAcct, 'DD') > " + DB.TO_DATE(dateSituation)
			+ " AND TRUNC(il." + C_INVOICELINE_LFR_IMPUTATIONDATEDEB + ", 'DD') < " + DB.TO_DATE(dateSituation);// FIXME nom colonne sur InvoiceLine -> utiliser des constantes
		}

		sql += "AND NOT EXISTS (SELECT * FROM LFR_ODSituationPrepaLine spl"
				+ " WHERE fa.Fact_Acct_ID = spl.Fact_Acct_ID"
				+ " AND spl.LFR_ODSituationPrepa_ID = " + getLFR_ODSituationPrepa_ID() + ")"
				;

		sql += " ORDER BY i.DateAcct, il.Line";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			// TODO gérer les paramètres ici avec une variable idx / tenir compte du typa CCA et/ou CAP 

			while (rs.next()) {
				MInvoice i = new MInvoice(getCtx(), rs.getInt("C_Invoice_ID"), get_TrxName());
				MInvoiceLine il = new MInvoiceLine(getCtx(), rs.getInt("C_InvoiceLine_ID"), get_TrxName());
				Timestamp ilImputDateDeb = (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB);
				Timestamp ilImputDateFin = (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN);
				String dbt = rs.getString("DocBaseType");

				MFactAcct fa = new MFactAcct(getCtx(), rs.getInt("Fact_Acct_ID"), get_TrxName());
				String accountValue = rs.getString("AccountValue"); 
				String bpName = rs.getString("BPartnerName");
				int evID = fa.getAccount_ID();

				MLFRODSituationPrepaLine spl = new MLFRODSituationPrepaLine(getCtx(), 0, get_TrxName());
				spl.setAD_Org_ID(getAD_Org_ID());
				spl.setLFR_ODSituationPrepa_ID(getLFR_ODSituationPrepa_ID());

				// Données de l'entête de la facture (lecture seule)
				spl.setC_Invoice_ID(i.getC_Invoice_ID());
				spl.setIsSOTrx(i.isSOTrx());

				// Données de la ligne de facture (lecture seule)
				spl.setC_InvoiceLine_ID(il.getC_InvoiceLine_ID());
				spl.setLFR_FactAcct_AmtAcct(fa.getAmtAcctDr().subtract(fa.getAmtAcctCr()));
				spl.setDescription(il.getDescription());
				spl.setLFR_ImputationDateDeb(ilImputDateDeb);
				spl.setLFR_ImputationDateFin(ilImputDateFin);

				// Données de la ligne d'écriture (lecture seule)
				spl.setFact_Acct_ID(fa.getFact_Acct_ID());
				spl.setLFR_FactAcct_Org_ID(fa.getAD_Org_ID());
				spl.setC_BPartner_ID(fa.getC_BPartner_ID());
				spl.setDateAcct(fa.getDateAcct());
				spl.setLFR_FactAcct_Account_ID(evID);

				// Données modifiables
				spl.setAD_OrgDoc_ID(fa.getAD_Org_ID());
				spl.setAccount_ID(evID);
				spl.setLineDescription(new StringBuilder(
						i.getDocumentNo()).append(" / ").append(il.getLine()).append(" / ")
						.append(bpName).append(" / ")
						.append((MOrg.get(getCtx(), il.getAD_Org_ID())).getName())
						.append(il.getDescription() == null ? "" : " / " + il.getDescription())
						.toString());

				// Tester le compte comptable ; si < 6 => Cocher case "Compte non éligible"
				if (!accountValue.startsWith("6") && !accountValue.startsWith("7"))
					spl.setLFR_IsCompteNonEligible(true);


				BigDecimal montant = new BigDecimal(0);
				Boolean inverseMtts = false;	// inversion des mtts dans le cas d'un avoir



				//	CCA ; date antérieure à la situation
				if (i.getDateAcct().getTime() <= dateSituation.getTime()) {
					spl.setType(MLFRODSituationPrepaLine.TYPE_CCA);

					//Calcul du montant restant après la date de situation

					if (TimeUtil.max(dateSituation, ilImputDateDeb) == ilImputDateDeb) // date début imputation après date situation, on prend le montant total
						montant = fa.getAmtAcctDr().subtract(fa.getAmtAcctCr());
					else {
						BigDecimal nbjours_ajout = Env.ONE; // on ajoute systématiquement 1 jour à la fonction de Compiere
						BigDecimal nbjours_total = new BigDecimal(TimeUtil.getDaysBetween(ilImputDateDeb, ilImputDateFin)).add(nbjours_ajout);
						BigDecimal nbjours_apres = new BigDecimal(TimeUtil.getDaysBetween(dateSituation, ilImputDateFin)).add(nbjours_ajout);
						montant = (fa.getAmtAcctDr().subtract(fa.getAmtAcctCr()).divide(nbjours_total, i.getPrecision(), RoundingMode.HALF_UP)).multiply(nbjours_apres);	
					}

					// Inversion du montant car CCA avec avoir
					if (dbt.equals(MDocType.DOCBASETYPE_APCreditMemo) || dbt.equals(MDocType.DOCBASETYPE_ARCreditMemo))
						inverseMtts = true;
				} // Fin CCA

				//	CAP ; date supérieure à la situation en option
				else if (i.getDateAcct().getTime() > dateSituation.getTime()) {
					spl.setType(MLFRODSituationPrepaLine.TYPE_CAP);
					spl.setC_Tax_ID(il.getC_Tax_ID());	// calcul du TaxAmt dans le beforeSave

					//Calcul du montant restant après la date de situation
					if (TimeUtil.max(dateSituation, ilImputDateDeb) == ilImputDateDeb) // date début imputation après date situation, on prend le montant total
						montant = fa.getAmtAcctDr().subtract(fa.getAmtAcctCr());
					else if (TimeUtil.max(dateSituation, ilImputDateFin) == dateSituation) { // si date de fin imputation < date de situation, on prend le montant total
						montant = fa.getAmtAcctDr().subtract(fa.getAmtAcctCr());
					}
					else {
						BigDecimal nbjours_ajout = new BigDecimal(1); // on ajoute systématiquement 1 jour à la fonction de Compiere
						BigDecimal nbjours_total = new BigDecimal(TimeUtil.getDaysBetween(ilImputDateDeb, ilImputDateFin)).add(nbjours_ajout);
						BigDecimal nbjours_avant = new BigDecimal(TimeUtil.getDaysBetween(ilImputDateDeb, dateSituation)).add(nbjours_ajout);
						montant = (fa.getAmtAcctDr().subtract(fa.getAmtAcctCr()).multiply(nbjours_avant).divide(nbjours_total, i.getPrecision(), RoundingMode.HALF_UP));
					}

					// Inversion du montant car CAP sur facture
					if (!dbt.equals(MDocType.DOCBASETYPE_APCreditMemo) || !dbt.equals(MDocType.DOCBASETYPE_ARCreditMemo))
						inverseMtts = true;
				} // Fin CAP

				if (inverseMtts) {
					spl.setLFR_FactAcct_AmtAcct(spl.getLFR_FactAcct_AmtAcct().negate());
					montant = montant.negate();
				}

				spl.setAmtAcct(montant);
				spl.setAmt(montant);

				if (dbt.equals(MDocType.DOCBASETYPE_APCreditMemo) || dbt.equals(MDocType.DOCBASETYPE_ARCreditMemo))
					spl.setLFR_IsCreditMemo(true);	// uniquement pour les avoirs

				spl.setTaxAmtForCAP(i);

				spl.saveEx();
				count++;
			}
		}
		catch (SQLException e) {
			log.severe("Error while inserting lines " + type + " : " + e);
			return -1;
		}
		finally {
			DB.close(rs, pstmt);
		}

		return count;
	}
	
	private String generate() {

		MAcctSchema as = MAcctSchema.get(getCtx(), getC_AcctSchema_ID());
		MAcctSchemaGL asGL = as.getAcctSchemaGL();
		MJournalBatch jb = null;
		MJournal jCCA = null;
		MJournal jCAP = null;

		if (isLFR_IsGroupInJournalBatch()) {

			jb = new MJournalBatch(getCtx(), getGL_JournalBatch_ID(), get_TrxName());

			if (getGL_JournalBatch_ID() == 0) {
				jb.setAD_Org_ID(getAD_OrgDoc_ID());
				jb.setC_DocType_ID(getC_DocType_ID());
				jb.setGL_Category_ID(MDocType.get(getCtx(), getC_DocType_ID()).getGL_Category_ID());
				jb.setPostingType(MJournalBatch.POSTINGTYPE_Actual);
				jb.setDescription("Situation " + getDateAcct());
				jb.setDateAcct(getDateAcct());
				jb.setDateDoc(getDateAcct());
				jb.setC_Currency_ID(as.getC_Currency_ID());
				jb.saveEx();
			}
			else {
				jb.setDocStatus(MJournal.DOCSTATUS_Completed);
				jb.setDocAction(MJournal.DOCACTION_Re_Activate);
				jb.processIt(MJournal.ACTION_ReActivate);
				jb.saveEx();
			}
		}

		// CCA
		MLFRODSituationPrepaLine[] lines = getActiveLines("Type = " + DB.TO_STRING(MLFRODSituationPrepaLine.TYPE_CCA) + " AND LFR_IsCompteNonEligible = 'N'");

		if (lines.length > 0) {

			if (isLFR_IsGroupInJournalBatch()) {
				if (getLFR_JournalCCA_ID() > 0) {
					for (MJournal j : jb.getJournals(false)) {
						if (j.getGL_Journal_ID() == getLFR_JournalCCA_ID()) {
							jCCA = j;
							String err = reopenJournalAndDeleteLines(jCCA);
							if (!Util.isEmpty(err))
								return "@Error@" + err;
							break;
						}
					}
				}
				else
					jCCA = new MJournal(jb);
			}
			else {
				jCCA = new MJournal(getCtx(), getLFR_JournalCCA_ID(), get_TrxName());
				if (getLFR_JournalCCA_ID() == 0)
					createJournal(jCCA, jb, as);
				else {
					if (jCCA.isComplete()) {
						String err = reopenJournalAndDeleteLines(jCCA);
						if (!Util.isEmpty(err))
							return "@Error@" + err;
					}
				}
			}

			jCCA.setDescription(MRefList.getListName(getCtx(), MColumn.get(getCtx(), MLFRODSituationPrepa.Table_Name, MLFRODSituationPrepa.COLUMNNAME_LFR_ODSituationType).getAD_Reference_Value_ID(), MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CCA));
			jCCA.saveEx();

			for (int i = 0; i < lines.length; i++) { // Création des lignes d'OD correspondants aux lignes de préparation
				MLFRODSituationPrepaLine fromLine = lines[i];
				MJournalLine line = new MJournalLine (jCCA);
				updateJournalLine(line, fromLine, as.getC_AcctSchema_ID());
				line.saveEx();
			}

			// écriture équilibre
			MJournalLine line = new MJournalLine (jCCA);
			line.setC_ValidCombination_ID(asGL.get_ValueAsInt("XXA_ODSituationPrepaCCA_Acct")); // TODO utiliser des constantes et pas de combinaison

			String sql = "SELECT SUM(AmtSourceDR) - SUM(AmtSourceCr) FROM GL_JournalLine WHERE GL_Journal_ID = ?";
			BigDecimal amt = DB.getSQLValueBDEx(get_TrxName(), sql, jCCA.getGL_Journal_ID());

			if (amt.signum() > 0) {
				line.setAmtAcctCr(amt);
				line.setAmtSourceCr(amt);
			} else {
				line.setAmtAcctDr(amt.negate());
				line.setAmtSourceDr(amt.negate());
			}

			line.setAD_Org_ID(jCCA.getAD_Org_ID());
			line.setProcessed(false);
			line.saveEx();
		}

		// CAP
		lines = getActiveLines("Type = " + DB.TO_STRING(MLFRODSituationPrepaLine.TYPE_CAP) + " AND LFR_IsCompteNonEligible = 'N'");

		if (lines.length > 0) {
			if (isLFR_IsGroupInJournalBatch()) {
				if (getLFR_JournalCAP_ID() > 0) {
					for (MJournal j : jb.getJournals(false)) {
						if (j.getGL_Journal_ID() == getLFR_JournalCAP_ID()) {
							jCAP = j;
							String err = reopenJournalAndDeleteLines(jCAP);
							if (!Util.isEmpty(err))
								return "@Error@" + err;
							break;
						}
					}
				}
				else
					jCAP = new MJournal(jb);
			}
			else {
				jCAP = new MJournal(getCtx(), getLFR_JournalCAP_ID(), get_TrxName());
				if (getLFR_JournalCAP_ID() == 0)
					createJournal(jCAP, jb, as);
				else {
					if (jCAP.isComplete()) {
						String err = reopenJournalAndDeleteLines(jCAP);
						if (!Util.isEmpty(err))
							return "@Error@" + err;
					}
				}
			}

			jCAP.setDescription(MRefList.getListName(getCtx(), MColumn.get(getCtx(), MLFRODSituationPrepa.Table_Name, MLFRODSituationPrepa.COLUMNNAME_LFR_ODSituationType).getAD_Reference_Value_ID(), MLFRODSituationPrepa.LFR_ODSITUATIONTYPE_CAP));
			jCAP.saveEx();

			for (int i = 0; i < lines.length; i++) { // pour une ligne CAP, 3 lignes d'OD : HT, TVA, frs
				MLFRODSituationPrepaLine fromLine = lines[i];
				//				if (fromLine.getType().equals(X_XXA_ODSituationPrepaLine.TYPE_CAP)) {
				//	HT
				MJournalLine line = new MJournalLine (jCAP);
				updateJournalLine(line, fromLine, as.getC_AcctSchema_ID());
				line.saveEx();

				//	TVA
				MJournalLine lineTVA = new MJournalLine (jCAP);
				lineTVA.setAD_Org_ID(fromLine.getAD_OrgDoc_ID());
				lineTVA.setDescription(fromLine.getLineDescription());

				//	recupérer le account_id de la combinaison XXA_ODSituationPrepaTCAP_Acct
				MAccount combi = new MAccount(getCtx(), asGL.get_ValueAsInt("XXA_ODSituationPrepaTCAP_Acct"), get_TrxName()); // TODO utiliser des constantes et pas de combinaison

				lineTVA.setAD_Org_ID(fromLine.getAD_OrgDoc_ID());
				lineTVA.setAccount_ID(combi.getAccount_ID());
				lineTVA.setC_BPartner_ID(line.getC_BPartner_ID());

				//	Frs
				MJournalLine lineFrs = new MJournalLine (jCAP);
				lineFrs.setAD_Org_ID(fromLine.getAD_OrgDoc_ID());
				lineFrs.setDescription(fromLine.getLineDescription());

				//	recupérer le account_id de la combinaison XXA_ODSituationPrepaTCAP_Acct
				combi = new MAccount(getCtx(), asGL.get_ValueAsInt("XXA_ODSituationPrepaCAP_Acct"), get_TrxName()); // TODO utiliser des constantes et pas de combinaison

				lineFrs.setAD_Org_ID(fromLine.getAD_OrgDoc_ID());
				lineFrs.setAccount_ID(combi.getAccount_ID());
				lineFrs.setC_BPartner_ID(line.getC_BPartner_ID());

				//	Montants TVA & Frs
				BigDecimal mtt = (fromLine.getAmtAcct().add(fromLine.getTaxAmt())).negate();
				if (line.getAmtSourceDr().compareTo(Env.ZERO) != 0) {
					lineTVA.setAmtAcctDr(fromLine.getTaxAmt().negate());
					lineTVA.setAmtSourceDr(fromLine.getTaxAmt().negate());
					lineFrs.setAmtAcctCr(mtt);
					lineFrs.setAmtSourceCr(mtt);
				} else {
					lineTVA.setAmtAcctCr(fromLine.getTaxAmt().negate());
					lineTVA.setAmtSourceCr(fromLine.getTaxAmt().negate());
					lineFrs.setAmtAcctDr(mtt);
					lineFrs.setAmtSourceDr(mtt);
				}
				lineTVA.saveEx();
				lineFrs.saveEx();
				//				}
			}			
		}

		if (isLFR_IsGroupInJournalBatch()) {
			jb.processIt(MJournal.ACTION_Complete);
			jb.saveEx();
		}
		else {
			if (jCCA != null) {
				jCCA.processIt(MJournal.ACTION_Complete);
				jCCA.saveEx();
			}
			if (jCAP != null) {
				jCAP.processIt(MJournal.ACTION_Complete);
				jCAP.saveEx();
			}
		}

		if (isLFR_IsGroupInJournalBatch())
			setGL_JournalBatch_ID(jb.getGL_JournalBatch_ID());

		setLFR_JournalCAP_ID(jCAP != null ? jCAP.getGL_Journal_ID() : -1);
		setLFR_JournalCCA_ID(jCCA != null ? jCCA.getGL_Journal_ID() : -1);
		return "";
	}

	private void updateJournalLine(MJournalLine line, MLFRODSituationPrepaLine fromLine, int as_id) {
		line.setAD_Org_ID(fromLine.getAD_OrgDoc_ID());
		line.setAccount_ID(fromLine.getAccount_ID());
		line.setDescription(fromLine.getLineDescription());
		if (fromLine.getAmtAcct().compareTo(Env.ZERO) <= 0) {
			line.setAmtAcctDr(fromLine.getAmtAcct().negate());
			line.setAmtSourceDr(fromLine.getAmtAcct().negate());
		}
		if (fromLine.getAmtAcct().compareTo(Env.ZERO) >= 0) {
			line.setAmtAcctCr(fromLine.getAmtAcct());
			line.setAmtSourceCr(fromLine.getAmtAcct());
		}
		line.setProcessed(false);
	}

	private void createJournal(MJournal journal, MJournalBatch jb, MAcctSchema as) {
		journal.setClientOrg(getAD_Client_ID(), getAD_OrgDoc_ID());
		journal.setC_DocType_ID(getC_DocType_ID());
		journal.setPostingType(MFactAcct.POSTINGTYPE_Actual);
		journal.setDateDoc(getDateAcct());
		journal.setDateAcct(getDateAcct());
		journal.setC_AcctSchema_ID(as.getC_AcctSchema_ID());
		journal.setC_ConversionType_ID(MConversionType.getDefault(getAD_Client_ID()));
		journal.setC_Currency_ID(as.getC_Currency_ID());
	}

	private String reopenJournalAndDeleteLines(MJournal j) {

		if (!MPeriod.isOpen(getCtx(), j.getDateAcct(), MDocType.DOCBASETYPE_GLJournal, j.getAD_Org_ID()))
			return "@Error@ : @PeriodClosed@ @Date@=" + j.getDateAcct() +", @DocBaseType@=" + MRefList.getListName(Env.getCtx(), MDocType.DOCBASETYPE_AD_Reference_ID, MDocType.DOCBASETYPE_GLJournal);

		j.setDocStatus(MJournal.DOCSTATUS_Completed);
		j.setDocAction(MJournal.DOCACTION_Re_Activate);
		j.processIt(MJournal.ACTION_ReActivate);
		j.saveEx();
		DB.executeUpdateEx("DELETE FROM GL_JournalLine WHERE GL_Journal_ID = ?", new Object[] {j.getGL_Journal_ID()}, get_TrxName());

		return "";
	}

}
