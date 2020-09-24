package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.service.GeneratedInternCertificateService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Slf4j
public class PublishInternCertificateAction extends AbstractAction<PublishCertificateActionDefinition> {

    private final GeneratedInternCertificateService generatedInternCertificateService;
    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;
    private final SubAppContext uiContext;

    @Inject
    public PublishInternCertificateAction(PublishCertificateActionDefinition definition, JcrNodeAdapter item, EditorCallback callback, EditorValidator validator, GeneratedInternCertificateService generatedInternCertificateService, SubAppContext uiContext) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.generatedInternCertificateService = generatedInternCertificateService;
        this.uiContext = uiContext;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (generatedInternCertificateService.validateForm(validator)) {
            Node node;
            try {
                node = item.applyChanges();
                generatedInternCertificateService.executeGenerateCertificate(node, true);
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Publication successful");
            callback.onSuccess(getDefinition().getName());
        }
    }
}