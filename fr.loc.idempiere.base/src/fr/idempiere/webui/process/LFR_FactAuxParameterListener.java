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

import org.adempiere.webui.apps.IProcessParameterListener;
import org.adempiere.webui.apps.ProcessParameterPanel;
import org.adempiere.webui.editor.WEditor;

import fr.idempiere.util.LfrFactReconciliationUtil;
import fr.idempiere.webui.util.LfrProcessParameterListenerUtil;

/**
 *  Mécanismes liés aux process basés sur LFR_FactAux
 *  @author Nicolas Micoud - TGI
 */
public class LFR_FactAuxParameterListener extends LfrProcessParameterListenerUtil implements IProcessParameterListener {

	@Override
	public void onChange(ProcessParameterPanel parameterPanel, String columnName, WEditor editor) {

		if (editor.getColumnName().equals("LFR_Customer_ID")) {

			if (editor.getValue() == null)
				clearParameter(parameterPanel, "LFR_Customer_Acct");
			else {
				WEditor fAcctSchema = parameterPanel.getEditor("C_AcctSchema_ID");

				if (fAcctSchema != null && !fAcctSchema.isNullOrEmpty())
					parameterPanel.getEditor("LFR_Customer_Acct").setValue(LfrFactReconciliationUtil.getCompteAuxiliaireClient(fAcctSchema.getValue(), editor.getValue()));	
			}
		}
		else if (editor.getColumnName().equals("LFR_Vendor_ID")) {

			if (editor.getValue() == null)
				clearParameter(parameterPanel, "LFR_Vendor_Acct");
			else {
				WEditor fAcctSchema = parameterPanel.getEditor("C_AcctSchema_ID");

				if (fAcctSchema != null && !fAcctSchema.isNullOrEmpty())
					parameterPanel.getEditor("LFR_Vendor_Acct").setValue(LfrFactReconciliationUtil.getCompteAuxiliaireFournisseur(fAcctSchema.getValue(), editor.getValue()));	
			}
		}
		else if (columnName.equals("IsCustomer") && (Boolean) editor.getValue()) {
			clearParameter(parameterPanel, "IsVendor");
			clearParameter(parameterPanel, "IsEmployee");
			clearParameter(parameterPanel, "LFR_Vendor_Acct");
		}
		else if (columnName.equals("IsVendor") && (Boolean) editor.getValue()) {
			clearParameter(parameterPanel, "IsCustomer");
			clearParameter(parameterPanel, "LFR_Customer_Acct");
		}
	}
}
