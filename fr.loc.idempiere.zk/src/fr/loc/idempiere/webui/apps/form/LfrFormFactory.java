package fr.loc.idempiere.webui.apps.form;

import org.adempiere.webui.factory.AnnotationBasedFormFactory;
import org.adempiere.webui.factory.IFormFactory;
import org.osgi.service.component.annotations.Component;


@Component(service = IFormFactory.class, immediate = true)
public class LfrFormFactory extends AnnotationBasedFormFactory {

	public LfrFormFactory() {
	}

	@Override
	protected String[] getPackages() {
		return new String[] {"fr.loc.idempiere.webui.apps.form"};
	}

}