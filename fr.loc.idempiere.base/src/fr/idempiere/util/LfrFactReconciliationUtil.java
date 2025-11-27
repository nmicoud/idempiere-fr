package fr.idempiere.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import fr.idempiere.model.MLFRFactReconciliationCode;

public class LfrFactReconciliationUtil {
	
	private static CLogger s_log = CLogger.getCLogger(LfrFactReconciliationUtil.class);

	public static void factReconcile(PO po) {
		
		if (po.get_Table_ID() == MInvoice.Table_ID && !po.get_ValueAsBoolean("IsPaid"))
			return;
		else if (po.get_Table_ID() == MPayment.Table_ID && !po.get_ValueAsBoolean("IsAllocated"))
			return;

		String sql = "SELECT fa.C_AcctSchema_ID, fa.Fact_Acct_ID"
				+ " FROM Fact_Acct fa, C_ElementValue ev"
				+ " WHERE fa.PostingType='A' AND fa.AD_Client_ID=" + po.getAD_Client_ID()
				+ " AND AD_Table_ID = " + po.get_Table_ID() + " AND Record_ID = " + po.get_ID()
				+ " AND fa.Account_ID = ev.C_ElementValue_ID AND ev.BPartnerType IS NOT NULL AND ev.AD_Client_ID = " + po.getAD_Client_ID()
				+ " ORDER BY fa.Account_ID, fa.Fact_Acct_ID";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement (sql, po.get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int acctSchemaID = rs.getInt("C_AcctSchema_ID");
				int factAcctID = rs.getInt("Fact_Acct_ID");
				LfrFactReconciliationUtil.LettrageFactAcct(po.getCtx(), factAcctID, acctSchemaID, po.get_TrxName());
			}
		}
		catch (Exception e) { // TODO ne pas bloquer la génération des écritures comptables
			throw new AdempiereException("Error...", e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}

	public static int LettrageFactAcct (Properties ctx, int Fact_Acct_ID, int C_AcctSchema_ID, String trxName)
	{
		int nbLettrees = 0;
		int retValue = 0;
		String sql = "SELECT fa.Account_ID, fa.AD_Table_ID, fa.Record_ID, fa.Line_ID, fa.C_BPartner_ID" 
				+ " FROM Fact_Acct fa"
				+ " WHERE Fact_Acct_ID = " + Fact_Acct_ID;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql, trxName);
			rs = pstmt.executeQuery ();
			ArrayList<MAllocationHdr> allocs = new ArrayList<MAllocationHdr>();
			ArrayList<Object> docs = new ArrayList<Object>();

			while (rs.next()) {
				int Account_ID = rs.getInt(1);
				int AD_Table_ID = rs.getInt(2);
				int Record_ID = rs.getInt(3);
				int Line_ID = rs.getInt(4);
				int C_BPartner_ID = rs.getInt(5);

				//Verification que l'écriture comptable n'a pas été léttrée précédemment TODO rendre optionnel car pas d'intérêt de le faire suite à la génération des écritures d'une facture
				String matchcode = getMatchCodeOfFactAcct(C_AcctSchema_ID, AD_Table_ID, Record_ID, Line_ID, Account_ID, trxName);
				if (matchcode != null)
					continue;

				MInvoice inv = null;
				MPayment pay = null;

				MAllocationHdr alloc = null;
				allocs = new ArrayList<MAllocationHdr>();
				docs = new ArrayList<Object>();

				//Récupération des affectations du document courant
				if (AD_Table_ID == MInvoice.Table_ID) {
					inv = new MInvoice(ctx, Record_ID, trxName);
					docs.add(inv);
					allocs = getOfPosted(ctx, AD_Table_ID, Record_ID, trxName);
				}
				else if (AD_Table_ID == MPayment.Table_ID) {
					pay = new MPayment(ctx, Record_ID, trxName);
					docs.add(pay);
					allocs = getOfPosted(ctx, AD_Table_ID, Record_ID, trxName);
				}
				else if (AD_Table_ID == MAllocationHdr.Table_ID) {
					alloc = new MAllocationHdr(ctx, Record_ID, trxName);
					allocs.add(alloc);
				}
				else
					continue;

				//Récupération de toutes les affectations de tous les documents
				getDocumentsAndOtherAllocations(ctx, C_BPartner_ID, allocs, docs, trxName);

				if (allocs.size() > 0 && docs.size() > 0) {
					//Fabrique une clause where pour retrouver les écritures comptables concernés des documents et des affectations
					String SQLWhereFact = getSQLWhereForFactAcct(allocs, docs) + " AND Account_ID =" + Account_ID;

					//Récupération des ID des écr comptables sous forme d'une chaine de caractère séparée par des virgules 
					String listFactAcctIDs = "";
					listFactAcctIDs = getlistFactAcctIDs(SQLWhereFact, trxName);

					//Récupération de la date max parmi toutes les écr comptables sélectionnées
					Timestamp MaxDateAcct = null; 
					MaxDateAcct = getMaxDateAcct(SQLWhereFact, trxName);

					//Insertion dans la table Fact_Reconciliation des écritures comptables d'un même lettrage
					retValue = InsertAndUpdateXXA_Lettrage(ctx, MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner, listFactAcctIDs, /*IsLettrageCodeTemp, */MaxDateAcct, C_BPartner_ID, C_AcctSchema_ID, trxName);

					nbLettrees += retValue;
				}
			}
		}
		catch (Exception e) {
			s_log.log(Level.SEVERE, "Erreur dans LettrageBPartner", e);
			e.printStackTrace();
			return -1;
		}
		finally {
			DB.close(rs, pstmt);
		}
		return nbLettrees;
	}	// LettrageFactAcct
	
	/** Renvoie des ID d'écritures comptables sous forme de chaine de caractères */
	static String getlistFactAcctIDs (String SQLWhereClause, String trxName) 
	{
		String listFactAcctIDs = "";
		int[] faIDs = DB.getIDsEx(trxName, "SELECT Fact_Acct_ID FROM Fact_Acct WHERE " + SQLWhereClause);
		for (int faID : faIDs)
			listFactAcctIDs += faID + ", ";
		if (listFactAcctIDs.length()>0)
			listFactAcctIDs = listFactAcctIDs.substring(0, listFactAcctIDs.length()-2);	// retrait dernier espace et ,
		return listFactAcctIDs;
	}	// getlistFactAcctIDs


	/**
	 * 	Fonction récursive : récupère les affectations des documents des affectations des documents ..., jusqu'à ce qu'il n'y ait plus d'affectations à récupérer.
	 *  Si une affectation comporte 3 documents (paiement, facture, lignes d'OD), on va récupérer la liste des affectations de ces 3 documents. Avec cette nouvelle liste d'affectations, 
	 *  on va vérifier la liste des documents rattachés pour voir si d'autres affectations sont à prendre en compte et ainsi de suite
	 *	@param ctx context
	 *	@param allocs list of allocations 
	 *	@param docs documents of these allocations
	 *	@param trxName transaction
	 *	@return 
	 */
	public static boolean getDocumentsAndOtherAllocations(Properties ctx, int C_BPartner_ID, ArrayList<MAllocationHdr> allocs, ArrayList<Object> docs, String trxName)
	{
		boolean ISSUEFOUND = false;
		boolean OTHERALLOCATION_EXIST = false;

		if (allocs.size() == 0)
			return ISSUEFOUND;

		String sql = "SELECT ah.C_AllocationHdr_ID, i.C_Invoice_ID, p.C_Payment_ID/*, odl.GL_JournalLine_ID*/" 
				+ " FROM C_AllocationHdr ah "
				+ " INNER JOIN C_AllocationLine al ON (ah.C_AllocationHdr_ID=al.C_AllocationHdr_ID AND al.C_BPartner_ID = " + C_BPartner_ID + ")"
				+ " LEFT OUTER JOIN C_Invoice i ON (al.C_Invoice_ID=i.C_Invoice_ID AND i.C_BPartner_ID = " + C_BPartner_ID + ")"
				+ " LEFT OUTER JOIN C_Payment p ON (al.C_Payment_ID=p.C_Payment_ID AND p.C_BPartner_ID = " + C_BPartner_ID + ")"
				+ " WHERE ah.isActive='Y' AND ah.C_AllocationHdr_ID IN (";

		boolean needcomma = false;
		for (MAllocationHdr alloc : allocs)
		{
			if (needcomma)
				sql += ","+alloc.getC_AllocationHdr_ID(); 
			else
			{
				sql += alloc.getC_AllocationHdr_ID();
				needcomma = true;
			}
		}

		sql += ")";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				int C_Invoice_ID = rs.getInt(2);
				int C_Payment_ID = rs.getInt(3);

				if (C_Invoice_ID != 0)
				{
					ArrayList<MAllocationHdr> allocs_i = getOfPosted(ctx, MInvoice.Table_ID, C_Invoice_ID, trxName);
					for (MAllocationHdr alloc : allocs_i)
					{
						if (!allocs.contains(alloc))
						{
							allocs.add(alloc);
							OTHERALLOCATION_EXIST = true;
						}
						MInvoice inv = new MInvoice(ctx, C_Invoice_ID, trxName);
						if (!docs.contains(inv))
							docs.add(inv);
						if (!ISSUEFOUND && (!inv.isPosted() || !inv.isPaid() || !alloc.isPosted()))
							ISSUEFOUND = true;
					}
				}
				if (C_Payment_ID != 0)
				{
					ArrayList<MAllocationHdr> allocs_p = getOfPosted(ctx, MPayment.Table_ID, C_Payment_ID, trxName);
					for (MAllocationHdr alloc : allocs_p)
					{
						if (!allocs.contains(alloc))
						{
							allocs.add(alloc);
							OTHERALLOCATION_EXIST = true;
						}
						MPayment pay = new MPayment(ctx, C_Payment_ID, trxName);
						if (!docs.contains(pay))
							docs.add(pay);
						if (!ISSUEFOUND && (!pay.isPosted() || !pay.isAllocated() || !alloc.isPosted()))
							ISSUEFOUND = true;
					}
				}
			}
