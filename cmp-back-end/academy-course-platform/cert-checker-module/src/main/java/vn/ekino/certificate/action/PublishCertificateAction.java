package vn.ekino.certificate.action;

import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.service.GeneratedCertificateService;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Slf4j
public class PublishCertificateAction extends AbstractAction<PublishCertificateActionDefinition> {

    private final GeneratedCertificateService generatedCertificateService;
    private final JcrNodeAdapter item;
    private final EditorCallback callback;
    private final EditorValidator validator;
    private final SubAppContext uiContext;

    @Inject
    public PublishCertificateAction(PublishCertificateActionDefinition definition, JcrNodeAdapter item, EditorCallback callback, EditorValidator validator, GeneratedCertificateService generatedCertificateService, SubAppContext uiContext) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.validator = validator;
        this.generatedCertificateService = generatedCertificateService;
        this.uiContext = uiContext;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (generatedCertificateService.validateForm(validator)) {
            Node node;
            try {
                node = item.applyChanges();
                generatedCertificateService.executeGenerateCertificate(node, true);
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Publication successful");
            callback.onSuccess(getDefinition().getName());
        }
    }
}