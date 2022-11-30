package fr.idempiere.process;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IProcessFactory;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessCall;
import org.compiere.util.CLogger;

public abstract class LfrProcessFactory implements IProcessFactory {

	private final static CLogger log = CLogger.getCLogger(LfrProcessFactory.class);
	private List<Class<? extends LfrProcess>> cacheProcess = new ArrayList<Class<? extends LfrProcess>>();
	
	/**
	 * For initialize class. Register the process to build
	 * 
	 * <pre>
	 * protected void initialize() {
	 * 	registerProcess(PPrintPluginInfo.class);
	 * }
	 * </pre>
	 */
	protected abstract void initialize();

	/**
	 * Register process
	 * 
	 * @param processClass Process class to register
	 */
	protected void registerProcess(Class<? extends LfrProcess> processClass) {
		cacheProcess.add(processClass);
		log.info(String.format("LfrProcess registered -> %s", processClass.getName()));
	}

	/**
	 * Default constructor
	 */
	public LfrProcessFactory() {
		initialize();
	}

	@Override
	public ProcessCall newProcessInstance(String className) {
		for (int i = 0; i < cacheProcess.size(); i++) {
			if (className.equals(cacheProcess.get(i).getName())) {
				try {
					LfrProcess customProcess = cacheProcess.get(i).getConstructor().newInstance();
					log.info(String.format("LfrProcess created -> %s", className));
					return customProcess;
				} catch (Exception e) {
					log.severe(String.format("Class %s can not be instantiated, Exception: %s", className, e));
					throw new AdempiereException(e);
				}
			}
		}
		return null;
	}
}
