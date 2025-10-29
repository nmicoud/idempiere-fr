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

/** Generated Interface for LFR_ODSituationPrepaLine
 *  @author iDempiere (generated) 
 *  @version Release 12
 */
@SuppressWarnings("all")
public interface I_LFR_ODSituationPrepaLine 
{

    /** TableName=LFR_ODSituationPrepaLine */
    public static final String Table_Name = "LFR_ODSituationPrepaLine";

    /** AD_Table_ID=1000007 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_OrgDoc_ID */
    public static final String COLUMNNAME_AD_OrgDoc_ID = "AD_OrgDoc_ID";

	/** Set Document Org.
	  * Document Organization (independent from account organization)
	  */
	public void setAD_OrgDoc_ID (int AD_OrgDoc_ID);

	/** Get Document Org.
	  * Document Organization (independent from account organization)
	  */
	public int getAD_OrgDoc_ID();

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

    /** Column name Account_ID */
    public static final String COLUMNNAME_Account_ID = "Account_ID";

	/** Set Account.
	  * Account used
	  */
	public void setAccount_ID (int Account_ID);

	/** Get Account.
	  * Account used
	  */
	public int getAccount_ID();

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException;

    /** Column name Amt */
    public static final String COLUMNNAME_Amt = "Amt";

	/** Set Amount.
	  * Amount
	  */
	public void setAmt (BigDecimal Amt);

	/** Get Amount.
	  * Amount
	  */
	public BigDecimal getAmt();

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

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner.
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner.
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_InvoiceLine_ID */
    public static final String COLUMNNAME_C_InvoiceLine_ID = "C_InvoiceLine_ID";

	/** Set Invoice Line.
	  * Invoice Detail Line
	  */
	public void setC_InvoiceLine_ID (int C_InvoiceLine_ID);

	/** Get Invoice Line.
	  * Invoice Detail Line
	  */
	public int getC_InvoiceLine_ID();

	public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException;

    /** Column name C_Invoice_ID */
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";

	/** Set Invoice.
	  * Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID);

	/** Get Invoice.
	  * Invoice Identifier
	  */
	public int getC_Invoice_ID();

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException;

    /** Column name C_Tax_ID */
    public static final String COLUMNNAME_C_Tax_ID = "C_Tax_ID";

	/** Set Tax.
	  * Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID);

	/** Get Tax.
	  * Tax identifier
	  */
	public int getC_Tax_ID();

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException;

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

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsManual */
    public static final String COLUMNNAME_IsManual = "IsManual";

	/** Set Manual.
	  * This is a manual process
	  */
	public void setIsManual (boolean IsManual);

	/** Get Manual.
	  * This is a manual process
	  */
	public boolean isManual();

    /** Column name IsSOTrx */
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";

	/** Set Sales Transaction.
	  * This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx);

	/** Get Sales Transaction.
	  * This is a Sales Transaction
	  */
	public boolean isSOTrx();

    /** Column name LFR_FactAcct_Account_ID */
    public static final String COLUMNNAME_LFR_FactAcct_Account_ID = "LFR_FactAcct_Account_ID";

	/** Set LFR_FactAcct_Account_ID	  */
	public void setLFR_FactAcct_Account_ID (int LFR_FactAcct_Account_ID);

	/** Get LFR_FactAcct_Account_ID	  */
	public int getLFR_FactAcct_Account_ID();

	public org.compiere.model.I_C_ElementValue getLFR_FactAcct_Account() throws RuntimeException;

    /** Column name LFR_FactAcct_AmtAcct */
    public static final String COLUMNNAME_LFR_FactAcct_AmtAcct = "LFR_FactAcct_AmtAcct";

	/** Set LFR_FactAcct_AmtAcct	  */
	public void setLFR_FactAcct_AmtAcct (BigDecimal LFR_FactAcct_AmtAcct);

	/** Get LFR_FactAcct_AmtAcct	  */
	public BigDecimal getLFR_FactAcct_AmtAcct();

    /** Column name LFR_FactAcct_Org_ID */
    public static final String COLUMNNAME_LFR_FactAcct_Org_ID = "LFR_FactAcct_Org_ID";

	/** Set LFR_FactAcct_Org_ID	  */
	public void setLFR_FactAcct_Org_ID (int LFR_FactAcct_Org_ID);

	/** Get LFR_FactAcct_Org_ID	  */
	public int getLFR_FactAcct_Org_ID();

    /** Column name LFR_ImputationDateDeb */
    public static final String COLUMNNAME_LFR_ImputationDateDeb = "LFR_ImputationDateDeb";

