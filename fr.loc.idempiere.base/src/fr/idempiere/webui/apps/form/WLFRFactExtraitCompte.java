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

package fr.idempiere.webui.apps.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.Callback;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Menupopup;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.Dialog;
import org.compiere.minigrid.ColumnInfo;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MColumn;
import org.compiere.model.MFactAcct;
import org.compiere.model.MLanguage;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MProcess;
import org.compiere.model.MScheduler;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.print.MPrintFormat;
import org.compiere.print.MPrintFormatItem;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.tools.FileUtil;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Language;
import org.compiere.util.MimeType;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.East;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vlayout;

import fr.idempiere.util.LfrUtil;
import fr.idempiere.webui.util.LfrWebuiUtil;

/**
 *  Formulaire de consultation des extraits de compte
 *  @author Nicolas Micoud - TGI
 */
//@org.idempiere.ui.zk.annotation.Form(name = "fr.loc.idempiere.webui.apps.form.WLFRFactExtraitCompte")
public class WLFRFactExtraitCompte implements IFormController, EventListener<Event>
{
	public static CLogger s_log = CLogger.getCLogger(WLFRFactExtraitCompte.class);
	private East m_east;
	public Listbox fProcessMain, fGridLayout;
	private Button bExport, bReRun;
	private WListbox table;
	public Iframe preview;
	private Panel previewPanel;
	public Hlayout hlPreviewHLayout;
	private Toolbarbutton bDownloadSelectedFile;
	private Panel parameterPanel;
	private Label lParams;
	private StringBuilder m_paramsStr; 
	private Menupopup m_popup;
	public int m_WindowNo = 0;
	private CustomForm form = new CustomForm();
	private Panel mainPanel = new Panel();
	private Borderlayout mainLayout = new Borderlayout();
	private Button bRefresh, bZoomToFact, bZoomToDoc;
	private Panel southPanel;

	protected BigDecimal m_DifferenceAmt = Env.ZERO;
	protected int idxColID= 0;
	protected int idxColDateAcct = 2;
	protected int idxColAmtAcctDr = 6;
	protected int idxColAmtAcctCr = 7;
	protected int idxColMatchCode = 10;

	private final static String PROCESS_PARAM_LFR_PINSTANCE_SOURCE_ID = "LFR_PInstance_Source_ID";
	private final static String PROCESS_PARAM_LFR_FACTAUXTYPE = "LFR_FactAuxType";
	private final static String POPUP_ZOOM_DOC = "zoomDoc";
	private final static String POPUP_ZOOM_FA = "zoomFactAcct";
	private final static String OUTPUT_JASPER = "XXA_J";

	public final static String SYSCONFIG_NAME_PREFIX = "LFR_EXTRAIT_COMPTE_PROCESS_";
	public final static String SYSCONFIG_SEP = ";";
	public final static String SYSCONFIG_EQ = "=";
	public final static String SYSCONFIG_PROCESS_PREFIX = "PROCESS_ID";
	public final static String SYSCONFIG_JASPER_PREFIX = "JASPER_PROCESS_ID";
	public final static String SYSCONFIG_EXPORT_PREFIX = "EXPORT_PROCESS_ID";
	public final static String SYSCONFIG_LAYOUT_PREFIX = "LAYOUT";
	public final static String SYSCONFIG_LAYOUT_START = "{";
	public final static String SYSCONFIG_LAYOUT_END = "}";
	
	public final static String SYSCONFIG_TYPE_PREFIX = "TYPE";
	public final static String SYSCONFIG_TYPE_GENE = "G";
	public final static String SYSCONFIG_TYPE_AUX_CLT = "C";
	public final static String SYSCONFIG_TYPE_AUX_FRS = "F";
	
	public final static String LAYOUT_01 = "01";
	public final static String LAYOUT_02 = "02";
	public final static String LAYOUT_03 = "03";
	public final static String LAYOUT_04 = "04";
	public final static String LAYOUT_05 = "05";

	private int m_instanceMainID = 0;
	private ArrayList<String> listParamsInContext;
	private ColumnInfo m_columnInfoID = null;

	public final static String CTX_LFR_FACT_EXTRAIT_COMPTE_PANEL = "IsWLFRFactExtraitCompte";
	
	public WLFRFactExtraitCompte()
	{
		try {
			m_WindowNo = form.getWindowNo();

			dynInit();
			zkInit();

			Env.setContext(Env.getCtx(), m_WindowNo, CTX_LFR_FACT_EXTRAIT_COMPTE_PANEL, "Y");
		}
		catch(Exception e) {
			s_log.log(Level.SEVERE, "", e);
		}
	}

