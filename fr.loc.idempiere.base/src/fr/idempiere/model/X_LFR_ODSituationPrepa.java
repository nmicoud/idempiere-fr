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

/** Generated Model for LFR_ODSituationPrepa
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="LFR_ODSituationPrepa")
public class X_LFR_ODSituationPrepa extends PO implements I_LFR_ODSituationPrepa, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251029L;

    /** Standard Constructor */
    public X_LFR_ODSituationPrepa (Properties ctx, int LFR_ODSituationPrepa_ID, String trxName)
    {
      super (ctx, LFR_ODSituationPrepa_ID, trxName);
      /** if (LFR_ODSituationPrepa_ID == 0)
        {
			setAD_OrgDoc_ID (0);
			setC_AcctSchema_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setLFR_IsAllOrgs (false);
// N
			setLFR_IsGroupInJournalBatch (false);
// N
			setLFR_ODSituationPrepa_ID (0);
			setLFR_ODSituationType (null);
			setName (null);
			setProcessed (false);
// N
			setProcessing (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_LFR_ODSituationPrepa (Properties ctx, int LFR_ODSituationPrepa_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_ODSituationPrepa_ID, trxName, virtualColumns);
      /** if (LFR_ODSituationPrepa_ID == 0)
        {
			setAD_OrgDoc_ID (0);
			setC_AcctSchema_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setLFR_IsAllOrgs (false);
// N
			setLFR_IsGroupInJournalBatch (false);
// N
			setLFR_ODSituationPrepa_ID (0);
			setLFR_ODSituationType (null);
			setName (null);
			setProcessed (false);
// N
			setProcessing (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_LFR_ODSituationPrepa (Properties ctx, String LFR_ODSituationPrepa_UU, String trxName)
    {
      super (ctx, LFR_ODSituationPrepa_UU, trxName);
      /** if (LFR_ODSituationPrepa_UU == null)
        {
			setAD_OrgDoc_ID (0);
			setC_AcctSchema_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setLFR_IsAllOrgs (false);
// N
			setLFR_IsGroupInJournalBatch (false);
// N
			setLFR_ODSituationPrepa_ID (0);
			setLFR_ODSituationType (null);
			setName (null);
			setProcessed (false);
// N
			setProcessing (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_LFR_ODSituationPrepa (Properties ctx, String LFR_ODSituationPrepa_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_ODSituationPrepa_UU, trxName, virtualColumns);
      /** if (LFR_ODSituationPrepa_UU == null)
        {
			setAD_OrgDoc_ID (0);
			setC_AcctSchema_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setLFR_IsAllOrgs (false);
// N
			setLFR_IsGroupInJournalBatch (false);
// N
			setLFR_ODSituationPrepa_ID (0);
			setLFR_ODSituationType (null);
			setName (null);
			setProcessed (false);
// N
			setProcessing (false);
// N
        } */
    }

    /** Load Constructor */
    public X_LFR_ODSituationPrepa (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_LFR_ODSituationPrepa[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
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

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getC_DocType_ID(), get_TrxName());
	}

	/** Set Document Type.
		@param C_DocType_ID Document type or rules
	*/
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0)
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** &lt;None&gt; = -- */
	public static final String DOCACTION_None = "--";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Set Document Action.
		@param DocAction The targeted status of the document
	*/
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction()
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Set Document Status.
		@param DocStatus The current status of the document
	*/
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus()
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	public org.compiere.model.I_GL_JournalBatch getGL_JournalBatch() throws RuntimeException
	{
		return (org.compiere.model.I_GL_JournalBatch)MTable.get(getCtx(), org.compiere.model.I_GL_JournalBatch.Table_ID)
			.getPO(getGL_JournalBatch_ID(), get_TrxName());
	}

	/** Set Journal Batch.
		@param GL_JournalBatch_ID General Ledger Journal Batch
	*/
	public void setGL_JournalBatch_ID (int GL_JournalBatch_ID)
	{
		if (GL_JournalBatch_ID < 1)
			set_Value (COLUMNNAME_GL_JournalBatch_ID, null);
		else
			set_Value (COLUMNNAME_GL_JournalBatch_ID, Integer.valueOf(GL_JournalBatch_ID));
	}

	/** Get Journal Batch.
		@return General Ledger Journal Batch
	  */
	public int getGL_JournalBatch_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_JournalBatch_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_IsAllOrgs.
		@param LFR_IsAllOrgs LFR_IsAllOrgs
	*/
	public void setLFR_IsAllOrgs (boolean LFR_IsAllOrgs)
	{
		set_Value (COLUMNNAME_LFR_IsAllOrgs, Boolean.valueOf(LFR_IsAllOrgs));
	}

	/** Get LFR_IsAllOrgs.
		@return LFR_IsAllOrgs	  */
	public boolean isLFR_IsAllOrgs()
	{
		Object oo = get_Value(COLUMNNAME_LFR_IsAllOrgs);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set LFR_IsGroupInJournalBatch.
		@param LFR_IsGroupInJournalBatch LFR_IsGroupInJournalBatch
	*/
	public void setLFR_IsGroupInJournalBatch (boolean LFR_IsGroupInJournalBatch)
	{
		set_Value (COLUMNNAME_LFR_IsGroupInJournalBatch, Boolean.valueOf(LFR_IsGroupInJournalBatch));
	}

	/** Get LFR_IsGroupInJournalBatch.
		@return LFR_IsGroupInJournalBatch	  */
	public boolean isLFR_IsGroupInJournalBatch()
	{
		Object oo = get_Value(COLUMNNAME_LFR_IsGroupInJournalBatch);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_GL_Journal getLFR_JournalCAP() throws RuntimeException
	{
		return (org.compiere.model.I_GL_Journal)MTable.get(getCtx(), org.compiere.model.I_GL_Journal.Table_ID)
			.getPO(getLFR_JournalCAP_ID(), get_TrxName());
	}

	/** Set LFR_JournalCAP_ID.
		@param LFR_JournalCAP_ID LFR_JournalCAP_ID
	*/
	public void setLFR_JournalCAP_ID (int LFR_JournalCAP_ID)
	{
		if (LFR_JournalCAP_ID < 1)
			set_Value (COLUMNNAME_LFR_JournalCAP_ID, null);
		else
			set_Value (COLUMNNAME_LFR_JournalCAP_ID, Integer.valueOf(LFR_JournalCAP_ID));
	}

	/** Get LFR_JournalCAP_ID.
		@return LFR_JournalCAP_ID	  */
	public int getLFR_JournalCAP_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_JournalCAP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_GL_Journal getLFR_JournalCCA() throws RuntimeException
	{
		return (org.compiere.model.I_GL_Journal)MTable.get(getCtx(), org.compiere.model.I_GL_Journal.Table_ID)
			.getPO(getLFR_JournalCCA_ID(), get_TrxName());
	}

	/** Set LFR_JournalCCA_ID.
		@param LFR_JournalCCA_ID LFR_JournalCCA_ID
	*/
	public void setLFR_JournalCCA_ID (int LFR_JournalCCA_ID)
	{
		if (LFR_JournalCCA_ID < 1)
			set_Value (COLUMNNAME_LFR_JournalCCA_ID, null);
		else
			set_Value (COLUMNNAME_LFR_JournalCCA_ID, Integer.valueOf(LFR_JournalCCA_ID));
	}

	/** Get LFR_JournalCCA_ID.
		@return LFR_JournalCCA_ID	  */
	public int getLFR_JournalCCA_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_JournalCCA_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set LFR_ODSituationPrepa_UU.
		@param LFR_ODSituationPrepa_UU LFR_ODSituationPrepa_UU
	*/
	public void setLFR_ODSituationPrepa_UU (String LFR_ODSituationPrepa_UU)
	{
		set_Value (COLUMNNAME_LFR_ODSituationPrepa_UU, LFR_ODSituationPrepa_UU);
	}

	/** Get LFR_ODSituationPrepa_UU.
		@return LFR_ODSituationPrepa_UU	  */
	public String getLFR_ODSituationPrepa_UU()
	{
		return (String)get_Value(COLUMNNAME_LFR_ODSituationPrepa_UU);
	}

	/** CCA = A */
	public static final String LFR_ODSITUATIONTYPE_CCA = "A";
	/** CAP = P */
	public static final String LFR_ODSITUATIONTYPE_CAP = "P";
	/** Set LFR_ODSituationType.
		@param LFR_ODSituationType LFR_ODSituationType
	*/
	public void setLFR_ODSituationType (String LFR_ODSituationType)
	{

		set_Value (COLUMNNAME_LFR_ODSituationType, LFR_ODSituationType);
	}

	/** Get LFR_ODSituationType.
		@return LFR_ODSituationType	  */
	public String getLFR_ODSituationType()
	{
		return (String)get_Value(COLUMNNAME_LFR_ODSituationType);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
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

	/** Set Processed On.
		@param ProcessedOn The date+time (expressed in decimal format) when the document has been processed
	*/
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Process Now.
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Total Amount.
		@param TotalAmt Total Amount
	*/
	public void setTotalAmt (BigDecimal TotalAmt)
	{
		set_ValueNoCheck (COLUMNNAME_TotalAmt, TotalAmt);
	}

	/** Get Total Amount.
		@return Total Amount
	  */
	public BigDecimal getTotalAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}