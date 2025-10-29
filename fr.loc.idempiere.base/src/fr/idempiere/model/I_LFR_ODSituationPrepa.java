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

/** Generated Interface for LFR_ODSituationPrepa
 *  @author iDempiere (generated) 
 *  @version Release 12
 */
@SuppressWarnings("all")
public interface I_LFR_ODSituationPrepa 
{

    /** TableName=LFR_ODSituationPrepa */
    public static final String Table_Name = "LFR_ODSituationPrepa";

    /** AD_Table_ID=1000006 */
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

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

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

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocStatus */
    public static final String COLUMNNAME_DocStatus = "DocStatus";

	/** Set Document Status.
	  * The current status of the document
	  */
	public void setDocStatus (String DocStatus);

	/** Get Document Status.
	  * The current status of the document
	  */
	public String getDocStatus();

    /** Column name GL_JournalBatch_ID */
    public static final String COLUMNNAME_GL_JournalBatch_ID = "GL_JournalBatch_ID";

	/** Set Journal Batch.
	  * General Ledger Journal Batch
	  */
	public void setGL_JournalBatch_ID (int GL_JournalBatch_ID);

	/** Get Journal Batch.
	  * General Ledger Journal Batch
	  */
	public int getGL_JournalBatch_ID();

	public org.compiere.model.I_GL_JournalBatch getGL_JournalBatch() throws RuntimeException;

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

    /** Column name LFR_IsAllOrgs */
    public static final String COLUMNNAME_LFR_IsAllOrgs = "LFR_IsAllOrgs";

	/** Set LFR_IsAllOrgs	  */
	public void setLFR_IsAllOrgs (boolean LFR_IsAllOrgs);

	/** Get LFR_IsAllOrgs	  */
	public boolean isLFR_IsAllOrgs();

    /** Column name LFR_IsGroupInJournalBatch */
    public static final String COLUMNNAME_LFR_IsGroupInJournalBatch = "LFR_IsGroupInJournalBatch";

	/** Set LFR_IsGroupInJournalBatch	  */
	public void setLFR_IsGroupInJournalBatch (boolean LFR_IsGroupInJournalBatch);

	/** Get LFR_IsGroupInJournalBatch	  */
	public boolean isLFR_IsGroupInJournalBatch();

    /** Column name LFR_JournalCAP_ID */
    public static final String COLUMNNAME_LFR_JournalCAP_ID = "LFR_JournalCAP_ID";

	/** Set LFR_JournalCAP_ID	  */
	public void setLFR_JournalCAP_ID (int LFR_JournalCAP_ID);

	/** Get LFR_JournalCAP_ID	  */
	public int getLFR_JournalCAP_ID();

	public org.compiere.model.I_GL_Journal getLFR_JournalCAP() throws RuntimeException;

    /** Column name LFR_JournalCCA_ID */
    public static final String COLUMNNAME_LFR_JournalCCA_ID = "LFR_JournalCCA_ID";

	/** Set LFR_JournalCCA_ID	  */
	public void setLFR_JournalCCA_ID (int LFR_JournalCCA_ID);

	/** Get LFR_JournalCCA_ID	  */
	public int getLFR_JournalCCA_ID();

	public org.compiere.model.I_GL_Journal getLFR_JournalCCA() throws RuntimeException;

    /** Column name LFR_ODSituationPrepa_ID */
    public static final String COLUMNNAME_LFR_ODSituationPrepa_ID = "LFR_ODSituationPrepa_ID";

	/** Set LFR_ODSituationPrepa	  */
	public void setLFR_ODSituationPrepa_ID (int LFR_ODSituationPrepa_ID);

	/** Get LFR_ODSituationPrepa	  */
	public int getLFR_ODSituationPrepa_ID();

    /** Column name LFR_ODSituationPrepa_UU */
    public static final String COLUMNNAME_LFR_ODSituationPrepa_UU = "LFR_ODSituationPrepa_UU";

	/** Set LFR_ODSituationPrepa_UU	  */
	public void setLFR_ODSituationPrepa_UU (String LFR_ODSituationPrepa_UU);

	/** Get LFR_ODSituationPrepa_UU	  */
	public String getLFR_ODSituationPrepa_UU();

    /** Column name LFR_ODSituationType */
    public static final String COLUMNNAME_LFR_ODSituationType = "LFR_ODSituationType";

	/** Set LFR_ODSituationType	  */
	public void setLFR_ODSituationType (String LFR_ODSituationType);

	/** Get LFR_ODSituationType	  */
	public String getLFR_ODSituationType();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name ProcessedOn */
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";

	/** Set Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn);

	/** Get Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name TotalAmt */
    public static final String COLUMNNAME_TotalAmt = "TotalAmt";

	/** Set Total Amount.
	  * Total Amount
	  */
	public void setTotalAmt (BigDecimal TotalAmt);

	/** Get Total Amount.
	  * Total Amount
	  */
	public BigDecimal getTotalAmt();

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
