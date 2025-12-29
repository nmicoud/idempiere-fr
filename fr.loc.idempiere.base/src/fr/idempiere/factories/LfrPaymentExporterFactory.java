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

package fr.idempiere.factories;

import org.adempiere.base.IPaymentExporterFactory;
import org.compiere.util.CLogger;
import org.compiere.util.PaymentExport;
import fr.idempiere.util.SEPAPaymentExport;

public class LfrPaymentExporterFactory implements IPaymentExporterFactory {

	@SuppressWarnings("unused")
	private final static CLogger s_log = CLogger.getCLogger(LfrPaymentExporterFactory.class);

	/**
	 * default constructor
	 */
	public LfrPaymentExporterFactory() {
	}

	@Override
	public PaymentExport newPaymentExporterInstance(String className) {
		if (SEPAPaymentExport.class.getName().equals(className))
			return new SEPAPaymentExport();
		return null;
	}

}
