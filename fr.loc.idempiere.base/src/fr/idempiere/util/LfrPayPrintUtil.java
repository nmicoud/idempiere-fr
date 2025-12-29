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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.IPaymentExporterFactory;
import org.adempiere.base.Service;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaymentBatch;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.PaymentExport;
import org.compiere.util.Util;

public class LfrPayPrintUtil {

	public static CLogger log = CLogger.getCLogger(LfrPayPrintUtil.class);
	PaymentExport m_PaymentExport;
	String m_PaymentExportClass = null;
	String m_pPaymentRule = "";
	MPaySelectionCheck[] m_checks = null;
	MPaymentBatch m_batch = null; 
	int m_C_BankAccount_ID = -1;
	int m_C_PaySelection_ID = 0;
	boolean isfDepositBatch= false; //hardcoded
	public String contentType = "";
	public String filenameForDownload = "";
	public InputStream content = null;
	
	public LfrPayPrintUtil (Properties ctx, int C_PaySelection_ID, String trxName) {
		m_C_PaySelection_ID=C_PaySelection_ID;
		m_pPaymentRule = DB.getSQLValueStringEx(trxName, "SELECT PaymentRule FROM C_PaySelectionCheck WHERE C_PaySelection_ID=?", C_PaySelection_ID); // assume is always the same
		m_checks = getChecks(C_PaySelection_ID, m_pPaymentRule, trxName);
		
		if (m_checks == null || m_checks.length == 0) { // uniquement des acomptes, on va simuler un PSC pour rester dans le standard
			ArrayList<MPaySelectionCheck> list = new ArrayList<MPaySelectionCheck>();
			MPaySelectionCheck psc = new MPaySelectionCheck(Env.getCtx(), 0, null);
			psc.setC_PaySelection_ID(C_PaySelection_ID);
			list.add(psc);
			m_checks = list.toArray(m_checks);
			m_pPaymentRule = MPaySelectionCheck.PAYMENTRULE_DirectDeposit;
		}
	}	//	PayPrintUtil_Tgi

	public static MPaySelectionCheck[] getChecks(int C_PaySelection_ID, String PaymentRule, String trxName) {
		return MPaySelectionCheck.get(C_PaySelection_ID, PaymentRule, trxName);
	}
	
	public String exportFile() {
		try
		{
			int no = 0;
			StringBuffer err = new StringBuffer("");
			File tempFile = null;

			// org.compiere.apps.form.PayPrint.loadPaySelectInfo(int)
			m_C_BankAccount_ID = -1;
			String sql = "SELECT ps.C_BankAccount_ID, b.Name || ' ' || ba.AccountNo,"	//	1..2
					+ " c.ISO_Code, CurrentBalance, ba.PaymentExportClass "					//	3..5
					+ "FROM C_PaySelection ps"
					+ " INNER JOIN C_BankAccount ba ON (ps.C_BankAccount_ID=ba.C_BankAccount_ID)"
					+ " INNER JOIN C_Bank b ON (ba.C_Bank_ID=b.C_Bank_ID)"
					+ " INNER JOIN C_Currency c ON (ba.C_Currency_ID=c.C_Currency_ID) "
					+ "WHERE ps.C_PaySelection_ID=? AND ps.Processed='Y' AND ba.IsActive='Y'";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, m_C_PaySelection_ID);
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					m_C_BankAccount_ID = rs.getInt(1);
					m_PaymentExportClass = rs.getString(5);
				}
				else
				{
					m_C_BankAccount_ID = -1;
					m_PaymentExportClass = null;
					log.log(Level.SEVERE, "No active BankAccount for C_PaySelection_ID=" + m_C_PaySelection_ID);
				}
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

			// org.compiere.apps.form.PayPrint.loadPaymentExportClass(StringBuffer)
			m_PaymentExport = null ;

			if (m_PaymentExportClass == null || m_PaymentExportClass.trim().length() == 0) {
				m_PaymentExportClass = "org.compiere.util.GenericPaymentExport";
			}
			try
			{
				List<IPaymentExporterFactory> factories = Service.locator().list(IPaymentExporterFactory.class).getServices();
				if (factories != null && !factories.isEmpty()) {
					for(IPaymentExporterFactory factory : factories) {
						m_PaymentExport = factory.newPaymentExporterInstance(m_PaymentExportClass);
						if (m_PaymentExport != null)
							break;
					}
				}

				if (m_PaymentExport == null)
				{
					Class<?> clazz = Class.forName (m_PaymentExportClass);
					m_PaymentExport = (PaymentExport)clazz.getDeclaredConstructor().newInstance();
				}

			}
			catch (ClassNotFoundException e)
			{
				if (err!=null)
				{
					err.append("No custom PaymentExport class " + m_PaymentExportClass + " - " + e.toString());
					log.log(Level.SEVERE, err.toString(), e);
				}
				//				return -1;
			}
			catch (Exception e)
			{
				if (err!=null)
				{
					err.append("Error in " + m_PaymentExportClass + " check log, " + e.toString());
					log.log(Level.SEVERE, err.toString(), e);
				}
				//				return -1;
			}
			//			return 0 ;

			//  Get File Info
			tempFile = File.createTempFile(m_PaymentExport.getFilenamePrefix(), m_PaymentExport.getFilenameSuffix());
			System.out.println(tempFile);
			no = m_PaymentExport.exportToFile(m_checks,	isfDepositBatch, m_pPaymentRule, tempFile, err);

			if (no >= 0 && Util.isEmpty(err.toString())) {

				filenameForDownload = m_PaymentExport.getFilenamePrefix() + m_PaymentExport.getFilenameSuffix();
				contentType = m_PaymentExport.getContentType();
				content = new FileInputStream(tempFile);
				return tempFile.getAbsolutePath();

			} else {
				return err.toString();
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		return "Error";
	}

	public InputStream getContent() {
		return content;
	}
	
	public String getContentType() {
		return contentType;
	}
	public String getFilename() {
		return filenameForDownload;
	}

	public int createPayment() {
		return MPaySelectionCheck.confirmPrint (m_checks, m_batch, isfDepositBatch);
	}
}
