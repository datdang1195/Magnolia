package vn.ekino.certificate.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddRoleToUserTask;
import info.magnolia.module.delta.FilterOrderingTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.ReplaceIfExistsTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is optional and lets you manage the versions of your module,
 * by registering "deltas" to maintain the module's configuration, or other type of content.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 *
 * @see info.magnolia.module.DefaultModuleVersionHandler
 * @see info.magnolia.module.ModuleVersionHandler
 * @see info.magnolia.module.delta.Task
 */
public class CertificateServicesModuleVersionHandler extends DefaultModuleVersionHandler {

    private static final String APP_LAUNCHER_APPS_PATH = "/modules/ui-admincentral/config/appLauncherLayout/";
    private static final String DEFAULT_GROUP_PROPERTY = "defaultGroup";
    private static final String ACADEMY_USER_ROLE = "academy-user-role";

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        List<Task> extraTasks = new ArrayList<>();

        // Add defaultGroup to app launcher layout which new apps will be added automatically
//        extraTasks.add(new HasPropertyDelegateTask(
//                "Add defaultGroup property to app launcher",
//                "Defines the group (one from underneath the groups node) where a new app will be added automatically if this information is not given explicitly,",
//                RepositoryConstants.CONFIG,
//                APP_LAUNCHER_APPS_PATH,
//                DEFAULT_GROUP_PROPERTY,
//                null,
//                new NewPropertyTask("", "", RepositoryConstants.CONFIG, APP_LAUNCHER_APPS_PATH, DEFAULT_GROUP_PROPERTY, "edit")));

        // Add role 'academy-user-role' to anonymous
        extraTasks.add(new AddRoleToUserTask("Add role 'academy-user-role' to user 'anonymous'", "anonymous", ACADEMY_USER_ROLE));

        extraTasks.add(new ReplaceIfExistsTask("Update Category app",
                "",
                "Cannot update category app",
                RepositoryConstants.CONFIG,
                "/modules/categorization/apps/categories/subApps/detail/editor/form/tabs/category/fields",
                "/mgnl-bootstrap-samples/cert-checker-module/config.modules.categorization.apps.categories.subApps.detail.editor.form.tabs.category.fields.yaml"));
        return extraTasks;
    }

    protected List<Task> getStartupTasks(InstallContext installContext) {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new ReplaceIfExistsTask("Register academy combobox field types",
                "",
                "No academy combobox field exists",
                RepositoryConstants.CONFIG,
                "/modules/ui-framework/fieldTypes/combobox",
                "/mgnl-bootstrap/cert-checker-module/config/config.modules.ui-framework.fieldTypes.combobox.yaml"));
        tasks.add(new ReplaceIfExistsTask("Register academy session field types",
                "",
                "No academy session field exists",
                RepositoryConstants.CONFIG,
                "/modules/ui-framework/fieldTypes/sessionComposite",
                "/mgnl-bootstrap/cert-checker-module/config/config.modules.ui-framework.fieldTypes.sessionComposite.yaml"));
        tasks.add(new ReplaceIfExistsTask("Register academy date field types",
                "",
                "No academy date field exists",
                RepositoryConstants.CONFIG,
                "/modules/ui-framework/fieldTypes/datewithevent",
                "/mgnl-bootstrap/cert-checker-module/config/config.modules.ui-framework.fieldTypes.datewithevent.yaml"));

        tasks.add(new FilterOrderingTask("academy-filter", new String[]{"login, gzip"}));

        tasks.add(new NodeExistsDelegateTask("Reorder filters of locale filter",
                "Reorder filters of locale filter before contentType",
                RepositoryConstants.CONFIG,
                "/server/filters/logout",
                new OrderNodeBeforeTask("", "",
                        RepositoryConstants.CONFIG,
                        "/server/filters/academy-filter",
                        "logout")));

        return tasks;
    }
}
