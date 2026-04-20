/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd. All Rights Reserved.                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package fr.idempiere.webui.apps.form;

import static fr.idempiere.model.SystemIDs_LFR.LFR_ACCT_VIEWER_SHOW_DISPLAY_QTY;
import static fr.idempiere.model.SystemIDs_LFR.LFR_ACCT_VIEWER_SHOW_DISPLAY_SOURCE;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.adempiere.base.upload.IUploadService;
import org.adempiere.util.Callback;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.Extensions;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.acct.WAcctViewerData;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Datebox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.component.VerticalBox;
import org.adempiere.webui.component.WListItemRenderer;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.InfoPanel;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.DateRangeButton;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaElement;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAuthorizationAccount;
import org.compiere.model.MClientInfo;
import org.compiere.model.MColumn;
import org.compiere.model.MFactAcct;
import org.compiere.model.MJournal;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPeriod;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.SystemIDs;
import org.compiere.model.X_AD_CtxHelp;
import org.compiere.model.X_C_AcctSchema_Element;
import org.compiere.report.core.RModel;
import org.compiere.report.core.RModelExcelExporter;
import org.compiere.tools.FileUtil;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.idempiere.ui.zk.media.IMediaView;
import org.idempiere.ui.zk.media.Medias;
import org.idempiere.ui.zk.media.WMediaOptions;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;

import fr.idempiere.util.LfrFactReconciliationUtil;

/**
 *  LFR Account Viewer
 *	@author Nicolas Micoud - TGI
 */
public class WLFRAcctViewer extends ADForm implements EventListener<Event>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1486130498847806234L;

	/* Predefined context variables to initialize the form */
	public static final String INITIAL_RECORD_ID = "_Initial_Record_ID_";
	public static final String INITIAL_AD_TABLE_ID = "_Initial_AD_Table_ID_";

	private static final String TITLE = "Posting";

	private static final int PAGE_SIZE = 1000;

	/** State Info          */
	private WLFRAcctViewerData	m_data = null;
	private WTableDirEditor fAcctSchema, fPostingType, fTable, fOrg, fSortBy1, fSortBy2, fSortBy3, fSortBy4, fLettrage;
