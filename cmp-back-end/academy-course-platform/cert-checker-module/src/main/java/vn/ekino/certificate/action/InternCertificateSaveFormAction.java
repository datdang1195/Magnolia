package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.service.GeneratedInternCertificateService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Slf4j
public class InternCertificateSaveFormAction extends AbstractAction<CertificateSaveFormActionDefinition> {

    private final GeneratedInternCertificateService generatedInternCertificateService;
    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;

    @Inject
    public InternCertificateSaveFormAction(CertificateSaveFormActionDefinition definition, JcrNodeAdapter item, EditorCallback callback, EditorValidator validator, GeneratedInternCertificateService generatedInternCertificateService) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.generatedInternCertificateService = generatedInternCertificateService;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (generatedInternCertificateService.validateForm(validator)) {
            Node node;
            try {
                node = item.applyChanges();
                generatedInternCertificateService.executeGenerateCertificate(node, false);
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        }
    }
}