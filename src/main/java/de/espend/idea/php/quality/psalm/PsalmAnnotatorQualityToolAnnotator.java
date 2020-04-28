package de.espend.idea.php.quality.psalm;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.execution.ExecutionException;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.config.interpreters.PhpSdkFileTransfer;
import com.jetbrains.php.tools.quality.*;
import de.espend.idea.php.quality.CheckstyleQualityToolMessageProcessor;
import de.espend.idea.php.quality.psalm.blacklist.PsalmValidatorBlackList;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorProjectConfiguration;
import de.espend.idea.php.quality.psalm.form.PsalmValidatorConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmAnnotatorQualityToolAnnotator extends QualityToolAnnotator {
    public static final PsalmAnnotatorQualityToolAnnotator INSTANCE = new PsalmAnnotatorQualityToolAnnotator();

    @NotNull
    @Override
    protected String getTemporaryFilesFolder() {
        return "psalm_temp.tmp";
    }

    @NotNull
    @Override
    protected String getInspectionId() {
        return (new PsalmValidationInspection()).getID();
    }

    @Override
    protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo qualityToolAnnotatorInfo) {
        return new CheckstyleQualityToolMessageProcessor(qualityToolAnnotatorInfo) {
            @Override
            protected Configurable getToolConfigurable(@NotNull Project project) {
                return new PsalmValidatorConfigurable(project);
            }
        };
    }

    protected void runTool(@NotNull QualityToolMessageProcessor messageProcessor, @NotNull QualityToolAnnotatorInfo annotatorInfo, @NotNull PhpSdkFileTransfer transfer) throws ExecutionException {
        List<String> params = getCommandLineOptions(annotatorInfo.getFilePath());
        PsalmValidatorBlackList blackList = PsalmValidatorBlackList.getInstance(annotatorInfo.getProject());
        QualityToolProcessCreator.runToolProcess(annotatorInfo, blackList, messageProcessor, annotatorInfo.getProject().getBasePath(), transfer, params);
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

        options.add("--output-format=checkstyle");
        options.add(filePath);

        return options;
    }

    @Nullable
    protected QualityToolConfiguration getConfiguration(@NotNull Project project, @NotNull LocalInspectionTool inspection) {
        try {
            return PsalmValidatorProjectConfiguration.getInstance(project).findSelectedConfiguration(project);
        } catch (QualityToolValidationException e) {
            return null;
        }
    }
}
