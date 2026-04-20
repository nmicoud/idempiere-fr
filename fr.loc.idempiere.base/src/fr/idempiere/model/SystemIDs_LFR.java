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

import org.compiere.model.MSysConfig;

/**
 *  List all hardcoded ID used in the code
 *  @author Nicolas Micoud - TGI
 */

public class SystemIDs_LFR {

	public final static int PROCESS_C_ALLOCATION_RESET_DIRECT = 53199;

	// System Configurator
	private final static String LFR_COLUMN_ELEMENTVALUE_LFR_ISCUMULONGRANDLIVRE = "LFR_COL_C_ELEMENTVALUE_ISCUMULONGRANDLIVRE";
	private final static String LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT = "LFR_COL_C_ACCTSCHEMA_GL_RAN_BENEFACCT";
	private final static String LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT = "LFR_COL_C_ACCTSCHEMA_GL_RAN_PERTEACCT";
	private final static String LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CCAACCT = "LFR_COL_C_ACCTSCHEMA_GL_ODSITPREP_CCAACCT";
	private final static String LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CAPACCT = "LFR_COL_C_ACCTSCHEMA_GL_ODSITPREP_CAPACCT";
	private final static String LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_TCAPACCT = "LFR_COL_C_ACCTSCHEMA_GL_ODSITPREP_TCAPACCT";
	private final static String LFR_COLUMN_C_INVOICELINE_LFR_IMPUTATIONDATEDEB = "LFR_COL_C_INVOICELINE_IMPUTATIONDATEDEB";
	private final static String LFR_COLUMN_C_INVOICELINE_LFR_IMPUTATIONDATEFIN = "LFR_COL_C_INVOICELINE_IMPUTATIONDATEFIN";

	public final static String LFR_ACCT_VIEWER_SHOW_DISPLAY_QTY = "LFR_ACCT_VIEWER_SHOW_DISPLAY_QTY";
	public final static String LFR_ACCT_VIEWER_SHOW_DISPLAY_SOURCE = "LFR_ACCT_VIEWER_SHOW_DISPLAY_SOURCE";
	public final static String LFR_FEC_PER_ORG = "LFR_FEC_PER_ORG";
	public final static String LFR_IMMEDIATE_RECONCILIATION = "LFR_IMMEDIATE_RECONCILIATION";
	public final static String LFR_IN_USE = "LFR_IN_USE";
	public final static String LFR_PAYSELECTION_SEPA_DEBTOR_NAME = "LFR_PAYSELECTION_SEPA_DEBTOR_NAME";
	public final static String LFR_PAYSELECTION_SEPA_ENDTOENDID_TEMPLATE = "LFR_PAYSELECTION_SEPA_ENDTOENDID_TEMPLATE";
	public final static String LFR_PAYSELECTION_SEPA_INITIATOR_NAME = "LFR_PAYSELECTION_SEPA_INITIATOR_NAME";
	public final static String LFR_PAYSELECTION_SEPA_REMITTANCE_PREFIX = "LFR_PAYSELECTION_SEPA_REMITTANCE_PREFIX";
	public final static String LFR_PAYSELECTION_SEPA_REMITTANCE_TEMPLATE = "LFR_PAYSELECTION_SEPA_REMITTANCE_TEMPLATE";
	public final static String LFR_PERIOD_AUTO_CLOSE_DOCBASETYPE_DAYS = "LFR_PERIOD_AUTO_CLOSE_DOCBASETYPE_DAYS";
	public final static String LFR_PERIOD_VALIDATION_VIEW_UNPOSTED = "LFR_PERIOD_VALIDATION_VIEW_UNPOSTED";
	public final static String LFR_PERIOD_VALIDATION_VIEW_UNPROCESSED = "LFR_PERIOD_VALIDATION_VIEW_UNPROCESSED";
	public final static String LFR_REPORT_FACT_TITLE_PREFIX = "LFR_REPORT_FACT_TITLE_PREFIX";

	// Colonnes
	public static final String C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT = MSysConfig.getValue(LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_RAN_BENEFACCT, "LFR_RanBenef_Acct");
	public static final String C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT = MSysConfig.getValue(LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_RAN_PERTEACCT, "LFR_RanPerte_Acct");
	public static final String C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CCAACCT = MSysConfig.getValue(LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CCAACCT, "LFR_ODSituationPrepaCCA_Acct");
	public static final String C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CAPACCT = MSysConfig.getValue(LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_CAPACCT, "LFR_ODSituationPrepaCAP_Acct");
	public static final String C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_TCAPACCT = MSysConfig.getValue(LFR_COLUMN_C_ACCTSCHEMA_GL_LFR_ODSITUATIONPREPA_TCAPACCT, "LFR_ODSituationPrepaTCAP_Acct");
	public static final String C_BP_BANKACCOUNT_LFR_ISDEFAULT = "LFR_IsDefault";
	public static final String C_ELEMENT_VALUE_LFR_ISCUMULONGRANDLIVRE = MSysConfig.getValue(LFR_COLUMN_ELEMENTVALUE_LFR_ISCUMULONGRANDLIVRE, "LFR_IsCumulOnGrandLivre");
	public static final String C_INVOICELINE_LFR_IMPUTATIONDATEDEB = MSysConfig.getValue(LFR_COLUMN_C_INVOICELINE_LFR_IMPUTATIONDATEDEB, "LFR_ImputationDateDeb");
	public static final String C_INVOICELINE_LFR_IMPUTATIONDATEFIN = MSysConfig.getValue(LFR_COLUMN_C_INVOICELINE_LFR_IMPUTATIONDATEFIN, "LFR_ImputationDateFin");
	public static final String C_PAYSELECTION_LFR_PAYSELECTIONCREATEPAYMENT = "LFR_PaySelectionCreatePayment";
	public static final String C_PAYSELECTION_LFR_PAYSELECTIONEXPORT = "LFR_PaySelectionExport";

	public final static String REFERENCE_LFR_LETTRAGEFILTRE = "49b609c0-822e-4132-9a9c-0b18c5ac7748";
}