	private void zkInit() {
		form.appendChild(mainPanel);
		mainPanel.appendChild(mainLayout);
		mainPanel.setStyle("width: 100%; height: 100%; padding: 0; margin: 0");
		mainLayout.setHeight("100%");
		mainLayout.setWidth("99%");
		bZoomToFact = new Button(Msg.translate(Env.getCtx(), "Fact_Acct_ID"));
		bZoomToFact.setImage(ThemeManager.getThemeResource("images/Zoom24.png"));
		bZoomToFact.addEventListener(Events.ON_CLICK, this);
		bZoomToDoc = new Button(Msg.translate(Env.getCtx(), "SelectDocument"));
		bZoomToDoc.setImage(ThemeManager.getThemeResource("images/Zoom24.png"));
		bZoomToDoc.addEventListener(Events.ON_CLICK, this);
		bRefresh = new Button();
		bRefresh.setImage(ThemeManager.getThemeResource("images/Refresh24.png"));
		bRefresh.addEventListener(Events.ON_CLICK, this);

		North north = new North();
		north.setStyle("border: none");
		north.setAutoscroll(true);
		mainLayout.appendChild(north);

		lParams = new Label();
		parameterPanel = new Panel();

		Hlayout hl = LfrWebuiUtil.getHlayout();
		hl.appendChild(new Label("Etat"));
		hl.appendChild(fProcessMain);
		hl.appendChild(fGridLayout);
		hl.appendChild(LfrWebuiUtil.getSeparator(15));
		hl.appendChild(bReRun);
		hl.appendChild(LfrWebuiUtil.getSeparator(10));
		hl.appendChild(bExport);
		parameterPanel.appendChild(hl);
		
		Vlayout vl = new Vlayout();
		vl.appendChild(hl);
		vl.appendChild(lParams);
		north.appendChild(vl);
		
		South south = new South();
		south.setStyle("border: none");
		mainLayout.appendChild(south);
		southPanel = new Panel();
		southPanel.appendChild(bZoomToFact);
		southPanel.appendChild(bZoomToDoc);	
		south.appendChild(southPanel);

		Center center = new Center();
		mainLayout.appendChild(center);
		
		center.appendChild(table);
		ZKUpdateUtil.setWidth(table, "100%");
		ZKUpdateUtil.setVflex(table, "1");
		center.setBorder("none");
		
		m_east = new East();
		m_east.setSplittable(true);
		m_east.setAutoscroll(true);
		m_east.setCollapsible(true);
		LayoutUtils.addSclass("tab-editor-form-west-panel", m_east);
		mainLayout.appendChild(m_east);
		
		previewPanel = new Panel();
		previewPanel.setHeight("100%");
		preview = new Iframe();
		ZKUpdateUtil.setHeight(preview, "100%");
		ZKUpdateUtil.setWidth(preview, "100%");

		m_east.appendChild(previewPanel);
		m_east.setTitle("PDF");
		
		ZKUpdateUtil.setWidth(m_east, "300px");
		m_east.setOpen(false);
		m_east.setVisible(true);
		
		hlPreviewHLayout = LfrWebuiUtil.getHlayout();
		
		bDownloadSelectedFile = new Toolbarbutton();
		bDownloadSelectedFile.setImage(ThemeManager.getThemeResource("images/Export16.png"));
		bDownloadSelectedFile.addEventListener(Events.ON_CLICK, this);
		hlPreviewHLayout.appendChild(bDownloadSelectedFile);
	
		previewPanel.appendChild(hlPreviewHLayout);
		previewPanel.appendChild(preview);
	}

	private void dynInit() throws Exception {

		fProcessMain = ListboxFactory.newDropdownListbox();

		populateProcessField(fProcessMain);
		fProcessMain.addEventListener(Events.ON_SELECT, this);
		fProcessMain.setValue(null);
		bExport = LfrWebuiUtil.getButton("Exporter", "", "Export24", this);
		bReRun = LfrWebuiUtil.getButton("Relancer", "", "ReRun24", this);

		table = ListboxFactory.newDataTable();

		fGridLayout = ListboxFactory.newDropdownListbox();
		fGridLayout.addEventListener(Events.ON_SELECT, this);
		fGridLayout.setValue(null);
	}   //  dynInit

	private String getSelectedMainRecord() {
		Object value = fProcessMain.getSelectedItem().getValue();
		return (String) value;
	}

	private String getSelectedLayout() {

		if (fGridLayout.getSelectedItem() != null) {
			Object value = fGridLayout.getSelectedItem().getValue();
			return (String) value;
		}

		return "";
	}

	public void dispose()
	{
		SessionManager.getAppDesktop().closeActiveWindow();
	}	//	dispose

