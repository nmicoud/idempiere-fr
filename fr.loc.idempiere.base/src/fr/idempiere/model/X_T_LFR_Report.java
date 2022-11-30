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
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="T_LFR_Report")
public class X_T_LFR_Report extends PO implements I_T_LFR_Report, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20221130L;

    /** Standard Constructor */
    public X_T_LFR_Report (Properties ctx, int T_LFR_Report_ID, String trxName)
    {
      super (ctx, T_LFR_Report_ID, trxName);
      /** if (T_LFR_Report_ID == 0)
        {
			setT_LFR_Report_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_T_LFR_Report (Properties ctx, int T_LFR_Report_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, T_LFR_Report_ID, trxName, virtualColumns);
      /** if (T_LFR_Report_ID == 0)
        {
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