	/** Set LFR_ImputationDateDeb	  */
	public void setLFR_ImputationDateDeb (Timestamp LFR_ImputationDateDeb);

	/** Get LFR_ImputationDateDeb	  */
	public Timestamp getLFR_ImputationDateDeb();

    /** Column name LFR_ImputationDateFin */
    public static final String COLUMNNAME_LFR_ImputationDateFin = "LFR_ImputationDateFin";

	/** Set LFR_ImputationDateFin	  */
	public void setLFR_ImputationDateFin (Timestamp LFR_ImputationDateFin);

	/** Get LFR_ImputationDateFin	  */
	public Timestamp getLFR_ImputationDateFin();

    /** Column name LFR_IsCompteNonEligible */
    public static final String COLUMNNAME_LFR_IsCompteNonEligible = "LFR_IsCompteNonEligible";

	/** Set LFR_IsCompteNonEligible	  */
	public void setLFR_IsCompteNonEligible (boolean LFR_IsCompteNonEligible);

	/** Get LFR_IsCompteNonEligible	  */
	public boolean isLFR_IsCompteNonEligible();

    /** Column name LFR_IsCreditMemo */
    public static final String COLUMNNAME_LFR_IsCreditMemo = "LFR_IsCreditMemo";

	/** Set LFR_IsCreditMemo	  */
	public void setLFR_IsCreditMemo (boolean LFR_IsCreditMemo);

	/** Get LFR_IsCreditMemo	  */
	public boolean isLFR_IsCreditMemo();

    /** Column name LFR_IsDiffBetweenFactAcctAndSPL */
    public static final String COLUMNNAME_LFR_IsDiffBetweenFactAcctAndSPL = "LFR_IsDiffBetweenFactAcctAndSPL";

	/** Set LFR_IsDiffBetweenFactAcctAndSPL	  */
	public void setLFR_IsDiffBetweenFactAcctAndSPL (boolean LFR_IsDiffBetweenFactAcctAndSPL);

	/** Get LFR_IsDiffBetweenFactAcctAndSPL	  */
	public boolean isLFR_IsDiffBetweenFactAcctAndSPL();

    /** Column name LFR_ODSituationPrepaLine_ID */
    public static final String COLUMNNAME_LFR_ODSituationPrepaLine_ID = "LFR_ODSituationPrepaLine_ID";

	/** Set LFR_ODSituationPrepaLine	  */
	public void setLFR_ODSituationPrepaLine_ID (int LFR_ODSituationPrepaLine_ID);

	/** Get LFR_ODSituationPrepaLine	  */
	public int getLFR_ODSituationPrepaLine_ID();

    /** Column name LFR_ODSituationPrepaLine_UU */
    public static final String COLUMNNAME_LFR_ODSituationPrepaLine_UU = "LFR_ODSituationPrepaLine_UU";

	/** Set LFR_ODSituationPrepaLine_UU	  */
	public void setLFR_ODSituationPrepaLine_UU (String LFR_ODSituationPrepaLine_UU);

	/** Get LFR_ODSituationPrepaLine_UU	  */
	public String getLFR_ODSituationPrepaLine_UU();

    /** Column name LFR_ODSituationPrepa_ID */
    public static final String COLUMNNAME_LFR_ODSituationPrepa_ID = "LFR_ODSituationPrepa_ID";

	/** Set LFR_ODSituationPrepa	  */
	public void setLFR_ODSituationPrepa_ID (int LFR_ODSituationPrepa_ID);

	/** Get LFR_ODSituationPrepa	  */
	public int getLFR_ODSituationPrepa_ID();

	public I_LFR_ODSituationPrepa getLFR_ODSituationPrepa() throws RuntimeException;

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

    /** Column name LineDescription */
    public static final String COLUMNNAME_LineDescription = "LineDescription";

	/** Set Line Description.
	  * Description of the Line
	  */
	public void setLineDescription (String LineDescription);

	/** Get Line Description.
	  * Description of the Line
	  */
	public String getLineDescription();

    /** Column name TaxAmt */
    public static final String COLUMNNAME_TaxAmt = "TaxAmt";

	/** Set Tax Amount.
	  * Tax Amount for a document
	  */
	public void setTaxAmt (BigDecimal TaxAmt);

	/** Get Tax Amount.
	  * Tax Amount for a document
	  */
	public BigDecimal getTaxAmt();

    /** Column name Type */
    public static final String COLUMNNAME_Type = "Type";

	/** Set Type.
	  * Type of Validation (SQL, Java Script, Java Language)
	  */
	public void setType (String Type);

	/** Get Type.
	  * Type of Validation (SQL, Java Script, Java Language)
	  */
	public String getType();

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