//	private Listbox selAcctSchema = new Listbox();
//	private Listbox selTable = new Listbox();
//	private Listbox selPostingType = new Listbox();
//	private Listbox selOrg = new Listbox();
//	private Listbox sortBy1 = new Listbox();
//	private Listbox sortBy2 = new Listbox();
//	private Listbox sortBy3 = new Listbox();
//	private Listbox sortBy4 = new Listbox();

	private Button selRecord = new Button();
	private Button selAcct = new Button();
	private Button bQuery = new Button();
	private Button bRePost = new Button();
	private Button bExport = new Button();
	private Button bZoom = new Button(); // Elaine 2009/07/29
	private Button sel1 = new Button();
	private Button sel2 = new Button();
	private Button sel3 = new Button();
	private Button sel4 = new Button();
	private Button sel5 = new Button();
	private Button sel6 = new Button();
	private Button sel7 = new Button();
	private Button sel8 = new Button();

	private Label statusLine = new Label();
	private Label lsel1 = new Label();
	private Label lsel2 = new Label();
	private Label lsel3 = new Label();
	private Label lsel4 = new Label();
	private Label lsel5 = new Label();
	private Label lsel6 = new Label();
	private Label lsel7 = new Label();
	private Label lsel8 = new Label();

	private Label lacctSchema = new Label();
	private Label lpostingType = new Label();
	private Label lOrg = new Label();
	private Label lAcct = new Label();
	private Label lDate = new Label();
	private Label lSort = new Label();
	private Label lGroup = new Label();

	private WDateEditor selDateFrom = new WDateEditor();
	private WDateEditor selDateTo = new WDateEditor();

	private Checkbox selDocument = new Checkbox();
	private Checkbox displayQty = new Checkbox();
	private Checkbox displaySourceAmt = new Checkbox();
	private Checkbox displayDocumentInfo = new Checkbox();
	private Checkbox group1 = new Checkbox();
	private Checkbox group2 = new Checkbox();
	private Checkbox group3 = new Checkbox();
	private Checkbox group4 = new Checkbox();
	private Checkbox forcePost = new Checkbox();
	
	private Checkbox displayLines0 = new Checkbox();
	private Checkbox groupByAccount = new Checkbox();

	private Tabbox tabbedPane = new Tabbox();

	private Listbox table = new Listbox();
	private Paging paging = new Paging();

	private VerticalBox displayPanel = new VerticalBox();
	private VerticalBox selectionPanel = new VerticalBox();

	private Tab tabQuery = new Tab();
	private Tab tabResult = new Tab();
	private Tabs tabs = new Tabs();
	private Tabpanel result = new Tabpanel();
	private Tabpanel query = new Tabpanel();
	private Tabpanels tabpanels = new Tabpanels();

	private Hlayout southPanel = new Hlayout();

	private ArrayList<ArrayList<Object>> m_queryData;

	private South pagingPanel;

	private Borderlayout resultPanel;

	private RModel m_rmodel;
	/**
	 * SysConfig USE_ESC_FOR_TAB_CLOSING
	 */
	private boolean isUseEscForTabClosing = MSysConfig.getBooleanValue(MSysConfig.USE_ESC_FOR_TAB_CLOSING, false, Env.getAD_Client_ID(Env.getCtx()));


	private int m_defaultTableID = MAllocationHdr.Table_ID;
	private Label lLettrage = new Label();
	private Datebox selLettrageDate = new Datebox();
	
	/**	Logger				*/
	private static final CLogger log = CLogger.getCLogger(WLFRAcctViewer.class);

	/**
	 *  Default constructor
	 */
	public WLFRAcctViewer()
	{
		this (0, 0, 0);
	} // AcctViewer

	/**
	 *  Detail Constructor
	 *
	 *  @param AD_Client_ID Client
	 *  @param AD_Table_ID Table
	 *  @param Record_ID Record
	 */
	public WLFRAcctViewer(int AD_Client_ID, int AD_Table_ID, int Record_ID)
	{
	super ();

		if (log.isLoggable(Level.INFO))
			log.info("AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + Record_ID);

		m_data = new WLFRAcctViewerData (Env.getCtx(), m_WindowNo, AD_Client_ID, AD_Table_ID);

		try
		{
			init();
			setAttribute(MODE_KEY, MODE_EMBEDDED);
			setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
			setAttribute(IDesktop.WINDOWNO_ATTRIBUTE, m_WindowNo);	// for closing the window with shortcut
	    	SessionManager.getSessionApplication().getKeylistener().addEventListener(Events.ON_CTRL_KEY, this);
	    	addEventListener(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT, this);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}

	/**
	 *  Layout window
	 *  <pre>
	 *  - mainPanel
	 *      - tabbedPane
	 *          - query
	 *          - result
	 *          - graphPanel
	 *  </pre>
	 *  @throws Exception
	 */
	private void init() throws Exception
	{
		// Selection Panel
		ZKUpdateUtil.setHflex(selectionPanel, "1");
			// Accounting Schema
		
		Grid grid = new Grid();
		ZKUpdateUtil.setHflex(grid, "1");
		grid.setSclass("grid-layout");
		
		selectionPanel.appendChild(grid);
		
		Columns columns = new Columns();
		grid.appendChild(columns);
		Column column = new Column();
		ZKUpdateUtil.setWidth(column, "30%");
		columns.appendChild(column);
		column = new Column();
		ZKUpdateUtil.setWidth(column, "60%");
		columns.appendChild(column);
		
		Rows rows = grid.newRows();

		lacctSchema.setValue(Msg.translate(Env.getCtx(), "C_AcctSchema_ID"));
		MLookup lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, 2463, DisplayType.Table, Env.getLanguage(Env.getCtx()), "C_AcctSchema_ID", 
				0, false, "");
		fAcctSchema = new WTableDirEditor("C_AcctSchema_ID", true, false, true, lookup);

		Row row = rows.newRow();
		row.appendChild(lacctSchema);
		row.appendChild(fAcctSchema.getComponent());

		selDocument.setLabel(Msg.getMsg(Env.getCtx(), "SelectDocument"));
		selDocument.addEventListener(Events.ON_CHECK, this);
		
		StringBuilder tableValidation = new StringBuilder("EXISTS (SELECT * FROM AD_Column c WHERE AD_Table.AD_Table_ID=c.AD_Table_ID AND c.IsActive='Y' AND c.ColumnName='Posted') AND AD_Table.IsView='N'");
		String excludedTableName = MSysConfig.getValue("LFR_ACCT_VIEWER_EXCLUDED_TABLENAMES", "", Env.getAD_Client_ID(Env.getCtx()));
		if (!Util.isEmpty(excludedTableName)) {
			StringBuilder tableNames = new StringBuilder("");
			for (String tableName : excludedTableName.split(",")) {
				if (tableNames.length() > 0)
					tableNames.append(", ");
				tableNames.append(DB.TO_STRING(tableName.trim()));
			}
			tableValidation.append(" AND AD_Table.TableName NOT IN (").append(tableNames).append(")");
		}

		int columnID = MColumn.getColumn_ID(MTable.Table_Name, MTable.COLUMNNAME_AD_Table_ID);
		lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, columnID, DisplayType.Table, Env.getLanguage(Env.getCtx()), "AD_Table_ID", 0, false, tableValidation.toString());
		fTable = new WTableDirEditor("AD_Table_ID", true, false, true, lookup);
		fTable.getComponent().addEventListener(Events.ON_SELECT, this);

		row = rows.newRow();
		row.appendChild(selDocument);
		Hlayout hlayout = new Hlayout();
		hlayout.appendChild(fTable.getComponent());
		hlayout.appendChild(selRecord);
		row.appendChild(hlayout);		

		// Posting Type
		lpostingType.setValue(Msg.translate(Env.getCtx(), "PostingType"));
		int refID = MColumn.get(Env.getCtx(), MFactAcct.Table_Name, MFactAcct.COLUMNNAME_PostingType).getAD_Reference_Value_ID();
		StringBuilder postingTypeValidation = new StringBuilder("Value IN (").append(DB.TO_STRING(MFactAcct.POSTINGTYPE_Actual)).append(", ").append(DB.TO_STRING(MFactAcct.POSTINGTYPE_Budget)).append(")");
		lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, DisplayType.List, Env.getLanguage(Env.getCtx()), "", refID, false, postingTypeValidation.toString());
		fPostingType = new WTableDirEditor("PostingType", false, false, true, lookup);
		if (m_data.AD_Table_ID != MJournal.Table_ID)
			fPostingType.setValue(MFactAcct.POSTINGTYPE_Actual);
		
		row = rows.newRow();
		row.appendChild(lpostingType);
		row.appendChild(fPostingType.getComponent());

			// Date

		lDate.setValue(Msg.translate(Env.getCtx(), "DateAcct"));

		row = rows.newRow();
		row.appendChild(lDate);
		hlayout = new Hlayout();
		hlayout.appendChild(selDateFrom.getComponent());		
		hlayout.appendChild(new Label(" - "));
		hlayout.appendChild(selDateTo.getComponent());
		DateRangeButton drb = (new DateRangeButton(selDateFrom, selDateTo));
		hlayout.appendChild(drb);
		row.appendChild(hlayout);

			// Organization

		lOrg.setValue(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		lookup = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, 2163, DisplayType.TableDir);
		fOrg = new WTableDirEditor ("AD_Org_ID", false, false, true, lookup);
		
		row = rows.newRow();
		row.appendChild(lOrg);
		row.appendChild(fOrg.getComponent());
		

		lLettrage.setValue(Msg.translate(Env.getCtx(), "XXA_Lettrage"));
		lookup = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, DisplayType.List, Env.getLanguage(Env.getCtx()), "", SystemIDs.REFERENCE_YESNO, false, "");
		fLettrage = new WTableDirEditor("XXA_Lettrage", false, false, true, lookup);
		lDate.setValue(Msg.translate(Env.getCtx(), "DateAcct"));

		row = rows.newRow();
		row.appendChild(lLettrage);
		hlayout = new Hlayout();
		hlayout.appendChild(fLettrage.getComponent());		
		hlayout.appendChild(new Label(" - "));
		hlayout.appendChild(selLettrageDate);
		row.appendChild(hlayout);

			// Account

		lAcct.setValue(Msg.translate(Env.getCtx(), "Account_ID"));

		row = rows.newRow();
		row.appendChild(lAcct);
		row.appendChild(selAcct);

		row = rows.newRow();
		row.appendChild(lsel1);
		row.appendChild(sel1);

		row = rows.newRow();
		row.appendChild(lsel2);
		row.appendChild(sel2);

		row = rows.newRow();
		row.appendChild(lsel3);
		row.appendChild(sel3);

		row = rows.newRow();
		row.appendChild(lsel4);
		row.appendChild(sel4);

		row = rows.newRow();
		row.appendChild(lsel5);
		row.appendChild(sel5);

		row = rows.newRow();
		row.appendChild(lsel6);
		row.appendChild(sel6);

		row = rows.newRow();
		row.appendChild(lsel7);
		row.appendChild(sel7);

		row = rows.newRow();
		row.appendChild(lsel8);
		row.appendChild(sel8);
		
		//Display Panel

			// Display Document Info

		displayDocumentInfo.setLabel(Msg.getMsg(Env.getCtx(), "DisplayDocumentInfo"));
		displayDocumentInfo.addEventListener(Events.ON_CLICK, this);

			// Display Source Info

		displaySourceAmt.setLabel(Msg.getMsg(Env.getCtx(), "DisplaySourceInfo"));
		displaySourceAmt.addEventListener(Events.ON_CHECK, this);

			// Display Quantity

		displayQty.setLabel(Msg.getMsg(Env.getCtx(), "DisplayQty"));
		displayQty.addEventListener(Events.ON_CHECK, this);

		displayLines0.setLabel(Msg.getMsg(Env.getCtx(), "LFR_AcctViewerDisplayLines0"));
		groupByAccount.setLabel(Msg.getMsg(Env.getCtx(), "LFR_AcctViewerGroupByAccount"));
		groupByAccount.addEventListener(Events.ON_CHECK, this);

		ZKUpdateUtil.setWidth(displayPanel, "100%");
		displayPanel.appendChild(displayDocumentInfo);

		if (isShowDisplaySourceAmt())
			displayPanel.appendChild(displaySourceAmt);

		if (isShowDisplayQty())
			displayPanel.appendChild(displayQty);

		displayPanel.appendChild(displayLines0);
		displayPanel.appendChild(new Space());
		displayPanel.appendChild(groupByAccount);
		displayPanel.appendChild(new Space());
		
		grid = new Grid();
		grid.setSclass("grid-layout");
		ZKUpdateUtil.setHflex(grid, "1");
		displayPanel.appendChild(grid);
		columns = new Columns();
		grid.appendChild(columns);
		column = new Column();
		ZKUpdateUtil.setWidth(column, "70%");
		columns.appendChild(column);
		column = new Column();
		ZKUpdateUtil.setWidth(column, "30%");
		columns.appendChild(column);

		rows = grid.newRows();
		row = rows.newRow();
		lSort.setValue(Msg.getMsg(Env.getCtx(), "SortBy"));
		lGroup.setValue(Msg.getMsg(Env.getCtx(), "GroupBy"));
		row.appendChild(lSort);
		ZKUpdateUtil.setHflex(lSort, "1");
		row.appendChild(lGroup);

		lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, DisplayType.List, Env.getLanguage(Env.getCtx()), "", 0, false, "");

		fSortBy1 = new WTableDirEditor("SortBy1", false, false, true, lookup);
		fSortBy2 = new WTableDirEditor("SortBy2", false, false, true, lookup);
		fSortBy3 = new WTableDirEditor("SortBy3", false, false, true, lookup);
		fSortBy4 = new WTableDirEditor("SortBy4", false, false, true, lookup);
		
