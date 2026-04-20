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

package fr.idempiere.util;

import static fr.idempiere.model.SystemIDs_LFR.C_BP_BANKACCOUNT_LFR_ISDEFAULT;
import static fr.idempiere.model.SystemIDs_LFR.LFR_PAYSELECTION_SEPA_DEBTOR_NAME;
import static fr.idempiere.model.SystemIDs_LFR.LFR_PAYSELECTION_SEPA_ENDTOENDID_TEMPLATE;
import static fr.idempiere.model.SystemIDs_LFR.LFR_PAYSELECTION_SEPA_INITIATOR_NAME;
import static fr.idempiere.model.SystemIDs_LFR.LFR_PAYSELECTION_SEPA_REMITTANCE_PREFIX;
import static fr.idempiere.model.SystemIDs_LFR.LFR_PAYSELECTION_SEPA_REMITTANCE_TEMPLATE;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.compiere.model.I_C_BankAccount;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocation;
import org.compiere.model.MOrg;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.IBAN;
import org.compiere.util.PaymentExport;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.idempiere.model.MLFRPaySelectionPrepayment;

/**
 * SEPA Payment Export based on generic export example
 * 
 * @author integratio/pb
 * @author mbozem@bozem.de
 * 
 * modified by Diego Ruiz - Bx Service GmbH
 */
public class SEPAPaymentExport implements PaymentExport {
	/** Logger */
	static private CLogger s_log = CLogger.getCLogger(SEPAPaymentExport.class);

	//Main xml elements
	private static final String PAYMENT_INFO_ELEMENT = "PmtInf"; 
	private static final String SECOND_SCT_ELEMENT   = "CstmrCdtTrfInitn"; 
	private static final String ROOT_ELEMENT         = "Document";
	
	//SEPA file type
	private static final String SEPA_CREDIT_TRANSFER = "pain.001.001.03"; //Use for payments

	private boolean directDebit = false;
	private String documentType;
	private boolean m_isVirtInstantane = false;

	/**************************************************************************
	 * Export to File
	 * 
	 * @param checks
	 *            array of checks
	 * @param file
	 *            file to export checks
	 * @return number of lines
	 */
	@Override
	public int exportToFile(MPaySelectionCheck[] checks, boolean collectiveBooking, String paymentRule, File file, StringBuffer err) {

		setDocumentType(paymentRule);
		if (documentType == null) {
			s_log.log(Level.SEVERE, "Payment Rule not supported");
			return -1;
		}
		
		int noLines = checks.length;
		try {
			int paySelectionID = checks[0].getC_PaySelection_ID();
			MLFRPaySelectionPrepayment[] psps = MLFRPaySelectionPrepayment.get(paySelectionID, null);

			File xmlFile = generateCreditTransferFile(paySelectionID, checks, psps, err);
			if (file.exists())
				file.delete();

			xmlFile.renameTo(file);

			//noLines = numberOfTransactions;
		} catch (Exception e) {
				err.append(" (type d'erreur: ").append(e.toString()).append(")");
				err.insert(0, " : ");
				s_log.log(Level.SEVERE, "", e);
				return -1;
		}

		return noLines;
	} // exportToFile
	
	private File generateCreditTransferFile(int paySelectionID, MPaySelectionCheck[] checks, MLFRPaySelectionPrepayment[] psps, StringBuffer err) throws Exception {
		
		String creationFileDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) ;
		File xmlFile = File.createTempFile("SEPA-Credit-Transfer-" + creationFileDate, ".xml");
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document document = builder.newDocument();

		MClient client = MClient.get(Env.getCtx());

		String msgId;
		String creationDate;
		int numberOfTransactions = 0;
		String initiatorName;
		BigDecimal ctrlSum = BigDecimal.ZERO;

		if (checks[0].getPayAmt() != null) {
			for (int i = 0; i < checks.length; i++) {
				MPaySelectionCheck mpp = checks[i];
				ctrlSum = ctrlSum.add(mpp.getPayAmt());
				numberOfTransactions++;
			}	
		}

