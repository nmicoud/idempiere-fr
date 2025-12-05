package fr.loc.idempiere.webui.apps.form;

import static org.compiere.model.SystemIDs.COLUMN_C_INVOICE_C_BPARTNER_ID;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.DateRangeButton;
import org.adempiere.webui.window.Dialog;
import org.compiere.minigrid.ColumnInfo;
import org.compiere.minigrid.IDColumn;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPayment;
import org.compiere.model.MRole;
import org.compiere.model.SystemIDs;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Vlayout;

import fr.idempiere.model.MLFRFactReconciliationCode;
import fr.idempiere.util.LfrFactReconciliationUtil;


/**
 *  Formulaire de lettrage manuel
 *  @author Nico
 */
public class WLFRFactReconcile implements IFormController, EventListener<Event>, ValueChangeListener, WTableModelListener
{
	private CustomForm form = new CustomForm();
	public static CLogger s_log = CLogger.getCLogger(WLFRFactReconcile.class);
	private Panel mainPanel = new Panel();
	private Borderlayout mainLayout = new Borderlayout();
	private WListbox miniTable = ListboxFactory.newDataTable();
	private Button bGenerate, bRefresh, bZoomToFact, bZoomToDoc, bReset;
	private Panel southPanel;
	private WTableDirEditor fAcctSchema, fAccount, fOrg, fType, fReconciled;
	private WSearchEditor fBPartner;
	private Label labelDateAcctFrom = new Label();
	private WDateEditor fieldDateAcctFrom = new WDateEditor();
	private Label labelDateAcctTo = new Label();
	private WDateEditor fieldDateAcctTo = new WDateEditor();
	private WStringEditor fDescription, fMatchCode;
	private StatusBarPanel statusBar = new StatusBarPanel();
	private Label dataStatus = new Label();

	public WLFRFactReconcile()
	{
		try {
			m_WindowNo = form.getWindowNo();

			dynInit();
			zkInit();
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
			fBPartner.setVisible(false);
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
		bReset = new Button();
		bReset.setImage(ThemeManager.getThemeResource("images/Reset24.png"));
		bReset.addEventListener(Events.ON_CLICK, this);
		bGenerate = new Button();
		bGenerate.setImage(ThemeManager.getThemeResource("images/Process24.png"));
		bGenerate.addEventListener(Events.ON_CLICK, this);

		labelDateAcctFrom.setText(Msg.translate(Env.getCtx(), "DateAcct"));
		labelDateAcctTo.setText("-");

		North north = new North();
		north.setStyle("border: none");
		north.setAutoscroll(true);
		mainLayout.appendChild(north);

		Vlayout vl = new Vlayout();
		north.appendChild(vl);

		Hlayout hl = getHlayout();
		hl.appendChild(fAcctSchema.getLabel().rightAlign());
		fAcctSchema.getLabel().setHflex("1");
		hl.appendChild(fAcctSchema.getComponent());
		ZKUpdateUtil.setHflex(fAcctSchema.getComponent(), "1");

		hl.appendChild(fType.getLabel().rightAlign());
		fType.getLabel().setHflex("1");
		hl.appendChild(fType.getComponent());
		ZKUpdateUtil.setHflex(fType.getComponent(), "1");

		hl.appendChild(fBPartner.getLabel().rightAlign());
		fBPartner.getLabel().setHflex("1");
		hl.appendChild(fBPartner.getComponent());
		ZKUpdateUtil.setHflex(fBPartner.getComponent(), "1");

		vl.appendChild(hl);
		hl = getHlayout();

		hl.appendChild(fAccount.getLabel().rightAlign());
		hl.appendChild(fAccount.getComponent());
		ZKUpdateUtil.setHflex(fAccount.getComponent(), "1");

		hl.appendChild(fDescription.getLabel().rightAlign());
		fDescription.getLabel().setHflex("1");
		hl.appendChild(fDescription.getComponent());
		ZKUpdateUtil.setHflex(fDescription.getComponent(), "1");

		vl.appendChild(hl);
		hl = getHlayout();

		hl.appendChild(fOrg.getLabel().rightAlign());
		fOrg.getLabel().setHflex("1");
		hl.appendChild(fOrg.getComponent());
		ZKUpdateUtil.setHflex(fOrg.getComponent(), "1");

		hl.appendChild(labelDateAcctFrom.rightAlign());
		labelDateAcctFrom.setHflex("1");

		Hbox hbox = new Hbox();
		hbox.appendChild(fieldDateAcctFrom.getComponent());
		hbox.appendChild(labelDateAcctTo.rightAlign());
		hbox.appendChild(fieldDateAcctTo.getComponent());
		DateRangeButton drb = (new DateRangeButton(fieldDateAcctFrom, fieldDateAcctTo));
		hbox.appendChild(drb);
		hl.appendChild(hbox);
		ZKUpdateUtil.setHflex(hbox, "1");

		hl.appendChild(fReconciled.getLabel().rightAlign());
		ZKUpdateUtil.setHflex(fReconciled.getLabel(), "1");
		hl.appendChild(fReconciled.getComponent());
		ZKUpdateUtil.setWidth(fReconciled.getComponent(), "100px");

		hl.appendChild(fMatchCode.getLabel().rightAlign());
		fMatchCode.getLabel().setHflex("1");
		hl.appendChild(fMatchCode.getComponent());
		ZKUpdateUtil.setHflex(fMatchCode.getComponent(), "1");

		hl.appendChild(createVerticalSeparator(50));
		hl.appendChild(bRefresh);
		vl.appendChild(hl);

		South south = new South();
		south.setStyle("border: none");
		mainLayout.appendChild(south);
		southPanel = new Panel();
		southPanel.appendChild(bZoomToFact);
		southPanel.appendChild(bZoomToDoc);	
		southPanel.appendChild(createVerticalSeparator(30));
		southPanel.appendChild(createVerticalSeparator(30));
		southPanel.appendChild(bGenerate);
		southPanel.appendChild(createVerticalSeparator(30));
		southPanel.appendChild(bReset);
		southPanel.appendChild(createVerticalSeparator(30));
		southPanel.appendChild(dataStatus);
		south.appendChild(southPanel);

		Center center = new Center();
		mainLayout.appendChild(center);
		center.appendChild(miniTable);
		bGenerate.setEnabled(false);
		bReset.setEnabled(false);

		fAcctSchema.showMenu();
		fType.showMenu();
		fBPartner.showMenu();
		fAccount.showMenu();
		fDescription.showMenu();
		fOrg.showMenu();
		fMatchCode.showMenu();
		fReconciled.showMenu();
	}

	protected void dynInit() throws Exception
	{
		m_AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());

		MLookup lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, 2463, DisplayType.Table, Env.getLanguage(Env.getCtx()), "C_AcctSchema_ID", 0, false, "");
		fAcctSchema = new WTableDirEditor("C_AcctSchema_ID", true, false, true, lookup);
		fAcctSchema.getComponent().setSelectedIndex(0);
		fAcctSchema.getLabel().setValue(Msg.getElement(Env.getCtx(), "C_AcctSchema_ID"));

		lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, 2463, DisplayType.Table, Env.getLanguage(Env.getCtx()), "Account_ID", 0, false, "1=2");
		fAccount = new WTableDirEditor("C_AcctSchema_ID", true, false, true, lookup);
		fAccount.getLabel().setValue(Msg.getElement(Env.getCtx(), "Account_ID"));

		int refID = MColumn.get(Env.getCtx(), MLFRFactReconciliationCode.Table_Name, MLFRFactReconciliationCode.COLUMNNAME_LFR_FactReconciliationType).getAD_Reference_Value_ID();
		lookup = MLookupFactory.get(Env.getCtx(), 0, 0, DisplayType.List, Env.getLanguage(Env.getCtx()), "LFR_FactReconciliationType", refID, false, "");
		fType = new WTableDirEditor("LFR_FactReconciliationType", true, false, true, lookup);
		fType.getComponent().addEventListener(Events.ON_SELECT, this);
		fType.getLabel().setValue(Msg.getElement(Env.getCtx(), "LFR_FactReconciliationType"));

		lookup =  MLookupFactory.get (Env.getCtx(), m_WindowNo, 59767, DisplayType.Table, Env.getLanguage(Env.getCtx()), "AD_Org_ID", 0, false, "");
		fOrg = new WTableDirEditor("AD_Org_ID", true, false, true, lookup);
		fOrg.getLabel().setValue(Msg.getElement(Env.getCtx(), "AD_Org_ID"));

		lookup = MLookupFactory.get (Env.getCtx(), m_WindowNo, 0, COLUMN_C_INVOICE_C_BPARTNER_ID, DisplayType.Search);
		fBPartner = new WSearchEditor ("C_BPartner_ID", false, false, true, lookup);
		fBPartner.addValueChangeListener(this);
		fBPartner.getLabel().setText(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
		prepareTable(miniTable, "");

		lookup = MLookupFactory.get(Env.getCtx(), 0, 0, DisplayType.List, Env.getLanguage(Env.getCtx()), "Reconciled", SystemIDs.REFERENCE_YESNO, false, "");
		fReconciled = new WTableDirEditor("Reconciled", false, false, true, lookup);
		fReconciled.getComponent().addEventListener(Events.ON_SELECT, this);
		fReconciled.getLabel().setValue("Ecritures lettrées");
		fReconciled.setValue("N");

		fDescription = new WStringEditor();
		fDescription.getLabel().setValue(Msg.getElement(Env.getCtx(), "Description"));
		fMatchCode = new WStringEditor();
		fMatchCode.getLabel().setValue(Msg.getElement(Env.getCtx(), "LFR_MatchCode"));
		
		miniTable.getModel().addTableModelListener(this);

		statusBar.setStatusLine("");
		statusBar.setStatusDB("");
	}   //  dynInit

	public void loadTableLines(String lettrageType)
	{
		if (fAcctSchema.isNullOrEmpty())
			throw new WrongValueException(fAcctSchema.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory"));
		if (fType.isNullOrEmpty())
			throw new WrongValueException(fType.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory"));
		if (fAccount.isNullOrEmpty())
			throw new WrongValueException(fAccount.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory"));

		Timestamp dateAcctFrom = (Timestamp) fieldDateAcctFrom.getValue();
		Timestamp dateAcctTo = (Timestamp) fieldDateAcctTo.getValue();
		int acctSchemaID = (Integer) fAcctSchema.getValue();
		int orgID = fOrg.isNullOrEmpty() ? -1 : (Integer) fOrg.getValue();
		int accountID = (Integer) fAccount.getValue();
		String description = (String) fDescription.getValue();
		String matchCode = (String) fMatchCode.getValue();
		String Lettree = fReconciled.isNullOrEmpty() ? "" : (String) fReconciled.getValue();

		int bpartnerID = 0;
		if (!fBPartner.isNullOrEmpty() && lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			bpartnerID = (Integer) fBPartner.getValue();

		loadTableInfo(lettrageType, bpartnerID, dateAcctFrom, dateAcctTo, orgID, acctSchemaID, accountID, Lettree, description, matchCode, miniTable);
		calculate();
	}

	public void dispose()
	{
		SessionManager.getAppDesktop().closeActiveWindow();
	}	//	dispose

	public void onEvent (Event e)
	{
		dataStatus.setValue(""); // on vide le champs

		String lettrageType = fType.isNullOrEmpty() ? "" : (String) fType.getValue();

		if (e.getTarget() == bZoomToFact)
			zoomToFact();
		else if (e.getTarget() == bZoomToDoc)
			zoomToDocument();
		else if (e.getTarget() == bGenerate || e.getTarget() == bReset) {

			if (e.getTarget() == bGenerate)
				generateLettrage(false, lettrageType);
			if (e.getTarget() == bReset)
				resetLettrage(false, lettrageType);
		}
		else if (e.getTarget() == bRefresh) {	
			if ((fAccount.getComponent().getItemCount() != 0)
					&& (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account)
							|| ((lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner) && fBPartner.getValue() != null))))
				loadTableLines(lettrageType);
			else
				statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "XXA_NotEnoughCriteriaForSearching"), true);
		}
		else if (e.getTarget() == fType.getComponent())
		{
			boolean onlyAux = false;
			if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
				onlyAux = true;

			fAccount.getComponent().removeAllItems();

			for (KeyNamePair acct : getAccountData(onlyAux))
				fAccount.getComponent().appendItem(acct.getName(), acct.getKey());

			bpartnerActivate(lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner));
			loadTableLines(lettrageType);
		}
		
	}   //  onEvent

	public void calculate()
	{
		statusBar.setStatusLine(calculateSelection(miniTable));
		statusBar.setStatusDB(m_noSelected + "/" + m_noRows);
		buttonsActivateAndSetText();
	}

	/**
	 *  Table Model Listener
	 *  @param e event
	 */
	public void tableChanged(WTableModelEvent e)
	{
		if (e.getColumn() == 0)
			calculateSelection();
	}   //  valueChanged

	/**
	 *  Calculate selected rows.
	 *  - add up selected rows
	 */
	public void calculateSelection()
	{
		statusBar.setStatusLine(calculateSelection(miniTable) + " - " + Msg.getMsg(Env.getCtx(), "Difference") + " = " + m_format.format(m_DifferenceAmt));
		statusBar.setStatusDB(m_noSelected + "/" + m_noRows);
		buttonsActivateAndSetText();
	}   //  calculateSelection

	public ADForm getForm() {
		return form;
	}

	private void buttonsActivateAndSetText()
	{
		//		if (fieldAction.getSelectedItem() == null)
		//			return;

		//		String action = (String) fieldAction.getSelectedItem().getValue();
		//
		//		if (action.equals(actionLettrage))
		//		{
		bGenerate.setEnabled(m_noSelected>0 && m_DifferenceAmt.signum()==0);
		bGenerate.setLabel(Msg.getMsg(Env.getCtx(), "XXA_Lettrer"));
		bReset.setLabel(Msg.getMsg(Env.getCtx(), "XXA_Delettrer"));
		//		}
		//		else if (action.equals(actionPointage))
		//		{
		//			bGenerate.setEnabled(m_noSelected>0);
		//			bGenerate.setLabel(Msg.getMsg(Env.getCtx(), "XXA_Pointer"));
		//			bReset.setLabel(Msg.getMsg(Env.getCtx(), "XXA_Depointer"));
		//		}
		bReset.setEnabled(m_noSelected>0);	// dans tous les cas
	}

	private void bpartnerActivate(boolean activate)
	{
		fBPartner.setVisible(activate);
		fBPartner.setValue(null);
	}

	private void zoomToFact()
	{
		int selected = miniTable.getSelectedRow();

		if (selected == -1)
			return;

		int factId = ((IDColumn) miniTable.getModel().getValueAt(selected, i_ID)).getRecord_ID();

		AEnv.zoom(MFactAcct.Table_ID, factId);
	}   //  zoomToFact

	private void zoomToDocument()
	{
		int selected = miniTable.getSelectedRow();

		if (selected == -1)
			return;

		int factId = ((IDColumn) miniTable.getModel().getValueAt(selected, i_ID)).getRecord_ID();
		MFactAcct fa = new MFactAcct(Env.getCtx(), factId, null);
		AEnv.zoom(fa.getAD_Table_ID(), fa.getRecord_ID());

	}   //  zoomToDocument

	private void generateLettrage(boolean isTemp, String LettrageType) 
	{
		if (miniTable.getRowCount() == 0)
			return;
		calculate();
		if (m_noSelected == 0)
			return;

		int nbLines = m_noSelected;
		int acctSchemaID = (Integer) fAcctSchema.getValue();
		int bpartnerID = LettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner) ? (Integer) fBPartner.getValue() : 0;
		int accountID = (Integer) fAccount.getValue();

		String msg = generateLettrage(miniTable, isTemp, LettrageType, acctSchemaID, bpartnerID, accountID);

		if(msg != null && msg.length() > 0)	{
			Dialog.error(0, "SaveError", msg);
			return;
		}

		loadTableLines(LettrageType);
		dataStatus.setValue(nbLines + " " + (Msg.getMsg(Env.getCtx(), (isTemp ? "XXA_Lettrageecriturespointees" : "XXA_LettrageecrituresLettrees"))));
	}   //  generateLettrage


	private void resetLettrage(boolean isTemp, String LettrageType)
	{
		if (miniTable.getRowCount() == 0)
			return;
		calculate();
		if (m_noSelected == 0)
			return;

		int acctSchemaID = (Integer) fAcctSchema.getValue();

		int Record_ID = 0;
		if (LettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			Record_ID = (Integer) fBPartner.getValue();
		else if (LettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account))
		{
			if (fAccount.isNullOrEmpty())
				return;
			Record_ID = (Integer) fAccount.getValue();
		}

		int nbReset = resetLettrage(miniTable, LettrageType, isTemp, acctSchemaID, Record_ID);

		loadTableLines(LettrageType);
		dataStatus.setValue(nbReset + " " + (Msg.getMsg(Env.getCtx(), "XXA_LettrageEcrituresDeLettrees")));
	}   //  resetLettrage

	/** Renvoi un Separator avec un spacing exprimé en px */
	private Separator createVerticalSeparator(int spacing)
	{
		Separator sep = new Separator("vertical");
		sep.setSpacing(spacing + "px");
		return sep;
	}

	/**	Window No			*/
	public int         	m_WindowNo = 0;
	/** Format                  */
	public DecimalFormat   m_format = DisplayType.getNumberFormat(DisplayType.Amount);
	/** SQL for Query           */
	private String          m_sql;
	/** Number of selected rows */
	public int             m_noSelected = 0;
	/** Client ID               */
	private int             m_AD_Client_ID = 0;
	/**/
	public boolean         m_isLocked = false;
	/** Payment Selection		*/
	public MPaySelection	m_ps = null;
	/**	Logger			*/

	int	m_C_BPartner_ID = 0;
	int m_Account_ID = 0;
	int m_AcctSchema_ID = 0;
	Timestamp m_DateAcctFrom = null;
	Timestamp m_DateAcctTo = null;
	int	m_AD_Org_ID = 0;
	public int m_noRows = 0;
	protected BigDecimal m_DifferenceAmt = Env.ZERO;
	protected int         m_noNdFLine = 0;
	protected int i_ID= 0;
	int i_AmtAcctDr = 6;	//	n° colonne débit
	int i_AmtAcctCr = 7;	//	n° colonne crédit
	int i_Code= 10;	// n° colonne code lettrage


	private static final ColumnInfo[] layout = {
			new ColumnInfo(" ", "fa.Fact_Acct_ID", IDColumn.class, false, false, null),
			new ColumnInfo(Msg.translate(Env.getCtx(), "AD_Org_ID"), "o.Name", KeyNamePair.class, true, false, "o.AD_Org_ID"),	//	1
			new ColumnInfo(Msg.translate(Env.getCtx(), "DateAcct"), "fa.DateAcct", Timestamp.class),	//	2
			new ColumnInfo(Msg.translate(Env.getCtx(), "GL_Category_ID"), "glc.Name", KeyNamePair.class, true, false, "glc.GL_Category_ID"),	//	3
			new ColumnInfo(Msg.translate(Env.getCtx(), "DocumentNo"), "(CASE WHEN fa.AD_Table_ID = 318 THEN i.DocumentNo  WHEN fa.AD_Table_ID = 335 THEN p.DocumentNo END)", String.class),	//	4  TODO à supprimer (fait partie de la description de la ligne)
			new ColumnInfo(Msg.translate(Env.getCtx(), "Description"), "fa.Description", String.class),	//	5
			new ColumnInfo(Msg.translate(Env.getCtx(), "AmtAcctDr"), "fa.AmtAcctDr", BigDecimal.class),	//	6
			new ColumnInfo(Msg.translate(Env.getCtx(), "AmtAcctCr"), "fa.AmtAcctCr", BigDecimal.class),	//	7
			new ColumnInfo(Msg.translate(Env.getCtx(), "IsAllocated"), "(CASE WHEN fa.AD_Table_ID = 318 THEN i.IsPaid WHEN fa.AD_Table_ID = 335 THEN p.IsAllocated ELSE 'Y' END)", Boolean.class),	// 8 TODO n'afficher que si lettrage tiers ?
			new ColumnInfo(Msg.translate(Env.getCtx(), "LFR_ReconciliationDate"), "l.LFR_ReconciliationDate", Timestamp.class),	//	9
			new ColumnInfo(Msg.translate(Env.getCtx(), "MatchCode"), "l.MatchCode", String.class),			//	10
	};

	public void prepareTable(IMiniTable miniTable, String LettrageType)
	{
		m_sql = miniTable.prepareTable(
				layout,
				//	FROM
				"Fact_Acct fa"
				+ " LEFT OUTER JOIN Fact_Reconciliation l ON (fa.Fact_Acct_ID = l.Fact_Acct_ID AND l.LFR_FactReconciliationType=?)"
				+ " LEFT OUTER JOIN C_BPartner bp ON (fa.C_BPartner_ID = bp.C_BPartner_ID)"
				+ " LEFT OUTER JOIN AD_Org o ON (o.AD_Org_ID = fa.AD_Org_ID)"
				+ " LEFT OUTER JOIN GL_Category glc ON (fa.GL_Category_ID = glc.GL_Category_ID)"
				+ " LEFT OUTER JOIN C_Invoice i ON (fa.Record_ID=i.C_Invoice_ID AND fa.AD_Table_ID = 318)"
				+ " LEFT OUTER JOIN C_Payment p ON (fa.Record_ID=p.C_Payment_ID AND fa.AD_Table_ID = 335)"
				+ " LEFT OUTER JOIN C_AllocationHdr a ON (fa.Record_ID=a.C_AllocationHdr_ID AND fa.AD_Table_ID = 735)"
				,
				// WHERE avec critères obligatoires 
				" fa.Account_ID = ?"
				+ " AND fa.C_AcctSchema_ID = ?"
				+ " AND fa.PostingType = 'A'",
				true, "fa");
	}   //  dynInit

	public void loadTableInfo(String lettrageType, int bpartner_id, Timestamp dateAcctFrom, Timestamp dateAcctTo, int orgID, int acctSchemaID, 
			int accountID, String lettree, String description, String codeLettrage, IMiniTable miniTable)
	{
		if (m_sql == null)
			return;

		String sql = m_sql;
		//  Parameters

		if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account)) {
			// le lettrage d'écritures sur un compte centralisateur avec un tiers >0 ne peut se faire qu'en mode lettrage de tiers

			String sqlEV = "SELECT 1 FROM C_ElementValue WHERE BPartnerType IS NOT NULL AND C_ElementValue_ID = " + accountID;
			int noEV = DB.getSQLValue(null, sqlEV);

			if (noEV > 0)
				sql += " AND fa.C_BPartner_ID IS NULL";
		}

		else if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			sql += " AND fa.C_BPartner_ID = ?";

		if (lettree.length() > 0 && lettree != null) {
			sql += " AND ((SELECT SUM(f.AmtAcctDR-f.AmtAcctCR) FROM Fact_Reconciliation let " +
					" INNER JOIN Fact_Acct f ON (f.Fact_Acct_ID = let.Fact_Acct_ID) " +
					" WHERE l.MatchCode = let.MatchCode "
					+ " AND let.LFR_FactReconciliationType ='" + lettrageType + "'";
			if  (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
				sql += " AND f.C_BPartner_ID="+bpartner_id + " AND f.Account_ID="+accountID;
			if  (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account))
				sql += " AND f.Account_ID="+accountID;
			sql += ")";

			if (lettree.equals("Y"))
				sql += "= 0) ";
			else
				sql += "<> 0 OR l.MatchCode IS NULL) ";
		}

		// Critères optionnels
		// Organisation
		if (orgID > 0)
			sql += " AND fa.AD_Org_ID = ?";

		if (dateAcctFrom != null )	// DateFrom
			sql +=" AND TRUNC (fa.DateAcct) >= ?";

		if (dateAcctTo != null )	// DateTo
			sql +=" AND TRUNC (fa.DateAcct) <= ?";

		if (description.length() > 0)
			sql += " AND UPPER(fa.Description) LIKE ?";

		if (codeLettrage.length() > 0)
			sql += " AND UPPER(l.MatchCode) LIKE ?";

		sql += " ORDER BY fa.DateAcct";
		System.out.println(sql);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			int index = 1;
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setString(index++, lettrageType);
			pstmt.setInt(index++, accountID);
			pstmt.setInt(index++, acctSchemaID);
			if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
				pstmt.setInt(index++, bpartner_id);

			if (orgID > 0)                    
				pstmt.setInt(index++, orgID);
			if (dateAcctFrom!=null)
				pstmt.setTimestamp(index++, dateAcctFrom);
			if (dateAcctTo!=null)
				pstmt.setTimestamp(index++, dateAcctTo);
			if (description.length() > 0)
				pstmt.setString(index++, getSQLText(description));
			if (codeLettrage.length() > 0)
				pstmt.setString(index++, getSQLText(codeLettrage));

			rs = pstmt.executeQuery();
			miniTable.loadTable(rs);
		}
		catch (SQLException e) {
			s_log.log(Level.SEVERE, sql, e);
		}
		finally {
			DB.close(rs, pstmt);
		}
	}   //  loadTableInfo

	public ArrayList<KeyNamePair> getAccountData(boolean onlyAux)
	{
		ArrayList<KeyNamePair> data = new ArrayList<KeyNamePair>();
		String sql = null;
		try {
			sql = MRole.getDefault().addAccessSQL(
					"SELECT ev.C_ElementValue_ID, ev.Value || ' - ' || ev.Name FROM C_ElementValue ev", "ev",
					MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO)
					+ "AND ev.IsActive='Y' AND ev.IsSummary='N' " 
					+ "AND ev.C_Element_ID IN (SELECT C_Element_ID FROM C_AcctSchema_Element ase WHERE ase.ElementType='AC' AND ase.AD_Client_ID=" + m_AD_Client_ID + ") ";
			if (onlyAux)		
				sql += "AND ev.BPartnerType IS NOT NULL ";	// uniquement les comptes centralisateurs

			sql += "ORDER BY 2";

			KeyNamePair acct = new KeyNamePair(0, "");
			data.add(acct);
			PreparedStatement pstmt = DB.prepareStatement(sql, null);			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				acct = new KeyNamePair(rs.getInt(1), rs.getString(2));
				data.add(acct);
			}
			DB.close(rs, pstmt);
		}
		catch (SQLException e) {
			s_log.log(Level.SEVERE, sql, e);
		}

		return data;
	}

	public String calculateSelection(IMiniTable miniTable) {
		m_noSelected = 0;

		BigDecimal TotalDr = new BigDecimal(0.0);
		BigDecimal TotalCr = new BigDecimal(0.0);
		m_noNdFLine = 0;

		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++) {
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);
			if (id.isSelected()) {
				TotalDr = TotalDr.add((BigDecimal)miniTable.getValueAt(i, i_AmtAcctDr));
				TotalCr = TotalCr.add((BigDecimal)miniTable.getValueAt(i, i_AmtAcctCr));
				m_noSelected++;
			}
		}
		StringBuffer info = new StringBuffer();
		info.append(Msg.parseTranslation(Env.getCtx(), " @XXA_Debit@ = " + m_format.format(TotalDr) + " @XXA_Credit@ = " + m_format.format(TotalCr)));
		m_DifferenceAmt = TotalDr.subtract(TotalCr); // pour mise à jour dans le champ fieldDifference
		m_noRows = rows;

		return info.toString();
	}   //  calculateSelection

	public String generateLettrage(IMiniTable miniTable, boolean isTemp, String LettrageType, int acctSchemaID, int c_bpartner_id, int accountID)
	{
		int count = 0;

		Trx trx = Trx.get(Trx.createTrxName("XXA_Lettrage"), true);		
		String trxName = trx.getTrxName();


		int Record_ID = 0;
		if (LettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			Record_ID = c_bpartner_id;
		else if (LettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account))
			Record_ID = accountID;

		boolean IsDocsAllocated = true;
		String err = "";

		// Si lettrage de tiers, les documents sélectionnés doivent être lettrés
		if (!isTemp && LettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner)) {
			// Récup des matchcode sélectionnés
			for (int r = 0; r < miniTable.getRowCount(); r++ ) {
				if (((IDColumn) miniTable.getValueAt(r, i_ID)).isSelected()) {
					int factId = ((IDColumn) miniTable.getValueAt(r, i_ID)).getRecord_ID();

					MFactAcct fa = new MFactAcct(Env.getCtx(), factId, trxName);

					if (fa.getAD_Table_ID() == MInvoice.Table_ID) {
						MInvoice invoice = new MInvoice(Env.getCtx(), fa.getRecord_ID(), null);
						if (!invoice.isPaid())
							err = invoice.getDocumentInfo() + " " +  Msg.getMsg(Env.getCtx(), "XXA_NotPaid");
					}
					else if (fa.getAD_Table_ID() == MPayment.Table_ID) {
						MPayment payment = new MPayment(Env.getCtx(), fa.getRecord_ID(), null);
						if (!payment.isAllocated())
							err = payment.getDocumentInfo() + " " + Msg.getMsg(Env.getCtx(), "XXA_NotAllocated");
					}
					else if (fa.getAD_Table_ID() == MAllocationHdr.Table_ID) { // on ne fait rien
					}

					else
						err = Msg.getMsg(Env.getCtx(), "XXA_LettrageTableNotUsed");

					if (err != null && err.length() > 0)
						IsDocsAllocated = false;
				}
			}
		}

		if (!IsDocsAllocated)
			return err;

		String matchcode = MLFRFactReconciliationCode.getCodeNext(acctSchemaID, LettrageType, Record_ID, trx.getTrxName());
		String listFactAcctIDs = "";

		for (int r = 0; r < miniTable.getRowCount(); r++) {
			if (((IDColumn) miniTable.getValueAt(r, i_ID)).isSelected()) {
				int factId = ((IDColumn) miniTable.getValueAt(r, i_ID)).getRecord_ID();
				listFactAcctIDs += factId + ", ";

				String sql = "SELECT COUNT(*) FROM Fact_Reconciliation " +
						" WHERE AD_Client_ID = " + m_AD_Client_ID +
						" AND Fact_Acct_ID = " + factId;
				int no = DB.getSQLValue(trxName, sql);

				int user_id = Env.getContextAsInt(Env.getCtx(), "#AD_User_ID");

				if (no == 0) {	// Insert
					String sqlInsert = "INSERT INTO Fact_Reconciliation " +
							"(Fact_Reconciliation_ID, Fact_Reconciliation_UU, AD_Client_ID, AD_Org_ID, Created, CreatedBy, Updated, UpdatedBy, " +
							"IsActive, Fact_Acct_ID, MatchCode, LFR_FactReconciliationType, LFR_IsManual)" + 
							"SELECT nextidfunc((SELECT AD_Sequence_ID FROM AD_Sequence WHERE Name = 'Fact_Reconciliation' AND IsActive='Y' AND IsTableID='Y' AND IsAutoSequence='Y' AND AD_Client_ID = 0), 'N'),generate_uuid(), AD_Client_ID, AD_Org_ID, SysDate, " + user_id + ", " +
							"SysDate, " + user_id + ", IsActive, " +
							"Fact_Acct_ID, '" + matchcode + "', '" + LettrageType + "', 'Y'" +
							"FROM Fact_Acct f " +
							"WHERE Fact_Acct_ID = " + factId;

					PreparedStatement pstmt = DB.prepareStatement(sqlInsert, trxName);
					try {
						count = pstmt.executeUpdate();
						if (count < 0) {
							trx.rollback();
							trx.close();
							return Msg.getMsg(Env.getCtx(), "XXA_LettrageErrorInGenerate");
						}
					}
					catch (SQLException e) {
						s_log.log(Level.SEVERE, sqlInsert, e);
					}
				} else {	// Update dans le cas où l'écriture est déjà présente dans la table
					String sqlUpdate = "UPDATE Fact_Reconciliation SET" 
							+ " MatchCode = '" + matchcode + "',"
							+ " Updated = SysDate,"
							+ " UpdatedBy = " + user_id
							+ ", LFR_IsManual = 'Y'";

					sqlUpdate += " WHERE Fact_Acct_ID = " + factId;
					DB.executeUpdate(sqlUpdate, trxName);
				}
			}
		}

		// Retrait du code temp et mise à jour de la date lettrage ET du LettrageCodeType
		if (!isTemp) {	
			Timestamp MaxDateAcct = null;
			listFactAcctIDs = listFactAcctIDs.substring(0, listFactAcctIDs.length()-2);	// retrait dernier espace et ,

			try {
				String sqlMaxDateAcct = "SELECT MAX(DateAcct) FROM Fact_Acct WHERE Fact_Acct_ID IN (" + listFactAcctIDs + ")";	// détermination de la dateMax des écritures
				PreparedStatement pstmtMaxDateAcct = null;
				pstmtMaxDateAcct = DB.prepareStatement(sqlMaxDateAcct, trxName);
				ResultSet rsMaxDateAcct = pstmtMaxDateAcct.executeQuery();
				if (rsMaxDateAcct.next())
					MaxDateAcct = rsMaxDateAcct.getTimestamp(1);
				DB.close(rsMaxDateAcct, pstmtMaxDateAcct);
			} catch (SQLException e) {
				e.printStackTrace();
				return "Impossible de déterminer la date de lettrage";
			}

			String sqlUpdate2 = "UPDATE Fact_Reconciliation  SET" 
					+ " LFR_ReconciliationDate = " + DB.TO_DATE(MaxDateAcct)
					+ ", LFR_FactReconciliationType = '" + LettrageType + "'"
					+ " WHERE Fact_Acct_ID IN (" + listFactAcctIDs + ")";

			DB.executeUpdate(sqlUpdate2, trxName);
		}
		trx.commit();
		trx.close();

		return null;
	}   //  generateLettrage

	public int resetLettrage(IMiniTable miniTable, String LettrageType, boolean isTemp, int acctSchemaID, int record_id)
	{
		Trx trx = Trx.get(Trx.createTrxName("XXA_LettrageReset"), true);		
		String trxName = trx.getTrxName();

		int no = 0;
		ArrayList<String> matchcodeList = new ArrayList<String>();

		// Récup des matchcode sélectionnés
		for ( int r = 0; r < miniTable.getRowCount(); r++ ) {
			if (((IDColumn) miniTable.getValueAt(r, i_ID)).isSelected()) {
				String matchcode = ((String) miniTable.getValueAt(r, i_Code));
				if (matchcode==null)
					continue;
				if (!matchcodeList.contains(matchcode))
					matchcodeList.add(matchcode);	
			}
		}

		String[] matchcode_id = new String[matchcodeList.size()];
		for (int i = 0; i < matchcode_id.length; i++) {
			matchcode_id[i] = matchcodeList.get(i);
			String matchcode1 = matchcode_id[i];
			no += LfrFactReconciliationUtil.supprLettrage(LettrageType, matchcode1, m_AD_Client_ID, acctSchemaID, record_id, trxName);
		}

		trx.commit();
		trx.close();

		return no;
	}

	private String getSQLText (String f)
	{
		String s = f.toUpperCase();
		if (!s.endsWith("%"))
			s += "%";
		return s;
	}   //  getSQLText

	public static Hlayout getHlayout() {
		Hlayout layout = new Hlayout();
		layout.setValign("middle");
		return layout;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		if (evt.getSource() == fBPartner) {

			fAccount.setValue(null);

			if (evt.getNewValue() != null && !fAcctSchema.isNullOrEmpty()) {
				MBPartner bp = new MBPartner(Env.getCtx(), (Integer) evt.getNewValue(), null);
				if (bp.isCustomer() || bp.isVendor() && !(bp.isCustomer() && bp.isVendor())) {
					int accountID = 0;
					if (bp.isCustomer())
						accountID = DB.getSQLValueEx(null, "SELECT vc.Account_ID FROM C_BP_Customer_Acct bpa, C_ValidCombination vc WHERE bpa.C_Receivable_Acct = vc.C_ValidCombination_ID AND bpa.C_BPartner_ID = ? AND bpa.C_AcctSchema_ID = ?", evt.getNewValue(), fAcctSchema.getValue());
					else if (bp.isVendor())
						accountID = DB.getSQLValueEx(null, "SELECT vc.Account_ID FROM C_BP_Vendor_Acct bpa, C_ValidCombination vc WHERE bpa.V_Liability_Acct = vc.C_ValidCombination_ID AND bpa.C_BPartner_ID = ? AND bpa.C_AcctSchema_ID = ?", evt.getNewValue(), fAcctSchema.getValue());
					if (accountID > 0)
						fAccount.setValue(accountID);
				}
			}

		}
	}
}   //  WLFRFactReconcile
