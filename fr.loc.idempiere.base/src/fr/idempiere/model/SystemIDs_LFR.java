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


/**
 *  List all hardcoded ID used in the code
 *  @author Nicolas Micoud - TGI
 */

public class SystemIDs_LFR {

	// System Configurator
	public final static String LFR_IN_USE = "LFR_IN_USE";
	public final static String LFR_PERIOD_AUTO_CLOSE_DOCBASETYPE_DAYS = "LFR_PERIOD_AUTO_CLOSE_DOCBASETYPE_DAYS";

	// Colonnes
	public static final String C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT = "LFR_RanBenef_Acct";
	public static final String C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT = "LFR_RanPerte_Acct";
	public static final String C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CCAACCT = "LFR_ODSituationPrepaCCA_Acct";
	public static final String C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CAPACCT = "LFR_ODSituationPrepaCAP_Acct";
	public static final String C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_TCAPACCT = "LFR_ODSituationPrepaTCAP_Acct";
	public static final String C_INVOICELINE_LFR_IMPUTATIONDATEDEB = "LFR_ImputationDateDeb";
	public static final String C_INVOICELINE_LFR_IMPUTATIONDATEFIN = "LFR_ImputationDateFin";
}