		for (MLFRPaySelectionPrepayment psp : psps) {
			ctrlSum = ctrlSum.add(psp.getPayAmt());
			numberOfTransactions++;
		}

		MPaySelection firstPaySelection = new MPaySelection(Env.getCtx(), paySelectionID, null);

		m_isVirtInstantane = MSysConfig.getBooleanValue("XXA_PAYSELECTION_SEPA_ALLOW_VIRT_INST", false, firstPaySelection.getAD_Client_ID())
				&& TimeUtil.isSameDay(firstPaySelection.getPayDate(), new Timestamp(TimeUtil.getToday().getTimeInMillis()));

		initiatorName = MSysConfig.getValue(LFR_PAYSELECTION_SEPA_INITIATOR_NAME, "", client.getAD_Client_ID(), firstPaySelection.getAD_Org_ID());

		if (Util.isEmpty(initiatorName)) {
			if (firstPaySelection.getAD_Org_ID() != 0)
				initiatorName = MOrg.get(Env.getCtx(), firstPaySelection.getAD_Org_ID()).getName();
			else
				initiatorName = client.getName();
		}

		int psCurrencyID = MBankAccount.get(Env.getCtx(), firstPaySelection.getC_BankAccount_ID()).getC_Currency_ID();
		
		msgId = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firstPaySelection.getCreated());

		creationDate = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()) + "T"
				+ new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + ".000Z";

		String paymentInfoId = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())+ "/TRF";

		
		//Header
		Element root = document.createElement(ROOT_ELEMENT);
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
		root.setAttribute("xmlns", "urn:iso:std:iso:20022:tech:xsd:" + documentType);
		root.setAttribute("xsi:schemaLocation", "urn:iso:std:iso:20022:tech:xsd:" + documentType + " " + documentType + ".xsd");

		//begin of second level node
		Element secondNodeInitnElement = document.createElement(SECOND_SCT_ELEMENT);

		//Group header element same for both cases
		Element GrpHdrElement = document.createElement("GrpHdr");
		GrpHdrElement.appendChild(document.createElement("MsgId")).setTextContent(iSEPA_ConvertSign(msgId, 35));
		GrpHdrElement.appendChild(document.createElement("CreDtTm")).setTextContent(iSEPA_ConvertSign(creationDate));
		GrpHdrElement.appendChild(document.createElement("NbOfTxs")).setTextContent(String.valueOf(numberOfTransactions));
		GrpHdrElement.appendChild(document.createElement("InitgPty")).appendChild(document.createElement("Nm")).setTextContent(iSEPA_ConvertSign(initiatorName, 70));
		secondNodeInitnElement.appendChild(GrpHdrElement);
		
		//Begin of PmtInf
		Element paymentInfoElement = document.createElement(PAYMENT_INFO_ELEMENT);

		paymentInfoElement.appendChild(document.createElement("PmtInfId"))
		.setTextContent(iSEPA_ConvertSign(paymentInfoId, 35));
		paymentInfoElement.appendChild(document.createElement("PmtMtd")).setTextContent("TRF");
		paymentInfoElement.appendChild(document.createElement("BtchBookg")).setTextContent("true");
		paymentInfoElement.appendChild(document.createElement("NbOfTxs"))
		.setTextContent(String.valueOf(numberOfTransactions));
		paymentInfoElement.appendChild(document.createElement("CtrlSum")).setTextContent(String.valueOf(ctrlSum));
		
		Element PmtTpInfElement = document.createElement("PmtTpInf");
		PmtTpInfElement.appendChild(document.createElement("SvcLvl"))
					.appendChild(document.createElement("Cd")).setTextContent("SEPA");

		if (m_isVirtInstantane)
			PmtTpInfElement.appendChild(document.createElement("LclInstrm")).appendChild(document.createElement("Cd")).setTextContent("INST");
		
		I_C_BankAccount bankAccount = firstPaySelection.getC_BankAccount();
		
		String executionDate = new SimpleDateFormat("yyyy-MM-dd").format(firstPaySelection.getPayDate());
		String dbtr_Name = MSysConfig.getValue(LFR_PAYSELECTION_SEPA_DEBTOR_NAME, MOrg.get(Env.getCtx(), firstPaySelection.getAD_Org_ID()).getName(), firstPaySelection.getAD_Client_ID());
		String dbtrAcct_IBAN = IBAN.normalizeIBAN(bankAccount.getIBAN());
		String dbtrAcct_BIC = bankAccount.getC_Bank().getSwiftCode();

		if (!IBAN.isValid(dbtrAcct_IBAN)) {
			err.append("IBAN " + dbtrAcct_IBAN + " is not valid.");
			throw new Exception();
		}

		if (!Util.isEmpty(dbtrAcct_BIC) && dbtrAcct_BIC.length() > 11) {
			err.append("BIC/SWIFTCode " + dbtrAcct_BIC + " is not valid.");
			throw new Exception();
		}
		
		paymentInfoElement.appendChild(PmtTpInfElement);
		paymentInfoElement.appendChild(document.createElement("ReqdExctnDt"))
					.setTextContent(iSEPA_ConvertSign(executionDate));
		paymentInfoElement.appendChild(document.createElement("Dbtr")).appendChild(document.createElement("Nm"))
					.setTextContent(iSEPA_ConvertSign(dbtr_Name, 70));
		paymentInfoElement.appendChild(document.createElement("DbtrAcct")).appendChild(document.createElement("Id"))
					.appendChild(document.createElement("IBAN")).setTextContent(dbtrAcct_IBAN);
		paymentInfoElement.appendChild(document.createElement("DbtrAgt"))
					.appendChild(document.createElement("FinInstnId")).appendChild(document.createElement("BIC"))
					.setTextContent(iSEPA_ConvertSign(dbtrAcct_BIC));
		paymentInfoElement.appendChild(document.createElement("ChrgBr")).setTextContent("SLEV");

		for (MPaySelectionCheck check : checks) {
			if (check == null || check.getC_PaySelectionCheck_ID() == 0)
				continue;

			paymentInfoElement.appendChild(getCreditTransferTrxInfo(new GenericPayment(check), psCurrencyID, document, err));
		}

		for (MLFRPaySelectionPrepayment psp : psps) {
			paymentInfoElement.appendChild(getCreditTransferTrxInfo(new GenericPayment(psp), psCurrencyID, document, err));
		}
		
		secondNodeInitnElement.appendChild(paymentInfoElement);
		root.appendChild(secondNodeInitnElement);
		document.appendChild(root);
		
		convertToXMLFile(document, xmlFile);
		
		return xmlFile;
	}
	
	private Element getCreditTransferTrxInfo(GenericPayment gp, int psCurrencyID, Document document, StringBuffer err) throws Exception {

		String pmtId = gp.getPmtId();
		String creditorName;
		String CdtrAcct_BIC;
		String CdtrAcct_IBAN;
		String unverifiedReferenceLine;

		unverifiedReferenceLine = gp.getUstrd();

		BigDecimal payAmt = gp.getPayAmt();

		MBPartner bPartner = MBPartner.get(Env.getCtx(), gp.getBPartnerID());

		MBPBankAccount bpBankAccount = null;

		if (gp.getBPBankAccountID() > 0) {
			bpBankAccount = new MBPBankAccount(Env.getCtx(), gp.getBPBankAccountID(), null);
		}
		else {
			MBPBankAccount[] bpBankAccounts = bPartner.getBankAccounts(true);
			ArrayList<MBPBankAccount> listdirectDepositBankAccounts = new ArrayList<MBPBankAccount>();

			for (MBPBankAccount bpba : bpBankAccounts) {
				if (bpba.getBPBankAcctUse().equals(MBPBankAccount.BPBANKACCTUSE_DirectDeposit))
					listdirectDepositBankAccounts.add(bpba);
			}

			if (listdirectDepositBankAccounts.size() == 0) {
				err.append(bPartner.getName() + " n'a pas de compte bancaire valide");
				throw new Exception();
			}
			else if (listdirectDepositBankAccounts.size() == 1)
				bpBankAccount = listdirectDepositBankAccounts.get(0);
			else {
				boolean hasDefault = false;

				for (MBPBankAccount bpba : listdirectDepositBankAccounts) {
					if (hasDefault && bpba.get_ValueAsBoolean(C_BP_BANKACCOUNT_LFR_ISDEFAULT)) {
						err.append(bPartner.getName() + " a plusieurs IBAN par défaut");
						throw new Exception();
					}

					if (bpba.get_ValueAsBoolean(C_BP_BANKACCOUNT_LFR_ISDEFAULT)) {
						hasDefault = true;
						bpBankAccount = bpba;
					}
				}

				if (bpBankAccount == null) {
					err.append(bPartner.getName() + " n'a pas de compte bancaire pour virement défini par défaut");
					throw new Exception();
				}
			}
		}

		CdtrAcct_IBAN = IBAN.normalizeIBAN(bpBankAccount.getIBAN());
		CdtrAcct_BIC = bpBankAccount.getSwiftCode();
		creditorName = bpBankAccount.getA_Name();

		if (!IBAN.isValid(CdtrAcct_IBAN)) {
			err.append("IBAN " + CdtrAcct_IBAN + " is not valid. Creditor: " + creditorName);
			throw new Exception();
		}

		MLocation loc = new MLocation(Env.getCtx(), gp.getLocationID(), null);

		if (Util.isEmpty(loc.getPostal())) {
			err.append("No postal code for " + creditorName);
			throw new Exception();
		}

		String countryCode = loc.getCountry().getCountryCode();

		Element CdtTrfTxInfElement = document.createElement("CdtTrfTxInf");

		CdtTrfTxInfElement.appendChild(document.createElement("PmtId"))
				.appendChild(document.createElement("EndToEndId")).setTextContent(iSEPA_ConvertSign(pmtId, 35));

		Element InstdAmtElement = document.createElement("InstdAmt");
		InstdAmtElement.setAttribute("Ccy",
				MCurrency.getISO_Code(Env.getCtx(), psCurrencyID));
		InstdAmtElement.setTextContent(String.valueOf(payAmt));

		CdtTrfTxInfElement.appendChild(document.createElement("Amt")).appendChild(InstdAmtElement);
		CdtTrfTxInfElement.appendChild(document.createElement("CdtrAgt"))
					.appendChild(document.createElement("FinInstnId")).appendChild(document.createElement("BIC"))
					.setTextContent(iSEPA_ConvertSign(CdtrAcct_BIC));

		Element Cdtr = document.createElement("Cdtr");
		CdtTrfTxInfElement.appendChild(Cdtr);
		Cdtr.appendChild(document.createElement("Nm")).setTextContent(iSEPA_ConvertSign(creditorName, 70));

		Element pstlAdr = document.createElement("PstlAdr");
		Cdtr.appendChild(pstlAdr);

		pstlAdr.appendChild(document.createElement("StrtNm")).setTextContent(iSEPA_ConvertSign(loc.getAddress1()));
		pstlAdr.appendChild(document.createElement("PstCd")).setTextContent(iSEPA_ConvertSign(loc.getPostal()));
		pstlAdr.appendChild(document.createElement("TwnNm")).setTextContent(iSEPA_ConvertSign(loc.getCity()));
		pstlAdr.appendChild(document.createElement("Ctry")).setTextContent(countryCode);

		CdtTrfTxInfElement.appendChild(document.createElement("CdtrAcct"))
					.appendChild(document.createElement("Id")).appendChild(document.createElement("IBAN"))
					.setTextContent(CdtrAcct_IBAN);
		CdtTrfTxInfElement.appendChild(document.createElement("RmtInf"))
					.appendChild(document.createElement("Ustrd"))
					.setTextContent(iSEPA_ConvertSign(unverifiedReferenceLine, 140));
		
		return CdtTrfTxInfElement;
	}
	
	private void convertToXMLFile(Document document, File xmlFile) throws Exception {
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(xmlFile);
		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.transform(domSource, streamResult);
	}

	/**
	 * 
	 * Generate unstructured reference line
	 * 
	 * @param mpp
	 *            check
	 * @return String with the reference line
	 * 
	 *         see EACT www.eact.eu/main.php?page=SEPA
	 */
	private String getUnverifiedReferenceLine(MPaySelectionCheck mpp) {
		MPaySelectionLine[] mPaySelectionLines = mpp.getPaySelectionLines(true);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",Locale.GERMANY);


		StringBuffer remittanceInformationSB = new StringBuffer();
		String remittanceInformationTemplate = MSysConfig.getValue(LFR_PAYSELECTION_SEPA_REMITTANCE_TEMPLATE, "", mpp.getAD_Client_ID(), mpp.getAD_Org_ID());
		String remittanceInformationPrefix = MSysConfig.getValue(LFR_PAYSELECTION_SEPA_REMITTANCE_PREFIX, "", mpp.getAD_Client_ID(), mpp.getAD_Org_ID());

		for (MPaySelectionLine mPaySelectionLine : mPaySelectionLines) {
			String documentNo = null;
			MInvoice invoice = mPaySelectionLine.getInvoice();
			if (invoice != null) {
				if (remittanceInformationSB.length() != 0) {
					remittanceInformationSB.append(" / ");
				}

				if (!Util.isEmpty(remittanceInformationTemplate))
					remittanceInformationSB.append(Env.parseVariable(remittanceInformationTemplate, invoice, null, true));
				else {
					remittanceInformationSB.append(dateFormat.format(invoice.getDateInvoiced()));
					remittanceInformationSB.append(" ");
					documentNo = invoice.getDocumentNo();
					if (documentNo != null && documentNo.length() > 0) {
						remittanceInformationSB.append(documentNo);
					}
					
					if (invoice.getPOReference() != null) {
						if (!Util.isEmpty(invoice.getPOReference())) {
							remittanceInformationSB.append(" ");
							remittanceInformationSB.append(invoice.getPOReference());
						}
					}
					remittanceInformationSB.append(" ");
					remittanceInformationSB.append(NumberFormat.getNumberInstance(Locale.GERMANY).format(invoice.getGrandTotal()));					
				}
			}
		}
		
		if (!Util.isEmpty(remittanceInformationPrefix))
			remittanceInformationSB.insert(0, remittanceInformationPrefix);

		if (remittanceInformationSB.length() >= 136)
			return remittanceInformationSB.toString().substring(0, 136) + " u.a.";

		return remittanceInformationSB.toString();
	} // getUnverifiedReferenceLine

	private String getEndToEndId(MPaySelectionCheck mpp) {

		StringBuilder endToEndID = new StringBuilder();
		MPaySelectionLine[] mPaySelectionLines = mpp.getPaySelectionLines(true);

		String endToEndIdTemplate = MSysConfig.getValue(LFR_PAYSELECTION_SEPA_ENDTOENDID_TEMPLATE, "", mpp.getAD_Client_ID(), mpp.getAD_Org_ID());

		for (MPaySelectionLine mPaySelectionLine : mPaySelectionLines) {
			String documentNo = null;
			MInvoice invoice = mPaySelectionLine.getInvoice();
			if (invoice != null) {
				
				if (!Util.isEmpty(endToEndIdTemplate))
					documentNo = Env.parseVariable(endToEndIdTemplate, invoice, null, true);
				else
					documentNo = invoice.getDocumentNo();

				if (documentNo != null && documentNo.length() > 0) {
					endToEndID.append(documentNo);
					endToEndID.append("/");
				}
			}
		}

		return endToEndID.toString().substring(0, endToEndID.toString().length()-1);  //remove last /
	}

	/**
	 * Get Vendor/Customer Bank Account Information Based on BP_
	 * 
	 * @param bPartner
	 *            BPartner
	 * @return Account of business partner
	 */
