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
import org.compiere.util.Env;

import fr.idempiere.webui.util.LfrProcessParameterListenerUtil;

/**
 *  Mécanismes liés aux process basés sur LFR_FactGeneExtraitCompte
 *  @author Nicolas Micoud - TGI
 */
public class LFR_FactGeneExtraitCompteParameterListener extends LfrProcessParameterListenerUtil implements IProcessParameterListener {

	@Override
	public void onChange(ProcessParameterPanel parameterPanel, String columnName, WEditor editor) {

		if (Env.getContext(Env.getCtx(), parameterPanel.getWindowNo(), CTX_LFR_FACT_EXTRAIT_COMPTE_PANEL).equals("")) {
			if (columnName.equals("LFR_AccountSelection") && editor.getValue() != null) {
				clearParameter(parameterPanel, "Account_ID");
				clearParameterTo(parameterPanel, "Account_ID");
			}
			else if (columnName.startsWith("Account_ID") && editor.getValue() != null)
				clearParameter(parameterPanel, "LFR_AccountSelection");
		}
	}
}