	public void onEvent (Event event) throws Exception {

		if (Events.ON_SELECT.equals(event.getName()) && event.getTarget() == fProcessMain) {
			String value = getSelectedMainRecord();

			fGridLayout.removeAllItems();
			for (String l : getLayout((String) value).split(",")) {

				if (l.length() > Integer.toString(MTable.MAX_OFFICIAL_ID).length()) {
					MPrintFormat pf = MPrintFormat.get(Integer.valueOf(l));
					fGridLayout.appendItem(pf.getName(), l);		
				}
				else
					fGridLayout.appendItem(getLayoutName(l), l);
			}

			String selLayout = getSelectedLayout();

			if (Util.isEmpty(selLayout))
				return;

			onSelectProcess(value, false);
		}
		else if (Events.ON_SELECT.equals(event.getName()) && event.getTarget() == fGridLayout) {

			if (table.getRowCount() > 0)
				refreshTable(m_instanceMainID);
			else
				onSelectProcess(getSelectedMainRecord(), false);
		}
		else if (event.getTarget() == bExport) {
			String value = getSelectedMainRecord();
			
			int processReportID = getExportProcessID(value);
			int printFormatID = -1;
			
			export(processReportID, printFormatID);
		}
		else if (event.getTarget() == bReRun) {
			onSelectProcess(getSelectedMainRecord(), true);
		}
		else if (event.getTarget() == bDownloadSelectedFile)
			Filedownload.save(getJasperAsFile(preview), "pdf");
		else if (event.getTarget() instanceof ProcessModalDialog && event.getName().equals(DialogEvents.ON_WINDOW_CLOSE)) {
			ProcessModalDialog dialog = (ProcessModalDialog) event.getTarget();
			if (!dialog.isCancel())
				generateGrid(dialog);
		}
		else if (event.getTarget() instanceof Menuitem) {
			Menuitem menuItem = (Menuitem) event.getTarget();

			if (menuItem.getValue().equals(POPUP_ZOOM_DOC))
				zoomToDocument();
			else if (menuItem.getValue().equals(POPUP_ZOOM_FA))
				zoomToFact();
		}
		else if (event.getTarget() == bZoomToFact)
			zoomToFact();
		else if (event.getTarget() == bZoomToDoc)
			zoomToDocument();
		else if (event.getTarget() == table && event.getName().equals(Events.ON_RIGHT_CLICK)) {
			showPopupOnTableSub(event);
		}
	}   //  onEvent

	private void onSelectProcess(String scName, boolean isReRun) {

		int instanceID = 0;
		listParamsInContext = new ArrayList<String>();

		int processID = Integer.valueOf(getProcessID(scName));

		if (isReRun)
			instanceID = m_instanceMainID;
		else if (!isReRun) {
			Env.setContext(Env.getCtx(), m_WindowNo, "C_AcctSchema_ID", Env.getContext(Env.getCtx(), Env.C_ACCTSCHEMA_ID));
			Env.setContext(Env.getCtx(), m_WindowNo, "PostingType", MFactAcct.POSTINGTYPE_Actual);
			Env.setContext(Env.getCtx(), m_WindowNo, "LFR_LettrageFiltre", "A");
			Env.setContext(Env.getCtx(), m_WindowNo, "AD_Org_ID", Env.getContext(Env.getCtx(), Env.USER_ORG));
			
			String extraitCompteType = getType(scName);
			
			if (extraitCompteType.equals(SYSCONFIG_TYPE_AUX_CLT) || extraitCompteType.equals(SYSCONFIG_TYPE_AUX_FRS)) {
				Env.setContext(Env.getCtx(), m_WindowNo, "LFR_IsRANDetail", "Y");
				Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", extraitCompteType.equals(SYSCONFIG_TYPE_AUX_CLT));
			}
		}

		if (instanceID > 0) {
			MPInstance ip = new MPInstance(Env.getCtx(), instanceID, null);
			MPInstancePara[] iParams = ip.getParameters();

			for (MPInstancePara iParam : iParams) {

				if (iParam.getP_Number() != null)
					Env.setContext(Env.getCtx(), m_WindowNo, iParam.getParameterName(), iParam.getP_Number().toString());
				if (iParam.getP_Number_To() != null)
					Env.setContext(Env.getCtx(), m_WindowNo, iParam.getParameterName() + "To", iParam.getP_Number_To().toString());

				if (iParam.getP_Date() != null)
					Env.setContext(Env.getCtx(), m_WindowNo, iParam.getParameterName(), iParam.getP_Date());
				if (iParam.getP_Date_To() != null)
					Env.setContext(Env.getCtx(), m_WindowNo, iParam.getParameterName() + "To", iParam.getP_Date_To());

				if (iParam.getP_String() != null)
					Env.setContext(Env.getCtx(), m_WindowNo, iParam.getParameterName(), iParam.getP_String());
				if (iParam.getP_String_To() != null)
					Env.setContext(Env.getCtx(), m_WindowNo, iParam.getParameterName() + "To", iParam.getP_String_To());

				listParamsInContext.add(iParam.getParameterName());
			}
		}

		final ProcessModalDialog dialog = new ProcessModalDialog(this, m_WindowNo, processID, 0, 0, false);

		if (dialog.isValid()) {

			dialog.setWidth("500px");
			dialog.setBorder("normal");
			AEnv.showCenterWindow(getForm(), dialog);
		}
	}

