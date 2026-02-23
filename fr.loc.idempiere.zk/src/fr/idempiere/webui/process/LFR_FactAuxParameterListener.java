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

import static fr.idempiere.webui.apps.form.WLFRFactExtraitCompte.CTX_LFR_FACT_EXTRAIT_COMPTE_PANEL;

import org.adempiere.webui.apps.IProcessParameterListener;
import org.adempiere.webui.apps.ProcessParameterPanel;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.compiere.util.Env;

import fr.idempiere.util.LfrFactReconciliationUtil;

/**
 *  Mécanismes liés aux process basés sur LFR_FactAux
 *  @author Nicolas Micoud - TGI
 */
public class LFR_FactAuxParameterListener implements IProcessParameterListener {

	@Override
	public void onChange(ProcessParameterPanel parameterPanel, String columnName, WEditor editor) {

		if (Env.getContext(Env.getCtx(), parameterPanel.getWindowNo(), CTX_LFR_FACT_EXTRAIT_COMPTE_PANEL).equals("Y")) {

			if (editor.getColumnName().equals("LFR_Customer_ID")) {

				if (editor.getValue() == null)
					setEditorNull(parameterPanel, "LFR_Customer_Acct");
				else {
					WEditor fAcctSchema = parameterPanel.getEditor("C_AcctSchema_ID");

					if (fAcctSchema != null && !fAcctSchema.isNullOrEmpty())
						parameterPanel.getEditor("LFR_Customer_Acct").setValue(LfrFactReconciliationUtil.getCompteAuxiliaireClient(fAcctSchema.getValue(), editor.getValue()));	
				}
			}
			else if (editor.getColumnName().equals("LFR_Vendor_ID")) {

				if (editor.getValue() == null)
					setEditorNull(parameterPanel, "LFR_Vendor_Acct");
				else {
					WEditor fAcctSchema = parameterPanel.getEditor("C_AcctSchema_ID");

					if (fAcctSchema != null && !fAcctSchema.isNullOrEmpty())
						parameterPanel.getEditor("LFR_Vendor_Acct").setValue(LfrFactReconciliationUtil.getCompteAuxiliaireClient(fAcctSchema.getValue(), editor.getValue()));	
				}
			}
		}
		else if (columnName.equals("IsCustomer") && (Boolean) editor.getValue()) {
			setEditorNull(parameterPanel, "IsVendor");
			setEditorNull(parameterPanel, "IsEmployee");
			setEditorNull(parameterPanel, "LFR_Vendor_Acct");
		}
		else if (columnName.equals("IsVendor") && (Boolean) editor.getValue()) {
			setEditorNull(parameterPanel, "IsCustomer");
			setEditorNull(parameterPanel, "LFR_Customer_Acct");
		}
	}

	private void setEditorNull(ProcessParameterPanel parameterPanel, String columnName) {
		WEditor editorTarget = parameterPanel.getEditor(columnName);
		if (editorTarget != null) {
			if (editorTarget.getClass() == WYesNoEditor.class)
				editorTarget.setValue(false);
			else
				editorTarget.setValue(null);
		}
	}
}
