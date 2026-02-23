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

/** Generated Model for T_LFR_Report
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="T_LFR_Report")
public class X_T_LFR_Report extends PO implements I_T_LFR_Report, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20260223L;

    /** Standard Constructor */
    public X_T_LFR_Report (Properties ctx, int T_LFR_Report_ID, String trxName)
    {
      super (ctx, T_LFR_Report_ID, trxName);
      /** if (T_LFR_Report_ID == 0)
        {
			setIsSummary (false);
// N
			setT_LFR_Report_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_T_LFR_Report (Properties ctx, int T_LFR_Report_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, T_LFR_Report_ID, trxName, virtualColumns);
      /** if (T_LFR_Report_ID == 0)
        {
			setIsSummary (false);
// N
			setT_LFR_Report_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_T_LFR_Report (Properties ctx, String T_LFR_Report_UU, String trxName)
    {
      super (ctx, T_LFR_Report_UU, trxName);
      /** if (T_LFR_Report_UU == null)
        {
			setIsSummary (false);
// N
			setT_LFR_Report_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_T_LFR_Report (Properties ctx, String T_LFR_Report_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, T_LFR_Report_UU, trxName, virtualColumns);
      /** if (T_LFR_Report_UU == null)
        {
			setIsSummary (false);
// N
			setT_LFR_Report_ID (0);
        } */
    }

    /** Load Constructor */
    public X_T_LFR_Report (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org
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
      StringBuilder sb = new StringBuilder ("X_T_LFR_Report[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_PInstance getAD_PInstance() throws RuntimeException
	{
		return (org.compiere.model.I_AD_PInstance)MTable.get(getCtx(), org.compiere.model.I_AD_PInstance.Table_ID)
			.getPO(getAD_PInstance_ID(), get_TrxName());
	}

	/** Set Process Instance.
		@param AD_PInstance_ID Instance of the process
	*/
	public void setAD_PInstance_ID (int AD_PInstance_ID)
	{
		if (AD_PInstance_ID < 1)
			set_Value (COLUMNNAME_AD_PInstance_ID, null);
		else
			set_Value (COLUMNNAME_AD_PInstance_ID, Integer.valueOf(AD_PInstance_ID));
	}

	/** Get Process Instance.
		@return Instance of the process
	  */
	public int getAD_PInstance_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_PInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Key.
		@param AccountValue Key of Account Element
	*/
	public void setAccountValue (String AccountValue)
	{
		set_ValueNoCheck (COLUMNNAME_AccountValue, AccountValue);
	}

	/** Get Account Key.
		@return Key of Account Element
	  */
	public String getAccountValue()
	{
		return (String)get_Value(COLUMNNAME_AccountValue);
	}

	/** Set Account Name.
		@param Account_Name Account Name
	*/
	public void setAccount_Name (String Account_Name)
	{
		set_ValueNoCheck (COLUMNNAME_Account_Name, Account_Name);
	}

	/** Get Account Name.
		@return Account Name	  */
	public String getAccount_Name()
	{
		return (String)get_Value(COLUMNNAME_Account_Name);
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

	/** Set Accounted Credit.
		@param AmtAcctCr Accounted Credit Amount
	*/
	public void setAmtAcctCr (BigDecimal AmtAcctCr)
	{
		set_ValueNoCheck (COLUMNNAME_AmtAcctCr, AmtAcctCr);
	}

	/** Get Accounted Credit.
		@return Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Accounted Debit.
		@param AmtAcctDr Accounted Debit Amount
	*/
	public void setAmtAcctDr (BigDecimal AmtAcctDr)
	{
		set_ValueNoCheck (COLUMNNAME_AmtAcctDr, AmtAcctDr);
	}

	/** Get Accounted Debit.
		@return Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set BP Name.
		@param BPName BP Name
	*/
	public void setBPName (String BPName)
	{
		set_Value (COLUMNNAME_BPName, BPName);
	}

	/** Get BP Name.
		@return BP Name	  */
	public String getBPName()
	{
		return (String)get_Value(COLUMNNAME_BPName);
	}

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException
	{
		return (org.compiere.model.I_C_AcctSchema)MTable.get(getCtx(), org.compiere.model.I_C_AcctSchema.Table_ID)
			.getPO(getC_AcctSchema_ID(), get_TrxName());
	}

	/** Set Accounting Schema.
		@param C_AcctSchema_ID Rules for accounting
	*/
	public void setC_AcctSchema_ID (int C_AcctSchema_ID)
	{
		if (C_AcctSchema_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
	}

	/** Get Accounting Schema.
		@return Rules for accounting
	  */
	public int getC_AcctSchema_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_AcctSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_C_ElementValue getC_ElementValue() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getC_ElementValue_ID(), get_TrxName());
	}

	/** Set Account Element.
		@param C_ElementValue_ID Account Element
	*/
	public void setC_ElementValue_ID (int C_ElementValue_ID)
	{
		if (C_ElementValue_ID < 1)
			set_Value (COLUMNNAME_C_ElementValue_ID, null);
		else
			set_Value (COLUMNNAME_C_ElementValue_ID, Integer.valueOf(C_ElementValue_ID));
	}

	/** Get Account Element.
		@return Account Element
	  */
	public int getC_ElementValue_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ElementValue_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Tenant Name.
		@param ClientName Tenant Name
	*/
	public void setClientName (String ClientName)
	{
		set_ValueNoCheck (COLUMNNAME_ClientName, ClientName);
	}

	/** Get Tenant Name.
		@return Tenant Name	  */
	public String getClientName()
	{
		return (String)get_Value(COLUMNNAME_ClientName);
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

	/** Set Document Type Name.
		@param DocTypeName Name of the Document Type
	*/
	public void setDocTypeName (String DocTypeName)
	{
		set_Value (COLUMNNAME_DocTypeName, DocTypeName);
	}

	/** Get Document Type Name.
		@return Name of the Document Type
	  */
	public String getDocTypeName()
	{
		return (String)get_Value(COLUMNNAME_DocTypeName);
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

	/** Set Footer Center.
		@param FooterCenter Content of the center portion of the footer.
	*/
	public void setFooterCenter (String FooterCenter)
	{
		set_Value (COLUMNNAME_FooterCenter, FooterCenter);
	}

	/** Get Footer Center.
		@return Content of the center portion of the footer.
	  */
	public String getFooterCenter()
	{
		return (String)get_Value(COLUMNNAME_FooterCenter);
	}

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException
	{
		return (org.compiere.model.I_GL_Category)MTable.get(getCtx(), org.compiere.model.I_GL_Category.Table_ID)
			.getPO(getGL_Category_ID(), get_TrxName());
	}

	/** Set GL Category.
		@param GL_Category_ID General Ledger Category
	*/
	public void setGL_Category_ID (int GL_Category_ID)
	{
		if (GL_Category_ID < 1)
			set_ValueNoCheck (COLUMNNAME_GL_Category_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_GL_Category_ID, Integer.valueOf(GL_Category_ID));
	}

	/** Get GL Category.
		@return General Ledger Category
	  */
	public int getGL_Category_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Header Center.
		@param HeaderCenter Content of the center portion of the header.
	*/
	public void setHeaderCenter (String HeaderCenter)
	{
		set_Value (COLUMNNAME_HeaderCenter, HeaderCenter);
	}

	/** Get Header Center.
		@return Content of the center portion of the header.
	  */
	public String getHeaderCenter()
	{
		return (String)get_Value(COLUMNNAME_HeaderCenter);
	}

	/** Set Summary Level.
		@param IsSummary This is a summary entity
	*/
	public void setIsSummary (boolean IsSummary)
	{
		set_Value (COLUMNNAME_IsSummary, Boolean.valueOf(IsSummary));
	}

	/** Get Summary Level.
		@return This is a summary entity
	  */
	public boolean isSummary()
	{
		Object oo = get_Value(COLUMNNAME_IsSummary);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LFR_AmtAcctPrec.
		@param LFR_AmtAcctPrec LFR_AmtAcctPrec
	*/
	public void setLFR_AmtAcctPrec (BigDecimal LFR_AmtAcctPrec)
	{
		set_Value (COLUMNNAME_LFR_AmtAcctPrec, LFR_AmtAcctPrec);
	}

	/** Get LFR_AmtAcctPrec.
		@return LFR_AmtAcctPrec	  */
	public BigDecimal getLFR_AmtAcctPrec()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtAcctPrec);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtAcctPrecCr.
		@param LFR_AmtAcctPrecCr LFR_AmtAcctPrecCr
	*/
	public void setLFR_AmtAcctPrecCr (BigDecimal LFR_AmtAcctPrecCr)
	{
		set_Value (COLUMNNAME_LFR_AmtAcctPrecCr, LFR_AmtAcctPrecCr);
	}

	/** Get LFR_AmtAcctPrecCr.
		@return LFR_AmtAcctPrecCr	  */
	public BigDecimal getLFR_AmtAcctPrecCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtAcctPrecCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtAcctPrecDr.
		@param LFR_AmtAcctPrecDr LFR_AmtAcctPrecDr
	*/
	public void setLFR_AmtAcctPrecDr (BigDecimal LFR_AmtAcctPrecDr)
	{
		set_Value (COLUMNNAME_LFR_AmtAcctPrecDr, LFR_AmtAcctPrecDr);
	}

	/** Get LFR_AmtAcctPrecDr.
		@return LFR_AmtAcctPrecDr	  */
	public BigDecimal getLFR_AmtAcctPrecDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtAcctPrecDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtDebutCr.
		@param LFR_AmtDebutCr LFR_AmtDebutCr
	*/
	public void setLFR_AmtDebutCr (BigDecimal LFR_AmtDebutCr)
	{
		set_Value (COLUMNNAME_LFR_AmtDebutCr, LFR_AmtDebutCr);
	}

	/** Get LFR_AmtDebutCr.
		@return LFR_AmtDebutCr	  */
	public BigDecimal getLFR_AmtDebutCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtDebutCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtDebutDr.
		@param LFR_AmtDebutDr LFR_AmtDebutDr
	*/
	public void setLFR_AmtDebutDr (BigDecimal LFR_AmtDebutDr)
	{
		set_Value (COLUMNNAME_LFR_AmtDebutDr, LFR_AmtDebutDr);
	}

	/** Get LFR_AmtDebutDr.
		@return LFR_AmtDebutDr	  */
	public BigDecimal getLFR_AmtDebutDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtDebutDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtFinalCr.
		@param LFR_AmtFinalCr LFR_AmtFinalCr
	*/
	public void setLFR_AmtFinalCr (BigDecimal LFR_AmtFinalCr)
	{
		set_Value (COLUMNNAME_LFR_AmtFinalCr, LFR_AmtFinalCr);
	}

	/** Get LFR_AmtFinalCr.
		@return LFR_AmtFinalCr	  */
	public BigDecimal getLFR_AmtFinalCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtFinalCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtFinalDr.
		@param LFR_AmtFinalDr LFR_AmtFinalDr
	*/
	public void setLFR_AmtFinalDr (BigDecimal LFR_AmtFinalDr)
	{
		set_Value (COLUMNNAME_LFR_AmtFinalDr, LFR_AmtFinalDr);
	}

	/** Get LFR_AmtFinalDr.
		@return LFR_AmtFinalDr	  */
	public BigDecimal getLFR_AmtFinalDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtFinalDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtPeriodeDef.
		@param LFR_AmtPeriodeDef LFR_AmtPeriodeDef
	*/
	public void setLFR_AmtPeriodeDef (BigDecimal LFR_AmtPeriodeDef)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_AmtPeriodeDef, LFR_AmtPeriodeDef);
	}

	/** Get LFR_AmtPeriodeDef.
		@return LFR_AmtPeriodeDef	  */
	public BigDecimal getLFR_AmtPeriodeDef()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtPeriodeDef);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtPeriodeDefCr.
		@param LFR_AmtPeriodeDefCr LFR_AmtPeriodeDefCr
	*/
	public void setLFR_AmtPeriodeDefCr (BigDecimal LFR_AmtPeriodeDefCr)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_AmtPeriodeDefCr, LFR_AmtPeriodeDefCr);
	}

	/** Get LFR_AmtPeriodeDefCr.
		@return LFR_AmtPeriodeDefCr	  */
	public BigDecimal getLFR_AmtPeriodeDefCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtPeriodeDefCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtPeriodeDefDr.
		@param LFR_AmtPeriodeDefDr LFR_AmtPeriodeDefDr
	*/
	public void setLFR_AmtPeriodeDefDr (BigDecimal LFR_AmtPeriodeDefDr)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_AmtPeriodeDefDr, LFR_AmtPeriodeDefDr);
	}

	/** Get LFR_AmtPeriodeDefDr.
		@return LFR_AmtPeriodeDefDr	  */
	public BigDecimal getLFR_AmtPeriodeDefDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtPeriodeDefDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtPeriodeTemp.
		@param LFR_AmtPeriodeTemp LFR_AmtPeriodeTemp
	*/
	public void setLFR_AmtPeriodeTemp (BigDecimal LFR_AmtPeriodeTemp)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_AmtPeriodeTemp, LFR_AmtPeriodeTemp);
	}

	/** Get LFR_AmtPeriodeTemp.
		@return LFR_AmtPeriodeTemp	  */
	public BigDecimal getLFR_AmtPeriodeTemp()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtPeriodeTemp);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtPeriodeTempCr.
		@param LFR_AmtPeriodeTempCr LFR_AmtPeriodeTempCr
	*/
	public void setLFR_AmtPeriodeTempCr (BigDecimal LFR_AmtPeriodeTempCr)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_AmtPeriodeTempCr, LFR_AmtPeriodeTempCr);
	}

	/** Get LFR_AmtPeriodeTempCr.
		@return LFR_AmtPeriodeTempCr	  */
	public BigDecimal getLFR_AmtPeriodeTempCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtPeriodeTempCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_AmtPeriodeTempDr.
		@param LFR_AmtPeriodeTempDr LFR_AmtPeriodeTempDr
	*/
	public void setLFR_AmtPeriodeTempDr (BigDecimal LFR_AmtPeriodeTempDr)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_AmtPeriodeTempDr, LFR_AmtPeriodeTempDr);
	}

	/** Get LFR_AmtPeriodeTempDr.
		@return LFR_AmtPeriodeTempDr	  */
	public BigDecimal getLFR_AmtPeriodeTempDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_AmtPeriodeTempDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set LFR_BPartnerDisplayName.
		@param LFR_BPartnerDisplayName LFR_BPartnerDisplayName
	*/
	public void setLFR_BPartnerDisplayName (String LFR_BPartnerDisplayName)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_BPartnerDisplayName, LFR_BPartnerDisplayName);
	}

	/** Get LFR_BPartnerDisplayName.
		@return LFR_BPartnerDisplayName	  */
	public String getLFR_BPartnerDisplayName()
	{
		return (String)get_Value(COLUMNNAME_LFR_BPartnerDisplayName);
	}

	/** Set LFR_BalanceGeneRegrLevel.
		@param LFR_BalanceGeneRegrLevel LFR_BalanceGeneRegrLevel
	*/
	public void setLFR_BalanceGeneRegrLevel (String LFR_BalanceGeneRegrLevel)
	{
		set_Value (COLUMNNAME_LFR_BalanceGeneRegrLevel, LFR_BalanceGeneRegrLevel);
	}

	/** Get LFR_BalanceGeneRegrLevel.
		@return LFR_BalanceGeneRegrLevel	  */
	public String getLFR_BalanceGeneRegrLevel()
	{
		return (String)get_Value(COLUMNNAME_LFR_BalanceGeneRegrLevel);
	}

	/** Set LFR_CL1.
		@param LFR_CL1 LFR_CL1
	*/
	public void setLFR_CL1 (String LFR_CL1)
	{
		set_Value (COLUMNNAME_LFR_CL1, LFR_CL1);
	}

	/** Get LFR_CL1.
		@return LFR_CL1	  */
	public String getLFR_CL1()
	{
		return (String)get_Value(COLUMNNAME_LFR_CL1);
	}

	/** Set LFR_CL2.
		@param LFR_CL2 LFR_CL2
	*/
	public void setLFR_CL2 (String LFR_CL2)
	{
		set_Value (COLUMNNAME_LFR_CL2, LFR_CL2);
	}

	/** Get LFR_CL2.
		@return LFR_CL2	  */
	public String getLFR_CL2()
	{
		return (String)get_Value(COLUMNNAME_LFR_CL2);
	}

	/** Set LFR_CL3.
		@param LFR_CL3 LFR_CL3
	*/
	public void setLFR_CL3 (String LFR_CL3)
	{
		set_Value (COLUMNNAME_LFR_CL3, LFR_CL3);
	}

	/** Get LFR_CL3.
		@return LFR_CL3	  */
	public String getLFR_CL3()
	{
		return (String)get_Value(COLUMNNAME_LFR_CL3);
	}

	/** Set LFR_DateAsString.
		@param LFR_DateAsString LFR_DateAsString
	*/
	public void setLFR_DateAsString (String LFR_DateAsString)
	{
		set_Value (COLUMNNAME_LFR_DateAsString, LFR_DateAsString);
	}

	/** Get LFR_DateAsString.
		@return LFR_DateAsString	  */
	public String getLFR_DateAsString()
	{
		return (String)get_Value(COLUMNNAME_LFR_DateAsString);
	}

	/** Set LFR_FactAcctDescription.
		@param LFR_FactAcctDescription LFR_FactAcctDescription
	*/
	public void setLFR_FactAcctDescription (String LFR_FactAcctDescription)
	{
		set_Value (COLUMNNAME_LFR_FactAcctDescription, LFR_FactAcctDescription);
	}

	/** Get LFR_FactAcctDescription.
		@return LFR_FactAcctDescription	  */
	public String getLFR_FactAcctDescription()
	{
		return (String)get_Value(COLUMNNAME_LFR_FactAcctDescription);
	}

	/** Set LFR_FactAcctOrg.
		@param LFR_FactAcctOrg LFR_FactAcctOrg
	*/
	public void setLFR_FactAcctOrg (String LFR_FactAcctOrg)
	{
		set_Value (COLUMNNAME_LFR_FactAcctOrg, LFR_FactAcctOrg);
	}

	/** Get LFR_FactAcctOrg.
		@return LFR_FactAcctOrg	  */
	public String getLFR_FactAcctOrg()
	{
		return (String)get_Value(COLUMNNAME_LFR_FactAcctOrg);
	}

	/** Set LFR_GLCategoryPrintName.
		@param LFR_GLCategoryPrintName LFR_GLCategoryPrintName
	*/
	public void setLFR_GLCategoryPrintName (String LFR_GLCategoryPrintName)
	{
		set_Value (COLUMNNAME_LFR_GLCategoryPrintName, LFR_GLCategoryPrintName);
	}

	/** Get LFR_GLCategoryPrintName.
		@return LFR_GLCategoryPrintName	  */
	public String getLFR_GLCategoryPrintName()
	{
		return (String)get_Value(COLUMNNAME_LFR_GLCategoryPrintName);
	}

	/** Set LFR_MatchCode.
		@param LFR_MatchCode LFR_MatchCode
	*/
	public void setLFR_MatchCode (String LFR_MatchCode)
	{
		set_Value (COLUMNNAME_LFR_MatchCode, LFR_MatchCode);
	}

	/** Get LFR_MatchCode.
		@return LFR_MatchCode	  */
	public String getLFR_MatchCode()
	{
		return (String)get_Value(COLUMNNAME_LFR_MatchCode);
	}

	/** Set LFR_NumEcriture.
		@param LFR_NumEcriture LFR_NumEcriture
	*/
	public void setLFR_NumEcriture (int LFR_NumEcriture)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_NumEcriture, Integer.valueOf(LFR_NumEcriture));
	}

	/** Get LFR_NumEcriture.
		@return LFR_NumEcriture	  */
	public int getLFR_NumEcriture()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_NumEcriture);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_ReconciliationDate.
		@param LFR_ReconciliationDate LFR_ReconciliationDate
	*/
	public void setLFR_ReconciliationDate (Timestamp LFR_ReconciliationDate)
	{
		set_ValueNoCheck (COLUMNNAME_LFR_ReconciliationDate, LFR_ReconciliationDate);
	}

	/** Get LFR_ReconciliationDate.
		@return LFR_ReconciliationDate	  */
	public Timestamp getLFR_ReconciliationDate()
	{
		return (Timestamp)get_Value(COLUMNNAME_LFR_ReconciliationDate);
	}

	/** Set LFR_SoldeProgressif.
		@param LFR_SoldeProgressif LFR_SoldeProgressif
	*/
	public void setLFR_SoldeProgressif (BigDecimal LFR_SoldeProgressif)
	{
		set_Value (COLUMNNAME_LFR_SoldeProgressif, LFR_SoldeProgressif);
	}

	/** Get LFR_SoldeProgressif.
		@return LFR_SoldeProgressif	  */
	public BigDecimal getLFR_SoldeProgressif()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LFR_SoldeProgressif);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Organization Name.
		@param OrgName Name of the Organization
	*/
	public void setOrgName (String OrgName)
	{
		set_ValueNoCheck (COLUMNNAME_OrgName, OrgName);
	}

	/** Get Organization Name.
		@return Name of the Organization
	  */
	public String getOrgName()
	{
		return (String)get_Value(COLUMNNAME_OrgName);
	}

	/** PostingType AD_Reference_ID=125 */
	public static final int POSTINGTYPE_AD_Reference_ID=125;
	/** Actual = A */
	public static final String POSTINGTYPE_Actual = "A";
	/** Budget = B */
	public static final String POSTINGTYPE_Budget = "B";
	/** Commitment = E */
	public static final String POSTINGTYPE_Commitment = "E";
	/** Reservation = R */
	public static final String POSTINGTYPE_Reservation = "R";
	/** Statistical = S */
	public static final String POSTINGTYPE_Statistical = "S";
	/** Set Posting Type.
		@param PostingType The type of posted amount for the transaction
	*/
	public void setPostingType (String PostingType)
	{

		set_ValueNoCheck (COLUMNNAME_PostingType, PostingType);
	}

	/** Get Posting Type.
		@return The type of posted amount for the transaction
	  */
	public String getPostingType()
	{
		return (String)get_Value(COLUMNNAME_PostingType);
	}

	/** Set Print Text.
		@param PrintName The label text to be printed on a document or correspondence.
	*/
	public void setPrintName (String PrintName)
	{
		set_Value (COLUMNNAME_PrintName, PrintName);
	}

	/** Get Print Text.
		@return The label text to be printed on a document or correspondence.
	  */
	public String getPrintName()
	{
		return (String)get_Value(COLUMNNAME_PrintName);
	}

	/** Set T_LFR_Report.
		@param T_LFR_Report_ID T_LFR_Report
	*/
	public void setT_LFR_Report_ID (int T_LFR_Report_ID)
	{
		if (T_LFR_Report_ID < 1)
			set_ValueNoCheck (COLUMNNAME_T_LFR_Report_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_T_LFR_Report_ID, Integer.valueOf(T_LFR_Report_ID));
	}

	/** Get T_LFR_Report.
		@return T_LFR_Report	  */
	public int getT_LFR_Report_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_T_LFR_Report_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Title.
		@param Title Name this entity is referred to as
	*/
	public void setTitle (String Title)
	{
		set_ValueNoCheck (COLUMNNAME_Title, Title);
	}

	/** Get Title.
		@return Name this entity is referred to as
	  */
	public String getTitle()
	{
		return (String)get_Value(COLUMNNAME_Title);
	}
}