	private void generateGrid(ProcessModalDialog dialog) {

		for (String paramName : listParamsInContext) {
			Env.setContext(Env.getCtx(), m_WindowNo, paramName, "");
			Env.setContext(Env.getCtx(), m_WindowNo, paramName + "To", "");
		}

		int instanceID = dialog.getProcessInfo().getAD_PInstance_ID();

		if (dialog.isCancel())
			return;

		final ProcessInfo pi = dialog.getProcessInfo();

		if (pi.isError()) {
			onSelectProcess(getSelectedMainRecord(), true);
			Dialog.error(m_WindowNo, "ProcessRunError", pi.getSummary());
		}
		else {

			m_instanceMainID = instanceID;	

			String value = getSelectedMainRecord();

			refreshTable(instanceID);

			int exportProcessID = getExportProcessID(value);
			int jasperProcessID = getJasperProcessID(value);

			lParams.setValue(m_paramsStr.toString());
			bExport.setEnabled(exportProcessID > 0 && LfrUtil.getProcessAccess(exportProcessID));

			if (jasperProcessID > 0 && LfrUtil.getProcessAccess(jasperProcessID))
				export0(Integer.valueOf(jasperProcessID), 0, OUTPUT_JASPER);
			else {
				preview.setContent(null);
				m_east.setVisible(false);
			}

			getForm().invalidate();
		}
	}
	
	private void refreshTable(int instanceID) {
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		ListModelTable tableModel;

		String selLayout = fGridLayout.getSelectedItem().getValue();

		ColumnInfo[] layout = getLayout(instanceID, selLayout);
		table.prepareTable(layout, "", "", true , "osef");
		table.addEventListener(Events.ON_SELECT, this);
		table.addEventListener(Events.ON_DOUBLE_CLICK, this);
		table.addEventListener(Events.ON_RIGHT_CLICK, this);
		table.setEmptyMessage("no data available");

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement (gridContentSql.toString(), null);
			pstmt.setInt(1, instanceID);

			System.out.println(gridContentSql);
			System.out.println(instanceID);
			rs = pstmt.executeQuery();
			while (rs.next ()) {

				Vector<Object> line = new Vector<Object>();
				for (ColumnInfo ci : layout) {

					String columnName = ci.getColSQL();
					if (columnName.contains(" AS "))
						columnName = ci.getColSQL().substring(ci.getColSQL().indexOf(" AS ") + 4);

					if (ci == m_columnInfoID)
						line.add(new KeyNamePair(rs.getInt("Fact_Acct_ID"), rs.getString(columnName)));
					else
						line.add(rs.getObject(columnName));
				}
				data.add(line);
			}
		}
		catch (Exception e) {
			throw new AdempiereException(e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		tableModel = new ListModelTable(data);
		table.setModel(tableModel);
		table.repaint();

		for (Component c : table.getChildren()) {
			for (Component c1 : c.getChildren()) {
				if (c1 instanceof ListHeader) {
					ListHeader lh = (ListHeader) c1;
					lh.setSort("none");
				}
			}
		}
	}

	protected ArrayList<String> getParamsToHide() {

		ArrayList<String> retValue = new ArrayList<String>();
		retValue.add(PROCESS_PARAM_LFR_FACTAUXTYPE);
		return retValue;
	}

	private void setParamStr(int instanceID) {

		m_paramsStr = new StringBuilder("");

		MPInstance instance = new MPInstance(Env.getCtx(), instanceID, null);
		ArrayList<String> paramsToHide = getParamsToHide();
		for (MPInstancePara ip : instance.getParameters()) {

			if (paramsToHide.contains(ip.getParameterName()))
				continue;
			else if (ip.getParameterName().equals("C_AcctSchema_ID") && MAcctSchema.getClientAcctSchema(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx())).length == 1) // Schéma masqué si un seul sur la société
				continue;
			else if (ip.getParameterName().equals("PostingType") && ip.getP_String().equals(MFactAcct.POSTINGTYPE_Actual)) // Type d'écriture masqué si Réel
				continue;

			if (!Util.isEmpty(ip.getInfo()) && !Util.isEmpty(ip.getInfo_To()))
				LfrUtil.add(m_paramsStr, MProcess.get(instance.getAD_Process_ID()).getParameter(ip.getParameterName()).get_Translation("Name") + " = " + ip.getInfo() + " > " + ip.getInfo_To(), ", ");
			else if (!Util.isEmpty(ip.getInfo()))
				LfrUtil.add(m_paramsStr, MProcess.get(instance.getAD_Process_ID()).getParameter(ip.getParameterName()).get_Translation("Name") + " = " + ip.getInfo(), ", ");
			else if (!Util.isEmpty(ip.getInfo_To()))
				LfrUtil.add(m_paramsStr, " > " + ip.getInfo_To(), ", ");
		}

		m_paramsStr.insert(0, "Params : ");
	}

