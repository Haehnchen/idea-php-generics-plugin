package de.espend.idea.php.quality.psalm.blacklist;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmValidatorIgnoredFilesConfigurable extends QualityToolsIgnoreFilesConfigurable {
    public PsalmValidatorIgnoredFilesConfigurable(Project project) {
        super(PsalmValidatorBlackList.getInstance(project), project);
    }

    @NotNull
    public String getId() {
        return PsalmValidatorIgnoredFilesConfigurable.class.getName();
    }

    @NotNull
    protected String getQualityToolName() {
        return "Psalm Validator";
    }
}
