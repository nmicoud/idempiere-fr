/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package fr.idempiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for T_LFR_Report
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_T_LFR_Report 
{

    /** TableName=T_LFR_Report */
    public static final String Table_Name = "T_LFR_Report";

    /** AD_Table_ID=1000007 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

    /** Load Meta Data */

    /** Column name Account_Name */
    public static final String COLUMNNAME_Account_Name = "Account_Name";

	/** Set Account Name	  */
	public void setAccount_Name (String Account_Name);

	/** Get Account Name	  */
	public String getAccount_Name();

    /** Column name AccountValue */
    public static final String COLUMNNAME_AccountValue = "AccountValue";

	/** Set Account Key.
	  * Key of Account Element
	  */
	public void setAccountValue (String AccountValue);

	/** Get Account Key.
	  * Key of Account Element
	  */
	public String getAccountValue();

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name AD_PInstance_ID */
    public static final String COLUMNNAME_AD_PInstance_ID = "AD_PInstance_ID";

	/** Set Process Instance.
	  * Instance of the process
	  */
	public void setAD_PInstance_ID (int AD_PInstance_ID);

	/** Get Process Instance.
	  * Instance of the process
	  */
	public int getAD_PInstance_ID();

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException;

    /** Column name AmtAcct */
    public static final String COLUMNNAME_AmtAcct = "AmtAcct";

	/** Set Accounted Amount.
	  * Amount Balance in Currency of Accounting Schema
	  */
	public void setAmtAcct (BigDecimal AmtAcct);

	/** Get Accounted Amount.
	  * Amount Balance in Currency of Accounting Schema
	  */
	public BigDecimal getAmtAcct();

    /** Column name AmtAcctCr */
    public static final String COLUMNNAME_AmtAcctCr = "AmtAcctCr";

	/** Set Accounted Credit.
	  * Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr);

	/** Get Accounted Credit.
	  * Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr();

    /** Column name AmtAcctDr */
    public static final String COLUMNNAME_AmtAcctDr = "AmtAcctDr";

	/** Set Accounted Debit.
	  * Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr);

	/** Get Accounted Debit.
	  * Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr();

    /** Column name BPName */
    public static final String COLUMNNAME_BPName = "BPName";

	/** Set BP Name	  */
	public void setBPName (String BPName);

	/** Get BP Name	  */
	public String getBPName();

    /** Column name C_ElementValue_ID */
    public static final String COLUMNNAME_C_ElementValue_ID = "C_ElementValue_ID";

	/** Set Account Element.
	  * Account Element
	  */
	public void setC_ElementValue_ID (int C_ElementValue_ID);

	/** Get Account Element.
	  * Account Element
	  */
	public int getC_ElementValue_ID();

	public org.compiere.model.I_C_ElementValue getC_ElementValue() throws RuntimeException;

    /** Column name ClientName */
    public static final String COLUMNNAME_ClientName = "ClientName";

	/** Set Tenant Name	  */
	public void setClientName (String ClientName);

	/** Get Tenant Name	  */
	public String getClientName();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name Fact_Acct_ID */
    public static final String COLUMNNAME_Fact_Acct_ID = "Fact_Acct_ID";

	/** Set Accounting Fact	  */
	public void setFact_Acct_ID (int Fact_Acct_ID);

	/** Get Accounting Fact	  */
	public int getFact_Acct_ID();

	public org.compiere.model.I_Fact_Acct getFact_Acct() throws RuntimeException;

    /** Column name FooterCenter */
    public static final String COLUMNNAME_FooterCenter = "FooterCenter";

	/** Set Footer Center.
	  * Content of the center portion of the footer.
	  */
	public void setFooterCenter (String FooterCenter);

	/** Get Footer Center.
	  * Content of the center portion of the footer.
	  */
	public String getFooterCenter();

    /** Column name GL_Category_ID */
    public static final String COLUMNNAME_GL_Category_ID = "GL_Category_ID";

	/** Set GL Category.
	  * General Ledger Category
	  */
	public void setGL_Category_ID (int GL_Category_ID);

	/** Get GL Category.
	  * General Ledger Category
	  */
	public int getGL_Category_ID();

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException;

    /** Column name IsSummary */
    public static final String COLUMNNAME_IsSummary = "IsSummary";

	/** Set Summary Level.
	  * This is a summary entity
	  */
	public void setIsSummary (boolean IsSummary);

	/** Get Summary Level.
	  * This is a summary entity
	  */
	public boolean isSummary();

    /** Column name LFR_AmtAcctPrec */
    public static final String COLUMNNAME_LFR_AmtAcctPrec = "LFR_AmtAcctPrec";

	/** Set LFR_AmtAcctPrec	  */
	public void setLFR_AmtAcctPrec (BigDecimal LFR_AmtAcctPrec);

	/** Get LFR_AmtAcctPrec	  */
	public BigDecimal getLFR_AmtAcctPrec();

    /** Column name LFR_AmtAcctPrecCr */
    public static final String COLUMNNAME_LFR_AmtAcctPrecCr = "LFR_AmtAcctPrecCr";

	/** Set LFR_AmtAcctPrecCr	  */
	public void setLFR_AmtAcctPrecCr (BigDecimal LFR_AmtAcctPrecCr);

	/** Get LFR_AmtAcctPrecCr	  */
	public BigDecimal getLFR_AmtAcctPrecCr();

    /** Column name LFR_AmtAcctPrecDr */
    public static final String COLUMNNAME_LFR_AmtAcctPrecDr = "LFR_AmtAcctPrecDr";

	/** Set LFR_AmtAcctPrecDr	  */
	public void setLFR_AmtAcctPrecDr (BigDecimal LFR_AmtAcctPrecDr);

	/** Get LFR_AmtAcctPrecDr	  */
	public BigDecimal getLFR_AmtAcctPrecDr();

    /** Column name LFR_BalanceGeneRegrLevel */
    public static final String COLUMNNAME_LFR_BalanceGeneRegrLevel = "LFR_BalanceGeneRegrLevel";

	/** Set LFR_BalanceGeneRegrLevel	  */
	public void setLFR_BalanceGeneRegrLevel (String LFR_BalanceGeneRegrLevel);

	/** Get LFR_BalanceGeneRegrLevel	  */
	public String getLFR_BalanceGeneRegrLevel();

    /** Column name LFR_CL1 */
    public static final String COLUMNNAME_LFR_CL1 = "LFR_CL1";

	/** Set LFR_CL1	  */
	public void setLFR_CL1 (String LFR_CL1);

	/** Get LFR_CL1	  */
	public String getLFR_CL1();

    /** Column name LFR_CL2 */
    public static final String COLUMNNAME_LFR_CL2 = "LFR_CL2";

	/** Set LFR_CL2	  */
	public void setLFR_CL2 (String LFR_CL2);

	/** Get LFR_CL2	  */
	public String getLFR_CL2();

    /** Column name LFR_CL3 */
    public static final String COLUMNNAME_LFR_CL3 = "LFR_CL3";

	/** Set LFR_CL3	  */
	public void setLFR_CL3 (String LFR_CL3);

	/** Get LFR_CL3	  */
	public String getLFR_CL3();

    /** Column name LFR_DateAsString */
    public static final String COLUMNNAME_LFR_DateAsString = "LFR_DateAsString";

	/** Set LFR_DateAsString	  */
	public void setLFR_DateAsString (String LFR_DateAsString);

	/** Get LFR_DateAsString	  */
	public String getLFR_DateAsString();

    /** Column name LFR_FactAcctOrg */
    public static final String COLUMNNAME_LFR_FactAcctOrg = "LFR_FactAcctOrg";

	/** Set LFR_FactAcctOrg	  */
	public void setLFR_FactAcctOrg (String LFR_FactAcctOrg);

	/** Get LFR_FactAcctOrg	  */
	public String getLFR_FactAcctOrg();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name OrgName */
    public static final String COLUMNNAME_OrgName = "OrgName";

	/** Set Organization Name.
	  * Name of the Organization
	  */
	public void setOrgName (String OrgName);

	/** Get Organization Name.
	  * Name of the Organization
	  */
	public String getOrgName();

    /** Column name PrintName */
    public static final String COLUMNNAME_PrintName = "PrintName";

	/** Set Print Text.
	  * The label text to be printed on a document or correspondence.
	  */
	public void setPrintName (String PrintName);

	/** Get Print Text.
	  * The label text to be printed on a document or correspondence.
	  */
	public String getPrintName();

    /** Column name T_LFR_Report_ID */
    public static final String COLUMNNAME_T_LFR_Report_ID = "T_LFR_Report_ID";

	/** Set T_LFR_Report	  */
	public void setT_LFR_Report_ID (int T_LFR_Report_ID);

	/** Get T_LFR_Report	  */
	public int getT_LFR_Report_ID();

    /** Column name Title */
    public static final String COLUMNNAME_Title = "Title";

	/** Set Title.
	  * Name this entity is referred to as
	  */
	public void setTitle (String Title);

	/** Get Title.
	  * Name this entity is referred to as
	  */
	public String getTitle();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}
