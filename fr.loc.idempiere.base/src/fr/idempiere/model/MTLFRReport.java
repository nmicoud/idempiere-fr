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

package fr.idempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.DB;

/**
 * Temp table for LFR reports
 * @author Nicolas Micoud - TGI
 */

public class MTLFRReport extends X_T_LFR_Report {

	private static final long serialVersionUID = -8117681337697309118L;

	public MTLFRReport (Properties ctx, int T_LFR_Report_ID, String trxName) {
		super (ctx, T_LFR_Report_ID, trxName);
	}	//	MTLFRReport


	public MTLFRReport (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}	//	MTLFRReport

	public MTLFRReport (Properties ctx, int T_LFR_Report_ID, int instanceID, String trxName) {
		super (ctx, T_LFR_Report_ID, trxName);
		setAD_PInstance_ID(instanceID);
	}	//	MTLFRReport

	/** Renvoie la séquence liée à la table T_LFR_Report */
	public static int getSequenceID(String trxName) {
		return DB.getSQLValueEx(trxName, "SELECT AD_Sequence_ID FROM AD_Sequence WHERE Name = ? AND	IsActive = 'Y' AND IsTableID = 'Y' AND IsAutoSequence = 'Y' AND AD_Client_ID = 0", Table_Name);
	}

}
