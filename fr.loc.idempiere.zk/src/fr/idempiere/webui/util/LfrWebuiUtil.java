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

package fr.idempiere.webui.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.adempiere.webui.component.Button;
import org.adempiere.webui.theme.ThemeManager;
import org.compiere.util.Util;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Separator;

/**
 * Util class for LFR
 * @author Nicolas Micoud - TGI
 */

public class LfrWebuiUtil {

	public static Hlayout getHlayout() {
		Hlayout layout = new Hlayout();
		layout.setValign("middle");
		return layout;
	}


	public static Button getButton(String label, String tooltip, String image, EventListener<Event> listener) { // TODO gérer images font/css
		Button btn = new Button();
		btn.setLabel(label);
		btn.setTooltiptext(tooltip);
		btn.setImage(Util.isEmpty(image) ? "" : ThemeManager.getThemeResource("images/" + image + ".png"));
		btn.addEventListener(Events.ON_CLICK, listener);
		return btn;
	}

	/**
	 * Get charset from content type header. Fallback to UTF-8
	 * @param contentType
	 * @return charset
	 */
	static private String getCharset(String contentType) {
		if (contentType != null) {
			int j = contentType.indexOf("charset=");
			if (j >= 0) {
				String cs = contentType.substring(j + 8).trim();
				if (cs.length() > 0) return cs;
			}
		}
		return "UTF-8";
	}

	public static File mediaToFile(Media media, File tempFile) {

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(tempFile);
			byte[] bytes = null;
			if (media.inMemory()) {
				bytes = media.isBinary() ? media.getByteData() : media.getStringData().getBytes(getCharset(media.getContentType()));
			} else {
				InputStream is = media.getStreamData();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[ 1000 ];
				int byteread = 0;
				while (( byteread=is.read(buf) )!=-1)
					baos.write(buf,0,byteread);
				bytes = baos.toByteArray();
			}

			fos.write(bytes);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			return null;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {}
		}
		return tempFile;
	}

	public static Separator getSeparator(int width) {
		Separator sep = new Separator("horizontal");
		sep.setWidth(width + "px");
		return sep;
	}
}
