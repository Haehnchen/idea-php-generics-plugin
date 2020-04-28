package de.espend.idea.php.quality.psalm.configuration;

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
public class PsalmValidatorConfigurationManager extends QualityToolConfigurationManager<PsalmValidatorConfiguration> {
    public PsalmValidatorConfigurationManager(@Nullable Project project) {
        super(project);
        if (project != null) {
            this.myProjectManager = ServiceManager.getService(project, ProjectPsalmValidatorConfigurationBaseManager.class);
        }

        this.myApplicationManager = ServiceManager.getService(AppPsalmValidatorConfigurationBaseManager.class);
    }

    @NotNull
    protected List<PsalmValidatorConfiguration> getDefaultProjectSettings() {
        ProjectPsalmValidatorConfigurationBaseManager service = ServiceManager.getService(
            ProjectManager.getInstance().getDefaultProject(),
            ProjectPsalmValidatorConfigurationBaseManager.class
        );

        return service.getSettings();
    }

    public static PsalmValidatorConfigurationManager getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PsalmValidatorConfigurationManager.class);
    }

    @State(
        name = "PsalmValidator",
        storages = {@Storage("php.xml")}
    )
    static class AppPsalmValidatorConfigurationBaseManager extends PsalmValidatorConfigurationBaseManager {
        AppPsalmValidatorConfigurationBaseManager() {}
    }

    @State(
        name = "PsalmValidator",
        storages = {@Storage("php.xml")}
    )
    static class ProjectPsalmValidatorConfigurationBaseManager extends PsalmValidatorConfigurationBaseManager {
        ProjectPsalmValidatorConfigurationBaseManager() {}
    }
}
