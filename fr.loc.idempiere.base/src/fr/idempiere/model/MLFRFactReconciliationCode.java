package fr.idempiere.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;

/**
 *  xxxxx TODO + cartouche
 *	
 *  @author Nico
 *  @version $Id: MLFRFactReconciliationCode.java
 */
public class MLFRFactReconciliationCode extends X_LFR_FactReconciliationCode
{
	private static final long serialVersionUID = 5028600630588224439L;
	private static CLogger s_log = CLogger.getCLogger(MLFRFactReconciliationCode.class);
	
	private final static String PREMIER_CODE = "A";
	private final static int INCREMENT = 1;
	
	/**************************************************************************
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param LFR_FactReconciliationCode_ID id
	 *	@param trxName transaction
	 */
	public MLFRFactReconciliationCode (Properties ctx, int LFR_FactReconciliationCode_ID, String trxName)
	{
		super (ctx, LFR_FactReconciliationCode_ID, trxName);
	}	//	MLFRFactReconciliationCode

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MLFRFactReconciliationCode (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MLFRFactReconciliationCode


	private static int getIntFromAlpha (String colName) //	http://stackoverflow.com/questions/763691/programming-riddle-how-might-you-translate-an-excel-column-name-to-a-number
	{
		//remove any whitespace
		colName = colName.trim();

		StringBuffer buff = new StringBuffer(colName);

		//string to lower case, reverse then place in char array
		char chars[] = buff.reverse().toString().toLowerCase().toCharArray();

		int retVal=0, multiplier=0;

		for(int i = 0; i < chars.length;i++){
			//retrieve ascii value of character, subtract 96 so number corresponds to place in alphabet. ascii 'a' = 97 
			multiplier = (int)chars[i]-96;
			//mult the number by 26^(position in array)
			retVal += multiplier * Math.pow(26, i);
		}
		return retVal;
	}

	private static String getAlphaFromInt (int colNum) {

		String res = "";

		int quot = colNum;
		int rem;        
		/*1. Subtract one from number.
		 *2. Save the mod 26 value.
		 *3. Divide the number by 26, save result.
		 *4. Convert the remainder to a letter.
		 *5. Repeat until the number is zero.
		 *6. Return that bitch...
		 */
		while(quot > 0)
		{
			quot = quot - 1;
			rem = quot % 26;
			quot = quot / 26;

			//cast to a char and add to the beginning of the string
			//add 97 to convert to the correct ascii number
			res = (char)(rem+97) + res;            
		}   
		return res.toUpperCase();
	}

	public static synchronized String getCodeNext(int acctSchemaID, String type, int recordID, String trxName) {
		// cf org.compiere.model.MSequence.getDocumentNoFromSeq

		String whereClause = " WHERE C_AcctSchema_ID = ? AND LFR_FactReconciliationType = ? AND Record_ID = ? AND IsActive='Y'";
		StringBuilder sql = new StringBuilder("SELECT Code FROM LFR_FactReconciliationCode frc").append(whereClause);
		
		if (DB.isPostgreSQL())
			sql.append(" FOR UPDATE OF frc");
		else if (DB.isOracle())
			sql.append(" FOR UPDATE OF frc.Code");

		Connection conn = null;
		Trx trx = trxName == null ? null : Trx.get(trxName, true);
		String retValue = "";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if (trx != null)
				conn = trx.getConnection();
			else
				conn = DB.getConnection(false);
			//	Error
			if (conn == null)
				return null;

			pstmt = conn.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			pstmt.setInt(1, acctSchemaID);
			pstmt.setString(2, type);
			pstmt.setInt(3, recordID);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				retValue = rs.getString(1);	// le code lettrage à utiliser

				// détermination du prochain code lettrage (pour enregistrement dans la base)
				int num = getIntFromAlpha(retValue);	// conversion Alpha > Int
				int next = num + INCREMENT;	// Int + 1
				String nextCode = getAlphaFromInt(next);	// conversion Int > Alpha

				PreparedStatement updateSQL = null;
				try {
					updateSQL = conn.prepareStatement("UPDATE LFR_FactReconciliationCode SET Code = ? " + whereClause);
					updateSQL.setString(1, nextCode);
					updateSQL.setInt(2, acctSchemaID);
					updateSQL.setString(3, type);
					updateSQL.setInt(4, recordID);
					updateSQL.executeUpdate();
				}
				finally {
					DB.close(updateSQL);
				}
			}
			else {
				MLFRFactReconciliationCode frc = new MLFRFactReconciliationCode(Env.getCtx(), 0, trxName);
				frc.setC_AcctSchema_ID(acctSchemaID);
				frc.setLFR_FactReconciliationType(type);
				frc.setRecord_ID(recordID);
				frc.setCode(PREMIER_CODE);
				frc.saveEx();
				retValue = PREMIER_CODE;
			}
			//	Commit
			if (trx == null)
				conn.commit();
		}
		catch (Exception e) {
			s_log.log(Level.SEVERE, "(FactRecCode) [" + trxName + "]", e);
			if (DBException.isTimeout(e))
				throw new AdempiereException("Timeout dans la récupération ou la mise à jour du code lettrage", e);
			else
				throw new AdempiereException("Erreur dans la récupération ou la mise à jour du code lettrage", e);
		}
		finally {
			//Finish
			DB.close(rs, pstmt);
			try {
				if (trx == null && conn != null) {
					conn.close();
					conn = null;
				}
			}
			catch (Exception e) {
				s_log.log(Level.SEVERE, "(FactRecCode) - finish", e);
			}
		}

		return retValue;
	}
	
}	//	MLFRFactReconciliationCode