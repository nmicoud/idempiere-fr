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

import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;

import fr.idempiere.model.MLFRODSituationPrepa;

/**
 *  Process de préparation des OD de situation
 *  @author Nicolas Micoud - TGI
 */

public class LFR_ODSituationPrepasyncLines extends LfrProcess {

	private boolean	p_DeleteOld = true;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("DeleteOld"))
				p_DeleteOld = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	protected String doIt () throws Exception
	{
		MLFRODSituationPrepa sp = new MLFRODSituationPrepa (getCtx(), getRecord_ID(), get_TrxName());

		if (p_DeleteOld)
			DB.executeUpdateEx("DELETE FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = " + getRecord_ID(), get_TrxName()); // TODO passer par une variable

		return sp.syncLines(false);
	}
}