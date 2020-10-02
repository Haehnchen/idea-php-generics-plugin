package de.espend.idea.php.quality.psalm;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.tools.quality.*;
import de.espend.idea.php.quality.CheckstyleQualityToolMessageProcessor;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorConfiguration;
import de.espend.idea.php.quality.psalm.configuration.PsalmValidatorProjectConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PsalmAnnotatorQualityToolAnnotator extends QualityToolAnnotator {
    public static final PsalmAnnotatorQualityToolAnnotator INSTANCE = new PsalmAnnotatorQualityToolAnnotator();

    @Override
    protected QualityToolMessageProcessor createMessageProcessor(@NotNull QualityToolAnnotatorInfo qualityToolAnnotatorInfo) {
        return new CheckstyleQualityToolMessageProcessor(qualityToolAnnotatorInfo) {

            @Override
            protected String getMessagePrefix() {
                return "psalm";
            }

            @Override
            protected QualityToolType<PsalmValidatorConfiguration> getQualityToolType() {
                return PsalmQualityToolType.getInstance();
            }

            @NotNull
            @Override
            protected String getQuickFixFamilyName() {
                return "Psalm";
            }
        };
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

    @Override
    protected @NotNull QualityToolType<PsalmValidatorConfiguration> getQualityToolType() {
        return PsalmQualityToolType.getInstance();
    }

    @Override
    protected @Nullable List<String> getOptions(@Nullable String s, @NotNull QualityToolValidationInspection qualityToolValidationInspection, @NotNull Project project) {
        return getCommandLineOptions(s);
    }
}
