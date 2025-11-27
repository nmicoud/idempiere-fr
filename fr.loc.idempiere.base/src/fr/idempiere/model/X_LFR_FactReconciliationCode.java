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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for LFR_FactReconciliationCode
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="LFR_FactReconciliationCode")
public class X_LFR_FactReconciliationCode extends PO implements I_LFR_FactReconciliationCode, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251125L;

    /** Standard Constructor */
    public X_LFR_FactReconciliationCode (Properties ctx, int LFR_FactReconciliationCode_ID, String trxName)
    {
      super (ctx, LFR_FactReconciliationCode_ID, trxName);
      /** if (LFR_FactReconciliationCode_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setCode (null);
			setLFR_FactReconciliationCode_ID (0);
			setLFR_FactReconciliationType (null);
			setRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_LFR_FactReconciliationCode (Properties ctx, int LFR_FactReconciliationCode_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_FactReconciliationCode_ID, trxName, virtualColumns);
      /** if (LFR_FactReconciliationCode_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setCode (null);
			setLFR_FactReconciliationCode_ID (0);
			setLFR_FactReconciliationType (null);
			setRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_LFR_FactReconciliationCode (Properties ctx, String LFR_FactReconciliationCode_UU, String trxName)
    {
      super (ctx, LFR_FactReconciliationCode_UU, trxName);
      /** if (LFR_FactReconciliationCode_UU == null)
        {
			setC_AcctSchema_ID (0);
			setCode (null);
			setLFR_FactReconciliationCode_ID (0);
			setLFR_FactReconciliationType (null);
			setRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_LFR_FactReconciliationCode (Properties ctx, String LFR_FactReconciliationCode_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_FactReconciliationCode_UU, trxName, virtualColumns);
      /** if (LFR_FactReconciliationCode_UU == null)
        {
			setC_AcctSchema_ID (0);
			setCode (null);
			setLFR_FactReconciliationCode_ID (0);
			setLFR_FactReconciliationType (null);
			setRecord_ID (0);
        } */
    }

    /** Load Constructor */
    public X_LFR_FactReconciliationCode (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 2 - Client
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
      StringBuilder sb = new StringBuilder ("X_LFR_FactReconciliationCode[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_ID)
			.getPO(getAD_Table_ID(), get_TrxName());
	}

	/** Set Table.
		@param AD_Table_ID Database Table information
	*/
	public void setAD_Table_ID (int AD_Table_ID)
	{
		throw new IllegalArgumentException ("AD_Table_ID is virtual column");	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Validation code.
		@param Code Validation Code
	*/
	public void setCode (String Code)
	{
		set_Value (COLUMNNAME_Code, Code);
	}

	/** Get Validation code.
		@return Validation Code
	  */
	public String getCode()
	{
		return (String)get_Value(COLUMNNAME_Code);
	}

	/** Set LFR_FactReconciliationCode.
		@param LFR_FactReconciliationCode_ID LFR_FactReconciliationCode
	*/
	public void setLFR_FactReconciliationCode_ID (int LFR_FactReconciliationCode_ID)
	{
		if (LFR_FactReconciliationCode_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_FactReconciliationCode_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_FactReconciliationCode_ID, Integer.valueOf(LFR_FactReconciliationCode_ID));
	}

	/** Get LFR_FactReconciliationCode.
		@return LFR_FactReconciliationCode	  */
	public int getLFR_FactReconciliationCode_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_FactReconciliationCode_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_FactReconciliationCode_UU.
		@param LFR_FactReconciliationCode_UU LFR_FactReconciliationCode_UU
	*/
	public void setLFR_FactReconciliationCode_UU (String LFR_FactReconciliationCode_UU)
	{
		set_Value (COLUMNNAME_LFR_FactReconciliationCode_UU, LFR_FactReconciliationCode_UU);
	}

	/** Get LFR_FactReconciliationCode_UU.
		@return LFR_FactReconciliationCode_UU	  */
	public String getLFR_FactReconciliationCode_UU()
	{
		return (String)get_Value(COLUMNNAME_LFR_FactReconciliationCode_UU);
	}

	/** Account = A */
	public static final String LFR_FACTRECONCILIATIONTYPE_Account = "A";
	/** BPartner = B */
	public static final String LFR_FACTRECONCILIATIONTYPE_BPartner = "B";
	/** Set LFR_FactReconciliationType.
		@param LFR_FactReconciliationType LFR_FactReconciliationType
	*/
	public void setLFR_FactReconciliationType (String LFR_FactReconciliationType)
	{

		set_ValueNoCheck (COLUMNNAME_LFR_FactReconciliationType, LFR_FactReconciliationType);
	}

	/** Get LFR_FactReconciliationType.
		@return LFR_FactReconciliationType	  */
	public String getLFR_FactReconciliationType()
	{
		return (String)get_Value(COLUMNNAME_LFR_FactReconciliationType);
	}

	/** Set Record ID.
		@param Record_ID Direct internal record ID
	*/
	public void setRecord_ID (int Record_ID)
	{
		if (Record_ID < 0)
			set_ValueNoCheck (COLUMNNAME_Record_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
	}

	/** Get Record ID.
		@return Direct internal record ID
	  */
	public int getRecord_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}