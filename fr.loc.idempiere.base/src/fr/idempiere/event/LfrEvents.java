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

package fr.idempiere.event;

import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEDEB;
import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEFIN;
import static fr.idempiere.model.SystemIDs_LFR.LFR_IN_USE;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MBank;
import org.compiere.model.MBankAccount;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLocation;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.osgi.service.event.Event;

import fr.idempiere.model.MLFRPaySelectionPrepayment;
import fr.idempiere.util.LfrFactReconciliationUtil;
import fr.idempiere.util.LfrUtil;
import fr.idempiere.util.SEPAPaymentExport;


public class LfrEvents extends AbstractEventHandler {

	@Override
	protected void initialize() {
		registerEvent(IEventTopics.AFTER_LOGIN);

		registerTableEvent(IEventTopics.PO_BEFORE_NEW, MInvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MInvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_NEW, MPaySelectionLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, MPaySelectionLine.Table_Name);
		registerTableEvent(IEventTopics.PO_AFTER_DELETE, MPaySelectionLine.Table_Name);
		
		registerTableEvent(IEventTopics.DOC_BEFORE_REACTIVATE, MPayment.Table_Name);

		registerTableEvent(IEventTopics.DOC_AFTER_POST, MInvoice.Table_Name);
		registerTableEvent(IEventTopics.DOC_AFTER_POST, MPayment.Table_Name);
		registerTableEvent(IEventTopics.DOC_AFTER_POST, MAllocationHdr.Table_Name);
		
		registerProcessEvent(IEventTopics.AFTER_PROCESS, "org.compiere.process.PaySelectionCreateFrom");

	}

	@Override
	protected void doHandleEvent(Event event) {

		String topic = event.getTopic();

		if (topic.equals(IEventTopics.AFTER_LOGIN)) {
			LoginEventData loginData = (LoginEventData) event.getProperty(IEventManager.EVENT_DATA);
			boolean useLfr = MSysConfig.getBooleanValue(LFR_IN_USE, false, loginData.getAD_Client_ID());
			Env.setContext(Env.getCtx(), "#LFR", useLfr);
			return;
		}

		if (event.getProperty(IEventManager.EVENT_DATA) instanceof PO) {
			PO po = getPO(event);

			if (topic.equals(IEventTopics.DOC_AFTER_POST) && LfrFactReconciliationUtil.isUseImmediateReconciliation(po.getAD_Client_ID())) {
				if (po.get_TableName().equals(MInvoice.Table_Name)) {
					LfrFactReconciliationUtil.factReconcile(po);
				}
				else if (po.get_TableName().equals(MPayment.Table_Name)) {
					LfrFactReconciliationUtil.factReconcile(po);
				}
				else if (po.get_TableName().equals(MAllocationHdr.Table_Name)) {

					MAllocationHdr alloc = (MAllocationHdr) po;
					MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(po.getCtx(), po.getAD_Client_ID());
					for (MAcctSchema as : ass) {
						LfrFactReconciliationUtil.lettrageAlloc(po.getCtx(), alloc, as.getC_AcctSchema_ID(), -1, po.get_TrxName());
					}
				}
			}
			else if (po.get_TableName().equals(MInvoiceLine.Table_Name)) {

				MInvoiceLine il = (MInvoiceLine) po;

				if (il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB) != null && il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN) != null) {
					Timestamp deb = (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB);
					Timestamp fin = (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN);

					String err = LfrUtil.checkInvoiceLineImputation(il.getCtx(), deb, fin);
					if (!Util.isEmpty(err))
						throw new AdempiereException(err);
				}
			} // Fin lettrage

