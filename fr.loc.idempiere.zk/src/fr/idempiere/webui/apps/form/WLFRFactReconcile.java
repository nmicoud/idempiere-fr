package fr.idempiere.webui.apps.form;

import static org.compiere.model.SystemIDs.COLUMN_C_INVOICE_C_BPARTNER_ID;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WDateEditor;
import org.adempiere.webui.editor.WEditor;
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
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MBPartner;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MColumn;
import org.compiere.model.MConversionRate;
import org.compiere.model.MConversionType;
import org.compiere.model.MDocType;
import org.compiere.model.MElementValue;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPayment;
import org.compiere.model.MPeriod;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.model.SystemIDs;
import org.compiere.process.DocumentEngine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
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
import org.zkoss.zul.Span;
import org.zkoss.zul.Vlayout;

import fr.idempiere.model.MLFRFactReconciliationCode;
import fr.idempiere.util.LfrFactReconciliationUtil;


/**
 *  Formulaire de lettrage manuel
 *  @author Nico
 */
@org.idempiere.ui.zk.annotation.Form(name = "fr.idempiere.webui.apps.form.WLFRFactReconcile")
public class WLFRFactReconcile implements IFormController, EventListener<Event>, ValueChangeListener, WTableModelListener
{
	public static CLogger s_log = CLogger.getCLogger(WLFRFactReconcile.class);
	private int m_WindowNo = 0;
	private String m_sql;
	private int m_noSelected = 0;
	private int m_AD_Client_ID = 0;
	private CustomForm form = new CustomForm();
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
	private Checkbox cbCreateJournal = new Checkbox();
	private BigDecimal m_maxAmtToCreateJournal;

	private int m_noRows = 0;
	protected BigDecimal m_DifferenceAmt = Env.ZERO;
	protected int idxColID= 0;
	protected int idxColDateAcct = 2;
	protected int idxColAmtAcctDr = 6;
	protected int idxColAmtAcctCr = 7;
	protected int idxColMatchCode = 10;

	private DecimalFormat m_format = DisplayType.getNumberFormat(DisplayType.Amount);	

