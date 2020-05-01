package de.espend.idea.php.quality;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.config.interpreters.PhpInterpretersManagerImpl;
import com.jetbrains.php.run.remote.PhpRemoteInterpreterManager;
import com.jetbrains.php.tools.quality.QualityToolAnnotatorInfo;
import com.jetbrains.php.util.pathmapper.PhpPathMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class QualityToolUtil {
    /**
     * Psalm needs a working directory which is the project; for remote we need to resolve it
     *
     * We pipe the working directory in general for all our tools
     */
    @Nullable
    private static String getRemotePath(@NotNull Project project, @NotNull String interpreterId) {
        PhpInterpreter interpreter = PhpInterpretersManagerImpl.getInstance(project).findInterpreterById(interpreterId);
        if (interpreter == null || !interpreter.isRemote()) {
            return null;
        }

        PhpRemoteInterpreterManager instance = PhpRemoteInterpreterManager.getInstance();
        if (instance == null) {
            return null;
        }

        PhpPathMapper pathMapper;
        try {
            pathMapper = instance.createPathMapper(project, interpreter.getPhpSdkAdditionalData()).createPathMapper(project);
        } catch (ExecutionException e) {
            return null;
        }

        return pathMapper.getRemoteFilePath(project.getBaseDir());
    }

    /**
     * Extract the working dir for local or remote resolving
     */
    @Nullable
    public static String getWorkingDirectoryFromAnnotator(@NotNull QualityToolAnnotatorInfo annotatorInfo) {
        String interpreterId = annotatorInfo.getInterpreterId();

        String workingDir = null;
        if (interpreterId != null) {
            workingDir = QualityToolUtil.getRemotePath(annotatorInfo.getProject(), interpreterId);
        }

        if (workingDir == null) {
            workingDir = annotatorInfo.getProject().getBasePath();
        }

        return workingDir;
    }
}
