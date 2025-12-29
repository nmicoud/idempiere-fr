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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for LFR_PaySelectionPrepayment
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="LFR_PaySelectionPrepayment")
public class X_LFR_PaySelectionPrepayment extends PO implements I_LFR_PaySelectionPrepayment, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251219L;

    /** Standard Constructor */
    public X_LFR_PaySelectionPrepayment (Properties ctx, int LFR_PaySelectionPrepayment_ID, String trxName)
    {
      super (ctx, LFR_PaySelectionPrepayment_ID, trxName);
      /** if (LFR_PaySelectionPrepayment_ID == 0)
        {
			setC_PaySelection_ID (0);
			setC_Payment_ID (0);
			setIsPrinted (false);
// N
			setLFR_PaySelectionPrepayment_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_PaySelectionPrepayment WHERE C_PaySelection_ID = @C_PaySelection_ID@
			setPayAmt (Env.ZERO);
			setProcessed (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_LFR_PaySelectionPrepayment (Properties ctx, int LFR_PaySelectionPrepayment_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_PaySelectionPrepayment_ID, trxName, virtualColumns);
      /** if (LFR_PaySelectionPrepayment_ID == 0)
        {
			setC_PaySelection_ID (0);
			setC_Payment_ID (0);
			setIsPrinted (false);
// N
			setLFR_PaySelectionPrepayment_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_PaySelectionPrepayment WHERE C_PaySelection_ID = @C_PaySelection_ID@
			setPayAmt (Env.ZERO);
			setProcessed (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_LFR_PaySelectionPrepayment (Properties ctx, String LFR_PaySelectionPrepayment_UU, String trxName)
    {
      super (ctx, LFR_PaySelectionPrepayment_UU, trxName);
      /** if (LFR_PaySelectionPrepayment_UU == null)
        {
			setC_PaySelection_ID (0);
			setC_Payment_ID (0);
			setIsPrinted (false);
// N
			setLFR_PaySelectionPrepayment_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_PaySelectionPrepayment WHERE C_PaySelection_ID = @C_PaySelection_ID@
			setPayAmt (Env.ZERO);
			setProcessed (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_LFR_PaySelectionPrepayment (Properties ctx, String LFR_PaySelectionPrepayment_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_PaySelectionPrepayment_UU, trxName, virtualColumns);
      /** if (LFR_PaySelectionPrepayment_UU == null)
        {
			setC_PaySelection_ID (0);
			setC_Payment_ID (0);
			setIsPrinted (false);
// N
			setLFR_PaySelectionPrepayment_ID (0);
			setLine (0);
// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM LFR_PaySelectionPrepayment WHERE C_PaySelection_ID = @C_PaySelection_ID@
			setPayAmt (Env.ZERO);
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_LFR_PaySelectionPrepayment (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_LFR_PaySelectionPrepayment[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_BP_BankAccount getC_BP_BankAccount() throws RuntimeException
	{
		return (org.compiere.model.I_C_BP_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BP_BankAccount.Table_ID)
			.getPO(getC_BP_BankAccount_ID(), get_TrxName());
	}

	/** Set Partner Bank Account.
		@param C_BP_BankAccount_ID Bank Account of the Business Partner
	*/
	public void setC_BP_BankAccount_ID (int C_BP_BankAccount_ID)
	{
		if (C_BP_BankAccount_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_BP_BankAccount_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_BP_BankAccount_ID, Integer.valueOf(C_BP_BankAccount_ID));
	}

	/** Get Partner Bank Account.
		@return Bank Account of the Business Partner
	  */
	public int getC_BP_BankAccount_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_BankAccount_ID);
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

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());
	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
	*/
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
	{
		if (C_BPartner_Location_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_Location_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
	}

	/** Get Partner Location.
		@return Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaySelection getC_PaySelection() throws RuntimeException
	{
		return (org.compiere.model.I_C_PaySelection)MTable.get(getCtx(), org.compiere.model.I_C_PaySelection.Table_ID)
			.getPO(getC_PaySelection_ID(), get_TrxName());
	}

	/** Set Payment Selection.
		@param C_PaySelection_ID Payment Selection
	*/
	public void setC_PaySelection_ID (int C_PaySelection_ID)
	{
		if (C_PaySelection_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_PaySelection_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_PaySelection_ID, Integer.valueOf(C_PaySelection_ID));
	}

	/** Get Payment Selection.
		@return Payment Selection
	  */
	public int getC_PaySelection_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaySelection_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Payment getC_Payment() throws RuntimeException
	{
		return (org.compiere.model.I_C_Payment)MTable.get(getCtx(), org.compiere.model.I_C_Payment.Table_ID)
			.getPO(getC_Payment_ID(), get_TrxName());
	}

	/** Set Payment.
		@param C_Payment_ID Payment identifier
	*/
	public void setC_Payment_ID (int C_Payment_ID)
	{
		if (C_Payment_ID < 1)
			set_Value (COLUMNNAME_C_Payment_ID, null);
		else
			set_Value (COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
	}

	/** Get Payment.
		@return Payment identifier
	  */
	public int getC_Payment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Payment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Printed.
		@param IsPrinted Indicates if this document / line is printed
	*/
	public void setIsPrinted (boolean IsPrinted)
	{
		set_ValueNoCheck (COLUMNNAME_IsPrinted, Boolean.valueOf(IsPrinted));
	}

	/** Get Printed.
		@return Indicates if this document / line is printed
	  */
	public boolean isPrinted()
	{
		Object oo = get_Value(COLUMNNAME_IsPrinted);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LFR_PaySelectionPrepayment.
		@param LFR_PaySelectionPrepayment_ID LFR_PaySelectionPrepayment
	*/
	public void setLFR_PaySelectionPrepayment_ID (int LFR_PaySelectionPrepayment_ID)
	{
		if (LFR_PaySelectionPrepayment_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_PaySelectionPrepayment_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_PaySelectionPrepayment_ID, Integer.valueOf(LFR_PaySelectionPrepayment_ID));
	}

	/** Get LFR_PaySelectionPrepayment.
		@return LFR_PaySelectionPrepayment	  */
	public int getLFR_PaySelectionPrepayment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_PaySelectionPrepayment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_PaySelectionPrepayment_UU.
		@param LFR_PaySelectionPrepayment_UU LFR_PaySelectionPrepayment_UU
	*/
	public void setLFR_PaySelectionPrepayment_UU (String LFR_PaySelectionPrepayment_UU)
	{
		set_Value (COLUMNNAME_LFR_PaySelectionPrepayment_UU, LFR_PaySelectionPrepayment_UU);
	}

	/** Get LFR_PaySelectionPrepayment_UU.
		@return LFR_PaySelectionPrepayment_UU	  */
	public String getLFR_PaySelectionPrepayment_UU()
	{
		return (String)get_Value(COLUMNNAME_LFR_PaySelectionPrepayment_UU);
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

	/** Set Payment amount.
		@param PayAmt Amount being paid
	*/
	public void setPayAmt (BigDecimal PayAmt)
	{
		set_Value (COLUMNNAME_PayAmt, PayAmt);
	}

	/** Get Payment amount.
		@return Amount being paid
	  */
	public BigDecimal getPayAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PayAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}
}