package de.espend.idea.php.quality.phpstan;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PathUtil;
import com.jetbrains.php.config.interpreters.PhpSdkFileTransfer;
import com.jetbrains.php.tools.quality.*;
import com.jetbrains.php.tools.quality.phpCSFixer.PhpCSFixerValidationInspection;
import de.espend.idea.php.quality.CheckstyleQualityToolMessageProcessor;
import de.espend.idea.php.quality.QualityToolUtil;
import de.espend.idea.php.quality.phpstan.blacklist.PhpStanValidatorBlackList;
import de.espend.idea.php.quality.phpstan.configuration.PhpStanValidatorProjectConfiguration;
import de.espend.idea.php.quality.phpstan.form.PhpStanValidatorConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpStanAnnotatorQualityToolAnnotator extends QualityToolAnnotator {
    public static final PhpStanAnnotatorQualityToolAnnotator INSTANCE = new PhpStanAnnotatorQualityToolAnnotator();

    @NotNull
    @Override
    protected String getTemporaryFilesFolder() {
        return "phpstan_temp.tmp";
    }

    @NotNull
    @Override
    protected String getInspectionId() {
        return (new PhpStanFixerValidationInspection()).getID();
    }

    @Override
    protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo qualityToolAnnotatorInfo) {
        return new CheckstyleQualityToolMessageProcessor(qualityToolAnnotatorInfo) {
            @Override
            protected Configurable getToolConfigurable(@NotNull Project project) {
                return new PhpStanValidatorConfigurable(project);
            }
        };
    }

    protected void runTool(@NotNull QualityToolMessageProcessor messageProcessor, @NotNull QualityToolAnnotatorInfo annotatorInfo, @NotNull PhpSdkFileTransfer transfer) throws ExecutionException {
        List<String> params = getCommandLineOptions(annotatorInfo.getFilePath());
        PhpStanValidatorBlackList blackList = PhpStanValidatorBlackList.getInstance(annotatorInfo.getProject());

        String workingDir = QualityToolUtil.getWorkingDirectoryFromAnnotator(annotatorInfo);
        QualityToolProcessCreator.runToolProcess(annotatorInfo, blackList, messageProcessor, workingDir, transfer, params);
        if (messageProcessor.getInternalErrorMessage() != null) {
            if (annotatorInfo.isOnTheFly()) {
                String message = messageProcessor.getInternalErrorMessage().getMessageText();
                showProcessErrorMessage(annotatorInfo, blackList, message);
            }

            messageProcessor.setFatalError();
        }
    }

    private List<String> getCommandLineOptions(String filePath) {
        ArrayList<String> options = new ArrayList<>();

        options.add("analyse");
        options.add("--error-format=checkstyle");
        options.add(filePath);

        return options;
    }

    @Nullable
    protected QualityToolConfiguration getConfiguration(@NotNull Project project, @NotNull LocalInspectionTool inspection) {
        try {
            return PhpStanValidatorProjectConfiguration.getInstance(project).findSelectedConfiguration(project);
        } catch (QualityToolValidationException e) {
            return null;
        }
    }
}