	private StringBuilder gridContentSql = new StringBuilder("SELECT * FROM T_LFR_Report WHERE AD_PInstance_ID = ? ORDER BY T_LFR_Report_ID");
	private static ColumnInfo org = new ColumnInfo("Bureau", "LFR_FactAcctOrg", KeyNamePair.class, true, false, "o.AD_Org_ID");
	private static ColumnInfo dateAcct = new ColumnInfo("Date", "DateAcct", Timestamp.class);
	private static ColumnInfo glCat = new ColumnInfo("Journal", "LFR_GLCategoryPrintName", KeyNamePair.class, true, false, "glc.GL_Category_ID");
	private static ColumnInfo libelle = new ColumnInfo("Libellé", "LFR_FactAcctDescription", String.class);
	private static ColumnInfo lettrage = new ColumnInfo("Lettrage", "LFR_MatchCode || ' - ' || to_char(LFR_ReconciliationDate, 'DD/MM/YYYY') AS InfoLettrage", String.class);
	private static ColumnInfo matchCode = new ColumnInfo("Let. code", "LFR_MatchCode", String.class);
	private static ColumnInfo recDate = new ColumnInfo("Let. date", "LFR_ReconciliationDate", String.class);
	private static ColumnInfo amtAcctDr = new ColumnInfo(Msg.translate(Env.getCtx(), "AmtAcctDr"), "AmtAcctDr", BigDecimal.class);
	private static ColumnInfo amtAcctCr = new ColumnInfo(Msg.translate(Env.getCtx(), "AmtAcctCr"), "AmtAcctCr", BigDecimal.class);
	private static ColumnInfo solde = new ColumnInfo("Solde", "LFR_SoldeProgressif", BigDecimal.class);
	
	private ColumnInfo[] getLayout(int instanceID, String layoutId) { // si > 1000000 -> printformat sinon hardcoded

		setParamStr(instanceID);
		Object value = fProcessMain.getSelectedItem().getValue();
		
		if (layoutId.length() > Integer.toString(MTable.MAX_OFFICIAL_ID).length()) {
			int printFormatID = Integer.valueOf(layoutId);
			MPrintFormat pf = MPrintFormat.get(printFormatID);

			ArrayList<ColumnInfo> list = new ArrayList<>();
			boolean hasKeyPairColSQL = false;
			for (MPrintFormatItem item :  pf.getAllItems()) {
				if (!item.isPrinted())
					continue;
				MColumn col = MColumn.get(item.getAD_Column_ID());
				ColumnInfo ci = new ColumnInfo(item.get_Translation("PrintName"), col.getColumnName(), DisplayType.getClass(col.getAD_Reference_ID(), true), true, false, null, col.getColumnName());

				if (!hasKeyPairColSQL && ci.getColClass() == String.class)
					ci.setAD_Reference_ID(printFormatID);

				list.add(ci);
			}		
// TODO le nom de la table utilisée dans gridContentSql doit être lié à la table/vue utilisée par le printformat
			ColumnInfo[] array = list.toArray(ColumnInfo[]::new);
			return array;	
		}

		ColumnInfo[] retValue = null;

		if (layoutId.equals(LAYOUT_01)) retValue = getLayout01();
		if (layoutId.equals(LAYOUT_02)) retValue = getLayout02();
		if (layoutId.equals(LAYOUT_03)) retValue = getLayout03();
		if (layoutId.equals(LAYOUT_04)) retValue = getLayout04();
		if (layoutId.equals(LAYOUT_05)) retValue = getLayout05();

		String orderBy = " ORDER BY T_LFR_Report_ID";
		if (getType((String) value).equals(SYSCONFIG_TYPE_AUX_CLT) || getType((String) value).equals(SYSCONFIG_TYPE_AUX_FRS))
			orderBy = " ORDER BY Line";

		gridContentSql = new StringBuilder("");

		for (ColumnInfo ci : retValue)
			LfrUtil.add(gridContentSql, ci.getColSQL(), ", ");

		gridContentSql.insert(0, "SELECT Fact_Acct_ID, ");
		gridContentSql.append(" FROM T_LFR_Report WHERE AD_PInstance_ID = ?").append(orderBy);

		return retValue;
	}