//			DB.close(rs, pstmt);
//			pstmt = null;
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, sql, e);
		}
		finally {
			DB.close(rs, pstmt);
		}
		
		if (OTHERALLOCATION_EXIST)
		{
			boolean ISSUEFOUND_REC = getDocumentsAndOtherAllocations(ctx, C_BPartner_ID, allocs, docs, trxName);
			ISSUEFOUND = ISSUEFOUND || ISSUEFOUND_REC;
		}
		return ISSUEFOUND;
	}
	
	/** Renvoie la date max parmi toutes les écritures comptables sélectionnées	 */
	static Timestamp getMaxDateAcct (String SQLWhereClause, String trxName) {
		return  DB.getSQLValueTSEx(trxName, "SELECT MAX(DateAcct) FROM Fact_Acct WHERE " + SQLWhereClause);
	}	// getMaxDateAcct
	
	/** Fabrique une clause where pour retrouver les écritures comptables concernés des documents et des affectations */
	private static String getSQLWhereForFactAcct(ArrayList<MAllocationHdr> allocations, ArrayList<Object> documents) // TODO pas besoin d'avoir des Object, on peut se contenter des IDs
	{
		String retvalue = "";
		String allocs_ID = "";
		String inv_ID = "";
		String pay_ID = "";
		boolean needcomma_a = false;
		boolean needcomma_i = false;
		boolean needcomma_p = false;

		for (MAllocationHdr alloc : allocations) {
			if (needcomma_a)
				allocs_ID += ","+alloc.getC_AllocationHdr_ID();
			else {
				allocs_ID += alloc.getC_AllocationHdr_ID();
				needcomma_a = true;
			}
		}
		for (Object doc : documents) {
			if (doc instanceof MInvoice) {
				if (needcomma_i)
					inv_ID += ","+((MInvoice)doc).getC_Invoice_ID();
				else {
					inv_ID += ((MInvoice)doc).getC_Invoice_ID();
					needcomma_i = true;
				}
			}
			else if (doc instanceof MPayment) {
				if (needcomma_p)
					pay_ID += ","+((MPayment)doc).getC_Payment_ID();
				else {
					pay_ID += ((MPayment)doc).getC_Payment_ID();
					needcomma_p = true;
				}
			}
		}

		if (allocs_ID.length() > 0)
			retvalue = "((AD_Table_ID = 735 AND Record_ID IN (" + allocs_ID +"))";
		if (inv_ID.length() > 0)
			retvalue += " OR (AD_Table_ID = 318 AND Record_ID IN (" + inv_ID +"))";
		if (pay_ID.length() > 0)
			retvalue += " OR (AD_Table_ID = 335 AND Record_ID IN (" + pay_ID +"))";
		retvalue += ")";
		return retvalue; 
	}	// getSQLWhereForFactAcct


	public static ArrayList<MAllocationHdr> getOfPosted (Properties ctx, int tableID, int recordID, String trxName)
	{
		ArrayList<MAllocationHdr> list = new ArrayList<MAllocationHdr>();
		String sql = "SELECT h.C_AllocationHdr_ID FROM C_AllocationHdr h"
				+ " WHERE EXISTS (SELECT * FROM C_AllocationLine l"
				+ " WHERE h.C_AllocationHdr_ID=l.C_AllocationHdr_ID AND l." + MTable.get(ctx, tableID).getTableName() + "_ID=?)"
				+ " ORDER BY h.posted ASC";
		int[] allocIDs = DB.getIDsEx(trxName, sql, recordID);
		for (int allocID : allocIDs)
			list.add (new MAllocationHdr(ctx, allocID, trxName));

		return list;
	}	//	getOfPosted
	
	

	/** Insertion dans la table XXA_Lettrage des écritures comptables d'un même lettrage */
	private static int InsertAndUpdateXXA_Lettrage(Properties ctx, String lettrageType, String sqlWhere, Timestamp dateLettrage, int recordID, int acctSchemaID, String trxName) {
		int user_id = Env.getAD_User_ID(ctx);
		int retvalue = 0;

		//	Vérification que Total Débit - Total Crédit = 0 pour les écritures à lettrer ensemble TODO à faire avant ??
		String sql = "SELECT COALESCE(SUM(AmtAcctDr),0)-COALESCE(SUM(AmtAcctCr),0) FROM Fact_Acct fa " +
				"LEFT OUTER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID) " +
				"WHERE fa.Fact_Acct_ID in (" + sqlWhere + ") " +
				"AND l.MatchCode IS NULL";

		BigDecimal balance = DB.getSQLValueBDEx(trxName, sql);
		if (balance.signum() != 0 )
			return 0;

		sql = "INSERT INTO Fact_Reconciliation" 
				+ " (Fact_Reconciliation_ID, Fact_Reconciliation_UU, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, IsActive, Fact_Acct_ID) " 
				//	+ "SELECT nextIDFunc(?, 'N')"
				+ " SELECT nextidfunc((SELECT AD_Sequence_ID FROM AD_Sequence WHERE Name = 'Fact_Reconciliation' AND IsActive='Y' AND IsTableID='Y' AND IsAutoSequence='Y' AND AD_Client_ID = 0), 'N')"
				+ ", generate_uuid(), AD_Client_ID, AD_Org_ID, Sysdate, " + user_id + ", Sysdate, " + user_id + ", IsActive, Fact_Acct_ID " 
				+ "FROM Fact_Acct fa " 
				+ "WHERE NOT EXISTS (SELECT 1 FROM Fact_Reconciliation l " 
				+ "WHERE l.Fact_Acct_ID = fa.Fact_Acct_ID)"+ " AND fa.Fact_Acct_ID in (" + sqlWhere + ")";
		DB.executeUpdateEx(sql, trxName);
		s_log.log(Level.FINE, "Inserted " + retvalue + " new facts into Fact_Reconciliation");

		String matchCode = MLFRFactReconciliationCode.getCodeNext(acctSchemaID, lettrageType, recordID, trxName);
		sql = "UPDATE Fact_Reconciliation "
				+ "SET MatchCode = '" + matchCode + "'" 
				+ ", LFR_ReconciliationDate =" + DB.TO_DATE(dateLettrage, true) 
				+ ", LFR_FactReconciliationType = '" + lettrageType + "'"
				+ ", LFR_IsManual = 'N'"
				+ ", Updated=Sysdate, "
				+ " UpdatedBy = " + user_id
				+ " WHERE Fact_Acct_ID in ("+sqlWhere+") " 
				+ " AND MatchCode IS NULL";

		retvalue = DB.executeUpdateEx(sql, trxName);
		s_log.log(Level.FINE, "Updated " + retvalue + " facts into Fact_Reconciliation");
		return retvalue;
	}	// InsertAndUpdateXXA_Lettrage
	
	/**
	 * Identifie le tiers de l'affectation ; le même tiers doit figurer sur toutes les lignes
	 **/
	public static int getAllocBPartnerID(Properties ctx, MAllocationHdr alloc, String trxName)
	{
		MAllocationLine[] lines = alloc.getLines(true);

		ArrayList<Integer> listBP = new ArrayList<Integer>();
		for (int i = 0; i < lines.length; i++)
		{
			MAllocationLine line = lines[i];
			if (!listBP.contains(line.getC_BPartner_ID()))
				listBP.add(line.getC_BPartner_ID());
		}
		if (listBP.size() != 1)
			return 0;	// concerne plusieurs tiers, impossible de lettrer
		else
			return listBP.get(0);
	}
	
	/** Renvoie les comptes identifiés comme étant des auxilliaires */
	public static int[] getElementValueBPartnerID(Properties ctx, int AD_Client_ID, int acctSchemaID, String trxName)
	{ // TODO il faut chercher ces comptes selon le schéma comptable
		String sql = "SELECT C_ElementValue_ID FROM C_ElementValue ev " +
				"WHERE BPartnerType IS NOT NULL " 
				+ "AND ev.IsActive='Y' AND ev.IsSummary='N' " 
				+ "AND ev.C_Element_ID IN (SELECT C_Element_ID FROM C_AcctSchema_Element ase WHERE ase.ElementType='AC' AND ase.AD_Client_ID=" + AD_Client_ID + " AND C_AcctSchema_ID = " + acctSchemaID + ") ";
		
		return DB.getIDsEx(trxName, sql);		
	}
	
	/** Lettre les écritures d'une affectation */
	public static int LettrageAlloc(Properties ctx, MAllocationHdr alloc, int AD_Client_ID, int C_AcctSchema_ID, int Account_ID, String trxName)
	{
		int nbLettrees=0;
		ArrayList<MAllocationHdr> allocs = new ArrayList<MAllocationHdr>();
		ArrayList<Object> docs = new ArrayList<Object>();
		int C_AllocationHdr_ID = alloc.getC_AllocationHdr_ID();
		try {
			int C_BPartner_ID = getAllocBPartnerID(ctx, alloc, trxName);
			if (C_BPartner_ID <= 0)
				return -1;	// concerne plusieurs tiers, impossible de lettrer

			allocs = new ArrayList<MAllocationHdr>();
			docs = new ArrayList<Object>();
			allocs.add(alloc);

			//Récupération de toutes les affectations de tous les documents
	/*		boolean IsMatcheCodeTemp =*/ getDocumentsAndOtherAllocations(ctx, C_BPartner_ID, allocs, docs, trxName);

			if (allocs.size() > 0 && docs.size() > 0) {
				//Fabrique une clause where pour retrouver les écritures comptables concernés des documents et des affectations
				String SQLWhereFact = getSQLWhereForFactAcct(allocs, docs);
				int[] comptesAux = null;
				if (Account_ID > 0) {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(Account_ID);
					comptesAux = new int[list.size()];
				}
				else
					comptesAux = getElementValueBPartnerID(ctx, AD_Client_ID, C_AcctSchema_ID, trxName);

				for (int elementValueID : comptesAux) {
					String SQLWhereFactEV = SQLWhereFact + " AND Account_ID=" + elementValueID;
					SQLWhereFactEV += " AND NOT EXISTS (SELECT 1 FROM Fact_Reconciliation l WHERE l.Fact_Acct_ID = Fact_Acct.Fact_Acct_ID AND l.MatchCode IS NOT NULL) ";

					//Récupération des ID des écr comptables sous forme d'une chaine de caractère séparée par des virgules
					String FactAcctIDs = getlistFactAcctIDs(SQLWhereFactEV, trxName);

					if (FactAcctIDs.length() != 0 && FactAcctIDs != null) {
						//Récupération de la date max parmi toutes les écr comptables sélectionnées
						Timestamp MaxDateAcct = getMaxDateAcct(SQLWhereFactEV, trxName);

						//Insertion dans la table Fact_Reconciliation des écritures comptables d'un même lettrage
						InsertAndUpdateXXA_Lettrage(ctx, MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner, FactAcctIDs,/* IsMatcheCodeTemp,*/ MaxDateAcct, C_BPartner_ID, C_AcctSchema_ID, trxName);
					}
				}
			}
		}
		catch (Exception e) {
			s_log.log(Level.SEVERE, "Erreur dans LettrageAlloc - C_AllocationHdr_ID="+C_AllocationHdr_ID, e);
			e.printStackTrace();
			return -1; // si erreur on renvoie -1
		}
		return nbLettrees;
	}	// LettrageAlloc

	/**
	 * 	Suppression du lettrage pour un code donné
	 *	@return number of rows or -1 for error
	 */
	public static int supprLettrage (String lettrageType,  String codeLettrage, int clientID, int acctSchemaID, int recordID, String trxName)
	{
		String ColumnName = "";
		if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account))
			ColumnName = MFactAcct.COLUMNNAME_Account_ID;
		else if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			ColumnName = MFactAcct.COLUMNNAME_C_BPartner_ID;

		StringBuilder sb = new StringBuilder("DELETE FROM Fact_Reconciliation WHERE Fact_Acct_ID IN (") // suppression pour être en mesure de recomptabiliser les documents
		.append("SELECT fa.Fact_Acct_ID")
		.append(" FROM Fact_Acct fa")
		.append(" INNER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID)")
		.append(" WHERE fa.").append(ColumnName).append(" = ").append(recordID)
		.append(" AND l.LFR_FactReconciliationType = ").append(DB.TO_STRING(lettrageType))
		.append(" AND l.MatchCode = ").append(DB.TO_STRING(codeLettrage));	

		if (acctSchemaID > 0)
			sb.append(" AND fa.C_AcctSchema_ID = ").append(acctSchemaID);

		sb.append(")");

		int no = DB.executeUpdate(sb.toString(), trxName);
		if (no == -1)
			s_log.log(Level.SEVERE, "failed: XXA_LettrageCode=" + codeLettrage);
		else
			s_log.fine("delete - XXA_LettrageCode=" + codeLettrage + " - #" + no);

		return no;
	}	//	supprLettrage
	
	/**
	 * 	Le document a-t-il des écritures qui sont lettrées ?
	 *	@return MatchCode
	 */
	public static String getMatchCodeOfFactAcct(int AcctSchema_ID, int AD_Table_ID, int Record_ID, int Line_ID, int Account_ID, String trxName)
	{
		StringBuilder sql = new StringBuilder("SELECT l.MatchCode FROM Fact_Acct fa") 
		.append(" INNER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID)") 
		.append(" WHERE fa.AD_Table_ID = ").append(AD_Table_ID) 
		.append(" AND fa.Record_ID = ").append(Record_ID);

		if (AcctSchema_ID > 0)
			sql.append(" AND fa.C_AcctSchema_ID = ").append(AcctSchema_ID);
		if (Line_ID > 0)
			sql.append(" AND fa.Line_ID = ").append(Line_ID);
		if (Account_ID > 0)
			sql.append(" AND fa.Account_ID = ").append(Account_ID);

		String matchcode = DB.getSQLValueString(trxName, sql.toString());
		return matchcode;
	}

}