	public WLFRFactReconcile()
	{
		try {
			m_WindowNo = form.getWindowNo();

			dynInit();
			zkInit();
			m_maxAmtToCreateJournal = getMaxAmtInBaseCurrency();
			cbCreateJournal.setVisible(false);
			cbCreateJournal.setChecked(false);
			southPanel.appendChild(new Separator());
			southPanel.appendChild(statusBar);
			fBPartner.setVisible(false);
			if (!fType.isNullOrEmpty())
				onReconciliationTypeChange();
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

		cbCreateJournal.setText(Msg.getMsg(Env.getCtx(), "FactReconcileCreateJournalCheckboxLabel", true));
		cbCreateJournal.setTooltiptext(Msg.getMsg(Env.getCtx(), "FactReconcileCreateJournalCheckboxLabel", false));
		cbCreateJournal.addEventListener(Events.ON_CHECK, this);

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
		southPanel.appendChild(cbCreateJournal);
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

	private void dynInit() throws Exception
	{
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

		int baseCurrencyID = MClientInfo.get(Env.getCtx()).getC_Currency_ID();
		int acCurrencyID = MAcctSchema.get((Integer) fAcctSchema.getValue()).getC_Currency_ID();
		m_maxAmtToCreateJournal = MConversionRate.convert(Env.getCtx(), getMaxAmtInBaseCurrency(), baseCurrencyID, acCurrencyID, Env.getAD_Client_ID(Env.getCtx()), 0);

	}   //  dynInit

	private void loadTableLines(String lettrageType)
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

	private String getReconciliationType() {
		return fType.isNullOrEmpty() ? "" : (String) fType.getValue();
	}
	
	public void onEvent (Event e)
	{
		dataStatus.setValue(""); // on vide le champs

		String lettrageType = getReconciliationType();

		if (e.getTarget() == bZoomToFact)
			zoomToFact();
		else if (e.getTarget() == bZoomToDoc)
			zoomToDocument();
		else if (e.getTarget() == bGenerate || e.getTarget() == bReset) {

			if (e.getTarget() == bGenerate) {

				if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account) && cbCreateJournal.isVisible() && cbCreateJournal.isChecked()) {
					MElementValue account = new MElementValue(Env.getCtx(), (Integer) fAccount.getValue(), null);
					if (account.isDocControlled())
						Dialog.warn(form.getWindowNo(), "Warning", Msg.getMsg(Env.getCtx(), "FactReconcileCantCreateJournalAccountDocControlled", new Object[] {account.getValue(), account.getName()}), "");
					else {
						Callback<MJournal> callback = new Callback<MJournal>() {

							@Override
							public void onCallback(MJournal journal) {

								if (journal != null && journal.getDocStatus().equals(MJournal.DOCSTATUS_Completed)) {

									if (!MClient.isClientAccountingImmediate()) {
										String error = DocumentEngine.postImmediate(Env.getCtx(), journal.getAD_Client_ID(), MJournal.Table_ID, journal.getGL_Journal_ID(), false, null);
										if (!Util.isEmpty(error)) {
											Dialog.error(form.getWindowNo(), "SavePostError", journal.getDocumentInfo());
											return;
										}
									}

									int balanceFactAcctID = new Query(Env.getCtx(), MFactAcct.Table_Name, "AD_Table_ID = ? AND Record_ID = ? AND Account_ID = ?", null)
											.setParameters(MJournal.Table_ID, journal.getGL_Journal_ID(), fAccount.getValue())
											.firstIdOnly();

									generateLettrage(false, lettrageType, balanceFactAcctID);
								}
								else {
									if (journal == null)
										Dialog.error(form.getWindowNo(), "Error", "Can't create journal");
									else {
										Dialog.error(form.getWindowNo(), "Error", journal.getDocumentInfo() + " is not completed, thus is not possible to reconcile posting"
												+ (Util.isEmpty(journal.getProcessMsg()) ? " (" + journal.getProcessMsg() + ")" : ""));
									}
								}
							}
						};
						new CreateJournalParams(callback);
					}
				}
				else
					generateLettrage(false, lettrageType, -1);
			}
			if (e.getTarget() == bReset)
				resetLettrage(false, lettrageType);
		}
		else if (e.getTarget() == bRefresh) {	
			if ((fAccount.getComponent().getItemCount() != 0)
					&& (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account)
							|| ((lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner) && fBPartner.getValue() != null)))) {
				loadTableLines(lettrageType);
			}
			else
				statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "XXA_NotEnoughCriteriaForSearching"), true);
		}
		else if (e.getTarget() == fType.getComponent())
		{
			onReconciliationTypeChange();
		}
		else if (e.getTarget().equals(cbCreateJournal))
			bGenerate.setEnabled(cbCreateJournal.isChecked());
		
	}   //  onEvent

	private void onReconciliationTypeChange() {
		String lettrageType = getReconciliationType();
		boolean onlyAux = false;
		if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			onlyAux = true;

		fAccount.getComponent().removeAllItems();

		for (KeyNamePair acct : getAccountData(onlyAux))
			fAccount.getComponent().appendItem(acct.getName(), acct.getKey());

		prepareTable(miniTable, lettrageType);
		bpartnerActivate(lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner));
		loadTableLines(lettrageType);
	}
	
	private void calculate()
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
	private void calculateSelection()
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
		bGenerate.setEnabled(m_noSelected>0 && m_DifferenceAmt.signum()==0);
		bGenerate.setLabel(Msg.getMsg(Env.getCtx(), "XXA_Lettrer"));
		bReset.setLabel(Msg.getMsg(Env.getCtx(), "XXA_Delettrer"));
		bReset.setEnabled(m_noSelected>0);	// dans tous les cas
		
		if (m_DifferenceAmt.signum() != 0 && ((String) fType.getValue()).equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account)) {
			cbCreateJournal.setVisible(m_maxAmtToCreateJournal.signum() == 0 || m_DifferenceAmt.abs().compareTo(m_maxAmtToCreateJournal.abs()) <= 0);
			cbCreateJournal.setChecked(false);
		}
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

		int factId = ((IDColumn) miniTable.getModel().getValueAt(selected, idxColID)).getRecord_ID();

		AEnv.zoom(MFactAcct.Table_ID, factId);
	}   //  zoomToFact

	private void zoomToDocument()
	{
		int selected = miniTable.getSelectedRow();

		if (selected == -1)
			return;

		int factId = ((IDColumn) miniTable.getModel().getValueAt(selected, idxColID)).getRecord_ID();
		MFactAcct fa = new MFactAcct(Env.getCtx(), factId, null);
		AEnv.zoom(fa.getAD_Table_ID(), fa.getRecord_ID());

	}   //  zoomToDocument

	private void generateLettrage(boolean isTemp, String LettrageType, int balanceFactAcctID) 
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

		String msg = generateLettrage(miniTable, isTemp, LettrageType, acctSchemaID, bpartnerID, accountID, balanceFactAcctID);

		if(msg != null && msg.length() > 0)	{
			Dialog.error(0, "SaveError", msg);
			return;
		}

		loadTableLines(LettrageType);
		dataStatus.setValue(nbLines + " " + (Msg.getMsg(Env.getCtx(), (isTemp ? "XXA_Lettrageecriturespointees" : "XXA_LettrageecrituresLettrees"))));
		cbCreateJournal.setChecked(false);
		cbCreateJournal.setVisible(false);
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
		cbCreateJournal.setChecked(false);
		cbCreateJournal.setVisible(false);
	}   //  resetLettrage

	/** Renvoi un Separator avec un spacing exprimé en px */
	private Separator createVerticalSeparator(int spacing)
	{
		Separator sep = new Separator("vertical");
		sep.setSpacing(spacing + "px");
		return sep;
	}

	protected ColumnInfo[] getLayout(String lettrageType) {
		
		ColumnInfo id = new ColumnInfo(" ", "fa.Fact_Acct_ID", IDColumn.class, false, false, null);
		ColumnInfo orgID = new ColumnInfo(Msg.translate(Env.getCtx(), "AD_Org_ID"), "o.Name", KeyNamePair.class, true, false, "o.AD_Org_ID");
		ColumnInfo dateAcct = new ColumnInfo(Msg.translate(Env.getCtx(), "DateAcct"), "fa.DateAcct", Timestamp.class);
		ColumnInfo glCat = new ColumnInfo(Msg.translate(Env.getCtx(), "GL_Category_ID"), "glc.Name", KeyNamePair.class, true, false, "glc.GL_Category_ID");
		ColumnInfo docNo = new ColumnInfo(Msg.translate(Env.getCtx(), "DocumentNo"), "(CASE WHEN fa.AD_Table_ID = 318 THEN i.DocumentNo  WHEN fa.AD_Table_ID = 335 THEN p.DocumentNo END)", String.class);
		ColumnInfo description = new ColumnInfo(Msg.translate(Env.getCtx(), "Description"), "fa.Description", String.class);
		ColumnInfo amtAcctDr = new ColumnInfo(Msg.translate(Env.getCtx(), "AmtAcctDr"), "fa.AmtAcctDr", BigDecimal.class);
		ColumnInfo amtAcctCr = new ColumnInfo(Msg.translate(Env.getCtx(), "AmtAcctCr"), "fa.AmtAcctCr", BigDecimal.class);
		ColumnInfo reconciliationDate = new ColumnInfo(Msg.translate(Env.getCtx(), "LFR_ReconciliationDate"), "l.LFR_ReconciliationDate", Timestamp.class);
		ColumnInfo matchCode = new ColumnInfo(Msg.translate(Env.getCtx(), "LFR_MatchCode"), "l.MatchCode", String.class);

		if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account)) {
			idxColMatchCode = 9;
			return new ColumnInfo[] {id, orgID, dateAcct, glCat, docNo, description, amtAcctDr, amtAcctCr, reconciliationDate, matchCode};
		}
		
		if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner)) {
			idxColMatchCode = 10;
			ColumnInfo isAllocated = new ColumnInfo(Msg.translate(Env.getCtx(), "IsAllocated"), "(CASE WHEN fa.AD_Table_ID = 318 THEN i.IsPaid WHEN fa.AD_Table_ID = 335 THEN p.IsAllocated ELSE 'N' END)", Boolean.class);
			return new ColumnInfo[] {id, orgID, dateAcct, glCat, docNo, description, amtAcctDr, amtAcctCr, isAllocated, reconciliationDate, matchCode};
		}

		return null;
	}

	public void prepareTable(IMiniTable miniTable, String LettrageType)
	{
		m_sql = miniTable.prepareTable(
				getLayout(LettrageType),
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
		if (Util.isEmpty(m_sql))
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

	private ArrayList<KeyNamePair> getAccountData(boolean onlyAux)
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

	private String calculateSelection(IMiniTable miniTable) {
		m_noSelected = 0;

		BigDecimal TotalDr = Env.ZERO;
		BigDecimal TotalCr = Env.ZERO;

		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++) {
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);
			if (id.isSelected()) {
				TotalDr = TotalDr.add((BigDecimal)miniTable.getValueAt(i, idxColAmtAcctDr));
				TotalCr = TotalCr.add((BigDecimal)miniTable.getValueAt(i, idxColAmtAcctCr));
				m_noSelected++;
			}
		}
		StringBuffer info = new StringBuffer();
		info.append(Msg.parseTranslation(Env.getCtx(), " @XXA_Debit@ = " + m_format.format(TotalDr) + " @XXA_Credit@ = " + m_format.format(TotalCr)));
		m_DifferenceAmt = TotalDr.subtract(TotalCr); // pour mise à jour dans le champ fieldDifference
		m_noRows = rows;

		return info.toString();
	}   //  calculateSelection

	private String generateLettrage(IMiniTable miniTable, boolean isTemp, String lettrageType, int acctSchemaID, int c_bpartner_id, int accountID, int balanceFactAcctID)
	{
		Trx trx = Trx.get(Trx.createTrxName("XXA_Lettrage"), true);		
		String trxName = trx.getTrxName();


		int recordID = 0;
		if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner))
			recordID = c_bpartner_id;
		else if (lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_Account))
			recordID = accountID;

		boolean IsDocsAllocated = true;
		String err = "";

		// Si lettrage de tiers, les documents sélectionnés doivent être lettrés
		if (!isTemp && lettrageType.equals(MLFRFactReconciliationCode.LFR_FACTRECONCILIATIONTYPE_BPartner)) {
			// Récup des matchcode sélectionnés
			for (int r = 0; r < miniTable.getRowCount(); r++ ) {
				if (((IDColumn) miniTable.getValueAt(r, idxColID)).isSelected()) {
					int factId = ((IDColumn) miniTable.getValueAt(r, idxColID)).getRecord_ID();

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

		String listFactAcctIDs = "";

		for (int r = 0; r < miniTable.getRowCount(); r++) {
			if (((IDColumn) miniTable.getValueAt(r, idxColID)).isSelected()) {
				int factId = ((IDColumn) miniTable.getValueAt(r, idxColID)).getRecord_ID();
				listFactAcctIDs += factId + ", ";
			}
		}

		if (balanceFactAcctID > 0) { // Insérer dans la table Fact_Reconciliation et dans listFactAcctIDs
			listFactAcctIDs += balanceFactAcctID + ", ";
		}
		
		listFactAcctIDs = listFactAcctIDs.substring(0, listFactAcctIDs.length()-2);	// retrait dernier espace et ,

		Timestamp maxDateAcct = LfrFactReconciliationUtil.getMaxDateAcct("Fact_Acct_ID IN (" + listFactAcctIDs + ")", trxName);

		int retValue = LfrFactReconciliationUtil.insertAndUpdateXXA_Lettrage(Env.getCtx(), lettrageType, listFactAcctIDs, maxDateAcct, recordID, acctSchemaID, trxName);
		System.out.println("retValue=" + retValue);
		
		trx.commit();
		trx.close();

		return null;
	}   //  generateLettrage

	private int resetLettrage(IMiniTable miniTable, String LettrageType, boolean isTemp, int acctSchemaID, int record_id)
	{
		Trx trx = Trx.get(Trx.createTrxName("XXA_LettrageReset"), true);		
		String trxName = trx.getTrxName();

		int no = 0;
		ArrayList<String> matchcodeList = new ArrayList<String>();

		// Récup des matchcode sélectionnés
		for ( int r = 0; r < miniTable.getRowCount(); r++ ) {
			if (((IDColumn) miniTable.getValueAt(r, idxColID)).isSelected()) {
				String matchcode = ((String) miniTable.getValueAt(r, idxColMatchCode));
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

	private static Hlayout getHlayout() {
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
						accountID = LfrFactReconciliationUtil.getCompteAuxiliaireClient(fAcctSchema.getValue(), evt.getNewValue());
					else if (bp.isVendor())
						accountID = LfrFactReconciliationUtil.getCompteAuxiliaireFournisseur(fAcctSchema.getValue(), evt.getNewValue());

					if (accountID > 0)
						fAccount.setValue(accountID);
				}
			}
		}
	}

	protected BigDecimal getMaxAmtInBaseCurrency() {
		return MSysConfig.getBigDecimalValue("FACT_RECONCILE_MAXIMUM_DIFFERENCE_AMOUNT", Env.ZERO, Env.getAD_Client_ID(Env.getCtx()));
	}

	private class CreateJournalParams extends Window implements EventListener<Event> {

		private static final long serialVersionUID = -6485565911294862992L;
		private Callback<MJournal>  m_CreateJournalCallback;
		private WTableDirEditor fCreateJournalOrg, fCreateJournalDocType, fCreateJournalAccount;
		private WDateEditor fCreateJournalDate;
		private WStringEditor fCreateJournalDescription;
		private ConfirmPanel createJournalConfirmPanel;

		private CreateJournalParams(Callback<MJournal> callback) {
			super();
			m_CreateJournalCallback = callback;
			setTitle(Msg.getMsg(Env.getCtx(), "Create Journal to balance posting"));
			init();

			setSclass("popup-dialog");
			setClosable(false);
			setMaximizable(true);
			setSizable(true);
			setShadow(true);
			setBorder(true);
			setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
			setStyle("position: absolute; width: 500px; height: 250px;");
			AEnv.showWindow(this);
		}

		private void init() {

			MLookup lookup = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, MColumn.getColumn_ID(MJournal.Table_Name, MJournal.COLUMNNAME_AD_Org_ID), DisplayType.TableDir);
			fCreateJournalOrg = new WTableDirEditor("AD_Org_ID", true, false, true, lookup);
			fCreateJournalOrg.getComponent().addEventListener(Events.ON_SELECT, this);
			fCreateJournalOrg.getLabel().setValue(Msg.getElement(Env.getCtx(), "AD_Org_ID"));
			if (!fOrg.isNullOrEmpty() && (Integer) fOrg.getValue() > 0)
				fCreateJournalOrg.setValue((Integer) fOrg.getValue());

			lookup = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, MColumn.getColumn_ID(MJournal.Table_Name, MJournal.COLUMNNAME_C_DocType_ID), DisplayType.TableDir);
			fCreateJournalDocType = new WTableDirEditor("C_DocType_ID", true, false, true, lookup);
			fCreateJournalDocType.getComponent().addEventListener(Events.ON_SELECT, this);
			fCreateJournalDocType.getLabel().setValue(Msg.getElement(Env.getCtx(), "C_DocType_ID"));
			if (fCreateJournalDocType.getComponent().getItemCount() == 1)
				fCreateJournalDocType.getComponent().setSelectedIndex(0);

			try {
				lookup = MLookupFactory.get (Env.getCtx(), form.getWindowNo(), MColumn.getColumn_ID(MElementValue.Table_Name, MElementValue.COLUMNNAME_C_ElementValue_ID), DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "Account_ID", 0, false,
						"C_ElementValue.IsSummary = 'N' AND C_ElementValue.IsDocControlled = 'N' AND EXISTS (SELECT * FROM C_AcctSchema_Element ae WHERE C_ElementValue.C_Element_ID=ae.C_Element_ID AND ae.ElementType='AC' AND ae.C_AcctSchema_ID=" + fAcctSchema.getValue()
						 + ") AND C_ElementValue.C_ElementValue_ID != " + fAccount.getValue());

			} catch (Exception e) {
				s_log.log(Level.SEVERE, "Can't init Account editor :", e);
			}

			fCreateJournalAccount = new WTableDirEditor("Account_ID", true, false, true, lookup);
			fCreateJournalAccount.getComponent().addEventListener(Events.ON_SELECT, this);
			fCreateJournalAccount.getLabel().setValue(Msg.getElement(Env.getCtx(), "Account_ID"));

			fCreateJournalDate = new WDateEditor();
			fCreateJournalDate.getLabel().setValue(Msg.getElement(Env.getCtx(), "DateAcct"));

			int rows = miniTable.getRowCount();
			for (int i = 0; i < rows; i++) {
				IDColumn id = (IDColumn) miniTable.getValueAt(i, idxColID);

				if (id.isSelected()) {
					Timestamp dateAcct = (Timestamp) miniTable.getValueAt(i, idxColDateAcct);
					if (fCreateJournalDate.isNullOrEmpty() || TimeUtil.max(dateAcct, fCreateJournalDate.getValue()) == dateAcct)
						fCreateJournalDate.setValue(dateAcct);
				}
			}

			fCreateJournalDescription = new WStringEditor();
			fCreateJournalDescription.getLabel().setValue(Msg.getElement(Env.getCtx(), "Description"));

			Borderlayout mainLayout = new Borderlayout();
			appendChild(mainLayout);

			Center center = new Center();
			mainLayout.appendChild(center);
			center.setStyle("padding: 4px;");

			Vlayout vl = new Vlayout();

			center.appendChild(vl);
			center.setAutoscroll(true);

			vl.appendChild(createLine(fCreateJournalOrg));
			vl.appendChild(createLine(fCreateJournalDate));
			vl.appendChild(createLine(fCreateJournalDocType));
			vl.appendChild(createLine(fCreateJournalAccount));
			vl.appendChild(createLine(fCreateJournalDescription));

			createJournalConfirmPanel = new ConfirmPanel(true);
			createJournalConfirmPanel.addActionListener(this);

			South south = new South();
			south.setStyle("text-align: right");
			south.setSclass("dialog-footer");
			mainLayout.appendChild(south);
			south.appendChild(createJournalConfirmPanel);
		}

		private static Hlayout createLine (WEditor editor) {
			Hlayout layout = new Hlayout();
			layout.setValign("middle");
			ZKUpdateUtil.setHflex(layout, "12");

			Span span = new Span();
			ZKUpdateUtil.setHflex(span, "3");
			layout.appendChild(span);

			span.appendChild(editor.getLabel());

			layout.appendChild(editor.getComponent());
			ZKUpdateUtil.setHflex(((HtmlBasedComponent) editor.getComponent()), "7");

			return layout;
		}	//	createLine

		public void onEvent(Event event) throws Exception {

			if (event.getTarget() == createJournalConfirmPanel.getOKButton()) {

				ArrayList<WrongValueException> list = new ArrayList<WrongValueException>();

				if (fCreateJournalOrg.isNullOrEmpty())
					list.add(new WrongValueException(fCreateJournalOrg.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory")));
				if (fCreateJournalDocType.isNullOrEmpty())
					list.add(new WrongValueException(fCreateJournalDocType.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory")));
				if (fCreateJournalAccount.isNullOrEmpty())
					list.add(new WrongValueException(fCreateJournalAccount.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory")));
				if (fCreateJournalDate.isNullOrEmpty())
					list.add(new WrongValueException(fCreateJournalDate.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory")));
				if (fCreateJournalDescription.isNullOrEmpty())
					list.add(new WrongValueException(fCreateJournalDescription.getComponent(), Msg.getMsg(Env.getCtx(), "FillMandatory")));

				if (list.size() > 0)
					throw new WrongValuesException(list.toArray(new WrongValueException[list.size()]));

				if (!MPeriod.isOpen(Env.getCtx(), fCreateJournalDate.getValue(), MDocType.DOCBASETYPE_GLJournal, (Integer) fCreateJournalOrg.getValue(), true))
					throw new WrongValueException(fCreateJournalDate.getComponent(), Msg.getMsg(Env.getCtx(), "PeriodClosed"));

				String trxName = Trx.createTrxName("FactReconcileCreateJournal");
				Trx trx = Trx.get(trxName, true);
				MJournal journal = null;
				try {
					journal = new MJournal(Env.getCtx(), 0, trxName);
					journal.setAD_Org_ID((Integer) fCreateJournalOrg.getValue());
					journal.setC_DocType_ID((Integer) fCreateJournalDocType.getValue());
					journal.setPostingType(MJournal.POSTINGTYPE_Actual);
					journal.setDateDoc(fCreateJournalDate.getValue());
					journal.setDateAcct(fCreateJournalDate.getValue());
					journal.setC_AcctSchema_ID((Integer) fAcctSchema.getValue());
					journal.setDescription (fCreateJournalDescription.getComponent().getValue());
					journal.setC_Currency_ID(MAcctSchema.get(Env.getCtx(), journal.getC_AcctSchema_ID()).getC_Currency_ID());
					journal.setC_ConversionType_ID(MConversionType.getDefault(journal.getAD_Client_ID()));
					journal.saveEx();

					MJournalLine jl = new MJournalLine(journal);
					jl.setAccount_ID((Integer) fAccount.getValue());
					if (m_DifferenceAmt.compareTo(Env.ZERO) < 0)
						jl.setAmtSourceDr(m_DifferenceAmt.negate());
					else
						jl.setAmtSourceCr(m_DifferenceAmt);
					jl.saveEx();

					jl = new MJournalLine(journal);
					jl.setAccount_ID((Integer) fCreateJournalAccount.getValue());
					if (m_DifferenceAmt.compareTo(Env.ZERO) > 0)
						jl.setAmtSourceDr(m_DifferenceAmt);
					else
						jl.setAmtSourceCr(m_DifferenceAmt.negate());
					jl.saveEx();

					if (journal.processIt(MJournal.DOCACTION_Complete))
						journal.saveEx();
				}
				catch (Exception e) {
					trx.rollback();
				} finally {
					trx.close();
				}

				onClose();
				m_CreateJournalCallback.onCallback(journal);

			}
			else if (event.getTarget() == createJournalConfirmPanel.getButton(ConfirmPanel.A_CANCEL)) {
				onClose();
			}
		}
	}

}   //  WLFRFactReconcile
