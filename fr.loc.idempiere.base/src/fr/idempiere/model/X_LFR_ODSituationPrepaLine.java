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
/** Generated Model - DO NOT CHANGE */
package fr.idempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for LFR_ODSituationPrepaLine
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="LFR_ODSituationPrepaLine")
public class X_LFR_ODSituationPrepaLine extends PO implements I_LFR_ODSituationPrepaLine, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251029L;

    /** Standard Constructor */
    public X_LFR_ODSituationPrepaLine (Properties ctx, int LFR_ODSituationPrepaLine_ID, String trxName)
    {
      super (ctx, LFR_ODSituationPrepaLine_ID, trxName);
      /** if (LFR_ODSituationPrepaLine_ID == 0)
        {
			setAD_OrgDoc_ID (0);
			setAccount_ID (0);
			setAmt (Env.ZERO);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setIsManual (false);
// N
			setIsSOTrx (false);
// N
			setLFR_FactAcct_Org_ID (0);
			setLFR_IsCompteNonEligible (false);
// N
			setLFR_IsCreditMemo (false);
// N
			setLFR_IsDiffBetweenFactAcctAndSPL (false);
// N
			setLFR_ODSituationPrepaLine_ID (0);
			setLFR_ODSituationPrepa_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = @LFR_ODSituationPrepa_ID@
        } */
    }

    /** Standard Constructor */
    public X_LFR_ODSituationPrepaLine (Properties ctx, int LFR_ODSituationPrepaLine_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_ODSituationPrepaLine_ID, trxName, virtualColumns);
      /** if (LFR_ODSituationPrepaLine_ID == 0)
        {
			setAD_OrgDoc_ID (0);
			setAccount_ID (0);
			setAmt (Env.ZERO);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setIsManual (false);
// N
			setIsSOTrx (false);
// N
			setLFR_FactAcct_Org_ID (0);
			setLFR_IsCompteNonEligible (false);
// N
			setLFR_IsCreditMemo (false);
// N
			setLFR_IsDiffBetweenFactAcctAndSPL (false);
// N
			setLFR_ODSituationPrepaLine_ID (0);
			setLFR_ODSituationPrepa_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = @LFR_ODSituationPrepa_ID@
        } */
    }

    /** Standard Constructor */
    public X_LFR_ODSituationPrepaLine (Properties ctx, String LFR_ODSituationPrepaLine_UU, String trxName)
    {
      super (ctx, LFR_ODSituationPrepaLine_UU, trxName);
      /** if (LFR_ODSituationPrepaLine_UU == null)
        {
			setAD_OrgDoc_ID (0);
			setAccount_ID (0);
			setAmt (Env.ZERO);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setIsManual (false);
// N
			setIsSOTrx (false);
// N
			setLFR_FactAcct_Org_ID (0);
			setLFR_IsCompteNonEligible (false);
// N
			setLFR_IsCreditMemo (false);
// N
			setLFR_IsDiffBetweenFactAcctAndSPL (false);
// N
			setLFR_ODSituationPrepaLine_ID (0);
			setLFR_ODSituationPrepa_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = @LFR_ODSituationPrepa_ID@
        } */
    }

    /** Standard Constructor */
    public X_LFR_ODSituationPrepaLine (Properties ctx, String LFR_ODSituationPrepaLine_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_ODSituationPrepaLine_UU, trxName, virtualColumns);
      /** if (LFR_ODSituationPrepaLine_UU == null)
        {
			setAD_OrgDoc_ID (0);
			setAccount_ID (0);
			setAmt (Env.ZERO);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setIsManual (false);
// N
			setIsSOTrx (false);
// N
			setLFR_FactAcct_Org_ID (0);
			setLFR_IsCompteNonEligible (false);
// N
			setLFR_IsCreditMemo (false);
// N
			setLFR_IsDiffBetweenFactAcctAndSPL (false);
// N
			setLFR_ODSituationPrepaLine_ID (0);
			setLFR_ODSituationPrepa_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_ODSituationPrepaLine WHERE LFR_ODSituationPrepa_ID = @LFR_ODSituationPrepa_ID@
        } */
    }

    /** Load Constructor */
    public X_LFR_ODSituationPrepaLine (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_LFR_ODSituationPrepaLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Document Org.
		@param AD_OrgDoc_ID Document Organization (independent from account organization)
	*/
	public void setAD_OrgDoc_ID (int AD_OrgDoc_ID)
	{
		if (AD_OrgDoc_ID < 1)
			set_Value (COLUMNNAME_AD_OrgDoc_ID, null);
		else
			set_Value (COLUMNNAME_AD_OrgDoc_ID, Integer.valueOf(AD_OrgDoc_ID));
	}

	/** Get Document Org.
		@return Document Organization (independent from account organization)
	  */
	public int getAD_OrgDoc_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgDoc_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getAccount_ID(), get_TrxName());
	}

	/** Set Account.
		@param Account_ID Account used
	*/
	public void setAccount_ID (int Account_ID)
	{
		if (Account_ID < 1)
			set_Value (COLUMNNAME_Account_ID, null);
		else
			set_Value (COLUMNNAME_Account_ID, Integer.valueOf(Account_ID));
	}

	/** Get Account.
		@return Account used
	  */
	public int getAccount_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Amount.
		@param Amt Amount
	*/
	public void setAmt (BigDecimal Amt)
	{
		set_Value (COLUMNNAME_Amt, Amt);
	}

	/** Get Amount.
		@return Amount
	  */
	public BigDecimal getAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Amt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Accounted Amount.
		@param AmtAcct Amount Balance in Currency of Accounting Schema
	*/
	public void setAmtAcct (BigDecimal AmtAcct)
	{
		set_ValueNoCheck (COLUMNNAME_AmtAcct, AmtAcct);
	}

	/** Get Accounted Amount.
		@return Amount Balance in Currency of Accounting Schema
	  */
	public BigDecimal getAmtAcct()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcct);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException
	{
		return (org.compiere.model.I_C_InvoiceLine)MTable.get(getCtx(), org.compiere.model.I_C_InvoiceLine.Table_ID)
			.getPO(getC_InvoiceLine_ID(), get_TrxName());
	}

	/** Set Invoice Line.
		@param C_InvoiceLine_ID Invoice Detail Line
	*/
	public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
	{
		if (C_InvoiceLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_InvoiceLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
	}

	/** Get Invoice Line.
		@return Invoice Detail Line
	  */
	public int getC_InvoiceLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_InvoiceLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
	{
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_ID)
			.getPO(getC_Invoice_ID(), get_TrxName());
	}

	/** Set Invoice.
		@param C_Invoice_ID Invoice Identifier
	*/
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException
	{
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_ID)
			.getPO(getC_Tax_ID(), get_TrxName());
	}

	/** Set Tax.
		@param C_Tax_ID Tax identifier
	*/
	public void setC_Tax_ID (int C_Tax_ID)
	{
		if (C_Tax_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Tax_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
	}

	/** Get Tax.
		@return Tax identifier
	  */
	public int getC_Tax_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_ValueNoCheck (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	public org.compiere.model.I_Fact_Acct getFact_Acct() throws RuntimeException
	{
		return (org.compiere.model.I_Fact_Acct)MTable.get(getCtx(), org.compiere.model.I_Fact_Acct.Table_ID)
			.getPO(getFact_Acct_ID(), get_TrxName());
	}

	/** Set Accounting Fact.
		@param Fact_Acct_ID Accounting Fact
	*/
	public void setFact_Acct_ID (int Fact_Acct_ID)
	{
		if (Fact_Acct_ID < 1)
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Fact_Acct_ID, Integer.valueOf(Fact_Acct_ID));
	}

	/** Get Accounting Fact.
		@return Accounting Fact	  */
	public int getFact_Acct_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Fact_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Manual.
		@param IsManual This is a manual process
	*/
	public void setIsManual (boolean IsManual)
	{
		set_Value (COLUMNNAME_IsManual, Boolean.valueOf(IsManual));
	}

	/** Get Manual.
		@return This is a manual process
	  */
	public boolean isManual()
	{
		Object oo = get_Value(COLUMNNAME_IsManual);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx This is a Sales Transaction
	*/
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_ValueNoCheck (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx()
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_C_ElementValue getLFR_FactAcct_Account() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getLFR_FactAcct_Account_ID(), get_TrxName());
	}

	/** Set LFR_FactAcct_Account_ID.
		@param LFR_FactAcct_Account_ID LFR_FactAcct_Account_ID
	*/
	public void setLFR_FactAcct_Account_ID (int LFR_FactAcct_Account_ID)
	{
		if (LFR_FactAcct_Account_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_FactAcct_Account_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_FactAcct_Account_ID, Integer.valueOf(LFR_FactAcct_Account_ID));
	}

	/** Get LFR_FactAcct_Account_ID.
		@return LFR_FactAcct_Account_ID	  */
	public int getLFR_FactAcct_Account_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_FactAcct_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_FactAcct_AmtAcct.
		@param LFR_FactAcct_AmtAcct LFR_FactAcct_AmtAcct
	*/
	public void setLFR_FactAcct_AmtAcct (BigDecimal LFR_FactAcct_AmtAcct)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_FactAcct_AmtAcct, LFR_FactAcct_AmtAcct);
	}

	/** Get LFR_FactAcct_AmtAcct.
		@return LFR_FactAcct_AmtAcct	  */
	public BigDecimal getLFR_FactAcct_AmtAcct()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_FactAcct_AmtAcct);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_FactAcct_Org_ID.
		@param LFR_FactAcct_Org_ID LFR_FactAcct_Org_ID
	*/
	public void setLFR_FactAcct_Org_ID (int LFR_FactAcct_Org_ID)
	{
		if (LFR_FactAcct_Org_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_FactAcct_Org_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_FactAcct_Org_ID, Integer.valueOf(LFR_FactAcct_Org_ID));
	}

	/** Get LFR_FactAcct_Org_ID.
		@return LFR_FactAcct_Org_ID	  */
	public int getLFR_FactAcct_Org_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_FactAcct_Org_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_ImputationDateDeb.
		@param LFR_ImputationDateDeb LFR_ImputationDateDeb
	*/
	public void setLFR_ImputationDateDeb (Timestamp LFR_ImputationDateDeb)
	{
		set_Value (COLUMNNAME_LFR_ImputationDateDeb, LFR_ImputationDateDeb);
	}

	/** Get LFR_ImputationDateDeb.
		@return LFR_ImputationDateDeb	  */
	public Timestamp getLFR_ImputationDateDeb()
	{
		return (Timestamp)get_Value(COLUMNNAME_LFR_ImputationDateDeb);
	}

	/** Set LFR_ImputationDateFin.
		@param LFR_ImputationDateFin LFR_ImputationDateFin
	*/
	public void setLFR_ImputationDateFin (Timestamp LFR_ImputationDateFin)
	{
		set_Value (COLUMNNAME_LFR_ImputationDateFin, LFR_ImputationDateFin);
	}

	/** Get LFR_ImputationDateFin.
		@return LFR_ImputationDateFin	  */
	public Timestamp getLFR_ImputationDateFin()
	{
		return (Timestamp)get_Value(COLUMNNAME_LFR_ImputationDateFin);
	}

	/** Set LFR_IsCompteNonEligible.
		@param LFR_IsCompteNonEligible LFR_IsCompteNonEligible
	*/
	public void setLFR_IsCompteNonEligible (boolean LFR_IsCompteNonEligible)
	{
		set_Value (COLUMNNAME_LFR_IsCompteNonEligible, Boolean.valueOf(LFR_IsCompteNonEligible));
	}

	/** Get LFR_IsCompteNonEligible.
		@return LFR_IsCompteNonEligible	  */
	public boolean isLFR_IsCompteNonEligible()
	{
		Object oo = get_Value(COLUMNNAME_LFR_IsCompteNonEligible);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LFR_IsCreditMemo.
		@param LFR_IsCreditMemo LFR_IsCreditMemo
	*/
	public void setLFR_IsCreditMemo (boolean LFR_IsCreditMemo)
	{
		set_Value (COLUMNNAME_LFR_IsCreditMemo, Boolean.valueOf(LFR_IsCreditMemo));
	}

	/** Get LFR_IsCreditMemo.
		@return LFR_IsCreditMemo	  */
	public boolean isLFR_IsCreditMemo()
	{
		Object oo = get_Value(COLUMNNAME_LFR_IsCreditMemo);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LFR_IsDiffBetweenFactAcctAndSPL.
		@param LFR_IsDiffBetweenFactAcctAndSPL LFR_IsDiffBetweenFactAcctAndSPL
	*/
	public void setLFR_IsDiffBetweenFactAcctAndSPL (boolean LFR_IsDiffBetweenFactAcctAndSPL)
	{
		set_Value (COLUMNNAME_LFR_IsDiffBetweenFactAcctAndSPL, Boolean.valueOf(LFR_IsDiffBetweenFactAcctAndSPL));
	}

	/** Get LFR_IsDiffBetweenFactAcctAndSPL.
		@return LFR_IsDiffBetweenFactAcctAndSPL	  */
	public boolean isLFR_IsDiffBetweenFactAcctAndSPL()
	{
		Object oo = get_Value(COLUMNNAME_LFR_IsDiffBetweenFactAcctAndSPL);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LFR_ODSituationPrepaLine.
		@param LFR_ODSituationPrepaLine_ID LFR_ODSituationPrepaLine
	*/
	public void setLFR_ODSituationPrepaLine_ID (int LFR_ODSituationPrepaLine_ID)
	{
		if (LFR_ODSituationPrepaLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_ODSituationPrepaLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_ODSituationPrepaLine_ID, Integer.valueOf(LFR_ODSituationPrepaLine_ID));
	}

	/** Get LFR_ODSituationPrepaLine.
		@return LFR_ODSituationPrepaLine	  */
	public int getLFR_ODSituationPrepaLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_ODSituationPrepaLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_ODSituationPrepaLine_UU.
		@param LFR_ODSituationPrepaLine_UU LFR_ODSituationPrepaLine_UU
	*/
	public void setLFR_ODSituationPrepaLine_UU (String LFR_ODSituationPrepaLine_UU)
	{
		set_Value (COLUMNNAME_LFR_ODSituationPrepaLine_UU, LFR_ODSituationPrepaLine_UU);
	}

	/** Get LFR_ODSituationPrepaLine_UU.
		@return LFR_ODSituationPrepaLine_UU	  */
	public String getLFR_ODSituationPrepaLine_UU()
	{
		return (String)get_Value(COLUMNNAME_LFR_ODSituationPrepaLine_UU);
	}

	public I_LFR_ODSituationPrepa getLFR_ODSituationPrepa() throws RuntimeException
	{
		return (I_LFR_ODSituationPrepa)MTable.get(getCtx(), I_LFR_ODSituationPrepa.Table_ID)
			.getPO(getLFR_ODSituationPrepa_ID(), get_TrxName());
	}

	/** Set LFR_ODSituationPrepa.
		@param LFR_ODSituationPrepa_ID LFR_ODSituationPrepa
	*/
	public void setLFR_ODSituationPrepa_ID (int LFR_ODSituationPrepa_ID)
	{
		if (LFR_ODSituationPrepa_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_ODSituationPrepa_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_ODSituationPrepa_ID, Integer.valueOf(LFR_ODSituationPrepa_ID));
	}

	/** Get LFR_ODSituationPrepa.
		@return LFR_ODSituationPrepa	  */
	public int getLFR_ODSituationPrepa_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_ODSituationPrepa_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line Description.
		@param LineDescription Description of the Line
	*/
	public void setLineDescription (String LineDescription)
	{
		set_ValueNoCheck (COLUMNNAME_LineDescription, LineDescription);
	}

	/** Get Line Description.
		@return Description of the Line
	  */
	public String getLineDescription()
	{
		return (String)get_Value(COLUMNNAME_LineDescription);
	}

	/** Set Tax Amount.
		@param TaxAmt Tax Amount for a document
	*/
	public void setTaxAmt (BigDecimal TaxAmt)
	{
		set_ValueNoCheck (COLUMNNAME_TaxAmt, TaxAmt);
	}

	/** Get Tax Amount.
		@return Tax Amount for a document
	  */
	public BigDecimal getTaxAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TaxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** CCA = A */
	public static final String TYPE_CCA = "A";
	/** CAP = P */
	public static final String TYPE_CAP = "P";
	/** Set Type.
		@param Type Type of Validation (SQL, Java Script, Java Language)
	*/
	public void setType (String Type)
	{

		set_ValueNoCheck (COLUMNNAME_Type, Type);
	}

	/** Get Type.
		@return Type of Validation (SQL, Java Script, Java Language)
	  */
	public String getType()
	{
		return (String)get_Value(COLUMNNAME_Type);
	}
}