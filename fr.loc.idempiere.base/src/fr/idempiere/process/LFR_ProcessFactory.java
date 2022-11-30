package fr.idempiere.process;

public class LFR_ProcessFactory extends LfrProcessFactory {


	@Override
	protected void initialize() {
		registerProcess(LFR_FactGeneJournaux.class);
	}
}
