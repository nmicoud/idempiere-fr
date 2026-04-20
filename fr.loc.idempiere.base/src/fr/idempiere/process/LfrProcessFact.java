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

import static fr.idempiere.model.SystemIDs_LFR.LFR_REPORT_FACT_TITLE_PREFIX;

import java.sql.Timestamp;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MGLCategory;
import org.compiere.model.MOrg;
import org.compiere.model.MSysConfig;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import fr.idempiere.util.LfrUtil;


/**
 * Mécanismes communs à tous les états comptables
 * @author Nicolas Micoud - TGI
 */
public abstract class LfrProcessFact extends LfrProcess {

	public int p_acctSchema_ID = 0;
	public String p_postingType = "";
	public String p_glCatToExcludeIDs = "";
	public int p_pinstanceSourceID = -1;

	public String p_orgIDs = "";
	public int[] p_orgList = null;

	private String m_reportTitle = "";
	private String m_reportTitleCenter = "";
	private String m_clientName = "";
	private String m_orgName = "";
	private StringBuilder m_headerCenter = new StringBuilder();
	private StringBuilder m_footerCenter = new StringBuilder();

	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (name.equals("C_AcctSchema_ID"))
				p_acctSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("PostingType"))
				p_postingType = para[i].getParameterAsString();
			else if (name.equals("LFR_ProcessParaOrg_ID")) {
				p_orgIDs = para[i].getParameterAsCSVInt();
				p_orgList = para[i].getParameterAsIntArray();
			}
			else if (name.equals("LFR_GLCategoryToExclude_ID"))
				p_glCatToExcludeIDs = para[i].getParameterAsCSVInt();
			else if (name.equals("LFR_PInstance_Source_ID"))
				p_pinstanceSourceID = para[i].getParameterAsInt();
		}
	}	//	prepare

	protected String getTitlePrefix() {
		return MSysConfig.getValue(LFR_REPORT_FACT_TITLE_PREFIX, "", getAD_Client_ID());
	}

	protected String getTitleSuffix() {
		return " (" + MCurrency.get(getCtx(), MAcctSchema.get(p_acctSchema_ID).getC_Currency_ID()).getISO_Code() + ")";
	}

	protected String getTitleCenter() {
		return "";
	}

	public void forceTitleCenter(String titleCenter) {
		m_reportTitleCenter = titleCenter;
	}

	protected String getReportTitle() {
		if (Util.isEmpty(m_reportTitle))
			m_reportTitle = getTitlePrefix() + (!Util.isEmpty(m_reportTitleCenter) ? m_reportTitleCenter : getTitleCenter()) + getTitleSuffix();
		return m_reportTitle;
	}

	/** Permet de forcer une régénération du titre */
	public void resetReportTitle() {
		m_reportTitle = "";
	}

	/** Renvoie un String qui précise quelles dates sont utilisées dans l'état */
	protected String getDateCriteres(int clientID, Timestamp dateFrom, Timestamp dateTo) {
		if (dateFrom != null && dateTo != null)
			return Msg.getMsg(getCtx(), "LFR_CritereDateBetween", new Object[] {LfrUtil.formatDate(getCtx(), clientID, dateFrom), LfrUtil.formatDate(getCtx(), clientID, dateTo)});
		else if (dateFrom != null && dateTo == null)
			return Msg.getMsg(getCtx(), "LFR_CritereDateFrom", new Object[] {LfrUtil.formatDate(getCtx(), clientID, dateFrom)});
		else if (dateFrom == null && dateTo != null)
			return Msg.getMsg(getCtx(), "LFR_CritereDateUntil", new Object[] {LfrUtil.formatDate(getCtx(), clientID, dateTo)});
		else
			return Msg.getMsg(getCtx(), "LFR_NoCritereDate");		
	}

	public int getDocTypeForBankStatement() {
		return MDocType.getOfDocBaseType(getCtx(), MDocType.DOCBASETYPE_BankStatement)[0].getC_DocType_ID();
	}

	public int getDocTypeForAllocation() {
		return MDocType.getOfDocBaseType(getCtx(), MDocType.DOCBASETYPE_PaymentAllocation)[0].getC_DocType_ID();
	}

	public final static String ORG_DISPLAY_NAME = "Name";
	public final static String ORG_DISPLAY_VALUE = "Value";

	protected String getOrgDisplay() {
		return ORG_DISPLAY_NAME;
	}

	protected String getFactAcctOrgDisplay() {
		return ORG_DISPLAY_NAME;
	}

	protected String getOrgName() {
		if (Util.isEmpty(m_orgName) && p_orgList.length > 0) {

			if (p_orgList.length == 1)
				m_orgName = MOrg.get(getCtx(), p_orgList[0]).get_ValueAsString(getOrgDisplay());
			else
				m_orgName = getOrgsName(" / ");
		}

		return m_orgName;
	}

	protected String getOrgsName(String sep) {
		StringBuilder retValue = new StringBuilder("");
		for (int orgID : p_orgList)
			LfrUtil.add(retValue, MOrg.get(getCtx(), orgID).get_ValueAsString(getOrgDisplay()), sep);
		return retValue.toString();
	}

	protected String getOrgNameIfSingle() {
		if (p_orgList.length == 1)
			return MOrg.get(getCtx(), p_orgList[0]).get_ValueAsString(getOrgDisplay());
		return "";
	}

	public String getSqlWhereOrg(String tableSynonym) {
		if (p_orgList.length == 0)
			return "";
		else if (p_orgList.length == 1)
			return " AND " + (Util.isEmpty(tableSynonym) ? "" : tableSynonym + ".") + "AD_Org_ID = " + p_orgList[0];
		else
			return " AND " + (Util.isEmpty(tableSynonym) ? "" : tableSynonym + ".") + "AD_Org_ID IN (" + p_orgIDs + ")";
	}

	protected String getClientName() {
		if (Util.isEmpty(m_clientName))
			m_clientName = MClient.get(getCtx(), getAD_Client_ID()).getName();

		return m_clientName;
	}

	public String getSqlWhereGLCatToExclude(String tableSynonym) {
		if (Util.isEmpty(p_glCatToExcludeIDs))
			return "";
		else 
			return " AND " + (Util.isEmpty(tableSynonym) ? "" : tableSynonym + ".") + "GL_Category_ID NOT IN (" + p_glCatToExcludeIDs + ")";
	}

	public String getGLCatToExclude() {
		if (Util.isEmpty(p_glCatToExcludeIDs))
			return "";
		else {
			StringBuilder retValue = new StringBuilder();
			for (String glCatId : p_glCatToExcludeIDs.split(","))
				LfrUtil.add(retValue, MGLCategory.get(getCtx(), Integer.valueOf(glCatId)).getPrintName(), ", ");

			if (p_glCatToExcludeIDs.indexOf(",") > 0)
				retValue.insert(0, "Cette édition masque les écritures des journaux ");
			else
				retValue.insert(0, "Cette édition masque les écritures du journal ");

			return retValue.toString();
		}
	}

	public void setHeaderCenter(String text) {
		LfrUtil.add(m_headerCenter, text, " / ");
	}

	public String getHeaderCenter() {
		return m_headerCenter.length() > 0 ? Msg.translate(getCtx(), "LFR_FooterCenterPrefix") + m_headerCenter : "";
	}

	public void setFooterCenter(String text) {
		LfrUtil.add(m_footerCenter, text, " / ");
	}
	
	public String getFooterCenter() {
		return m_footerCenter.length() > 0 ? Msg.translate(getCtx(), "LFR_FooterCenterPrefix") + m_footerCenter : "";
	}
}
