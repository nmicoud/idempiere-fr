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

package fr.idempiere.webui.process;

import static fr.idempiere.model.SystemIDs_LFR.PROCESS_C_ALLOCATION_RESET_DIRECT;

import org.adempiere.webui.apps.IProcessParameterListener;
import org.adempiere.webui.apps.ProcessParameterPanel;
import org.adempiere.webui.editor.WEditor;
import org.compiere.model.MAllocationHdr;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zul.Html;

import fr.idempiere.util.LfrFactReconciliationUtil;

/**
 *  Affichage d'un message d'avertissement quand il y a du lettrage
 *  @author Nicolas Micoud - TGI
 */
public class LFR_AllocationResetDirectParameterListener implements IProcessParameterListener {

	public void onInit(ProcessParameterPanel parameterPanel) {

		if (parameterPanel.getProcessInfo().getAD_Process_ID() == PROCESS_C_ALLOCATION_RESET_DIRECT) {
			if (DB.getSQLValueEx(null, "SELECT 1 FROM Fact_Reconciliation WHERE Fact_Acct_ID IN (SELECT Fact_Acct_ID FROM Fact_Acct WHERE AD_Table_ID = ? AND Record_ID = ? AND 1=2)", MAllocationHdr.Table_ID, parameterPanel.getProcessInfo().getRecord_ID()) == 1) {
				Html html = new Html("La validation de cette action entraînera la <strong>suppression définitive du lettrage comptable</strong> associé");
				parameterPanel.appendChild(html);
			}
			else {
				MAllocationHdr alloc = new MAllocationHdr(Env.getCtx(), parameterPanel.getProcessInfo().getRecord_ID(), null);

				int bpartnerID = LfrFactReconciliationUtil.getAllocBPartnerID(Env.getCtx(), alloc, null);
				if (bpartnerID <= 0) {
					Html html = new Html(alloc.getDocumentInfo() + " concerne plusieurs tiers, il ne sera pas possible de supprimer le lettrage automatiquement, il faut le faire manuellement");
					parameterPanel.appendChild(html);	
				}
				else {
					int[] factAcctIDs = LfrFactReconciliationUtil.getAllocationRelatedFactAcctIDs(parameterPanel.getProcessInfo().getRecord_ID(), bpartnerID);

					if (factAcctIDs != null && factAcctIDs.length > 0) {
						Html html = new Html("La validation de cette action entraînera la <strong>suppression définitive du lettrage comptable</strong> associé");
						parameterPanel.appendChild(html);
					}
				}
			}
		}
	}

	public void onChange(ProcessParameterPanel parameterPanel, String columnName, WEditor editor) {}
}
