package de.espend.idea.php.quality.phpstan.configuration;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.jetbrains.php.tools.quality.QualityToolConfigurationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorConfigurationManager extends QualityToolConfigurationManager<PhpStanValidatorConfiguration> {
    public PhpStanValidatorConfigurationManager(@Nullable Project project) {
        super(project);
        if (project != null) {
            this.myProjectManager = ServiceManager.getService(project, ProjectPhpStanValidatorConfigurationBaseManager.class);
        }

        this.myApplicationManager = ServiceManager.getService(AppPhpStanValidatorConfigurationBaseManager.class);
    }

    @NotNull
    protected List<PhpStanValidatorConfiguration> getDefaultProjectSettings() {
        ProjectPhpStanValidatorConfigurationBaseManager service = ServiceManager.getService(
            ProjectManager.getInstance().getDefaultProject(),
            ProjectPhpStanValidatorConfigurationBaseManager.class
        );

        return service.getSettings();
    }

    public static PhpStanValidatorConfigurationManager getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PhpStanValidatorConfigurationManager.class);
    }

    @State(
        name = "PhpStanValidator",
        storages = {@Storage("php.xml")}
    )
    static class AppPhpStanValidatorConfigurationBaseManager extends PhpStanValidatorConfigurationBaseManager {
        AppPhpStanValidatorConfigurationBaseManager() {}
    }

    @State(
        name = "PhpStanValidator",
        storages = {@Storage("php.xml")}
    )
    static class ProjectPhpStanValidatorConfigurationBaseManager extends PhpStanValidatorConfigurationBaseManager {
        ProjectPhpStanValidatorConfigurationBaseManager() {}
    }
}
