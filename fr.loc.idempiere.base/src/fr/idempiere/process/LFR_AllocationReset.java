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

import org.compiere.model.MAllocationHdr;
import org.compiere.process.AllocationReset;
import org.compiere.util.DB;
import org.compiere.util.Env;

import fr.idempiere.util.LfrFactReconciliationUtil;

/**
 *	Process de lettrage des écritures comptables
 *  @author Nicolas Micoud - TGI
 */

public class LFR_AllocationReset extends AllocationReset {

	protected void prepare() {
		super.prepare();
	}	//	prepare

	protected String doIt() throws Exception {
		return super.doIt();
	}

	protected String testIfDeleteable(MAllocationHdr hdr) {

		int bpartnerID = LfrFactReconciliationUtil.getAllocBPartnerID(Env.getCtx(), hdr, null);
		if (bpartnerID > 0) { // si plusieurs tiers, le lettrage devra avoir été supprimé manuellement

			int[] factAcctIDs = LfrFactReconciliationUtil.getAllocationRelatedFactAcctIDs(hdr.getC_AllocationHdr_ID(), bpartnerID);

			if (factAcctIDs != null && factAcctIDs.length > 0) {

				for (int factAcctID : factAcctIDs)
					DB.executeUpdateEx("DELETE FROM Fact_Reconciliation WHERE Fact_Acct_ID = ?", new Object[] {factAcctID}, m_trx.getTrxName());
			}
		}

		return super.testIfDeleteable(hdr);
	}

}	//	LFR_AllocationReset