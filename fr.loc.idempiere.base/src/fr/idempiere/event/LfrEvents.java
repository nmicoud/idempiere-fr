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

package fr.idempiere.event;

import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEDEB;
import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEFIN;
import static fr.idempiere.model.SystemIDs_LFR.LFR_IN_USE;

import java.sql.Timestamp;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.base.event.LoginEventData;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.osgi.service.event.Event;

import fr.idempiere.util.LfrFactReconciliationUtil;
import fr.idempiere.util.LfrUtil;


public class LfrEvents extends AbstractEventHandler {

	@Override
	protected void initialize() {
		registerEvent(IEventTopics.AFTER_LOGIN);

		registerTableEvent(IEventTopics.PO_BEFORE_NEW, MInvoiceLine.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MInvoiceLine.Table_Name);

		registerTableEvent(IEventTopics.DOC_AFTER_POST, MInvoice.Table_Name);
		registerTableEvent(IEventTopics.DOC_AFTER_POST, MPayment.Table_Name);
		registerTableEvent(IEventTopics.DOC_AFTER_POST, MAllocationHdr.Table_Name);
	}

	@Override
	protected void doHandleEvent(Event event) {

		String topic = event.getTopic();

		if (topic.equals(IEventTopics.AFTER_LOGIN)) {
			LoginEventData loginData = (LoginEventData) event.getProperty(IEventManager.EVENT_DATA);
			boolean useLfr = MSysConfig.getBooleanValue(LFR_IN_USE, false, loginData.getAD_Client_ID());
			Env.setContext(Env.getCtx(), "#LFR", useLfr);
			return;
		}

		PO po = getPO(event);

		if (topic.equals(IEventTopics.DOC_AFTER_POST)) {
			if (po.get_TableName().equals(MInvoice.Table_Name)) {
				LfrFactReconciliationUtil.factReconcile(po);
			}
			else if (po.get_TableName().equals(MPayment.Table_Name)) {
				LfrFactReconciliationUtil.factReconcile(po);
			}
			else if (po.get_TableName().equals(MAllocationHdr.Table_Name)) {

				MAllocationHdr alloc = (MAllocationHdr) po;
				MAcctSchema[] ass = MAcctSchema.getClientAcctSchema(po.getCtx(), po.getAD_Client_ID());
				for (MAcctSchema as : ass) {
					LfrFactReconciliationUtil.LettrageAlloc(po.getCtx(), alloc, alloc.getAD_Client_ID(), as.getC_AcctSchema_ID(), -1, po.get_TrxName());
				}
			}
		}
		else if (po.get_TableName().equals(MInvoiceLine.Table_Name)) {

			MInvoiceLine il = (MInvoiceLine) po;

			if (il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB) != null && il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN) != null) {
				Timestamp deb = (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEDEB);
				Timestamp fin = (Timestamp) il.get_Value(C_INVOICELINE_LFR_IMPUTATIONDATEFIN);

				String err = LfrUtil.checkInvoiceLineImputation(il.getCtx(), deb, fin);
				if (!Util.isEmpty(err))
					throw new AdempiereException(err);
			}
		}
	}
}
