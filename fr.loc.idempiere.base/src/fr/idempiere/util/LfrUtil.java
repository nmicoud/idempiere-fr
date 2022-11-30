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

package fr.idempiere.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

import org.compiere.model.MClient;
import org.compiere.util.Util;


/**
 * Util class for LFR
 * @author Nicolas Micoud - TGI
 */

public class LfrUtil extends Util {

	/** Formatte la date passée en paramètre selon le format défini */
	public static String formatDate (Properties ctx, int clientID, Object value) {
		return formatDate(ctx, clientID, value, "");
	}

	/** Formatte la date passée en paramètre selon le format défini */
	public static String formatDate (Properties ctx, int clientID, Object value, String format) {
		if (Util.isEmpty(format))
			format = "dd/MM/yyyy";

		Locale locale = MClient.get(ctx, clientID).getLanguage().getLocale();
		return new SimpleDateFormat(format, locale).format(value);
	}
	
}
