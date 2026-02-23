/**********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Nicolas Micoud - TGI                                              *
 **********************************************************************/

package fr.idempiere.util;

import static fr.idempiere.model.SystemIDs_LFR.LFR_FEC_PER_ORG;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MClient;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;


/**
 * Util class for LFR
 * @author Nicolas Micoud - TGI
 */

public class LfrUtil extends Util {

	/** Formatte la date passée en paramètre selon le format défini */
	public static String formatDate (Properties ctx, int clientID, Object value) {
		return formatDate(ctx, clientID, value, "");
	}

	/** Formatte la date passée en paramètre selon le format défini */
	public static String formatDate (Properties ctx, int clientID, Object value, String format) {
		if (Util.isEmpty(format))
			format = "dd/MM/yyyy";

		Locale locale = MClient.get(ctx, clientID).getLanguage().getLocale();
		return new SimpleDateFormat(format, locale).format(value);
	}

	public static String checkInvoiceLineImputation(Properties ctx, Timestamp deb, Timestamp fin) {
		if (deb != null && fin != null) {

			if (TimeUtil.max(deb, fin) == deb)
				return Msg.getMsg(ctx, "LFR_InvoiceLineImputationDateFinAvantDateDeb");
		}

		return "";
	}

	/** copie le fichier source dans le fichier resultat (http://java.developpez.com/faq/java/?page=langage_fichiers#LANGAGE_FICHIER_copier)
	 * retourne vrai si cela réussit
	 */
	public static boolean copyFile(File source, File dest){
		try{
			// Declaration et ouverture des flux
			java.io.FileInputStream sourceFile = new java.io.FileInputStream(source);

			try{
				java.io.FileOutputStream destinationFile = null;

				try{
					destinationFile = new FileOutputStream(dest);

					// Lecture par segment de 0.5Mo 
					byte buffer[] = new byte[512 * 1024];
					int nbLecture;

					while ((nbLecture = sourceFile.read(buffer)) != -1){
						destinationFile.write(buffer, 0, nbLecture);
					}
				} finally {
					destinationFile.close();
				}
			} finally {
				sourceFile.close();
			}
		} catch (IOException e){
			e.printStackTrace();
			return false; // Erreur
		}

		return true; // Résultat OK  
	}

	/** Renvoie un tableau de bytes à partir d'un fichier */
	public static byte[] readFile(File file) throws IOException { // http://stackoverflow.com/questions/858980/file-to-byte-in-java

		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ( (read = ios.read(buffer)) != -1 ) {
				ous.write(buffer, 0, read);
			}
		} finally { 
			try {
				if ( ous != null ) 
					ous.close();
			} catch ( IOException e) {
				throw new AdempiereException(e);
			}

			try {
				if ( ios != null ) 
					ios.close();
			} catch ( IOException e) {
				throw new AdempiereException(e);
			}
		}
		return ous.toByteArray();
	}
	
	/** Ajout du text dans le sb avec séparateur */
	public static void add(StringBuilder sb, String text, String sep) {

		if (!Util.isEmpty(text)) {
			if (sb.length() > 0)
				sb.append(sep);

			sb.append(text);
		}
	}

	/** Renvoie true si le rôle connecté peut accéder au process, false dans les autres cas (accès non autorisé ou null) */
	public static boolean getProcessAccess(int processID) {
		Boolean access = MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx())).getProcessAccess(processID);
		if (access != null && access.booleanValue())
			return true;
		else
			return false;
	}

	/** Renvoie true si le Fichier des Ecritures Comptables doit se faire par organisation */
	public static boolean isFecPerOrg(int clientID) {
		return MSysConfig.getBooleanValue(LFR_FEC_PER_ORG, false, clientID);
	}
}
