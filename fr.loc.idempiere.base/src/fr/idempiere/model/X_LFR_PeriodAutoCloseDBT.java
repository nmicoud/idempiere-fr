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

/** Generated Model for LFR_PeriodAutoCloseDBT
 *  @author iDempiere (generated) 
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="LFR_PeriodAutoCloseDBT")
public class X_LFR_PeriodAutoCloseDBT extends PO implements I_LFR_PeriodAutoCloseDBT, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20230224L;

    /** Standard Constructor */
    public X_LFR_PeriodAutoCloseDBT (Properties ctx, int LFR_PeriodAutoCloseDBT_ID, String trxName)
    {
      super (ctx, LFR_PeriodAutoCloseDBT_ID, trxName);
      /** if (LFR_PeriodAutoCloseDBT_ID == 0)
        {
			setDocBaseType (null);
			setLFR_CloseAfterPeriodEndDays (0);
// 0
			setLFR_PeriodAutoCloseDBT_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_LFR_PeriodAutoCloseDBT (Properties ctx, int LFR_PeriodAutoCloseDBT_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, LFR_PeriodAutoCloseDBT_ID, trxName, virtualColumns);
      /** if (LFR_PeriodAutoCloseDBT_ID == 0)
        {
			setDocBaseType (null);
			setLFR_CloseAfterPeriodEndDays (0);
// 0
			setLFR_PeriodAutoCloseDBT_ID (0);
        } */
    }

    /** Load Constructor */
    public X_LFR_PeriodAutoCloseDBT (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_LFR_PeriodAutoCloseDBT[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** DocBaseType AD_Reference_ID=183 */
	public static final int DOCBASETYPE_AD_Reference_ID=183;
	/** AP Credit Memo = APC */
	public static final String DOCBASETYPE_APCreditMemo = "APC";
	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AP Payment = APP */
	public static final String DOCBASETYPE_APPayment = "APP";
	/** AR Credit Memo = ARC */
	public static final String DOCBASETYPE_ARCreditMemo = "ARC";
	/** AR Pro Forma Invoice = ARF */
	public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** AR Receipt = ARR */
	public static final String DOCBASETYPE_ARReceipt = "ARR";
	/** Payment Allocation = CMA */
	public static final String DOCBASETYPE_PaymentAllocation = "CMA";
	/** Bank Statement = CMB */
	public static final String DOCBASETYPE_BankStatement = "CMB";
	/** Cash Journal = CMC */
	public static final String DOCBASETYPE_CashJournal = "CMC";
	/** Distribution Order = DOO */
	public static final String DOCBASETYPE_DistributionOrder = "DOO";
	/** Fixed Assets Addition = FAA */
	public static final String DOCBASETYPE_FixedAssetsAddition = "FAA";
	/** Fixed Assets Disposal = FAD */
	public static final String DOCBASETYPE_FixedAssetsDisposal = "FAD";
	/** Fixed Assets Depreciation = FDP */
	public static final String DOCBASETYPE_FixedAssetsDepreciation = "FDP";
	/** GL Document = GLD */
	public static final String DOCBASETYPE_GLDocument = "GLD";
	/** GL Journal = GLJ */
	public static final String DOCBASETYPE_GLJournal = "GLJ";
	/** Payroll = HRP */
	public static final String DOCBASETYPE_Payroll = "HRP";
	/** Manufacturing Cost Collector = MCC */
	public static final String DOCBASETYPE_ManufacturingCostCollector = "MCC";
	/** Material Physical Inventory = MMI */
	public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
	/** Material Movement = MMM */
	public static final String DOCBASETYPE_MaterialMovement = "MMM";
	/** Material Production = MMP */
	public static final String DOCBASETYPE_MaterialProduction = "MMP";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Maintenance Order = MOF */
	public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
	/** Manufacturing Order = MOP */
	public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
	/** Quality Order = MQO */
	public static final String DOCBASETYPE_QualityOrder = "MQO";
	/** Match Invoice = MXI */
	public static final String DOCBASETYPE_MatchInvoice = "MXI";
	/** Match PO = MXP */
	public static final String DOCBASETYPE_MatchPO = "MXP";
	/** Project Issue = PJI */
	public static final String DOCBASETYPE_ProjectIssue = "PJI";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Purchase Requisition = POR */
	public static final String DOCBASETYPE_PurchaseRequisition = "POR";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** FactureDouteuseSuivi = XFD */
	public static final String DOCBASETYPE_FactureDouteuseSuivi = "XFD";
	/** Budget = XGB */
	public static final String DOCBASETYPE_Budget = "XGB";
	/** Immobilisation = XIM */
	public static final String DOCBASETYPE_Immobilisation = "XIM";
	/** InvoiceNdF = XPF */
	public static final String DOCBASETYPE_InvoiceNdF = "XPF";
	/** RemiseDecaissement = XRD */
	public static final String DOCBASETYPE_RemiseDecaissement = "XRD";
	/** RemiseEncaissement = XRE */
	public static final String DOCBASETYPE_RemiseEncaissement = "XRE";
	/** ReglementTaxe = XRT */
	public static final String DOCBASETYPE_ReglementTaxe = "XRT";
	/** Set Document Base Type.
		@param DocBaseType Logical type of document
	*/
	public void setDocBaseType (String DocBaseType)
	{

		set_Value (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document Base Type.
		@return Logical type of document
	  */
	public String getDocBaseType()
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** Set LFR_CloseAfterPeriodEndDays.
		@param LFR_CloseAfterPeriodEndDays LFR_CloseAfterPeriodEndDays
	*/
	public void setLFR_CloseAfterPeriodEndDays (int LFR_CloseAfterPeriodEndDays)
	{
		set_Value (COLUMNNAME_LFR_CloseAfterPeriodEndDays, Integer.valueOf(LFR_CloseAfterPeriodEndDays));
	}

	/** Get LFR_CloseAfterPeriodEndDays.
		@return LFR_CloseAfterPeriodEndDays	  */
	public int getLFR_CloseAfterPeriodEndDays()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_CloseAfterPeriodEndDays);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_PeriodAutoCloseDBT.
		@param LFR_PeriodAutoCloseDBT_ID LFR_PeriodAutoCloseDBT
	*/
	public void setLFR_PeriodAutoCloseDBT_ID (int LFR_PeriodAutoCloseDBT_ID)
	{
		if (LFR_PeriodAutoCloseDBT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_LFR_PeriodAutoCloseDBT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_LFR_PeriodAutoCloseDBT_ID, Integer.valueOf(LFR_PeriodAutoCloseDBT_ID));
	}

	/** Get LFR_PeriodAutoCloseDBT.
		@return LFR_PeriodAutoCloseDBT	  */
	public int getLFR_PeriodAutoCloseDBT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_LFR_PeriodAutoCloseDBT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set LFR_PeriodAutoCloseDBT_UU.
		@param LFR_PeriodAutoCloseDBT_UU LFR_PeriodAutoCloseDBT_UU
	*/
	public void setLFR_PeriodAutoCloseDBT_UU (String LFR_PeriodAutoCloseDBT_UU)
	{
		set_Value (COLUMNNAME_LFR_PeriodAutoCloseDBT_UU, LFR_PeriodAutoCloseDBT_UU);
	}

	/** Get LFR_PeriodAutoCloseDBT_UU.
		@return LFR_PeriodAutoCloseDBT_UU	  */
	public String getLFR_PeriodAutoCloseDBT_UU()
	{
		return (String)get_Value(COLUMNNAME_LFR_PeriodAutoCloseDBT_UU);
	}
}