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

import static fr.idempiere.model.SystemIDs_LFR.C_PAYSELECTION_LFR_PAYSELECTIONEXPORT;

import java.io.File;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MPaySelection;
import org.compiere.process.ProcessInfo;
import org.compiere.util.Util;

import fr.idempiere.util.LfrPayPrintUtil;
import fr.idempiere.util.LfrUtil;

public class LFR_PaySelectionExport extends LfrProcess
{

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws Exception {
		MPaySelection ps = new MPaySelection(getCtx(), getRecord_ID(), get_TrxName());
		LfrPayPrintUtil ppu = new LfrPayPrintUtil(getCtx(), getRecord_ID(), get_TrxName());

		try {
			String err = ppu.exportFile();

			if (new File(err).exists()) {

				File currentFile = new File(err);
				File fileWithCorrectName = null;

				String filename = ppu.getFilename();

				boolean rename = false;

				if (!Util.isEmpty(filename)) {
					try {
						String path = currentFile.getParent() + File.separator + filename;
						fileWithCorrectName = new File(path);

						if (!LfrUtil.copyFile(currentFile, fileWithCorrectName))
							throw new AdempiereException("Can't copy file from '" + currentFile.getAbsoluteFile() + "' to '" + path + "'");

						rename = true;
					}
					catch(Exception e) {
						log.severe("Error copying content '" + currentFile + "' to '" + filename + "'");
					}
				}

				ps.set_ValueNoCheck(C_PAYSELECTION_LFR_PAYSELECTIONEXPORT, "Y");
				ps.saveEx();

				File fileToDownload = rename && fileWithCorrectName != null ? fileWithCorrectName : currentFile;

				if (processUI != null)
					processUI.download(fileToDownload);

				ProcessInfo m_pi = getProcessInfo();
				m_pi.setExportFile(fileToDownload);
				m_pi.setExportFileExtension(filename.substring(filename.lastIndexOf('.')));
			}
			else
				return "@Error@" + err;
		}
		catch(Exception e) {
			return "@Error@" + e;
		}

		return "@ProcessOK@";
	} // doIt

} // LFR_PaySelectionExport