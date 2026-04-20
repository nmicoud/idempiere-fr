package fr.idempiere.webui.apps.form;

import org.adempiere.webui.acct.WAcctViewer;
import org.adempiere.webui.factory.AnnotationBasedFormFactory;
import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.osgi.service.component.annotations.Component;


@Component(service = IFormFactory.class, immediate = true, property = {"service.ranking:Integer=1"})
public class LfrFormFactory extends AnnotationBasedFormFactory implements IFormFactory {

	public LfrFormFactory() {
	}

	@Override
	protected String[] getPackages() {
		return new String[] {"fr.loc.idempiere.webui.apps.form"};
	}

	public ADForm newFormInstance(String formName) {
		if (WAcctViewer.class.getName().equals(formName))
			return new WLFRAcctViewer();
		if (WLFRFactExtraitCompte.class.getName().equals(formName))
			return new WLFRFactExtraitCompte().getForm();
		if (WLFRFactReconcile.class.getName().equals(formName))
			return new WLFRFactReconcile().getForm();

		return null;
	}
}