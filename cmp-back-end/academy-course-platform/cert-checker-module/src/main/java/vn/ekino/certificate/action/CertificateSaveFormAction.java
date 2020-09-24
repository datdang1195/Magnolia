package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.service.GeneratedCertificateService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Slf4j
public class CertificateSaveFormAction extends AbstractAction<CertificateSaveFormActionDefinition> {

    private final GeneratedCertificateService generatedCertificateService;
    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;

    @Inject
    public CertificateSaveFormAction(CertificateSaveFormActionDefinition definition, JcrNodeAdapter item, EditorCallback callback, EditorValidator validator, GeneratedCertificateService generatedCertificateService) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.generatedCertificateService = generatedCertificateService;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (generatedCertificateService.validateForm(validator)) {
            Node node;
            try {
                node = item.applyChanges();
                generatedCertificateService.executeGenerateCertificate(node, false);
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        }
    }
}