	protected String getLayoutName(String id) throws AdempiereSystemError {
		if (id.equals(LAYOUT_01)) return getLayoutName01();
		if (id.equals(LAYOUT_02)) return getLayoutName02();
		if (id.equals(LAYOUT_03)) return getLayoutName03();
		if (id.equals(LAYOUT_04)) return getLayoutName04();
		if (id.equals(LAYOUT_05)) return getLayoutName05();
		throw new AdempiereSystemError("this layout doesn't exists " + id);
	}
	
	protected String getLayoutName01() { return "par défaut"; }
	protected String getLayoutName02() { return "sans bureau"; }
	protected String getLayoutName03() { return "détail lettrage"; }
	protected String getLayoutName04() { return "ec aux"; }
	protected String getLayoutName05() { return "ec aux détail lettrage"; }
	
	
	protected ColumnInfo[] getLayout01() {
		return new ColumnInfo[] {setIdColumn(org, 0), dateAcct, glCat, libelle, lettrage, amtAcctDr, amtAcctCr, solde};
	}
	protected ColumnInfo[] getLayout02() {
		return new ColumnInfo[] {dateAcct, setIdColumn(glCat, 1), libelle, lettrage, amtAcctDr, amtAcctCr, solde};
	}
	protected ColumnInfo[] getLayout03() {
		return new ColumnInfo[] {setIdColumn(org, 0), dateAcct, glCat, libelle, matchCode, recDate, amtAcctDr, amtAcctCr, solde};
	}
	protected ColumnInfo[] getLayout04() {
		ColumnInfo document = new ColumnInfo("Document", "DocTypeName", KeyNamePair.class, true, false, "DocTypeName");
		return new ColumnInfo[] {dateAcct, setIdColumn(org, 1), document, libelle, lettrage, amtAcctDr, amtAcctCr, solde};
	}
	protected ColumnInfo[] getLayout05() {
		ColumnInfo document = new ColumnInfo("Document", "DocTypeName", KeyNamePair.class, true, false, "DocTypeName");
		return new ColumnInfo[] {dateAcct, setIdColumn(org, 1), document, libelle, matchCode, recDate, amtAcctDr, amtAcctCr, solde};
	}
	
	/** Transforme un ColumnInfo de type String en KeyNamePair */
	private ColumnInfo setIdColumn(ColumnInfo ci, int idx) {
		ci.setColClass(KeyNamePair.class);
		m_columnInfoID = ci;
		idxColID = idx;
		return ci;
	}

	private void export(int processReportID, int printFormatID) { // TODO ? permettre de choisir le printFormatID ? si oui, il faut faire un autre Dialog.askForInput qui présente les PF associés au processReportID (s'il y en a plusieurs) 
		Callback<Object> callback = new Callback<Object>() {

			@Override
			public void onCallback(Object result) {

				if (result != null && result instanceof String) {
					export0(processReportID, printFormatID, result.toString());
				}
			}
		};

		try {
			MLookup lookup = MLookupFactory.get(Env.getCtx(), 0, 0, DisplayType.List, Env.getLanguage(Env.getCtx()), "", 
					MColumn.get(Env.getCtx(), MScheduler.Table_Name, MScheduler.COLUMNNAME_ReportOutputType).getAD_Reference_Value_ID(), false, "Value IN ('CSV', 'XLSX', 'PDF')");

			Dialog.askForInput("LFR_FactExtraitCompteExportType", lookup, DisplayType.TableDir, callback, AEnv.getDesktop(), 0, "Export " + fProcessMain.getSelectedItem().getLabel(), -1);
		} catch (Exception e) {
			Dialog.error(0, "Error", "Can't open output select" + e.getLocalizedMessage());
		}
	}

