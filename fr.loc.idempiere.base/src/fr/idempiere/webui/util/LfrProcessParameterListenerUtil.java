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

package fr.idempiere.webui.util;

import org.adempiere.webui.apps.ProcessParameterPanel;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WYesNoEditor;

/**
 *  Utils class pour les ProcessParameterListener
 *  @author Nicolas Micoud - TGI
 */
public class LfrProcessParameterListenerUtil {

	public void clearParameter(ProcessParameterPanel parameterPanel, String columnName) {
		setEditorNull(parameterPanel.getEditor(columnName));
	}

	public void clearParameterTo(ProcessParameterPanel parameterPanel, String columnName) {
		setEditorNull(parameterPanel.getEditorTo(columnName));
	}

	private void setEditorNull(WEditor editor) {
		if (editor != null) {
			if (editor.getClass() == WYesNoEditor.class)
				editor.setValue(false);
			else
				editor.setValue(null);
		}
	}
}
