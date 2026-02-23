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

import static fr.idempiere.model.SystemIDs_LFR.C_PAYSELECTION_LFR_PAYSELECTIONCREATEPAYMENT;

import org.compiere.model.MPaySelection;

import fr.idempiere.util.LfrPayPrintUtil;

public class LFR_PaySelectionCreatePayment extends LfrProcess
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

		int no = ppu.createPayment();

		if (no > 0) {
			ps.set_ValueNoCheck(C_PAYSELECTION_LFR_PAYSELECTIONCREATEPAYMENT, "Y");
			ps.saveEx();
		}
		else
			return "@Error@" + no;

		return "@ProcessOK@";
	} // doIt

} // LFR_PaySelectionCreatePayment