	private void export0(int processReportID, int printFormatID, String outputType) {

		int instanceID = m_instanceMainID;
		MPInstance ipSource = new MPInstance(Env.getCtx(), instanceID, null);
		String trxName = Trx.createTrxName("WDashboardExport");
		Trx trx = Trx.get(trxName, true);

		boolean success = true;
		ProcessInfo pi = null;
		MProcess process = MProcess.get(Env.getCtx(), processReportID);
		MPInstance instance = new MPInstance(process, -1, 0, null);

		try {

			MPInstancePara[] iParamsSource = ipSource.getParameters();

			MPInstancePara[] iParams = instance.getParameters();

			for (MPInstancePara iParamTo : iParams) {

				for (MPInstancePara iParamFrom : iParamsSource) {
					if (iParamFrom.getParameterName().equals(iParamTo.getParameterName())) {

						for (String column : instanceParamColumnsToCopy) {
							if (iParamFrom.get_Value(column) != null)
								iParamTo.set_ValueOfColumn(column, iParamFrom.get_Value(column));
						}

						iParamTo.saveEx();
						break;
					}
				}
			}

			if (outputType.equals(OUTPUT_JASPER)) {
				for (MPInstancePara ip : iParams) {
					if (ip.getParameterName().equals(PROCESS_PARAM_LFR_PINSTANCE_SOURCE_ID)) {
						ip.setP_Number(instanceID);
						ip.setInfo(Integer.toString(instanceID));
						ip.saveEx();
						break;
					}
				}
			}

			instance.setAD_PrintFormat_ID(printFormatID);
			instance.setReportType(outputType);
			instance.saveEx();

			pi = new ProcessInfo (process.getName(), process.getAD_Process_ID(), 0, 0);
			pi.setAD_User_ID(Env.getAD_User_ID(Env.getCtx()));
			pi.setAD_Client_ID(Env.getAD_Client_ID(Env.getCtx()));
			pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
			pi.setAD_Process_UU(process.getAD_Process_UU());
			pi.setIsBatch(true);
			pi.setPrintPreview(true);
			pi.setTransactionName(trxName);
			pi.setLanguageID(MLanguage.get(Env.getCtx(), Language.getLoginLanguage().getAD_Language()).getAD_Language_ID());

			if (instance.getAD_PrintFormat_ID() > 0)
				pi.setSerializableObject(new MPrintFormat(Env.getCtx(), instance.getAD_PrintFormat_ID(), trxName));
			pi.setReportType(outputType);

			if (!Util.isEmpty(process.getJasperReport())) {
				pi.setPrintPreview(false);
				pi.setExport(true);
				pi.setTransactionName(null);
				ServerProcessCtl.process(pi, null);

				if (pi.isError())
					throw new AdempiereException("Can't execute ProcessInfo : " + pi + " - " + pi.getSummary());
			}
			else
				ServerProcessCtl.process(pi, trx);

		} catch (Exception e) {
			s_log.severe("Error on Trx level : " + e.toString());
			success = false;
		} finally {
			trx.commit();
			trx.close();
		}

		if (!success) {
			Dialog.error(m_WindowNo, "Error", "impossible de générer l'état");
			return;
		}

		File file = null;

		if (pi != null) {
			if (outputType.equals("PDF")) {
				file = pi.getPDFReport();
			}
			else if (pi.getExportFile() != null) { // csv, xlsx et jasper
				file = pi.getExportFile();
			}
			else
				Dialog.error(m_WindowNo, "Error", "export file is null");
		}
		else
			Dialog.error(m_WindowNo, "Error", "pi is null");

		if (file != null) {
			if (outputType.equals(OUTPUT_JASPER)) {
				String contentType = MimeType.getMimeType(file.getName());

				try {
					m_east.setVisible(true);

					AMedia media = new AMedia(file.getName(), null, contentType, LfrUtil.readFile(file));
					preview.setContent(media);
					preview.setVisible(true);
					preview.invalidate();

					setParamStr(instance.getAD_PInstance_ID());
				} catch (Exception e) {
					Dialog.error(m_WindowNo, "Error", e.toString());
				}
			}
			else {
				try {
					Filedownload.save(file, outputType);
				} catch (FileNotFoundException e) {
					s_log.severe(e.toString());
					Dialog.error(m_WindowNo, "Error", "Can't download file " + e);
				}
			}
		}
		else
			Dialog.error(m_WindowNo, "Error", "file is null");
	}

	private static final String[] instanceParamColumnsToCopy = {
			MPInstancePara.COLUMNNAME_Info,
			MPInstancePara.COLUMNNAME_Info_To,
			MPInstancePara.COLUMNNAME_IsNotClause,
			MPInstancePara.COLUMNNAME_P_Date,
			MPInstancePara.COLUMNNAME_P_Date_To,
			MPInstancePara.COLUMNNAME_P_Number,
			MPInstancePara.COLUMNNAME_P_Number_To,
			MPInstancePara.COLUMNNAME_P_String,
			MPInstancePara.COLUMNNAME_P_String_To
	};
	
	private void populateProcessField(Listbox fProcessField) {
		
		fProcessField.appendItem("", -1);
		List<List<Object>> rows = DB.getSQLArrayObjectsEx(null, "SELECT Name FROM AD_SysConfig wHERE AD_Client_ID IN (0, ?) AND Name LIKE ? AND IsActive = 'Y' ORDER BY Name", Env.getAD_Client_ID(Env.getCtx()), SYSCONFIG_NAME_PREFIX + "%");
		if (rows != null && rows.size() > 0) {
			for (List<Object> row : rows) {
				String scName = (String) row.get(0);
				int processID = Integer.valueOf(getProcessID(scName));
				if (LfrUtil.getProcessAccess(processID)) {
					String name = MProcess.get(processID).get_Translation("Name");
					fProcessField.appendItem(name, scName);
				}
			}
		}
	}

