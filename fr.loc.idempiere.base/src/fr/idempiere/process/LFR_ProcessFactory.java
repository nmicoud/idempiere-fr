package fr.idempiere.process;

public class LFR_ProcessFactory extends LfrProcessFactory {


	@Override
	protected void initialize() {
		registerProcess(LFR_FactGeneBalance.class);
		registerProcess(LFR_FactGeneJournaux.class);
		registerProcess(LFR_PeriodAutoClose.class);
		registerProcess(LFR_PeriodAutoCloseDbtMaintain.class);
	}
}
