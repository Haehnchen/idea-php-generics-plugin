package de.espend.idea.php.quality.psalm.blacklist;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.QualityToolBlackList;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
@State(
    name = "PsalmValidatorDetectorBlackList",
    storages = {@Storage("$WORKSPACE_FILE$")}
)
public class PsalmValidatorBlackList extends QualityToolBlackList {
    public static PsalmValidatorBlackList getInstance(Project project) {
        return ServiceManager.getService(project, PsalmValidatorBlackList.class);
    }
}