			if (po.get_TableName().equals(MPayment.Table_Name)) {

				MPayment p = (MPayment) po;

				if (topic.equals(IEventTopics.DOC_BEFORE_REACTIVATE)) {
					if (MLFRPaySelectionPrepayment.Table_ID > 0) {
						if (DB.getSQLValueEx(po.get_TrxName(), "SELECT 1 FROM LFR_PaySelectionPrepayment WHERE C_Payment_ID = ?", p.getC_Payment_ID()) == 1)
							throw new AdempiereUserError("Impossible de réactiver le paiement, il est utilisé sur une préparation de virement");
					}
				}
			}
			else if (po.get_TableName().equals(MPaySelectionLine.Table_Name)) {
				MLFRPaySelectionPrepayment.updatePaySelectionTotalAmt(po.get_ValueAsInt("C_PaySelection_ID"), po.get_TrxName());
			}
		}
		else if (event.getProperty(IEventManager.EVENT_DATA) instanceof ProcessInfo) {

			String type = event.getTopic();
			ProcessInfo pi = getProcessInfo(event);
			String trxName = pi.getTransactionName();

			if (pi.getClassName().equals("org.compiere.process.PaySelectionCreateFrom") && type.equals(IEventTopics.AFTER_PROCESS)) {

				MPaySelection ps = new MPaySelection (Env.getCtx(), pi.getRecord_ID(), trxName);

				ArrayList<Integer> listBPartners = new ArrayList<Integer>();

				for (MPaySelectionLine psl : ps.getLines(true)) { // need to reload as some lines could have been removed previously

					int bpartnerID = DB.getSQLValueEx(trxName, "SELECT C_BPartner_ID FROM C_Invoice i WHERE C_Invoice_ID = ?", psl.getC_Invoice_ID());

					if (!listBPartners.contains(bpartnerID)) {

						MBPartner bp = MBPartner.get(Env.getCtx(), bpartnerID);
						MBPBankAccount[] bas = bp.getBankAccounts(false);
						if (bas == null || bas.length == 0)
							pi.addLog(0, null, null, bp.getValue() + " - " + bp.getName() + " : " + "@NotFound@ @C_BP_BankAccount_ID@");
						else {
							String paymentExportClass = MBankAccount.get(ps.getC_BankAccount_ID()).getPaymentExportClass();

							if (!Util.isEmpty(paymentExportClass) && paymentExportClass.equals(SEPAPaymentExport.class.getCanonicalName())) {

								ArrayList<MBPBankAccount> listdirectDepositBankAccounts = new ArrayList<MBPBankAccount>();

								for (MBPBankAccount bpba : bas) {
									if (bpba.getBPBankAcctUse().equals(MBPBankAccount.BPBANKACCTUSE_DirectDeposit))
										listdirectDepositBankAccounts.add(bpba);									
								}

								if (listdirectDepositBankAccounts.size() == 0)
									pi.addLog(0, null, null, bp.getValue() + " - " + bp.getName() + " : " + "@NotFound@ @C_BP_BankAccount_ID@ virement avec IBAN");
								else {
									for (MBPBankAccount bpba : listdirectDepositBankAccounts) {
										if (Util.isEmpty(bpba.getIBAN()))
											pi.addLog(0, null, null, bp.getValue() + " - " + bp.getName() + " : " + "@NotFound@ @IBAN@");
										else {

											if (bpba.getC_Bank_ID() <= 0)
												pi.addLog(0, null, null, "Attention : pas de banque pour " + bp.getName());
											else {
												MBank bank = MBank.get(bpba.getC_Bank_ID());
												if (Util.isEmpty(bank.getSwiftCode()))
													pi.addLog(0, null, null, "Attention : pas de @SwiftCode@ pour pour " + bp.getName());	
											}

											int locID = DB.getSQLValueEx(trxName, "SELECT bpl.C_Location_ID FROM C_BPartner_Location bpl, C_Invoice i WHERE i.C_BPartner_Location_ID = bpl.C_BPartner_Location_ID AND i.C_Invoice_ID = ?", psl.getC_Invoice_ID());
											MLocation loc = new MLocation(Env.getCtx(), locID, null);
											String countryCode = loc.getCountry().getCountryCode();

											if (!countryCode.equals(bpba.getIBAN().substring(0, 2)))
												pi.addLog(0, null, null, "Attention : Le code pays est '" + countryCode + "' alors que l'IBAN commence avec '" + bpba.getIBAN().substring(0, 2) + "' pour " + bp.getName());
											if (Util.isEmpty(loc.getAddress1()))
												pi.addLog(0, null, null, "Attention : pas d'adresse 1 pour " + bp.getName());
											if (Util.isEmpty(loc.getPostal()))
												pi.addLog(0, null, null, "Attention : pas de code postal pour " + bp.getName());
											if (Util.isEmpty(loc.getCity()))
												pi.addLog(0, null, null, "Attention : pas de ville pour " + bp.getName());	
										}
									}

									if (listdirectDepositBankAccounts.size() > 1) {
										pi.addLog(0, null, null, "Attention : plusieurs IBAN pour " + bp.getName() + " :");
										int noDefault = 0;
										for (MBPBankAccount bpba : listdirectDepositBankAccounts) {

											boolean isDefault = bpba.get_ValueAsBoolean("LFR_IsDefault");
											String comments = bpba.get_ValueAsString("LFR_Comments");

											StringBuilder log = new StringBuilder(" - ").append(bpba.getIBAN());
											if (!Util.isEmpty(comments))
												log.append(" : ").append(comments);
											if (isDefault) {
												log.append(" <-- PAR DEFAUT");
												noDefault++;
											}

											pi.addLog(0, null, null, log.toString());
										}
										if (noDefault == 0)
											pi.addLog(0, null, null, "Aucun IBAN par défaut");
										else if (noDefault > 1)
											pi.addLog(0, null, null, "Plusieurs IBAN par défaut");
									}
								}
							}
						}

						// Message si présence d'un règlement ou d'un avoir non affecté
						if (DB.getSQLValueEx(trxName, "SELECT 1 FROM C_Payment WHERE C_BPartner_ID = ? AND IsAllocated = 'N' AND DocStatus IN ('CO', 'CL')", bp.getC_BPartner_ID()) == 1)
							pi.addLog(0, null, null, bp.getName() + " a au moins un règlement non affecté");
						if (DB.getSQLValueEx(trxName, "SELECT 1 FROM C_Invoice i WHERE i.C_BPartner_ID = ? AND i.IsPaid = 'N' AND i.DocStatus IN ('CO', 'CL')"
								+ " AND i.C_DocType_ID IN (SELECT dt.C_DocType_ID FROM C_DocType dt WHERE dt.AD_Client_ID = i.AD_Client_ID AND charat(DocBaseType, 3) = 'C')", bp.getC_BPartner_ID()) == 1)
							pi.addLog(0, null, null, bp.getName() + " a au moins un avoir non affecté");

						listBPartners.add(bp.getC_BPartner_ID());
					}
				}
			}
		}
	}
}
