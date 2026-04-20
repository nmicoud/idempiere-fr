package fr.idempiere.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.adempiere.base.IProcessFactory;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessCall;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;

public abstract class LfrProcessFactory implements IProcessFactory {

	private final static CLogger log = CLogger.getCLogger(LfrProcessFactory.class);
	private List<Class<? extends LfrProcess>> cacheProcess = new ArrayList<Class<? extends LfrProcess>>();
	private HashMap<String, Class<? extends SvrProcess>> cacheProcessOverride = new HashMap<String, Class<? extends SvrProcess>>();

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
	 * Register process
	 * 
	 * @param processClass Process class to register
	 */
	protected void registerProcess(String classname, Class<? extends SvrProcess> processClass) {
		cacheProcessOverride.put(classname, processClass);
		log.info(String.format("LfrProcess registered -> %s", processClass.getName() + " for classname=" + classname));
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
		
		if (cacheProcessOverride.size() > 0) {
			for (HashMap.Entry<String, Class<? extends SvrProcess>> entry : cacheProcessOverride.entrySet()) {
				
				if (entry.getKey().equals(className)) {
					try {
						log.info("LfrProcess created from className=" + className + ",LfrClassName=" + entry.getValue().getName());
						return entry.getValue().getConstructor().newInstance();	
					}
					catch(Exception e) {
						log.severe(String.format("Class %s can not be instantiated, Exception: %s", className, e));
						throw new AdempiereException(e);
					}
				}
			}
		}
		
		return null;
	}
}
