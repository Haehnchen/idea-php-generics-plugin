package de.espend.idea.php.quality.phpstan.blacklist;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolsIgnoreFilesConfigurable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanValidatorIgnoredFilesConfigurable extends QualityToolsIgnoreFilesConfigurable {
    public PhpStanValidatorIgnoredFilesConfigurable(Project project) {
        super(PhpStanValidatorBlackList.getInstance(project), project);
    }

    @NotNull
    public String getId() {
        return PhpStanValidatorIgnoredFilesConfigurable.class.getName();
    }

    @NotNull
    protected String getQualityToolName() {
        return "PhpStan Validator";
    }
}
