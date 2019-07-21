package de.espend.idea.php.quality.phpstan.blacklist;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolBlackList;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
@State(
    name = "PhpStanValidatorDetectorBlackList",
    storages = {@Storage("$WORKSPACE_FILE$")}
)
public class PhpStanValidatorBlackList extends QualityToolBlackList {
    public static PhpStanValidatorBlackList getInstance(Project project) {
        return ServiceManager.getService(project, PhpStanValidatorBlackList.class);
    }
}