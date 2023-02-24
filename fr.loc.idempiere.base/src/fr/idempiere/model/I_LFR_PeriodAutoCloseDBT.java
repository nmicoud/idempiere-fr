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

/** Generated Interface for LFR_PeriodAutoCloseDBT
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_LFR_PeriodAutoCloseDBT 
{

    /** TableName=LFR_PeriodAutoCloseDBT */
    public static final String Table_Name = "LFR_PeriodAutoCloseDBT";

    /** AD_Table_ID=1000008 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 2 - Client 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(2);

    /** Load Meta Data */

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

    /** Column name DocBaseType */
    public static final String COLUMNNAME_DocBaseType = "DocBaseType";

	/** Set Document Base Type.
	  * Logical type of document
	  */
	public void setDocBaseType (String DocBaseType);

	/** Get Document Base Type.
	  * Logical type of document
	  */
	public String getDocBaseType();

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

    /** Column name LFR_CloseAfterPeriodEndDays */
    public static final String COLUMNNAME_LFR_CloseAfterPeriodEndDays = "LFR_CloseAfterPeriodEndDays";

	/** Set LFR_CloseAfterPeriodEndDays	  */
	public void setLFR_CloseAfterPeriodEndDays (int LFR_CloseAfterPeriodEndDays);

	/** Get LFR_CloseAfterPeriodEndDays	  */
	public int getLFR_CloseAfterPeriodEndDays();

    /** Column name LFR_PeriodAutoCloseDBT_ID */
    public static final String COLUMNNAME_LFR_PeriodAutoCloseDBT_ID = "LFR_PeriodAutoCloseDBT_ID";

	/** Set LFR_PeriodAutoCloseDBT	  */
	public void setLFR_PeriodAutoCloseDBT_ID (int LFR_PeriodAutoCloseDBT_ID);

	/** Get LFR_PeriodAutoCloseDBT	  */
	public int getLFR_PeriodAutoCloseDBT_ID();

    /** Column name LFR_PeriodAutoCloseDBT_UU */
    public static final String COLUMNNAME_LFR_PeriodAutoCloseDBT_UU = "LFR_PeriodAutoCloseDBT_UU";

	/** Set LFR_PeriodAutoCloseDBT_UU	  */
	public void setLFR_PeriodAutoCloseDBT_UU (String LFR_PeriodAutoCloseDBT_UU);

	/** Get LFR_PeriodAutoCloseDBT_UU	  */
	public String getLFR_PeriodAutoCloseDBT_UU();

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
