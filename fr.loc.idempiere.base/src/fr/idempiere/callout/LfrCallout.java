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

package fr.idempiere.callout;

import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEDEB;
import static fr.idempiere.model.SystemIDs_LFR.C_INVOICELINE_LFR_IMPUTATIONDATEFIN;

import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoiceLine;
import org.compiere.util.CLogger;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

import fr.idempiere.util.LfrUtil;


public class LfrCallout implements IColumnCallout {

	private static CLogger s_log = CLogger.getCLogger (LfrCallout.class);

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		String tableName = mTab.getTableName();
		String columnName = mField.getColumnName();

		if (tableName.equals(MInvoiceLine.Table_Name)) {
			if (columnName.equals(C_INVOICELINE_LFR_IMPUTATIONDATEDEB))
				onInvoiceLineImputationDateDeb(ctx, mTab, value);
			else if (columnName.equals(C_INVOICELINE_LFR_IMPUTATIONDATEFIN))
				onInvoiceLineImputationDateFin(ctx, mTab, value);
		}

		return "";
	}

	private void onInvoiceLineImputationDateDeb(Properties ctx, GridTab mTab, Object value) {

		if (value != null && mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEFIN) != null)
			checkInvoiceLineImputationDates(ctx, mTab);
	}

	private void onInvoiceLineImputationDateFin(Properties ctx, GridTab mTab, Object value) {

		if (value != null && mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEDEB) != null)
			checkInvoiceLineImputationDates(ctx, mTab);
	}

	private void checkInvoiceLineImputationDates(Properties ctx, GridTab mTab) {

		Timestamp deb = (Timestamp) mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEDEB);
		Timestamp fin = (Timestamp) mTab.getValue(C_INVOICELINE_LFR_IMPUTATIONDATEFIN);

		String err = LfrUtil.checkInvoiceLineImputation(ctx, deb, fin);

		if (Util.isEmpty(err)) { // on s'assure qu'il n'y a pas trop d'écart entre les dates saisies
			int nbDays = TimeUtil.getDaysBetween(deb, fin);
			if (nbDays > 3650)
				mTab.fireDataStatusEEvent("Warning", "Plus de 10 ans entre les dates d'imputation", false);
		}
		else
			mTab.fireDataStatusEEvent("Warning", err, false);
	}

}