	private String getSysConfig(String sysConfig) {
		return MSysConfig.getValue(sysConfig, "", Env.getAD_Client_ID(Env.getCtx()));
	}
	
	protected int getProcessID(String sysConfig) {
		return getValueFromSysConfigAsInt(sysConfig, SYSCONFIG_PROCESS_PREFIX + SYSCONFIG_EQ, true);
	}
	
	protected int getJasperProcessID(String sysConfig) {
		return getValueFromSysConfigAsInt(sysConfig, SYSCONFIG_JASPER_PREFIX + SYSCONFIG_EQ, false);
	}

	protected int getExportProcessID(String sysConfig) {
		return getValueFromSysConfigAsInt(sysConfig, SYSCONFIG_EXPORT_PREFIX + SYSCONFIG_EQ, false);
	}

	protected String getType(String sysConfig) {
		return getValueFromSysConfig(sysConfig, SYSCONFIG_TYPE_PREFIX + SYSCONFIG_EQ, true);
	}

	protected String getLayout(String sysConfig) {
		String scValue = getSysConfig(sysConfig);
		int idx = scValue.indexOf(SYSCONFIG_LAYOUT_PREFIX + SYSCONFIG_LAYOUT_START);
		if (idx >= 0) {
			int end = scValue.indexOf(SYSCONFIG_LAYOUT_END, idx);
			
			String processid = scValue.substring(idx + (SYSCONFIG_LAYOUT_PREFIX + SYSCONFIG_LAYOUT_START).length(), end);
			return processid;
		}
		throw new AdempiereUserError(showSysConfigError(sysConfig, scValue));
	}
	
	protected String getValueFromSysConfig(String sysConfig, String search, boolean throwException) {
		String scValue = getSysConfig(sysConfig);
		int idx = scValue.indexOf(search);
		if (idx >= 0) {
			int end = scValue.indexOf(SYSCONFIG_SEP, idx);
			if (end < 0)
				end = scValue.length();
			
			String retValue = scValue.substring(idx + search.length(), end);
			return retValue;
		}
		if (throwException)
			throw new AdempiereUserError(showSysConfigError(sysConfig, scValue));
		return "";
	}
	
	protected int getValueFromSysConfigAsInt(String sysConfig, String search, boolean throwException) {
		
		String value = getValueFromSysConfig(sysConfig, search, throwException);
		if (!Util.isEmpty(value)) {
			try {
				return Integer.valueOf(value);
			}
			catch(Exception e) {
				throw new AdempiereUserError("Wrong configuration on '" + sysConfig + "' as '" + value + "' is not an integer");
			}	
		}

		return -1;
	}

	private String showSysConfigError(String scName, String scValue) {
		return "Wrong configuration on '" + scName + "' as can't find '" + "" + "' on '" + scValue + "'";
	}

	public File getJasperAsFile(Iframe iframe) {

		File file = null;
		try {
			file = FileUtil.createTempFile("grid2Jasper", ".pdf");
		} catch (IOException e) {
			throw new AdempiereException("Error while generating the pdf file" + e);
		}

		return LfrWebuiUtil.mediaToFile(iframe.getContent(), file);
	}
	
	public ADForm getForm() {
		return form;
	}

	private int getSelectedFactAcctID() {
		int selected = table.getSelectedRow();

		if (selected == -1)
			return -1;

		return ((KeyNamePair) table.getModel().getValueAt(selected, idxColID)).getKey();
	}

	private void zoomToFact() {
		AEnv.zoom(MFactAcct.Table_ID, getSelectedFactAcctID());
	}

	private void zoomToDocument() {
		MFactAcct fa = new MFactAcct(Env.getCtx(), getSelectedFactAcctID(), null);
		AEnv.zoom(fa.getAD_Table_ID(), fa.getRecord_ID());
	}

	private void showPopupOnTableSub(Event event) {

		m_popup = new Menupopup();

		Menuitem menuItem = new Menuitem("Zoom document");
		menuItem.addEventListener(Events.ON_CLICK, this);
		menuItem.setValue(POPUP_ZOOM_DOC);
		menuItem.setParent(m_popup);

		menuItem = new Menuitem("Zoom écriture");
		menuItem.addEventListener(Events.ON_CLICK, this);
		menuItem.setValue(POPUP_ZOOM_FA);
		menuItem.setParent(m_popup);

		m_popup.setPage(event.getPage());
		m_popup.open(event.getTarget());
	}
}   //  WLFRFactExtraitCompte
