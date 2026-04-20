package fr.idempiere.process;

public class LFR_ProcessFactory extends LfrProcessFactory {


	@Override
	protected void initialize() {
		registerProcess(LFR_FactAux.class);
		registerProcess(LFR_FactGeneBalance.class);
		registerProcess(LFR_FactGeneExtraitCompte.class);
		registerProcess(LFR_FactGeneGrandLivre.class);
		registerProcess(LFR_FactGeneJournaux.class);
		registerProcess(LFR_ODSituationPrepaSyncLines.class);
		registerProcess(LFR_PaySelectionCreatePayment.class);
		registerProcess(LFR_PaySelectionExport.class);
		registerProcess(LFR_PaySelectionPrepaymentAdd.class);
		registerProcess(LFR_PeriodAutoClose.class);
		registerProcess(LFR_PeriodAutoCloseDbtMaintain.class);
		registerProcess(LFR_RanPrepa.class);
		registerProcess(LFR_Reconcile.class);
		registerProcess(LFR_PaySelectionCreatePayment.class);
		registerProcess(LFR_PaySelectionExport.class);
		registerProcess(LFR_PaySelectionPrepaymentAdd.class);

		registerProcess("org.compiere.process.AllocationReset", LFR_AllocationReset.class);
	}
}