//		fSortBy1.setValue("DateAcct");
		
		row = rows.newRow();		
		row.appendChild(fSortBy1.getComponent());
		row.appendChild(group1);

		row = rows.newRow();
		row.appendChild(fSortBy2.getComponent());
		row.appendChild(group2);

		row = rows.newRow();
		row.appendChild(fSortBy3.getComponent());
		row.appendChild(group3);

		row = rows.newRow();
		row.appendChild(fSortBy4.getComponent());
		row.appendChild(group4);

		Groupbox groupDisplay = new Groupbox();
		Caption capDisplay = new Caption(Msg.getMsg(Env.getCtx(), "Display"));
		groupDisplay.appendChild(capDisplay);
		groupDisplay.appendChild(displayPanel);

		Groupbox groupSelection = new Groupbox();
		Caption capSelection = new Caption(Msg.getMsg(Env.getCtx(), "Selection"));
		groupSelection.appendChild(capSelection);
		groupSelection.appendChild(selectionPanel);

		Hlayout boxQueryPanel = new Hlayout();
		ZKUpdateUtil.setHflex(boxQueryPanel, "3");

		boxQueryPanel.appendChild(groupSelection);
		ZKUpdateUtil.setHflex(groupSelection, "2");
		Separator separator = new Separator();
		separator.setOrient("vertical");
		boxQueryPanel.appendChild(separator);
		boxQueryPanel.appendChild(groupDisplay);
		ZKUpdateUtil.setHflex(groupDisplay, "1");

		//  South Panel

		bRePost.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "RePost")));
		bRePost.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "RePostInfo")));
		bRePost.addEventListener(Events.ON_CLICK, this);
		bRePost.setVisible(false);

		forcePost.setLabel(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Force")));
		forcePost.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "ForceInfo")));
		forcePost.setVisible(false);

		// Elaine 2009/07/29
		if (ThemeManager.isUseFontIconForImage())
			bZoom.setIconSclass("z-icon-Zoom");
		else
			bZoom.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
		bZoom.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Zoom")));
		bZoom.setVisible(tabbedPane.getSelectedIndex() == 1);
		bZoom.addEventListener(Events.ON_CLICK, this);
		//
		
		if (ThemeManager.isUseFontIconForImage())
			bQuery.setIconSclass("z-icon-Refresh");
		else
			bQuery.setImage(ThemeManager.getThemeResource("images/Refresh16.png"));
		bQuery.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Refresh")));
		bQuery.addEventListener(Events.ON_CLICK, this);

		if (ThemeManager.isUseFontIconForImage())
			bExport.setIconSclass("z-icon-Export");
		else
			bExport.setImage(ThemeManager.getThemeResource("images/Export16.png"));
		bExport.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Export")));
		bExport.addEventListener(Events.ON_CLICK, this);
		bExport.setVisible(false);

		ZKUpdateUtil.setHflex(southPanel, "5");
		Grid southLeftGrid = new Grid();
		southLeftGrid.setSclass("grid-layout");
		southPanel.appendChild(southLeftGrid);
		ZKUpdateUtil.setHflex(southLeftGrid, "4");
		rows = southLeftGrid.newRows();
		Row southLeft = rows.newRow();
		Hlayout repostLayout = new Hlayout();
		southLeft.appendChild(repostLayout);
		repostLayout.appendChild(bRePost);
		repostLayout.appendChild(new Separator());
		repostLayout.appendChild(forcePost);
		ZKUpdateUtil.setVflex(repostLayout, "1");
		southLeft.appendChild(statusLine);
		
		Grid southRight = new Grid();
		southRight.setSclass("grid-layout");
		ZKUpdateUtil.setHflex(southRight, "1");
		southPanel.appendChild(southRight);
		Panel southRightPanel = new Panel();
		southRightPanel.setStyle("display: flex; flex-direction: row; align-items: center; gap: 5px; justify-content: flex-end;");
		southRightPanel.appendChild(bZoom); // Elaine 2009/07/29
		southRightPanel.appendChild(bExport);
		southRightPanel.appendChild(bQuery);
		rows = southRight.newRows();
		row = rows.newRow();
		row.setAlign("right");
		row.appendChild(southRightPanel);

		// Result Tab

		resultPanel = new Borderlayout();
		resultPanel.setStyle("position: absolute");
		ZKUpdateUtil.setWidth(resultPanel, "99%");
		ZKUpdateUtil.setHeight(resultPanel, "99%");
		result.appendChild(resultPanel);

		Center resultCenter = new Center();
		resultPanel.appendChild(resultCenter);
		ZKUpdateUtil.setHflex(table, "1");
		ZKUpdateUtil.setVflex(table, true);
		resultCenter.appendChild(table);
		ZKUpdateUtil.setHflex(table, "1");
		table.addEventListener(Events.ON_DOUBLE_CLICK, this);
		if (ClientInfo.isMobile())
			table.setSizedByContent(true);

		pagingPanel = new South();
		resultPanel.appendChild(pagingPanel);
		pagingPanel.appendChild(paging);

		ZKUpdateUtil.setHflex(result, "1");
		ZKUpdateUtil.setHeight(result, "100%");
		result.setStyle("position: relative");

		paging.addEventListener("onPaging", this);
		paging.setAutohide(true);
		paging.setDetailed(true);

		// Query Tab

		ZKUpdateUtil.setHflex(query, "1");
		query.appendChild(boxQueryPanel);

		// Tabbox

		tabQuery.addEventListener(Events.ON_SELECT, this);
		tabQuery.setLabel(Msg.getMsg(Env.getCtx(), "ViewerQuery").replaceAll("[&]", ""));

		tabResult.addEventListener(Events.ON_SELECT, this);
		tabResult.setLabel(Msg.getMsg(Env.getCtx(), "ViewerResult").replaceAll("[&]", ""));

		tabs.appendChild(tabQuery);
		tabs.appendChild(tabResult);

		ZKUpdateUtil.setHflex(tabpanels, "1");
		tabpanels.appendChild(query);
		tabpanels.appendChild(result);

		ZKUpdateUtil.setHflex(tabbedPane, "1");
		ZKUpdateUtil.setVflex(tabbedPane, "1");
		tabbedPane.appendChild(tabs);
		tabbedPane.appendChild(tabpanels);

		Borderlayout layout = new Borderlayout();
		layout.setParent(this);
		ZKUpdateUtil.setHeight(layout, "100%");
		ZKUpdateUtil.setWidth(layout, "100%");
		layout.setStyle("background-color: transparent; margin: 0; position: relative; padding: 0;");

		Center center = new Center();
		center.setParent(layout);
		center.setStyle("background-color: transparent; padding: 2px;");
		tabbedPane.setParent(center);
		ZKUpdateUtil.setHflex(tabbedPane, "1");
		ZKUpdateUtil.setVflex(tabbedPane, "1");

		South south = new South();
		south.setParent(layout);
		south.setStyle("background-color: transparent");
		ZKUpdateUtil.setHeight(south, "36px");
		southPanel.setParent(south);
		ZKUpdateUtil.setVflex(southPanel, "1");
		ZKUpdateUtil.setHflex(southPanel, "1");
	}

	/**
	 *  Dynamic Init
	 *
	 *  @param AD_Table_ID table
	 *  @param Record_ID record
	 */
	private void dynInit (int AD_Table_ID, int Record_ID)
	{
/*		m_data.validateAcctSchemas(Record_ID); voir si diff�rence avec m�thode tgi ?
		m_data.fillAcctSchema(selAcctSchema );
		selAcctSchema.addEventListener(Events.ON_SELECT, this);
		selAcctSchema.setSelectedIndex(0);
*/		
		
		
		MClientInfo info = MClientInfo.get(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), null);
		fAcctSchema.setValue(info.getC_AcctSchema1_ID());
		m_data.validateAcctSchemas(Record_ID);
		
		actionAcctSchema();

	//	m_data.fillTable(selTable);
	//	selTable.addEventListener(Events.ON_SELECT, this);

		if (ThemeManager.isUseFontIconForImage())
			selRecord.setIconSclass("z-icon-Find");
		else
			selRecord.setImage(ThemeManager.getThemeResource("images/Find16.png"));
		selRecord.addEventListener(Events.ON_CLICK, this);
		selRecord.setLabel("");

		//  Mandatory Elements

	//	m_data.fillOrg(selOrg);
		selAcct.setName("Account_ID");
		selAcct.addEventListener(Events.ON_CLICK, this);
		selAcct.setLabel("");
		if (ThemeManager.isUseFontIconForImage())
			selAcct.setIconSclass("z-icon-Find");
		else
			selAcct.setImage(ThemeManager.getThemeResource("images/Find16.png"));

		statusLine.setValue(" " + Msg.getMsg(Env.getCtx(), "ViewerOptions"));

		//  Document Select
		boolean haveDoc = (AD_Table_ID != 0 && Record_ID != 0);
		selDocument.setChecked(haveDoc);
		groupByAccount.setChecked(!haveDoc); // affichage d'un document -> pas de regroupement
		actionDocument();
		if (!haveDoc)
		{
			fTable.setValue(m_defaultTableID);
			actionTable();
		}
		else
		{
			if (setSelectedTable(AD_Table_ID, Record_ID))
			{
				actionQuery();
			}
			else
			{
				//reset
				haveDoc = false;
				selDocument.setChecked(haveDoc);
				actionDocument();
				fTable.setValue(m_defaultTableID);
				actionTable();
			}
		}

		if (tabResult.isSelected())
			stateChanged();
	} // dynInit

	/**
	 * set selected table and record id
	 * @param AD_Table_ID
	 * @param Record_ID
	 * @return true if AD_Table_ID is found, false otherwise
	 */
	private boolean setSelectedTable(int AD_Table_ID, int Record_ID)
	{
		for (Comboitem ci : fTable.getComponent().getItems()) {
			
			if ((Integer) ci.getValue() == AD_Table_ID) {
				fTable.setValue(AD_Table_ID);
				m_data.AD_Table_ID = AD_Table_ID;

				m_data.Record_ID = Record_ID;
				selRecord.setLabel("");
				selRecord.setName(MTable.getTableName(Env.getCtx(), AD_Table_ID) + "_ID");
				return true;				
			}
		}

		return false;
	}

	/**
	 *  Dispose window
	 */
	public void dispose()
	{
		m_data.dispose();
		m_data = null;
		this.detach();
	} // dispose;

	/**
	 *  After Tab Selection Changed
	 */
	public void stateChanged()
	{
		boolean visible = m_data.documentQuery && tabResult.isSelected();

		bRePost.setVisible(visible && !Env.isReadOnlySession());
		bExport.setVisible(tabResult.isSelected());
		bZoom.setVisible(tabResult.isSelected());

		forcePost.setVisible(visible && !Env.isReadOnlySession());
	}   //  stateChanged

	/**
	 *  Event Performed (Event Listener)
	 *  @param e Event
	 */
	@Override
	public void onEvent(Event e) throws Exception
	{
		Object source = e.getTarget();

		if (source == tabResult)
			stateChanged();
		else if (source == tabQuery)
			stateChanged();
		else if (source == fAcctSchema.getComponent())
			actionAcctSchema();
		else if (source == bQuery)
			actionQuery();
		else if (source == selDocument)
			actionDocument();
		else if (source == fTable.getComponent())
			actionTable();
		else if (source == bRePost)
			actionRePost();
		else if  (source == bExport)
			actionExport();
		// Elaine 2009/07/29
		else if (source == bZoom)
			actionZoom();

		//
		//  InfoButtons
		else if (source instanceof Button)
			actionButton((Button)source);
		else if (source == paging)
		{
			int pgno = paging.getActivePage();
			int start = pgno * PAGE_SIZE;
			int end = start + PAGE_SIZE;
			if ( end > paging.getTotalSize())
				end = paging.getTotalSize();
			List<ArrayList<Object>> list = m_queryData.subList(start, end);
			ListModelTable model = new ListModelTable(list);
			table.setModel(model);
		}
		else if (Events.ON_DOUBLE_CLICK.equals(e.getName()) && source instanceof Listbox && source == table && !groupByAccount.isChecked()) {
			actionZoomFactAcct();
		}
		else if (e.getName().equals(Events.ON_CTRL_KEY)) {
        	KeyEvent keyEvent = (KeyEvent) e;
			if (LayoutUtils.isReallyVisible(this))
				this.onCtrlKeyEvent(keyEvent);
		}
		else if(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT.equals(e.getName())) {
        	IDesktop desktop = SessionManager.getAppDesktop();
        	if (m_WindowNo > 0 && desktop.isCloseTabWithShortcut())
        		desktop.closeWindow(m_WindowNo);
        	else
        		desktop.setCloseTabWithShortcut(true);
        }
	} // onEvent

	/**
	 * Export to excel.<br/>
	 * Show excel viewer if available.
	 */
	private void actionExport() {
		if (m_rmodel != null && m_rmodel.getRowCount() > 0) {
			RModelExcelExporter exporter = new RModelExcelExporter(m_rmodel);
			File file;
			try {
				file = new File(FileUtil.getTempMailName(Msg.getMsg(Env.getCtx(), TITLE), ".xlsx"));
				exporter.export(file, Env.getLanguage(Env.getCtx()));
				AMedia media = new AMedia(file.getName(), null, Medias.EXCEL_XML_MIME_TYPE, file, true);
				IMediaView view = Extensions.getMediaView(Medias.EXCEL_XML_MIME_TYPE, Medias.EXCEL_XML_FILE_EXT, ClientInfo.isMobile());
				Map<MAuthorizationAccount, IUploadService> uploadServicesMap = MAuthorizationAccount.getUserUploadServices();
				if (view != null || uploadServicesMap.size() > 0) {
					WMediaOptions options = new WMediaOptions(media, view != null ? () -> {
						Window viewWindow = new Window();
						viewWindow.setWidth("100%");
						viewWindow.setHeight("100%");
						viewWindow.setTitle(media.getName());
						viewWindow.setAttribute(Window.MODE_KEY, Mode.EMBEDDED);
						AEnv.showWindow(viewWindow);
						view.renderMediaView(viewWindow, media, false);
					} : null, uploadServicesMap);
					options.setPage(getPage());
					options.doHighlighted();
				} else {
					Filedownload.save(file, Medias.EXCEL_XML_MIME_TYPE);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}			
		}
		
	}

	/**
	 * 	Handle Acct Schema selection
	 */
	private void actionAcctSchema()
	{
		if (fAcctSchema.getValue() == null)
			return;
		
		m_data.C_AcctSchema_ID = (Integer) fAcctSchema.getValue();
		m_data.ASchema = MAcctSchema.get(Env.getCtx(), m_data.C_AcctSchema_ID);

		if (log.isLoggable(Level.INFO))
			log.info(m_data.ASchema.toString());
		
		fSortBy1.getComponent().removeAllItems();
		fSortBy2.getComponent().removeAllItems();
		fSortBy3.getComponent().removeAllItems();
		fSortBy4.getComponent().removeAllItems();
		
		sortAddItem("","");
		sortAddItem("DateAcct", Msg.translate(Env.getCtx(), "DateAcct"));
		sortAddItem("C_Period_ID", Msg.translate(Env.getCtx(), "C_Period_ID"));

		Label[] labels = new Label[] {lsel1, lsel2, lsel3, lsel4, lsel5, lsel6, lsel7, lsel8};
		Button[] buttons = new Button[] {sel1 , sel2, sel3, sel4, sel5, sel6, sel7, sel8};

		int selectionIndex = 0;

		MAcctSchemaElement[] elements = m_data.ASchema.getAcctSchemaElements();

		for (int i = 0; i < elements.length && selectionIndex < labels.length; i++)
		{
			MAcctSchemaElement ase = elements[i];
			String columnName = ase.getColumnName();
			String displayColumnName = ase.getDisplayColumnName();

			//  Add Sort Option

			sortAddItem(columnName, Msg.translate(Env.getCtx(), displayColumnName));

			//  Additional Elements

			if (!ase.isElementType(X_C_AcctSchema_Element.ELEMENTTYPE_Organization)
				&& !ase.isElementType(X_C_AcctSchema_Element.ELEMENTTYPE_Account))
			{
				labels[selectionIndex].setValue(Msg.translate(Env.getCtx(), displayColumnName));
				labels[selectionIndex].setVisible(true);
				buttons[selectionIndex].setName(columnName); // actionCommand
				buttons[selectionIndex].addEventListener(Events.ON_CLICK, this);
				if (ThemeManager.isUseFontIconForImage())
					buttons[selectionIndex].setIconSclass("z-icon-Find");
				else
					buttons[selectionIndex].setImage(ThemeManager.getThemeResource("images/Find16.png"));
				buttons[selectionIndex].setLabel("");
				buttons[selectionIndex].setVisible(true);
				selectionIndex++;
			}
		}

		fSortBy1.setValue("DateAcct"); // on force le tri sur la date comptable

		//	don't show remaining

		while (selectionIndex < labels.length)
		{
			labels[selectionIndex].setVisible(false);
			buttons[selectionIndex++].setVisible(false);
		}
	} // actionAcctSchema

//	/**
//	 * 	Add to Sort
//	 *	@param vn name pair
//	 */
//	private void sortAddItem(ValueNamePair vn)
//	{
////		sortBy1.appendItem(vn.getName(), vn);
////		sortBy2.appendItem(vn.getName(), vn);
////		sortBy3.appendItem(vn.getName(), vn);
////		sortBy4.appendItem(vn.getName(), vn);
//		
////		fSortBy1.getComponent().appendItem(vn.getName(), vn.getID());
////		fSortBy2.getComponent().appendItem(vn.getName(), vn.getID());
////		fSortBy3.getComponent().appendItem(vn.getName(), vn.getID());
////		fSortBy4.getComponent().appendItem(vn.getName(), vn.getID());
//	} // sortAddItem
	

	private void sortAddItem(Object value, String name) {
		fSortBy1.getComponent().appendItem(name, value);
		fSortBy2.getComponent().appendItem(name, value);
		fSortBy3.getComponent().appendItem(name, value);
		fSortBy4.getComponent().appendItem(name, value);
	}
	
	
	/**
	 *  Query.
	 *  Delegate to {@link WAcctViewerData#query()}
	 */
	private void actionQuery()
	{
		//  Parameter Info

		StringBuilder para = new StringBuilder();

		//  Reset Selection Data

		m_data.C_AcctSchema_ID = 0;
		m_data.AD_Org_ID = 0;
/*
		//  Save Selection Choices

		Listitem listitem = selAcctSchema.getSelectedItem();

		KeyNamePair kp = null;

		if (listitem != null)
			kp = (KeyNamePair)listitem.getValue();

		if (kp != null)
			m_data.C_AcctSchema_ID = kp.getKey();
*/			
			if (fAcctSchema.getValue() != null)
			m_data.C_AcctSchema_ID = (Integer) fAcctSchema.getValue();
			

		para.append("C_AcctSchema_ID=").append(m_data.C_AcctSchema_ID);
/*
		listitem = selPostingType.getSelectedItem();

		ValueNamePair vp = null;

		if (listitem != null)
			vp = (ValueNamePair)listitem.getValue();
		else
			return;

		m_data.PostingType = vp.getValue();
	*/	
		if (fPostingType.getValue() != null)
			m_data.PostingType = (String) fPostingType.getValue();
		
		para.append(", PostingType=").append(m_data.PostingType);


		if (fLettrage.getValue() == null)
			m_data.Lettrage = null;
		else
			m_data.Lettrage = (String) fLettrage.getValue();

		para.append(", Lettrage=").append(m_data.Lettrage);

		m_data.LettrageDate = selLettrageDate.getValue() != null ? new Timestamp(selLettrageDate.getValue().getTime()) : null;
		para.append(", LettrageDate=").append(m_data.LettrageDate);


		//  Document

		m_data.documentQuery = selDocument.isChecked();
		para.append(", DocumentQuery=").append(m_data.documentQuery);

		if (selDocument.isChecked())
		{
			if (m_data.AD_Table_ID == 0 || m_data.Record_ID == 0)
				return;

			para.append(", AD_Table_ID=").append(m_data.AD_Table_ID)
				.append(", Record_ID=").append(m_data.Record_ID);
		}
		else
		{
			m_data.DateFrom = selDateFrom.getValue() != null
				? new Timestamp(selDateFrom.getValue().getTime()) : null;
			para.append(", DateFrom=").append(m_data.DateFrom);
			m_data.DateTo = selDateTo.getValue() != null
				? new Timestamp(selDateTo.getValue().getTime()) : null;
			para.append(", DateTo=").append(m_data.DateTo);
/*
			listitem = selOrg.getSelectedItem();

			if (listitem != null)
				kp = (KeyNamePair)listitem.getValue();
			else
				kp = null;

			if (kp != null)
				m_data.AD_Org_ID = kp.getKey();
	*/			
				
			if (fOrg.getValue() != null)
				m_data.AD_Org_ID = (Integer) fOrg.getValue();
					
			para.append(", AD_Org_ID=").append(m_data.AD_Org_ID);
			//
			Iterator<String> it = m_data.whereInfo.values().iterator();
			while (it.hasNext())
				para.append(", ").append(it.next());
		}

		//  Save Display Choices

		m_data.displayQty = displayQty.isChecked();
		para.append(" - Display Qty=").append(m_data.displayQty);
		m_data.displaySourceAmt = displaySourceAmt.isChecked();
		para.append(", Source=").append(m_data.displaySourceAmt);
		m_data.displayDocumentInfo = displayDocumentInfo.isChecked();
		para.append(", Doc=").append(m_data.displayDocumentInfo);

		m_data.displayLines0 = displayLines0.isChecked();
		para.append(", Lines0=").append(m_data.displayLines0);
		m_data.groupByAccount = groupByAccount.isChecked();
		para.append(", groupByAccount=").append(m_data.groupByAccount);

		if (fSortBy1.getValue() != null) {
			m_data.sortBy1 = (String) fSortBy1.getValue();
			m_data.group1 = group1.isChecked();
			para.append(" - Sorting: ").append(m_data.sortBy1).append("/").append(m_data.group1);
		}

		if (fSortBy2.getValue() != null) {
			m_data.sortBy2 = (String) fSortBy2.getValue();
			m_data.group2 = group2.isChecked();
			para.append(", ").append(m_data.sortBy2).append("/").append(m_data.group2);
		}
		
		if (fSortBy3.getValue() != null) {
			m_data.sortBy3 = (String) fSortBy3.getValue();
			m_data.group3 = group3.isChecked();
			para.append(", ").append(m_data.sortBy3).append("/").append(m_data.group3);
		}
		
		if (fSortBy4.getValue() != null) {
			m_data.sortBy4 = (String) fSortBy4.getValue();
			m_data.group4 = group4.isChecked();
			para.append(", ").append(m_data.sortBy3).append("/").append(m_data.group4);
		}


		bQuery.setEnabled(false);
		statusLine.setValue(" " + Msg.getMsg(Env.getCtx(), "Processing"));

		if (log.isLoggable(Level.CONFIG)) log.config(para.toString());

		//  Switch to Result pane

		tabbedPane.setSelectedIndex(1);
		stateChanged();

		//  Set TableModel with Query

		m_rmodel = m_data.query();
		m_queryData = m_rmodel.getRows();
		List<ArrayList<Object>> list = null;
		paging.setPageSize(PAGE_SIZE);
		if (m_queryData.size() > PAGE_SIZE)
		{
			list = m_queryData.subList(0, PAGE_SIZE);
			paging.setTotalSize(m_queryData.size());
			pagingPanel.setVisible(true);
		}
		else
		{
			list = m_queryData;
			paging.setTotalSize(m_queryData.size());
			pagingPanel.setVisible(false);
		}
		paging.setActivePage(0);

		ListModelTable listmodeltable = new ListModelTable(list);

		if (table.getListhead() == null)
		{
			Listhead listhead = new Listhead();
			listhead.setSizable(true);

			for (int i = 0; i < m_rmodel.getColumnCount(); i++)
			{
				Listheader listheader = new Listheader(m_rmodel.getColumnName(i));
				listheader.setTooltiptext(m_rmodel.getColumnName(i));
				if (!m_data.displayDocumentInfo) {
					if ("AD_Table_ID".equals(m_rmodel.getRColumn(i).getColumnName())) 
					{
						listheader.setVisible(false);
					}
					else if ("Record_ID".equals(m_rmodel.getRColumn(i).getColumnName()))
					{
						listheader.setVisible(false);
					}
					else if ("Fact_Acct_ID".equals(m_rmodel.getRColumn(i).getColumnName()))
					{
						listheader.setVisible(false);
					}
				}
				listhead.appendChild(listheader);
			}

			table.appendChild(listhead);
		}
		else
		{
			Listhead listhead = table.getListhead();

			// remove existing column header
			listhead.getChildren().clear();

			// add in new column header
			for (int i = 0; i < m_rmodel.getColumnCount(); i++)
			{
				Listheader listheader = new Listheader(m_rmodel.getColumnName(i));
				if (!m_data.displayDocumentInfo) {
					if ("AD_Table_ID".equals(m_rmodel.getRColumn(i).getColumnName())) 
					{
						listheader.setVisible(false);
					}
					else if ("Record_ID".equals(m_rmodel.getRColumn(i).getColumnName()))
					{
						listheader.setVisible(false);
					}
				}
				listhead.appendChild(listheader);
			}
		}
		//

		table.getItems().clear();

		table.setItemRenderer(new WListItemRenderer());
		table.setModel(listmodeltable);
		table.setSizedByContent(true);

		resultPanel.invalidate();

		bQuery.setEnabled(true);
		statusLine.setValue(" " + Msg.getMsg(Env.getCtx(), "ViewerOptions"));

		tabResult.setLabel(getTabResultLabel(m_data.AD_Table_ID, m_data.Record_ID));
	}   //  actionQuery

	/**
	 *  Document selection
	 */
	private void actionDocument()
	{
		boolean doc = selDocument.isChecked();
		fTable.getComponent().setEnabled(doc);
		selRecord.setEnabled(doc);
		//
		selDateFrom.setReadWrite(!doc);
		selDateTo.setReadWrite(!doc);
		fOrg.getComponent().setEnabled(!doc);
		selAcct.setEnabled(!doc);
		sel1.setEnabled(!doc);
		sel2.setEnabled(!doc);
		sel3.setEnabled(!doc);
		sel4.setEnabled(!doc);
		sel5.setEnabled(!doc);
		sel6.setEnabled(!doc);
		sel7.setEnabled(!doc);
		sel8.setEnabled(!doc);
	} // actionDocument

	/**
	 *  Handle Table selection (reset Record selection)
	 */
	private void actionTable()
	{
		if (fTable.getValue() != null) {
			int tableID = (Integer) fTable.getValue();
			m_data.AD_Table_ID = tableID;

			//  Reset Record
			m_data.Record_ID = 0;
			selRecord.setLabel("");
			selRecord.setName(MTable.getTableName(Env.getCtx(), tableID) + "_ID");
		}
	} // actionTable

	/**
	 *  Handle Info Button action.<br/>
	 *  Show info window.
	 *
	 *  @param button pressed button
	 *  @throws Exception
	 */
	private void actionButton(final Button button) throws Exception
	{
		final String keyColumn = button.getName();
		String whereClause = "(IsSummary='N' OR IsSummary IS NULL)";
		String lookupColumn = keyColumn;

		if ("Account_ID".equals(keyColumn))
		{
			lookupColumn = "C_ElementValue_ID";
			MAcctSchemaElement ase = m_data.ASchema
				.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_Account);

			if (ase != null)
				whereClause += " AND C_Element_ID=" + ase.getC_Element_ID();
		}
		else if ("User1_ID".equals(keyColumn))
		{
			lookupColumn = "C_ElementValue_ID";
			MAcctSchemaElement ase = m_data.ASchema
				.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserElementList1);

			if (ase != null)
				whereClause += " AND C_Element_ID=" + ase.getC_Element_ID();
		}
		else if ("User2_ID".equals(keyColumn))
		{
			lookupColumn = "C_ElementValue_ID";
			MAcctSchemaElement ase = m_data.ASchema
				.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserElementList2);

			if (ase != null)
				whereClause += " AND C_Element_ID=" + ase.getC_Element_ID();
		}
		else if (keyColumn.equals("AD_OrgTrx_ID"))
		{
			lookupColumn = "AD_Org_ID";
		}
		else if (keyColumn.equals("UserElement1_ID")) // KTU
		{	
			MAcctSchemaElement ase = m_data.ASchema.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserColumn1);
			lookupColumn = MColumn.getColumnName(Env.getCtx(), ase.getAD_Column_ID());
			whereClause = "";
		}
		else if (keyColumn.equals("UserElement2_ID")) // KTU
		{
			MAcctSchemaElement ase = m_data.ASchema.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserColumn2);
			lookupColumn = MColumn.getColumnName(Env.getCtx(), ase.getAD_Column_ID());
			whereClause = "";
		}
		
		else if (selDocument.isChecked())
			whereClause = "";

		final String tableName = lookupColumn.substring(0, lookupColumn.length()-3);

		// Show info window
		final InfoPanel info = InfoPanel.create(m_data.WindowNo, tableName, lookupColumn, "", false, whereClause);

		if (!info.loadedOK())
		{
			button.setLabel("");
			m_data.whereInfo.put(keyColumn, "");
			return;
		}

		info.setVisible(true);
		final String lookupColumnRef = lookupColumn;
		info.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				String selectSQL = info.getSelectedSQL();       //  C_Project_ID=100 or ""
				Integer key = (Integer)info.getSelectedKey();

				if (selectSQL == null || selectSQL.length() == 0 || key == null)
				{
					button.setLabel("");
					m_data.whereInfo.put(keyColumn, "");    //  no query
					return;
				}

				//  Save for query

				if (log.isLoggable(Level.CONFIG)) log.config(keyColumn + " - " + key);
				if (button == selRecord)                            //  Record_ID
					m_data.Record_ID = key.intValue();
				else
					m_data.whereInfo.put(keyColumn, keyColumn + "=" + key.intValue());

				//  Display Selection and resize
				button.setLabel(m_data.getButtonText(tableName, lookupColumnRef, selectSQL));
				//pack();
				
			}
		});
		AEnv.showWindow(info);		
	} // actionButton

	/**
	 *  RePost Record
	 */
	private void actionRePost()
	{
		if (m_data.documentQuery) {
			String matchcode = LfrFactReconciliationUtil.getMatchCodeOfFactAcct(m_data.C_AcctSchema_ID, m_data.AD_Table_ID, m_data.Record_ID, 0, 0, null);
			
			if (!Util.isEmpty(matchcode)) {
				Dialog.warn(m_data.WindowNo, "", Msg.getMsg(Env.getCtx(), "LFR_AcctViewerPostingReconciled", new Object[] {matchcode}), tabResult.getLabel());
				return;
			}
		}

		if (m_data.documentQuery
			&& m_data.AD_Table_ID != 0 && m_data.Record_ID != 0)
		{
			// IDEMPIERE-2392
			if (! MPeriod.isOpen(Env.getCtx(), m_data.AD_Table_ID, m_data.Record_ID, null, true)) {
				Dialog.error(0, "Error", Msg.getMsg(Env.getCtx(), "PeriodClosed"));
				return;
			}

			Dialog.ask(m_data.WindowNo, "PostImmediate?", new Callback<Boolean>() {
				
				@Override
				public void onCallback(Boolean result) 
				{
					if (result)
					{
						//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						boolean force = forcePost.isChecked();
						String error = AEnv.postImmediate (m_data.WindowNo, m_data.AD_Client_ID,
							m_data.AD_Table_ID, m_data.Record_ID, force);
						//setCursor(Cursor.getDefaultCursor());
						if (error != null)
							Dialog.error(0, "PostingError-N", error);

						actionQuery();
					}
				}
			});			
		}
	} // actionRePost

	/**
	 * Zoom to table id + record id
	 */
	private void actionZoom()
	{
		int selected = table.getSelectedIndex();
		if(selected == -1) return;

		int tableIdColumn = m_rmodel.getColumnIndex("AD_Table_ID");
		int recordIdColumn = m_rmodel.getColumnIndex("Record_ID");
		ListModelTable model = (ListModelTable) table.getListModel();
		KeyNamePair tabknp = (KeyNamePair) model.getDataAt(selected, tableIdColumn);
		Integer recint = (Integer) model.getDataAt(selected, recordIdColumn);
		if (tabknp != null && recint != null) {
			int AD_Table_ID = tabknp.getKey();
			int Record_ID = recint.intValue();

			AEnv.zoom(AD_Table_ID, Record_ID);
		}
	}

	/**
	 * Zoom to fact acct window (double click action)
	 */
	private void actionZoomFactAcct() {
		int selected = table.getSelectedIndex();
		if(selected == -1) return;

		int factAcctIdColumn = m_rmodel.getColumnIndex("Fact_Acct_ID");
		ListModelTable model = (ListModelTable) table.getListModel();
		Integer faint = (Integer) model.getDataAt(selected, factAcctIdColumn);
		if (faint != null) {
			int fact_acct_ID = faint.intValue();

			AEnv.zoom(MFactAcct.Table_ID, fact_acct_ID);
		}
	}

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (newpage != null)
			SessionManager.getAppDesktop().updateHelpContext(X_AD_CtxHelp.CTXTYPE_Home, 0);
	}
	
	/**
	 * Handle shortcut key event
	 * @param keyEvent
	 */
	private void onCtrlKeyEvent(KeyEvent keyEvent) {
		if ((keyEvent.isAltKey() && keyEvent.getKeyCode() == 0x58)	// Alt-X
				|| (keyEvent.getKeyCode() == 0x1B && isUseEscForTabClosing)) { 	// ESC
			keyEvent.stopPropagation();
			Events.echoEvent(new Event(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT, this));
		}
	}

	@Override
	protected void initForm() {
		setTitle(Msg.getMsg(Env.getCtx(), TITLE));
		int AD_Table_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, Env.PREFIX_PREDEFINED_VARIABLE + INITIAL_AD_TABLE_ID, true);
		int Record_ID = Env.getContextAsInt(Env.getCtx(), m_WindowNo, Env.PREFIX_PREDEFINED_VARIABLE + INITIAL_RECORD_ID, true);
		dynInit(AD_Table_ID, Record_ID);
	}
	
	private String getTabResultLabel(int tableID, int recordID) {
		if (fTable.getComponent().isDisabled())
			return Msg.getMsg(Env.getCtx(), "ViewerResult").replaceAll("[&]", "");

		String tableName = MTable.getTableName(Env.getCtx(), tableID);
		return Msg.getElement(Env.getCtx(),  tableName + "_ID") + " : " + DB.getSQLValueStringEx(null, "SELECT DocumentNo FROM " + tableName + " WHERE " + tableName + "_ID = ?", recordID);
	}

	protected boolean isShowDisplaySourceAmt() {
		return MSysConfig.getBooleanValue(LFR_ACCT_VIEWER_SHOW_DISPLAY_SOURCE, true, Env.getAD_Client_ID(Env.getCtx()));
	}

	protected boolean isShowDisplayQty() {
		return MSysConfig.getBooleanValue(LFR_ACCT_VIEWER_SHOW_DISPLAY_QTY, true, Env.getAD_Client_ID(Env.getCtx()));
	}
}