/* inutile car on veut savoir s'il y a plusieurs IBAN
	private MBPBankAccount getBPartnerAccount(MBPartner bPartner) {

		MBPBankAccount[] bpBankAccounts = bPartner.getBankAccounts(true);
		MBPBankAccount bpBankAccount = null;
		for (MBPBankAccount bpBankAccountTemp : bpBankAccounts) {
			if (bpBankAccountTemp.isActive() && !Util.isEmpty(bpBankAccountTemp.getIBAN())) {

				if (isDirectDebit() && bpBankAccountTemp.isDirectDebit())
					bpBankAccount = bpBankAccountTemp;
				else if (!isDirectDebit() && bpBankAccountTemp.isDirectDeposit())
					bpBankAccount = bpBankAccountTemp;

				if (bpBankAccount != null)
					break;
			}
		}
		return bpBankAccount;
	} // getBPartnerAccount
*/
	public static String iSEPA_ConvertSign(String text) {
		text = text.replace("ä", "ae");
		text = text.replace("ö", "oe");
		text = text.replace("ü", "ue");
		text = text.replace("Ä", "Ae");
		text = text.replace("Ö", "Oe");
		text = text.replace("Ü", "Ue");
		text = text.replace("ß", "ss");
		text = text.replace("é", "e");
		text = text.replace("è", "e");
		text = text.replace("&", "und");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		text = text.replace("\"", "&quot;");
		text = text.replace("'", "&apos;");
		return text;
	}

	public static String iSEPA_ConvertSign(String text, int maxLength) {
		String targettext = iSEPA_ConvertSign(text);

		if (targettext.length() <= maxLength) {
			return targettext;
		} else {
			return targettext.substring(0, maxLength);
		}
	}

	public void setDocumentType(String paymentRule) {
		if (MPaySelectionCheck.PAYMENTRULE_DirectDeposit.equals(paymentRule)) {
			documentType = SEPA_CREDIT_TRANSFER;
			directDebit = false;
		}
	}

	@Override
	public String getFilenamePrefix() {
		String creationDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis());
		if (m_isVirtInstantane)
			creationDate += "-INST";

		return "SEPA-" + creationDate ;
	}

	@Override
	public String getFilenameSuffix() {
		return ".xml";
	}

	@Override
	public String getContentType() {
		return "text/xml";
	}

	public boolean supportsDepositBatch() {
		return false;
	}

	public boolean supportsSeparateBooking() {
		return true;
	}

	public boolean getDefaultDepositBatch() {
		return false;
	}

	public boolean isDirectDebit() {
		return directDebit;
	}

	public void setDirectDebit(boolean isDirectDebit) {
		this.directDebit = isDirectDebit;
	}

	private class GenericPayment {

		private String m_pmtId, m_ustrd;
		private int m_bpartnerID, m_locationID, m_bpBankAccountID;
		private BigDecimal m_payAmt;

		public GenericPayment(MPaySelectionCheck psc) {
			m_pmtId = psc.getC_PaySelectionCheck_ID() > 0 ? getEndToEndId(psc) : "";
			m_ustrd = psc.getC_PaySelectionCheck_ID() > 0 ? getUnverifiedReferenceLine(psc) : "";
			m_bpartnerID = psc.getC_BPartner_ID();
			m_locationID = psc.getC_PaySelectionCheck_ID() > 0 ? psc.getPaySelectionLines(false)[0].getC_Invoice().getC_BPartner_Location().getC_Location_ID() : -1;
			m_bpBankAccountID = 0;
			m_payAmt = psc.getPayAmt();
		}

		public GenericPayment(MLFRPaySelectionPrepayment psp) {
			m_pmtId = DB.getSQLValueStringEx(null, "SELECT DocumentNo FROM C_Payment WHERE C_Payment_ID = ?", psp.getC_Payment_ID());
			m_ustrd = psp.getDescription();
			m_bpartnerID = psp.getC_BPartner_ID();
			m_locationID = new MBPartnerLocation(Env.getCtx(), psp.getC_BPartner_Location_ID(), null).getC_Location_ID();
			m_bpBankAccountID = psp.getC_BP_BankAccount_ID();
			m_payAmt = psp.getPayAmt();
		}

		public String getPmtId() { return m_pmtId == null ? "" : m_pmtId; }
		public String getUstrd() { return m_ustrd == null ? "" : m_ustrd; }
		public int getBPartnerID() { return m_bpartnerID; }
		public int getLocationID() { return m_locationID; }
		public int getBPBankAccountID() { return m_bpBankAccountID; }
		public BigDecimal getPayAmt() { return m_payAmt; }

	}

} // PaymentExport
