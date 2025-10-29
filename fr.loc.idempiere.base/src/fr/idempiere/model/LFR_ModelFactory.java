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

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

public class LFR_ModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {

		if (MLFRODSituationPrepa.Table_Name.equals(tableName))
			return MLFRODSituationPrepa.class;
		if (MLFRODSituationPrepaLine.Table_Name.equals(tableName))
			return MLFRODSituationPrepaLine.class;
		if (MLFRPeriodAutoCloseDBT.Table_Name.equals(tableName))
			return MLFRPeriodAutoCloseDBT.class;
		if (MTLFRReport.Table_Name.equals(tableName))
			return MTLFRReport.class;

		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {

		if (tableName.equals(MLFRODSituationPrepa.Table_Name))
			return new MLFRODSituationPrepa(Env.getCtx(), Record_ID, trxName);
		if (tableName.equals(MLFRODSituationPrepaLine.Table_Name))
			return new MLFRODSituationPrepaLine(Env.getCtx(), Record_ID, trxName);
		if (tableName.equals(MLFRPeriodAutoCloseDBT.Table_Name))
			return new MLFRPeriodAutoCloseDBT(Env.getCtx(), Record_ID, trxName);
		if (tableName.equals(MTLFRReport.Table_Name))
			return new MTLFRReport(Env.getCtx(), Record_ID, trxName);

		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {

		if (MLFRODSituationPrepa.Table_Name.equals(tableName))
			return new MLFRODSituationPrepa(Env.getCtx(), rs, trxName);
		if (MLFRODSituationPrepaLine.Table_Name.equals(tableName))
			return new MLFRODSituationPrepaLine(Env.getCtx(), rs, trxName);
		if (MLFRPeriodAutoCloseDBT.Table_Name.equals(tableName))
			return new MLFRPeriodAutoCloseDBT(Env.getCtx(), rs, trxName);
		if (MTLFRReport.Table_Name.equals(tableName))
			return new MTLFRReport(Env.getCtx(), rs, trxName);

		return null;
	}

}
